import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306";
        String databaseName = "skillbox";
        String user = "root";
        String password = "testtest";
        String timeZoneString = "serverTimezone=UTC";

        SqlConnector sqlConnector = new SqlConnector(url, databaseName, user, password, timeZoneString);
        Statement statement = sqlConnector.createStatement();

        try {
            ResultSet resultSet;
            System.out.println("== Общее количество подписок на каждый курс: ");
            resultSet = statement.executeQuery("SELECT course_name, COUNT(subscription_date) " +
                    "FROM purchaselist GROUP BY course_name");
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1) + "\t" + resultSet.getString(2));
            }
            System.out.println("== Количество подписок по курсам и по месяцам: ");
            resultSet = statement.executeQuery("SELECT course_name, COUNT(subscription_date), subscription_date " +
                    "FROM purchaselist GROUP BY MONTH(subscription_date), course_name");
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3));
            }
            System.out.println("== Количество подписок по курсам в месяц: ");
            resultSet = statement.executeQuery("SELECT course_name, MONTHNAME(subscription_date) AS month_name, " +
                    "date_format(subscription_date, '%m') AS month_number, COUNT(subscription_date) AS count " +
                    "FROM purchaselist " +
                    "GROUP BY month_name, course_name");
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1) + "\t" + resultSet.getString(2) +
                        "\t" + resultSet.getString(3) + "\t" + resultSet.getString(4));
            }
            System.out.println("== Среднее количество подписок по курсам за все месяцы: ");
            resultSet = statement.executeQuery("SELECT course_name, month_number, month_name, AVG(count) " +
                    "FROM (SELECT course_name, COUNT(subscription_date) as count, MONTHNAME(subscription_date) as month_name, " +
                    "date_format(subscription_date, '%m') AS month_number " +
                    "FROM purchaselist GROUP BY month_name, course_name) s " +
                    "GROUP BY course_name");

            while (resultSet.next()) {
                System.out.println(resultSet.getString(1) + "\t" + resultSet.getString(2) +
                        "\t" + resultSet.getString(3) + "\t" + resultSet.getString(4));
            }
            resultSet.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        sqlConnector.closeConnection();
    }
}

import java.sql.*;

public class DBConnection {
    private static Connection connection;

    private static String dbName = "learn";
    private static String dbUser = "root";
    private static String dbPass = "testtest";

    private static StringBuilder insertQuery = new StringBuilder();
    private static StringBuilder insertStationQuery = new StringBuilder();

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + dbName +
                                "?user=" + dbUser + "&password=" + dbPass + "&useUnicode=true" +
                                "&useJDBCCompliantTimeZoneShift=true" +
                                "&useLegacyDatetimeCode=false" +
                                "&serverTimezone=UTC");
                connection.createStatement().execute("DROP TABLE IF EXISTS voter_count");
                connection.createStatement().execute("CREATE TABLE voter_count(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "name TINYTEXT NOT NULL, " +
                        "birthDate DATE NOT NULL, " +
                        "`count` INT NOT NULL, " +
                        "PRIMARY KEY (id), " +
                        "KEY (name(50)), " +
                        "UNIQUE KEY name_date(name(50), birthDate))");
                connection.createStatement().execute("DROP TABLE IF EXISTS station_worktime");
                connection.createStatement().execute("CREATE TABLE station_worktime(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "`station` INT NOT NULL, " +
                        "worktime_start DATETIME NOT NULL, " +
                        "worktime_end DATETIME NOT NULL, " +
                        "PRIMARY KEY (id), " +
                        "UNIQUE KEY (station))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void multiInsertFixedWorkTime() throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO station_worktime(`station`, worktime_start, worktime_end) ")
                .append("VALUES").append(insertStationQuery.toString())
                .append("AS new (a, b, c)")
                .append("ON DUPLICATE KEY UPDATE ")
                .append("worktime_start=(SELECT IF(b < worktime_start, b, worktime_start)), ")
                .append("worktime_end=(SELECT IF(c>worktime_end, c, worktime_end))");
        DBConnection.getConnection().createStatement().execute(sqlBuilder.toString());
    }

    public static void executeMultiInsert() throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO voter_count(name, birthDate, `count`) ")
                .append("VALUES").append(insertQuery.toString())
                .append("ON DUPLICATE KEY UPDATE `count`=`count` + 1"); // При дубликатах - увеличиваем счетчик голосований
        DBConnection.getConnection().createStatement().execute(sqlBuilder.toString());
    }

    public static void fixWorkTime(Integer station, String time) throws SQLException {
        time = time.replace('.', '-');
        insertStationQuery.append(insertStationQuery.length() > 0 ? "," : "")
                .append("(").append(station).append(", '").append(time).append("', '").append(time).append("')");
        if (insertStationQuery.length() > 100_000) {
            multiInsertFixedWorkTime();
            insertStationQuery = new StringBuilder();
        }
    }

    public static void uploadLastPartStringbuilders() throws SQLException {
        executeMultiInsert();
        insertQuery = new StringBuilder();
        multiInsertFixedWorkTime();
        insertStationQuery = new StringBuilder();
    }

    public static int CustomSelect(String name) throws SQLException {
        String sql = "SELECT ID FROM voter_count WHERE name='" + name + "'";
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
        if (!rs.next()) {
            rs.close();
            return -1;
        } else {
            int result = rs.getInt("id");
            rs.close();
            return result;
        }
    }

    public static String serchStation(Integer station) throws SQLException {
        String sql = "SELECT * FROM station_worktime WHERE station='" + station + "'";
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
        if (!rs.next()) {
            rs.close();
            return "Ничего не найдено";
        } else {
            String result = "Станция №" + rs.getString("station") + ", время работы: " + rs.getString("worktime_start")
                    + " - " + rs.getString("worktime_end");
            rs.close();
            return result;
        }
    }

    public static void countVoter(String name, String birthDay) throws SQLException {
        // Если вставляемый фрагмент первый раз, то запятую не ставим, иначе добавляем
        birthDay = birthDay.replace('.', '-');
        insertQuery.append(insertQuery.length() > 0 ? "," : "")
                .append("('").append(name).append("', '").append(birthDay).append("', 1)");
        if (insertQuery.length() > 100_000) {
            executeMultiInsert();
            insertQuery = new StringBuilder();
        }
    }

    public static void printStations() throws SQLException {
        String sql = "SELECT station, worktime_start, worktime_end FROM station_worktime ORDER BY station ASC";
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
        while (rs.next()) {
            System.out.println("\tСтанция №" + rs.getString("station") + ", время работы: "
                    + rs.getString("worktime_start")
                    + " - " + rs.getString("worktime_end"));
        }
    }

    public static void printVoterCounts() throws SQLException {
        String sql = "SELECT name, birthDate, `count` FROM voter_count WHERE `count` > 1";
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
        while (rs.next()) {
            System.out.println("\t" + rs.getString("name") + " (" +
                    rs.getString("birthDate") + ") - " + rs.getInt("count"));
        }
    }
}
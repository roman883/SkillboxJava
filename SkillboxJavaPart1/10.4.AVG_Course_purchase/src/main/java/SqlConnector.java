import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SqlConnector {

    private String url;
    private String databaseName;
    private String user;
    private String password;
    private Statement statement;
    private Connection connection;
    private String timeZoneString;

    public SqlConnector(String url, String databaseName, String user, String password, String timeZoneString) {
        this.url = url;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
        this.timeZoneString = timeZoneString;
    }

    public Statement createStatement() {
        try {
            connection = DriverManager.getConnection(url + "/" + databaseName + "?" + timeZoneString, user, password);
            statement = connection.createStatement();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return statement;
    }

    public void closeConnection() {
        try {
            statement.close();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}

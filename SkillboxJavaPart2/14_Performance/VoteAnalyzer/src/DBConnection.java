import java.sql.*;

public class DBConnection {
    private static Connection connection;

    private static String dbName = "learn";
    private static String dbUser = "root";
    private static String dbPass = "testtest";

    private static PreparedStatement insertVoterPstmnt;
    private static PreparedStatement insertStationPstmnt;

    private static int voterCount = 0; // Счетчик кол-ва проголосовавших в запросе
    private static final int VOTER_COUNT_LIMIT = 10_000; // Лимит запроса для отправки (Batch-size) Some DB has Limit
    private static int allVotersCounter = 0;
    private static int stationCount = 0; // Счетчик кол-ва записей в запросе
    private static final int STATION_COUNT_LIMIT = 10_000; // Лимит запроса для отправки (Batch-size) Some DB has Limit
    private static int allStationsCounter = 0;

    public static Connection connectToDB() {
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

    public static void createPrepStatements() throws Exception {
        if (connection == null) {
            connectToDB();
        }
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO voter_count(name, birthDate, `count`) ")
                .append("VALUES").append("(?, ?, 1)")                   // ("('name', 'birthday', 1)")
                .append("ON DUPLICATE KEY UPDATE `count`=`count` + 1"); // При дубликатах - увеличиваем счетчик голосований
        insertVoterPstmnt = connection.prepareStatement(sqlBuilder.toString());
        StringBuilder builder = new StringBuilder("INSERT INTO station_worktime(`station`, worktime_start, worktime_end) ")
                .append("VALUES").append("(?, ?, ?)") // ("(station, time, time)")
                .append("AS new (a, b, c)")
                .append("ON DUPLICATE KEY UPDATE ")
                .append("worktime_start=(SELECT IF(b < worktime_start, b, worktime_start)), ")
                .append("worktime_end=(SELECT IF(c>worktime_end, c, worktime_end))");
        insertStationPstmnt = connection.prepareStatement(builder.toString());
    }

    public static void fixWorkTime(Integer station, String time) throws SQLException {
        if (stationCount >= STATION_COUNT_LIMIT) {
            long timeStamp = System.currentTimeMillis();
            System.out.println("Executing batch SQL query. Uploaded " + allStationsCounter + " station rows to DB");
            boolean autocommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            insertStationPstmnt.executeBatch();
            connection.setAutoCommit(autocommit);
            insertStationPstmnt.clearBatch();
            stationCount = 0;
            System.out.println("\t=> Batch SQL query successfully executed. Time: " + (System.currentTimeMillis() - timeStamp) / 1000 + " s");
        }
        time = time.replace('.', '-');
        insertStationPstmnt.setInt(1, station);
        insertStationPstmnt.setString(2, time);
        insertStationPstmnt.setString(3, time);
        insertStationPstmnt.addBatch();
        stationCount++;
        allStationsCounter++;
    }

    public static void uploadLastPart() throws SQLException {
        long timeStamp = System.currentTimeMillis();
        System.out.println("Executing batch SQL query. Uploading the last piece of voter-and-station's data");
        boolean autocommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        insertVoterPstmnt.executeBatch();
        insertStationPstmnt.executeBatch();
        connection.setAutoCommit(autocommit);
        insertVoterPstmnt.clearBatch();
        insertStationPstmnt.clearBatch();
        voterCount = 0;
        stationCount = 0;
        System.out.println("\t=> The last piece of voter-and-station's data have successfully uploaded in "
            + (System.currentTimeMillis() - timeStamp) / 1000 + " s");
}

    public static int CustomSelect(String name) throws SQLException {
        String sql = "SELECT ID FROM voter_count WHERE name='" + name + "'";
        ResultSet rs = DBConnection.connectToDB().createStatement().executeQuery(sql);
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
        ResultSet rs = DBConnection.connectToDB().createStatement().executeQuery(sql);
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
        if (voterCount >= VOTER_COUNT_LIMIT) {
            long timeStamp = System.currentTimeMillis();
            System.out.println("Executing batch SQL query. Uploaded " + allVotersCounter + " voter rows to DB");
            boolean autocommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            insertVoterPstmnt.executeBatch();
            connection.setAutoCommit(autocommit);
            insertVoterPstmnt.clearBatch();
            voterCount = 0;
            System.out.println("\t=> Batch SQL query successfully executed. Time: " + (System.currentTimeMillis() - timeStamp) / 1000 + " s");
        }
        birthDay = birthDay.replace('.', '-');
        insertVoterPstmnt.setString(1, name);
        insertVoterPstmnt.setString(2, birthDay);
        insertVoterPstmnt.addBatch();
        voterCount++;
        allVotersCounter++;
    }

    public static void printStations() throws SQLException {
        String sql = "SELECT station, worktime_start, worktime_end FROM station_worktime ORDER BY station ASC";
        ResultSet rs = DBConnection.connectToDB().createStatement().executeQuery(sql);
        while (rs.next()) {
            System.out.println("\tСтанция №" + rs.getString("station") + ", время работы: "
                    + rs.getString("worktime_start")
                    + " - " + rs.getString("worktime_end"));
        }
    }

    public static void printVoterCounts() throws SQLException {
        String sql = "SELECT name, birthDate, `count` FROM voter_count WHERE `count` > 1";
        ResultSet rs = DBConnection.connectToDB().createStatement().executeQuery(sql);
        while (rs.next()) {
            System.out.println("\t" + rs.getString("name") + " (" +
                    rs.getString("birthDate") + ") - " + rs.getInt("count"));
        }
    }
}
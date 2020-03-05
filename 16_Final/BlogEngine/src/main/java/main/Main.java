package main;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.convert.Property;

import java.sql.Connection;
import java.sql.DriverManager;

@SpringBootApplication
public class Main
{
//    @Value("${url}")
//    static String url;
//
//    @Value("${username}")
//    static String dbUser;
//
//    @Value("${password}")
//    static String dbPass;
//
//    @Value("${serverTimezone}")
//    static String serverTimeZone;

    public static void main(String[] args) {

//        connectToDB();
        SpringApplication.run(Main.class, args);
    }

//    private static void connectToDB() {
//        try {
//            Connection connection = DriverManager.getConnection(
//                    url +
//                            "?user=" + dbUser + "&password=" + dbPass + "&useUnicode=true" +
//                            "&useJDBCCompliantTimeZoneShift=true" +
//                            "&useLegacyDatetimeCode=false" +
//                            "&serverTimezone=" + serverTimeZone);
//            connection.createStatement().execute("DROP TABLE IF EXISTS voter_count");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
}
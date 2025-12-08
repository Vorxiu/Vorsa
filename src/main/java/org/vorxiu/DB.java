package org.vorxiu;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DB {
    public static void connect() {
        String url = "jdbc:oracle:thin:@localhost:1522/FREEPDB1";
        String user = "system";
        String pass = "vorsa123";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Connected to Oracle!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM USER_TABLES");

            while (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

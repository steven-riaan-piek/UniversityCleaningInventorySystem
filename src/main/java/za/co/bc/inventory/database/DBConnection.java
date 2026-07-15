package za.co.bc.inventory.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/cleaning_inventory_db";

    private static final String USER = "postgres";

    // Change this to YOUR PostgreSQL password
    private static final String PASSWORD = "AdolfJ";

    public static Connection getConnection() {

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to PostgreSQL!");
            return connection;

        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
            return null;
        }
    }
}
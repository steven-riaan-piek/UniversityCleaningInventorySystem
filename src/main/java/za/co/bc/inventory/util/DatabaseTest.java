package za.co.bc.inventory.util;

import java.sql.Connection;
import za.co.bc.inventory.database.DBConnection;

public class DatabaseTest {

    public static void main(String[] args) {

        Connection connection = DBConnection.getConnection();

        if (connection != null) {
            System.out.println("Database connection successful!");
        } else {
            System.out.println("Database connection failed!");
        }
    }
}
package com.projects.tictanggo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseCreator {
    private static String databasename = "TicTacToe";
    public static void main(String[] args) {
        createDatabase(databasename);
    }

    public static void createDatabase(String dbName) {
        String url = "jdbc:sqlite:" + dbName;

        try (Connection connection = DriverManager.getConnection(url)) {
            System.out.println("Database created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
        }
    }
}

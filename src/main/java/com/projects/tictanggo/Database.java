package com.projects.tictanggo;

import java.sql.*;
import java.util.ArrayList;

public class Database implements AutoCloseable {
    protected Connection connection;

    public Database(String dbName) {
        connect(dbName);
    }

    private void connect(String dbName) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbName + ".db");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void createTable(String tableName, ArrayList<String> columns) {
        StringBuilder createTableSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");

        for (String column : columns) {
            createTableSQL.append(column).append(", ");
        }

        createTableSQL.delete(createTableSQL.length() - 2, createTableSQL.length());
        createTableSQL.append(")");

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL.toString());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void create(String tableName, String values) {
        try {
            // Adding single quotes around string values, assuming values are separated by commas
            String[] parts = values.split(", ");
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {  // Assuming the first value is the player name
                    parts[i] = "'" + parts[i] + "'";
                } else if (!parts[i].matches("\\d+")) {
                    parts[i] = "'" + parts[i] + "'";
                }
            }
    
            // Creating the SQL statement without COALESCE for ID
            String createSQL = "INSERT INTO " + tableName + "(name, won, draw, lose) VALUES (" + String.join(", ", parts) + ")";
    
            try (PreparedStatement preparedStatement = connection.prepareStatement(createSQL)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    

    public void read(String tableName) {
        String selectSQL = "SELECT * FROM " + tableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void update(String tableName, String playerName, String values) {
        try {
            // Creating the SQL statement with a placeholder for the player name
            String updateSQL = "UPDATE " + tableName + " SET " + values + " WHERE name = ?";
    
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                // Set the player name as a parameter
                preparedStatement.setString(1, playerName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    

    public void delete(String tableName, int id) {
        String deleteSQL = "DELETE FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void close() throws SQLException {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}

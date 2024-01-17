package com.projects.tictanggo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseCreator {
    /**
     * Der Name der Datenbank, die für das TicTacToe-Spiel erstellt wird.
     */
    private static String databasename = "TicTacToe";

    /**
     * Die Hauptmethode des Programms. Ruft die Methode createDatabase auf, um die
     * TicTacToe-Datenbank zu erstellen.
     */
    public static void main(String[] args) {
        createDatabase(databasename);
    }

    /**
     * Erstellt eine SQLite-Datenbank mit dem angegebenen Datenbanknamen.
     *
     * @param dbName Der Name der zu erstellenden Datenbank.
     */
    public static void createDatabase(String dbName) {
        // Erstelle die Verbindungs-URL für die SQLite-Datenbank mit dem gegebenen Namen
        String url = "jdbc:sqlite:" + dbName;

        try (Connection connection = DriverManager.getConnection(url)) {
            // Erfolgsmeldung, wenn die Verbindung erfolgreich hergestellt wurde
            System.out.println("Database created successfully.");
        } catch (SQLException e) {
            // Fehlermeldung, falls ein Fehler bei der Verbindungsherstellung auftritt
            System.err.println("Error creating database: " + e.getMessage());
        }
    }

}

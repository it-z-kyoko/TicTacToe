package com.projects.tictanggo;

import java.sql.*;
import java.util.ArrayList;

public class Database implements AutoCloseable {

    // Die Connection-Instanz repräsentiert die Verbindung zur Datenbank
    protected Connection connection;

    /**
     * Konstruktor für die Database-Klasse. Stellt eine Verbindung zur
     * SQLite-Datenbank her.
     *
     * @param dbName Der Name der Datenbank, zu der eine Verbindung hergestellt
     *               werden soll.
     */
    public Database(String dbName) {
        // Die Methode connect wird aufgerufen, um die Verbindung herzustellen
        connect(dbName);
    }

    /**
     * Stellt eine Verbindung zur SQLite-Datenbank her.
     *
     * @param dbName Der Name der Datenbank, zu der eine Verbindung hergestellt
     *               werden soll.
     */
    private void connect(String dbName) {
        try {
            // Die Verbindungs-URL wird erstellt, indem der Datenbankname an die URL
            // angehängt wird
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbName + ".db");
        } catch (SQLException e) {
            // Fehlermeldung, falls die Verbindung nicht hergestellt werden kann
            System.err.println(e.getMessage());
        }
    }

    /**
     * Erstellt eine Tabelle in der Datenbank mit den angegebenen Spalten.
     *
     * @param tableName Der Name der Tabelle, die erstellt werden soll.
     * @param columns   Eine Liste von Spalten für die Tabelle.
     */
    public void createTable(String tableName, ArrayList<String> columns) {
        // Erstellt die CREATE TABLE SQL-Anweisung basierend auf den angegebenen Spalten
        // Fügt dann die Tabelle in die Datenbank ein, falls sie noch nicht existiert
        // Spalten werden durch Komma getrennt und mit einem Leerzeichen abgeschlossen
        // Der Primärschlüssel wird als ID-Spalte erstellt
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

    /**
     * Fügt einen neuen Datensatz in die angegebene Tabelle mit den angegebenen
     * Werten ein.
     *
     * @param tableName Der Name der Tabelle, in die Daten eingefügt werden sollen.
     * @param values    Eine Zeichenkette, die die Werte enthält, die eingefügt
     *                  werden sollen.
     */
    public void create(String tableName, String values) {
        try {
            // Zerlegt die Werte in Teile, um sie vor dem Einfügen zu formatieren
            String[] parts = values.split(", ");
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {
                    parts[i] = "'" + parts[i] + "'";
                } else if (!parts[i].matches("\\d+")) {
                    parts[i] = "'" + parts[i] + "'";
                }
            }

            // Erstellt die INSERT INTO SQL-Anweisung mit den formatierten Werten
            String createSQL = "INSERT INTO " + tableName + "(name, won, draw, lose) VALUES ("
                    + String.join(", ", parts) + ")";

            try (PreparedStatement preparedStatement = connection.prepareStatement(createSQL)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Liest alle Datensätze aus der angegebenen Tabelle und gibt sie auf der
     * Konsole aus.
     *
     * @param tableName Der Name der Tabelle, die gelesen werden soll.
     */
    public void read(String tableName) {
        // Erstellt die SELECT SQL-Anweisung, um alle Datensätze aus der Tabelle zu
        // lesen
        // Gibt dann die Ergebnisse auf der Konsole aus
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

    /**
     * Aktualisiert einen Datensatz in der angegebenen Tabelle mit den angegebenen
     * Werten.
     *
     * @param tableName  Der Name der Tabelle, in der Daten aktualisiert werden
     *                   sollen.
     * @param playerName Der Name des Spielers, dessen Datensatz aktualisiert werden
     *                   soll.
     * @param values     Eine Zeichenkette, die die Werte enthält, die aktualisiert
     *                   werden sollen.
     */
    public void update(String tableName, String playerName, String values) {
        try {
            // Erstellt die UPDATE SQL-Anweisung mit einem Platzhalter für den Spielernamen
            String updateSQL = "UPDATE " + tableName + " SET " + values + " WHERE name = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                // Setzt den Spielernamen als Parameter
                preparedStatement.setString(1, playerName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Löscht einen Datensatz aus der angegebenen Tabelle mit der angegebenen ID.
     *
     * @param tableName Der Name der Tabelle, aus der Daten gelöscht werden sollen.
     * @param id        Die ID des Datensatzes, der gelöscht werden soll.
     */
    public void delete(String tableName, int id) {
        // Erstellt die DELETE SQL-Anweisung, um einen Datensatz basierend auf der ID zu
        // löschen
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("DELETE FROM " + tableName + " WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Schließt die Verbindung zur Datenbank.
     *
     * @throws SQLException Wenn ein SQL-Fehler auftritt.
     */
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

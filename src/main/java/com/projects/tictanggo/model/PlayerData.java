package com.projects.tictanggo.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Die Klasse PlayerData repräsentiert Daten eines Spielers für die Anzeige in einer TableView.
 */
public class PlayerData {
    // Properties für Spielerdaten
    private final SimpleStringProperty player;
    private final SimpleIntegerProperty wins;
    private final SimpleIntegerProperty lost;
    private final SimpleIntegerProperty draw;
    private final SimpleIntegerProperty total;

    /**
     * Konstruktor für die PlayerData-Klasse. Erzeugt eine neue PlayerData-Instanz mit Spielerdaten.
     *
     * @param player Der Spielername.
     * @param wins   Die Anzahl der gewonnenen Spiele.
     * @param lost   Die Anzahl der verlorenen Spiele.
     * @param draw   Die Anzahl der unentschiedenen Spiele.
     */
    public PlayerData(String player, int wins, int lost, int draw) {
        // Initialisiere die Properties
        this.player = new SimpleStringProperty(player);
        this.wins = new SimpleIntegerProperty(wins);
        this.lost = new SimpleIntegerProperty(lost);
        this.draw = new SimpleIntegerProperty(draw);

        // Berechne den Gesamtwert basierend auf eigenen Kriterien (z.B. Gewinnpunkte - Verlustpunkte + Unentschiedenpunkte)
        this.total = new SimpleIntegerProperty((wins * 200) - (100 * lost) + (100 * draw));
    }

    /**
     * Erzeugt eine PlayerData-Instanz basierend auf einem ResultSet.
     *
     * @param rs Das ResultSet aus der Datenbankabfrage.
     * @return Eine PlayerData-Instanz.
     * @throws SQLException Wenn ein SQL-Fehler auftritt.
     */
    public static PlayerData createFromResultSet(ResultSet rs) throws SQLException {
        String playerName = rs.getString("name");
        int wins = rs.getInt("won");
        int lost = rs.getInt("lose");
        int draw = rs.getInt("draw");

        return new PlayerData(playerName, wins, lost, draw);
    }

    // Getter-Methoden für die Properties
    public String getPlayer() {
        return player.get();
    }

    public int getWins() {
        return wins.get();
    }

    public int getLost() {
        return lost.get();
    }

    public int getDraw() {
        return draw.get();
    }

    public int getTotal() {
        return total.get();
    }

    // Getter-Methoden für die Properties (für JavaFX TableView)
    public SimpleStringProperty playerProperty() {
        return player;
    }

    public SimpleIntegerProperty winsProperty() {
        return wins;
    }

    public SimpleIntegerProperty lostProperty() {
        return lost;
    }

    public SimpleIntegerProperty drawProperty() {
        return draw;
    }

    public SimpleIntegerProperty totalProperty() {
        return total;
    }
}

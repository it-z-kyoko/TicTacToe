package com.projects.tictanggo.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PlayerData {
    private final SimpleStringProperty player;
    private final SimpleIntegerProperty wins;
    private final SimpleIntegerProperty lost;
    private final SimpleIntegerProperty draw;
    private final SimpleIntegerProperty total;

    public PlayerData(String player, int wins, int lost, int draw) {
        this.player = new SimpleStringProperty(player);
        this.wins = new SimpleIntegerProperty(wins);
        this.lost = new SimpleIntegerProperty(lost);
        this.draw = new SimpleIntegerProperty(draw);
        this.total = new SimpleIntegerProperty((wins * 200) - ( 100 * lost) + ( 100 * draw));
    }

    // Hier implementierst du die Methode createFromResultSet
    public static PlayerData createFromResultSet(ResultSet rs) throws SQLException {
        String playerName = rs.getString("name");
        int wins = rs.getInt("won");
        int lost = rs.getInt("lose");
        int draw = rs.getInt("draw");

        return new PlayerData(playerName, wins, lost, draw);
    }

    // Hier implementierst du Getter-Methoden für die Properties
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

    // Hier implementierst du Getter-Methoden für die Properties
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

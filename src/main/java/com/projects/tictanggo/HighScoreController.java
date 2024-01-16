package com.projects.tictanggo;

import com.projects.tictanggo.model.PlayerData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HighScoreController {

    @FXML
    private GridPane gridPane;

    @FXML
    private Button back_button;

    @FXML
    private TableView<PlayerData> highscore_table;

    @FXML
    private TableColumn<PlayerData, String> playerColumn;

    @FXML
    private TableColumn<PlayerData, Integer> winsColumn;

    @FXML
    private TableColumn<PlayerData, Integer> lostColumn;

    @FXML
    private TableColumn<PlayerData, Integer> drawColumn;

    @FXML
    private TableColumn<PlayerData, Integer> totalColumn;

    private Database db;

    // Hier kannst du Daten zur TableView hinzufügen
    @FXML
    private void initialize() {
        // Hier kannst du die TableView und ihre Spalten konfigurieren
        playerColumn.setCellValueFactory(cellData -> cellData.getValue().playerProperty());
        winsColumn.setCellValueFactory(cellData -> cellData.getValue().winsProperty().asObject());
        lostColumn.setCellValueFactory(cellData -> cellData.getValue().lostProperty().asObject());
        drawColumn.setCellValueFactory(cellData -> cellData.getValue().drawProperty().asObject());
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());

        // Daten aus der Datenbank lesen und in die TableView setzen
        db = new Database("TicTacToe"); // Setze den Namen deiner Datenbank ein
        highscore_table.setItems(getDatabaseData());

        // Sortiere nach der "Total"-Spalte absteigend
        totalColumn.setSortType(TableColumn.SortType.DESCENDING);
        highscore_table.getSortOrder().add(totalColumn);
        highscore_table.sort();
    }

    // Hier kannst du Daten aus der Datenbank holen und sie in eine ObservableList
    // umwandeln
    private ObservableList<PlayerData> getDatabaseData() {
        ObservableList<PlayerData> data = FXCollections.observableArrayList();

        // Stelle sicher, dass die Methode read in der Datenbank die Spielerdaten
        // zurückgibt
        // Hier gehe ich davon aus, dass sie eine Methode mit dem Namen read ist und sie
        // die Spielerdaten als ResultSet zurückgibt
        String tableName = "players"; // Setze den Namen deiner Spielerdatenbanktabelle ein
        try (PreparedStatement preparedStatement = db.connection.prepareStatement("SELECT * FROM " + tableName);
                ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                // Hier gehe ich davon aus, dass die Methode PlayerData.createFromResultSet
                // existiert
                PlayerData playerData = PlayerData.createFromResultSet(rs);
                data.add(playerData);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return data;
    }

    protected void onBackButtonClick(ActionEvent event) {
        try {
            // Lade die FXML-Datei des Hauptmenüs
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainMenu.fxml"));
            Parent root = loader.load();

            // Erstelle eine neue Szene mit dem Hauptmenü
            Scene scene = new Scene(root);

            // Hole die Bühne (Stage) von dem Event, um die Szene zu setzen
            Stage stage = (Stage) gridPane.getScene().getWindow();
            stage.setScene(scene);

            // Setze den Titel der Bühne (optional)
            stage.setTitle("Main menu");

            // Zeige die Bühne an
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

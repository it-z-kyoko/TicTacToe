package com.projects.tictanggo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;

import com.projects.tictanggo.model.Game;
import com.projects.tictanggo.model.Game.PlayerMode;

public class MainMenuController {

    @FXML
    private RadioButton singleplayerRadioButton; // Definiere den Singleplayer Radio Button

    @FXML
    private RadioButton multiplayerRadioButton; // Definiere den Multiplayer Radio Button

    @FXML
    private RadioButton DiffEasy;

    @FXML
    private RadioButton DiffMedium;

    @FXML
    private RadioButton DiffHard;

    private ToggleGroup toggleGroupMode;
    // Definiere die ToggleGroup fürs Layout, in der die Radio Buttons sind.
    // Diese Toggle Group ermöglicht es uns, dass nur ein Radio Button innerhalb
    // dieser angehakt werden kann

    private ToggleGroup toggleGroupDifficulty;

    @FXML
    private Button startGameButton; // Definiere den Button, um das Game zu starten

    @FXML
    private TextField textplayer1; // Definiere das Textfeld für Spieler 1
    @FXML
    private TextField textplayer2; // Definiere das Textfeld für Spieler 2

    protected static String player1; // Definiere den Spielernamen für Spieler 1

    protected static String player2; // Definiere den Spielernamen für Spieler 2

    protected String diff; // Definiere die Schwierigkeit der KI

    @FXML
    protected void onStartGameButtonClick(ActionEvent event) {
        // Lade das FXML des Ingame-Menüs -> Wir laden mit dieser Funktion die fxml
        // Datei
        FXMLLoader loader = new FXMLLoader(getClass().getResource("inGame.fxml"));
        try {
            Parent root = loader.load();

            // Erstelle eine neue Szene mit dem Ingame-Menü
            Scene scene = new Scene(root);

            // Hole die Bühne (Stage) von dem Event, um die Szene zu setzen
            Stage stage = (Stage) startGameButton.getScene().getWindow();
            stage.setScene(scene);

            // Setze den Titel der Bühne (optional)
            stage.setTitle("Ingame Menü");

            // Zeige die Bühne an
            stage.show();

            // Holen Sie die Controller-Instanz des Ingame-Menüs
            InGameController ingameController = loader.getController();

            // Erstelle das Game-Objekt basierend auf dem ausgewählten RadioButton
            Game game = createGame();

            // Setze das Game-Objekt im InGameController
            ingameController.setGame(game);
            //Gebe die Werte des ausgewählten RadioButtons weiter
            ingameController.setSelectedRadioButton(getSelectedRadioButton(this.toggleGroupMode));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Erstelle das Game-Objekt basierend auf dem ausgewählten RadioButton
    private Game createGame() {
        if ("Singleplayer".equals(getSelectedRadioButton(this.toggleGroupMode))) {
            // Wenn "Singleplayer" ausgewählt ist, erstelle ein Spiel gegen die KI
            String player1Name = textplayer1.getText(); // Hole den Text vom Textfeld für Spieler 1
            this.player1 = player1Name;
            return new Game(player1Name, "AI", PlayerMode.VS_AI);
        } else if ("Multiplayer".equals(getSelectedRadioButton(this.toggleGroupMode))) {
            // Wenn "Multiplayer" ausgewählt ist, erstelle ein Spiel für zwei Spieler
            String player1Name = textplayer1.getText(); // Hole den Text vom Textfeld für Spieler 1
            String player2Name = textplayer2.getText(); // Hole den Text vom Textfeld für Spieler 2
            this.player1 = player1Name; // Weise dem player1 den Text vom Textfeld zu
            this.player2 = player2Name; // Weise dem player2 den Text vom Textfeld zu
            return new Game(player1Name, player2Name, PlayerMode.TWO_PLAYER);
        } else {
            return null;
        }
    }

    @FXML
    public void initialize() {
        toggleGroupMode = new ToggleGroup(); //Generiere eine neue ToggleGroup (ToggleGroup: ermöglicht die Gruppierung von Toggle Buttons/Radio Buttons, wodurch nur einer dieser gleichzeitig aktiviert sein kann)
        singleplayerRadioButton.setToggleGroup(toggleGroupMode); // Füge den SingleplayerRadioButton zu der Toggle Group hinzu
        multiplayerRadioButton.setToggleGroup(toggleGroupMode); // Füge den MultiplayerRadioButton zu der Toggle Group hinzu

        toggleGroupDifficulty = new ToggleGroup(); //Generiere eine neue ToggleGroup
        DiffEasy.setToggleGroup(toggleGroupDifficulty);
        DiffMedium.setToggleGroup(toggleGroupDifficulty);
        DiffHard.setToggleGroup(toggleGroupDifficulty);

        // Füge einen Listener für Änderungen in der ToggleGroup hinzu
        toggleGroupMode.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            // Aktualisiere die Sichtbarkeit basierend auf dem ausgewählten RadioButton
            updateVisibility();
        });

        toggleGroupDifficulty.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updateDifficulty();
        });

        // Rufe die Methode auf, um die Sichtbarkeit zu initialisieren
        updateVisibility();
        updateDifficulty();
        // Setue die Texte, die in der Funktion createGame() zugewiesen wurden. Diese Funktion wird erst wichtig, wenn man aus dem InGame zurück zum MainMenu kommt
        setTexts();
    }

    private void updateDifficulty() {
        this.diff = getSelectedRadioButton(this.toggleGroupDifficulty);
        System.out.println(this.diff);
    }

    // Diese Methode kann aufgerufen werden, um den ausgewählten RadioButton
    // abzurufen
    public String getSelectedRadioButton(ToggleGroup group) {
        RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle(); // Weise der Variable RadioButton diesen zu, welcher selected ist
        if (selectedRadioButton != null) { //Wenn ein RadioButton ausgewählt ist
            return selectedRadioButton.getText(); //Dann hole dir den Text des RadioButtons
        }
        return null; //TODO Fehlerhandling
    }

    // Methode, um die Sichtbarkeit zu aktualisieren
    private void updateVisibility() {
        // Wenn "Singleplayer" ausgewählt ist, mache textplayer2 nicht sichtbar, sonst mache es sichtbar
        textplayer2.setVisible(!"Singleplayer".equals(getSelectedRadioButton(this.toggleGroupMode)));
        textplayer2.setManaged(!"Singleplayer".equals(getSelectedRadioButton(this.toggleGroupMode)));
    }

    public void setTexts() {
        textplayer1.setText(player1); //Setze den Text der Text-Box auf den Wert von player1
        textplayer2.setText(player2); //Setze den Text der Text-Box auf den Wert von player2
    }

    @FXML
    protected void onHighscoreButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("highscore.fxml"));
            Parent root = loader.load();
    
            Stage stage = new Stage(); // Erstelle eine neue Bühne (Stage)
            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/com/projects/styles.css").toExternalForm());
    
            stage.setScene(scene);
            stage.setTitle("Statistics");
    
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}

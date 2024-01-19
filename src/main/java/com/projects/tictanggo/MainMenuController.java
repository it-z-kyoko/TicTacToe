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
    private RadioButton singleplayerRadioButton; // Referenz auf den Singleplayer-RadioButton

    @FXML
    private RadioButton multiplayerRadioButton; // Referenz auf den Multiplayer-RadioButton

    @FXML
    private RadioButton DiffEasy; // Referenz auf den Schwierigkeitsgrad-Einfach-RadioButton

    @FXML
    private RadioButton DiffMedium; // Referenz auf den Schwierigkeitsgrad-Mittel-RadioButton

    @FXML
    private RadioButton DiffHard; // Referenz auf den Schwierigkeitsgrad-Schwer-RadioButton

    private ToggleGroup toggleGroupMode; // ToggleGroup für die Spielmodus-Auswahl (Single- oder Multiplayer)
    // Diese Toggle Group ermöglicht es, dass nur ein RadioButton innerhalb dieser
    // angehakt werden kann

    private ToggleGroup toggleGroupDifficulty; // ToggleGroup für die Schwierigkeitsgrad-Auswahl
    // Diese Toggle Group ermöglicht es, dass nur ein RadioButton innerhalb dieser
    // angehakt werden kann

    @FXML
    private Button startGameButton; // Referenz auf den Button zum Starten des Spiels

    @FXML
    private TextField textplayer1; // Referenz auf das Textfeld für Spieler 1

    @FXML
    private TextField textplayer2; // Referenz auf das Textfeld für Spieler 2

    protected static String player1; // Statische Variable für den Spielernamen von Spieler 1

    protected static String player2; // Statische Variable für den Spielernamen von Spieler 2

    protected Integer difficulty; // Variable für die Schwierigkeitsstufe der KI

    /**
     * Behandelt das Klicken auf den "Start Game" Button.
     * Lädt das FXML des Ingame-Menüs, erstellt eine Szene mit dem Ingame-Menü und
     * zeigt sie an.
     * Übergibt das erstellte Game-Objekt sowie Informationen zu den ausgewählten
     * RadioButtons an den InGame-Controller.
     * 
     * @param event Das ActionEvent, das durch das Klicken des Buttons ausgelöst
     *              wurde.
     */
    @FXML
    protected void onStartGameButtonClick(ActionEvent event) {
        // Lade das FXML des Ingame-Menüs
        FXMLLoader loader = new FXMLLoader(getClass().getResource("inGame.fxml"));
        try {
            // Lade die Root-Komponente des Ingame-Menüs
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

            // Gebe die Werte des ausgewählten RadioButtons weiter
            ingameController.setSelectedRadioButton(getSelectedRadioButton(this.toggleGroupMode));
            ingameController.setDifficulty(this.difficulty);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Erstellt ein Game-Objekt basierend auf dem ausgewählten RadioButton.
     * Je nach Auswahl werden entweder ein Spiel gegen die KI (Singleplayer) oder
     * ein
     * Spiel für zwei Spieler (Multiplayer) erstellt.
     * 
     * @return Ein Game-Objekt, das aufgrund der Auswahl erstellt wurde.
     */
    private Game createGame() {
        if ("Singleplayer".equals(getSelectedRadioButton(this.toggleGroupMode))) {
            // Wenn "Singleplayer" ausgewählt ist, erstelle ein Spiel gegen die KI
            String player1Name = textplayer1.getText(); // Hole den Text vom Textfeld für Spieler 1
            this.player1 = player1Name;
            setTexts();
            return new Game(player1Name, "AI", PlayerMode.VS_AI);
        } else if ("Multiplayer".equals(getSelectedRadioButton(this.toggleGroupMode))) {
            // Wenn "Multiplayer" ausgewählt ist, erstelle ein Spiel für zwei Spieler
            String player1Name = textplayer1.getText(); // Hole den Text vom Textfeld für Spieler 1
            String player2Name = textplayer2.getText(); // Hole den Text vom Textfeld für Spieler 2
            this.player1 = player1Name; // Weise dem player1 den Text vom Textfeld zu
            this.player2 = player2Name; // Weise dem player2 den Text vom Textfeld zu
            return new Game(player1Name, player2Name, PlayerMode.TWO_PLAYER);
        } else {
            // Falls keine gültige Auswahl getroffen wurde, gib null zurück
            return null;
        }
    }

    /**
     * Initialisiert den Controller, wird automatisch aufgerufen, wenn die FXML
     * geladen wird.
     * Hier werden ToggleGroups für die Radio Buttons erstellt, Listener hinzugefügt
     * und die Sichtbarkeit sowie Schwierigkeitsstufe basierend auf den
     * Auswahlwerten aktualisiert.
     */
    @FXML
    public void initialize() {
        // Generiere eine neue ToggleGroup für den Spielmodus
        toggleGroupMode = new ToggleGroup();
        // Füge den SingleplayerRadioButton zu der Toggle Group hinzu
        singleplayerRadioButton.setToggleGroup(toggleGroupMode);
        // Füge den MultiplayerRadioButton zu der Toggle Group hinzu
        multiplayerRadioButton.setToggleGroup(toggleGroupMode);

        // Generiere eine neue ToggleGroup für die Schwierigkeitsgrade
        toggleGroupDifficulty = new ToggleGroup();
        DiffEasy.setToggleGroup(toggleGroupDifficulty);
        DiffMedium.setToggleGroup(toggleGroupDifficulty);
        DiffHard.setToggleGroup(toggleGroupDifficulty);

        // Füge einen Listener für Änderungen in der ToggleGroup für Spielmodus hinzu
        toggleGroupMode.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            // Aktualisiere die Sichtbarkeit basierend auf dem ausgewählten RadioButton
            updateVisibility();
        });

        // Füge einen Listener für Änderungen in der ToggleGroup für Schwierigkeitsgrade
        // hinzu
        toggleGroupDifficulty.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updateDifficulty();
        });

        // Rufe die Methode auf, um die Sichtbarkeit und Schwierigkeitsstufe zu
        // initialisieren
        updateVisibility();
        updateDifficulty();
        // Setze die Texte, die in der Funktion createGame() zugewiesen wurden.
        // Diese Funktion wird wichtig, wenn man aus dem InGame zurück zum MainMenu
        // kommt
        setTexts();
    }

    /**
     * Aktualisiert die Schwierigkeitsstufe basierend auf dem ausgewählten
     * RadioButton.
     * Je nach Auswahl wird die Schwierigkeitsstufe auf 1 (Easy), 2 (Medium) oder 3
     * (Hard) gesetzt.
     * Falls keine gültige Auswahl getroffen wurde, bleibt die Schwierigkeitsstufe
     * unverändert.
     */
    private void updateDifficulty() {
        // Holen Sie den ausgewählten RadioButton für die Schwierigkeitsgrade
        String selectedDifficulty = getSelectedRadioButton(this.toggleGroupDifficulty);

        // Aktualisiere die Schwierigkeitsstufe basierend auf dem ausgewählten
        // RadioButton
        switch (selectedDifficulty) {
            case "Easy":
                this.difficulty = 1;
                break;
            case "Medium":
                this.difficulty = 2;
                break;
            case "Hard":
                this.difficulty = 3;
                break;
            default:
                // Bei keiner gültigen Auswahl bleibt die Schwierigkeitsstufe unverändert
                break;
        }
    }

    /**
     * Ruft den Text des ausgewählten RadioButtons innerhalb einer ToggleGroup ab.
     *
     * @param group Die ToggleGroup, zu der die RadioButtons gehören.
     * @return Der Text des ausgewählten RadioButtons oder null, wenn kein
     *         RadioButton ausgewählt ist.
     *         TODO: Fehlerhandling kann implementiert werden, falls notwendig.
     */
    public String getSelectedRadioButton(ToggleGroup group) {
        // Holen Sie den ausgewählten RadioButton innerhalb der ToggleGroup
        RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();

        // Überprüfen Sie, ob ein RadioButton ausgewählt ist
        if (selectedRadioButton != null) {
            // Falls ausgewählt, geben Sie den Text des RadioButtons zurück
            return selectedRadioButton.getText();
        }

        // TODO: Hier könnte Fehlerbehandlung implementiert werden, falls gewünscht
        return null;
    }

    /**
     * Aktualisiert die Sichtbarkeit des Textfelds für Spieler 2 basierend auf dem
     * ausgewählten Spielmodus.
     * Wenn der Spielmodus "Singleplayer" ist, wird das Textfeld für Spieler 2
     * unsichtbar gemacht,
     * ansonsten wird es sichtbar gemacht.
     */
    private void updateVisibility() {
        // Überprüfe, ob der ausgewählte Spielmodus "Singleplayer" ist
        boolean isSingleplayerMode = "Singleplayer".equals(getSelectedRadioButton(this.toggleGroupMode));

        // Setze die Sichtbarkeit des Textfelds für Spieler 2 basierend auf dem
        // Spielmodus
        textplayer2.setVisible(!isSingleplayerMode);
        textplayer2.setManaged(!isSingleplayerMode);
    }

    /**
     * Setzt die Texte der Textfelder für Spieler 1 und Spieler 2 basierend auf den
     * gespeicherten Spielernamen.
     * Diese Methode wird verwendet, wenn der Benutzer vom Ingame-Menü zum Hauptmenü
     * zurückkehrt.
     */
    public void setTexts() {
        // Setze den Text des Textfelds für Spieler 1 auf den Wert von player1
        textplayer1.setText(player1);

        // Setze den Text des Textfelds für Spieler 2 auf den Wert von player2
        textplayer2.setText(player2);
    }

    /**
     * Event-Handler für den Klick auf den "Highscore" Button.
     * Öffnet ein neues Fenster (Stage) mit der Highscore-Ansicht.
     */
    @FXML
    protected void onHighscoreButtonClick(ActionEvent event) {
        try {
            // Lade die FXML-Datei der Highscore-Ansicht
            FXMLLoader loader = new FXMLLoader(getClass().getResource("highscore.fxml"));
            Parent root = loader.load();

            // Erstelle eine neue Bühne (Stage) für die Highscore-Ansicht
            Stage stage = new Stage();
            Scene scene = new Scene(root, 1280, 720);

            // Füge das externe Stylesheet zur Szene hinzu
            scene.getStylesheets().add(getClass().getResource("/com/projects/styles.css").toExternalForm());

            // Setze die Szene für die Highscore-Bühne
            stage.setScene(scene);

            // Setze den Titel der Highscore-Bühne
            stage.setTitle("Statistics");

            // Zeige die Highscore-Bühne an
            stage.show();
        } catch (IOException e) {
            // Handle eine mögliche IOException, indem du die Fehlermeldung ausgibst
            e.printStackTrace();
        }
    }

}

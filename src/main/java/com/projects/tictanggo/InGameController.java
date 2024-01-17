package com.projects.tictanggo;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.projects.tictanggo.model.AIPlayer; // Importiere die AIPlayer-Klasse
import com.projects.tictanggo.model.Game;
import com.projects.tictanggo.model.Game.GameState;
import com.projects.tictanggo.model.Game.PlayerMode;
import com.projects.tictanggo.model.Spieler;
import com.projects.tictanggo.model.Spieler.Symbol;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class InGameController {
    @FXML
    private Label welcomeText;
    @FXML
    private Button btn;
    @FXML
    private GridPane gridPane;

    // Spielzustand (GameState) und Spielermodus (PlayerMode)
    private GameState gameState;
    private PlayerMode playerMode;

    // Ausgewählter RadioButton und Datenbank-Verbindung
    private String selectedRadioButton;
    private Database db;

    // Schwierigkeitsgrad der KI
    private Integer difficulty;

    // Spieler-Objekte
    private Spieler spieler1;
    private Spieler spieler2;

    // Eine Instanz der AIPlayer-Klasse, die den Computergegner repräsentiert
    private final AIPlayer aiPlayer = new AIPlayer("AI", Spieler.Symbol.O);

    // Game-Objekt, das den aktuellen Zustand des Spiels verwaltet
    private Game game;

    // Aktueller Spieler, Annahme: spieler1 ist der Startspieler
    private Spieler aktuellerSpieler;

    /**
     * Setzt das Game-Objekt und initialisiert die Spieler, den aktuellen Spieler
     * und die Datenbank.
     * 
     * @param game Das Game-Objekt, das gesetzt werden soll.
     */
    public void setGame(Game game) {
        this.game = game; // Setze das Game-Objekt der Klasse auf das übergebene Game-Objekt
        this.spieler1 = game.getPlayer1(); // Setze den Spieler 1 der Klasse auf den Spieler 1 des Spiels
        this.spieler2 = game.getPlayer2(); // Setze den Spieler 2 der Klasse auf den Spieler 2 des Spiels
        this.aktuellerSpieler = spieler1; // Setze den aktuellen Spieler auf spieler1 (Annahme: spieler1 ist der
                                          // Startspieler)
        this.db = new Database("TicTacToe"); // Initialisiere die Datenbank mit dem Namen "TicTacToe"
    }

    /**
     * Event-Handler für den Klick auf den "Hello"-Button.
     * 
     * @param event Das ActionEvent, das den Button-Klick ausgelöst hat.
     */
    @FXML
    protected void onHelloButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        // Überprüfe, ob der aktuelle Spieler und sein Name vorhanden sind und ob der
        // Button leer ist
        if (aktuellerSpieler != null && aktuellerSpieler.getName() != null && clickedButton.getText().isEmpty()) {
            clickedButton.setText(aktuellerSpieler.getSymbol().toString());

            // Ermittle die Zeile und Spalte des geklickten Buttons
            int row = GridPane.getRowIndex(clickedButton);
            int col = GridPane.getColumnIndex(clickedButton);
            Symbol symbol = aktuellerSpieler.getSymbol();

            // Spiele den Zug und erhalte den aktuellen Spielzustand
            this.gameState = game.playTrain(row, col, symbol);

            // Handhabung des Spielzustands
            if (gameState == GameState.Won || gameState == GameState.Draw) {
                // Zeige einen Gewinn- oder Unentschieden-Alert
                showResultAlert(gameState);
                // Setze das Spiel zurück
                resetGame();
            } else {
                System.out.println("Das Spiel läuft weiter.");

                if (game.getPlayerMode() == PlayerMode.VS_AI && aktuellerSpieler.equals(spieler1)) {
                    // Wenn der aktive Spieler spieler1 und der Spielmodus VS_AI ist, wechsle zum
                    // KI-Spieler (aiPlayer)

                    // Verwende einen ScheduledExecutorService für eine Verzögerung von 1 Sekunde
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    executorService.schedule(() -> {
                        // Lasse die KI einen Zug machen
                        int[] aiMove = aiPlayer.makeMove(game.getPlayground(), this.difficulty);

                        // Aktualisiere den Button für die KI im Anwendungsthread
                        Platform.runLater(() -> {
                            Button aiButton = getButtonByCoordinates(aiMove[0], aiMove[1]);
                            aiButton.setText(aiPlayer.getSymbol().toString());

                            // Spiele den Zug der KI und erhalte den aktuellen Spielzustand
                            game.playTrain(aiMove[0], aiMove[1], aiPlayer.getSymbol());

                            // Überprüfe den Spielstatus nach dem Zug der KI
                            this.gameState = game.getGameState();

                            // Handhabung des Spielzustands nach dem Zug der KI
                            if (gameState == GameState.Won || gameState == GameState.Draw) {
                                // Zeige einen Gewinn- oder Unentschieden-Alert
                                showResultAlert(gameState);
                                // Setze das Spiel zurück
                                resetGame();
                            } else {
                                // Wechsle den aktiven Spieler nach dem Zug der KI zurück zu spieler1
                                aktuellerSpieler = spieler1;
                            }
                        });
                    }, 1, TimeUnit.SECONDS);
                }
            }
            // Aktualisiere den aktuellen Spieler auf den im Spiel aktiven Spieler
            this.aktuellerSpieler = game.getActivePlayer();
        }
    }

    /**
     * Methode, um den Button anhand von Zeilen- und Spaltenkoordinaten im GridPane
     * zu erhalten.
     * 
     * @param row Die Zeilenkoordinate des Buttons.
     * @param col Die Spaltenkoordinate des Buttons.
     * @return Der Button an den angegebenen Koordinaten oder null, wenn kein Button
     *         gefunden wurde.
     */
    private Button getButtonByCoordinates(int row, int col) {
        // Durchlaufe alle Kinder des GridPanes, um den Button mit den angegebenen
        // Koordinaten zu finden
        for (Node node : gridPane.getChildren()) {
            // Überprüfe, ob das Kind ein Button ist und ob es die richtigen Koordinaten hat
            if (node instanceof Button && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                // Gib den gefundenen Button zurück
                return (Button) node;
            }
        }
        // Wenn kein Button gefunden wurde, gib null zurück
        return null;
    }

    /**
     * Zeigt einen Dialog mit dem Ergebnis des Spiels an, basierend auf dem
     * übergebenen GameState.
     * 
     * @param gameState Der aktuelle Zustand des Spiels (Won, Draw).
     */
    private void showResultAlert(GameState gameState) {
        String winnerName; // Der Name des Gewinners oder "Niemand" bei einem Unentschieden
        String message; // Die Nachricht, die im Dialog angezeigt wird

        if (gameState == GameState.Won) {
            // Wenn das Spiel gewonnen ist, ermittele den tatsächlichen Gewinner
            winnerName = (aktuellerSpieler.equals(spieler1)) ? spieler1.getName() : spieler2.getName();
            message = winnerName + " won!"; // Erstelle die Gewinnnachricht

            // Speichere die Ergebnisse in der Datenbank
            if (winnerName.equals(spieler1.getName())) {
                safe("p1win", winnerName, spieler2.getName());
            } else if (winnerName.equals(spieler2.getName())) {
                safe("p1lose", spieler1.getName(), winnerName);
            }
        } else {
            // Bei einem Unentschieden
            winnerName = "Niemand";
            message = "The game ends in a draw!"; // Erstelle die Nachricht für ein Unentschieden

            // Speichere die Ergebnisse in der Datenbank
            safe("draw", spieler1.getName(), spieler2.getName());
        }

        // Zeige den Ergebnisdialog mit Titel "Game ends" und der erstellten Nachricht
        // an
        showAlert("Game ends", message);

        // Setze den GameState auf Running, um zu verhindern, dass das Spiel als
        // gewonnen endet,
        // nachdem es zurückgesetzt wurde.
        this.gameState = GameState.Running;
    }

    /**
     * Zeigt einen einfachen Informationsdialog mit dem angegebenen Titel und der
     * Nachricht an.
     * 
     * @param title   Der Titel des Dialogs.
     * @param message Die Nachricht, die im Dialog angezeigt wird.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Erstelle einen Informationsdialog
        alert.setTitle(title); // Setze den Titel des Dialogs
        alert.setHeaderText(null); // Setze den Header-Text auf null, da wir keinen Header benötigen
        alert.setContentText(message); // Setze die Nachricht des Dialogs

        // Füge einen OK-Button hinzu und setze das Event Handling
        ButtonType okButton = new ButtonType("OK"); // Erstelle einen OK-Button
        alert.getButtonTypes().setAll(okButton); // Setze nur den OK-Button als verfügbaren Button-Typ

        Optional<ButtonType> result = alert.showAndWait(); // Zeige den Dialog und warte auf die Bestätigung
    }

    /**
     * Setzt das Spiel zurück, indem die Texte aller Buttons im GridPane auf leer
     * gesetzt werden.
     * Außerdem wird zum Hauptmenü gewechselt.
     */
    private void resetGame() {
        // Durchlaufe alle Kinder des GridPanes
        for (Node node : gridPane.getChildren()) {
            // Überprüfe, ob das Kind ein Button ist
            if (node instanceof Button) {
                Button button = (Button) node; // Caste das Kind zu einem Button
                button.setText(""); // Setze den Text des Buttons auf leer
            }
        }
        // Wechsle zum Hauptmenü
        switchToMainMenu();
    }

    /**
     * Wechselt zur Hauptmenü-Szene, indem die FXML-Datei des Hauptmenüs geladen und
     * angezeigt wird.
     * Die Methode wird normalerweise aufgerufen, nachdem das Spiel zurückgesetzt
     * wurde.
     */
    private void switchToMainMenu() {
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
            // Hier kannst du ggf. mit der Ausnahme umgehen, z.B., indem du eine
            // Fehlermeldung anzeigst
        }
    }

    /**
     * Setzt den ausgewählten RadioButton und aktualisiert den Spielermodus
     * entsprechend.
     * 
     * @param selectedRadioButton Der ausgewählte RadioButton (Singleplayer oder
     *                            Multiplayer).
     */
    public void setSelectedRadioButton(String selectedRadioButton) {
        this.selectedRadioButton = selectedRadioButton; // Setze den ausgewählten RadioButton

        // Aktualisiere den Spielermodus basierend auf dem ausgewählten RadioButton
        if ("Singleplayer".equals(selectedRadioButton)) {
            this.playerMode = PlayerMode.VS_AI; // Wenn "Singleplayer" ausgewählt ist, setze den Spielermodus auf VS_AI
        } else if ("Multiplayer".equals(selectedRadioButton)) {
            this.playerMode = PlayerMode.TWO_PLAYER; // Wenn "Multiplayer" ausgewählt ist, setze den Spielermodus auf
                                                     // TWO_PLAYER
        } else {
            this.playerMode = null; // Setze den Spielermodus auf null, wenn kein gültiger RadioButton ausgewählt
                                    // ist
        }
    }

    /**
     * Aktualisiert die Statistikdaten basierend auf dem Spielergebnis.
     * 
     * @param Gcase  Der Fall des Spiels (p1win, draw, p1lose).
     * @param p1name Der Name des Spielers 1.
     * @param p2name Der Name des Spielers 2.
     */
    public void safe(String Gcase, String p1name, String p2name) {
        Integer p1w = 0; // Anzahl der Siege für Spieler 1
        Integer p2w = 0; // Anzahl der Siege für Spieler 2
        Integer p1d = 0; // Anzahl der Unentschieden für Spieler 1
        Integer p2d = 0; // Anzahl der Unentschieden für Spieler 2
        Integer p1l = 0; // Anzahl der Niederlagen für Spieler 1
        Integer p2l = 0; // Anzahl der Niederlagen für Spieler 2

        // Aktualisiere die Werte basierend auf dem Spielergewinn oder Unentschieden
        switch (Gcase) {
            case "p1win":
                p1w = 1;
                p2l = 1;
                break;
            case "draw":
                p1d = 1;
                p2d = 1;
                break;
            case "p1lose":
                p2w = 1;
                p1l = 1;
                break;
            default:
                break;
        }

        // Rufe die Methode auf, um die Statistikdaten zu aktualisieren
        safeData(p1name, p2name, p1w, p2w, p1d, p2d, p1l, p2l);
    }

    /**
     * Aktualisiert die Statistikdaten für die beiden Spieler in der Datenbank.
     * 
     * @param p1name Der Name des Spielers 1.
     * @param p2name Der Name des Spielers 2.
     * @param p1w    Die Anzahl der Siege für Spieler 1.
     * @param p2w    Die Anzahl der Siege für Spieler 2.
     * @param p1d    Die Anzahl der Unentschieden für Spieler 1.
     * @param p2d    Die Anzahl der Unentschieden für Spieler 2.
     * @param p1l    Die Anzahl der Niederlagen für Spieler 1.
     * @param p2l    Die Anzahl der Niederlagen für Spieler 2.
     */
    public void safeData(String p1name, String p2name, Integer p1w, Integer p2w, Integer p1d, Integer p2d, Integer p1l,
            Integer p2l) {
        // Überprüfe, ob ein Datensatz für p1name bereits vorhanden ist
        boolean p1Exists = checkPlayerExists(p1name);

        // Überprüfe, ob ein Datensatz für p2name bereits vorhanden ist
        boolean p2Exists = checkPlayerExists(p2name);

        // Wenn Datensatz für p1name vorhanden ist, aktualisiere ihn, sonst füge einen
        // neuen ein
        if (p1Exists) {
            updateData("players", p1name, p1w, p1d, p1l);
        } else {
            createData("players", p1name, p1w, p1d, p1l);
        }

        // Wenn Datensatz für p2name vorhanden ist, aktualisiere ihn, sonst füge einen
        // neuen ein
        if (p2Exists) {
            updateData("players", p2name, p2w, p2d, p2l);
        } else {
            createData("players", p2name, p2w, p2d, p2l);
        }
    }

    /**
     * Überprüft, ob bereits ein Datensatz für einen bestimmten Spieler in der
     * Datenbank existiert.
     * 
     * @param playerName Der Name des Spielers, dessen Existenz überprüft werden
     *                   soll.
     * @return true, wenn ein Datensatz für den Spieler gefunden wurde; false sonst.
     */
    private boolean checkPlayerExists(String playerName) {
        String selectSQL = "SELECT * FROM players WHERE name = ?"; // SQL-Abfrage, um nach dem Spieler in der Datenbank
                                                                   // zu suchen
        try (PreparedStatement preparedStatement = db.connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, playerName); // Setze den Spielername als Parameter für die SQL-Abfrage
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next(); // Gibt true zurück, wenn ein Datensatz gefunden wurde
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false; // Bei einer SQL-Ausnahme wird false zurückgegeben
        }
    }

    /**
     * Erstellt einen neuen Datensatz für einen Spieler in der Datenbank.
     * 
     * @param tableName  Der Name der Tabelle, in der der Datensatz erstellt werden
     *                   soll.
     * @param playerName Der Name des Spielers für den neuen Datensatz.
     * @param won        Die Anzahl der Siege für den neuen Datensatz.
     * @param draw       Die Anzahl der Unentschieden für den neuen Datensatz.
     * @param lose       Die Anzahl der Niederlagen für den neuen Datensatz.
     */
    private void createData(String tableName, String playerName, Integer won, Integer draw, Integer lose) {
        // Erstelle den Wertestring für die Datenbank-Operation
        String values = playerName + ", " + won + ", " + draw + ", " + lose;

        // Führe die Datenbankoperation zum Erstellen eines neuen Datensatzes aus
        db.create(tableName, values);
    }

    /**
     * Aktualisiert die Statistikdaten eines Spielers in der Datenbank.
     * 
     * @param tableName  Der Name der Tabelle, in der der Datensatz aktualisiert
     *                   werden soll.
     * @param playerName Der Name des Spielers, dessen Datensatz aktualisiert werden
     *                   soll.
     * @param won        Die Anzahl der Siege, die hinzugefügt werden sollen.
     * @param draw       Die Anzahl der Unentschieden, die hinzugefügt werden
     *                   sollen.
     * @param lose       Die Anzahl der Niederlagen, die hinzugefügt werden sollen.
     */
    private void updateData(String tableName, String playerName, Integer won, Integer draw, Integer lose) {
        // Erstelle den Wertestring für die Datenbank-Operation
        String updateValues = "won = won + " + won + ", draw = draw + " + draw + ", lose = lose + " + lose;

        // Führe die Datenbankoperation zum Aktualisieren des Datensatzes aus
        db.update(tableName, playerName, updateValues);
    }

    /**
     * Setzt die Schwierigkeitsstufe für das Spiel.
     * 
     * @param difficulty Die Schwierigkeitsstufe, die festgelegt werden soll.
     */
    protected void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

}
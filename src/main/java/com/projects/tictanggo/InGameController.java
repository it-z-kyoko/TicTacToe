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
    private GameState gameState;
    private PlayerMode playerMode;
    private String selectedRadioButton;
    private Database db;

    private Spieler spieler1;
    private Spieler spieler2;
    private final AIPlayer aiPlayer = new AIPlayer("AI", Spieler.Symbol.O); // Erstelle eine AIPlayer-Instanz
    private Game game;
    private Spieler aktuellerSpieler; // Annahme: spieler1 ist der Startspieler

    public void setGame(Game game) {
        this.game = game;
        this.spieler1 = game.getPlayer1();
        this.spieler2 = game.getSPlayer2();
        this.aktuellerSpieler = spieler1;
        this.db = new Database("TicTacToe");
    }

    @FXML
    protected void onHelloButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        if (aktuellerSpieler != null && aktuellerSpieler.getName() != null && clickedButton.getText().isEmpty()) {
            clickedButton.setText(aktuellerSpieler.getSymbol().toString());

            int row = GridPane.getRowIndex(clickedButton);
            int col = GridPane.getColumnIndex(clickedButton);
            Symbol symbol = aktuellerSpieler.getSymbol();

            this.gameState = game.playTrain(row, col, symbol);

            // Jetzt kannst du den gameState entsprechend handhaben
            if (gameState == GameState.Won || gameState == GameState.Draw) {
                // Zeige einen Gewinn- oder Unentschieden-Alert
                showResultAlert(gameState);
                // Hier wird das Spiel zurückgesetzt
                resetGame();
            } else {
                System.out.println("Das Spiel läuft weiter.");

                if (game.getPlayerMode() == PlayerMode.VS_AI && aktuellerSpieler.equals(spieler1)) {
                    aktuellerSpieler = aiPlayer;
                    // Wenn der aktive Spieler die KI ist, lasse sie einen Zug machen

                    // Verwende einen ScheduledExecutorService für eine Verzögerung von 1 Sekunde
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    executorService.schedule(() -> {
                        int[] aiMove = aiPlayer.makeMove(game.getPlayground(), 3);

                        // Aktualisiere den Button für die KI im Anwendungsthread
                        Platform.runLater(() -> {
                            Button aiButton = getButtonByCoordinates(aiMove[0], aiMove[1]);
                            aiButton.setText(aiPlayer.getSymbol().toString());

                            // Führe den restlichen Code nach der Verzögerung aus
                            game.playTrain(aiMove[0], aiMove[1], aiPlayer.getSymbol());

                            // Überprüfe den Spielstatus nach dem Zug der KI
                            this.gameState = game.getGameState();

                            if (gameState == GameState.Won || gameState == GameState.Draw) {
                                // Zeige einen Gewinn- oder Unentschieden-Alert nach dem Zug der KI
                                showResultAlert(gameState);
                                // Hier wird das Spiel zurückgesetzt
                                resetGame();
                            } else {
                                // Wechsle den aktiven Spieler nach dem Zug der KI
                                aktuellerSpieler = spieler1;
                            }
                        });
                    }, 1, TimeUnit.SECONDS);
                }
            }
            this.aktuellerSpieler = game.getActiveSpieler();
        }
    }

    // Methode, um den Button anhand von Zeilen- und Spaltenkoordinaten zu erhalten
    private Button getButtonByCoordinates(int row, int col) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof Button && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (Button) node;
            }
        }
        return null;
    }

    private void showResultAlert(GameState gameState) {
        String winnerName;
        String message;
        if (gameState == GameState.Won) {
            // Wenn das Spiel gewonnen ist, ermittele den tatsächlichen Gewinner
            winnerName = (aktuellerSpieler.equals(spieler1)) ? spieler1.getName() : spieler2.getName();
            message = winnerName + " won!";
            if (winnerName.equals(spieler1.getName())) {
                safe("p1win", winnerName, spieler2.getName());
            } else if (winnerName.equals(spieler2.getName())) {
                safe("p1lose", spieler1.getName(), winnerName);
            }
        } else {
            winnerName = "Niemand";
            message = "The game ends in a draw!";
            safe("draw", spieler1.getName(), spieler2.getName());
        }

        showAlert("Game ends", message);

        // Setze den GameState auf Running, um zu verhindern, dass das Spiel als
        // gewonnen endet,
        // nachdem es zurückgesetzt wurde.
        this.gameState = GameState.Running;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Füge einen OK-Button hinzu und setze das Event Handling
        ButtonType okButton = new ButtonType("OK");
        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
    }

    private void resetGame() {

        // Setze die Texte aller Buttons auf leer
        for (Node node : gridPane.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setText("");
            }
        }
        switchToMainMenu();
    }

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
            // Hier kannst du ggf. mit der Ausnahme umgehen
        }
    }

    public void setSelectedRadioButton(String selectedRadioButton) {
        this.selectedRadioButton = selectedRadioButton;
        if ("Singleplayer".equals(selectedRadioButton)) {
            this.playerMode = PlayerMode.VS_AI;
        } else if ("Multiplayer".equals(selectedRadioButton)) {
            this.playerMode = PlayerMode.TWO_PLAYER;
        } else {
            this.playerMode = null;
        }
    }

    public void safe(String Gcase, String p1name, String p2name) {
        Integer p1w = 0;
        Integer p2w = 0;
        Integer p1d = 0;
        Integer p2d = 0;
        Integer p1l = 0;
        Integer p2l = 0;
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
        safeData(p1name, p2name, p1w, p2w, p1d, p2d, p1l, p2l);
    }

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

    private boolean checkPlayerExists(String playerName) {
        String selectSQL = "SELECT * FROM players WHERE name = ?";
        try (PreparedStatement preparedStatement = db.connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, playerName);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next(); // Gibt true zurück, wenn ein Datensatz gefunden wurde
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    private void createData(String tableName, String playerName, Integer won, Integer draw, Integer lose) {
        String values = playerName + ", " + won + ", " + draw + ", " + lose;
        db.create(tableName, values);
    }

    private void updateData(String tableName, String playerName, Integer won, Integer draw, Integer lose) {
        String updateValues = "won = won + " + won + ", draw = draw + " + draw + ", lose = lose + " + lose;
        db.update(tableName, playerName, updateValues);
    }

}
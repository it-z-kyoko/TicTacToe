package com.projects.tictanggo.model;

import com.projects.tictanggo.model.Spieler.Symbol;

/**
 * Die Klasse Game repräsentiert ein Tic-Tac-Toe-Spiel.
 */
public class Game {
    // Spielfeld
    private Symbol[][] playground;

    // Spieler
    private Spieler player1;
    private Spieler player2;
    private Spieler activePlayer;

    // Spielzustand
    private GameState gameState;

    // Spielmodus
    private PlayerMode playerMode;

    // KI-Spieler (falls im KI-Modus)
    private AIPlayer aiPlayer;

    /**
     * Konstruktor für die Game-Klasse. Erzeugt eine neue Instanz des Spiels.
     *
     * @param name1      Der Name des ersten Spielers.
     * @param name2      Der Name des zweiten Spielers.
     * @param playerMode Der Spielmodus (Zwei Spieler oder gegen KI).
     */
    public Game(String name1, String name2, PlayerMode playerMode) {
        this.player1 = new Spieler(name1, Symbol.X);
        this.player2 = new Spieler(name2, Symbol.O);
        this.activePlayer = this.player1;
        this.playground = new Symbol[3][3];
        this.gameState = GameState.Running;
        this.playerMode = playerMode;

        // Initialisiere das Spielfeld mit leeren Symbolen
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                playground[i][j] = Symbol.E;
            }
        }

        // Falls der Spielmodus KI gegen Spieler ist, erstelle einen KI-Spieler
        if (playerMode == PlayerMode.VS_AI) {
            aiPlayer = new AIPlayer("AI", Spieler.Symbol.O);
        }
    }

    // Getter-Methoden für verschiedene Attribute
    public Spieler getPlayer1() {
        return player1;
    }

    public Spieler getPlayer2() {
        return player2;
    }

    public Spieler getActivePlayer() {
        return activePlayer;
    }

    public Symbol[][] getPlayground() {
        return playground;
    }

    public GameState getGameState() {
        return gameState;
    }

    public PlayerMode getPlayerMode() {
        return playerMode;
    }

    /**
     * Spielt einen Zug auf dem Spielfeld und aktualisiert den Spielzustand.
     *
     * @param x      Die x-Koordinate des Zugs.
     * @param y      Die y-Koordinate des Zugs.
     * @param symbol Das Symbol des Spielers, der den Zug macht.
     * @return Der aktuelle Spielzustand nach dem Zug.
     */
    public GameState playTrain(int x, int y, Symbol symbol) {
        if (this.gameState == GameState.Running) {
            playground[x][y] = symbol;

            // Wenn der aktive Spieler die KI ist, lasse sie einen Zug machen
            if (playerMode == PlayerMode.VS_AI && activePlayer == aiPlayer) {
                int[] aiMove = aiPlayer.makeMove(playground, 3);
                playground[aiMove[0]][aiMove[1]] = aiPlayer.getSymbol();
            }

            // Warte 3 Sekunden, bevor CheckWin aufgerufen wird (kann angepasst werden)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Aktualisiere den Spielzustand und wechsle den aktiven Spieler
            this.gameState = checkWin();
            switchPlayer();
        }

        return this.gameState;
    }

    /**
     * Wechselt den aktiven Spieler.
     */
    private void switchPlayer() {
        if (this.activePlayer == this.player1) {
            this.activePlayer = this.player2;
        } else {
            this.activePlayer = this.player1;
        }
    }

    /**
     * Überprüft, ob ein Spieler gewonnen hat oder ob das Spiel unentschieden ist.
     *
     * @return Der aktuelle Spielzustand nach der Überprüfung.
     */
    private GameState checkWin() {
        // Überprüfe auf eine Gewinnbedingung basierend auf den Symbolen in einer Reihe,
        // Spalte oder Diagonale
        for (int i = 0; i < 3; i++) {
            if (playground[i][0] != Symbol.E && playground[i][0] == playground[i][1]
                    && playground[i][1] == playground[i][2]) {
                return GameState.Won;
            }
            if (playground[0][i] != Symbol.E && playground[0][i] == playground[1][i]
                    && playground[1][i] == playground[2][i]) {
                return GameState.Won;
            }
        }

        if (playground[0][0] != Symbol.E && playground[0][0] == playground[1][1]
                && playground[1][1] == playground[2][2]) {
            return GameState.Won;
        }

        if (playground[0][2] != Symbol.E && playground[0][2] == playground[1][1]
                && playground[1][1] == playground[2][0]) {
            return GameState.Won;
        }

        // Überprüfe auf ein Unentschieden
        boolean isDraw = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (playground[i][j] == Symbol.E) {
                    isDraw = false;
                    break;
                }
            }
        }

        if (isDraw) {
            return GameState.Draw;
        }

        // Das Spiel läuft noch
        return GameState.Running;
    }

    public enum PlayerMode {
        TWO_PLAYER,
        VS_AI
    }

    public enum GameState {
        Running, Won, Draw
    }
}

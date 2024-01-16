package com.projects.tictanggo.model;

import com.projects.tictanggo.model.Spieler.Symbol;

public class Game {
    private Symbol[][] playground;
    private Spieler Player1;
    private Spieler Player2;
    private Spieler activeSpieler;
    private GameState gameState;
    private PlayerMode playerMode;
    private AIPlayer aiPlayer;

    public Game(String name1, String name2, PlayerMode playerMode) {
        this.Player1 = new Spieler(name1, Symbol.X);
        this.Player2 = new Spieler(name2, Symbol.O);
        this.activeSpieler = this.Player1;
        this.playground = new Symbol[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                playground[i][j] = Symbol.E;
            }
        }
        this.gameState = GameState.Running;
        this.playerMode = playerMode;
        if (playerMode == PlayerMode.VS_AI) {
            aiPlayer = new AIPlayer("AI", Spieler.Symbol.O);
        }
    }

    public Spieler getPlayer1() {
        return Player1;
    }

    public Symbol[][] getPlayground() {
        return playground;
    }

    public Spieler getPlayer2() {
        return Player2;
    }

    public Spieler getActiveSpieler() {
        return activeSpieler;
    }

    public GameState getGameState() {
        return gameState;
    }

    public enum PlayerMode {
        TWO_PLAYER,
        VS_AI
    }

    public enum GameState {
        Running, Won, Draw
    }

    public PlayerMode getPlayerMode() {
        return playerMode;
    }

    private void switchPlayer() {
        if (this.activeSpieler == this.Player1) {
            this.activeSpieler = this.Player2;
        } else {
            this.activeSpieler = this.Player1;
        }
    }

    public GameState playTrain(int x, int y, Symbol symbol) {
        if (this.gameState == GameState.Running) {
            playground[x][y] = symbol;
    
            if (playerMode == PlayerMode.VS_AI && activeSpieler == aiPlayer) {
                // Wenn der aktive Spieler die KI ist, lasse sie einen Zug machen
                int[] aiMove = aiPlayer.makeMove(playground, 3);
                playground[aiMove[0]][aiMove[1]] = aiPlayer.getSymbol();
            }
    
            // Warte 3 Sekunden, bevor CheckWin aufgerufen wird
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    
            this.gameState = CheckWin();
            switchPlayer();
        }
    
        return this.gameState;
    }
    

    private GameState CheckWin() {
        // Check for a winning condition based on the symbols in a row, column, or diagonal
        for (int i = 0; i < 3; i++) {
            if (playground[i][0] != Symbol.E && playground[i][0] == playground[i][1] && playground[i][1] == playground[i][2]) {
                return GameState.Won;
            }
            if (playground[0][i] != Symbol.E && playground[0][i] == playground[1][i] && playground[1][i] == playground[2][i]) {
                return GameState.Won;
            }
        }
    
        if (playground[0][0] != Symbol.E && playground[0][0] == playground[1][1] && playground[1][1] == playground[2][2]) {
            return GameState.Won;
        }
    
        if (playground[0][2] != Symbol.E && playground[0][2] == playground[1][1] && playground[1][1] == playground[2][0]) {
            return GameState.Won;
        }
    
        // Check for a draw
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
    
        // The game is still running
        return GameState.Running;
    }
    

}

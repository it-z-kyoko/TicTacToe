package com.projects.tictanggo.model;

import java.util.Random;

public class AIPlayer extends Spieler {
    public AIPlayer(String name, Symbol symbol) {
        super(name, symbol);
    }

    public int[] makeMove(Symbol[][] playground, int difficulty) {
        switch (difficulty) {
            case 1:
                return makeRandomMove(playground);
            case 2:
                return makeStrategicMove(playground, false);
            case 3:
                return makeStrategicMove(playground, true);
            default:
                throw new IllegalArgumentException("Ungültiger Schwierigkeitsgrad");
        }
    }

    private int[] makeRandomMove(Symbol[][] playground) {
        Random random = new Random();

        int x, y;
        do {
            x = random.nextInt(3);
            y = random.nextInt(3);
        } while (playground[x][y] != Symbol.E);

        return new int[] { x, y };
    }

    private int[] makeStrategicMove(Symbol[][] playground, boolean prioritizeWin) {
        Symbol aiSymbol = Symbol.O; // Annahme: Die KI verwendet das Symbol "O"

        // Überprüfe, ob die KI einen eigenen Gewinnzug machen kann
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (playground[i][j] == Symbol.E) {
                    playground[i][j] = aiSymbol;
                    if (checkWin(playground, aiSymbol)) {
                        // Wenn die KI durch diesen Zug gewinnen kann, mache diesen Zug
                        return new int[] { i, j };
                    }
                    // Setze den Zustand zurück, um andere Züge zu überprüfen
                    playground[i][j] = Symbol.E;
                }
            }
        }

        // Überprüfe, ob der Gegner durch diesen Zug gewinnen kann
        Symbol opponentSymbol = (aiSymbol == Symbol.X) ? Symbol.O : Symbol.X;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (playground[i][j] == Symbol.E) {
                    playground[i][j] = opponentSymbol;
                    if (checkWin(playground, opponentSymbol)) {
                        // Verhindere den Gewinn des Gegners, indem du diesen Zug machst
                        return new int[] { i, j };
                    }
                    // Setze den Zustand zurück, um andere Züge zu überprüfen
                    playground[i][j] = Symbol.E;
                }
            }
        }

        // Wenn es keine Möglichkeit gibt, den Gewinn des Gegners zu verhindern, mache
        // einen zufälligen Zug
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(3);
            y = random.nextInt(3);
        } while (playground[x][y] != Symbol.E);

        return new int[] { x, y };
    }

    private boolean checkWin(Symbol[][] playground, Symbol symbol) {
        // Überprüfe Zeilen und Spalten
        for (int i = 0; i < 3; i++) {
            if (playground[i][0] == symbol && playground[i][1] == symbol && playground[i][2] == symbol) {
                return true; // Gewonnen in der Reihe
            }
            if (playground[0][i] == symbol && playground[1][i] == symbol && playground[2][i] == symbol) {
                return true; // Gewonnen in der Spalte
            }
        }

        // Überprüfe Diagonalen
        if (playground[0][0] == symbol && playground[1][1] == symbol && playground[2][2] == symbol) {
            return true; // Gewonnen in der Hauptdiagonale
        }
        if (playground[0][2] == symbol && playground[1][1] == symbol && playground[2][0] == symbol) {
            return true; // Gewonnen in der Nebendiagonale
        }

        return false;
    }

}

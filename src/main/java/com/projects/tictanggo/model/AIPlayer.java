package com.projects.tictanggo.model;

import java.util.Random;

/**
 * Die Klasse AIPlayer repräsentiert einen KI-Spieler im Tic-Tac-Toe-Spiel.
 */
public class AIPlayer extends Player {

    /**
     * Konstruktor für die AIPlayer-Klasse.
     *
     * @param name   Der Name des KI-Spielers.
     * @param symbol Das Symbol des KI-Spielers (X oder O).
     */
    public AIPlayer(String name, Symbol symbol) {
        super(name, symbol);
    }

    /**
     * Macht einen Zug basierend auf dem gewählten Schwierigkeitsgrad.
     *
     * @param playground Das aktuelle Spielfeld.
     * @param difficulty Der Schwierigkeitsgrad (1: Zufällige Züge, 2: Strategische
     *                   Züge, 3: Priorisierte gewinnbringende Züge).
     * @return Ein Array mit den Koordinaten des Zugs (x, y).
     */
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

    /**
     * Macht einen zufälligen Zug auf dem Spielfeld.
     *
     * @param playground Das aktuelle Spielfeld.
     * @return Ein Array mit den Koordinaten des Zugs (x, y).
     */
    private int[] makeRandomMove(Symbol[][] playground) {
        Random random = new Random();

        int x, y;
        do {
            x = random.nextInt(3);
            y = random.nextInt(3);
        } while (playground[x][y] != Symbol.E);

        return new int[] { x, y };
    }

    /**
     * Macht einen strategischen Zug auf dem Spielfeld.
     *
     * @param playground        Das aktuelle Spielfeld.
     * @param prioritizeWinning Gibt an, ob gewinnbringende Züge priorisiert werden
     *                          sollen.
     * @return Ein Array mit den Koordinaten des Zugs (x, y).
     */
    private int[] makeStrategicMove(Symbol[][] playground, boolean prioritizeWinning) {
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

        // Wenn es keine Möglichkeit gibt, den Gewinn des Gegners zu verhindern oder zu
        // gewinnen,
        // mache einen zufälligen Zug (kann verbessert werden)
        if (!prioritizeWinning) {
            return makeRandomMove(playground);
        }

        // Priorisiere gewinnbringende Züge (kann angepasst werden)
        return prioritizeWinningMove(playground);
    }

    /**
     * Priorisiert einen gewinnbringenden Zug auf dem Spielfeld.
     *
     * @param playground Das aktuelle Spielfeld.
     * @return Ein Array mit den Koordinaten des gewinnbringenden Zugs (x, y).
     */
    private int[] prioritizeWinningMove(Symbol[][] playground) {
        // Implementiere deine Logik für priorisierte gewinnbringende Züge hier (kann
        // angepasst werden)
        // In dieser einfachen Implementierung wird ein zufälliger Zug gemacht
        return makeRandomMove(playground);
    }

    /**
     * Überprüft, ob ein Spieler mit dem gegebenen Symbol gewonnen hat.
     *
     * @param playground Das aktuelle Spielfeld.
     * @param symbol     Das zu überprüfende Symbol.
     * @return True, wenn der Spieler mit dem Symbol gewonnen hat, sonst False.
     */
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

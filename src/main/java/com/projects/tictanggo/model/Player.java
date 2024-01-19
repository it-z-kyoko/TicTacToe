package com.projects.tictanggo.model;

/**
 * Die Klasse Spieler repräsentiert einen Spieler im Tic-Tac-Toe-Spiel.
 */
public class Player {
    // Der Name des Spielers
    private final String name;

    // Das Symbol (X, O oder E) des Spielers
    private final Symbol symbol;

    /**
     * Konstruktor für die Spieler-Klasse. Erzeugt einen neuen Spieler mit einem Namen und einem Symbol.
     *
     * @param name   Der Name des Spielers.
     * @param symbol Das Symbol des Spielers (X, O oder E).
     */
    public Player(String name, Symbol symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    /**
     * Gibt den Namen des Spielers zurück.
     *
     * @return Der Name des Spielers.
     */
    public String getName() {
        return name;
    }

    /**
     * Gibt das Symbol des Spielers zurück.
     *
     * @return Das Symbol des Spielers (X, O oder E).
     */
    public Symbol getSymbol() {
        return symbol;
    }

    /**
     * Enum für die Symbole, die ein Spieler haben kann (X, O oder E).
     */
    public enum Symbol {
        X, // Symbol für Spieler 1
        O, // Symbol für Spieler 2
        E  // Leeres Feld (Symbol für ein leeres Spielfeld)
    }
}

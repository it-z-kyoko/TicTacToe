package com.projects.tictanggo.model;

public class Spieler {
    private final String name;
    private final Symbol symbol;

    public String getName() {
        return name;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Spieler(String name, Symbol symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public enum Symbol {
        X, O, E
    }

}

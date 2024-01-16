package com.projects.tictanggo.migrations;

import java.util.ArrayList;

public class InGameTable implements table{
    @Override
    public String tableName() {
        return "inGame";
    }
    @Override
    public ArrayList<String> values() {
        ArrayList<String> TableColumns = new ArrayList<>();

        TableColumns.add("id INTEGER PRIMARY KEY");
        TableColumns.add("player TEXT");
        TableColumns.add("won int");
        TableColumns.add("lose int");

        return TableColumns;
    }
}

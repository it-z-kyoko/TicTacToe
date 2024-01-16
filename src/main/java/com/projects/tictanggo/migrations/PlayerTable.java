package com.projects.tictanggo.migrations;

import java.util.ArrayList;

public class PlayerTable implements table{
    @Override
    public String tableName() {
        return "player";
    }
    @Override
    public ArrayList<String> values() {
        ArrayList<String> TableColumns = new ArrayList<>();

        TableColumns.add("id INTEGER PRIMARY KEY");
        TableColumns.add("player TEXT");
        TableColumns.add("won INT");
        TableColumns.add("lose INT");
        TableColumns.add("draw INT");
        TableColumns.add("total INT");

        return TableColumns;
    }
}

package com.projects.tictanggo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        initializeDatabase();
        FXMLLoader fxmlLoader = new FXMLLoader(
        HelloApplication.class.getResource("/com/projects/tictanggo/mainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("TicTangGo");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void initializeDatabase() {
        try (Database database = new Database("TicTacToe")) {
            ArrayList<String> columns = new ArrayList<>();
            columns.add("id INTEGER PRIMARY KEY");
            columns.add("name TEXT");
            columns.add("won INTEGER");
            columns.add("draw INTEGER");
            columns.add("lose INTEGER");

            database.createTable("players", columns);
        } catch (SQLException e) {
            System.out.println("Fehler");
            e.printStackTrace(); // Handle the exception appropriately in your application
        }
    }
}

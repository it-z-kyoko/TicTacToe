package com.projects.tictanggo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    /**
     * Die start-Methode wird aufgerufen, wenn die JavaFX-Anwendung gestartet wird.
     * Hier wird die Anwendung initialisiert, die Datenbank eingerichtet und das
     * Hauptmenü geladen.
     *
     * @param stage Die Hauptbühne (Stage), auf der die JavaFX-Anwendung angezeigt
     *              wird.
     * @throws IOException Wenn es Probleme beim Laden der FXML-Datei gibt.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Initialisiere die Datenbank
        initializeDatabase();

        // Lade die FXML-Datei des Hauptmenüs mit Hilfe des FXMLLoader
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("/com/projects/tictanggo/mainMenu.fxml"));

        // Erstelle eine Szene (Scene) mit der geladenen FXML-Datei und einer
        // festgelegten Größe
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        // Setze den Titel der Hauptbühne
        stage.setTitle("TicTangGo");

        // Setze die Szene auf die Hauptbühne
        stage.setScene(scene);

        // Zeige die Hauptbühne an
        stage.show();
    }

    /**
     * Die main-Methode ist der Einstiegspunkt für die Ausführung der
     * Java-Anwendung.
     * Sie ruft die launch-Methode von JavaFX auf, um die Anwendung zu starten.
     *
     * @param args Kommandozeilenargumente, die beim Start der Anwendung übergeben
     *             werden können.
     */
    public static void main(String[] args) {
        // Starte die JavaFX-Anwendung
        launch();
    }

    /**
     * Initialisiert die Datenbank für das TicTacToe-Spiel. Erstellt eine Tabelle
     * namens "players"
     * mit den erforderlichen Spalten, falls sie noch nicht existiert.
     */
    public void initializeDatabase() {
        try (Database database = new Database("TicTacToe")) {
            // Erstelle eine Liste von Spalten für die "players"-Tabelle
            ArrayList<String> columns = new ArrayList<>();
            columns.add("id INTEGER PRIMARY KEY");
            columns.add("name TEXT");
            columns.add("won INTEGER");
            columns.add("draw INTEGER");
            columns.add("lose INTEGER");

            // Erstelle die "players"-Tabelle in der Datenbank, wenn sie noch nicht
            // existiert
            database.createTable("players", columns);
        } catch (SQLException e) {
            System.out.println("Fehler bei der Initialisierung der Datenbank.");
            e.printStackTrace(); // Handle the exception appropriately in your application
        }
    }

}

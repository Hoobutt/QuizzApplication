package com.example.quizzapplication;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;


public class Application extends javafx.application.Application {
    Stage stage;


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("home-view.fxml"));
        Scene HomeScene = new Scene(fxmlLoader.load(), 600, 400);

        this.stage = stage;
        stage.setTitle("Home");
        stage.setScene(HomeScene);
        stage.show();
        stage.centerOnScreen();
    }

    public static void changeScene(String fxml, Scene currentScene) {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource(fxml));
        Scene newScene = null;
        try {
            newScene = new Scene(fxmlLoader.load(), 600, 400);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) currentScene.getWindow();
        stage.setScene(newScene);
        stage.centerOnScreen();
    }

    public static void largeScene(String fxml, Scene currentScene) {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource(fxml));
        Scene newScene = null;
        try {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            newScene = new Scene(fxmlLoader.load(), screenBounds.getWidth(), screenBounds.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) currentScene.getWindow();
        stage.setScene(newScene);
        stage.setMaximized(true);
        stage.centerOnScreen();
    }
}

package com.climproved;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.*;

import java.io.IOException;

public class Main extends Application {
    static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        Main.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        Scene scene = new Scene(fxmlLoader.load(), size.getWidth() * 0.6, size.getHeight() * 0.6);
        Main.stage.setTitle("CLImproved");
        Main.stage.setScene(scene);
        Main.stage.setResizable(true);
        Main.stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
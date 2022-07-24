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
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        Main.stage.setHeight(size.getHeight() * 0.7);
        Main.stage.setWidth(((size.getHeight() * 0.7) / 9) * 16);

       // Main.stage.getIcons().add();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        Main.stage.setTitle("CLImproved");
        Main.stage.setScene(scene);
        Main.stage.setResizable(true);
        Main.stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
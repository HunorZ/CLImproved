package com.climproved;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        //create main stage
        Main.stage = stage;
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        Main.stage.setHeight(size.getHeight() * 0.7);
        Main.stage.setWidth(((size.getHeight() * 0.7) / 9) * 16);
        Main.stage.getIcons().add(new Image(
                Objects.requireNonNull(this.getClass().getResourceAsStream("CLImproved_newLogo.png"))));

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("application.css")).toExternalForm());
        Main.stage.setTitle("CLImproved");
        Main.stage.setScene(scene);
        Main.stage.setResizable(true);
        Main.stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
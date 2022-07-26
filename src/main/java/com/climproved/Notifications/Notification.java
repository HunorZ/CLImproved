package com.climproved.Notifications;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public abstract class Notification {
    public static Stage owner;

    protected Stage stage = new Stage();
    protected Scene scene;
    protected Label label = new Label();
    protected AnchorPane anchorPane = new AnchorPane();

    protected void setMessage(String message) {
        label.setText(message);
    }
}

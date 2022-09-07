package com.climproved.Notifications;

import com.climproved.Main;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Alert extends Notification {

    Button button = new Button("Ok");

    public Alert(String message) {
        setMessage(message);

        button.setPrefWidth(70);
        button.setOnAction(actionEvent -> stage.close());
        AnchorPane.setRightAnchor(button, 50.0);
        AnchorPane.setBottomAnchor(button, 25.0);

        label.setFont(new Font(15));
        AnchorPane.setLeftAnchor(label, 50.0);
        AnchorPane.setRightAnchor(label, 50.0);
        AnchorPane.setTopAnchor(label, 40.0);

        anchorPane.getChildren().addAll(label, button);

        scene = new Scene(anchorPane);
        stage = new Stage();
        stage.setWidth(400);
        stage.setHeight(message.chars().filter(ch -> ch == '\n').count() * 20 + 140);
        stage.initOwner(owner);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);

    }


    public void fire() {
        stage.showAndWait();
    }
}

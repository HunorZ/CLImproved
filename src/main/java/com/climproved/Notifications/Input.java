package com.climproved.Notifications;

import com.climproved.Main;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Input extends Notification {

    TextField textField;
    Button button;

    public Input(String message) {
        label.setFont(new Font(15));
        AnchorPane.setTopAnchor(label, 20.0);
        AnchorPane.setLeftAnchor(label, 25.0);
        AnchorPane.setRightAnchor(label, 25.0);
        setMessage(message);
        textField = new TextField();
        textField.setFont(new Font(15));
        AnchorPane.setLeftAnchor(textField, 25.0);
        AnchorPane.setRightAnchor(textField, 25.0);
        AnchorPane.setBottomAnchor(textField, 75.0);

        button = new Button("Ok");
        button.setPrefWidth(70);
        AnchorPane.setRightAnchor(button, 50.0);
        AnchorPane.setBottomAnchor(button, 25.0);

        anchorPane.getChildren().addAll(label, textField, button);


        stage = new Stage();
        stage.setWidth(400);
        stage.setHeight(170 + message.chars().filter(ch -> ch == '\n').count() * 20);
        stage.setResizable(false);
        stage.initOwner(owner);
        stage.initStyle(StageStyle.UNDECORATED);

        scene = new Scene(anchorPane);
        stage.setScene(scene);

        button.setOnAction(actionEvent -> stage.close());
        scene.setOnKeyPressed(k -> {
            if (k.getCode().equals(KeyCode.ENTER)) {
                button.fire();
            }
        });
    }

    public String fire() {
        stage.showAndWait();
        return textField.getText();
    }
}

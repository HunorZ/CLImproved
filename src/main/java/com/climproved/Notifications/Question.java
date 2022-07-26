package com.climproved.Notifications;

import com.climproved.Main;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Question extends Notification {

    Button apply = new Button("Yes");
    Button deny = new Button("No");

    boolean answer = false;

    public Question(String message) {
        setMessage(message);
        setMessage(message);

        apply.setPrefWidth(70);
        apply.setOnAction(actionEvent -> {
            answer = true;
            stage.close();
        });
        AnchorPane.setRightAnchor(apply, 140.0);
        AnchorPane.setBottomAnchor(apply, 25.0);

        deny.setPrefWidth(70);
        deny.setOnAction(actionEvent -> stage.close());
        AnchorPane.setRightAnchor(deny, 40.0);
        AnchorPane.setBottomAnchor(deny, 25.0);

        label.setFont(new Font(15));
        AnchorPane.setLeftAnchor(label, 50.0);
        AnchorPane.setRightAnchor(label, 50.0);
        AnchorPane.setTopAnchor(label, 40.0);

        anchorPane.getChildren().addAll(label, apply, deny);

        scene = new Scene(anchorPane);
        stage = new Stage();
        stage.setWidth(400);
        stage.setHeight(message.chars().filter(ch -> ch == '\n').count() * 20 + 140);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initOwner(owner);
        stage.setScene(scene);
    }

    public boolean fire() {
        stage.showAndWait();
        return answer;
    }
}

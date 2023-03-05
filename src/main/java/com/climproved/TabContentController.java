package com.climproved;

import com.climproved.Notifications.Alert;
import com.climproved.Notifications.Input;
import com.climproved.Notifications.Question;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class TabContentController {

    @FXML
    private HBox modeContainer;

    private JSONFileHandler jsonFileHandler;

    @FXML
    public TextArea textArea;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    GridPane commandContainer;

    @FXML
    void initialize() {
        jsonFileHandler = new JSONFileHandler();
        try {
            jsonFileHandler.init(
                    System.getenv("localappdata") + "\\CLImproved\\ciscoFile.json");
        } catch (FileNotFoundException e) {
            if (new Question("There is no dataset available.\nDo you want to download it?").fire()) {
                Controller.updateDataSet();

                try {
                    jsonFileHandler.init(
                            System.getenv("localappdata") + "\\CLImproved\\ciscoFile.json");
                } catch (Exception f) {
                    System.exit(0);
                }
            } else {
                new Alert("There is no dataset available").fire();
                System.exit(0);
            }
        }

        //get modes and display them
        String[] execModes = jsonFileHandler.getModes();
        System.out.println(Arrays.toString(execModes));
        for (int i = 0; i < execModes.length; i++) {
            Button button = new Button(execModes[i]);
            int finalI = i;
            button.setOnAction(actionEvent ->
                    updateCommands(jsonFileHandler.changeMode(finalI)));
            modeContainer.getChildren().add(button);
        }

        scrollPane.setContent(commandContainer);
        updateCommands(jsonFileHandler.changeMode(0));
    }

    void updateCommands(Word[] words) {
        commandContainer.getChildren().clear();
        String content = jsonFileHandler.commandWriter.getContent();
        textArea.clear();
        textArea.appendText(content);


        for (int i = 0; i < words.length; i++) {
            Node n = null;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("wordRow.fxml"));
            try {
                n = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }

            WordRow wordRow = loader.getController();


            final int finalI = i;
            wordRow.button.setOnAction(actionEvent -> {
                if (words[finalI].type == Word.Type.PARAM || words[finalI].type == Word.Type.PARAM_ENTERSUBMODE) {
                    String parameter = new Input(words[finalI].word + "\n" +
                            words[finalI].description).fire();
                    jsonFileHandler.commandWriter.writeWord(parameter);
                }
                updateCommands(jsonFileHandler.getNextCommands(finalI));

            });
            if (words.length == 1) {
                wordRow.button.fire();
                return;
            }

            wordRow.button.setGraphic(new ImageView(Main.center_addButton_image));
            wordRow.button.setId("wordButton");

            wordRow.wordLabel.setText(words[i].word);
            wordRow.descriptionLabel.setText(words[i].description);

            switch (words[i].type) {
                case FINISH, EXITSUBMODE -> {
                    wordRow.wordLabel.setBackground(new Background(new BackgroundFill(
                            Color.rgb(107, 65, 65), CornerRadii.EMPTY, Insets.EMPTY)));
                    wordRow.descriptionLabel.setBackground(new Background(new BackgroundFill(
                            Color.rgb(107, 65, 65), CornerRadii.EMPTY, Insets.EMPTY)));
                }
                case COMMAND_ENTERSUBMODE, PARAM_ENTERSUBMODE -> {
                    wordRow.wordLabel.setBackground(new Background(new BackgroundFill(
                            Color.rgb(67, 103, 58), CornerRadii.EMPTY, Insets.EMPTY)));
                    wordRow.descriptionLabel.setBackground(new Background(new BackgroundFill(
                            Color.rgb(67, 103, 58), CornerRadii.EMPTY, Insets.EMPTY)));
                }
            }
            switch (words[i].type) {
                case FINISH, EXITSUBMODE -> wordRow.button.setStyle("-fx-background-color: #6B4141");

                case COMMAND_ENTERSUBMODE, PARAM_ENTERSUBMODE -> wordRow.button.setStyle("-fx-background-color: #43673A");
            }

            commandContainer.add(n, 0, i);
        }
    }
}

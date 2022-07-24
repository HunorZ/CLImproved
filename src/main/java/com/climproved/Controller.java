package com.climproved;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.ArrayList;

public class Controller {

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab addTab;

    ArrayList<JSONFileHandler> jsonFileHandlerArrayList = new ArrayList<>();

    ArrayList<AnchorPane> outerAnchorPanes = new ArrayList<>();
    ArrayList<VBox> vBoxes = new ArrayList<>();
    ArrayList<AnchorPane> innerAnchorPanes = new ArrayList<>();
    ArrayList<HBox> hBoxes = new ArrayList<>();
    ArrayList<GridPane> gridPanes = new ArrayList<>();
    ArrayList<ColumnConstraints> firstColumnConstrains = new ArrayList<>();
    ArrayList<ColumnConstraints> secondColumnConstrains = new ArrayList<>();
    ArrayList<RowConstraints> rowConstraints = new ArrayList<>();
    ArrayList<TextArea> textAreas = new ArrayList<>();
    ArrayList<ScrollPane> scrollPanes = new ArrayList<>();
    ArrayList<GridPane> commandContainers = new ArrayList<>();

    int currentTabIndex = 0;

    String filePathAtLastSave;
    String contentAtLastSave;

    public void initialize() {
        //add first tab at init
        createNewTab();

        //addTab function
        addTab.setOnSelectionChanged(e -> {
            createNewTab();
        });
    }

    public void createNewTab() {
        //new Tab
        Tab tab = new Tab("tab");


        outerAnchorPanes.add(new AnchorPane());
        outerAnchorPanes.get(outerAnchorPanes.size() - 1).maxWidth(1.7976931348623157E308);
        outerAnchorPanes.get(outerAnchorPanes.size() - 1).maxHeight(1.7976931348623157E308);
        outerAnchorPanes.get(outerAnchorPanes.size() - 1).minWidth(0.0);
        outerAnchorPanes.get(outerAnchorPanes.size() - 1).minHeight(0.0);

        vBoxes.add(new VBox());
        vBoxes.get(vBoxes.size() - 1).setAlignment(Pos.TOP_CENTER);
        AnchorPane.setBottomAnchor(vBoxes.get(vBoxes.size() - 1), 0.0);
        AnchorPane.setLeftAnchor(vBoxes.get(vBoxes.size() - 1), 0.0);
        AnchorPane.setRightAnchor(vBoxes.get(vBoxes.size() - 1), 0.0);
        AnchorPane.setTopAnchor(vBoxes.get(vBoxes.size() - 1), 0.0);

        innerAnchorPanes.add(new AnchorPane());
        innerAnchorPanes.get(innerAnchorPanes.size() - 1).setPrefHeight(50.0);

        hBoxes.add(new HBox());
        hBoxes.get(hBoxes.size() - 1).setAlignment(Pos.CENTER_LEFT);
        hBoxes.get(hBoxes.size() - 1).setFillHeight(false);
        hBoxes.get(hBoxes.size() - 1).prefHeight(46.0);
        hBoxes.get(hBoxes.size() - 1).setSpacing(30.0);
        hBoxes.get(hBoxes.size() - 1).setPadding(new Insets(0, 0, 0, 100));

        AnchorPane.setBottomAnchor(hBoxes.get(hBoxes.size() - 1), 0.0);
        AnchorPane.setLeftAnchor(hBoxes.get(hBoxes.size() - 1), 0.0);
        AnchorPane.setRightAnchor(hBoxes.get(hBoxes.size() - 1), 0.0);
        AnchorPane.setTopAnchor(hBoxes.get(hBoxes.size() - 1), 0.0);

        gridPanes.add(new GridPane());
        gridPanes.get(gridPanes.size() - 1).setAlignment(Pos.CENTER);
        gridPanes.get(gridPanes.size() - 1).setGridLinesVisible(true);
        VBox.setVgrow(gridPanes.get(gridPanes.size() - 1), Priority.ALWAYS);

        firstColumnConstrains.add(new ColumnConstraints());
        firstColumnConstrains.get(firstColumnConstrains.size() - 1).setHgrow(Priority.SOMETIMES);
        firstColumnConstrains.get(firstColumnConstrains.size() - 1).setMinWidth(10);
        firstColumnConstrains.get(firstColumnConstrains.size() - 1).setPercentWidth(70.0);
        firstColumnConstrains.get(firstColumnConstrains.size() - 1).setPrefWidth(100.0);

        secondColumnConstrains.add(new ColumnConstraints());
        secondColumnConstrains.get(secondColumnConstrains.size() - 1).setHgrow(Priority.SOMETIMES);
        secondColumnConstrains.get(secondColumnConstrains.size() - 1).setMinWidth(10);
        secondColumnConstrains.get(secondColumnConstrains.size() - 1).setPercentWidth(30.0);
        secondColumnConstrains.get(secondColumnConstrains.size() - 1).setPrefWidth(100.0);

        rowConstraints.add(new RowConstraints());
        rowConstraints.get(rowConstraints.size() - 1).setMinHeight(10.0);
        rowConstraints.get(rowConstraints.size() - 1).setPrefHeight(30.0);
        rowConstraints.get(rowConstraints.size() - 1).setVgrow(Priority.SOMETIMES);
        gridPanes.get(gridPanes.size() - 1).getColumnConstraints().addAll(firstColumnConstrains.get(firstColumnConstrains.size() - 1), secondColumnConstrains.get(secondColumnConstrains.size() - 1));
        gridPanes.get(gridPanes.size() - 1).getRowConstraints().add(rowConstraints.get(rowConstraints.size() - 1));

        textAreas.add(new TextArea());
        scrollPanes.add(new ScrollPane());
        commandContainers.add(new GridPane());

        scrollPanes.get(scrollPanes.size() - 1).setContent(commandContainers.get(commandContainers.size() - 1));
        gridPanes.get(gridPanes.size() - 1).add(scrollPanes.get(scrollPanes.size() - 1), 0, 0);
        gridPanes.get(gridPanes.size() - 1).add(textAreas.get(textAreas.size() - 1), 1, 0);
        innerAnchorPanes.get(innerAnchorPanes.size() - 1).getChildren().add(hBoxes.get(hBoxes.size() - 1));
        vBoxes.get(vBoxes.size() - 1).getChildren().addAll(innerAnchorPanes.get(innerAnchorPanes.size() - 1), gridPanes.get(gridPanes.size() - 1));
        outerAnchorPanes.get(outerAnchorPanes.size() - 1).getChildren().add(vBoxes.get(vBoxes.size() - 1));
        tab.setContent(outerAnchorPanes.get(outerAnchorPanes.size() - 1));

        //add tab to tabPane
        tabPane.getTabs().add(tabPane.getTabs().size() - 1, tab);
        //select newly created tab
        tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);

        //as new tab is automatically selected at creation the currentTabIndex has to be changed as well
        currentTabIndex = tabPane.getTabs().size() - 2;

        //set id of tab to its index in tabPane
        tab.setId(currentTabIndex + "");
        //create new jsonFileHandler instance for new tab
        jsonFileHandlerArrayList.add(new JSONFileHandler());
        jsonFileHandlerArrayList.get(currentTabIndex).init("ciscoFile.json");

        String[] execModes = jsonFileHandlerArrayList.get(0).getModes();
        for (int i = 0; i < execModes.length; i++) {
            Button button = new Button(execModes[i]);
            int finalI = i;
            button.setOnAction(actionEvent -> {
                jsonFileHandlerArrayList.get(currentTabIndex).changeMode(finalI);
                updateCommands();
            });
            hBoxes.get(currentTabIndex).getChildren().add(button);
        }
        scrollPanes.get(scrollPanes.size() - 1).setContent(commandContainers.get(commandContainers.size() - 1));
        updateCommands();
        tab.setOnClosed(ex -> {
            System.out.println(tab.getId());
            for (int i = Integer.parseInt(tab.getId()); i < tabPane.getTabs().size(); i++) {
                tabPane.getTabs().get(i).setId("" + i);
            }

            int tabID = Integer.parseInt(tab.getId());
            //remove jsonFileHandler instance as tab is closed and object is no longer needed
            jsonFileHandlerArrayList.remove(tabID);

            outerAnchorPanes.remove(tabID);
            vBoxes.remove(tabID);
            innerAnchorPanes.remove(tabID);
            hBoxes.remove(tabID);
            gridPanes.remove(tabID);
            firstColumnConstrains.remove(tabID);
            secondColumnConstrains.remove(tabID);
            rowConstraints.remove(tabID);
            textAreas.remove(tabID);
            scrollPanes.remove(tabID);
            commandContainers.remove(tabID);
        });

        tab.setOnSelectionChanged(ex -> {
            if (tab.isSelected()) {
                //if the tab is selected, change currentTabIndex to index of the selected tab
                currentTabIndex = tabPane.getSelectionModel().getSelectedIndex();
                System.out.println(currentTabIndex);
            }
        });
    }

    public void updateCommands() {
        commandContainers.get(currentTabIndex).getChildren().clear();
        textAreas.get(currentTabIndex).setText(jsonFileHandlerArrayList.get(currentTabIndex).commandWriter.content);
        String[] words = jsonFileHandlerArrayList.get(currentTabIndex).getWords();
        String[] descriptions = jsonFileHandlerArrayList.get(currentTabIndex).getDescriptions();
        commandContainers.get(currentTabIndex).setVgap(5);
        commandContainers.get(currentTabIndex).setHgap(50);
        for (int i = 0; i < words.length; i++) {
            Button button = new Button();
            int finalI = i;
            button.setOnAction(actionEvent -> {
                if (jsonFileHandlerArrayList.get(currentTabIndex).isParam(finalI)) {
                    String parameter = PopUp.readLine(jsonFileHandlerArrayList.get(currentTabIndex).getWords()[finalI]);
                    jsonFileHandlerArrayList.get(currentTabIndex).commandWriter.writeWord(parameter);
                }
                jsonFileHandlerArrayList.get(currentTabIndex).loadNextWords(finalI);

                updateCommands();

            });
            if (jsonFileHandlerArrayList.get(currentTabIndex).getWords().length == 1) {
                button.fire();
                break;
            }

            commandContainers.get(currentTabIndex).add(button, 0, i);
            commandContainers.get(currentTabIndex).add(new Label(words[i]), 1, i);
            commandContainers.get(currentTabIndex).add(new Label(descriptions[i]), 2, i);
        }
    }

    @FXML
    private void saveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.setInitialFileName("Script_File");

        //Set to user directory or go to default if cannot access
        String userDirectoryString = System.getProperty("user.home") + "/Desktop";
        File userDirectory = new File(userDirectoryString);
        if (!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        fileChooser.setInitialDirectory(userDirectory);

        //Opening a dialog box
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
        try {
            File selectedFile = fileChooser.showSaveDialog(Main.stage);
            filePathAtLastSave = selectedFile.getAbsolutePath();
            BufferedWriter output = Files.newBufferedWriter(selectedFile.toPath(), StandardCharsets.UTF_8);
            contentAtLastSave = textAreas.get(currentTabIndex).getText();
            output.write(textAreas.get(currentTabIndex).getText());
            output.flush();
        } catch (Exception e) {
            System.out.println("Filesave aborted!");
        }
    }

    @FXML
    private void save() {
        if (!filePathAtLastSave.equals("")) {
            try {
                BufferedWriter output = Files.newBufferedWriter(Paths.get(filePathAtLastSave), StandardCharsets.UTF_8);
                contentAtLastSave = textAreas.get(currentTabIndex).getText();
                output.write(textAreas.get(currentTabIndex).getText());
                output.flush();
            } catch (Exception e) {
                System.out.println("Filesave aborted!");
            }
        } else {
            saveAs();
        }
    }
}
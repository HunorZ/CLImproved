package com.climproved;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    boolean aboutPageOpen = false;

    String filePathAtLastSave = "";
    String contentAtLastSave;

    Image center_addButton_image;
    Image header_logo_image;

    {
        try {
            center_addButton_image = new Image(new FileInputStream("assets\\add_button.png"));
            header_logo_image = new Image(new FileInputStream("assets\\CLImproved_newLogo.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        //add first tab at init
        createNewTab();
        //addTab function
        addTab.setOnSelectionChanged(e -> createNewTab());

        Main.stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.S,
                    KeyCombination.CONTROL_DOWN);

            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    save();
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });

        Main.stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.T,
                    KeyCombination.CONTROL_DOWN);

            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    createNewTab();
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        Main.stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.W,
                    KeyCombination.CONTROL_DOWN);

            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    removeTab(currentTabIndex);
                    tabPane.getTabs().remove(currentTabIndex);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
    }

    public void updateCommands() {
        commandContainers.get(currentTabIndex).getChildren().clear();
        String content = jsonFileHandlerArrayList.get(currentTabIndex).commandWriter.getContent();
        textAreas.get(currentTabIndex).clear();
        textAreas.get(currentTabIndex).appendText(content);
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
            button.setGraphic(new ImageView(center_addButton_image));
            button.setId("button_commands");
            commandContainers.get(currentTabIndex).add(button, 0, i);
            commandContainers.get(currentTabIndex).add(new Label(words[i]), 1, i);
            commandContainers.get(currentTabIndex).add(new Label(descriptions[i]), 2, i);
        }
    }


    public void createNewTab() {
        //new Tab
        Tab tab = new Tab("tab");
        tab.setText("Untitled                      ");


        //create new jsonFileHandler instance for new tab
        jsonFileHandlerArrayList.add(new JSONFileHandler());
        jsonFileHandlerArrayList.get(jsonFileHandlerArrayList.size() - 1).init("ciscoFile.json");

        int index = jsonFileHandlerArrayList.size() - 1;

        outerAnchorPanes.add(new AnchorPane());
        outerAnchorPanes.get(index).maxWidth(1.7976931348623157E308);
        outerAnchorPanes.get(index).maxHeight(1.7976931348623157E308);
        outerAnchorPanes.get(index).minWidth(0.0);
        outerAnchorPanes.get(index).minHeight(0.0);

        vBoxes.add(new VBox());
        vBoxes.get(index).setAlignment(Pos.TOP_CENTER);
        AnchorPane.setBottomAnchor(vBoxes.get(index), 0.0);
        AnchorPane.setLeftAnchor(vBoxes.get(index), 0.0);
        AnchorPane.setRightAnchor(vBoxes.get(index), 0.0);
        AnchorPane.setTopAnchor(vBoxes.get(index), 0.0);

        innerAnchorPanes.add(new AnchorPane());
        innerAnchorPanes.get(index).setPrefHeight(50.0);

        hBoxes.add(new HBox());
        hBoxes.get(index).setAlignment(Pos.CENTER_LEFT);
        hBoxes.get(index).setFillHeight(false);
        hBoxes.get(index).prefHeight(46.0);
        hBoxes.get(index).setSpacing(30.0);
        hBoxes.get(index).setPadding(new Insets(0, 0, 0, 100));

        AnchorPane.setBottomAnchor(hBoxes.get(index), 0.0);
        AnchorPane.setLeftAnchor(hBoxes.get(index), 0.0);
        AnchorPane.setRightAnchor(hBoxes.get(index), 0.0);
        AnchorPane.setTopAnchor(hBoxes.get(index), 0.0);

        gridPanes.add(new GridPane());
        gridPanes.get(index).setAlignment(Pos.CENTER);
        VBox.setVgrow(gridPanes.get(index), Priority.ALWAYS);

        firstColumnConstrains.add(new ColumnConstraints());
        firstColumnConstrains.get(index).setHgrow(Priority.SOMETIMES);
        firstColumnConstrains.get(index).setMinWidth(10);
        firstColumnConstrains.get(index).setPercentWidth(60.0);
        firstColumnConstrains.get(index).setPrefWidth(100.0);

        secondColumnConstrains.add(new ColumnConstraints());
        secondColumnConstrains.get(index).setHgrow(Priority.SOMETIMES);
        secondColumnConstrains.get(index).setMinWidth(10);
        secondColumnConstrains.get(index).setPercentWidth(40.0);
        secondColumnConstrains.get(index).setPrefWidth(100.0);

        rowConstraints.add(new RowConstraints());
        rowConstraints.get(index).setMinHeight(10.0);
        rowConstraints.get(index).setPrefHeight(30.0);
        rowConstraints.get(index).setVgrow(Priority.SOMETIMES);
        gridPanes.get(index).getColumnConstraints().addAll(firstColumnConstrains.get(index), secondColumnConstrains.get(index));
        gridPanes.get(index).getRowConstraints().add(rowConstraints.get(index));
        gridPanes.get(index).setGridLinesVisible(true);
        gridPanes.get(index).setId("gridPane");
        gridPanes.get(index).setFocusTraversable(false);

        textAreas.add(new TextArea());
        textAreas.get(index).setId("textArea");
        textAreas.get(index).setFocusTraversable(false);
        textAreas.get(index).textProperty().addListener((observableValue, s, t1) -> jsonFileHandlerArrayList.get(currentTabIndex).commandWriter.setContent(textAreas.get(currentTabIndex).getText()));

        scrollPanes.add(new ScrollPane());
        scrollPanes.get(index).setId("darkMode_center_scrollPane");
        scrollPanes.get(index).setFocusTraversable(false);

        commandContainers.add(new GridPane());

        scrollPanes.get(index).setContent(commandContainers.get(index));
        gridPanes.get(index).add(scrollPanes.get(index), 0, 0);
        gridPanes.get(index).add(textAreas.get(index), 1, 0);
        innerAnchorPanes.get(index).getChildren().add(hBoxes.get(index));
        vBoxes.get(index).getChildren().addAll(innerAnchorPanes.get(index), gridPanes.get(index));
        outerAnchorPanes.get(index).getChildren().add(vBoxes.get(index));
        tab.setContent(outerAnchorPanes.get(index));

        //add tab to tabPane
        tabPane.getTabs().add(index, tab);
        //select newly created tab
        tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);

        //as new tab is automatically selected at creation the currentTabIndex has to be changed as well
        currentTabIndex = tabPane.getTabs().size() - 2;

        //set id of tab to its index in tabPane
        tab.setId(currentTabIndex + "");

        String[] execModes = jsonFileHandlerArrayList.get(0).getModes();
        for (int i = 0; i < execModes.length; i++) {
            Button button = new Button(execModes[i]);
            button.setId("darkMode_header_modeButton");
            int finalI = i;
            button.setOnAction(actionEvent -> {
                jsonFileHandlerArrayList.get(currentTabIndex).changeMode(finalI);
                updateCommands();
            });
            hBoxes.get(currentTabIndex).getChildren().add(button);
        }
        scrollPanes.get(index).setContent(commandContainers.get(index));
        updateCommands();

        tab.setOnCloseRequest(ex -> removeTab(Integer.parseInt(tab.getId())));

        tab.setOnSelectionChanged(ex -> {
            if (tab.isSelected()) {
                //if the tab is selected, change currentTabIndex to index of the selected tab
                currentTabIndex = tabPane.getSelectionModel().getSelectedIndex();
            }
        });
    }

    public void removeTab(int tabID) {
        for (int i = tabID; i < tabPane.getTabs().size() - 1; i++) {
            tabPane.getTabs().get(i).setId((Integer.parseInt(tabPane.getTabs().get(i).getId()) - 1) + "");
        }

        //remove jsonFileHandler instance as tab is closed and object is no longer needed
        jsonFileHandlerArrayList.remove(tabID);

        //remove all JavaFx components, as they are no longer displayed
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
        if (tabPane.getTabs().size() == 1) {
            createNewTab();
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
            output.write(shorten(textAreas.get(currentTabIndex).getText()));
            output.flush();

            String fileName = selectedFile.getName().split("\\.")[0];
            tabPane.getTabs().get(currentTabIndex).setText(fileName + " ".repeat(30 - fileName.length()));
        } catch (Exception e) {
            //Filesave aborted!
        }
    }

    @FXML
    private void save() {
        if (!filePathAtLastSave.equals("")) {
            try {
                BufferedWriter output = Files.newBufferedWriter(Paths.get(filePathAtLastSave), StandardCharsets.UTF_8);
                contentAtLastSave = textAreas.get(currentTabIndex).getText();
                output.write(shorten(textAreas.get(currentTabIndex).getText()));
                output.flush();
            } catch (Exception e) {
                //Filesave aborted!
            }
        } else {
            saveAs();
        }
    }

    @FXML
    private void openAboutWindow() {
        if (!aboutPageOpen) {
            aboutPageOpen = true;

            Stage aboutStage = new Stage();
            BorderPane aboutPane = new BorderPane();
            Scene aboutStage_scene = new Scene(aboutPane);

            ImageView aboutPagelogoImage = new ImageView(header_logo_image);
            HBox picuteHBox = new HBox(aboutPagelogoImage);
            Label infoLabel = new Label("CLImproved for Windows\n" +
                    "Version 1.3.1\n" +
                    "GUI Version 1.0\n\n" +
                    "Made by\n" +
                    "-Felix Payer\n" +
                    "-Hunor Zakarias\n\n" +
                    "OS: " + System.getProperty("os.name") + "\n" +
                    "Architecture: " + System.getProperty("os.arch") + "\n" +
                    "Java Version: " + System.getProperty("java.version"));

            infoLabel.setFont(new Font("System Regular", 15));

            picuteHBox.setPadding(new Insets(50, 10, 10, 20));

            aboutPagelogoImage.setFitWidth(80);
            aboutPagelogoImage.setFitHeight(80);

            aboutPane.setPrefWidth(Double.MAX_VALUE);
            aboutPane.setPrefHeight(Double.MAX_VALUE);
            aboutPane.setRight(infoLabel);
            aboutPane.setLeft(picuteHBox);
            aboutPane.setPadding(new Insets(10, 70, 10, 10));

            aboutStage.setResizable(false);
            aboutStage.setWidth(330);
            aboutStage.setHeight(300);

            aboutStage.setTitle("About");
            aboutStage.getIcons().add(header_logo_image);
            aboutStage.setScene(aboutStage_scene);
            aboutStage.show();

            aboutStage.setOnCloseRequest(e -> aboutPageOpen = false);
        }
    }

    private String shorten(String s) {
        while ("\n".equals(s.charAt(0) + "") || "\t".equals(s.charAt(0) + "") || " ".equals(s.charAt(0) + "")) {
            s = s.substring(1);
        }

        while ("\n".equals(s.charAt(s.length() - 1) + "") || "\t".equals(s.charAt(s.length() - 1) + "") || " ".equals(s.charAt(s.length() - 1) + "")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
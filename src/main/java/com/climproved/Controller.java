package com.climproved;

import com.climproved.Notifications.Alert;
import com.climproved.Notifications.Input;
import com.climproved.Notifications.Notification;
import com.climproved.Notifications.Question;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Controller {

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab addTab;

    ArrayList<JSONFileHandler> jsonFileHandlerArrayList = new ArrayList<>();

    ArrayList<HBox> hBoxes = new ArrayList<>();
    ArrayList<TextArea> textAreas = new ArrayList<>();
    ArrayList<GridPane> commandContainers = new ArrayList<>();
    ArrayList<String> filePathsAtLastSave = new ArrayList<>();

    int currentTabIndex = 0;

    String contentAtLastSave;

    Stage aboutStage = new Stage();

    Image center_addButton_image;
    Image header_logo_image;

    {
        center_addButton_image = new Image(
                Objects.requireNonNull(this.getClass().getResourceAsStream("add_button.png")));
        header_logo_image = new Image(
                Objects.requireNonNull(this.getClass().getResourceAsStream("CLImproved_newLogo.png")));
    }

    public void initialize() {


        Notification.owner = Main.stage;
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
        Main.stage.addEventFilter(KeyEvent.KEY_RELEASED
                , new EventHandler<>() {
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

    @FXML
    public void updateDataSetFromMenu() {
        updateDataSet();
        createNewTab();
    }

    public void updateDataSet() {
        try {
            String link =
                    "https://raw.githubusercontent.com/HunorZ/CLImproved/main/ciscoFile.json";
            String fileName = "ciscoFile.json";
            URL url = new URL(link);
            try {
                URLConnection connection = new URL(link).openConnection();
                connection.connect();
            } catch (IOException e) {
                new Alert("Internet is not connected\nDataset could not be updated!").fire();
                return;
            }
            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
            Map<String, List<String>> header = https.getHeaderFields();
            while (isRedirected(header)) {
                link = header.get("Location").get(0);
                url = new URL(link);
                https = (HttpsURLConnection) url.openConnection();
                header = https.getHeaderFields();
            }
            InputStream input = https.getInputStream();
            byte[] buffer = new byte[4096];
            int n;

            File path = new File((System.getenv("localappdata")) + "\\CLImproved");
            if (!path.exists()) {
                if (!path.mkdirs()) {
                    new Alert("Could not create path!").fire();
                    return;
                }
            }
            OutputStream output = new FileOutputStream(path + "\\" + fileName);
            while ((n = input.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }
            output.close();
            new Alert("Dataset successfully updated!\nOnly new tabs have the new dataset applied.").fire();
        } catch (Exception e) {
            new Alert("Dataset could not be updated!\nPlease make sure to have a stable internet connection.").fire();
        }


    }

    private boolean isRedirected(Map<String, List<String>> header) {
        for (String hv : header.get(null)) {
            if (hv.contains(" 301 ")
                    || hv.contains(" 302 ")) return true;
        }
        return false;
    }

    public void updateCommands(Word[] words) {
        commandContainers.get(currentTabIndex).getChildren().clear();
        String content = jsonFileHandlerArrayList.get(currentTabIndex).commandWriter.getContent();
        textAreas.get(currentTabIndex).clear();
        textAreas.get(currentTabIndex).appendText(content);


        for (int i = 0; i < words.length; i++) {
            Button button = new Button();

            final int finalI = i;
            button.setOnAction(actionEvent -> {
                if (words[finalI].type == Word.Type.PARAM || words[finalI].type == Word.Type.PARAM_ENTERSUBMODE) {
                    String parameter = new Input(words[finalI].word + "\n" +
                            words[finalI].description).fire();
                    jsonFileHandlerArrayList.get(currentTabIndex).commandWriter.writeWord(parameter);
                }
                updateCommands(jsonFileHandlerArrayList.get(currentTabIndex).getNextCommands(finalI));

            });
            if (words.length == 1) {
                button.fire();
                return;
            }

            button.setGraphic(new ImageView(center_addButton_image));
            button.setId("wordButton");

            Label[] labels = {new Label(words[i].word), new Label(words[i].description)};


            for (Label label : labels) {
                label.setId("wordLabel");
                AnchorPane.setLeftAnchor(label, 0.0);
                AnchorPane.setTopAnchor(label, 0.0);
                AnchorPane.setRightAnchor(label, 0.0);
                AnchorPane.setBottomAnchor(label, 0.0);
                switch (words[i].type) {
                    case FINISH, EXITSUBMODE -> label.setBackground(new Background(new BackgroundFill(
                            Color.rgb(107, 65, 65), CornerRadii.EMPTY, Insets.EMPTY)));

                    case COMMAND_ENTERSUBMODE, PARAM_ENTERSUBMODE -> label.setBackground(new Background(new BackgroundFill(
                            Color.rgb(67, 103, 58), CornerRadii.EMPTY, Insets.EMPTY)));
                }
            }
            switch (words[i].type) {
                case FINISH, EXITSUBMODE -> button.setStyle("-fx-background-color: #6B4141");

                case COMMAND_ENTERSUBMODE, PARAM_ENTERSUBMODE -> button.setStyle("-fx-background-color: #43673A");
            }


            commandContainers.get(currentTabIndex).add(button, 0, i);
            commandContainers.get(currentTabIndex).add(new AnchorPane(labels[0]), 1, i);
            commandContainers.get(currentTabIndex).add(new AnchorPane(labels[1]), 2, i);
        }
    }

    public void createNewTab() {
        //new Tab
        Tab tab = new Tab("tab");
        tab.setText("Untitled                      ");

        //create new jsonFileHandler instance for new tab
        jsonFileHandlerArrayList.add(new JSONFileHandler());
        try {
            jsonFileHandlerArrayList.get(jsonFileHandlerArrayList.size() - 1).init(
                    System.getenv("localappdata") + "\\CLImproved\\ciscoFile.json");
        } catch (FileNotFoundException e) {
            if (new Question("There is no dataset available.\nDo you want to download it?").fire()) {
                updateDataSet();

                try {
                    jsonFileHandlerArrayList.get(jsonFileHandlerArrayList.size() - 1).init(
                            System.getenv("localappdata") + "\\CLImproved\\ciscoFile.json");
                } catch (Exception f) {
                    System.exit(0);
                }
            } else {
                new Alert("There is no dataset available").fire();
                System.exit(0);
            }
        }
        int index = jsonFileHandlerArrayList.size() - 1;


        AnchorPane anchorPane = new AnchorPane();

        VBox vBox = new VBox();
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        AnchorPane.setLeftAnchor(vBox, 0.0);

        hBoxes.add(new HBox());
        hBoxes.get(index).setId("modeContainer");

        GridPane gridPane = new GridPane();

        gridPane.setId("gridPane");

        ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(60);

        ColumnConstraints right = new ColumnConstraints();
        right.setPercentWidth(40);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.SOMETIMES);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("scrollPane");

        commandContainers.add(new GridPane());
        commandContainers.get(index).setId("wordsGrid");

        textAreas.add(new TextArea());
        textAreas.get(index).setId("textArea");
        textAreas.get(index).textProperty().addListener(
                (observableValue, s, t1) -> jsonFileHandlerArrayList.get(currentTabIndex).commandWriter
                        .setContent(textAreas.get(currentTabIndex).getText()));

        gridPane.getColumnConstraints().addAll(left, right);
        gridPane.getRowConstraints().add(rowConstraints);

        //labelAnchor.getChildren().add(label);
        scrollPane.setContent(commandContainers.get(index));
        gridPane.add(scrollPane, 0, 0);
        gridPane.add(textAreas.get(index), 1, 0);
        vBox.getChildren().addAll(hBoxes.get(index), gridPane);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        anchorPane.getChildren().add(vBox);
        tab.setContent(anchorPane);

        filePathsAtLastSave.add("");

        //add tab to tabPane
        tabPane.getTabs().add(index, tab);
        //select newly created tab
        tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);

        //as new tab is automatically selected at creation the currentTabIndex has to be changed as well
        currentTabIndex = tabPane.getTabs().size() - 2;

        //set id of tab to its index in tabPane
        tab.setId(currentTabIndex + "");

        //get modes and display them
        String[] execModes = jsonFileHandlerArrayList.get(index).getModes();
        for (int i = 0; i < execModes.length; i++) {
            Button button = new Button(execModes[i]);
            int finalI = i;
            button.setOnAction(actionEvent ->
                    updateCommands(jsonFileHandlerArrayList.get(currentTabIndex).changeMode(finalI)));
            hBoxes.get(currentTabIndex).getChildren().add(button);
        }
        updateCommands(jsonFileHandlerArrayList.get(currentTabIndex).changeMode(0));

        tab.setOnCloseRequest(ex -> removeTab(Integer.parseInt(tab.getId())));

        //if a tab is selected, change currentTabIndex to the index of the selected tab
        tab.setOnSelectionChanged(ex -> {
            if (tab.isSelected()) {

                currentTabIndex = tabPane.getSelectionModel().getSelectedIndex();
            }
        });
    }

    private void removeTab(int tabID) {
        String s = "";
        try {
            s = new String(Files.readAllBytes(Path.of(filePathsAtLastSave.get(tabID))));
        } catch (IOException ignored) {
        }

        if (!s.equals(textAreas.get(tabID).getText())) {
            if (new Question("There are unsaved changes left!\n" +
                    "Do you want to save them?").fire()) {
                save();
            }
        }

        for (int i = tabID; i < tabPane.getTabs().size() - 1; i++)
            tabPane.getTabs().get(i).setId((Integer.parseInt(tabPane.getTabs().get(i).getId()) - 1) + "");


        //remove jsonFileHandler instance as tab is closed and object is no longer needed
        jsonFileHandlerArrayList.remove(tabID);

        //remove all JavaFx components, as they are no longer displayed
        hBoxes.remove(tabID);
        textAreas.remove(tabID);
        commandContainers.remove(tabID);
        filePathsAtLastSave.remove(tabID);

        if (tabPane.getTabs().size() == 1) createNewTab();
    }


    @FXML
    private void saveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.setInitialFileName("Script_File");

        //Set to user directory or go to default if no accessible
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
            filePathsAtLastSave.set(currentTabIndex, selectedFile.getAbsolutePath());
            BufferedWriter output = Files.newBufferedWriter(selectedFile.toPath(), StandardCharsets.UTF_8);
            contentAtLastSave = textAreas.get(currentTabIndex).getText();
            output.write(textAreas.get(currentTabIndex).getText().trim());
            output.close();

            String fileName = selectedFile.getName().split("\\.")[0];
            tabPane.getTabs().get(currentTabIndex).setText(fileName + " ".repeat(30 - fileName.length()));
        } catch (Exception ignored) {
            //Filesave aborted!
        }
    }

    @FXML
    private void save() {
        if (!filePathsAtLastSave.get(currentTabIndex).equals("")) {
            try {
                BufferedWriter output = Files.newBufferedWriter(Paths.get(filePathsAtLastSave.get(currentTabIndex)), StandardCharsets.UTF_8);
                contentAtLastSave = textAreas.get(currentTabIndex).getText();
                output.write(textAreas.get(currentTabIndex).getText().trim());
                output.close();
            } catch (Exception ignored) {
                //Filesave aborted!
            }
        } else {
            saveAs();
        }
    }

    @FXML
    private void openAboutWindow() {
        if (aboutStage.isShowing()) return;

        BorderPane aboutPane = new BorderPane();
        Scene aboutStage_scene = new Scene(aboutPane);

        ImageView aboutPageLogoImage = new ImageView(header_logo_image);
        HBox pictureHBox = new HBox(aboutPageLogoImage);
        Label infoLabel = new Label("CLImproved for Windows\n" +
                "Version 1.4.1\n" +
                "GUI Version 2.0\n\n" +
                "Made by\n" +
                "-Hunor Zakarias\n" +
                "(-Felix Payer)\n\n" +
                "OS: " + System.getProperty("os.name") + "\n" +
                "Architecture: " + System.getProperty("os.arch") + "\n" +
                "Java Version: " + System.getProperty("java.version"));

        infoLabel.setFont(new Font("System Regular", 15));

        pictureHBox.setPadding(new Insets(50, 10, 10, 20));

        aboutPageLogoImage.setFitWidth(80);
        aboutPageLogoImage.setFitHeight(80);

        aboutPane.setPrefWidth(Double.MAX_VALUE);
        aboutPane.setPrefHeight(Double.MAX_VALUE);
        aboutPane.setRight(infoLabel);
        aboutPane.setLeft(pictureHBox);
        aboutPane.setPadding(new Insets(10, 70, 10, 10));

        aboutStage.setResizable(false);
        aboutStage.setWidth(330);
        aboutStage.setHeight(300);
        aboutStage.initOwner(Main.stage);
        aboutStage.setTitle("About");
        aboutStage.getIcons().add(header_logo_image);
        aboutStage.setScene(aboutStage_scene);
        aboutStage.show();
    }
}
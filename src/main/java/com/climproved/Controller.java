package com.climproved;

import com.climproved.Notifications.Alert;
import com.climproved.Notifications.Input;
import com.climproved.Notifications.Notification;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Controller {

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab addTab;

    ArrayList<JSONFileHandler> jsonFileHandlerArrayList = new ArrayList<>();

    ArrayList<HBox> hBoxes = new ArrayList<>();
    ArrayList<TextArea> textAreas = new ArrayList<>();
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

        Main.stage.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<>() {
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
                new com.climproved.Notifications.Alert("Internet is not connected\nDataset could not be updated!").fire();
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
            OutputStream output = new FileOutputStream(fileName);
            while ((n = input.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }
            output.close();
            createNewTab();
            new com.climproved.Notifications.Alert("Dataset successfully updated!\nOnly new tabs have the new dataset applied.").fire();
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

    public void updateCommands() {
        commandContainers.get(currentTabIndex).getChildren().clear();
        String content = jsonFileHandlerArrayList.get(currentTabIndex).commandWriter.getContent();
        textAreas.get(currentTabIndex).clear();
        textAreas.get(currentTabIndex).appendText(content);

        String[] words = jsonFileHandlerArrayList.get(currentTabIndex).getWords();
        String[] descriptions = jsonFileHandlerArrayList.get(currentTabIndex).getDescriptions();

        for (int i = 0; i < words.length; i++) {
            Button button = new Button();
            //variable used in lambda expression must be final or effectively final
            final int finalI = i;


            button.setOnAction(actionEvent -> {
                if (jsonFileHandlerArrayList.get(currentTabIndex).isParam(finalI)) {
                    String parameter = new Input(jsonFileHandlerArrayList.get(currentTabIndex).getWords()[finalI] + "\n" +
                            jsonFileHandlerArrayList.get(currentTabIndex).getDescriptions()[finalI]).fire();
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
            button.setId("wordButton");
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

        VBox leftVBox = new VBox();

        AnchorPane labelAnchor = new AnchorPane();

        Label label = new Label();
        label.setText("");
        label.setId("inWords");
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        AnchorPane.setLeftAnchor(label, 0.0);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("scrollPane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        commandContainers.add(new GridPane());
        commandContainers.get(index).setId("wordsGrid");

        textAreas.add(new TextArea());
        textAreas.get(index).setId("textArea");
        textAreas.get(index).textProperty().addListener(
                (observableValue, s, t1) -> {
                    jsonFileHandlerArrayList.get(currentTabIndex).commandWriter
                            .setContent(textAreas.get(currentTabIndex).getText());
                });

        gridPane.getColumnConstraints().addAll(left, right);
        gridPane.getRowConstraints().add(rowConstraints);

        labelAnchor.getChildren().add(label);
        scrollPane.setContent(commandContainers.get(index));
        leftVBox.getChildren().addAll(labelAnchor, scrollPane);
        gridPane.add(leftVBox, 0, 0);
        gridPane.add(textAreas.get(index), 1, 0);
        vBox.getChildren().addAll(hBoxes.get(index), gridPane);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        anchorPane.getChildren().add(vBox);
        tab.setContent(anchorPane);


        //add tab to tabPane
        tabPane.getTabs().add(index, tab);
        //select newly created tab
        tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);

        //as new tab is automatically selected at creation the currentTabIndex has to be changed as well
        currentTabIndex = tabPane.getTabs().size() - 2;

        //set id of tab to its index in tabPane
        tab.setId(currentTabIndex + "");

        //get modes and display them
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
        updateCommands();

        tab.setOnCloseRequest(ex -> removeTab(Integer.parseInt(tab.getId())));

        //if a tab is selected, change currentTabIndex to the index of the selected tab
        tab.setOnSelectionChanged(ex -> {
            if (tab.isSelected()) {

                currentTabIndex = tabPane.getSelectionModel().getSelectedIndex();
            }
        });
    }

    private void removeTab(int tabID) {

        for (int i = tabID; i < tabPane.getTabs().size() - 1; i++) {
            tabPane.getTabs().get(i).setId((Integer.parseInt(tabPane.getTabs().get(i).getId()) - 1) + "");
        }

        //remove jsonFileHandler instance as tab is closed and object is no longer needed
        jsonFileHandlerArrayList.remove(tabID);

        //remove all JavaFx components, as they are no longer displayed
        hBoxes.remove(tabID);
        textAreas.remove(tabID);
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
            aboutStage.initOwner(Main.stage);
            aboutStage.setTitle("About");
            aboutStage.getIcons().add(header_logo_image);
            aboutStage.setScene(aboutStage_scene);
            aboutStage.show();

            aboutStage.setOnCloseRequest(e -> aboutPageOpen = false);
        }
    }

    private String shorten(String s) {
        if (!s.equals("")) {
            while ("\n".equals(s.charAt(0) + "") || "\t".equals(s.charAt(0) + "") || " ".equals(s.charAt(0) + "")) {
                s = s.substring(1);
            }

            while ("\n".equals(s.charAt(s.length() - 1) + "") || "\t".equals(s.charAt(s.length() - 1) + "") || " ".equals(s.charAt(s.length() - 1) + "")) {
                s = s.substring(0, s.length() - 1);
            }
            return s;
        }
        return "";
    }

}
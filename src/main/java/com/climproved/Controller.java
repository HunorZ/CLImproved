package com.climproved;

import com.climproved.Notifications.Alert;
import com.climproved.Notifications.Notification;
import com.climproved.Notifications.Question;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Controller {

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab addTab;


    ArrayList<TabContentController> tabContentControllers = new ArrayList<>();
    ArrayList<String> filePathsAtLastSave = new ArrayList<>();

    int currentTabIndex = 0;

    String contentAtLastSave;

    Stage aboutStage = new Stage();


    public void initialize() {


        Notification.owner = Main.stage;
        //add first tab at init
        createNewTab();
        //addTab function
        addTab.setOnSelectionChanged(e -> createNewTab());

        aboutStage.initOwner(Main.stage);

        Main.stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
            final KeyCombination save_keyComb = new KeyCodeCombination(KeyCode.S,
                    KeyCombination.CONTROL_DOWN);

            final KeyCombination newTab_keyComb = new KeyCodeCombination(KeyCode.T,
                    KeyCombination.CONTROL_DOWN);

            public void handle(KeyEvent ke) {
                if (save_keyComb.match(ke)) {
                    save();
                    ke.consume(); // <-- stops passing the event to next node
                } else if (newTab_keyComb.match(ke)) {
                    createNewTab();
                    ke.consume();
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

    public static void updateDataSet() {
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

    private static boolean isRedirected(Map<String, List<String>> header) {
        for (String hv : header.get(null)) {
            if (hv.contains(" 301 ")
                    || hv.contains(" 302 ")) return true;
        }
        return false;
    }

    public void createNewTab() {

        //initialize another FXML document for the tab content
        Node n = null;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("tabContent.fxml"));
        try {
            n = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //new Tab
        Tab tab = new Tab("tab");
        tab.setText("Untitled                      ");


        tabContentControllers.add(loader.getController());
        int index = tabContentControllers.size() - 1;
        tab.setContent(n);

        filePathsAtLastSave.add("");

        //add tab to tabPane
        tabPane.getTabs().add(index, tab);
        //select newly created tab
        tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);

        //as new tab is automatically selected at creation the currentTabIndex has to be changed as well
        currentTabIndex = tabPane.getTabs().size() - 2;

        //set id of tab to its index in tabPane
        tab.setId(currentTabIndex + "");


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

        if (!s.equals(tabContentControllers.get(tabID).textArea.getText())) {
            if (new Question("There are unsaved changes left!\n" +
                    "Do you want to save them?").fire()) {
                save();
            }
        }

        for (int i = tabID; i < tabPane.getTabs().size() - 1; i++)
            tabPane.getTabs().get(i).setId((Integer.parseInt(tabPane.getTabs().get(i).getId()) - 1) + "");

        //remove all JavaFx components, as they are no longer displayed
        tabContentControllers.remove(tabID);
        //commandContainers.remove(tabID);
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
            contentAtLastSave = tabContentControllers.get(currentTabIndex).textArea.getText();
            output.write(tabContentControllers.get(currentTabIndex).textArea.getText().trim());
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
                contentAtLastSave = tabContentControllers.get(currentTabIndex).textArea.getText();
                output.write(tabContentControllers.get(currentTabIndex).textArea.getText().trim());
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

        ImageView aboutPageLogoImage = new ImageView(Main.header_logo_image);
        HBox pictureHBox = new HBox(aboutPageLogoImage);
        Label infoLabel = new Label("CLImproved for Windows\n" +
                "Version 1.4.2\n" +
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

        aboutStage.setTitle("About");
        aboutStage.getIcons().add(Main.header_logo_image);
        aboutStage.setScene(aboutStage_scene);
        aboutStage.show();
        aboutStage.onCloseRequestProperty();
    }
}
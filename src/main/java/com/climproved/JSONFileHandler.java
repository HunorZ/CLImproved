package com.climproved;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;

/**
 * @author Hunor Zakarias
 * @version 1.3.1
 */
public class JSONFileHandler {
    //content of the file
    private JSONArray fileContent;
    //stores the currently aviable commands
    private JSONArray nextCommands;

    //all currently accessed modes
    private Stack<JSONObject> accessedModes = new Stack<>();
    private boolean isInSubMode = false;

    //all accessed multicommands
    private Stack<JSONArray> multiCommands = new Stack<>();
    //index of multicommands which the user is in
    private Stack<Integer> currentMultiCommand = new Stack<>();
    private boolean isInMultiCommand = false;

    public CommandWriter commandWriter = new CommandWriter();


    /**
     * @param file JSONfile that should be interpreted
     */
    public void init(String file) {
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(Path.of(file));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File konnte nicht gelesen weden");
        }
        //the content of the json-file is stored inside fileContent
        JSONTokener tokener = new JSONTokener(inputStream);
        fileContent = new JSONArray(tokener);

        //tries to change the mode to the first available one
        try {
            changeMode(0);
        } catch (Exception e) {
            //executes only if it was not successful to change the mode, in which case the file is empty or faulty
            throw new IllegalArgumentException("File might be empty or is faulty");
        }
    }

    /**
     * @return returns array with all available modes
     */
    public String[] getModes() {
        if (!isInSubMode) {
            //executes code only, if the user is not in a submode
            String[] modes = new String[fileContent.length()];
            for (int i = 0; i < fileContent.length(); i++) {
                modes[i] = fileContent.getJSONObject(i).getString("category");
            }
            return modes;
        }
        return new String[]{};
    }

    /**
     * @param index changes the mode to the index
     */
    public void changeMode(int index) {
        if (!isInSubMode) {
            //executes code only, if the user is not in a submode as submodes must be exited
            nextCommands = fileContent.getJSONObject(index).getJSONArray("words");
            //as the method can only change the mode to one of the outer ones, the stack can be cleared
            // and the changed mode can be pushed onto the stack
            accessedModes.clear();
            accessedModes.push(fileContent.getJSONObject(index));
        }
    }

    /**
     * @return array with all currently available words
     */
    public String[] getWords() {
        String[] words = new String[nextCommands.length()];

        //loops through every element and gets the value for the object key "word"
        for (int i = 0; i < words.length; i++) {
            if (nextCommands.getJSONObject(i).getString("type").equals("finish")) {
                words[i] = "end";
            } else {
                try {
                    //tries to get the value for the object key "word"
                    words[i] = nextCommands.getJSONObject(i).getString("word");
                } catch (Exception e) {
                    //executes if object key "word" is not available
                    words[i] = "";
                }
            }
        }
        return words;
    }

    /**
     * @return a string array with all descriptions of the current words in the correct order
     */
    public String[] getDescriptions() {
        String[] descriptions = new String[nextCommands.length()];

        //loops through every element and gets the value for the object key "description"
        for (int i = 0; i < descriptions.length; i++) {
            try {
                //tries to get the value for the object key "description"
                descriptions[i] = nextCommands.getJSONObject(i).getString("description");
            } catch (Exception e) {
                //executes if object key "description" is not available
                descriptions[i] = "No Description";
            }
        }
        return descriptions;
    }

    /**
     * <p>loads next words which can be accessed by the getWords() method</p>
     *
     * @param indexOfPressedCommand index of the command from which the subcommands should be loaded
     */
    public void loadNextWords(int indexOfPressedCommand) {
        try {
            switch (nextCommands.getJSONObject(indexOfPressedCommand).getString("type")) {
                case "command":
                    //gets the value of the object "word" and writes it into the file
                    commandWriter.writeWord(nextCommands.getJSONObject(indexOfPressedCommand).getString("word"));

                    //loads the following commands of command with "indexOfPressedCommand",
                    //throws an error if no further commands are available
                    nextCommands = nextCommands.getJSONObject(indexOfPressedCommand).getJSONArray("words");
                    break;

                case "multiCommand":
                    //gets the value of the object "word" and writes it into the file
                    commandWriter.writeWord(nextCommands.getJSONObject(indexOfPressedCommand).getString("word"));

                    //as the chosen command is a multicommand, isInMultiCommand is set to true
                    //the multicommands of the command are pushed onto the stack as well as
                    //the integer of the multicommand, which the user is currently in is pushed onto the stack
                    isInMultiCommand = true;
                    multiCommands.push(nextCommands.getJSONObject(indexOfPressedCommand).getJSONArray("words"));
                    currentMultiCommand.push(0);

                    //the next commands are loaded into nextCommands
                    nextCommands = multiCommands.peek().getJSONArray(currentMultiCommand.peek());

                    System.out.println(nextCommands);
                    break;

                case "param":
                    //as parameters are handeld by the frontend no further operations have to be done and
                    //the next commands can be loaded into nextCommands
                    nextCommands = nextCommands.getJSONObject(indexOfPressedCommand).getJSONArray("words");
                    break;
                case "param/enterSubMode":
                    //isInSubMode is set to true
                    //the submode is pushed onto the "accessedMode" stack
                    isInSubMode = true;
                    accessedModes.push(nextCommands.getJSONObject(indexOfPressedCommand).getJSONObject("submode"));

                    //makes a brake as no further commands are available and
                    //a tab is added to visualize the submode in the final txt document
                    commandWriter.makeBreak();
                    commandWriter.addTab();

                    //the next commands are loaded into nextCommands
                    nextCommands = accessedModes.peek().getJSONArray("words");
                    break;

                case "command/enterSubMode":
                    //gets the value of the object "word" and writes it into the file,
                    //makes a brake as no further commands are available and
                    //a tab is added to visualize the submode in the final txt document
                    commandWriter.writeWord(nextCommands.getJSONObject(indexOfPressedCommand).getString("word"));
                    commandWriter.makeBreak();
                    commandWriter.addTab();

                    //isInSubMode is set to true
                    //the submode is pushed onto the "accessedMode" stack
                    isInSubMode = true;
                    accessedModes.push(nextCommands.getJSONObject(indexOfPressedCommand).getJSONObject("submode"));

                    //the next commands are loaded into nextCommands
                    nextCommands = accessedModes.peek().getJSONArray("words");
                    break;

                case "exitSubMode":
                    //gets the value of the object "word" and writes it into the file,
                    //makes a brake as no further commands are available and
                    //a tab is removed to visualize the exit of the submode in the final txt document
                    commandWriter.writeWord(nextCommands.getJSONObject(indexOfPressedCommand).getString("word"));
                    commandWriter.makeBreak();
                    commandWriter.makeBreak();
                    commandWriter.removeTab();

                    //as the submode is exited, it can be removed from the stack
                    accessedModes.pop();

                    //the next commands are loaded into nextCommands
                    nextCommands = accessedModes.peek().getJSONArray("words");

                    //if the size of the stack is 1, "isInSubMode" is set to false as this is the outermost mode
                    //which cannot be exited
                    if (accessedModes.size() == 1) {
                        isInSubMode = false;
                    }
                    break;

                case "finish":
                    //force into catch block as no further operations have to be made
                    throw new JSONException("");
            }
        } catch (JSONException e) {
            //if the user is currently in a multicommand and there are still  elements in the array left:
            if (isInMultiCommand && currentMultiCommand.peek() < multiCommands.peek().length() - 1) {
                //increment the currentMultiCommand variable
                currentMultiCommand.push(currentMultiCommand.pop() + 1);

                //loaded the next commands from the stack into nextCommands
                nextCommands = multiCommands.peek().getJSONArray(currentMultiCommand.peek());
            } else {

                //as the condition was false, no more multicommands are available
                //and the object can be ramoved from the stack
                if (isInMultiCommand) {
                    multiCommands.pop();
                    currentMultiCommand.pop();
                }

                //the next commands are loaded from the mode-stack
                nextCommands = accessedModes.peek().getJSONArray("words");

                //sets "isInMultiCommand" to false if the "multiCommands" stack is empty
                if (multiCommands.empty()) {
                    isInMultiCommand = false;
                }
                //creates a break
                commandWriter.makeBreak();
            }
        }
    }

    /**
     * <p>checks if a word is a parameter</p>
     *
     * @param indexOfPressedCommand index of the word which should be checked
     * @return the boolean value
     */
    public boolean isParam(int indexOfPressedCommand) {
        //if the value of the object key "type" equals "param" return true
        String currenPressedCommand = nextCommands.getJSONObject(indexOfPressedCommand).getString("type");
        if (currenPressedCommand.equals("param") || currenPressedCommand.equals("param/enterSubMode")) {
            return true;
        }
        return false;
    }
}

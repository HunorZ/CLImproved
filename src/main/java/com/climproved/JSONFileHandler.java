package com.climproved;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
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
    private Stack<JSONArray> accessedModes = new Stack<>();
    private boolean isInSubMode = false;

    //all accessed multicommands
    private Stack<JSONArray> multiCommands = new Stack<>();
    //index of multicommands which the user is in
    private Stack<Integer> currentMultiCommand = new Stack<>();
    private boolean isInMultiCommand = false;

    public CommandWriter commandWriter = new CommandWriter();

    //store the index of the current mode
    private int currentMode = 0;


    /**
     * @param file JSONfile that should be interpreted
     */
    public void init(String file) throws IllegalArgumentException, FileNotFoundException {
        InputStream inputStream;
        try {
            inputStream = Files.newInputStream(Path.of(file));
        } catch (IOException e) {
            throw new FileNotFoundException("Path not found");
        }
        //the content of the json-file is stored inside fileContent
        fileContent = new JSONArray(new JSONTokener(inputStream));

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
    public Word[] changeMode(int index) {
        if (!isInSubMode) {
            //executes code only, if the user is not in a submode as submodes must be exited
            nextCommands = fileContent.getJSONObject(index).getJSONArray("words");
            currentMode = index;

            //as the method can only change the mode to one of the outer ones, the stack can be cleared
            // and the changed mode can be pushed onto the stack
            accessedModes.clear();
        }

        return getWords();
    }

    /**
     * <p>loads next words which can be accessed by the getWords() method</p>
     *
     * @param indexOfPressedCommand index of the command from which the subcommands should be loaded
     */
    public Word[] getNextCommands(int indexOfPressedCommand) {
        try {
            switch (nextCommands.getJSONObject(indexOfPressedCommand).getString("type")) {
                case "command" -> {
                    //gets the value of the object "word" and writes it into the file and writes it into the file
                    commandWriter.writeWord(nextCommands.getJSONObject(indexOfPressedCommand).getString("word"));

                    //loads the following commands of command with "indexOfPressedCommand",
                    //throws an error if no further commands are available
                    nextCommands = nextCommands.getJSONObject(indexOfPressedCommand).getJSONArray("words");


                }
                case "multiCommand" -> {
                    //gets the value of the object "word" and writes it into the file as well as into the stack
                    commandWriter.writeWord(nextCommands.getJSONObject(indexOfPressedCommand).getString("word"));

                    //as the chosen command is a multicommand, isInMultiCommand is set to true
                    //the multicommands of the command are pushed onto the stack as well as
                    //the integer of the multicommand, which the user is currently in is pushed onto the stack
                    isInMultiCommand = true;
                    multiCommands.push(nextCommands.getJSONObject(indexOfPressedCommand).getJSONArray("words"));
                    currentMultiCommand.push(0);

                    //the next commands are loaded into nextCommands
                    nextCommands = multiCommands.peek().getJSONArray(currentMultiCommand.peek());
                }
                case "param" ->
                        //as parameters are handeld by the frontend no further operations have to be done and
                        //the next commands can be loaded into nextCommands
                        nextCommands = nextCommands.getJSONObject(indexOfPressedCommand).getJSONArray("words");

                case "param_enterSubMode" -> {
                    //isInSubMode is set to true
                    //the submode is pushed onto the "accessedMode" stack
                    isInSubMode = true;
                    accessedModes.push(nextCommands.getJSONObject(indexOfPressedCommand).getJSONArray("words"));


                    //makes a brake as no further commands are available and
                    //a tab is added to visualize the submode in the final txt document
                    commandWriter.addTab();
                    commandWriter.makeBreak();


                    //the next commands are loaded into nextCommands
                    nextCommands = accessedModes.peek();
                }
                case "command_enterSubMode" -> {
                    //gets the value of the object "word" and writes it into the file,
                    //makes a brake as no further commands are available and
                    //a tab is added to visualize the submode in the final txt document
                    commandWriter.writeWord(nextCommands.getJSONObject(indexOfPressedCommand).getString("word"));
                    commandWriter.addTab();
                    commandWriter.makeBreak();


                    //isInSubMode is set to true
                    //the submode is pushed onto the "accessedMode" stack
                    isInSubMode = true;
                    accessedModes.push(nextCommands.getJSONObject(indexOfPressedCommand).getJSONArray("words"));

                    //the next commands are loaded into nextCommands
                    nextCommands = accessedModes.peek();
                }
                case "exitSubMode" -> {
                    //gets the value of the object "word" and writes it into the file,
                    //makes a brake as no further commands are available and
                    //a tab is removed to visualize the exit of the submode in the final txt document
                    commandWriter.writeWord(nextCommands.getJSONObject(indexOfPressedCommand).getString("word"));
                    commandWriter.removeTab();
                    commandWriter.makeBreak();
                    commandWriter.makeBreak();


                    //as the submode is exited, it can be removed from the stack
                    accessedModes.pop();

                    //the next commands are loaded into nextCommands
                    if (accessedModes.size() > 0) {
                        nextCommands = accessedModes.peek();
                    }

                    //if the size of the stack is 0, "isInSubMode" is set to false
                    if (accessedModes.size() == 0) {
                        isInSubMode = false;
                        //load next commands
                        nextCommands = fileContent.getJSONObject(currentMode).getJSONArray("words");
                    }
                }
                case "finish" ->
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
                //and the object can be removed from the stack
                if (isInMultiCommand) {
                    multiCommands.pop();
                    currentMultiCommand.pop();
                }

                //if the size of the stack is 0, the content is loaded from the file
                if (accessedModes.size() == 0) {
                    nextCommands = fileContent.getJSONObject(currentMode).getJSONArray("words");
                } else {
                    nextCommands = accessedModes.peek();
                }

                //sets "isInMultiCommand" to false if the "multiCommands" stack is empty
                if (multiCommands.empty()) isInMultiCommand = false;

                commandWriter.makeBreak();
            }
        }
        return getWords();
    }

    private Word[] getWords() {
        Word[] words = new Word[nextCommands.length()];
        for (int i = 0; i < nextCommands.length(); i++) {

            String word;
            if (nextCommands.getJSONObject(i).getString("type").equals("finish")) {
                word = "finish";
            } else {
                try {
                    //tries to get the value for the object key "word"
                    word = nextCommands.getJSONObject(i).getString("word");
                } catch (Exception e) {
                    //executes if object key "word" is not available
                    word = "";
                }
            }

            String description;
            try {
                //tries to get the value for the object key "description"
                description = nextCommands.getJSONObject(i).getString("description");
            } catch (Exception e) {
                //executes if object key "description" is not available
                description = "No Description";
            }
            words[i] = new Word(word, description,
                    Enum.valueOf(Word.Type.class, nextCommands.getJSONObject(i).getString("type").toUpperCase()));
        }
        return words;
    }
}

package com.climproved;

public class CommandWriter {
    private String content = "";
    private String tabs = "";
    private boolean lineJumpMade = true;

    /**
     * @param s writes a String to the last line without making a break
     */
    public void writeWord(String s) {
        if (lineJumpMade) {
            lineJumpMade = false;
        } else {
            content += " ";
        }
        content += s;
    }

    public void setContent(String s) {
        content = s;
    }

    public String getContent() {
        return content;
    }

    /**
     * makes a break
     */
    public void makeBreak() {
        content += "\n" + tabs;
        lineJumpMade = true;
    }

    /**
     * adds a tab which then are added after evey break
     */
    public void addTab() {
        tabs += "\t";
    }

    /**
     * removes a tab which then are added after evey break
     */
    public void removeTab() {
        tabs = tabs.substring(1);
    }
}

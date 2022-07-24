package com.climproved;

import javax.swing.*;

public class PopUp {
    static final long serialVersionUID = -445307629455571367L;

    public static void print(String message) {
        JOptionPane.showMessageDialog(null, message, "Ausgabe", JOptionPane.PLAIN_MESSAGE);
    }

    public static boolean confirm(String question) {
        return JOptionPane.showConfirmDialog(null, question, "Frage", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static String readLine(String prompt) {
        JFrame jf = new JFrame();
        jf.setAlwaysOnTop(true);
        jf.requestFocus();
        String s = JOptionPane.showInputDialog(jf, prompt, "Eingabe", JOptionPane.PLAIN_MESSAGE);
        return (s != null) ? s : "";
    }

    public static String readWord(String prompt) {
        return readLine(prompt).replaceAll("[\\p{Punct}\\s]+", " ").trim().split(" ")[0];
    }

    public static int readInt(String prompt) {
        String s = readLine(prompt);
        while (true) {
            try {
                return s.isEmpty() ? 0 : Integer.parseInt(s);
            } catch (NumberFormatException e) {
                s = readLine("\"" + s + "\" ist keine int-Zahl!\n\n" + prompt);
            }
        }
    }

    public static long readLong(String prompt) {
        String s = readLine(prompt);
        while (true) {
            try {
                return s.isEmpty() ? 0L : Long.parseLong(s);
            } catch (NumberFormatException e) {
                s = readLine("\"" + s + "\" ist keine long-Zahl!\n\n" + prompt);
            }
        }
    }

    public static float readFloat(String prompt) {
        String s = readLine(prompt);
        while (true) {
            try {
                return s.isEmpty() ? 0F : Float.parseFloat(s);
            } catch (NumberFormatException e) {
                s = readLine("\"" + s + "\" ist keine float-Zahl!\n\n" + prompt);
            }
        }
    }

    public static double readDouble(String prompt) {
        String s = readLine(prompt);
        while (true) {
            try {
                return s.isEmpty() ? 0D : Double.parseDouble(s);
            } catch (NumberFormatException e) {
                s = readLine("\"" + s + "\" ist keine double-Zahl!\n\n" + prompt);
            }
        }
    }
}

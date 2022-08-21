package com.climproved;


public class Word {

    public enum Type {
        COMMAND,
        PARAM,
        MULTICOMMAND,
        COMMAND_ENTERSUBMODE,
        PARAM_ENTERSUBMODE,
        EXITSUBMODE,
        FINISH
    }

    final String word;
    final String description;
    final Type type;

    public Word(String word, String description, Type type) {
        this.word = word;
        this.description = description;
        this.type = type;
    }

    @Override
    public String toString() {
        return word + " | " + description + " | " + type;
    }
}

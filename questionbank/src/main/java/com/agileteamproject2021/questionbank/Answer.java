package com.agileteamproject2021.questionbank;

public class Answer {
    String textValue;
    boolean isCorrect;

    public Answer(String textValue, boolean isCorrect){
        this.textValue = textValue;
        this.isCorrect = isCorrect;
    }

    @Override
    public String toString() {
        return String.format("Answer:\t%s\tisCorrect?:\t%b", textValue, isCorrect);
    }
}
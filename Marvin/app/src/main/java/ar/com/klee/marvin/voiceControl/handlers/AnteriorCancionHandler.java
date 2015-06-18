package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class AnteriorCancionHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public AnteriorCancionHandler(String command, TTS textToSpeech){

        expressionMatcher = new ExpressionMatcher("anterior canción");

        this.command = command;

        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        textToSpeech.speakText("Reproduciendo");

        //CODIGO PARA VOLVER A LA CANCIÓN ANTERIOR

        return 0;

    }
}

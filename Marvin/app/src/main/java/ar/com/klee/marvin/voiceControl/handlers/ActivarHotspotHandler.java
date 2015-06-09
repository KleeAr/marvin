package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class ActivarHotspotHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public ActivarHotspotHandler(String command, TTS textToSpeech){

        expressionMatcher = new ExpressionMatcher("activar hotspot");

        this.command = command;

        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        textToSpeech.speakText("activando hotspot");

        //CODIGO PARA ACTIVAR HOTSPOT

        return 0;

    }
}

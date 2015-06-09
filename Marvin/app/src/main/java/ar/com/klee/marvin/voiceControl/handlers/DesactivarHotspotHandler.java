package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class DesactivarHotspotHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public DesactivarHotspotHandler(String command, TTS textToSpeech){

        expressionMatcher = new ExpressionMatcher("desactivar hotspot");

        this.command = command;

        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        textToSpeech.speakText("desactivando hotspot");

        //CODIGO PARA DESACTIVAR HOTSPOT

        return 0;

    }
}

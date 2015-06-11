package ar.com.klee.marvin.voiceControl.handlers;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class ReproducirCancionHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private String song;
    private TTS textToSpeech;

    public ReproducirCancionHandler(String command, TTS textToSpeech){

        expressionMatcher = new ExpressionMatcher("reproducir canción {cancion}");

        this.command = command;

        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        Map<String, String> values = expressionMatcher.getValuesFromExpression(command);

        song = values.get("cancion");

        textToSpeech.speakText("Reproduciendo canción "+song);

        //CODIGO PARA BUSCAR Y REPRODUCIR UNA CANCION

        return 0;

    }
}
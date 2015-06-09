package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class DetenerReproduccionHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public DetenerReproduccionHandler(String command, TTS textToSpeech){

        expressionMatcher = new ExpressionMatcher("detener reproducción");

        this.command = command;

        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        textToSpeech.speakText("Deteniendo reproducción");

        //CODIGO PARA DETENER MUSICA

        return 0;

    }
}

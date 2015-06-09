package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class SiguienteCancionHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public SiguienteCancionHandler(String command, TTS textToSpeech){

        expressionMatcher = new ExpressionMatcher("siguiente cancion");

        this.command = command;

        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        textToSpeech.speakText("Reproduciendo");

        //CODIGO PARA AVANZAR A LA CANCIÓN SIGUIENTE

        return 0;

    }
}

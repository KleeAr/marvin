package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class CerrarCamaraHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public CerrarCamaraHandler(String command, TTS textToSpeech){

        expressionMatcher = new ExpressionMatcher("cerrar cámara");

        this.command = command;

        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        textToSpeech.speakText("Cerrando cámara");

        //CODIGO PARA CERRAR CAMARA

        return 0;

    }
}

package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class GuardarFotoHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public GuardarFotoHandler(String command, TTS textToSpeech){

        expressionMatcher = new ExpressionMatcher("guardar foto");

        this.command = command;

        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        textToSpeech.speakText("Guardando foto");

        //CODIGO PARA GUARDAR FOTO

        return 0;

    }
}
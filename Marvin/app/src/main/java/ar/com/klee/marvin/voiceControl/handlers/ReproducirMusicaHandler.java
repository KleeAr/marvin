package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class ReproducirMusicaHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public ReproducirMusicaHandler(String command, TTS textToSpeech){

        super(expressionMatcher, textToSpeech, context, commandHandlerManager);
        expressionMatcher = new ExpressionMatcher("reproducir música");

        this.command = command;

        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        textToSpeech.speakText("Reproduciendo música");

        //CODIGO PARA REPRUDUCIR MUSICA

        return 0;

    }
}

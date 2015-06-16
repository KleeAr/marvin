package ar.com.klee.marvin.voiceControl.handlers;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class ReproducirArtistaHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private String artist;
    private TTS textToSpeech;

    public ReproducirArtistaHandler(String command, TTS textToSpeech){

        super(expressionMatcher, textToSpeech, context, commandHandlerManager);
        expressionMatcher = new ExpressionMatcher("reproducir artista {artista}");

        this.command = command;

        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        Map<String, String> values = expressionMatcher.getValuesFromExpression(command);

        artist = values.get("cancion");

        textToSpeech.speakText("Reproduciendo artista "+artist);

        //CODIGO PARA BUSCAR Y REPRODUCIR UN ARTISTA

        return 0;

    }
}

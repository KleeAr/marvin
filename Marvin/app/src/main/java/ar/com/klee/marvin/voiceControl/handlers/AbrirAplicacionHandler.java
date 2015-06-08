package ar.com.klee.marvin.voiceControl.handlers;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class AbrirAplicacionHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public AbrirAplicacionHandler(String command, TTS textToSpeech){

        expressionMatcher = new ExpressionMatcher("abrir {aplicacion}");

        this.command = command;

        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        Map<String, String> values = expressionMatcher.getValuesFromExpression(command);

        textToSpeech.speakText("Abriendo "+values.get("aplicacion"));

        //CODIGO PARA ABRIR APLICACION

        return 0;

    }
}

package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class CalleSiguienteHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public CalleSiguienteHandler(String command, TTS textToSpeech){

        super(expressionMatcher, textToSpeech, context, commandHandlerManager);
        expressionMatcher = new ExpressionMatcher("calle siguiente");

        this.command = command;

        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        textToSpeech.speakText("Estás en ");

        //CODIGO PARA OBTENER CALLE SIGUIENTE

        return 0;

    }
}

package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class CerrarCamaraHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;
    private CameraActivity activity;
    CommandHandlerManager commandHandlerManager;

    public CerrarCamaraHandler(String command, TTS textToSpeech, CameraActivity activity, CommandHandlerManager commandHandlerManager){

        super(expressionMatcher, textToSpeech, context, commandHandlerManager);
        expressionMatcher = new ExpressionMatcher("cerrar cámara");

        this.command = command;

        this.textToSpeech = textToSpeech;

        this.commandHandlerManager = commandHandlerManager;

        this.activity = activity;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        textToSpeech.speakText("Cerrando cámara");

        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_MAIN,null);

        activity.finish();

        return 0;

    }
}

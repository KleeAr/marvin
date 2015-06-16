package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class GuardarFotoHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;
    private CameraActivity cameraActivity;

    public GuardarFotoHandler(String command, TTS textToSpeech, CameraActivity cameraActivity){

        super(expressionMatcher, textToSpeech, context, commandHandlerManager);
        expressionMatcher = new ExpressionMatcher("guardar foto");

        this.command = command;

        this.textToSpeech = textToSpeech;

        this.cameraActivity = cameraActivity;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        textToSpeech.speakText("Guardando foto");

        cameraActivity.save();

        return 0;

    }
}

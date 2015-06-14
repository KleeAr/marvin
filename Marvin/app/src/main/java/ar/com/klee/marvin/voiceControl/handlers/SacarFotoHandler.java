package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class SacarFotoHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;
    private CameraActivity cameraActivity;

    public SacarFotoHandler(String command, TTS textToSpeech, CameraActivity cameraActivity){

        expressionMatcher = new ExpressionMatcher("sacar foto");

        this.command = command;

        this.textToSpeech = textToSpeech;

        this.cameraActivity = cameraActivity;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        textToSpeech.speakText("Sacando foto");

        cameraActivity.takePicture();

        return 0;

    }
}

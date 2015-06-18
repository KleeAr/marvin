package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;
import android.content.Intent;

import java.util.Map;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.Helper;
import ar.com.klee.marvin.voiceControl.TTS;

public class AbrirAplicacionHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    private Context context;
    private CommandHandlerManager commandHandlerManager;

    public AbrirAplicacionHandler(String command, TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){

        expressionMatcher = new ExpressionMatcher("abrir {aplicacion}");

        this.command = command;

        this.textToSpeech = textToSpeech;

        this.context = context;

        this.commandHandlerManager = commandHandlerManager;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        Map<String, String> values = expressionMatcher.getValuesFromExpression(command);

        String app = values.get("aplicacion");

        textToSpeech.speakText("Abriendo "+ app);

        //CODIGO PARA ABRIR APLICACION

        if(app.equals("cámara")) {
            Intent intent = new Intent(context, CameraActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        return 0;

    }
}

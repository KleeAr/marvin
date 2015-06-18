package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;
import android.content.Intent;

import java.util.Map;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class AbrirAplicacionHandler extends CommandHandler{

    public AbrirAplicacionHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){

        super("abrir {aplicacion}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext currentContext){

        Map<String, String> values = getExpressionMatcher().getValuesFromExpression(currentContext.getString(COMMAND));

        String app = values.get("aplicacion");

        getTextToSpeech().speakText("Abriendo "+ app);
        
        //CODIGO PARA ABRIR APLICACION

        if(app.equals("cámara")) {
            Intent intent = new Intent(getContext(), CameraActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
        currentContext.put(STEP, 0);
        return currentContext;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // Do nothing
    }
}

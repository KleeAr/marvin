package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.Arrays;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.social.NotificationService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;
import ar.com.klee.marvin.voiceControl.TTS;

public class LeerWhatsappHandler extends CommandHandler {

    public LeerWhatsappHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("leer whatsapp"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Integer step = context.getInteger(STEP);
        switch(step){

            case 1:
                return stepOne(context);
            case 3:
                return stepThree(context);
        }
        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {

    }

    //WHATSAPP RECIBIDO
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        if(NotificationService.instance.wasListening) {
            if (STTService.getInstance().numberOfWhatsapp == 1)
                getTextToSpeech().speakText(STTService.getInstance().whatsAppContact + " te envió un mensaje de watsap. ¿Querés leerlo?");
            else
                getTextToSpeech().speakText(STTService.getInstance().whatsAppContact + " te envió " + STTService.getInstance().numberOfWhatsapp + " mensajes de watsap. ¿Querés leerlos?");
        }else{
            getTextToSpeech().speakText(NotificationService.instance.lastContact + " te envió un mensaje de watsap. ¿Querés leerlo?");
        }
        context.put(STEP, 3);
        return context;
    }

    //INDICA SI QUIERE LEER EL WHATSAPP
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("Abriendo watsap");

            Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage("com.whatsapp");
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(launchIntent);

            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando lectura");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("Cancelando lectura");
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 3);
        return context;

    }

}


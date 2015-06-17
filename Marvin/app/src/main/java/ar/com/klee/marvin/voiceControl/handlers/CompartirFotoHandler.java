package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class CompartirFotoHandler extends CommandHandler{

    public CompartirFotoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("compartir foto", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){
        Integer step = context.get(STEP, Integer.class);
        switch(step){

            case 1:
                return stepOne(context);
            case 3:
                return stepThree(context);
            case 4:
                return facebook(context);
            case 5:
                return twitter(context);
            case 6:
                return instagram(context);

        }
        context.put(STEP, 0);
        return context;
    }

    public CommandHandlerContext stepOne(CommandHandlerContext context){

        CameraActivity cameraActivity = context.get(ACTIVITY, CameraActivity.class);
        getTextToSpeech().speakText("¿En qué red social deseás compartir la foto?");

        cameraActivity.share();

        context.put(STEP, 3);
        return context;

    }

    public CommandHandlerContext stepThree(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        if(input.equals("facebook")) {
            getTextToSpeech().speakText("Compartiendo foto en Facebook. ¿Querés agregar un mensaje?");
            context.put(STEP, 4);
            return context;
        }else if(input.equals("twitter")) {
            getTextToSpeech().speakText("Compartiendo foto en Twitter. ¿Querés agregar un mensaje?");
            context.put(STEP, 5);
            return context;
        }else if(input.equals("instagram")) {
            getTextToSpeech().speakText("Compartiendo foto en Instagram. ¿Querés agregar un mensaje?");
            context.put(STEP, 6);
        }
        getTextToSpeech().speakText("Debe indicar facebook, twitter o instagram");
        context.put(STEP, 3);
        return context;
    }

    public CommandHandlerContext facebook(CommandHandlerContext context){

        String input = context.get(COMMAND, String.class);
        if(input.equals("si")) {
            getCommandHandlerManager().setCurrentCommandHandler(new CompartirEnFacebookHandler("compartir en facebook", textToSpeech, activity));
            textToSpeech.speakText("¿Qué mensaje querés agregar?");
            return 1;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            commandHandlerManager.setCurrentCommandHandler(new CompartirEnFacebookHandler("compartir en facebook", textToSpeech, activity));
            textToSpeech.speakText("¿Querés agregar un hashtag?");
            return 5;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 4;

    }

    public int twitter(String input){

        if(input.equals("si")) {
            commandHandlerManager.setCurrentCommandHandler(new CompartirEnTwitterHandler("compartir en twitter", textToSpeech, activity));
            textToSpeech.speakText("¿Qué mensaje querés agregar?");
            return 1;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            commandHandlerManager.setCurrentCommandHandler(new CompartirEnTwitterHandler("compartir en twitter", textToSpeech, activity));
            textToSpeech.speakText("¿Querés agregar un hashtag?");
            return 5;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 5;

    }

    public int instagram(String input){

        if(input.equals("si")) {
            commandHandlerManager.setCurrentCommandHandler(new CompartirEnInstagramHandler("compartir en instagram", textToSpeech, activity));
            textToSpeech.speakText("¿Qué mensaje querés agregar?");
            return 1;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            commandHandlerManager.setCurrentCommandHandler(new CompartirEnInstagramHandler("compartir en instagram", textToSpeech, activity));
            textToSpeech.speakText("¿Querés agregar un hashtag?");
            return 5;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 5;

    }

}

package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class CompartirEnInstagramHandler extends CommandHandler{

    public static final String INSTAGRAM_HASHTAG = "INSTAGRAM_HASHTAG";

    public CompartirEnInstagramHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("compartir en instagram", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Integer step = context.get(STEP, Integer.class);
        switch(step){

            case 1:
                return stepOne(context);
            case 3:
                return stepThree(context);
            case 5:
                return stepFive(context);
            case 7:
                return stepSeven(context);
            case 9:
                return stepNine(context);
            case 11:
                return stepEleven(context);

        }

        context.put(STEP, 0);
        return context;

    }

    //PRONUNCIA MENSAJE
    public CommandHandlerContext stepOne(CommandHandlerContext context){

        String message = context.get(MESSAGE, String.class);

        getTextToSpeech().speakText("¿Querés publicar el mensaje " + message + " junto a la foto?");
        context.put(STEP, 3);
        return context;

    }

    //CONFIRMA MENSAJE
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.get(COMMAND, String.class);

        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Querés agregar un hashtag junto a la foto?");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje deseás publicar junto a la foto?");
            context.put(STEP, 1);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 3);
        return context;

    }

    //INDICA SI SE QUIERE AGREGAR UN HASHTAG
    public CommandHandlerContext stepFive(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            context.put(STEP, 7);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("Publicando la foto en Instagram");
            CameraActivity cameraActivity = context.get(ACTIVITY, CameraActivity.class);
            cameraActivity.shareInInstagram();
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 5);
        return context;

    }

    //INGRESA HASHTAG
    @SuppressWarnings("unchecked")
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        getTextToSpeech().speakText("¿Querés agregar el hashtag "+input+" junto a la foto?");
        if(!context.containsKey(INSTAGRAM_HASHTAG)) {
            List<String> hashtags = new ArrayList<>();
            context.put(INSTAGRAM_HASHTAG, hashtags);
        }
        List<String> hashtags = context.get(INSTAGRAM_HASHTAG, List.class);
        hashtags.add(input);
        context.put(STEP, 9);
        return context;

    }

    //CONFIRMA HASHTAG
    public CommandHandlerContext stepNine(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Querés agregar otro hashtag?");
            context.put(STEP, 11);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            List<String> hashtags = context.get(INSTAGRAM_HASHTAG, List.class);
            hashtags.remove(hashtags.size()-1);
            context.put(STEP, 7);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");
        context.put(STEP, 9);
        return context;
    }

    //INDICA SI SE QUIERE AGREGAR OTRO HASHTAG
    public CommandHandlerContext stepEleven(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            context.put(STEP, 7);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("Publicando la foto en Instagram");

            CameraActivity cameraActivity = context.get(ACTIVITY, CameraActivity.class);
            cameraActivity.shareInInstagram();

            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");
        context.put(STEP, 11);
        return context;

    }


}


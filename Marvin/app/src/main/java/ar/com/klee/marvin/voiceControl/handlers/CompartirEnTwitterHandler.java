package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class CompartirEnTwitterHandler extends CommandHandler{

    public static final String TWITTER_HASHTAG = "TWITTER_HASHTAG";

    public CompartirEnTwitterHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("compartir en twitter", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(context.getBoolean(SET_MESSAGE)) {
            context.put(MESSAGE, context.getString(COMMAND));
        }
        Integer step = context.getInteger(STEP);
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

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        List<String> hashtags = new ArrayList<>();
        commandHandlerContext.put(TWITTER_HASHTAG, hashtags);
    }

    //PRONUNCIA MENSAJE
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        String message = context.getString(MESSAGE);
        context.put(SET_MESSAGE, false);
        getTextToSpeech().speakText("¿Querés publicar el mensaje " + message + " junto a la foto?");
        context.put(STEP, 3);
        return context;
    }

    //CONFIRMA MENSAJE
    public CommandHandlerContext stepThree(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Querés agregar un hashtag junto a la foto?");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            context.put(STEP, 9);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje deseás publicar junto a la foto?");
            context.put(STEP, 1);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        context.put(STEP, 3);
        return context;

    }

    //INDICA SI SE QUIERE AGREGAR UN HASHTAG
    public CommandHandlerContext stepFive(CommandHandlerContext context){
        String input = context.getString(COMMAND);
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
            getTextToSpeech().speakText("Publicando la foto en Twitter");
            CameraActivity cameraActivity = context.getObject(ACTIVITY, CameraActivity.class);
            cameraActivity.shareInTwitter();
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        context.put(STEP, 5);
        return context;

    }

    //INGRESA HASHTAG
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        getTextToSpeech().speakText("¿Querés agregar el hashtag "+input+" junto a la foto?");
        List<String> hashtags = context.getList(TWITTER_HASHTAG, String.class);
        hashtags.add(input);
        context.put(STEP, 9);
        return context;
    }

    //CONFIRMA HASHTAG
    public CommandHandlerContext stepNine(CommandHandlerContext context){
        String input = context.getString(COMMAND);
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
            List<String> hashtags = context.getList(TWITTER_HASHTAG, String.class);
            hashtags.remove(hashtags.size() - 1);
            context.put(STEP, 7);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");
        context.put(STEP, 9);
        return context;

    }

    //INDICA SI SE QUIERE AGREGAR OTRO HASHTAG
    public CommandHandlerContext stepEleven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
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
            getTextToSpeech().speakText("Publicando la foto en Twitter");

            CameraActivity cameraActivity = context.getObject(ACTIVITY, CameraActivity.class);
            cameraActivity.shareInTwitter();

            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        context.put(STEP, 11);
        return context;

    }

}


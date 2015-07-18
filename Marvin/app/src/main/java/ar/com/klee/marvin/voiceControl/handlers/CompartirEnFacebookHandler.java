package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class CompartirEnFacebookHandler extends CommandHandler{

    protected static final String FACEBOOK_HASHTAG = "FACEBOOK_HASHTAG";

    public CompartirEnFacebookHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("compartir en facebook", textToSpeech, context, commandHandlerManager);
    }


    @Override
    public CommandHandlerContext drive(CommandHandlerContext context){
        Integer step = context.getInteger(STEP);
        if(context.getBoolean(SET_MESSAGE)) {
            context.put(MESSAGE, context.getString(COMMAND));
        }
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
        commandHandlerContext.put("HASHTAGS", new ArrayList<String>());
        commandHandlerContext.put("TAGGED", new ArrayList<String>());
        // TODO
    }

    //PRONUNCIA MENSAJE
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        getTextToSpeech().speakText("¿Querés publicar el mensaje " + context.getString(MESSAGE) + " junto a la foto?");
        context.put(SET_MESSAGE, false);
        return context.put(STEP, 3);
    }

    //CONFIRMA MENSAJE
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            return context.put(STEP, 5);
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            return context.put(STEP, 0);
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje deseás publicar junto a la foto?");
            return context.put(STEP, 1);
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        return context.put(STEP, 3);

    }

    //INDICA SI SE QUIERE AGREGAR UN HASHTAG
    public CommandHandlerContext stepFive(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            return context.put(STEP, 7);
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            return context.put(STEP, 0);
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("Publicando en el muro de Facebook");

            context.getObject(ACTIVITY, CameraActivity.class).shareInFacebook();
            return context.put(STEP, 0);
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        return context.put(STEP, 5);
    }

    //INGRESA HASHTAG
    public CommandHandlerContext stepSeven(CommandHandlerContext context){

        String hashtag = context.getString(COMMAND);
        getTextToSpeech().speakText("¿Querés agregar el hashtag " + hashtag + "?");

        context.getList(FACEBOOK_HASHTAG, String.class).add(hashtag);

        return context.put(STEP, 9);

    }

    //CONFIRMA HASHTAG
    public CommandHandlerContext stepNine(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Querés agregar otro hashtag?");
            return context.put(STEP, 11);
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            return context.put(STEP, 0);
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            List<String> hashtags = context.getList(FACEBOOK_HASHTAG, String.class);
            hashtags.remove(hashtags.size()-1);
            return context.put(STEP, 7);
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        return context.put(STEP, 9);

    }

    //INDICA SI SE QUIERE AGREGAR OTRO HASHTAG
    public CommandHandlerContext stepEleven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            return context.put(STEP, 7);
        }
        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            return context.put(STEP, 0);
        }
        if(input.equals("no")){
            getTextToSpeech().speakText("Publicando en el muro de Facebook");
            context.getObject(ACTIVITY, CameraActivity.class).shareInFacebook();
            return context.put(STEP, 0);
        }
        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");
        return context.put(STEP, 11);
    }

}


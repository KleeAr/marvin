package ar.com.klee.marvin.voiceControl.handlers.site;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvin.activities.SiteActivity;
import ar.com.klee.marvin.activities.TripActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class CompartirSitioHandler extends CommandHandler {

    public CompartirSitioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("compartir sitio","publicar sitio"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){
        Integer step = context.getInteger(STEP);
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

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }

    public CommandHandlerContext stepOne(CommandHandlerContext context){

        getTextToSpeech().speakText("¿En qué red social deseás compartirlo?");
        getCommandHandlerManager().setIsPhotoTaken(false);

        context.put(STEP, 3);
        return context;

    }

    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);

        if(input.equals("facebook")) {
            getTextToSpeech().speakText("Compartiendo sitio en Facebook. ¿Querés agregar un mensaje?");
            context.put(STEP, 4);
            return context;
        }

        if(input.equals("twitter")) {
            getTextToSpeech().speakText("Compartiendo sitio en Twitter. ¿Querés agregar un mensaje?");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("instagram")) {
            getTextToSpeech().speakText("Compartiendo sitio en Instagram");
            SiteActivity siteActivity = context.getObject(ACTIVITY, SiteActivity.class);
            siteActivity.shareInInstagram("");
            //context.put(STEP, 6);
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar facebook, twitter o instagram");
        context.put(STEP, 3);
        return context;
    }

    public CommandHandlerContext facebook(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getCommandHandlerManager().setCurrentCommandHandler(getCommandHandlerManager().getCompartirEnFacebookHandler());
            getTextToSpeech().speakText("¿Qué mensaje querés agregar?");
            context.put(SET_MESSAGE, true);
            return context.put(STEP, 1);
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            return context.put(STEP, 0);
        }

        if(input.equals("no")){
            getCommandHandlerManager().setCurrentCommandHandler(getCommandHandlerManager().getCompartirEnFacebookHandler());
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            return context.put(STEP, 5);
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        return context.put(STEP, 4);

    }

    public CommandHandlerContext twitter(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getCommandHandlerManager().setCurrentCommandHandler(getCommandHandlerManager().getCompartirEnTwitterHandler());
            getTextToSpeech().speakText("¿Qué mensaje querés agregar?");
            context.put(SET_MESSAGE, true);
            return context.put(STEP, 1);
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            return context.put(STEP, 0);
        }

        if(input.equals("no")){
            getCommandHandlerManager().setCurrentCommandHandler(getCommandHandlerManager().getCompartirEnTwitterHandler());
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            return context.put(STEP, 5);
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        return context.put(STEP, 5);

    }

    public CommandHandlerContext instagram(CommandHandlerContext context){
        String input = context.getString(COMMAND);

        if(input.equals("si") || input.equals("sí")) {
            getCommandHandlerManager().setCurrentCommandHandler(getCommandHandlerManager().getCompartirInstagramHandler());
            getTextToSpeech().speakText("¿Qué mensaje querés agregar?");
            context.put(SET_MESSAGE, true);
            return context.put(STEP, 1);
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            return context.put(STEP, 0);
        }

        if(input.equals("no")){
            getCommandHandlerManager().setCurrentCommandHandler(getCommandHandlerManager().getCompartirInstagramHandler());
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            return context.put(STEP, 5);
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        return context.put(STEP, 5);

    }

}

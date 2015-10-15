package ar.com.klee.marvinSimulator.voiceControl.handlers.camera;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.CameraActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class CompartirFotoHandler extends CommandHandler {

    public CompartirFotoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("compartir foto","publicar foto","compartir"), textToSpeech, context, commandHandlerManager);
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

        if(getCommandHandlerManager().getIsPhotoTaken()) {

            getTextToSpeech().speakText("¿En qué red social deseás compartirla?");

            context.put(STEP, 3);
            return context;

        }else{

            getTextToSpeech().speakText("Debés sacar una foto antes");

            context.put(STEP, 0);
            return context;

        }

    }

    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);

        if(input.equals("facebook")) {
            getTextToSpeech().speakText("Compartiendo foto en Facebook. ¿Querés agregar un mensaje?");
            context.put(STEP, 4);
            return context;
        }

        if(input.equals("twitter")) {
            getTextToSpeech().speakText("Compartiendo foto en Twitter. ¿Querés agregar un mensaje?");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("instagram")) {
            getTextToSpeech().speakText("Compartiendo foto en Instagram");
            CameraActivity cameraActivity = context.getObject(ACTIVITY, CameraActivity.class);
            cameraActivity.shareInInstagram("");
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
            getCommandHandlerManager().getCommandHandler().addSpecificContextData(context);
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
            getCommandHandlerManager().getCommandHandler().addSpecificContextData(context);
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
            getCommandHandlerManager().getCommandHandler().addSpecificContextData(context);
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
            getCommandHandlerManager().getCommandHandler().addSpecificContextData(context);
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
            getCommandHandlerManager().getCommandHandler().addSpecificContextData(context);
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
            getCommandHandlerManager().getCommandHandler().addSpecificContextData(context);
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            return context.put(STEP, 5);
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        return context.put(STEP, 5);

    }

}

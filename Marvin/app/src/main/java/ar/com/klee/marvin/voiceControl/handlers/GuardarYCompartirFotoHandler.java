package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class GuardarYCompartirFotoHandler extends CommandHandler{

    public GuardarYCompartirFotoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("guardar y compartir foto", textToSpeech, context, commandHandlerManager);
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

            getTextToSpeech().speakText("Guardando foto. ¿En qué red social deseás compartirla?");
            context.getObject(ACTIVITY, CameraActivity.class).save();
            context.getObject(ACTIVITY, CameraActivity.class).share();
            getCommandHandlerManager().setIsPhotoTaken(false);

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
        }else if(input.equals("twitter")) {
            getTextToSpeech().speakText("Compartiendo foto en Twitter. ¿Querés agregar un mensaje?");
            context.put(STEP, 5);
            return context;
        }else if(input.equals("instagram")) {
            getTextToSpeech().speakText("Compartiendo foto en Instagram. ¿Querés agregar un mensaje?");
            context.put(STEP, 6);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar facebook, twitter o instagram");
        context.put(STEP, 3);
        return context;

    }

    public CommandHandlerContext facebook(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getCommandHandlerManager().setCurrentCommandHandler(getCommandHandlerManager().getCompartirEnFacebookHandler());
            getTextToSpeech().speakText("¿Qué mensaje querés agregar?");
            context.put(STEP, 1);
            context.put(SET_MESSAGE, true);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getCommandHandlerManager().setCurrentCommandHandler(getCommandHandlerManager().getCompartirEnFacebookHandler());
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 4);
        return context;
    }

    public CommandHandlerContext twitter(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getCommandHandlerManager().setCurrentCommandHandler(getCommandHandlerManager().getCompartirEnTwitterHandler());
            getTextToSpeech().speakText("¿Qué mensaje querés agregar?");
            context.put(STEP, 1);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getCommandHandlerManager().setCurrentCommandHandler(getCommandHandlerManager().getCompartirEnTwitterHandler());
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 5);
        return context;

    }

    public CommandHandlerContext instagram(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getCommandHandlerManager().setCurrentCommandHandler(getCommandHandlerManager().getCompartirInstagramHandler());
            getTextToSpeech().speakText("¿Qué mensaje querés agregar?");
            context.put(STEP, 1);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getCommandHandlerManager().setCurrentCommandHandler(getCommandHandlerManager().getCompartirInstagramHandler());
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");
        context.put(STEP, 5);
        return context;
    }

}

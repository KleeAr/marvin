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

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
        RETURN;
    }

    public CommandHandlerContext stepOne(CommandHandlerContext context){

        getTextToSpeech().speakText("");
        CameraActivity activity = context.get(ACTIVITY, CameraActivity.class);

        activity.save();

        getTextToSpeech().speakText("Guardando foto. ¿En qué red social deseás compartirla?");

        activity.share();

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
            return context;
        }

        getTextToSpeech().speakText("Debe indicar facebook, twitter o instagram");
        context.put(STEP, 3);
        return context;

    }

    public CommandHandlerContext facebook(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        if(input.equals("si")) {
            getCommandHandlerManager().setCommandHandler(new CompartirEnFacebookHandler("compartir en facebook", getTextToSpeech(), activity));
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
            getCommandHandlerManager().setCommandHandler(new CompartirEnFacebookHandler("compartir en facebook", getTextToSpeech(), activity));
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 4);
        return context;
    }

    public CommandHandlerContext twitter(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        if(input.equals("si")) {
            getCommandHandlerManager().setCommandHandler(new CompartirEnTwitterHandler("compartir en twitter", getTextToSpeech(), activity));
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
            getCommandHandlerManager().setCommandHandler(new CompartirEnTwitterHandler("compartir en twitter", getTextToSpeech(), activity));
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 5);
        return context;

    }

    public CommandHandlerContext instagram(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        if(input.equals("si")) {
            getCommandHandlerManager().setCommandHandler(new CompartirEnInstagramHandler("compartir en instagram", textToSpeech, activity));
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
            getCommandHandlerManager().setCommandHandler(new CompartirEnInstagramHandler("compartir en instagram", textToSpeech, activity));
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");
        context.put(STEP, 5);
        return context;
    }

}

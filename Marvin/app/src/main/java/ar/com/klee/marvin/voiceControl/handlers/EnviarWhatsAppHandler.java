package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.social.WhatsAppService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarWhatsAppHandler extends CommandHandler{


    public static final String MENSAJE = "mensaje";
    private final WhatsAppService whatsAppService;

    public EnviarWhatsAppHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("enviar whatsapp {mensaje}", textToSpeech, context, commandHandlerManager);
        this.whatsAppService = new WhatsAppService(context);
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

        }
        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        commandHandlerContext.put(SET_MESSAGE, false);
        commandHandlerContext.put(MESSAGE, getExpressionMatcher().getValuesFromExpression(commandHandlerContext.getString(COMMAND)).get(MENSAJE));
    }

    //PRONUNCIA MENSAJE
    public CommandHandlerContext stepOne(CommandHandlerContext context){

        getTextToSpeech().speakText("¿Querés enviar el mensaje " + context.getString(MESSAGE) + " por WhatsApp?");

        context.put(SET_MESSAGE, false);
        context.put(STEP, 3);
        return context;

    }

    //CONFIRMA MENSAJE
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            sendWhatsApp(context.getString(MESSAGE));
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje deseás enviar?");
            context.put(SET_MESSAGE, true);
            context.put(STEP, 1);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        context.put(STEP, 3);
        return context;
    }

    public void sendWhatsApp(String textToPublish) {

        Character firstCharacter, newFirstCharacter;

        firstCharacter = textToPublish.charAt(0);
        newFirstCharacter = Character.toUpperCase(firstCharacter);
        textToPublish = textToPublish.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        getTextToSpeech().speakText("Seleccioná el contacto y presioná dos veces atrás para volver a la aplicación");

        whatsAppService.sendWhatsApp(textToPublish);

    }

}


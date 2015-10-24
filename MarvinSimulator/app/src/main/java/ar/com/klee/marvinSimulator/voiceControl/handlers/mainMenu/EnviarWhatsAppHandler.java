package ar.com.klee.marvinSimulator.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.social.WhatsAppService;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class EnviarWhatsAppHandler extends CommandHandler {


    public static final String MENSAJE = "mensaje";
    private final WhatsAppService whatsAppService;

    public EnviarWhatsAppHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("enviar whatsapp {mensaje}","enviar por whatsapp {mensaje}","enviar mensaje de whatsapp {mensaje}"), textToSpeech, context, commandHandlerManager);
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
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(MESSAGE, getExpressionMatcher(command).getValuesFromExpression(command).get(MENSAJE));
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
        if(input.equals("si") || input.equals("sí")) {
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


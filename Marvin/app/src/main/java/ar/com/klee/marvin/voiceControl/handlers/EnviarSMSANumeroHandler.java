package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarSMSANumeroHandler extends CommandHandler{

    public static final String NUMBERO = "numero";
    private static final String SET_NUMBER = "SET_NUMBER";
    private static final String NUMBER = "NUMBER";

    public EnviarSMSANumeroHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("enviar sms aL {numero}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Boolean setContact = context.getBoolean(SET_NUMBER);
        if(setContact) {
            context.put(NUMBER, context.getString(COMMAND));
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

        }
        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        commandHandlerContext.put(NUMBER, getExpressionMatcher().getValuesFromExpression(commandHandlerContext.getString(COMMAND)).get(NUMBERO));
        commandHandlerContext.put(SET_NUMBER, false);
    }

    //PRONUNCIA NUMBERO
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        String contact = context.getString(NUMBER);
        getTextToSpeech().speakText("¿Querés enviar un sms al numero " + contact + "?");
        context.put(SET_NUMBER, false);
        context.put(STEP, 3);
        return context;
    }

    //CONFIRMA NUMBERO
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Qué mensaje le querés mandar por sms?");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿A qué numero querés mandarle el sms?");
            context.put(SET_NUMBER, true);
            context.put(STEP, 1);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 3);
        return context;

    }

    //INGRESO MENSAJE
    public CommandHandlerContext stepFive(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        getTextToSpeech().speakText("¿Querés enviar por sms el mensaje " + input + "?");

        context.put(STEP, 7);
        return context;

    }

    //CONFIRMACION DE MENSAJE
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("Enviando sms");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje querés mandar?");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");
        context.put(STEP, 7);
        return context;

    }


}


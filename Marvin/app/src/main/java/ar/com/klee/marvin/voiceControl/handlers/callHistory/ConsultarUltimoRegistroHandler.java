package ar.com.klee.marvin.voiceControl.handlers.callHistory;

import android.content.Context;
import android.os.Handler;

import java.util.Arrays;

import ar.com.klee.marvin.activities.CallHistoryActivity;
import ar.com.klee.marvin.activities.SMSInboxActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class ConsultarUltimoRegistroHandler extends CommandHandler {

    public ConsultarUltimoRegistroHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("consultar último registro","consultar última llamada"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

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

        context.put(STEP,0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }

    //PRONUNCIA COMANDO Y SE LEE EL REGISTRO
    public CommandHandlerContext stepOne(CommandHandlerContext context){

        String message = context.getObject(ACTIVITY, CallHistoryActivity.class).getLastCall();

        getTextToSpeech().speakText("CALL - " + message + ". ¿Te gustaría llamar a ese número o enviarle un sms?");

        final CommandHandlerContext c = context;

        c.getObject(ACTIVITY, CallHistoryActivity.class).showCallDialog();
        c.getObject(ACTIVITY, CallHistoryActivity.class).disableButtons();

        context.put(STEP, 3);
        return context;
    }

    public CommandHandlerContext stepThree(final CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Querés llamar o enviar sms?");
            context.put(STEP, 3);
            return context;
        }

        if(input.equals("llamar")) {
            getTextToSpeech().speakText("Realizando llamada");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    context.getObject(ACTIVITY, CallHistoryActivity.class).call();
                }
            }, 1000);
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("responder")) {
            getTextToSpeech().speakText("¿Qué mensaje le querés mandar?");
            context.getObject(ACTIVITY,CallHistoryActivity.class).respond();
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("No se responderá");
            context.getObject(ACTIVITY,CallHistoryActivity.class).cancelDialog();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("No se responderá");
            context.getObject(ACTIVITY, CallHistoryActivity.class).cancelDialog();
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar llamar, responder o cancelar");

        context.put(STEP, 3);
        return context;

    }

    //INGRESO MENSAJE
    public CommandHandlerContext stepFive(CommandHandlerContext context){
        String input = context.getString(COMMAND);

        Character firstCharacter, newFirstCharacter;
        firstCharacter = input.charAt(0);
        newFirstCharacter = Character.toUpperCase(firstCharacter);
        input = input.replaceFirst(firstCharacter.toString(), newFirstCharacter.toString());

        context.getObject(ACTIVITY, CallHistoryActivity.class).setAnswer(input);
        context.getObject(ACTIVITY, CallHistoryActivity.class).disableButtonsRespond();
        getTextToSpeech().speakText("CALLR - ¿Querés responder el mensaje " + input + "?");
        context.put(STEP, 7);
        return context;
    }

    //CONFIRMACION DE MENSAJE
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText(context.getObject(ACTIVITY,CallHistoryActivity.class).respondMessage());
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.getObject(ACTIVITY,CallHistoryActivity.class).cancelDialog();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje querés responder?");
            context.getObject(ACTIVITY, CallHistoryActivity.class).setAnswer("");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");
        context.put(STEP, 7);
        return context;

    }

}

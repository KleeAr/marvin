package ar.com.klee.marvin.voiceControl.handlers.smsInbox;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.telephony.SmsManager;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.activities.SMSInboxActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class LeerUltimoSMSHandler extends CommandHandler {

    public LeerUltimoSMSHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("leer último sms", textToSpeech, context, commandHandlerManager);
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

    //PRONUNCIA COMANDO Y SE LEE EL MENSAJE
    public CommandHandlerContext stepOne(CommandHandlerContext context){

        String message = context.getObject(ACTIVITY, SMSInboxActivity.class).getLastMessage();

        int delayTime = (message.length()+58)/5 + 1;
        delayTime = delayTime * 550;

        getTextToSpeech().speakText(message + ". ¿Te gustaría llamar a ese número o enviarle un sms?");

        final CommandHandlerContext c = context;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                c.getObject(ACTIVITY, SMSInboxActivity.class).showCallDialog();
            }
        }, delayTime);

        context.put(STEP, 3);
        return context;
    }

    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Querés llamar o enviar sms?");
            context.put(STEP, 3);
            return context;
        }

        if(input.equals("llamar")) {
            getTextToSpeech().speakText("Realizando llamada");
            context.getObject(ACTIVITY, SMSInboxActivity.class).call();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("enviar sms")) {
            getTextToSpeech().speakText("¿Qué mensaje le querés mandar?");
            context.getObject(ACTIVITY,SMSInboxActivity.class).respond();
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("No se responderá");
            context.getObject(ACTIVITY,SMSInboxActivity.class).cancelDialog();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("No se responderá");
            context.getObject(ACTIVITY, SMSInboxActivity.class).cancelDialog();
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar llamar, enviar sms o cancelar");

        context.put(STEP, 3);
        return context;

    }

    //INGRESO MENSAJE
    public CommandHandlerContext stepFive(CommandHandlerContext context){
        String input = context.getString(COMMAND);

        Character firstCharacter, newFirstCharacter;
        firstCharacter = input.charAt(0);
        newFirstCharacter = Character.toUpperCase(firstCharacter);
        input = input.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        context.getObject(ACTIVITY, SMSInboxActivity.class).setAnswer(input);
        getTextToSpeech().speakText("¿Querés responder el mensaje " + input + "?");
        context.put(STEP, 7);
        return context;
    }

    //CONFIRMACION DE MENSAJE
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText(context.getObject(ACTIVITY,SMSInboxActivity.class).respondMessage());
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.getObject(ACTIVITY,SMSInboxActivity.class).cancelDialog();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje querés responder?");
            context.getObject(ACTIVITY, SMSInboxActivity.class).setAnswer("");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");
        context.put(STEP, 7);
        return context;

    }

}

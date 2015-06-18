package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarMailAContactoHandler extends CommandHandler{

    public static final String CONTACTO = "contacto";

    public EnviarMailAContactoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("enviar mail a {contacto}", textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Boolean setContact = context.getBoolean(SET_CONTACT);

        if(setContact) {
            context.put(CONTACT, context.getString(COMMAND));
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
        commandHandlerContext.put(CONTACT, getExpressionMatcher().getValuesFromExpression(commandHandlerContext.getString(COMMAND)).get(CONTACTO));
        commandHandlerContext.put(SET_CONTACT, false);
    }

    //PRONUNCIA CONTACTO
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        String contact = context.getString(CONTACT);
        getTextToSpeech().speakText("¿Querés enviar un mail al contacto " + contact + "?");

        context.put(SET_CONTACT, false);

        context.put(STEP, 3);
        return context;

    }

    //CONFIRMA CONTACTO
    public CommandHandlerContext stepThree(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Qué mensaje le querés mandar por mail?");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿A qué contacto querés mandarle el mail?");
            context.put(SET_CONTACT, true);
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
        getTextToSpeech().speakText("¿Querés enviar por mail el mensaje " + input + "?");

        context.put(STEP, 7);
        return context;

    }

    //CONFIRMACION DE MENSAJE
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("Enviando mail");
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
    //INDICA SI QUIERE AGREGAR ASUNTO
    public int stepNine(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("¿Qué asunto deseás agregar?");
            return 11;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("Enviando mail");
            sendMail();
            return 0;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 9;

    }

    //INDICA ASUNTO
    public int stepEleven(String input){

        textToSpeech.speakText("¿Querés enviar el asunto " + input + "?");

        subject = input;

        return 13;

    }

    //CONFIRMA ASUNTO
    public int stepThirteen(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("Enviando mail");
            sendMail();
            return 0;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Qué asunto deseás agregar?");
            return 11;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 13;

    }


    public void sendMail(){

        message = message + "\n\n\n" + "Mensaje enviado a través de MARVIN";
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, contact);
        //emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(Intent.createChooser(emailIntent, "Seleccionar cuenta de Email:"));

    }

}


package ar.com.klee.marvin.voiceControl.handlers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarMailAContactoHandler extends CommandHandler{

    public static final String CONTACTO = "contacto";
    private static final String SUBJECT = "SUBJECT";
    private static final String MAIL = "MAIL";

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
            case 9:
                return stepNine(context);
            case 11:
                return stepEleven(context);
            case 13:
                return stepThirteen(context);
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

        context.put(MAIL,validateContact(contact));

        if(context.getString(MAIL).equals("")){
            getTextToSpeech().speakText("El contacto " + contact + "no fue encontrado. ¿A quién querés mandarle el mail?");
            context.put(SET_CONTACT, true);
            context.put(STEP, 1);
            return context;
        }

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
        context.put(MESSAGE, input);
        context.put(STEP, 7);
        return context;

    }

    //CONFIRMACION DE MENSAJE
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Deseás agregar un asunto?");
            context.put(STEP, 9);
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
    public CommandHandlerContext stepNine(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Qué asunto deseás agregar?");
            return context.put(STEP, 11);
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            return context.put(STEP, 0);
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("Configurando mail. Seleccioná con qué cuenta querés enviarlo.");
            context.put(SUBJECT, "");
            sendMail(context);
            return context.put(STEP, 0);
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        return context.put(STEP, 9);
    }

    //INDICA ASUNTO
    public CommandHandlerContext stepEleven(CommandHandlerContext context){
        getTextToSpeech().speakText("¿Querés enviar el asunto " + context.getString(COMMAND) + "?");
        context.put(SUBJECT, context.getString(COMMAND));
        return context.put(STEP, 13);
    }

    //CONFIRMA ASUNTO
    public CommandHandlerContext stepThirteen(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("Configurando mail. Seleccioná con qué cuenta querés enviarlo.");
            sendMail(context);
            return context.put(STEP, 0);
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            return context.put(STEP, 0);
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué asunto deseás agregar?");
            return context.put(STEP, 11);
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        return context.put(STEP, 13);

    }


    public void sendMail(CommandHandlerContext context){

        Character firstCharacter, newFirstCharacter;
        String message = context.getString(MESSAGE) + "\n\n\n" + "Mensaje enviado a través de MARVIN";

        firstCharacter = message.charAt(0);
        newFirstCharacter = Character.toUpperCase(firstCharacter);
        message = message.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", context.getString(MAIL), null));
        //emailIntent.setType("message/rfc822");
        //emailIntent.putExtra(Intent.EXTRA_EMAIL, context.getString(MAIL));
        //emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(SUBJECT));
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getCommandHandlerManager().getMainActivity().startActivity(Intent.createChooser(emailIntent, "Seleccioná Cuenta de Email:"));

    }

    public String validateContact(String contact){

        List<HashMap<String,String>> contacts = new ArrayList<HashMap<String,String>>();

        String accents = "áéíóúÁÉÍÓÚ";
        String noAccents = "aeiouAEIOU";
        String contactWithoutAccent = contact;

        int i;

        for(i=0;i<accents.length();i++){

            contactWithoutAccent = contactWithoutAccent.replace(accents.charAt(i),noAccents.charAt(i));

        }

        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String mail = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        HashMap<String,String> data = new HashMap<String,String>();
                        data.put("NAME",name);
                        data.put("NUMBER",phoneNo);
                        data.put("MAIL",mail);
                        contacts.add(data);
                    }
                    pCur.close();
                }
            }

            i = 0;

            while(i < contacts.size()){
                if(contacts.get(i).get("NAME").toLowerCase().equals(contact) || contacts.get(i).get("NAME").toLowerCase().equals(contactWithoutAccent) )
                    return contacts.get(i).get("MAIL");

                i++;
            }

        }

        return "";

    }

}


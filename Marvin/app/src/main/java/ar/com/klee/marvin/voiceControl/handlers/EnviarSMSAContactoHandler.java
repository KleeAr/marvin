package ar.com.klee.marvin.voiceControl.handlers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarSMSAContactoHandler extends CommandHandler{

    public static final String CONTACTO = "contacto";
    public static final String NUMBER= "NUMBER";

    public EnviarSMSAContactoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("enviar sms a {contacto}", textToSpeech, context, commandHandlerManager);
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
        commandHandlerContext.put(NUMBER, "");

        commandHandlerContext.getObject(ACTIVITY, MainMenuActivity.class).displaySendSMS();
    }

    //PRONUNCIA CONTACTO
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        String contact = context.getString(CONTACT);

        context.put(NUMBER,validateContact(contact));

        if(context.getString(NUMBER).equals("")){
            getTextToSpeech().speakText("El contacto no fue encontrado en la lista. Reingresalo");
            context.put(SET_CONTACT, true);
            context.put(STEP, 1);
            return context;
        }

        context.getObject(ACTIVITY, MainMenuActivity.class).setNumber(context.getString(NUMBER));

        getTextToSpeech().speakText("¿Querés enviar un sms al contacto " + contact + "?");
        context.put(SET_CONTACT, false);
        context.put(STEP, 3);
        return context;
    }

    //CONFIRMA CONTACTO
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Qué mensaje le querés mandar por sms?");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.getObject(ACTIVITY,MainMenuActivity.class).cancelMessage();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿A qué contacto querés mandarle el sms?");
            context.getObject(ACTIVITY, MainMenuActivity.class).setNumber("");
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

        Character firstCharacter, newFirstCharacter;
        firstCharacter = input.charAt(0);
        newFirstCharacter = Character.toUpperCase(firstCharacter);
        input = input.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        context.getObject(ACTIVITY, MainMenuActivity.class).setMessageBody(input);
        getTextToSpeech().speakText("¿Querés enviar por sms el mensaje " + input + "?");
        context.put(STEP, 7);
        return context;
    }

    //CONFIRMACION DE MENSAJE
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText(context.getObject(ACTIVITY,MainMenuActivity.class).sendMessage());
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.getObject(ACTIVITY,MainMenuActivity.class).cancelMessage();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje querés mandar?");
            context.getObject(ACTIVITY, MainMenuActivity.class).setMessageBody("");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");
        context.put(STEP, 7);
        return context;

    }

    public String validateContact(String contact){

        List<HashMap<String,String>> contacts = new ArrayList<HashMap<String,String>>();

        String accents = "áéíóúÁÉÍÓÚ";
        String noAccents = "aeiouAEIOU";
        String contactWithoutAccent = contact;

        int i;

        for(i=0;i<accents.length();i++){

            contactWithoutAccent.replace(accents.charAt(i),noAccents.charAt(i));

        }

        Log.d("APP",contact);
        Log.d("APP",contactWithoutAccent);

        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
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
                        contacts.add(data);
                    }
                    pCur.close();
                }
            }

            i = 0;

            while(i < contacts.size()){
                if(contacts.get(i).get("NAME").toLowerCase().equals(contact) || contacts.get(i).get("NAME").toLowerCase().equals(contactWithoutAccent) )
                    return contacts.get(i).get("NUMBER");

                i++;
            }

        }

        return "";

    }

}


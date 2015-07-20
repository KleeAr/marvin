package ar.com.klee.marvin.voiceControl.handlers.callHistory;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import ar.com.klee.marvin.activities.CallHistoryActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class ConsultarUltimoRegistroDeNumeroHandler extends CommandHandler {

    public static final String NUMERO = "numero";
    private static final String SET_NUMBER = "SET_NUMBER";
    private static final String NUMBER = "NUMBER";

    public ConsultarUltimoRegistroDeNumeroHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("consultar último registro del número {numero}", textToSpeech, context, commandHandlerManager);
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

        context.put(STEP,0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        commandHandlerContext.put(NUMBER, getExpressionMatcher().getValuesFromExpression(commandHandlerContext.getString(COMMAND)).get(NUMERO));
        commandHandlerContext.put(SET_NUMBER, false);
    }

    //INGRESA NUMERO Y LEE MENSAJE
    public CommandHandlerContext stepOne(CommandHandlerContext context){

        String contact = context.getString(NUMBER);

        contact = contact.replace(" ","");

        try{

            Integer.parseInt(contact);

        }catch (NumberFormatException e){

            getTextToSpeech().speakText("No se indicó un número. Reingresalo");
            context.put(SET_NUMBER, true);
            context.put(STEP, 1);
            return context;

        }

        int i = 0;
        String contactWithSpaces = "";

        while(i < contact.length()){
            contactWithSpaces += contact.charAt(i) + " ";
            i++;
        }

        String message = context.getObject(ACTIVITY, CallHistoryActivity.class).getLastCallOfNumber(contact);

        if(message.equals("")){
            getTextToSpeech().speakText("No hay registros del número " + contactWithSpaces);
            context.put(SET_NUMBER, false);
            context.put(STEP, 0);
            return context;
        }

        int delayTime = (message.length()+58)/5 + 1;
        delayTime = delayTime * 550;

        getTextToSpeech().speakText( message + ". ¿Te gustaría llamar a ese número o enviarle un sms?");

        final CommandHandlerContext c = context;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                c.getObject(ACTIVITY, CallHistoryActivity.class).showCallDialog();
            }
        }, delayTime);

        context.put(SET_NUMBER, false);
        context.put(STEP, 3);
        return context;

    }

    //INDICA SI QUIERE HACER ALGO
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Querés llamar o enviar sms?");
            context.put(STEP, 3);
            return context;
        }

        if(input.equals("llamar")) {
            getTextToSpeech().speakText("Realizando llamada");
            context.getObject(ACTIVITY, CallHistoryActivity.class).call();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("enviar sms")) {
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

        context.getObject(ACTIVITY, CallHistoryActivity.class).setAnswer(input);
        getTextToSpeech().speakText("¿Querés responder el mensaje " + input + "?");
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

    public List<HashMap<String,String>> validateContact(String contact){

        List<HashMap<String,String>> contacts = new ArrayList<HashMap<String,String>>();
        List<HashMap<String,String>> suggestedContacts = new ArrayList<HashMap<String,String>>();

        String accents = "áéíóúÁÉÍÓÚ";
        String noAccents = "aeiouAEIOU";
        String contactWithoutAccent = contact;

        StringTokenizer stringTokenizerContact = new StringTokenizer(contact);
        String suggestedContact = stringTokenizerContact.nextToken();
        String suggestedContactWithoutAccent = suggestedContact;

        int i;

        for(i=0;i<accents.length();i++){

            contactWithoutAccent = contactWithoutAccent.replace(accents.charAt(i),noAccents.charAt(i));
            suggestedContactWithoutAccent = suggestedContactWithoutAccent.replace(accents.charAt(i),noAccents.charAt(i));

        }

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
                        StringTokenizer stringTokenizerName = new StringTokenizer(name);
                        String firstName = stringTokenizerName.nextToken().toLowerCase();

                        if(name.toLowerCase().equals(contact) || name.toLowerCase().equals(contactWithoutAccent)) {
                            data.put("NAME", name);
                            data.put("NUMBER", phoneNo);
                            contacts.add(data);
                        }else if(firstName.equals(suggestedContact) || firstName.equals(suggestedContactWithoutAccent)){
                            data.put("NAME", name);
                            data.put("NUMBER", phoneNo);
                            suggestedContacts.add(data);
                        }
                    }
                    pCur.close();
                }
            }
        }

        cur.close();

        if(contacts.size() != 0)
            return contacts;
        return suggestedContacts;

    }

    public String convertNumber(String input){

        switch(input){

            case "uno":
                return "1";
            case "dos":
                return "2";
            case "tres":
                return "3";
            case "cuatro":
                return "4";
            case "cinco":
                return "5";
            case "seis":
                return "6";
            case "siete":
                return "7";
            case "ocho":
                return "8";
            case "nueve":
                return "9";
            case "diez":
                return "10";
            case "once":
                return "11";
            case "doce":
                return "12";
            case "trece":
                return "13";
            case "catorce":
                return "14";
            case "quince":
                return "15";
            case "dieciseis":
                return "16";
            case "diecisiete":
                return "17";
            case "dieciocho":
                return "18";
            case "diecinueve":
                return "19";
            case "veinte":
                return "20";
            case "veintiuno":
                return "21";
            case "veintidos":
                return "22";
            case "veintitres":
                return "23";
            default:
                return input;
        }
    }

}

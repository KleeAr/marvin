package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class LlamarAContactoHandler extends CommandHandler {

    public static final String CONTACTO = "contacto";
    public static final String NUMBER= "NUMBER";
    public static final String NUMBERS = "NUMBERS";
    public static final String NAMES = "NAMES";
    public static final String SET_MATCHES = "SET_MATCHES";

    public LlamarAContactoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("llamar a {contacto}", textToSpeech, context, commandHandlerManager);
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

        }
        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(CONTACT, getExpressionMatcher(command).getValuesFromExpression(command).get(CONTACTO));
        commandHandlerContext.put(SET_CONTACT, false);
        commandHandlerContext.put(NUMBER, "");
        commandHandlerContext.put(SET_MATCHES, false);

        commandHandlerContext.getObject(ACTIVITY, MainMenuActivity.class).openCallDialog();
    }

    //PRONUNCIA CONTACTO
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        String contact = context.getString(CONTACT);

        if(!context.getBoolean(SET_MATCHES)) {
            List<HashMap<String, String>> matches;

            matches = validateContact(contact);

            if(matches.size() == 0){
                getTextToSpeech().speakText("El contacto " + contact + " no fue encontrado. ¿A quién querés llamar?");
                context.put(SET_CONTACT, true);
                context.put(STEP, 1);
                return context;
            }

            if(matches.size() == 1) {
                context.put(NUMBER,matches.get(0).get("NUMBER"));
                context.getObject(ACTIVITY, MainMenuActivity.class).setCallNumber(context.getString(NUMBER));
                getTextToSpeech().speakText("¿Querés llamar al contacto " + contact + "?");
                context.put(SET_CONTACT, false);
                context.put(STEP, 3);
                return context;
            }

            String message = "Se detectaron varios contactos. Indicá ";
            List<String> numbers = new ArrayList<>();
            List<String> names = new ArrayList<>();

            for(int i=1; i<=matches.size(); i++){
                message += ((Integer)i).toString() + " si es " + matches.get(i-1).get("NAME") + ". ";
                names.add(matches.get(i-1).get("NAME"));
                numbers.add(matches.get(i-1).get("NUMBER"));
            }
            getTextToSpeech().speakText(message);
            context.put(NUMBERS, numbers);
            context.put(NAMES, names);
            context.put(SET_MATCHES, true);
            context.put(SET_CONTACT, true);
            context.put(STEP, 1);
            return context;

        }else{

            int number;

            try{

                contact = convertNumber(contact);
                number = Integer.parseInt(contact);

            }catch(NumberFormatException e){

                String message = "Debés indicar un número. Indicá ";

                for(int i=1; i<=context.getObject("NAMES",List.class).size(); i++){
                    message += ((Integer)i).toString() + " si es " + context.getObject("NAMES",List.class).get(i-1) + ". ";
                }

                getTextToSpeech().speakText(message);
                context.put(SET_MATCHES, true);
                context.put(SET_CONTACT, true);
                context.put(STEP, 1);
                return context;

            }

            if(number<1 || number>context.getObject("NAMES",List.class).size()){

                String message = "Número incorrecto. Indicá ";

                for(int i=1; i<=context.getObject("NAMES",List.class).size(); i++){
                    message += ((Integer)i).toString() + " si es " + context.getObject("NAMES",List.class).get(i-1) + ". ";
                }

                getTextToSpeech().speakText(message);
                context.put(SET_MATCHES, true);
                context.put(SET_CONTACT, true);
                context.put(STEP, 1);
                return context;
            }

            context.put(NUMBER, context.getObject(NUMBERS,List.class).get(number-1));
            context.put(CONTACT, context.getObject(NAMES,List.class).get(number-1));
            context.getObject(ACTIVITY, MainMenuActivity.class).setCallNumber(context.getString(NUMBER));
            getTextToSpeech().speakText("¿Querés llamar al contacto " + context.getString(CONTACT) + "?");
            context.put(SET_CONTACT, false);
            context.put(SET_MATCHES, false);
            context.put(STEP, 3);
            return context;

        }
    }

    //CONFIRMA CONTACTO
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            context.getObject(ACTIVITY,MainMenuActivity.class).callNumber(context.getString(NUMBER));
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando llamada");
            context.getObject(ACTIVITY,MainMenuActivity.class).closeCallDialog();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿A qué contacto querés llamar?");
            context.getObject(ACTIVITY, MainMenuActivity.class).setCallNumber("");
            context.put(SET_CONTACT, true);
            context.put(STEP, 1);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        context.put(STEP, 3);
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


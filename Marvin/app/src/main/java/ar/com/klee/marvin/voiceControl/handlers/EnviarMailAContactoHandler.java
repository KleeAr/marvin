package ar.com.klee.marvin.voiceControl.handlers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarMailAContactoHandler extends CommandHandler{

    public static final String CONTACTO = "contacto";
    private static final String SUBJECT = "SUBJECT";
    private static final String MAIL = "MAIL";
    private static final String MAILS = "MAILS";
    private static final String NAMES = "NAMES";
    private static final String SET_MATCHES = "SET_MATCHES";

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
        commandHandlerContext.put(SET_MATCHES, false);
    }

    //PRONUNCIA CONTACTO
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        String contact = context.getString(CONTACT);

        if(!context.getBoolean(SET_MATCHES)) {
            List<HashMap<String, String>> matches;

            matches = validateContact(contact);

            if(matches.size() == 0){
                getTextToSpeech().speakText("El contacto " + contact + " no fue encontrado o no tiene un mail asociado. ¿A quién querés mandarle el mail?");
                context.put(SET_CONTACT, true);
                context.put(STEP, 1);
                return context;
            }

            if(matches.size() == 1) {
                context.put(MAIL,matches.get(0).get("MAIL"));
                getTextToSpeech().speakText("¿Querés enviar un mail al contacto " + contact + "?");
                context.put(SET_CONTACT, false);
                context.put(STEP, 3);
                return context;
            }

            String message = "Se detectaron varios contactos con mail asociado. Indicá ";
            List<String> mails = new ArrayList<>();
            List<String> names = new ArrayList<>();

            for(int i=1; i<=matches.size(); i++){
                message += ((Integer)i).toString() + " si es " + matches.get(i-1).get("NAME") + ". ";
                names.add(matches.get(i-1).get("NAME"));
                mails.add(matches.get(i-1).get("MAIL"));
            }
            getTextToSpeech().speakText(message);
            context.put(MAILS, mails);
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

            context.put(MAIL, context.getObject(MAILS,List.class).get(number-1));
            context.put(CONTACT, context.getObject(NAMES,List.class).get(number-1));
            getTextToSpeech().speakText("¿Querés enviar un mail al contacto " + context.getString(CONTACT) + "?");
            context.put(SET_CONTACT, false);
            context.put(SET_MATCHES, false);
            context.put(STEP, 3);
            return context;

        }
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

        String subject = context.getString(SUBJECT);
        firstCharacter = subject.charAt(0);
        newFirstCharacter = Character.toUpperCase(firstCharacter);
        subject = subject.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", context.getString(MAIL), null));
        //emailIntent.setType("message/rfc822");
        //emailIntent.putExtra(Intent.EXTRA_EMAIL, context.getString(MAIL));
        //emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getCommandHandlerManager().getMainActivity().startActivity(Intent.createChooser(emailIntent, "Seleccioná Cuenta de Email:"));

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

                    Cursor emails = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);

                    while (emails.moveToNext()) {

                        String mail = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                        HashMap<String,String> data = new HashMap<String,String>();
                        StringTokenizer stringTokenizerName = new StringTokenizer(name);
                        String firstName = stringTokenizerName.nextToken().toLowerCase();
                        if(name.toLowerCase().equals(contact) || name.toLowerCase().equals(contactWithoutAccent)) {
                            data.put("MAIL",mail);
                            contacts.add(data);
                        }else if(firstName.equals(suggestedContact) || firstName.equals(suggestedContactWithoutAccent)){
                            data.put("NAME", name);
                            data.put("MAIL",mail);
                            suggestedContacts.add(data);
                        }
                    }

                    emails.close();
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


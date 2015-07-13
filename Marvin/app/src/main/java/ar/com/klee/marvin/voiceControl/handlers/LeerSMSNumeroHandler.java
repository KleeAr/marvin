package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.activities.SMSInboxActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class LeerSMSNumeroHandler extends CommandHandler{

    public final String NUMERO = "numero";

    public LeerSMSNumeroHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("leer sms número {numero}", textToSpeech, context, commandHandlerManager);
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

        String number = getExpressionMatcher().getValuesFromExpression(context.getString(COMMAND)).get(NUMERO);
        int index;

        try{

            index = Integer.parseInt(number);

        }catch (NumberFormatException e){

            try{

                index = Integer.parseInt(convertNumber(number));

            }catch (NumberFormatException e2) {

                getTextToSpeech().speakText("No se indicó un número. Reingresalo");
                context.put(STEP, 1);
                return context;

            }
        }

        String message = context.getObject(ACTIVITY, SMSInboxActivity.class).getMessageNro(index-1);

        if(message.length() < 5){

            getTextToSpeech().speakText("El número indicado debe ser mayor a 0 y menor a " + message + ". Reingresalo");
            context.put(STEP, 1);
            return context;

        }

        getTextToSpeech().speakText(message + ". ¿Te gustaría llamar a ese número o responder el mensaje?");

        context.getObject(ACTIVITY, SMSInboxActivity.class).showCallDialog();

        context.put(STEP, 3);
        return context;
    }

    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Querés llamar o responder?");
            context.put(STEP, 3);
            return context;
        }

        if(input.equals("llamar")) {
            getTextToSpeech().speakText("Realizando llamada");
            context.getObject(ACTIVITY, SMSInboxActivity.class).call();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("responder")) {
            getTextToSpeech().speakText("¿Qué mensaje le querés mandar?");
            context.getObject(ACTIVITY,SMSInboxActivity.class).respond();
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("No se enviará respuesta");
            context.getObject(ACTIVITY,SMSInboxActivity.class).cancelDialog();
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("No se enviará respuesta");
            context.getObject(ACTIVITY, SMSInboxActivity.class).cancelDialog();
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

    public String convertNumber(String input){

        switch(input){

            case "cero":
                return "0";
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
            case "veintiuna":
                return "21";
            case "veintiuno":
                return "21";
            case "veintidos":
                return "22";
            case "veintitres":
                return "23";
            case "veinticuatro":
                return "24";
            case "veinticinco":
                return "25";
            case "veintiseis":
                return "26";
            case "veintisiete":
                return "27";
            case "veintiocho":
                return "28";
            case "veintinueve":
                return "29";
            case "treinta":
                return "30";
            case "treinta y uno":
                return "31";
            case "treinta y dos":
                return "32";
            case "treinta y tres":
                return "33";
            case "treinta y cuatro":
                return "34";
            case "treinta y cinco":
                return "35";
            case "treinta y seis":
                return "36";
            case "treinta y siete":
                return "37";
            case "treinta y ocho":
                return "38";
            case "treinta y nueve":
                return "39";
            case "cuarenta":
                return "40";
            case "cuarenta y uno":
                return "41";
            case "cuarenta y dos":
                return "42";
            case "cuarenta y tres":
                return "43";
            case "cuarenta y cuatro":
                return "44";
            case "cuarenta y cinco":
                return "45";
            case "cuarenta y seis":
                return "46";
            case "cuarenta y siete":
                return "47";
            case "cuarenta y ocho":
                return "48";
            case "cuarenta y nueve":
                return "49";
            case "cincuenta":
                return "50";
            case "cincuenta y uno":
                return "51";
            case "cincuenta y dos":
                return "52";
            case "cincuenta y tres":
                return "53";
            case "cincuenta y cuatro":
                return "54";
            case "cincuenta y cinco":
                return "55";
            case "cincuenta y seis":
                return "56";
            case "cincuenta y siete":
                return "57";
            case "cincuenta y ocho":
                return "58";
            case "cincuenta y nueve":
                return "59";
            default:
                return input;
        }
    }

}

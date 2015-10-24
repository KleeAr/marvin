package ar.com.klee.marvinSimulator.voiceControl.handlers.tripHistory;

import android.content.Context;

import java.util.Arrays;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;
import ar.com.klee.marvinSimulator.voiceControl.TTS;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvinSimulator.voiceControl.handlers.CommandHandlerContext;

public class AbrirViajeNumeroHandler extends CommandHandler {

    public static final String NUMERO = "numero";
    public static final String NUMBER = "NUMBER";
    public static final String SET_NUMBER = "SET_NUMBER";

    public AbrirViajeNumeroHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("abrir viaje número {numero}"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Integer step = context.getInteger(STEP);

        Boolean setContact = context.getBoolean(SET_NUMBER);
        if(setContact) {
            context.put(NUMBER, context.getString(COMMAND));
        }

        switch(step){

            case 1:
                return stepOne(context);

        }

        context.put(STEP, 0);
        return context;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(NUMBER, getExpressionMatcher(command).getValuesFromExpression(command).get(NUMERO));
        commandHandlerContext.put(SET_NUMBER, false);
    }

    //PRONUNCIA COMANDO Y SE LEE EL MENSAJE
    public CommandHandlerContext stepOne(CommandHandlerContext context){

        String number = context.getString(NUMBER);

        int index;

        try{

            index = Integer.parseInt(number);

        }catch (NumberFormatException e){

            try{

                index = Integer.parseInt(convertNumber(number));

            }catch (NumberFormatException e2) {

                getTextToSpeech().speakText("No se indicó un número. Reingresá sólo el valor numérico");
                context.put(SET_NUMBER, true);
                context.put(STEP, 1);
                return context;

            }
        }

        boolean result = ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).checkTripExistence("number - " + index);

        if(result) {
            getTextToSpeech().speakText("Abriendo el viaje número " + index);
            ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).openTrip("number - " + index);
        }else
            getTextToSpeech().speakText("No se encontró el viaje indicado");

        context.put(SET_NUMBER, false);
        context.put(STEP, 0);
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

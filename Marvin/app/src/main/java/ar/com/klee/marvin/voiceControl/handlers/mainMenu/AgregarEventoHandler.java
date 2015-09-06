package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.StringTokenizer;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.social.CalendarService;
import ar.com.klee.marvin.social.FacebookService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class AgregarEventoHandler extends CommandHandler {

    protected static final String SET_EVENT = "SET_EVENT";
    protected static final String EVENT = "EVENT";
    protected static final String DAY = "DAY";
    protected static final String MONTH = "MONTH";
    protected static final String YEAR = "YEAR";
    protected static final String HOUR = "HOUR";
    protected static final String MINUTE = "MINUTE";

    private CalendarService calendarService;

    public AgregarEventoHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("agregar evento {evento}", textToSpeech, context, commandHandlerManager);
        calendarService = new CalendarService(context);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Boolean setEvent = context.getBoolean(SET_EVENT);
        String input = context.getString(COMMAND);
        Integer step = context.getInteger(STEP);

        if(setEvent) {
            context.put(EVENT, input);
        }

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

        }

        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(EVENT, getExpressionMatcher(command).getValuesFromExpression(command).get("evento"));
        commandHandlerContext.put(SET_EVENT, false);
    }

    //PRONUNCIA EVENTO
    public CommandHandlerContext stepOne(CommandHandlerContext context){

        getTextToSpeech().speakText("¿Querés agregar al calendario el evento " + context.getString(EVENT) + "?");

        String textToPublish = context.getString(EVENT);

        Character firstCharacter, newFirstCharacter;

        firstCharacter = textToPublish.charAt(0);
        newFirstCharacter = Character.toUpperCase(firstCharacter);
        textToPublish = textToPublish.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        context.put(EVENT, textToPublish);

        context.put(SET_EVENT, false);
        context.put(STEP, 3);
        return context;

    }

    //CONFIRMA EVENTO
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿En qué fecha es el evento?");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando agregado de evento");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué evento deseás agregar?");
            context.put(SET_EVENT, true);
            context.put(STEP, 1);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");
        context.put(STEP, 3);
        return context;

    }

    //INDICA FECHA
    public CommandHandlerContext stepFive(CommandHandlerContext context){

        String input = context.getString(COMMAND);

        ExpressionMatcher dateFormat = new ExpressionMatcher("{day} de {month} del {year}");

        if(!dateFormat.matches(input)){

            getTextToSpeech().speakText("La fecha debe tener el formato: 13 de abril del 2016. Ingresala de nuevo");

            context.put(STEP, 5);
            return context;

        }

        Map<String, String> values = dateFormat.getValuesFromExpression(input);

        String dayStr, monthStr, yearStr;

        dayStr = values.get("day");
        monthStr = values.get("month");
        yearStr = values.get("year");

        if(dayStr.equals("primero")){
            dayStr = "1";
        }

        try{

            context.put(DAY, Integer.parseInt(dayStr));

        }catch(NumberFormatException e){

            getTextToSpeech().speakText("Debe indicar un número para el día");

            context.put(STEP, 5);
            return context;

        }

        StringTokenizer st = new StringTokenizer(yearStr);
        ArrayList<String> yearValues = new ArrayList<String>();

        while(st.hasMoreTokens()){
            yearValues.add(st.nextToken());
        }

        try{

            Integer year = 0;
            int i = 0;
            while(i != yearValues.size()) {
                year += Integer.parseInt(yearValues.get(i));
                i++;
            }
            context.put(YEAR, year);

            if(year>2099){
                getTextToSpeech().speakText("El anio debe ser menor a 2100");
                context.put(STEP, 5);
                return context;
            }else if(year<2015){
                getTextToSpeech().speakText("El anio debe ser mayor a 2014");
                context.put(STEP, 5);
                return context;
            }

        }catch(NumberFormatException e){

            getTextToSpeech().speakText("Debe indicar un número para el año");

            context.put(STEP, 5);
            return context;

        }

        context.put(MONTH,numericMonth(monthStr));

        if(context.getInteger(MONTH) == 0){

            getTextToSpeech().speakText("La fecha debe tener el formato: 13 de abril del 2016. Ingresala de nuevo");

            context.put(STEP, 5);
            return context;

        }

        if(!validateDate(context.getInteger(DAY), context.getInteger(MONTH), context.getInteger(YEAR))){

            getTextToSpeech().speakText("La fecha indicada es inválida. Volvé a ingresarla");

            context.put(STEP, 5);
            return context;

        }

        getTextToSpeech().speakText("¿El evento es el " + input + "?");

        context.put(STEP, 7);
        return context;
    }

    //CONFIRMA FECHA
    public CommandHandlerContext stepSeven(CommandHandlerContext context) {
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿A qué hora es el evento?");
            context.put(STEP, 9);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando agregado de evento");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿En qué fecha es el evento?");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");
        context.put(STEP, 7);
        return context;

    }

    //INGRESA HORA
    public CommandHandlerContext stepNine(CommandHandlerContext context) {

        String input = context.getString(COMMAND);

        ExpressionMatcher hourFormat = new ExpressionMatcher("{hour} y {minute}");
        ExpressionMatcher hourOClockFormat = new ExpressionMatcher("{hour} horas");

        if(!hourFormat.matches(input) && !hourOClockFormat.matches(input)){

            getTextToSpeech().speakText("Debe indicar la hora como sigue: 22 y 15, o 13 horas, si es puntual");

            context.put(STEP, 9);
            return context;

        }

        Map<String, String> values;
        String hourStr, minuteStr = "";

        boolean isOClock = false;

        if(hourFormat.matches(input)) {
            values = hourFormat.getValuesFromExpression(input);
            hourStr = values.get("hour");
            minuteStr = values.get("minute");

            if(minuteStr.equals("cuarto"))
                minuteStr = "15";
            else if(minuteStr.equals("media"))
                minuteStr = "30";

        }else {
            values = hourOClockFormat.getValuesFromExpression(input);
            hourStr = values.get("hour");
            isOClock = true;
        }

        if(hourStr.equals("ciro")) {
            hourStr = "0";
            input = input.replace("ciro","0");
        }else if(hourStr.equals("una"))
            hourStr = "1";



        boolean isOne = false;

        if(hourStr.equals("1"))
            isOne = true;

        try{

            context.put(HOUR, Integer.parseInt(hourStr));

        }catch(NumberFormatException e){

            hourStr = convertHour(hourStr);

            try{

                context.put(HOUR, Integer.parseInt(hourStr));

            }catch (NumberFormatException e2){

                getTextToSpeech().speakText("Debe indicar un número para la hora");

                context.put(STEP, 9);
                return context;

            }

        }

        if(!isOClock) {
            try {

                context.put(MINUTE, Integer.parseInt(minuteStr));

            } catch (NumberFormatException e) {

                minuteStr = convertMinute(minuteStr);

                try{

                    context.put(MINUTE, Integer.parseInt(minuteStr));

                }catch (NumberFormatException e2) {

                    getTextToSpeech().speakText("Debe indicar un número para el minuto");

                    context.put(STEP, 9);
                    return context;

                }

            }
        }else{
            context.put(MINUTE, 0);
        }

        if(!validateHour(context.getInteger(HOUR),context.getInteger(MINUTE))){

            getTextToSpeech().speakText("Indicaste una hora inválida");

            context.put(STEP, 9);
            return context;

        }

        if(!isOne)
            getTextToSpeech().speakText("¿El evento es a las " + input + "?");
        else
            if(isOClock)
                getTextToSpeech().speakText("¿El evento es a la una horas?");
            else
                getTextToSpeech().speakText("¿El evento es a la una y " + minuteStr + "?");

        context.put(STEP, 11);
        return context;

    }

    //CONFIRMA HORA
    public CommandHandlerContext stepEleven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("Agregando evento en el calendario");
            createGoogleCalendarEvent(context);
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando agregado de evento");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿A qué hora es el evento?");
            context.put(STEP, 9);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 11);
        return context;

    }

    public void createGoogleCalendarEvent(CommandHandlerContext context){

        calendarService.createEvent(context.getString(EVENT),
                context.getInteger(DAY), context.getInteger(MONTH), context.getInteger(YEAR),
                context.getInteger(HOUR), context.getInteger(MINUTE));

    }


    public int numericMonth(String monthStr){

        monthStr = monthStr.toLowerCase();

        switch(monthStr){

            case "enero":
                return 1;
            case "febrero":
                return 2;
            case "marzo":
                return 3;
            case "abril":
                return 4;
            case "mayo":
                return 5;
            case "junio":
                return 6;
            case "julio":
                return 7;
            case "agosto":
                return 8;
            case "septiembre":
                return 9;
            case "setiembre":
                return 9;
            case "octubre":
                return 10;
            case "noviembre":
                return 11;
            case "diciembre":
                return 12;

        }

        return 0;
    }

    public boolean validateDate(int inputDay, int inputMonth, int inputYear){

        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setLenient(false);        // must do this
            gc.set(GregorianCalendar.YEAR, inputYear);
            gc.set(GregorianCalendar.MONTH, inputMonth);
            gc.set(GregorianCalendar.DATE, inputDay);

            gc.getTime(); // exception thrown here

            switch(inputMonth){

                case 1:
                    if(inputDay>31 || inputDay<1)
                        return false;
                    break;
                case 2:
                    boolean isLeapYear = ((inputYear % 4 == 0) && (inputYear % 100 != 0) || (inputYear % 400 == 0));
                    if(!isLeapYear) {
                        if (inputDay > 28 || inputDay < 1)
                            return false;
                    }else{
                        if (inputDay > 29 || inputDay < 1)
                            return false;
                    }
                    break;
                case 3:
                    if(inputDay>31 || inputDay<1)
                        return false;
                    break;
                case 4:
                    if(inputDay>30 || inputDay<1)
                        return false;
                    break;
                case 5:
                    if(inputDay>31 || inputDay<1)
                        return false;
                    break;
                case 6:
                    if(inputDay>30 || inputDay<1)
                        return false;
                    break;
                case 7:
                    if(inputDay>31 || inputDay<1)
                        return false;
                    break;
                case 8:
                    if(inputDay>31 || inputDay<1)
                        return false;
                    break;
                case 9:
                    if(inputDay>30 || inputDay<1)
                        return false;
                    break;
                case 10:
                    if(inputDay>31 || inputDay<1)
                        return false;
                    break;
                case 11:
                    if(inputDay>30 || inputDay<1)
                        return false;
                    break;
                case 12:
                    if(inputDay>31 || inputDay<1)
                        return false;
                    break;
            }

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();

            return false;
        }

    }

    public String convertHour(String input){

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
            default:
                return input;
        }
    }

    public String convertMinute(String input){

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

    public boolean validateHour(int hour, int minute){

        if(minute>=0 && minute<=59 && hour>=0 && hour<=23)
            return true;

        return false;

    }
}


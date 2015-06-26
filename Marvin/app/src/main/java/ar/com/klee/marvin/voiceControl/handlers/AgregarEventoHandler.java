package ar.com.klee.marvin.voiceControl.handlers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.social.CalendarService;
import ar.com.klee.marvin.social.FacebookService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class AgregarEventoHandler extends CommandHandler{

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
        commandHandlerContext.put(EVENT, getExpressionMatcher().getValuesFromExpression(commandHandlerContext.getString(COMMAND)).get("evento"));
        commandHandlerContext.put(SET_EVENT, false);
    }

    //PRONUNCIA EVENTO
    public CommandHandlerContext stepOne(CommandHandlerContext context){

        getTextToSpeech().speakText("¿Querés agregar el evento " + context.getString(EVENT) + "?");

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

        Log.d("DATE",input);

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

        try{

            context.put(DAY, Integer.parseInt(dayStr));

        }catch(NumberFormatException e){

            getTextToSpeech().speakText("Debe indicar un número para el día");

            context.put(STEP, 5);
            return context;

        }

        try{

            context.put(YEAR, Integer.parseInt(yearStr));

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

        context.put(DAY,input);
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

        Log.d("HOUR",input);

        ExpressionMatcher hourFormat = new ExpressionMatcher("{hour} y {minute}");
        ExpressionMatcher hourOClockFormat = new ExpressionMatcher("{hour} horas");

        if(!hourFormat.matches(input) && !hourOClockFormat.matches(input)){

            getTextToSpeech().speakText("Debe indicar la hora como sigue: 22 y 15, o 13 horas si es puntual");

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
        }else {
            values = hourOClockFormat.getValuesFromExpression(input);
            hourStr = values.get("hour");
            isOClock = true;
        }

        hourStr = hourStr.toLowerCase();

        if(hourStr.equals("una"))
            hourStr = "1";

        try{

            context.put(HOUR, Integer.parseInt(hourStr));

        }catch(NumberFormatException e){

            getTextToSpeech().speakText("Debe indicar un número para la hora");

            context.put(STEP, 9);
            return context;

        }

        if(!isOClock) {
            try {

                context.put(MINUTE, Integer.parseInt(minuteStr));

            } catch (NumberFormatException e) {

                getTextToSpeech().speakText("Debe indicar un número para el minuto");

                context.put(STEP, 9);
                return context;

            }
        }else{
            context.put(MINUTE, 0);
        }

        if(!validateHour(context.getInteger(HOUR),context.getInteger(MINUTE))){

            getTextToSpeech().speakText("Indicaste una hora inválida");

            context.put(STEP, 9);
            return context;

        }

        getTextToSpeech().speakText("¿El evento es a las " + input + "?");

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

    public boolean validateHour(int hour, int minute){

        if(minute>=0 && minute<=59 && hour>=0 && hour<=23)
            return true;

        return false;

    }
}


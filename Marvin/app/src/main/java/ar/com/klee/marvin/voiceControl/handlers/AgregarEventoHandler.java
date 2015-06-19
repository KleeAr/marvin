package ar.com.klee.marvin.voiceControl.handlers;

import android.app.Activity;
import android.content.Context;

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
    protected static final String HOUR = "HOUR";

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

        getTextToSpeech().speakText("¿Querés publicar " + context.getString(EVENT) + " en el muro?");

        context.put(SET_EVENT, false);
        context.put(STEP, 3);
        return context;

    }

    //CONFIRMA EVENTO
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿En qué fecha es el evento?");
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando agregado de evento");
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué evento deseás crear?");
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

        /*
        VALIDAR FECHA
         */
        String input = context.getString(COMMAND);
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
        getTextToSpeech().speakText("¿El evento es las " + input + "?");
        context.put(HOUR,input);
        context.put(STEP, 11);
        return context;

    }

    //CONFIRMA HORA
    public CommandHandlerContext stepEleven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("Agregando evento en el calendario");
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

    public void createGoogleCalendarEvent(int year, int month, int day){

        calendarService.createEvent(year, month, day);

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


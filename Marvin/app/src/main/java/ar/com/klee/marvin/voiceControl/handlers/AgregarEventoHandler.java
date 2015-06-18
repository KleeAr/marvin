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
import ar.com.klee.marvin.voiceControl.TTS;

public class AgregarEventoHandler extends CommandHandler{

    private static final String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    private ExpressionMatcher expressionMatcher;
    private String command;
    private String event;
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;
    private boolean setEvent = false;
    private TTS textToSpeech;

    private CalendarService calendarService;

    public AgregarEventoHandler(String command, TTS textToSpeech, Context context){

        expressionMatcher = new ExpressionMatcher("agregar evento {evento}");

        this.command = command;
        this.calendarService = new CalendarService(context);
        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){

        Map<String, String> values = expressionMatcher.getValuesFromExpression(command);

        event = values.get("evento");

        return expressionMatcher.matches(command);

    }

    public int drive(int step, String input){

        if(setEvent)
            event = input;

        switch(step){

            case 1:
                return stepOne();
            case 3:
                return stepThree(input);
            case 5:
                return stepFive(input);
            case 7:
                return stepSeven(input);
            case 9:
                return stepNine(input);
            case 11:
                return stepEleven(input);

        }

        return 0;

    }

    //PRONUNCIA EVENTO
    public int stepOne(){

        textToSpeech.speakText("¿Querés publicar " + event + " en el muro?");

        setEvent = false;

        return 3;

    }

    //CONFIRMA EVENTO
    public int stepThree(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("¿En qué fecha es el evento?");
            return 5;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando agregado de evento");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Qué evento deseás crear?");
            setEvent = true;
            return 1;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 3;

    }

    //INDICA FECHA
    public int stepFive(String input){

        ExpressionMatcher dateFormat = new ExpressionMatcher("{day} de {month} del {year}");

        if(!dateFormat.matches(input)){

            textToSpeech.speakText("Debe indicar la fecha con el formato: 13 de abril del 2016");

            return 5;

        }

        Map<String, String> values = dateFormat.getValuesFromExpression(input);

        String dayStr, monthStr, yearStr;

        dayStr = values.get("day");
        monthStr = values.get("month");
        yearStr = values.get("year");

        try{

            day = Integer.parseInt(dayStr);

        }catch(NumberFormatException e){

            textToSpeech.speakText("Debe indicar un número para el día");

            return 5;

        }

        try{

            year = Integer.parseInt(yearStr);

        }catch(NumberFormatException e){

            textToSpeech.speakText("Debe indicar un número para el año");

            return 5;

        }

        month = numericMonth(monthStr);

        if(month == 0){

            textToSpeech.speakText("Debe indicar la fecha con el formato: 13 de abril del 2016");

            return 5;

        }

        if(!validateDate(day, month, year)){

            textToSpeech.speakText("La fecha indicada es inválida");

            return 5;

        }

        textToSpeech.speakText("¿El evento es el "+input+"?");

        return 7;
    }

    //CONFIRMA FECHA
    public int stepSeven(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("¿A qué hora es el evento?");
            return 9;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando agregado de evento");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿En qué fecha es el evento?");
            return 5;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 7;

    }

    //INGRESA HORA
    public int stepNine(String input){

        ExpressionMatcher hourFormat = new ExpressionMatcher("{hour} y {minute}");

        if(!hourFormat.matches(input)){

            textToSpeech.speakText("Debe indicar la hora como el siguiente ejemplo: 22 y 15");

            return 9;

        }

        Map<String, String> values = hourFormat.getValuesFromExpression(input);

        String hourStr, minuteStr;

        hourStr = values.get("hour");
        minuteStr = values.get("minute");

        try{

            hour = Integer.parseInt(hourStr);

        }catch(NumberFormatException e){

            textToSpeech.speakText("Debe indicar un número para la hora");

            return 9;

        }

        try{

            minute = Integer.parseInt(minuteStr);

        }catch(NumberFormatException e){

            textToSpeech.speakText("Debe indicar un número para el minuto");

            return 9;

        }

        if(!validateHour(hour,minute)){

            textToSpeech.speakText("Indicaste una hora incorrecta");

            return 9;

        }

        textToSpeech.speakText("¿El evento es las "+input+"?");

        return 11;

    }

    //CONFIRMA HORA
    public int stepEleven(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("Agregando evento en el calendario");
            createGoogleCalendarEvent();
            return 0;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando agregado de evento");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿A qué hora es el evento?");
            return 9;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 11;

    }

    public void createGoogleCalendarEvent(){

        Log.d("MVN_", event + " " + year + "/" + month + "/" + day + " - " + hour + ":" + minute);

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


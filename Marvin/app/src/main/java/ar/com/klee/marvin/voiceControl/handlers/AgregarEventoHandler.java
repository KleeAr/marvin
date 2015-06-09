package ar.com.klee.marvin.voiceControl.handlers;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.social.CalendarService;
import ar.com.klee.marvin.social.FacebookService;
import ar.com.klee.marvin.voiceControl.TTS;

public class AgregarEventoHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private String event;
    private String day;
    private String hour;
    private boolean setEvent = false;
    private TTS textToSpeech;

    private CalendarService calendarService;

    public AgregarEventoHandler(String command, TTS textToSpeech, Activity activity){

        expressionMatcher = new ExpressionMatcher("agregar evento {evento}");

        this.command = command;
        this.calendarService = new CalendarService(activity);
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

        if(input.equals("sí")) {
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

        /*
        VALIDAR FECHA
         */

        textToSpeech.speakText("¿El evento es el "+input+"?");

        day = input;

        return 7;
    }

    //CONFIRMA FECHA
    public int stepSeven(String input){

        if(input.equals("sí")) {
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

        textToSpeech.speakText("¿El evento es las "+input+"?");

        hour = input;

        return 11;

    }

    //CONFIRMA HORA
    public int stepEleven(String input){

        if(input.equals("sí")) {
            textToSpeech.speakText("Agregando evento en el calendario");
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

    public void createGoogleCalendarEvent(int year, int month, int day){

        calendarService.createEvent(year, month, day);

    }
}


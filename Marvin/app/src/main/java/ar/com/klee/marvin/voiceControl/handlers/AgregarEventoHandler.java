package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import ar.com.klee.marvin.social.CalendarService;
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

        Boolean setEvent = context.get(SET_EVENT, Boolean.class);
        String input = context.get(COMMAND, String.class);
        Integer step = context.get(STEP, Integer.class);

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
        commandHandlerContext.put(EVENT, getExpressionMatcher().getValuesFromExpression(commandHandlerContext.get(COMMAND, String.class)).get("evento"));
        commandHandlerContext.put(SET_EVENT, false);
    }

    //PRONUNCIA EVENTO
    public CommandHandlerContext stepOne(CommandHandlerContext context){

        getTextToSpeech().speakText("¿Querés publicar " + context.get(EVENT, String.class) + " en el muro?");

        context.put(SET_EVENT, false);
        context.put(STEP, 3);
        return context;

    }

    //CONFIRMA EVENTO
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.get(COMMAND, String.class);
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
        String input = context.get(COMMAND, String.class);
        getTextToSpeech().speakText("¿El evento es el " + input + "?");

        context.put(DAY,input);
        context.put(STEP, 7);
        return context;
    }

    //CONFIRMA FECHA
    public CommandHandlerContext stepSeven(CommandHandlerContext context) {
        String input = context.get(COMMAND, String.class);
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
        String input = context.get(COMMAND, String.class);
        getTextToSpeech().speakText("¿El evento es las " + input + "?");
        context.put(HOUR,input);
        context.put(STEP, 11);
        return context;

    }

    //CONFIRMA HORA
    public CommandHandlerContext stepEleven(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
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
}


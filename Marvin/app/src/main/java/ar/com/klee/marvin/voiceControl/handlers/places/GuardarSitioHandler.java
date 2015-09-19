package ar.com.klee.marvin.voiceControl.handlers.places;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class GuardarSitioHandler extends CommandHandler {

    public static final String SITIO = "sitio";
    public static final String SITE = "SITE";
    public static final String SET_SITE = "SET_SITE";

    public GuardarSitioHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("guardar sitio {sitio}"), textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        Boolean setContact = context.getBoolean(SET_SITE);
        if(setContact) {
            context.put(SITE, context.getString(COMMAND));
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

        }
        context.put(STEP, 0);
        return context;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(SITE, getExpressionMatcher(command).getValuesFromExpression(command).get(SITIO));
        commandHandlerContext.put(SET_SITE, false);
    }

    //PRONUNCIA CONTACTO
    public CommandHandlerContext stepOne(CommandHandlerContext context){
        String site = context.getString(SITE);

        getCommandHandlerManager().getTextToSpeech().speakText("¿Quéres guardar el sitio " + site + "?");

        context.put(STEP, 3);
        return context;

    }

    //CONFIRMA CONTACTO
    public CommandHandlerContext stepThree(CommandHandlerContext context){

        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            getTextToSpeech().speakText("Indicá la dirección del sitio");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando guardado de sitio");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("Indicá el sitio a guardar");
            context.put(SET_SITE, true);
            context.put(STEP, 1);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        context.put(STEP, 3);
        return context;

    }

    //INGRESO MENSAJE
    public CommandHandlerContext stepFive(CommandHandlerContext context){
        String input = context.getString(COMMAND);

        context.put("ADDRESS",input);

        getTextToSpeech().speakText("¿La dirección es " + input + "?");
        context.put(STEP, 7);
        return context;
    }

    //CONFIRMACION DE MENSAJE
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        if(input.equals("si")) {
            int result = ((MainMenuActivity)getCommandHandlerManager().getMainActivity()).addNewSite(context.getString("ADDRESS"),context.getString(SITE));
            if(result == 0)
                getTextToSpeech().speakText("Guardando sitio");
            else if(result == 1)
                getTextToSpeech().speakText("Seleccioná la imagen del lugar");
            else if(result == 2)
                getTextToSpeech().speakText("El sitio ya existe");
            else if(result == 3)
                getTextToSpeech().speakText("La dirección indicada es inválida");
            else

            context.put(STEP, 0);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando envío");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("Indicá la dirección del sitio");
            context.put(STEP, 5);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");
        context.put(STEP, 7);
        return context;

    }

}


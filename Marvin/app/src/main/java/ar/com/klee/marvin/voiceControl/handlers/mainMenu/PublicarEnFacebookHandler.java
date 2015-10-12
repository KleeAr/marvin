package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import ar.com.klee.marvin.social.FacebookService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

@SuppressWarnings(value = "unchecked")
public class PublicarEnFacebookHandler extends CommandHandler {

    private static final String FACEBOOK_HASHTAGS = "FACEBOOK_HASHTAGS";
    public static final String MENSAJE = "mensaje";

    private FacebookService facebookService;

    public PublicarEnFacebookHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){

        super(Arrays.asList("publicar en facebook {mensaje}","publicar en muro {mensaje}","publicar en el muro de facebook {mensaje}","postear en facebook {mensaje}","postear en muro {mensaje}","postear en el muro de facebook {mensaje}"), textToSpeech, context, commandHandlerManager);
        facebookService = new FacebookService(context);
    }

    @Override
    public CommandHandlerContext drive(CommandHandlerContext currentContext){

        Boolean setMessage = currentContext.getBoolean(SET_MESSAGE);
        String input = currentContext.getString(COMMAND);
        if(setMessage) {
            currentContext.put(MESSAGE, input);
        }

        Integer step = currentContext.getInteger(STEP);

        switch(step){

            case 1:
                return stepOne(currentContext);
            case 3:
                return stepThree(currentContext);
            case 5:
                return stepFive(currentContext);
            case 7:
                return stepSeven(currentContext);
            case 9:
                return stepNine(currentContext);
            case 11:
                return stepEleven(currentContext);
        }

        currentContext.put(STEP, 0);
        return currentContext;

    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        commandHandlerContext.put(SET_MESSAGE, false);
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(MESSAGE, getExpressionMatcher(command).getValuesFromExpression(command).get(MENSAJE));
    }

    //PRONUNCIA COMANDO
    public CommandHandlerContext stepOne(CommandHandlerContext currentContext){
        getTextToSpeech().speakText("¿Querés publicar en el muro " + currentContext.getString(MESSAGE) + " ?");
        currentContext.put(SET_MESSAGE, false);
        currentContext.put(STEP, 3);
        currentContext.put(FACEBOOK_HASHTAGS, new ArrayList<String>());
        return currentContext;
    }

    //CONFIRMA MENSAJE
    public CommandHandlerContext stepThree(CommandHandlerContext currentContext){

        String input = currentContext.getString(COMMAND);

        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("¿Querés agregar un hashtag?");
            currentContext.put(STEP, 5);
            return currentContext;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            currentContext.put(STEP, 0);
            return currentContext;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje deseás publicar?");
            currentContext.put(SET_MESSAGE, true);
            currentContext.put(STEP, 1);
            return currentContext;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");
        currentContext.put(STEP, 3);
        return currentContext;

    }

    //INDICA SI SE QUIERE AGREGAR UN HASHTAG
    public CommandHandlerContext stepFive(CommandHandlerContext currentContext){

        String input = currentContext.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            currentContext.put(STEP, 7);
            return currentContext;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            currentContext.put(STEP, 0);
            return currentContext;
        }

        if(input.equals("no")){
            try {
                publishOnFacebook(currentContext);
                getTextToSpeech().speakText("Publicando en el muro de Facebook");
            }catch(Exception e){
                getTextToSpeech().speakText("No se pudo realizar la publicación. Recordá registrar tu cuenta en el menú perfil");
                e.printStackTrace();
            }
            currentContext.put(STEP, 0);
            return currentContext;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        currentContext.put(STEP, 5);
        return currentContext;
    }

    //INGRESA HASHTAG
    public CommandHandlerContext stepSeven(CommandHandlerContext currentContext){
        String input = currentContext.getString(COMMAND);
        getTextToSpeech().speakText("¿Querés agregar el hashtag " + input + "?");

        List<String> hashtags = currentContext.getList(FACEBOOK_HASHTAGS, String.class);
        hashtags.add(input);

        currentContext.put(STEP, 9);
        return currentContext;
    }

    //CONFIRMA HASHTAG
    public CommandHandlerContext stepNine(CommandHandlerContext currentContext){

        String input = currentContext.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("¿Querés agregar otro hashtag?");
            currentContext.put(STEP, 11);
            return currentContext;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            currentContext.put(STEP, 0);
            return currentContext;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            List<String> hashtags = currentContext.getList(FACEBOOK_HASHTAGS, String.class);
            hashtags.remove(hashtags.size() - 1);
            currentContext.put(STEP, 7);
            return currentContext;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        currentContext.put(STEP, 9);
        return currentContext;

    }

    //INDICA SI SE QUIERE AGREGAR OTRO HASHTAG
    public CommandHandlerContext stepEleven(CommandHandlerContext currentContext){

        String input = currentContext.getString(COMMAND);
        if(input.equals("si") || input.equals("sí")) {
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            currentContext.put(STEP, 7);
            return currentContext;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            currentContext.put(STEP, 0);
            return currentContext;
        }

        if(input.equals("no")){
            try {
                publishOnFacebook(currentContext);
                getTextToSpeech().speakText("Publicando en el muro de Facebook");
            }catch(Exception e){
                getTextToSpeech().speakText("No se pudo realizar la publicación. Recordá registrar tu cuenta en el menú perfil");
                e.printStackTrace();
            }
            currentContext.put(STEP, 0);
            return currentContext;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        currentContext.put(STEP, 11);
        return currentContext;

    }

    public void publishOnFacebook(CommandHandlerContext currentContext) {

        Character firstCharacter, newFirstCharacter;

        String textToPublish = currentContext.getString(MESSAGE);
        List<String> hashtags = currentContext.getList(FACEBOOK_HASHTAGS, String.class);

        firstCharacter = textToPublish.charAt(0);
        newFirstCharacter = Character.toUpperCase(firstCharacter);
        textToPublish = textToPublish.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        int i=0;

        while(i != hashtags.size()){

            textToPublish = textToPublish + " #";

            String hashtag = hashtags.get(i).toLowerCase();

            String word;

            StringTokenizer stringTokenizer = new StringTokenizer(hashtag);

            while(stringTokenizer.hasMoreTokens()){

                word = stringTokenizer.nextToken();

                firstCharacter = word.charAt(0);
                newFirstCharacter = Character.toUpperCase(firstCharacter);
                word = word.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

                textToPublish = textToPublish + word;

            }

            i++;

        }

        facebookService.publishText(textToPublish);

    }
}


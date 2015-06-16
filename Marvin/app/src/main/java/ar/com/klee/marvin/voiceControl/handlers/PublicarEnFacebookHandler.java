package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import ar.com.klee.marvin.social.FacebookService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

@SuppressWarnings(value = "unchecked")
public class PublicarEnFacebookHandler extends CommandHandler{

    private static final String FACEBOOK_HASHTAGS = "FACEBOOK_HASHTAGS";

    private FacebookService facebookService;

    public PublicarEnFacebookHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager){

        super("publicar en facebook {mensaje}", textToSpeech, context, commandHandlerManager);
        facebookService = new FacebookService(context);
    }

    @Override
    public CommandHandlerContext drive(CommandHandlerContext currentContext){

        Map<String, String> values = getExpressionMatcher().getValuesFromExpression(currentContext.get(COMMAND, String.class));

        currentContext.put(MESSAGE, values.get("mensaje"));

        Boolean setMessage = currentContext.get(SET_MESSAGE, Boolean.class);
        String input = currentContext.get(INPUT, String.class);
        if(setMessage != null && setMessage) {
            currentContext.put(MESSAGE, input);
        }

        Integer step = currentContext.get(STEP, Integer.class);

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

    //PRONUNCIA COMANDO
    public CommandHandlerContext stepOne(CommandHandlerContext currentContext){

        getTextToSpeech().speakText("¿Querés publicar en el muro " + currentContext.get(INPUT, String.class) + " ?");

        currentContext.put(SET_MESSAGE, false);
        currentContext.put(STEP, 3);
        return currentContext;

    }

    //CONFIRMA MENSAJE
    public CommandHandlerContext stepThree(CommandHandlerContext currentContext){

        String input = currentContext.get(INPUT, String.class);

        if(input.equals("si")) {
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

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");
        currentContext.put(STEP, 3);
        return currentContext;

    }

    //INDICA SI SE QUIERE AGREGAR UN HASHTAG
    public CommandHandlerContext stepFive(CommandHandlerContext currentContext){

        String input = currentContext.get(INPUT, String.class);
        if(input.equals("si")) {
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
            getTextToSpeech().speakText("Publicando en el muro de Facebook");
            currentContext.put(STEP, 0);
            return currentContext;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        currentContext.put(STEP, 5);
        return currentContext;
    }

    //INGRESA HASHTAG
    public CommandHandlerContext stepSeven(CommandHandlerContext currentContext){
        String input = currentContext.get(INPUT, String.class);
        getTextToSpeech().speakText("¿Querés agregar el hashtag " + input + "?");

        if(!currentContext.containsKey(FACEBOOK_HASHTAGS)) {
            List<String> hashtags = new ArrayList<>();
            currentContext.put(FACEBOOK_HASHTAGS, hashtags);
        }
        List<String> hashtags = currentContext.get(FACEBOOK_HASHTAGS, List.class);
        hashtags.add(input);

        currentContext.put(STEP, 9);
        return currentContext;
    }

    //CONFIRMA HASHTAG
    public CommandHandlerContext stepNine(CommandHandlerContext currentContext){

        String input = currentContext.get(INPUT, String.class);
        if(input.equals("si")) {
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

            List<String> hashtags = currentContext.get(FACEBOOK_HASHTAGS, List.class);
            hashtags.remove(hashtags.size()-1);
            currentContext.put(STEP, 7);
            return currentContext;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        currentContext.put(STEP, 9);
        return currentContext;

    }

    //INDICA SI SE QUIERE AGREGAR OTRO HASHTAG
    public CommandHandlerContext stepEleven(CommandHandlerContext currentContext){

        String input = currentContext.get(INPUT, String.class);
        if(input.equals("si")) {
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
            getTextToSpeech().speakText("Publicando en el muro de Facebook");
            publishOnFacebook(currentContext);
            currentContext.put(STEP, 0);
            return currentContext;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        currentContext.put(STEP, 11);
        return currentContext;

    }

    public void publishOnFacebook(CommandHandlerContext currentContext) {

        Character firstCharacter, newFirstCharacter;

        String textToPublish = currentContext.get(FACEBOOK_HASHTAGS, String.class);
        List<String> hashtags = currentContext.get(FACEBOOK_HASHTAGS, List.class);

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


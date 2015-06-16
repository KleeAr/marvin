package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.social.TwitterService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class TwittearHandler extends CommandHandler {

    public static final String MENSAJE = "mensaje";
    public static final String TWITTER_HASHTAGS = "TWITTER_HASHTAGS";

    public TwittearHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("twittear {mensaje}",textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(context.get(SET_MESSAGE, Boolean.class)) {
            context.put(MESSAGE, context.get(COMMAND, String.class));
        }
        Integer step = context.get(STEP, Integer.class);
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
        commandHandlerContext.put(SET_MESSAGE, false);
        commandHandlerContext.put(MESSAGE, getExpressionMatcher().getValuesFromExpression(commandHandlerContext.get(COMMAND, String.class)).get(MENSAJE));
    }

    //PRONUNCIA MENSAJE
    public CommandHandlerContext stepOne(CommandHandlerContext context){

        getTextToSpeech().speakText("¿Querés publicar en Twitter el mensaje " + context.get(MESSAGE, String.class) + " ?");
        context.put(TWITTER_HASHTAGS, new ArrayList<String>());
        context.put(SET_MESSAGE, false);
        context.put(STEP, 3);
        return context;

    }

    //CONFIRMA MENSAJE
    public CommandHandlerContext stepThree(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Querés agregar un hashtag junto al mensaje?");
            context.put(STEP, 5);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué mensaje deseás publicar?");
            context.put(SET_MESSAGE, true);
            context.put(STEP, 1);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 3);
        return context;

    }

    //INDICA SI SE QUIERE AGREGAR UN HASHTAG
    public CommandHandlerContext stepFive(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            context.put(STEP, 7);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            postTweet(context.get(MESSAGE, String.class), Collections.emptyList());
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 5);
        return context;

    }

    //INGRESA HASHTAG
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        getTextToSpeech().speakText("¿Querés agregar el hashtag " + input + " junto al mensaje?");

        context.get(TWITTER_HASHTAGS, List.class).add(input);
        context.put(STEP, 9);
        return context;

    }

    //CONFIRMA HASHTAG
    public CommandHandlerContext stepNine(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Querés agregar otro hashtag?");
            context.put(STEP, 11);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            List hashtags = context.get(TWITTER_HASHTAGS, List.class);
            hashtags.remove(hashtags.size()-1);
            context.put(STEP, 7);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 9);
        return context;

    }

    //INDICA SI SE QUIERE AGREGAR OTRO HASHTAG
    public CommandHandlerContext stepEleven(CommandHandlerContext context){
        String input = context.get(COMMAND, String.class);
        if(input.equals("si")) {
            getTextToSpeech().speakText("¿Qué hashtag querés agregar?");
            context.put(STEP, 7);
            return context;
        }

        if(input.equals("cancelar")) {
            getTextToSpeech().speakText("Cancelando publicación");
            context.put(STEP, 0);
            return context;
        }

        if(input.equals("no")){
            postTweet(context.get(MESSAGE,String.class), context.get(TWITTER_HASHTAGS,List.class));
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debe indicar sí, no o cancelar");

        context.put(STEP, 11);
        return context;

    }


    public void postTweet(String textToPublish, List<String> hashtags) {

        Character firstCharacter, newFirstCharacter;

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

        try {

            TwitterService.getInstance().postTweet(textToPublish);

            getTextToSpeech().speakText("El mensaje fue publicado");

        }catch(IllegalStateException e){

            getTextToSpeech().speakText("No agregaste una cuenta de Twitter en tu perfil");

        }

    }
}


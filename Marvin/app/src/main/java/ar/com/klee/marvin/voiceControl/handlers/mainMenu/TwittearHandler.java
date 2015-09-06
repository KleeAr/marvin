package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import ar.com.klee.marvin.social.TwitterService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class TwittearHandler extends CommandHandler {

    public static final String MENSAJE = "mensaje";
    public static final String TWITTER_HASHTAGS = "TWITTER_HASHTAGS";

    public TwittearHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super("twittear {mensaje}",textToSpeech, context, commandHandlerManager);
    }

    public CommandHandlerContext drive(CommandHandlerContext context){

        if(context.getBoolean(SET_MESSAGE)) {
            context.put(MESSAGE, context.getString(COMMAND));
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
        String command = commandHandlerContext.getString(COMMAND);
        commandHandlerContext.put(MESSAGE, getExpressionMatcher(command).getValuesFromExpression(command).get(MENSAJE));
    }

    //PRONUNCIA MENSAJE
    public CommandHandlerContext stepOne(CommandHandlerContext context){

        getTextToSpeech().speakText("¿Querés publicar en Twitter el mensaje " + context.getString(MESSAGE) + " ?");
        context.put(TWITTER_HASHTAGS, new ArrayList<String>());
        context.put(SET_MESSAGE, false);
        context.put(STEP, 3);
        return context;

    }

    //CONFIRMA MENSAJE
    public CommandHandlerContext stepThree(CommandHandlerContext context){
        String input = context.getString(COMMAND);
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

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        context.put(STEP, 3);
        return context;

    }

    //INDICA SI SE QUIERE AGREGAR UN HASHTAG
    public CommandHandlerContext stepFive(CommandHandlerContext context){
        String input = context.getString(COMMAND);
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
            postTweet(context.getString(MESSAGE), Collections.<String>emptyList());
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        context.put(STEP, 5);
        return context;

    }

    //INGRESA HASHTAG
    public CommandHandlerContext stepSeven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
        getTextToSpeech().speakText("¿Querés agregar el hashtag " + input + " junto al mensaje?");

        context.getList(TWITTER_HASHTAGS, String.class).add(input);
        context.put(STEP, 9);
        return context;

    }

    //CONFIRMA HASHTAG
    public CommandHandlerContext stepNine(CommandHandlerContext context){
        String input = context.getString(COMMAND);
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
            List hashtags = context.getList(TWITTER_HASHTAGS, String.class);
            hashtags.remove(hashtags.size()-1);
            context.put(STEP, 7);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

        context.put(STEP, 9);
        return context;

    }

    //INDICA SI SE QUIERE AGREGAR OTRO HASHTAG
    public CommandHandlerContext stepEleven(CommandHandlerContext context){
        String input = context.getString(COMMAND);
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
            postTweet(context.getString(MESSAGE), context.getList(TWITTER_HASHTAGS, String.class));
            context.put(STEP, 0);
            return context;
        }

        getTextToSpeech().speakText("Debés indicar sí, no o cancelar");

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


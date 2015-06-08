package ar.com.klee.marvin.voiceControl.handlers;

import java.util.ArrayList;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.social.TwitterService;
import ar.com.klee.marvin.voiceControl.TTS;

public class CompartirEnInstagramHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private String message;
    private ArrayList<String> hashtags;
    private TTS textToSpeech;

    public CompartirEnInstagramHandler(String command, TTS textToSpeech){

        expressionMatcher = new ExpressionMatcher("compartir en instagram");

        this.command = command;
        this.textToSpeech = textToSpeech;

        hashtags = new ArrayList<String>();

    }

    public boolean validateCommand(){

        return expressionMatcher.matches(command);

    }

    public int drive(int step, String input){

        switch(step){

            case 1:
                return stepOne(input);
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

    //PRONUNCIA MENSAJE
    public int stepOne(String input){

        message = input;

        textToSpeech.speakText("¿Querés publicar el mensaje " + message + " junto a la foto?");

        return 3;

    }

    //CONFIRMA MENSAJE
    public int stepThree(String input){

        if(input.equals("sí")) {
            textToSpeech.speakText("¿Querés agregar un hashtag junto a la foto?");
            return 5;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Qué mensaje deseás publicar junto a la foto?");
            return 1;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 3;

    }

    //INDICA SI SE QUIERE AGREGAR UN HASHTAG
    public int stepFive(String input){

        if(input.equals("sí")) {
            textToSpeech.speakText("¿Qué hashtag querés agregar?");
            return 7;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("Publicando la foto en Twitter");
            postTweet(message);
            return 0;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 5;

    }

    //INGRESA HASHTAG
    public int stepSeven(String input){

        textToSpeech.speakText("¿Querés agregar el hashtag "+input+" junto a la foto?");

        hashtags.add(input);

        return 9;

    }

    //CONFIRMA HASHTAG
    public int stepNine(String input){

        if(input.equals("sí")) {
            textToSpeech.speakText("¿Querés agregar otro hashtag?");
            return 11;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Qué hashtag querés agregar?");
            hashtags.remove(hashtags.size()-1);
            return 7;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 9;

    }

    //INDICA SI SE QUIERE AGREGAR OTRO HASHTAG
    public int stepEleven(String input){

        if(input.equals("sí")) {
            textToSpeech.speakText("¿Qué hashtag querés agregar?");
            return 7;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("Publicando la foto en Twitter");
            postTweet(message);
            return 0;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 11;

    }


    public void postTweet(String textToPublish) {

        TwitterService.getInstance().postTweet(textToPublish);

    }
}


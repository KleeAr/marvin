package ar.com.klee.marvin.voiceControl.handlers;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.social.FacebookService;
import ar.com.klee.marvin.voiceControl.TTS;

public class PublicarEnFacebookHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private String message;
    private boolean setMessage = false;
    private ArrayList<String> hashtags;
    private TTS textToSpeech;

    private FacebookService facebookService;

    public PublicarEnFacebookHandler(String command, TTS textToSpeech, Context context){

        expressionMatcher = new ExpressionMatcher("publicar en facebook {mensaje}");

        this.command = command;
        facebookService = new FacebookService(context);
        this.textToSpeech = textToSpeech;

        hashtags = new ArrayList<String>();

    }

    public boolean validateCommand(){

        Map<String, String> values = expressionMatcher.getValuesFromExpression(command);

        message = values.get("mensaje");

        return expressionMatcher.matches(command);

    }

    public int drive(int step, String input){

        if(setMessage)
            message = input;

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

    //PRONUNCIA COMANDO
    public int stepOne(){

        textToSpeech.speakText("¿Querés publicar " + message + " en el muro?");

        setMessage = false;

        return 3;

    }

    //CONFIRMA MENSAJE
    public int stepThree(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("¿Querés agregar un hashtag?");
            return 5;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Qué mensaje deseás publicar?");
            setMessage = true;
            return 1;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 3;

    }

    //INDICA SI SE QUIERE AGREGAR UN HASHTAG
    public int stepFive(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("¿Qué hashtag querés agregar?");
            return 7;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("Publicando en el muro de Facebook");
            publishOnFacebook(message);
            return 0;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 5;

    }

    //INGRESA HASHTAG
    public int stepSeven(String input){

        textToSpeech.speakText("¿Querés agregar el hashtag "+input+"?");

        hashtags.add(input);

        return 9;

    }

    //CONFIRMA HASHTAG
    public int stepNine(String input){

        if(input.equals("si")) {
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

        if(input.equals("si")) {
            textToSpeech.speakText("¿Qué hashtag querés agregar?");
            return 7;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("Publicando en el muro de Facebook");
            publishOnFacebook(message);
            return 0;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 11;

    }

    public void publishOnFacebook(String textToPublish) {

        int i=0;

        while(i != hashtags.size()){

            textToPublish = textToPublish + " #";

            String hashtag = hashtags.get(i).toLowerCase();

            String word;
            Character firstCharacter, newFirstCharacter;

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


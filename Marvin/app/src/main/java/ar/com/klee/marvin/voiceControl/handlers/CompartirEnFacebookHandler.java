package ar.com.klee.marvin.voiceControl.handlers;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.social.FacebookService;
import ar.com.klee.marvin.voiceControl.TTS;

public class CompartirEnFacebookHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private String message;
    private ArrayList<String> etiquetados;
    private ArrayList<String> hashtags;
    private TTS textToSpeech;

    private FacebookService facebookService;

    public CompartirEnFacebookHandler(String command, TTS textToSpeech, Activity activity){

        expressionMatcher = new ExpressionMatcher("compartir en facebook");

        this.command = command;
        facebookService = new FacebookService(activity);
        this.textToSpeech = textToSpeech;

        etiquetados = new ArrayList<String>();
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
            case 13:
                return stepThirteen(input);
            case 15:
                return stepFifteen(input);
            case 17:
                return stepSeventeen(input);
            case 19:
                return stepNineteen(input);

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
            textToSpeech.speakText("¿Querés etiquetar a alguien?");
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

    //INDICA SI QUIERE ETIQUETAR CONTACTOS
    public int stepFive(String input){

        if(input.equals("sí")) {
            textToSpeech.speakText("Indicá a qué contacto querés etiquetar");
            return 7;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Querés agregar un hashtag?");
            return 13;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 5;

    }

    //INDICA CONTACTO A ETIQUETAR
    public int stepSeven(String input){

        /*
        VALIDAR CONTACTO INGRESADO
         */

        textToSpeech.speakText("¿Querés etiquetar a "+input+"?");

        etiquetados.add(input);

        return 9;
    }

    //CONFIRMA ETIQUETACIÓN
    public int stepNine(String input){

        if(input.equals("sí")) {
            textToSpeech.speakText("¿Querés etiquetar a alguien más?");
            return 11;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("Indicá a qué contacto querés etiquetar");
            etiquetados.remove(etiquetados.size()-1);
            return 7;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 9;

    }

    //INDICA SI SE QUIERE ETIQUETAR A ALGUIEN MÁS
    public int stepEleven(String input){

        if(input.equals("sí")) {
            textToSpeech.speakText("Indicá a qué contacto querés etiquetar");
            return 7;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Querés agregar un hashtag?");
            return 13;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 11;

    }

    //INDICA SI SE QUIERE AGREGAR UN HASHTAG
    public int stepThirteen(String input){

        if(input.equals("sí")) {
            textToSpeech.speakText("¿Qué hashtag querés agregar?");
            return 15;
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

    //INGRESA HASHTAG
    public int stepFifteen(String input){

        textToSpeech.speakText("¿Querés agregar el hashtag "+input+"?");

        hashtags.add(input);

        return 17;

    }

    //CONFIRMA HASHTAG
    public int stepSeventeen(String input){

        if(input.equals("sí")) {
            textToSpeech.speakText("¿Querés agregar otro hashtag?");
            return 19;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Qué hashtag querés agregar?");
            hashtags.remove(hashtags.size()-1);
            return 15;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 17;

    }

    //INDICA SI SE QUIERE AGREGAR OTRO HASHTAG
    public int stepNineteen(String input){

        if(input.equals("sí")) {
            textToSpeech.speakText("¿Qué hashtag querés agregar?");
            return 15;
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

        facebookService.publishText(textToPublish);

    }
}


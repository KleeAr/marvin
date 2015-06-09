package ar.com.klee.marvin.voiceControl.handlers;

import android.app.Activity;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public class CompartirFotoHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;
    private CommandHandlerManager commandHandlerManager;
    private Activity activity;

    public CompartirFotoHandler(String command, TTS textToSpeech, CommandHandlerManager commandHandlerManager, Activity activity){

        expressionMatcher = new ExpressionMatcher("compartir foto");

        this.command = command;

        this.textToSpeech = textToSpeech;

        this.commandHandlerManager = commandHandlerManager;

        this.activity = activity;

    }

    public boolean validateCommand(){
        return expressionMatcher.matches(command);
    }

    public int drive(int step, String input){

        switch(step){

            case 1:
                return stepOne();
            case 3:
                return stepThree(input);
            case 4:
                return facebook(input);
            case 5:
                return twitter(input);
            case 6:
                return instagram(input);

        }

        return 0;

    }

    public int stepOne(){

        textToSpeech.speakText("¿En qué red social deseás compartir la foto?");

        //CODIGO PARA COMPARTIR FOTO

        return 3;

    }

    public int stepThree(String input){

        if(input.equals("facebook")) {
            textToSpeech.speakText("Compartiendo foto en Facebook. ¿Querés agregar un mensaje?");
            return 4;
        }else if(input.equals("twitter")) {
            textToSpeech.speakText("Compartiendo foto en Twitter. ¿Querés agregar un mensaje?");
            return 5;
        }else if(input.equals("instagram")) {
            textToSpeech.speakText("Compartiendo foto en Instagram. ¿Querés agregar un mensaje?");
            return 6;
        }

        textToSpeech.speakText("Debe indicar facebook, twitter o instagram");

        return 3;

    }

    public int facebook(String input){

        if(input.equals("sí")) {
            commandHandlerManager.setCommandHandler(new CompartirEnFacebookHandler("compartir en facebook", textToSpeech, activity));
            textToSpeech.speakText("¿Qué mensaje querés agregar?");
            return 1;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            commandHandlerManager.setCommandHandler(new CompartirEnFacebookHandler("compartir en facebook", textToSpeech, activity));
            textToSpeech.speakText("¿Querés etiquetar a alguien?");
            return 5;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 4;

    }

    public int twitter(String input){

        if(input.equals("sí")) {
            commandHandlerManager.setCommandHandler(new CompartirEnTwitterHandler("compartir en twitter", textToSpeech));
            textToSpeech.speakText("¿Qué mensaje querés agregar?");
            return 1;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            commandHandlerManager.setCommandHandler(new CompartirEnTwitterHandler("compartir en twitter", textToSpeech));
            textToSpeech.speakText("¿Querés agregar un hashtag?");
            return 5;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 5;

    }

    public int instagram(String input){

        if(input.equals("sí")) {
            commandHandlerManager.setCommandHandler(new CompartirEnInstagramHandler("compartir en instagram", textToSpeech));
            textToSpeech.speakText("¿Qué mensaje querés agregar?");
            return 1;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando publicación");
            return 0;
        }

        if(input.equals("no")){
            commandHandlerManager.setCommandHandler(new CompartirEnInstagramHandler("compartir en instagram", textToSpeech));
            textToSpeech.speakText("¿Querés agregar un hashtag?");
            return 5;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 5;

    }

}

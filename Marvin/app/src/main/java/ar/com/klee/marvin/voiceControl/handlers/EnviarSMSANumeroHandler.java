package ar.com.klee.marvin.voiceControl.handlers;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarSMSANumeroHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private String number;
    private boolean setNumber = false;
    private TTS textToSpeech;

    public EnviarSMSANumeroHandler(String command, TTS textToSpeech){

        super(expressionMatcher, textToSpeech, context, commandHandlerManager);
        expressionMatcher = new ExpressionMatcher("enviar sms al {numero}");

        this.command = command;
        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){

        Map<String, String> values = expressionMatcher.getValuesFromExpression(command);

        number = values.get("numero");

        return expressionMatcher.matches(command);

    }

    public int drive(int step, String input){

        if(setNumber)
            number = input;

        switch(step){

            case 1:
                return stepOne();
            case 3:
                return stepThree(input);
            case 5:
                return stepFive(input);
            case 7:
                return stepSeven(input);

        }

        return 0;

    }

    //PRONUNCIA NUMERO
    public int stepOne(){

        textToSpeech.speakText("¿Querés enviar un sms al número " + number + "?");

        setNumber = false;

        return 3;

    }

    //CONFIRMA NUMERO
    public int stepThree(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("¿Qué mensaje le querés mandar por sms?");
            return 5;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿A qué número querés mandarle el sms?");
            setNumber = true;
            return 1;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 3;

    }

    //INGRESO MENSAJE
    public int stepFive(String input){

        textToSpeech.speakText("¿Querés enviar por sms el mensaje " + input + "?");

        return 7;

    }

    //CONFIRMACION DE MENSAJE
    public int stepSeven(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("Enviando sms");
            return 0;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿Qué mensaje querés mandar?");
            return 5;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 7;

    }


}


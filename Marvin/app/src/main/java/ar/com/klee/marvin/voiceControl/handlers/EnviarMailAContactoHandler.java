package ar.com.klee.marvin.voiceControl.handlers;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.TTS;

public class EnviarMailAContactoHandler extends CommandHandler{

    private ExpressionMatcher expressionMatcher;
    private String command;
    private String contact;
    private boolean setContact = false;
    private TTS textToSpeech;

    public EnviarMailAContactoHandler(String command, TTS textToSpeech){

        super(expressionMatcher, textToSpeech, context, commandHandlerManager);
        expressionMatcher = new ExpressionMatcher("enviar mail a {contacto}");

        this.command = command;
        this.textToSpeech = textToSpeech;

    }

    public boolean validateCommand(){

        Map<String, String> values = expressionMatcher.getValuesFromExpression(command);

        contact = values.get("contacto");

        return expressionMatcher.matches(command);

    }

    public int drive(int step, String input){

        if(setContact)
            contact = input;

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

    //PRONUNCIA CONTACTO
    public int stepOne(){

        textToSpeech.speakText("¿Querés enviar un mail al contacto " + contact + "?");

        setContact = false;

        return 3;

    }

    //CONFIRMA CONTACTO
    public int stepThree(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("¿Qué mensaje le querés mandar por mail?");
            return 5;
        }

        if(input.equals("cancelar")) {
            textToSpeech.speakText("Cancelando envío");
            return 0;
        }

        if(input.equals("no")){
            textToSpeech.speakText("¿A qué contacto querés mandarle el mail?");
            setContact = true;
            return 1;
        }

        textToSpeech.speakText("Debe indicar sí, no o cancelar");

        return 3;

    }

    //INGRESO MENSAJE
    public int stepFive(String input){

        textToSpeech.speakText("¿Querés enviar por mail el mensaje " + input + "?");

        return 7;

    }

    //CONFIRMACION DE MENSAJE
    public int stepSeven(String input){

        if(input.equals("si")) {
            textToSpeech.speakText("Enviando mail");
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


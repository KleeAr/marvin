package ar.com.klee.marvin.voiceControl;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.speech.SpeechRecognizer;

import java.util.StringTokenizer;

import ar.com.klee.marvin.social.CalendarService;
import ar.com.klee.marvin.social.FacebookService;
import ar.com.klee.marvin.social.TwitterService;
import ar.com.klee.marvin.social.WhatsAppService;
import ar.com.klee.marvin.voiceControl.handlers.AbrirAplicacionHandler;
import ar.com.klee.marvin.voiceControl.handlers.CerrarCamaraHandler;
import ar.com.klee.marvin.voiceControl.handlers.CerrarSesionHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.GuardarFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.GuardarYCompartirFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.PublicarEnFacebookHandler;
import ar.com.klee.marvin.voiceControl.handlers.SacarFotoHandler;

public class CommandHandlerManager {

    private int actualStep = 0;
    private int errorCounter = 0;
    private CommandHandler commandHandler;

    private WhatsAppService whatsAppService;
    private CalendarService calendarService;

    private TTS textToSpeech;

    private Activity activity;

    CommandHandlerManager(Activity activity, SpeechRecognizer mSpeechRecognizer, Intent mSpeechRecognizerIntent){

        whatsAppService =  new WhatsAppService(activity);
        this.calendarService = new CalendarService(activity);

        textToSpeech = new TTS(activity, mSpeechRecognizer, mSpeechRecognizerIntent);

    }

    public boolean detectCommand(String command, boolean isListening){

        command = command.toLowerCase();

        StringTokenizer stringTokenizer = new StringTokenizer(command);

        String firstWord, secondWord, thirdWord;

        firstWord = stringTokenizer.nextToken();

        if(!isListening){

            if(firstWord.equals("marvin")){

                //ACTIVAR PANTALLA DE ESCUCHA******************************

                textToSpeech.speakText("Te escucho");

                return true;

            }

            return false;

        }

        if(actualStep != 0){

            actualStep = commandHandler.drive(actualStep, command);

            if(actualStep == 0)
                return false;

            return true;

        }else {

            switch (firstWord) {

                case "abrir":
                    commandHandler = new AbrirAplicacionHandler(command, textToSpeech);
                    if (commandHandler.validateCommand()) {
                        errorCounter = 0;
                        commandHandler.drive(1, command);
                    } else
                        wrongCommand("abrir aplicacion");
                    break;

                case "que":
                    if (stringTokenizer.hasMoreTokens()) {
                        secondWord = stringTokenizer.nextToken();


                    } else
                        wrongCommand("");
                    break;

                case "cerrar":
                    if (stringTokenizer.hasMoreTokens()) {
                        secondWord = stringTokenizer.nextToken();
                        if (secondWord.equals("sesión")) {
                            commandHandler = new CerrarSesionHandler(command, textToSpeech);
                            if (commandHandler.validateCommand()) {
                                errorCounter = 0;
                                commandHandler.drive(1, command);
                            } else
                                wrongCommand("cerrar sesion");
                        } else if (secondWord.equals("cámara")) {
                            commandHandler = new CerrarCamaraHandler(command, textToSpeech);
                            if (commandHandler.validateCommand()) {
                                errorCounter = 0;
                                commandHandler.drive(1, command);
                            } else
                                wrongCommand("cerrar camara");
                        } else
                            wrongCommand("cerrar sesion");
                    } else
                        wrongCommand("cerrar sesion");
                    break;

                case "publicar":
                    commandHandler = new PublicarEnFacebookHandler(command, textToSpeech, activity);
                    if (commandHandler.validateCommand()) {
                        errorCounter = 0;
                        actualStep = commandHandler.drive(1, command);
                    } else
                        wrongCommand("publicar en facebook mensaje");
                    break;

                case "sacar":
                    commandHandler = new SacarFotoHandler(command, textToSpeech);
                    if (commandHandler.validateCommand()) {
                        errorCounter = 0;
                        actualStep = commandHandler.drive(1, command);
                    } else
                        wrongCommand("sacar foto");
                    break;

                case "guardar":
                    if (stringTokenizer.hasMoreTokens()) {
                        secondWord = stringTokenizer.nextToken();
                        if (secondWord.equals("foto")) {
                            commandHandler = new GuardarFotoHandler(command, textToSpeech);
                            if (commandHandler.validateCommand()) {
                                errorCounter = 0;
                                actualStep = commandHandler.drive(1, command);
                            } else
                                wrongCommand("guardar foto");
                        } else if (secondWord.equals("y")) {
                            commandHandler = new GuardarYCompartirFotoHandler(command, textToSpeech, this, activity);
                            if (commandHandler.validateCommand()) {
                                errorCounter = 0;
                                actualStep = commandHandler.drive(1, command);
                            } else
                                wrongCommand("guardar foto");
                        } else
                            wrongCommand("guardar foto");
                    } else
                        wrongCommand("guardar foto");
                    break;
/*
                case "cancelar":
                    actualCommand = COMMAND_CANCELAR_FOTO;
                    cancelarFotoHandler();
                    break;

                case "compartir":
                    if (stringTokenizer.hasMoreTokens()) {
                        secondWord = stringTokenizer.nextToken();
                        if (secondWord.equals("foto")) {
                            actualCommand = COMMAND_COMPARTIR_FOTO;
                            compartirFotoHandler();
                        } else
                            wrongCommand();
                    } else
                        wrongCommand();
                    break;

                case "twittear":
                    actualCommand = COMMAND_TWITTEAR;
                    twittearHandler();
                    break;

                case "enviar":
                    if (stringTokenizer.hasMoreTokens()) {
                        secondWord = stringTokenizer.nextToken();
                        if (secondWord.equals("whatsapp")) {
                            actualCommand = COMMAND_ENVIAR_WHATSAPP;
                            enviarWhatsAppHandler();
                        } else if (secondWord.equals("mail")) {
                            actualCommand = COMMAND_ENVIAR_MAIL_A_CONTACTO;
                            enviarMailAContactoHandler();
                        } else if (secondWord.equals("sms")) {
                            if (stringTokenizer.hasMoreTokens()) {
                                thirdWord = stringTokenizer.nextToken();
                                if (thirdWord.equals("a")) {
                                    actualCommand = COMMAND_ENVIAR_SMS_A_CONTACTO;
                                    enviarSMSAContactoHandler();
                                } else if (thirdWord.equals("al")) {
                                    actualCommand = COMMAND_ENVIAR_SMS_A_NUMERO;
                                    enviarSMSANumeroHandler();
                                } else
                                    wrongCommand();
                            } else
                                wrongCommand();
                        } else
                            wrongCommand();
                    } else
                        wrongCommand();
                    break;

                case "sms":
                    actualCommand = COMMAND_ENVIAR_SMS_DE_EMERGENCIA;
                    enviarSMSDeEmergenciaHandler();
                    break;

                case "reproducir":
                    if (stringTokenizer.hasMoreTokens()) {
                        secondWord = stringTokenizer.nextToken();
                        if (secondWord.equals("musica")) {
                            actualCommand = COMMAND_REPRODUCIR_MUSICA;
                            reproducirMusicaHandler();
                        } else if (secondWord.equals("cancion")) {
                            actualCommand = COMMAND_REPRODUCIR_CANCION;
                            reproducirCancionHandler();
                        } else if (secondWord.equals("artista")) {
                            actualCommand = COMMAND_REPRODUCIR_ARTISTA;
                            reproducirArtistanHandler();
                        } else
                            wrongCommand();
                    } else
                        wrongCommand();
                    break;
*/

                default:
                    wrongCommand("");
                    break;

            }

            if (errorCounter >= 3)
                return false;
            else
                return true;

        }
    }

    public void wrongCommand(String suggestion){

        if(errorCounter == 0 || suggestion == "")
            textToSpeech.speakText("No pude entender el comando ingresado. Repetilo");
        else if(errorCounter == 1)
            textToSpeech.speakText("No pude entender el comando ingresado. Quizás quisiste decir "+suggestion);
        else
            textToSpeech.speakText("No pude entender el comando ingresado. Consulta la lista de comandos del menú ayuda");

        errorCounter++;

    }

    public void setCommandHandler(CommandHandler commandHandler){

        this.commandHandler = commandHandler;

    }













    public void sendWhatsApp(String textToPublish) {

        whatsAppService.sendWhatsApp(textToPublish);

    }

    public void createGoogleCalendarEvent(int year, int month, int day){

        calendarService.createEvent(year, month, day);

    }

}

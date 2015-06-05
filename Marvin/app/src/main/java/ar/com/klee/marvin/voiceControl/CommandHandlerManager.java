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

public class CommandHandlerManager {

    private final int TYPE_COMMAND = 1;
    private final int TYPE_CONFIRMATION = 2;
    private final int TYPE_FREE = 3;

    private final int COMMAND_UNSETTED = 0;
    private final int COMMAND_ABRIR_APLICACION = 1;
    private final int COMMAND_PREGUNTAR_HORA = 2;
    private final int COMMAND_PREGUNTAR_TEMPERATURA = 3;
    private final int COMMAND_CERRAR_SESION = 4;
    private final int COMMAND_PUBLICAR_EN_FACEBOOK = 10;
    private final int COMMAND_SACAR_FOTO = 20;
    private final int COMMAND_GUARDAR_FOTO = 21;
    private final int COMMAND_CANCELAR_FOTO = 22;
    private final int COMMAND_CERRAR_CAMARA = 23;
    private final int COMMAND_COMPARTIR_FOTO = 24;
    private final int COMMAND_GUARDAR_Y_COMPARTIR_FOTO = 25;
    private final int COMMAND_COMPARTIR_EN_FACEBOOK = 26;
    private final int COMMAND_COMPARTIR_EN_TWITTER = 27;
    private final int COMMAND_COMPARTIR_EN_INSTAGRAM = 28;
    private final int COMMAND_TWITTEAR= 30;
    private final int COMMAND_ENVIAR_WHATSAPP= 40;
    private final int COMMAND_LLAMAR_A_CONTACTO = 50;
    private final int COMMAND_LLAMAR_A_NUMERO = 51;
    private final int COMMAND_ENVIAR_SMS_A_CONTACTO = 60;
    private final int COMMAND_ENVIAR_SMS_A_NUMERO = 61;
    private final int COMMAND_ENVIAR_SMS_DE_EMERGENCIA = 62;
    private final int COMMAND_ENVIAR_MAIL_A_CONTACTO = 70;
    private final int COMMAND_REPRODUCIR_MUSICA = 71;
    private final int COMMAND_DETENER_REPRODUCCION = 72;
    private final int COMMAND_SIGUIENTE_CANCION = 73;
    private final int COMMAND_ANTERIOR_CANCION = 74;
    private final int COMMAND_REPRODUCIR_CANCION = 75;
    private final int COMMAND_REPRODUCIR_ARTISTA = 76;
    private final int COMMAND_PREGUNTAR_CANCION = 77;
    private final int COMMAND_PREGUNTAR_CALLE_ACTUAL = 110;
    private final int COMMAND_PREGUNTAR_PROXIMA_CALLE = 111;
    private final int COMMAND_PREGUNTAR_ANTERIOR_CALLE = 112;
    private final int COMMAND_PREGUNTAR_BARRIO = 113;
    private final int COMMAND_AGREGAR_EVENTO = 120;
    private final int COMMAND_ACTIVAR_HOTSPOT = 121;
    private final int COMMAND_DESACTIVAR_HOTSPOT = 122;

    private int actualCommand = COMMAND_UNSETTED;

    private WhatsAppService whatsAppService;
    private FacebookService facebookService;
    private CalendarService calendarService;

    private TTS textToSpeech;

    private Activity activity;

    CommandHandlerManager(Activity activity, SpeechRecognizer mSpeechRecognizer, Intent mSpeechRecognizerIntent){

        whatsAppService =  new WhatsAppService(activity);
        facebookService = new FacebookService(activity);
        this.calendarService = new CalendarService(activity);

        textToSpeech = new TTS(activity, mSpeechRecognizer, mSpeechRecognizerIntent);


    }

    public boolean detectCommand(String command, boolean isListening){

        StringTokenizer stringTokenizer = new StringTokenizer(command);

        String firstWord, secondWord, thirdWord;

        firstWord = stringTokenizer.nextToken();

        firstWord = firstWord.toLowerCase();

        if(!isListening){

            if(firstWord.equals("marvin")){

                //ACTIVAR PANTALLA DE ESCUCHA******************************

                textToSpeech.speakText("Te escucho");

                return true;

            }

            return false;

        }

        /*************

        ACÁ - SI YA TENGO UN COMANDO SETEADO

        LLAMO COMMANDHANDLER - drive, steps

        */

        switch (firstWord){

            case "abrir":
                actualCommand = COMMAND_ABRIR_APLICACION;
                abrirAplicacionHandler();
                break;

            case "que":
                if(stringTokenizer.hasMoreTokens()) {
                    secondWord = stringTokenizer.nextToken();




                }else
                    wrongCommand();
                break;

            case "cerrar":
                if(stringTokenizer.hasMoreTokens()) {
                    secondWord = stringTokenizer.nextToken().toLowerCase();
                    if(secondWord.equals("sesión")){
                        actualCommand = COMMAND_CERRAR_SESION;
                        cerrarSesionHandler();
                    }else if(secondWord.equals("cámara")){
                        actualCommand = COMMAND_CERRAR_CAMARA;
                        cerrarCamaraHandler();
                    }else
                        wrongCommand();
                }else
                    wrongCommand();
                break;

            case "publicar":
                actualCommand = COMMAND_PUBLICAR_EN_FACEBOOK;
                publicarEnFacebookHandler();
                break;

            case "sacar":
                actualCommand = COMMAND_SACAR_FOTO;
                sacarFotoHandler();
                break;

            case "guardar":
                if(stringTokenizer.hasMoreTokens()) {
                    secondWord = stringTokenizer.nextToken().toLowerCase();
                    if(secondWord.equals("foto")){
                        actualCommand = COMMAND_GUARDAR_FOTO;
                        guardarFotoHandler();
                    }else if(secondWord.equals("y")){
                        actualCommand = COMMAND_GUARDAR_Y_COMPARTIR_FOTO;
                        guardarYCompartirFotoHandler();
                    }else
                        wrongCommand();
                }else
                    wrongCommand();
                break;

            case "cancelar":
                actualCommand = COMMAND_CANCELAR_FOTO;
                cancelarFotoHandler();
                break;

            case "compartir":
                if(stringTokenizer.hasMoreTokens()) {
                    secondWord = stringTokenizer.nextToken().toLowerCase();
                    if(secondWord.equals("foto")){
                        actualCommand = COMMAND_COMPARTIR_FOTO;
                        compartirFotoHandler();
                    }else if(secondWord.equals("en")){
                        if(stringTokenizer.hasMoreTokens()) {
                            thirdWord = stringTokenizer.nextToken().toLowerCase();
                            if(thirdWord.equals("facebook")) {
                                actualCommand = COMMAND_COMPARTIR_EN_FACEBOOK;
                                compartirEnFacebookHandler();
                            }else if(thirdWord.equals("twitter")){
                                actualCommand = COMMAND_COMPARTIR_EN_TWITTER;
                                compartirEnTwitterHandler();
                            }else if(thirdWord.equals("instagram")){
                                actualCommand = COMMAND_COMPARTIR_EN_INSTAGRAM;
                                compartirEnInstagramHandler();
                            }else
                                wrongCommand();
                        }
                        else
                            wrongCommand();
                    }else
                        wrongCommand();
                }else
                    wrongCommand();
                break;

            case "twittear":
                actualCommand = COMMAND_TWITTEAR;
                twittearHandler();
                break;

            case "enviar":
                if(stringTokenizer.hasMoreTokens()) {
                    secondWord = stringTokenizer.nextToken().toLowerCase();
                    if(secondWord.equals("whatsapp")){
                        actualCommand = COMMAND_ENVIAR_WHATSAPP;
                        enviarWhatsAppHandler();
                    }else if(secondWord.equals("mail")){
                        actualCommand = COMMAND_ENVIAR_MAIL_A_CONTACTO;
                        enviarMailAContactoHandler();
                    }else if(secondWord.equals("sms")){
                        if(stringTokenizer.hasMoreTokens()){
                            thirdWord = stringTokenizer.nextToken().toLowerCase();
                            if(thirdWord.equals("a")) {
                                actualCommand = COMMAND_ENVIAR_SMS_A_CONTACTO;
                                enviarSMSAContactoHandler();
                            }else if(thirdWord.equals("al")){
                                actualCommand = COMMAND_ENVIAR_SMS_A_NUMERO;
                                enviarSMSANumeroHandler();
                            }else
                                wrongCommand();
                        }else
                            wrongCommand();
                    }else
                        wrongCommand();
                }else
                    wrongCommand();
                break;

            case "sms":
                actualCommand = COMMAND_ENVIAR_SMS_DE_EMERGENCIA;
                enviarSMSDeEmergenciaHandler();
                break;

            case "reproducir":
                if(stringTokenizer.hasMoreTokens()) {
                    secondWord = stringTokenizer.nextToken().toLowerCase();
                    if(secondWord.equals("musica")){
                        actualCommand = COMMAND_REPRODUCIR_MUSICA;
                        reproducirMusicaHandler();
                    }else if(secondWord.equals("cancion")) {
                        actualCommand = COMMAND_REPRODUCIR_CANCION;
                        reproducirCancionHandler();
                    }else if(secondWord.equals("artista")) {
                        actualCommand = COMMAND_REPRODUCIR_ARTISTA;
                        reproducirArtistanHandler();
                    }else
                        wrongCommand();
                }else
                    wrongCommand();
                break;



            default:
                wrongCommand();
                break;

        }

        return false;

    }

    public void abrirAplicacionHandler(){



    }

    public void cerrarSesionHandler(){



    }

    public void publicarEnFacebookHandler(){




    }

    public void sacarFotoHandler(){



    }

    public void cerrarCamaraHandler(){



    }

    public void guardarFotoHandler(){



    }

    public void guardarYCompartirFotoHandler(){



    }

    public void cancelarFotoHandler(){



    }

    public void compartirFotoHandler(){



    }

    public void compartirEnFacebookHandler(){



    }

    public void compartirEnTwitterHandler(){



    }

    public void compartirEnInstagramHandler(){



    }

    public void twittearHandler(){



    }

    public void enviarWhatsAppHandler(){



    }

    public void enviarMailAContactoHandler(){



    }

    public void enviarSMSAContactoHandler(){



    }

    public void enviarSMSANumeroHandler(){



    }

    public void enviarSMSDeEmergenciaHandler(){



    }

    public void reproducirMusicaHandler(){



    }

    public void reproducirCancionHandler(){



    }

    public void reproducirArtistanHandler(){



    }


    public void wrongCommand(){



    }













    public void sendWhatsApp(String textToPublish) {

        whatsAppService.sendWhatsApp(textToPublish);

    }

    public void publishOnFacebook(String textToPublish) {

        facebookService.publishText(textToPublish);

    }

    public void postTweet(String textToPublish) {

        TwitterService.getInstance().postTweet(textToPublish);

    }

    public void createGoogleCalendarEvent(int year, int month, int day){

        calendarService.createEvent(year, month, day);

    }

}

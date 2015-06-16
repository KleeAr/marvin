package ar.com.klee.marvin.voiceControl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.SpeechRecognizer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.voiceControl.handlers.AbrirAplicacionHandler;
import ar.com.klee.marvin.voiceControl.handlers.ActivarHotspotHandler;
import ar.com.klee.marvin.voiceControl.handlers.AgregarEventoHandler;
import ar.com.klee.marvin.voiceControl.handlers.AnteriorCancionHandler;
import ar.com.klee.marvin.voiceControl.handlers.BarrioHandler;
import ar.com.klee.marvin.voiceControl.handlers.CalleActualHandler;
import ar.com.klee.marvin.voiceControl.handlers.CalleAnteriorHandler;
import ar.com.klee.marvin.voiceControl.handlers.CalleSiguienteHandler;
import ar.com.klee.marvin.voiceControl.handlers.CancelarFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.CerrarCamaraHandler;
import ar.com.klee.marvin.voiceControl.handlers.CerrarSesionHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;
import ar.com.klee.marvin.voiceControl.handlers.CompartirFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.DesactivarHotspotHandler;
import ar.com.klee.marvin.voiceControl.handlers.DetenerReproduccionHandler;
import ar.com.klee.marvin.voiceControl.handlers.EnviarMailAContactoHandler;
import ar.com.klee.marvin.voiceControl.handlers.EnviarSMSAContactoHandler;
import ar.com.klee.marvin.voiceControl.handlers.EnviarSMSANumeroHandler;
import ar.com.klee.marvin.voiceControl.handlers.EnviarWhatsAppHandler;
import ar.com.klee.marvin.voiceControl.handlers.GuardarFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.GuardarYCompartirFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.PublicarEnFacebookHandler;
import ar.com.klee.marvin.voiceControl.handlers.ReproducirArtistaHandler;
import ar.com.klee.marvin.voiceControl.handlers.ReproducirCancionHandler;
import ar.com.klee.marvin.voiceControl.handlers.ReproducirMusicaHandler;
import ar.com.klee.marvin.voiceControl.handlers.SMSDeEmergenciaHandler;
import ar.com.klee.marvin.voiceControl.handlers.SacarFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.SiguienteCancionHandler;
import ar.com.klee.marvin.voiceControl.handlers.TwittearHandler;

public class CommandHandlerManager {

    public static final int ACTIVITY_MAIN = 1;
    public static final int ACTIVITY_CAMERA = 2;

    private int actualActivity = ACTIVITY_MAIN;
    private int actualStep = 0;
    private int errorCounter = 0;
    private CommandHandler commandHandler;

    private TTS textToSpeech;

    private Context context;
    private Activity activity;

    private boolean isError;
    private boolean isPhotoTaken;
    private List<CommandHandler> commandHandlers;
    private CommandHandlerContext currentContext;

    CommandHandlerManager(Context context, SpeechRecognizer mSpeechRecognizer, Intent mSpeechRecognizerIntent){
        Helper.commandHandlerManager = this;
        textToSpeech = new TTS(context, mSpeechRecognizer, mSpeechRecognizerIntent);
        this.context = context;

        commandHandlers = Arrays.asList(new AbrirAplicacionHandler(textToSpeech, context, this),
        new PublicarEnFacebookHandler(textToSpeech, context,this),
        new ActivarHotspotHandler(textToSpeech, context, this),
        new AgregarEventoHandler(textToSpeech, context, this),
        new AnteriorCancionHandler(textToSpeech, context, this),
        new BarrioHandler(textToSpeech, context, this),
        new CalleActualHandler(textToSpeech, context, this),
        new CalleAnteriorHandler(textToSpeech, context, this),
        new CalleSiguienteHandler(textToSpeech, context, this),
        new CancelarFotoHandler(textToSpeech, context, this),
        new CerrarCamaraHandler(textToSpeech, context, this),
        new CompartirFotoHandler(textToSpeech, context, this ),
        new );
    }

    public boolean detectCommand(String command, boolean isListening){
        command = command.toLowerCase();

        StringTokenizer stringTokenizer = new StringTokenizer(command);

        String firstWord, secondWord, thirdWord;

        firstWord = stringTokenizer.nextToken();

        isError = false;

        if(!isListening){

            if(firstWord.equals("marvin")){

                //ACTIVAR PANTALLA DE ESCUCHA******************************

                textToSpeech.speakText("Te escucho");

                return true;

            }

            textToSpeech.speakText(" ");

            return false;

        }

        if(actualStep != 0){

            actualStep = commandHandler.drive(actualStep, command);

            if(actualStep == 0)
                return false;

            return true;

        }

        if(actualActivity == ACTIVITY_MAIN) {

            boolean isDefault = false;


            final String finalCommand = command;
            CommandHandler commandHandler1 = CollectionUtils.find(commandHandlers, new Predicate<CommandHandler>() {
                @Override
                public boolean evaluate(CommandHandler handlerToEvaluate) {
                    return handlerToEvaluate.matches(finalCommand);
                }
            });

            if(commandHandler1 != null) {
                currentContext = commandHandler1.drive(currentContext);
            } else {
                wrongCommand("");
            }

            switch (firstWord) {
                case "que":
                    if (stringTokenizer.hasMoreTokens()) {
                        secondWord = stringTokenizer.nextToken();


                    } else
                        wrongCommand("");
                    break;

                case "cerrar":
                    commandHandler = new CerrarSesionHandler(command, textToSpeech);
                    if (commandHandler.matches()) {
                        errorCounter = 0;
                        actualStep = commandHandler.drive(1, command);
                    } else
                        wrongCommand("cerrar sesión");
                    break;

                case "twittear":
                    commandHandler = new TwittearHandler(command, textToSpeech);
                    if (commandHandler.matches()) {
                        errorCounter = 0;
                        actualStep = commandHandler.drive(1, command);
                    } else
                        wrongCommand("twittear mensaje");
                    break;

                case "enviar":
                    if (stringTokenizer.hasMoreTokens()) {
                        secondWord = stringTokenizer.nextToken();
                        if (secondWord.equals("whatsapp")) {
                            commandHandler = new EnviarWhatsAppHandler(command, textToSpeech, context);
                            if (commandHandler.matches()) {
                                errorCounter = 0;
                                actualStep = commandHandler.drive(1, command);
                            } else
                                wrongCommand("enviar whatsapp mensaje");
                        } else if (secondWord.equals("mail")) {
                            commandHandler = new EnviarMailAContactoHandler(command, textToSpeech);
                            if (commandHandler.matches()) {
                                errorCounter = 0;
                                actualStep = commandHandler.drive(1, command);
                            } else
                                wrongCommand("enviar mail a contacto");
                        } else if (secondWord.equals("sms")) {
                            if (stringTokenizer.hasMoreTokens()) {
                                thirdWord = stringTokenizer.nextToken();
                                if (thirdWord.equals("a")) {
                                    commandHandler = new EnviarSMSAContactoHandler(command, textToSpeech);
                                    if (commandHandler.matches()) {
                                        errorCounter = 0;
                                        actualStep = commandHandler.drive(1, command);
                                    } else
                                        wrongCommand("enviar sms a contacto");
                                } else if (thirdWord.equals("al")) {
                                    commandHandler = new EnviarSMSANumeroHandler(command, textToSpeech);
                                    if (commandHandler.matches()) {
                                        errorCounter = 0;
                                        actualStep = commandHandler.drive(1, command);
                                    } else
                                        wrongCommand("enviar sms al número");
                                } else
                                    wrongCommand("enviar sms a contacto");
                            } else
                                wrongCommand("enviar sms a contacto");
                        } else
                            wrongCommand("");
                    } else
                        wrongCommand("");
                    break;

                case "sms":
                    commandHandler = new SMSDeEmergenciaHandler(command, textToSpeech);
                    if (commandHandler.matches()) {
                        errorCounter = 0;
                        actualStep = commandHandler.drive(1, command);
                    } else
                        wrongCommand("sms de emergencia");
                    break;

                case "reproducir":
                    if (stringTokenizer.hasMoreTokens()) {
                        secondWord = stringTokenizer.nextToken();
                        if (secondWord.equals("música")) {
                            commandHandler = new ReproducirMusicaHandler(command, textToSpeech);
                            if (commandHandler.matches()) {
                                errorCounter = 0;
                                actualStep = commandHandler.drive(1, command);
                            } else
                                wrongCommand("reproducir música");
                        } else if (secondWord.equals("canción")) {
                            commandHandler = new ReproducirCancionHandler(command, textToSpeech);
                            if (commandHandler.matches()) {
                                errorCounter = 0;
                                actualStep = commandHandler.drive(1, command);
                            } else
                                wrongCommand("reproducir canción");
                        } else if (secondWord.equals("artista")) {
                            commandHandler = new ReproducirArtistaHandler(command, textToSpeech);
                            if (commandHandler.matches()) {
                                errorCounter = 0;
                                actualStep = commandHandler.drive(1, command);
                            } else
                                wrongCommand("reproducir artista");
                        } else
                            wrongCommand("reproducir música");
                    } else
                        wrongCommand("reproducir música");
                    break;

                case "detener":
                    commandHandler = new DetenerReproduccionHandler(command, textToSpeech);
                    if (commandHandler.matches()) {
                        errorCounter = 0;
                        actualStep = commandHandler.drive(1, command);
                    } else
                        wrongCommand("detener reproducción");
                    break;

                case "siguiente":
                    commandHandler = new SiguienteCancionHandler(command, textToSpeech);
                    if (commandHandler.matches()) {
                        errorCounter = 0;
                        actualStep = commandHandler.drive(1, command);
                    } else
                        wrongCommand("siguiente canción");
                    break;

                case "anterior":
                    commandHandler = new AnteriorCancionHandler(command, textToSpeech);
                    if (commandHandler.matches()) {
                        errorCounter = 0;
                        actualStep = commandHandler.drive(1, command);
                    } else
                        wrongCommand("anterior canción");
                    break;

                case "calle":
                    if (stringTokenizer.hasMoreTokens()) {
                        secondWord = stringTokenizer.nextToken();
                        if (secondWord.equals("actual")) {
                            commandHandler = new CalleActualHandler(command, textToSpeech);
                            if (commandHandler.matches()) {
                                errorCounter = 0;
                                actualStep = commandHandler.drive(1, command);
                            } else
                                wrongCommand("calle actual");
                        } else if (secondWord.equals("siguiente")) {
                            commandHandler = new CalleSiguienteHandler(command, textToSpeech);
                            if (commandHandler.matches()) {
                                errorCounter = 0;
                                actualStep = commandHandler.drive(1, command);
                            } else
                                wrongCommand("calle siguiente");
                        } else if (secondWord.equals("anterior")) {
                            commandHandler = new CalleAnteriorHandler(command, textToSpeech);
                            if (commandHandler.matches()) {
                                errorCounter = 0;
                                actualStep = commandHandler.drive(1, command);
                            } else
                                wrongCommand("calle anterior");
                        } else
                            wrongCommand("calle actual");
                    } else
                        wrongCommand("calle actual");
                    break;

                case "desactivar":
                    commandHandler = new DesactivarHotspotHandler(command, textToSpeech);
                    if (commandHandler.matches()) {
                        errorCounter = 0;
                        actualStep = commandHandler.drive(1, command);
                    } else
                        wrongCommand("desactivar hotspot");
                    break;

                default:
                    isDefault = true;
                    wrongCommand("");
                    break;

            }

            if (errorCounter >= 3 || (!isError && !isDefault && actualStep == 0))
                return false;
            else
                return true;

        } else if(actualActivity == ACTIVITY_CAMERA){

            boolean isDefault = false;

            if(isPhotoTaken) {

                switch (firstWord) {

                    case "cerrar":
                        commandHandler = new CerrarCamaraHandler(command, textToSpeech, (CameraActivity) activity, this);
                        if (commandHandler.matches()) {
                            errorCounter = 0;
                            actualStep = commandHandler.drive(1, command);
                        } else
                            wrongCommand("cerrar cámara");
                        break;

                    case "guardar":
                        if (stringTokenizer.hasMoreTokens()) {
                            secondWord = stringTokenizer.nextToken();
                            if (secondWord.equals("foto")) {
                                commandHandler = new GuardarFotoHandler(command, textToSpeech, (CameraActivity) activity);
                                if (commandHandler.matches()) {
                                    errorCounter = 0;
                                    actualStep = commandHandler.drive(1, command);
                                    isPhotoTaken = false;
                                } else
                                    wrongCommand("guardar foto");
                            } else if (secondWord.equals("y")) {
                                commandHandler = new GuardarYCompartirFotoHandler(command, textToSpeech, this, (CameraActivity) activity);
                                if (commandHandler.matches()) {
                                    errorCounter = 0;
                                    actualStep = commandHandler.drive(1, command);
                                    isPhotoTaken = false;
                                } else
                                    wrongCommand("guardar foto");
                            } else
                                wrongCommand("guardar foto");
                        } else
                            wrongCommand("guardar foto");
                        break;

                    case "cancelar":
                        commandHandler = new CancelarFotoHandler(command, textToSpeech, (CameraActivity) activity);
                        if (commandHandler.matches()) {
                            errorCounter = 0;
                            actualStep = commandHandler.drive(1, command);
                            isPhotoTaken = false;
                        } else
                            wrongCommand("cancelar foto");
                        break;

                    case "compartir":
                        commandHandler = new CompartirFotoHandler(command, textToSpeech, this, context, (CameraActivity) activity);
                        if (commandHandler.matches()) {
                            errorCounter = 0;
                            actualStep = commandHandler.drive(1, command);
                            isPhotoTaken = false;
                        } else
                            wrongCommand("compartir foto");
                        break;

                    default:
                        isDefault = true;
                        wrongCommand("guardar foto");
                        break;

                }

            }else{

                switch (firstWord) {

                    case "cerrar":
                        commandHandler = new CerrarCamaraHandler(command, textToSpeech, (CameraActivity) activity, this);
                        if (commandHandler.matches()) {
                            errorCounter = 0;
                            actualStep = commandHandler.drive(1, command);
                        } else
                            wrongCommand("cerrar cámara");
                        break;

                    case "sacar":
                        commandHandler = new SacarFotoHandler(command, textToSpeech, (CameraActivity) activity);
                        if (commandHandler.matches()) {
                            errorCounter = 0;
                            actualStep = commandHandler.drive(1, command);
                            isPhotoTaken = true;
                        } else
                            wrongCommand("sacar foto");
                        break;

                    default:
                        isDefault = true;
                        wrongCommand("sacar foto");
                        break;

                }

            }

            if (errorCounter >= 3 || (!isError && !isDefault && actualStep == 0))
                return false;
            else
                return true;
        }

        return false;

    }

    public void wrongCommand(String suggestion){

        if(errorCounter == 0 || (errorCounter == 1 && suggestion.equals("")))
            textToSpeech.speakText("No pude entender el comando ingresado. Repetilo");
        else if(errorCounter == 1)
            textToSpeech.speakText("No pude entender el comando ingresado. Quizás quisiste decir "+suggestion);
        else
            textToSpeech.speakText("No pude entender el comando ingresado. Consultá la lista de comandos del menú ayuda y volvé a llamarme por mi nombre");

        errorCounter++;
        isError = true;

    }

    public void setCommandHandler(CommandHandler commandHandler){

        this.commandHandler = commandHandler;

    }

    public void setIsPhotoTaken(boolean isPhotoTaken){

        this.isPhotoTaken = isPhotoTaken;

    }

    public void defineActivity(int activityType, Activity activity){

        actualActivity = activityType;

        this.activity = activity;

        if(actualActivity == ACTIVITY_CAMERA)
            isPhotoTaken = false;

    }

}

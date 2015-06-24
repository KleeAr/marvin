package ar.com.klee.marvin.voiceControl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.Arrays;
import java.util.List;

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
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;
import ar.com.klee.marvin.voiceControl.handlers.CompartirEnFacebookHandler;
import ar.com.klee.marvin.voiceControl.handlers.CompartirEnInstagramHandler;
import ar.com.klee.marvin.voiceControl.handlers.CompartirEnTwitterHandler;
import ar.com.klee.marvin.voiceControl.handlers.CompartirFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.DesactivarHotspotHandler;
import ar.com.klee.marvin.voiceControl.handlers.DetenerReproduccionHandler;
import ar.com.klee.marvin.voiceControl.handlers.EnviarMailAContactoHandler;
import ar.com.klee.marvin.voiceControl.handlers.EnviarSMSAContactoHandler;
import ar.com.klee.marvin.voiceControl.handlers.EnviarSMSANumeroHandler;
import ar.com.klee.marvin.voiceControl.handlers.EnviarWhatsAppHandler;
import ar.com.klee.marvin.voiceControl.handlers.GuardarFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.GuardarYCompartirFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.PausarMusicaHandler;
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

    private int currentActivity = ACTIVITY_MAIN;
    private int currentStep = 0;
    private int errorCounter = 0;
    private CommandHandler currentCommandHandler;

    private TTS textToSpeech;

    private Context context;
    private Activity activity;
    private Activity mainActivity;

    private boolean isError;
    private boolean isPhotoTaken;
    private List<CommandHandler> commandHandlers;
    private CommandHandlerContext currentContext;
    private CommandHandler compartirEnFacebookHandler;
    private CommandHandler compartirEnTwitterHandler;
    private CommandHandler compartirInstagramHandler;

    CommandHandlerManager(Context context, SpeechRecognizer mSpeechRecognizer, Intent mSpeechRecognizerIntent){
        Helper.commandHandlerManager = this;
        textToSpeech = new TTS(context, mSpeechRecognizer, mSpeechRecognizerIntent);
        this.context = context;

        this.compartirEnTwitterHandler = new CompartirEnTwitterHandler(textToSpeech, context, this);
        this.compartirEnFacebookHandler = new CompartirEnFacebookHandler(textToSpeech, context, this);
        this.compartirInstagramHandler = new CompartirEnInstagramHandler(textToSpeech, context, this);

        // Initialize all command handlers
        commandHandlers = Arrays.asList(new AbrirAplicacionHandler(textToSpeech, context, this),
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
        this.compartirEnFacebookHandler,
        this.compartirEnTwitterHandler,
        this.compartirInstagramHandler,
        new CompartirFotoHandler(textToSpeech, context, this),
        new DesactivarHotspotHandler(textToSpeech, context, this),
        new DetenerReproduccionHandler(textToSpeech, context, this),
        new EnviarMailAContactoHandler(textToSpeech, context, this),
        new EnviarSMSAContactoHandler(textToSpeech, context, this),
        new EnviarSMSANumeroHandler(textToSpeech, context, this),
        new EnviarWhatsAppHandler(textToSpeech, context, this),
        new GuardarFotoHandler(textToSpeech, context, this),
        new GuardarYCompartirFotoHandler(textToSpeech, context, this),
        new PausarMusicaHandler(textToSpeech, context, this),
        new PublicarEnFacebookHandler(textToSpeech, context,this),
        new ReproducirArtistaHandler(textToSpeech, context, this),
        new ReproducirCancionHandler(textToSpeech, context, this),
        new ReproducirMusicaHandler(textToSpeech, context, this),
        new SacarFotoHandler(textToSpeech, context, this),
        new SiguienteCancionHandler(textToSpeech, context, this),
        new SMSDeEmergenciaHandler(textToSpeech, context, this),
        new TwittearHandler(textToSpeech, context, this));
    }

    public boolean detectCommand(String command, boolean isListening){
        command = command.toLowerCase();

        isError = false;

        if(!isListening){

            if(command.startsWith("marvin")){

                textToSpeech.speakText("Te escucho");
                return true;

            }

            textToSpeech.speakText(" ");

            return false;

        }

        final String finalCommand = command;
        if(currentContext == null || currentContext.getInteger(CommandHandler.STEP) == 0) {
            // find the command that matches
            currentCommandHandler = CollectionUtils.find(commandHandlers, new Predicate<CommandHandler>() {
                @Override
                public boolean evaluate(CommandHandler handlerToEvaluate) {
                    return handlerToEvaluate.matches(finalCommand);
                }
            });
        }
        if(currentCommandHandler != null) {
            errorCounter = 0;
            currentContext = currentCommandHandler.drive(currentCommandHandler.createContext(currentContext, activity, command));
        } else {
            // If command handler is null, then it didn't found any match, the command was wrong
            wrongCommand(getSuggestions(command));
        }

        if (errorCounter >= 3 || (!isError && currentStep == 0)) {
            return false;
        } else {
            return true;
        }
    }

    private String getSuggestions(final String command) {
        CommandHandler suggestedHandler = CollectionUtils.find(commandHandlers, new Predicate<CommandHandler>() {
            @Override
            public boolean evaluate(CommandHandler handlerToEvaluate) {
                return handlerToEvaluate.isSimilar(command);
            }
        });
        if(suggestedHandler == null) {
            return "";
        }
        return suggestedHandler.getSuggestion();
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

    public void setCurrentCommandHandler(CommandHandler currentCommandHandler){

        this.currentCommandHandler = currentCommandHandler;

    }

    public void setIsPhotoTaken(boolean isPhotoTaken){

        this.isPhotoTaken = isPhotoTaken;

    }

    public void defineActivity(int activityType, Activity activity){

        currentActivity = activityType;

        this.activity = activity;

        if(currentActivity == ACTIVITY_CAMERA)
            isPhotoTaken = false;

    }

    public void defineMainActivity(Activity activity){

        this.mainActivity = activity;

        currentActivity = ACTIVITY_MAIN;

        this.activity = mainActivity;

    }

    public Activity getMainActivity(){

        return mainActivity;

    }

    public CommandHandler getCompartirEnFacebookHandler() {
        return compartirEnFacebookHandler;
    }

    public void setCompartirEnFacebookHandler(CommandHandler compartirEnFacebookHandler) {
        this.compartirEnFacebookHandler = compartirEnFacebookHandler;
    }

    public CommandHandler getCompartirEnTwitterHandler() {
        return compartirEnTwitterHandler;
    }

    public void setCompartirEnTwitterHandler(CommandHandler compartirEnTwitterHandler) {
        this.compartirEnTwitterHandler = compartirEnTwitterHandler;
    }


    public CommandHandler getCompartirInstagramHandler() {
        return compartirInstagramHandler;
    }

    public void setCompartirInstagramHandler(CommandHandler compartirInstagramHandler) {
        this.compartirInstagramHandler = compartirInstagramHandler;
    }
}

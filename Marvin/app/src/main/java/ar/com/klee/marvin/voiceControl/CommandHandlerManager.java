package ar.com.klee.marvin.voiceControl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.SpeechRecognizer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.klee.marvin.voiceControl.handlers.callHistory.CerrarHistorialDeLlamadasHandler;
import ar.com.klee.marvin.voiceControl.handlers.callHistory.ConsultarRegistroNumeroHandler;
import ar.com.klee.marvin.voiceControl.handlers.callHistory.ConsultarUltimoRegistroDeContactoHandler;
import ar.com.klee.marvin.voiceControl.handlers.callHistory.ConsultarUltimoRegistroDeNumeroHandler;
import ar.com.klee.marvin.voiceControl.handlers.callHistory.ConsultarUltimoRegistroHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.AbrirAplicacionHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.ActivarHotspotHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.ActivarReproduccionAleatoriaHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.AgregarEventoHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.AnteriorCancionHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.BajarVolumenHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.BarrioHandler;
import ar.com.klee.marvin.voiceControl.handlers.BuscarDispositivosHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.BuscarEnYoutubeHandler;
import ar.com.klee.marvin.voiceControl.handlers.smsInbox.CerrarHistorialDeSMSHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.DireccionHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.AnteriorInterseccionHandler;
import ar.com.klee.marvin.voiceControl.handlers.smsInbox.LeerSMSNumeroHandler;
import ar.com.klee.marvin.voiceControl.handlers.smsInbox.LeerUltimoSMSDeContactoHandler;
import ar.com.klee.marvin.voiceControl.handlers.smsInbox.LeerUltimoSMSDeNumeroHandler;
import ar.com.klee.marvin.voiceControl.handlers.smsInbox.LeerUltimoSMSHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.LlamarAContactoHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.LlamarANumeroHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.SiguienteInterseccion;
import ar.com.klee.marvin.voiceControl.handlers.camera.CancelarFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.camera.CerrarCamaraHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.CerrarSesionHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;
import ar.com.klee.marvin.voiceControl.handlers.camera.CompartirEnFacebookHandler;
import ar.com.klee.marvin.voiceControl.handlers.camera.CompartirEnInstagramHandler;
import ar.com.klee.marvin.voiceControl.handlers.camera.CompartirEnTwitterHandler;
import ar.com.klee.marvin.voiceControl.handlers.camera.CompartirFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.DesactivarHotspotHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.DesactivarReproduccionAleatoriaHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.DetenerReproduccionHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.EnviarMailAContactoHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.EnviarSMSAContactoHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.EnviarSMSANumeroHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.EnviarWhatsAppHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.EstablecerVolumenHandler;
import ar.com.klee.marvin.voiceControl.handlers.camera.GuardarFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.camera.GuardarYCompartirFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.PausarMusicaHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.PublicarEnFacebookHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.ReproducirArtistaHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.ReproducirCancionHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.ReproducirMusicaHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.SMSDeEmergenciaHandler;
import ar.com.klee.marvin.voiceControl.handlers.camera.SacarFotoHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.SiguienteCancionHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.SubirVolumenHandler;
import ar.com.klee.marvin.voiceControl.handlers.mainMenu.TwittearHandler;

public class CommandHandlerManager {

    public static final int ACTIVITY_MAIN = 1;
    public static final int ACTIVITY_CAMERA = 2;
    public static final int ACTIVITY_SMS_INBOX = 3;
    public static final int ACTIVITY_INCOMING_CALL = 4;
    public static final int ACTIVITY_CALL_HISTORY = 5;
    private static CommandHandlerManager instance;

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
    private List<CommandHandler> commandHandlersMainMenu;
    private List<CommandHandler> commandHandlersCamera;
    private List<CommandHandler> commandHandlersSMSInbox;
    private List<CommandHandler> commandHandlersCallHistory;
    private Map<Integer,List<CommandHandler>> commandHandlers;
    private CommandHandlerContext currentContext;
    private CommandHandler compartirEnFacebookHandler;
    private CommandHandler compartirEnTwitterHandler;
    private CommandHandler compartirInstagramHandler;

    public static CommandHandlerManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getInstance");
        }
        return instance;
    }

    public static CommandHandlerManager initializeInstance(Context context, SpeechRecognizer mSpeechRecognizer, Intent mSpeechRecognizerIntent) {
        if(instance != null) {
            throw new IllegalStateException("Instance already initialized");
        }
        CommandHandlerManager.instance = new CommandHandlerManager(context, mSpeechRecognizer, mSpeechRecognizerIntent);
        return instance;
    }

    private CommandHandlerManager(Context context, SpeechRecognizer mSpeechRecognizer, Intent mSpeechRecognizerIntent){

        textToSpeech = new TTS(context, mSpeechRecognizer, mSpeechRecognizerIntent);
        this.context = context;

        this.compartirEnTwitterHandler = new CompartirEnTwitterHandler(textToSpeech, context, this);
        this.compartirEnFacebookHandler = new CompartirEnFacebookHandler(textToSpeech, context, this);
        this.compartirInstagramHandler = new CompartirEnInstagramHandler(textToSpeech, context, this);

        // Initialize all command handlers
        commandHandlersMainMenu = Arrays.asList(new AbrirAplicacionHandler(textToSpeech, context, this),
            new BuscarDispositivosHandler(textToSpeech, context, this),
            new ActivarHotspotHandler(textToSpeech, context, this),
            new ActivarReproduccionAleatoriaHandler(textToSpeech, context, this),
            new AgregarEventoHandler(textToSpeech, context, this),
            new AnteriorCancionHandler(textToSpeech, context, this),
            new AnteriorInterseccionHandler(textToSpeech, context, this),
            new BajarVolumenHandler(textToSpeech, context, this),
            new BarrioHandler(textToSpeech, context, this),
            new BuscarEnYoutubeHandler(textToSpeech, context, this),
            new CerrarSesionHandler(textToSpeech, context, this),
            new DesactivarHotspotHandler(textToSpeech, context, this),
            new DesactivarReproduccionAleatoriaHandler(textToSpeech, context, this),
            new DetenerReproduccionHandler(textToSpeech, context, this),
            new DireccionHandler(textToSpeech, context, this),
            new EnviarMailAContactoHandler(textToSpeech, context, this),
            new EnviarSMSAContactoHandler(textToSpeech, context, this),
            new EnviarSMSANumeroHandler(textToSpeech, context, this),
            new EnviarWhatsAppHandler(textToSpeech, context, this),
            new EstablecerVolumenHandler(textToSpeech, context, this),
            new LlamarAContactoHandler(textToSpeech, context, this),
            new LlamarANumeroHandler(textToSpeech, context, this),
            new PausarMusicaHandler(textToSpeech, context, this),
            new PublicarEnFacebookHandler(textToSpeech, context,this),
            new ReproducirArtistaHandler(textToSpeech, context, this),
            new ReproducirCancionHandler(textToSpeech, context, this),
            new ReproducirMusicaHandler(textToSpeech, context, this),
            new SiguienteCancionHandler(textToSpeech, context, this),
            new SiguienteInterseccion(textToSpeech, context, this),
            new SMSDeEmergenciaHandler(textToSpeech, context, this),
            new SubirVolumenHandler(textToSpeech, context, this),
            new TwittearHandler(textToSpeech, context, this));

        commandHandlersCamera = Arrays.asList(new CancelarFotoHandler(textToSpeech, context, this),
            new CerrarCamaraHandler(textToSpeech, context, this),
            new CompartirFotoHandler(textToSpeech, context, this ),
            this.compartirEnFacebookHandler,
            this.compartirEnTwitterHandler,
            this.compartirInstagramHandler,
            new CompartirFotoHandler(textToSpeech, context, this),
            new GuardarFotoHandler(textToSpeech, context, this),
            new GuardarYCompartirFotoHandler(textToSpeech, context, this),
            new SacarFotoHandler(textToSpeech, context, this));

        commandHandlersSMSInbox = Arrays.asList(new LeerUltimoSMSHandler(textToSpeech, context, this),
            new CerrarHistorialDeSMSHandler(textToSpeech, context, this),
            new LeerSMSNumeroHandler(textToSpeech, context, this),
            new LeerUltimoSMSDeContactoHandler(textToSpeech, context, this),
            new LeerUltimoSMSDeNumeroHandler(textToSpeech, context, this));

        commandHandlersCallHistory = Arrays.asList(new ConsultarUltimoRegistroHandler(textToSpeech, context, this),
                new CerrarHistorialDeLlamadasHandler(textToSpeech, context, this),
                new ConsultarRegistroNumeroHandler(textToSpeech, context, this),
                new ConsultarUltimoRegistroDeContactoHandler(textToSpeech, context, this),
                new ConsultarUltimoRegistroDeNumeroHandler(textToSpeech, context, this));

        commandHandlers = new HashMap<>();

        commandHandlers.put(ACTIVITY_MAIN,commandHandlersMainMenu);
        commandHandlers.put(ACTIVITY_CAMERA,commandHandlersCamera);
        commandHandlers.put(ACTIVITY_SMS_INBOX,commandHandlersSMSInbox);
        commandHandlers.put(ACTIVITY_CALL_HISTORY,commandHandlersCallHistory);

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
            currentCommandHandler = CollectionUtils.find(commandHandlers.get(currentActivity), new Predicate<CommandHandler>() {
                @Override
                public boolean evaluate(CommandHandler handlerToEvaluate) {
                    return handlerToEvaluate.matches(finalCommand);
                }
            });
        }
        if(currentCommandHandler != null) {
            errorCounter = 0;
            currentContext = currentCommandHandler.drive(currentCommandHandler.createContext(currentContext, activity, command));
            currentStep = currentContext.getInteger("STEP");
        } else {
            // If command handler is null, then it didn't found any match, the command was wrong
            wrongCommand(getSuggestions(command));
        }

        if (errorCounter >= 3 || (!isError && currentStep == 0)) {
            currentCommandHandler = null;
            return false;
        } else {
            return true;
        }
    }

    private String getSuggestions(final String command) {
        CommandHandler suggestedHandler = CollectionUtils.find(commandHandlers.get(currentActivity), new Predicate<CommandHandler>() {
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

    public void setCurrentContext(CommandHandlerContext currentContext){

        this.currentContext = currentContext;

    }

    public void setIsPhotoTaken(boolean isPhotoTaken){

        this.isPhotoTaken = isPhotoTaken;

    }

    public boolean getIsPhotoTaken(){

        return isPhotoTaken;

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

    public Context getContext(){

        return context;

    }

    public void setNullCommand(){

        currentCommandHandler = null;
        currentContext = null;

    }

    public TTS getTextToSpeech(){

        return textToSpeech;

    }

    public CommandHandler getCommandHandler(){

        return currentCommandHandler;

    }

    public int getCurrentActivity(){
        return currentActivity;
    }

    public Activity getActivity(){
        return activity;
    }

    public CommandHandlerContext getCurrentContext(){
        return currentContext;
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

    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    public static void destroyInstance() {
        instance = null;
    }
}

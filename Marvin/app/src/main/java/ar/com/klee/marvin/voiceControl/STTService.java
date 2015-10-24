package ar.com.klee.marvin.voiceControl;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.fragments.MainMenuFragment;
import ar.com.klee.marvin.multimedia.music.MusicService;
import ar.com.klee.marvin.sms.SMSDriver;
import ar.com.klee.marvin.social.NotificationService;
import ar.com.klee.marvin.voiceControl.handlers.LeerWhatsappHandler;
import ar.com.klee.marvin.voiceControl.handlers.ResponderSMSHandler;

/* Clase STT
** -Gestión del pasaje de audio a texto
*/
public class STTService extends Service {

    protected AudioManager mAudioManager;
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    private static STTService instance;

    private CommandHandlerManager commandHandlerManager;

    private boolean isListening;
    private boolean previousListening;

    private boolean sttState;

    public int numberOfWhatsapp = 0;
    public String whatsAppContact;

    private boolean wasPlayingMusic = false;

    LocalBroadcastManager broadcaster;
    public static final String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";
    public static final String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";

    private Thread isAlive;

    @Override
    public void onCreate(){
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,this.getPackageName());

        if(CommandHandlerManager.isInstanceInitialized()) {
            commandHandlerManager = CommandHandlerManager.getInstance();
        } else {
            commandHandlerManager = CommandHandlerManager.initializeInstance(getApplicationContext(), mSpeechRecognizer, mSpeechRecognizerIntent);
        }

        broadcaster = LocalBroadcastManager.getInstance(this);

        isListening = false;
        sttState = true;
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

        isAlive = new Thread() {

            @Override
            public void run() {
                try {

                    Thread.sleep(5000);

                    while (!isInterrupted()) {

                        try{
                            MusicService.getInstance();
                        }catch (IllegalStateException e) {
                            ((MainMenuActivity)CommandHandlerManager.getInstance().getMainActivity()).initializeMusicService();
                        }

                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                } catch (NullPointerException e) {
                }
            }
        };

        isAlive.start();

        instance = this;

        sendResult("Started");
    }

    public static STTService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getInstance");
        }
        return instance;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        instance = null;

        if (mSpeechRecognizer != null)
            mSpeechRecognizer.destroy();
    }

    public void stopListening(){

        if(sttState) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            sttState = false;

            //Log.d("STT", "stopListening");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected class SpeechRecognitionListener implements RecognitionListener{

        @Override
        public void onBeginningOfSpeech(){
            //Log.d("STT", "onBeginningOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer){

        }

        @Override
        public void onEndOfSpeech(){
            //Log.d("STT", "onEndOfSpeech");
        }

        @Override
        public void onError(final int error){

            //Log.d("STT", "onError");
            //Log.d("STT", getErrorText(error));

            if(error != SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        sttState = true;

                        if(error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {

                            if(MainMenuFragment.isInstanceInitialized()) {
                                MainMenuFragment.spokenText.setText("Hablá, yo escucho...");
                                MainMenuFragment.marvinImage.setImageResource(R.drawable.marvin_off);
                            }

                            if (!isListening &&
                                    commandHandlerManager.getCurrentActivity() == CommandHandlerManager.ACTIVITY_MAIN &&
                                    SMSDriver.getInstance().getInboxSize() != 0 &&
                                    commandHandlerManager.getCommandHandler() == null
                                    ) {

                                SMSDriver.getInstance().displayIncomingSMS();
                            }

                            if (NotificationService.isInstanceInitialized() &&
                                    !isListening &&
                                    commandHandlerManager.getCurrentActivity() == CommandHandlerManager.ACTIVITY_MAIN &&
                                    NotificationService.instance.checkMessageList() &&
                                    commandHandlerManager.getCommandHandler() == null
                                    ) {

                                whatsAppContact = NotificationService.instance.getNextContact();
                                numberOfWhatsapp = NotificationService.instance.getNumberOfMessages(whatsAppContact);

                                setIsListening(true);
                                ((MainMenuActivity) commandHandlerManager.getMainActivity()).setButtonsDisabled();
                                commandHandlerManager.setCurrentCommandHandler(new LeerWhatsappHandler(commandHandlerManager.getTextToSpeech(), commandHandlerManager.getContext(), commandHandlerManager));
                                commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "leer whatsapp")));

                            }
                        }
                        //Log.d("STT", "onErrorActivate");
                    }
                }, 1000);
            }else{
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        sttState = true;
                        //Log.d("STT", "onErrorActivate");
                    }
                }, 1000);
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params){

            //Log.d("STT", "onEvent");

        }

        @Override
        public void onPartialResults(Bundle partialResults){

            //Log.d("STT", "onPartialResults");

        }

        @Override
        public void onReadyForSpeech(Bundle params){

            //Log.d("STT", "onReadyForSpeech");

        }

        @Override
        public void onResults(Bundle results){

            sttState = false;

            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String text = matches.get(0);

            //Log.d("STT", "onResults");
            //Log.d("STT", text);

            previousListening = isListening;

            isListening = commandHandlerManager.detectCommand(text, isListening);

            if(text != null && text != "") {
                if (isListening && !previousListening) {
                    sendResult("Marvin");
                    if(commandHandlerManager.getCurrentActivity() >= CommandHandlerManager.ACTIVITY_CAMERA &&
                            MusicService.getInstance().isPlaying()){
                        wasPlayingMusic = true;
                        MusicService.getInstance().pause();
                    }
                }else if (isListening && previousListening) {
                    sendResult("Command " + text);
                }else if (!isListening && previousListening) {
                    sendResult("MarvinFinish " + text);
                    if(commandHandlerManager.getCurrentActivity() >= CommandHandlerManager.ACTIVITY_CAMERA){
                        if(wasPlayingMusic) {
                            commandHandlerManager.getTextToSpeech().setPlayMusic(true);
                            wasPlayingMusic = false;
                        }
                    }
                }
            }

        }

        @Override
        public void onRmsChanged(float rmsdB){

            //sendResult("PB " + (int) rmsdB);

        }

        /* Método getErrorText
        ** -Devuelve la descripcion de un codigo de error pasado por parametro
        */
        public String getErrorText(int errorCode) {
            String message;
            switch (errorCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "Audio recording error";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "Client side error";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient permissions";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "Network error";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "Network timeout";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "No match";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RecognitionService busy";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "error from server";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "No speech input";
                    break;
                default:
                    message = "Didn't understand, please try again.";
                    break;
            }

            return message;

        }

    }

    public void sendResult(String message) {
        Intent intent = new Intent(COPA_RESULT);
        if(message != null)
            intent.putExtra(COPA_MESSAGE, message);
        broadcaster.sendBroadcast(intent);

    }

    public void setIsListening(boolean listening){

        isListening = listening;

    }

    public boolean getIsListening(){

        return isListening;

    }

    public void setState(boolean state){

        sttState = state;

    }

    public void interruptThread(){
        isAlive.interrupt();
    }

    public static boolean isInstanceInitialized() {
        return instance != null;
    }

}
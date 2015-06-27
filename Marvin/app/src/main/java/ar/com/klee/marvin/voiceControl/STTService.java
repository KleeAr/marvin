package ar.com.klee.marvin.voiceControl;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

/* Clase STT
** -Gestión del pasaje de audio a texto
*/
public class STTService extends Service {

    protected AudioManager mAudioManager;
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;

    private CommandHandlerManager commandHandlerManager;

    private boolean isListening;
    private boolean previousListening;

    LocalBroadcastManager broadcaster;
    public static final String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";
    public static final String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";


    @Override
    public void onCreate(){
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,this.getPackageName());

        commandHandlerManager = CommandHandlerManager.initializeInstance(getApplicationContext(), mSpeechRecognizer, mSpeechRecognizerIntent);

        broadcaster = LocalBroadcastManager.getInstance(this);

        isListening = false;
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (mSpeechRecognizer != null)
            mSpeechRecognizer.destroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected class SpeechRecognitionListener implements RecognitionListener{

        @Override
        public void onBeginningOfSpeech(){
            //Log.d(TAG, "onBeginingOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onBufferReceived(byte[] buffer){

        }

        @Override
        public void onEndOfSpeech(){
            //Log.d(TAG, "onEndOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onError(int error){

            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

        }

        @Override
        public void onEvent(int eventType, Bundle params){

        }

        @Override
        public void onPartialResults(Bundle partialResults){

        }

        @Override
        public void onReadyForSpeech(Bundle params){

            //Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$

        }

        @Override
        public void onResults(Bundle results){

            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String text = matches.get(0);

            previousListening = isListening;

            isListening = commandHandlerManager.detectCommand(text, isListening);

            if(isListening && !previousListening)
                sendResult("Marvin");
            else if(isListening && previousListening)
                sendResult("Command " + text);
            else if(!isListening && previousListening)
                sendResult("MarvinFinish");

        }

        @Override
        public void onRmsChanged(float rmsdB){

            sendResult("PB " + (int) rmsdB);

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

}
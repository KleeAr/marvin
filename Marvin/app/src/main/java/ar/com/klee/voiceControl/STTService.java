package ar.com.klee.voiceControl;

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
import java.util.StringTokenizer;

/* Clase STT
** -Gestión del pasaje de audio a texto
*/
public class STTService extends Service {

    private static final String TAG = "MV - ";
    protected AudioManager mAudioManager;
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;

    public TTS textToSpeech;

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

        textToSpeech = new TTS(this, mSpeechRecognizer, mSpeechRecognizerIntent);

        broadcaster = LocalBroadcastManager.getInstance(this);

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

            Log.d(TAG, "Error "+getErrorText(error));

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

            if(validarResultado(text)) {

                sendResult("TV " + text);

                textToSpeech.speakText(text);

            }else {

                sendResult("TV Comando Incorrecto");

                textToSpeech.speakText("Comando Incorrecto");

            }

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

    /* Método validarResultado
    ** -Valida si el comando es correcto o no
    */
    public boolean validarResultado(String result){

        StringTokenizer st = new StringTokenizer(result);

        String keyToken = st.nextToken();

        if(keyToken.equals("Marvin") || keyToken.equals("marvin"))
            return true;
        else
            return false;
    }

    public void sendResult(String message) {
        Intent intent = new Intent(COPA_RESULT);
        if(message != null)
            intent.putExtra(COPA_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

}
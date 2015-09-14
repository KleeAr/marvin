package ar.com.klee.marvin.voiceControl;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

import ar.com.klee.marvin.activities.MainMenuActivity;

/* Clase TTS
** -Gestión del pasaje de textos a audio
*/
public class TTS {

    private TextToSpeech ttsObject;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean firstTime = true;

    /* Constructor de la clase TTS
    ** -Inicializa el objeto de la clase TextToSpeech que nos va a permitir reproducir texto
    ** -param.context: Se le debe pasar el contexto de ejecución
    */
    public TTS(Context context, final SpeechRecognizer mSpeechRecognizer, final Intent mSpeechRecognizerIntent) {

        this.mSpeechRecognizer = mSpeechRecognizer;

        this.mSpeechRecognizerIntent = mSpeechRecognizerIntent;

        // Creamos el objeto TextToSpeech que nos va a permitir gestionar la reproduccion de textos
        ttsObject = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {

                    ttsObject.setLanguage(new Locale("spa", "AR"));
                    ttsObject.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {

                        @Override
                        public void onUtteranceCompleted(String utteranceId) {

                            if (utteranceId.equals("FINISH_SPEAK")) {
                                //Log.d("TTS","Salta alarma");
                                ((MainMenuActivity)CommandHandlerManager.getInstance().getMainActivity()).activate(mSpeechRecognizer, mSpeechRecognizerIntent);
                            }

                        }
                    });
                }
            }
        });

    }

    /* Método speakText
    ** -Reproduce un texto
    ** -param.textToSpeak: String con el texto a ser reproducido
    */
    public void speakText(String textToSpeak) {

        STTService.getInstance().stopListening();

        HashMap<String, String> myHashAlarm = new HashMap<String, String>();
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "FINISH_SPEAK");

        //Log.d("TTS","Reproduce texto");

        // Reproduce el texto
        ttsObject.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, myHashAlarm);

    }

    public int speakTextWithoutStart(String textToSpeak){

        STTService.getInstance().stopListening();

        ttsObject.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);

        int delayTime = textToSpeak.length()/5 + 1;
        return (delayTime * 550);

    }

    /* Método finishSpeak
    ** -Libera los recursos del TextToSpeak engine
    */
    public void finishSpeak() {

        ttsObject.stop();
        ttsObject.shutdown();

    }

}

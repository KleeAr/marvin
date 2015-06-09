package ar.com.klee.marvin.voiceControl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

/* Clase TTS
** -Gestión del pasaje de textos a audio
*/
public class TTS {

    private TextToSpeech ttsObject;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;

    /* Constructor de la clase TTS
    ** -Inicializa el objeto de la clase TextToSpeech que nos va a permitir reproducir texto
    ** -param.context: Se le debe pasar el contexto de ejecución
    */
    public TTS(Context context, SpeechRecognizer mSpeechRecognizer, Intent mSpeechRecognizerIntent) {

        // Creamos el objeto TextToSpeech que nos va a permitir gestionar la reproduccion de textos
        ttsObject = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    ttsObject.setLanguage(new Locale("spa", "AR"));
                }
            }
        });

        this.mSpeechRecognizer = mSpeechRecognizer;

        this.mSpeechRecognizerIntent = mSpeechRecognizerIntent;

    }

    /* Método speakText
    ** -Reproduce un texto
    ** -param.textToSpeak: String con el texto a ser reproducido
    */
    public void speakText(String textToSpeak) {

        int delayTime = textToSpeak.length()/15 + 1;
        delayTime = delayTime * 2000;

        // Reproduce el texto
        ttsObject.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);

        // Execute some code after delayTime seconds have passed
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }
        }, delayTime);

    }

    /* Método finishSpeak
    ** -Libera los recursos del TextToSpeak engine
    */
    public void finishSpeak() {

        ttsObject.stop();
        ttsObject.shutdown();

    }

}

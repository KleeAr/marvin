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

import ar.com.klee.marvin.activities.CallHistoryActivity;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.activities.SMSInboxActivity;
import ar.com.klee.marvin.gps.LocationSender;
import ar.com.klee.marvin.multimedia.music.MusicService;
import ar.com.klee.marvin.sms.SMSDriver;

/* Clase TTS
** -Gestión del pasaje de textos a audio
*/
public class TTS {

    private TextToSpeech ttsObject;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean speedAlert = false;
    private boolean playMusic = false;
    private boolean smsRead = false;
    private boolean smsRespond = false;
    private boolean inbox = false;
    private boolean inboxRespond = false;
    private boolean call = false;
    private boolean callRespond = false;

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
                                MainMenuActivity mainMenuActivity = ((MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity());
                                mainMenuActivity.activate(mSpeechRecognizer, mSpeechRecognizerIntent);

                                if(playMusic){
                                    playMusic = false;
                                    MusicService.getInstance().startPlaying();
                                }

                                if(speedAlert){
                                    speedAlert = false;
                                    LocationSender.getInstance().setSpeedAlertActivated(1);
                                }

                                if(smsRead){
                                    smsRead = false;
                                    SMSDriver.getInstance().enableButtons();
                                }else if(smsRespond){
                                    smsRespond = false;
                                    SMSDriver.getInstance().enableButtonsRespond();
                                }else if(inbox){
                                    inbox = false;
                                    ((SMSInboxActivity)CommandHandlerManager.getInstance().getActivity()).enableButtons();
                                }else if(inboxRespond){
                                    inboxRespond = false;
                                    ((SMSInboxActivity) CommandHandlerManager.getInstance().getActivity()).enableButtonsRespond();
                                }else if(call){
                                    call = false;
                                    ((CallHistoryActivity)CommandHandlerManager.getInstance().getActivity()).enableButtons();
                                }else if(callRespond){
                                    callRespond = false;
                                    ((CallHistoryActivity) CommandHandlerManager.getInstance().getActivity()).enableButtonsRespond();
                                }

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

        if(textToSpeak.startsWith("SPEED ALERT - ")){
            textToSpeak = textToSpeak.replace("SPEED ALERT - ","");
            speedAlert = true;
        }else{
            speedAlert = false;
        }

        if(textToSpeak.startsWith("SMS - ")){
            textToSpeak = textToSpeak.replace("SMS - ","");
            smsRead = true;
        }else{
            smsRead = false;
            if(textToSpeak.startsWith("SMSR - ")){
                textToSpeak = textToSpeak.replace("SMSR - ","");
                smsRespond = true;
            }else{
                smsRespond = false;
            }
        }

        if(textToSpeak.startsWith("INBOX - ")){
            textToSpeak = textToSpeak.replace("INBOX - ","");
            inbox = true;
        }else{
            inbox = false;
            if(textToSpeak.startsWith("INBOXR - ")){
                textToSpeak = textToSpeak.replace("INBOXR - ","");
                inboxRespond = true;
            }else{
                inboxRespond = false;
            }
        }

        if(textToSpeak.startsWith("CALL - ")){
            textToSpeak = textToSpeak.replace("CALL - ","");
            call = true;
        }else{
            call = false;
            if(textToSpeak.startsWith("CALLR - ")){
                textToSpeak = textToSpeak.replace("CALLR - ","");
                callRespond = true;
            }else{
                callRespond = false;
            }
        }

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

    public void setPlayMusic(boolean value){
        playMusic = value;
    }

}

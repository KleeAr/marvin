package ar.com.klee.marvin.configuration;


import android.content.Context;
import android.content.Intent;
import android.speech.SpeechRecognizer;

public class UserConfig {

    private static UserConfig instance;

    private Long miniumTripTime; //Guardado en horas
    private Long miniumTripDistance; //Guardado en kilómetros
    private String emergencyNumber; //Número al que enviarle un sms de emergencia
    private String emergencySMS; //Sms de emergencia a enviar

    public static UserConfig getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getInstance");
        }
        return instance;
    }

    public static UserConfig initializeInstance() {
        if(instance != null) {
            throw new IllegalStateException("Instance already initialized");
        }
        UserConfig.instance = new UserConfig();
        return instance;
    }

    public UserConfig(){



    }

    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    public static void destroyInstance() {
        instance = null;
    }

}

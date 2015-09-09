package ar.com.klee.marvin.configuration;


import android.content.Context;
import android.content.Intent;
import android.speech.SpeechRecognizer;

public class UserConfig {

    private static UserConfig instance;

    public static final int ORIENTATION_PORTRAIT = 0;
    public static final int ORIENTATION_LANDSCAPE = 1;

    private Long miniumTripTime; //Guardado en horas
    private Long miniumTripDistance; //Guardado en kilómetros
    private String emergencyNumber; //Número al que enviarle un sms de emergencia
    private String emergencySMS; //Sms de emergencia a enviar
    private int orientation; //Orientación de la pantalla
    private boolean openAppWhenStop = false; //Indica si está activada la opción de abrir una app al detenerse
    private String appToOpenWhenStop; //Indica el nombre de la aplicación a abrir al detenerse
    private String hotspotName = "MRVN"; //Nombre de la red creada
    private String hotspotPassword = "marvinHotSpot"; //Contraseña de la red creada

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

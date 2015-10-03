package ar.com.klee.marvin.configuration;


import ar.com.klee.marvin.client.model.UserSetting;

public class UserConfig {

    private static UserConfig instance;

    public static final int ORIENTATION_PORTRAIT = 0;
    public static final int ORIENTATION_LANDSCAPE = 1;

    //private Long miniumTripTime; //Guardado en minutos
    //private Long miniumTripDistance; //Guardado en metros
    //private String emergencyNumber; //Número al que enviarle un sms de emergencia
    //private String emergencySMS; //Sms de emergencia a enviar
    //private int orientation; //Orientación de la pantalla
    //private boolean openAppWhenStop = false; //Indica si está activada la opción de abrir una app al detenerse
    //private String appToOpenWhenStop; //Indica el nombre de la aplicación a abrir al detenerse
    //private String hotspotName = "MRVN"; //Nombre de la red creada
    //private String hotspotPassword = "marvinHotSpot"; //Contraseña de la red creada
    private static UserSetting settings;

    public static UserSetting getSettings() {
        if (settings == null) {
            settings = new UserSetting();
        }
        return settings;
    }

    public UserConfig(){
        instance = this;
    }

    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    public static void destroyInstance() {
        instance = null;
    }

    public static void setSettings(UserSetting settings) {
        UserConfig.settings = settings;
    }
}

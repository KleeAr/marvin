package ar.com.klee.marvin.configuration;


import ar.com.klee.marvin.client.model.UserSetting;

public class UserConfig {

    private static UserConfig instance;
    private long userId;
    private String userName;
    private String mail;

    public static final int ORIENTATION_PORTRAIT = 0;
    public static final int ORIENTATION_LANDSCAPE = 1;

    //private static int miniumTripTime; //Guardado en minutos
    //private static int miniumTripDistance; //Guardado en kilómetros
    //private static boolean emergencyEnable;
    //private static String emergencyNumber; //Número al que enviarle un sms de emergencia
    //private static String emergencySMS; //Sms de emergencia a enviar
    //private static int orientation; //Orientación de la pantalla
    //private static boolean openAppWhenStop = false; //Indica si está activada la opción de abrir una app al detenerse
    //private static String appToOpenWhenStop; //Indica el nombre de la aplicación a abrir al detenerse
    //private static String hotspotName = "MRVN"; //Nombre de la red creada
    //private static String hotspotPassword = "marvinHotSpot"; //Contraseña de la red creada
    //private static int alertSpeed; //Velocidad a la que se emite el alerta
    //private static boolean speedAlertEnabled = false; //Indica si está habilitado el alerta de velocidad
    private static UserSetting settings;
    private int orientation;

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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public static UserConfig getInstance(){
        return instance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}

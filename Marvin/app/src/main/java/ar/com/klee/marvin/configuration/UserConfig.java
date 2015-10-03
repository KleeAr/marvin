package ar.com.klee.marvin.configuration;


import ar.com.klee.marvin.client.model.UserSetting;

public class UserConfig {

    private static UserConfig instance;

    public static final int ORIENTATION_PORTRAIT = 0;
    public static final int ORIENTATION_LANDSCAPE = 1;

    private static int miniumTripTime; //Guardado en horas
    private static int miniumTripDistance; //Guardado en kilómetros
    private static boolean emergencyEnable;
    private static String emergencyNumber; //Número al que enviarle un sms de emergencia
    private static String emergencySMS; //Sms de emergencia a enviar
    private static int orientation; //Orientación de la pantalla
    private static boolean openAppWhenStop = false; //Indica si está activada la opción de abrir una app al detenerse
    private static String appToOpenWhenStop; //Indica el nombre de la aplicación a abrir al detenerse
    private static String hotspotName = "MRVN"; //Nombre de la red creada
    private static String hotspotPassword = "marvinHotSpot"; //Contraseña de la red creada
    private static UserSetting settings;

    public static UserSetting getSettings() {
        if (settings == null) {
            settings = new UserSetting();
        }
        return settings;
    }

    private UserConfig(){
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

    public static int getMiniumTripTime() {
        return miniumTripTime;
    }

    public static void setMiniumTripTime(int miniumTripTime) {
        UserConfig.miniumTripTime = miniumTripTime;
    }

    public static int getMiniumTripDistance() {
        return miniumTripDistance;
    }

    public static void setMiniumTripDistance(int miniumTripDistance) {
        UserConfig.miniumTripDistance = miniumTripDistance;
    }


    public static boolean isEmergencyEnable(boolean b) {
        return emergencyEnable;
    }

    public static void setEmergencyEnable(boolean emergencyEnable) {
        UserConfig.emergencyEnable = emergencyEnable;
    }

    public String getEmergencyNumber() {
        return emergencyNumber;
    }

    public static void setEmergencyNumber(String emergencyNumber) {
        UserConfig.emergencyNumber = emergencyNumber;
    }

    public String getEmergencySMS() {
        return emergencySMS;
    }

    public static void setEmergencySMS(String emergencySMS) {
        UserConfig.emergencySMS = emergencySMS;
    }

    public int getOrientation() {
        return orientation;
    }

    public static void setOrientation(int orientation) {
        UserConfig.orientation = orientation;
    }

    public boolean isOpenAppWhenStop() {
        return openAppWhenStop;
    }

    public static void setOpenAppWhenStop(boolean openAppWhenStop) {
        UserConfig.openAppWhenStop = openAppWhenStop;
    }

    public String getAppToOpenWhenStop() {
        return appToOpenWhenStop;
    }

    public static void setAppToOpenWhenStop(String appToOpenWhenStop) {
        UserConfig.appToOpenWhenStop = appToOpenWhenStop;
    }

    public String getHotspotName() {
        return hotspotName;
    }

    public static void setHotspotName(String hotspotName) {
        UserConfig.hotspotName = hotspotName;
    }

    public String getHotspotPassword() {
        return hotspotPassword;
    }

    public static void setHotspotPassword(String hotspotPassword) {
        UserConfig.hotspotPassword = hotspotPassword;
    }


}

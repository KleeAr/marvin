package ar.com.klee.marvin.hotspot;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ar.com.klee.marvin.client.model.User;
import ar.com.klee.marvin.configuration.UserConfig;

public class WiFiHotspot {

    private Context context;

    public WiFiHotspot(Context context){

        this.context = context;

    }

    public boolean createWifiAccessPoint() {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
        }
        Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
        boolean methodFound=false;
        for(Method method: wmMethods){
            if(method.getName().equals("setWifiApEnabled")){
                methodFound=true;
                WifiConfiguration netConfig = new WifiConfiguration();
                netConfig.SSID = UserConfig.getSettings().getHotspotName();
                netConfig.preSharedKey = UserConfig.getSettings().getHotspotPassword();
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                try {
                    boolean apstatus=(Boolean) method.invoke(wifiManager, netConfig,true);
                    //Log.d("HTSP", "Creating a Wi-Fi Network \"" + netConfig.SSID + "\"");
                    for (Method isWifiApEnabledmethod: wmMethods){
                        if(isWifiApEnabledmethod.getName().equals("isWifiApEnabled")){
                            while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){
                                synchronized(this){
                                    wait(500);
                                }
                            };
                            for(Method method1: wmMethods){
                                if(method1.getName().equals("getWifiApState")){
                                    int apstate;
                                    apstate=(Integer)method1.invoke(wifiManager);
                                    //                    netConfig=(WifiConfiguration)method1.invoke(wifi);
                                    //Log.d("HTSP","\nSSID:"+netConfig.SSID+"  Password:"+netConfig.preSharedKey+"\n");
                                }
                            }
                        }
                    }
                    if(apstatus){
                        Log.d("HTSP", "Success creating AP");
                        return true;
                    }else{
                        Log.d("HTSP", "Failed creating AP");
                        return false;
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if(!methodFound){
            Log.d("HTSP","Your phone's API does not contain setWifiApEnabled method to configure an access point");
            return false;
        }

        return false;
    }

    public boolean removeWifiAccessPoint() {
        WifiManager wifimanager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            // if WiFi is on, turn it off
            wifimanager.setWifiEnabled(false);
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, false);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

}

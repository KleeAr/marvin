package ar.com.klee.marvin.applications;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.ApplicationList;
import ar.com.klee.marvin.activities.MainMenuActivity;

public class Application implements Serializable {

    String name; //Nombre de la aplicación
    String packageName; //nombre del paquete de la aplicación, necesario para ejecutar la app
    Drawable icon; // icono de la aplicación
    boolean configured; //setea si la app esta habilitada en un boton o no

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public Application(String name, String packageName, Drawable icon, boolean configured) {
        this.name = name;
        this.packageName = packageName;
        this.icon =icon;
        this.configured = configured;

    }
    public Application(String name, String packageName, Drawable icon) {
        this.name = name;
        this.packageName = packageName;
        this.icon =icon;

    }


}

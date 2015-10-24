package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.applications.Application;
import ar.com.klee.marvin.applications.ApplicationAdapter;
import ar.com.klee.marvin.configuration.UserConfig;
import ar.com.klee.marvin.fragments.MainMenuFragment;
import ar.com.klee.marvin.gps.Site;


public class ApplicationList extends Activity {

    private List<Application> appsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(UserConfig.getInstance().getOrientation() == UserConfig.ORIENTATION_PORTRAIT)
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        setContentView(R.layout.activity_application_list);

        Bundle bundle = getIntent().getExtras();
        int buttonClick=bundle.getInt("buttonClick");


        getInstalledAppList(buttonClick);
    }



    public void getInstalledAppList(final int buttonClick) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appsList  = new ArrayList<>();
        final List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);

        boolean setContacts = false;
        boolean setGoogle = false;
        boolean setGoogleApp = false;

        for (Object object : pkgAppsList) {
            //Recorrido de la lista de las app instaladas
            ResolveInfo info = (ResolveInfo) object;
            Drawable icon = getBaseContext().getPackageManager().getApplicationIcon(info.activityInfo.applicationInfo);
            String appName = getBaseContext().getPackageManager().getApplicationLabel(info.activityInfo.applicationInfo).toString();
            String packageName  = info.activityInfo.applicationInfo.packageName;
            //String appDir  	= info.activityInfo.applicationInfo.publicSourceDir.toString(); Por ahora no me parece necesario

            if(!appName.equals("Marvin")) {
                if((!appName.equals("Aplicación Google")||!setGoogleApp)&&(!appName.equals("Google+")||!setGoogle)&&(!appName.equals("Contactos")||!setContacts)) {
                    Application tmp = new Application(appName, packageName, icon);
                    appsList.add(tmp);
                }

                if(appName.equals("Aplicación Google"))
                    setGoogleApp = true;
                else if(appName.equals("Google+"))
                    setGoogle = true;
                else if(appName.equals("Contactos"))
                    setContacts = true;
            }
        }

        List<Application> marvinApps = Arrays.asList(
                new Application("Marvin - Cámara","ar.com.klee.marvin",getResources().getDrawable(getResources().getIdentifier("drawable/ic_camera",null,getPackageName()))),
                new Application("Marvin - Comandos de voz","ar.com.klee.marvin",getResources().getDrawable(getResources().getIdentifier("drawable/ic_voice",null,getPackageName()))),
                new Application("Marvin - Configuración","ar.com.klee.marvin",getResources().getDrawable(getResources().getIdentifier("drawable/ic_configuration",null,getPackageName()))),
                new Application("Marvin - Dónde estacioné","ar.com.klee.marvin",getResources().getDrawable(getResources().getIdentifier("drawable/ic_parking",null,getPackageName()))),
                new Application("Marvin - Historial de llamadas","ar.com.klee.marvin",getResources().getDrawable(getResources().getIdentifier("drawable/ic_call_history",null,getPackageName()))),
                new Application("Marvin - Historial de sms","ar.com.klee.marvin",getResources().getDrawable(getResources().getIdentifier("drawable/ic_sms_history",null,getPackageName()))),
                new Application("Marvin - Historial de viajes","ar.com.klee.marvin",getResources().getDrawable(getResources().getIdentifier("drawable/ic_clock",null,getPackageName()))),
                new Application("Marvin - Mapa","ar.com.klee.marvin",getResources().getDrawable(getResources().getIdentifier("drawable/ic_map",null,getPackageName()))),
                new Application("Marvin - Mis sitios","ar.com.klee.marvin",getResources().getDrawable(getResources().getIdentifier("drawable/ic_places",null,getPackageName()))),
                new Application("Marvin - Perfil","ar.com.klee.marvin",getResources().getDrawable(getResources().getIdentifier("drawable/ic_profile",null,getPackageName()))),
                new Application("Marvin - Salir","ar.com.klee.marvin",getResources().getDrawable(getResources().getIdentifier("drawable/ic_close",null,getPackageName())))
        );

        int i = 0;

        while(i<marvinApps.size()){
            appsList.add(marvinApps.get(i));
            i++;
        }

        java.util.Collections.sort(appsList,new AppComparator());

        ListView listview = (ListView) findViewById(R.id.listView);
        final ApplicationAdapter adapter = new ApplicationAdapter(this, appsList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Application app=(Application) parent.getAdapter().getItem(position); //recupera el objeto en la posición deseada

                //Setea los valores del boton
                MainMenuFragment.shortcutList[buttonClick].setPackageName(app.getPackageName());
                MainMenuFragment.shortcutList[buttonClick].setName(app.getName());
                MainMenuFragment.shortcutList[buttonClick].setIcon(app.getIcon());
                MainMenuFragment.shortcutList[buttonClick].setConfigured(true);

               // Toast.makeText(getApplicationContext(), Tab1.shortcutList[buttonClick].getName()+buttonClick, Toast.LENGTH_SHORT).show();


                MainMenuFragment.shortcutButton[buttonClick].setImageDrawable(app.getIcon());
                MainMenuFragment.shortcutButton[buttonClick].setBackgroundColor(Color.WHITE);

                // Creamos la instancia de "SharedPreferences"
                // Y también la "SharedPreferences.Editor"
                SharedPreferences settings = getSharedPreferences("PREFERENCES", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("ButtonPack"+buttonClick, app.getPackageName());
                editor.putString("ButtonName"+buttonClick, app.getName());
                editor.putBoolean("ButtonConfig"+buttonClick, true);
                editor.commit();

                finish();

            }
        });


    }

    class AppComparator implements Comparator<Application> {
        @Override
        public int compare(Application a, Application b) {
            return a.getName().compareToIgnoreCase(b.getName());
        }
    }

}

package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.applications.Application;
import ar.com.klee.marvin.applications.ApplicationAdapter;


public class ApplicationList extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);

        Bundle bundle = getIntent().getExtras();
        int buttonClick=bundle.getInt("buttonClick");


        getInstalledAppList(buttonClick);
    }



    public void getInstalledAppList(final int buttonClick) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<Application> appsList = new ArrayList<>();
        final List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
        for (Object object : pkgAppsList) {
            //Recorrido de la lista de las app instaladas
            ResolveInfo info = (ResolveInfo) object;
            Drawable icon = getBaseContext().getPackageManager().getApplicationIcon(info.activityInfo.applicationInfo);
            String appName = getBaseContext().getPackageManager().getApplicationLabel(info.activityInfo.applicationInfo).toString();
            String packageName  = info.activityInfo.applicationInfo.packageName;
            //String appDir  	= info.activityInfo.applicationInfo.publicSourceDir.toString(); Por ahora no me parece necesario

            Application tmp = new Application(appName,packageName,icon);
            appsList.add(tmp);
        }
        ListView listview = (ListView) findViewById(R.id.listView);
        final ApplicationAdapter adapter = new ApplicationAdapter(this, appsList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Application app=(Application) parent.getAdapter().getItem(position); //recupera el objeto en la posición deseada

                //Setea los valores del boton
                MainMenuActivity.shortcutList[buttonClick].setPackageName(app.getPackageName());
                MainMenuActivity.shortcutList[buttonClick].setName(app.getName());
                MainMenuActivity.shortcutList[buttonClick].setIcon(app.getIcon());
                MainMenuActivity.shortcutList[buttonClick].setConfigured(true);

               // Toast.makeText(getApplicationContext(), Tab1.shortcutList[buttonClick].getName()+buttonClick, Toast.LENGTH_SHORT).show();


                MainMenuActivity.shortcutButton[buttonClick].setImageDrawable(app.getIcon());
                MainMenuActivity.shortcutButton[buttonClick].setBackgroundColor(Color.WHITE);

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




}

package ar.com.klee.marvinSimulator.activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import ar.com.klee.marvinSimulator.fragments.MainMenuFragment;


public class Tab2 extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(ar.com.klee.marvinSimulator.R.layout.tab_2,container,false);

        MainMenuFragment.shortcutButton[6] = (ImageButton) v.findViewById(ar.com.klee.marvinSimulator.R.id.imageButton1);
        MainMenuFragment.shortcutButton[7] = (ImageButton) v.findViewById(ar.com.klee.marvinSimulator.R.id.imageButton2);
        MainMenuFragment.shortcutButton[8] = (ImageButton) v.findViewById(ar.com.klee.marvinSimulator.R.id.imageButton3);
        MainMenuFragment.shortcutButton[9] = (ImageButton) v.findViewById(ar.com.klee.marvinSimulator.R.id.imageButton4);
        MainMenuFragment.shortcutButton[10] = (ImageButton) v.findViewById(ar.com.klee.marvinSimulator.R.id.imageButton5);
        MainMenuFragment.shortcutButton[11] = (ImageButton) v.findViewById(ar.com.klee.marvinSimulator.R.id.imageButton6);
        //re-establece la imagen de los botones que estan configurados
        for (int i=6; i<12;i++)
            if(MainMenuFragment.shortcutList[i].isConfigured()){
                MainMenuFragment.shortcutButton[i].setBackgroundColor(Color.WHITE);
                MainMenuFragment.shortcutButton[i].setImageDrawable(MainMenuFragment.shortcutList[i].getIcon());

            }

        MainMenuFragment.shortcutButton[6].setOnClickListener(this);
        MainMenuFragment.shortcutButton[7].setOnClickListener(this);
        MainMenuFragment.shortcutButton[8].setOnClickListener(this);
        MainMenuFragment.shortcutButton[9].setOnClickListener(this);
        MainMenuFragment.shortcutButton[10].setOnClickListener(this);
        MainMenuFragment.shortcutButton[11].setOnClickListener(this);

        MainMenuFragment.shortcutButton[6].setOnLongClickListener(this);
        MainMenuFragment.shortcutButton[7].setOnLongClickListener(this);
        MainMenuFragment.shortcutButton[8].setOnLongClickListener(this);
        MainMenuFragment.shortcutButton[9].setOnLongClickListener(this);
        MainMenuFragment.shortcutButton[10].setOnLongClickListener(this);
        MainMenuFragment.shortcutButton[11].setOnLongClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v){
        int index=6;
        // do what I need to do when a button is clicked here...
        switch (v.getId())
        {
            case ar.com.klee.marvinSimulator.R.id.imageButton1:
                index = 6;
                break;

            case ar.com.klee.marvinSimulator.R.id.imageButton2:
                index = 7;
                break;
            case ar.com.klee.marvinSimulator.R.id.imageButton3:
                index = 8;
                break;

            case ar.com.klee.marvinSimulator.R.id.imageButton4:
                index = 9;
                break;
            case ar.com.klee.marvinSimulator.R.id.imageButton5:
                index = 10;
                break;

            case ar.com.klee.marvinSimulator.R.id.imageButton6:
                index = 11;
                break;
        }
        if (!MainMenuFragment.shortcutList[index].isConfigured()) {
            //Si el boton no esta configurado lanza el listado de apps instaladas
            Intent i = new Intent(getActivity().getApplicationContext(), ApplicationList.class);
            i.putExtra("buttonClick", index);
            startActivity(i);
        } else {//sino lanza la aplicación seteada
            switch (MainMenuFragment.shortcutList[index].getName()) {
                case "Marvin - Cámara":
                    Intent cameraIntent = new Intent(getActivity(), CameraActivity.class);
                    getActivity().startActivity(cameraIntent);
                    break;
                case "Marvin - Comandos de voz":
                    MainMenuFragment.getInstance().getPager().setCurrentItem(0);
                    ((MainMenuActivity) getActivity()).previousMenus.push(1);
                    ((MainMenuActivity)getActivity()).setFragment(2);
                    break;
                case "Marvin - Configuración":
                    MainMenuFragment.getInstance().getPager().setCurrentItem(0);
                    ((MainMenuActivity) getActivity()).previousMenus.push(1);
                    ((MainMenuActivity)getActivity()).setFragment(8);
                    break;
                case "Marvin - Dónde estacioné":
                    MainMenuFragment.getInstance().getPager().setCurrentItem(0);
                    ((MainMenuActivity) getActivity()).previousMenus.push(1);
                    ((MainMenuActivity)getActivity()).setFragment(6);
                    break;
                case "Marvin - Historial de llamadas":
                    Intent callHistory = new Intent(getActivity(), CallHistoryActivity.class);
                    getActivity().startActivity(callHistory);
                    break;
                case "Marvin - Historial de sms":
                    Intent smsInbox = new Intent(getActivity(), SMSInboxActivity.class);
                    getActivity().startActivity(smsInbox);
                    break;
                case "Marvin - Historial de viajes":
                    MainMenuFragment.getInstance().getPager().setCurrentItem(0);
                    ((MainMenuActivity) getActivity()).previousMenus.push(1);
                    ((MainMenuActivity)getActivity()).setFragment(4);
                    break;
                case "Marvin - Mapa":
                    Intent map = new Intent(getActivity(), MapActivity.class);
                    getActivity().startActivity(map);
                    break;
                case "Marvin - Mis sitios":
                    MainMenuFragment.getInstance().getPager().setCurrentItem(0);
                    ((MainMenuActivity) getActivity()).previousMenus.push(1);
                    ((MainMenuActivity)getActivity()).setFragment(5);
                    break;
                case "Marvin - Perfil":
                    MainMenuFragment.getInstance().getPager().setCurrentItem(0);
                    ((MainMenuActivity) getActivity()).previousMenus.push(1);
                    ((MainMenuActivity)getActivity()).setFragment(0);
                    break;
                case "Marvin - Salir":
                    MainMenuFragment.getInstance().setItem(2);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if(MainMenuFragment.isInstanceInitialized())
                                MainMenuFragment.getInstance().stopThread();
                            MainMenuActivity.mapFragment.finishTrip();
                            ((MainMenuActivity)getActivity()).stopServices();
                        }
                    }, 1000);
                    break;
                default:
                    startNewActivity(getActivity().getApplicationContext(), MainMenuFragment.shortcutList[index].getPackageName());
                    break;
            }

        }
    }
    @Override
    public boolean onLongClick(View v) {
        int index=6;
        // do what I need to do when a button is clicked here...
        switch (v.getId())
        {
            case ar.com.klee.marvinSimulator.R.id.imageButton1:
                index = 6;
                break;

            case ar.com.klee.marvinSimulator.R.id.imageButton2:
                index = 7;
                break;
            case ar.com.klee.marvinSimulator.R.id.imageButton3:
                index = 8;
                break;

            case ar.com.klee.marvinSimulator.R.id.imageButton4:
                index = 9;
                break;
            case ar.com.klee.marvinSimulator.R.id.imageButton5:
                index = 10;
                break;

            case ar.com.klee.marvinSimulator.R.id.imageButton6:
                index = 11;
                break;
        }
        if (MainMenuFragment.shortcutList[index].isConfigured())
            multipleChoiceDialog(index);
        else
            addApplicationDialog(index);
        return false;
    }



    private void addApplicationDialog(final int buttonNumber) {
        final CharSequence[] items = {"Agregar Atajo"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Intent i = new Intent(getActivity().getApplicationContext(), ApplicationList.class);
                i.putExtra("buttonClick", buttonNumber);
                startActivity(i);
            }
        });
        dialog.show();
    }

    private void multipleChoiceDialog(final int buttonNumber) {
        final CharSequence[] items = {"Cambiar atajo", "Borrar atajo"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());//this);
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent i = new Intent(getActivity().getApplicationContext(), ApplicationList.class);
                    i.putExtra("buttonClick", buttonNumber);
                    startActivity(i);
                } else {
                    //borra la memoria del boton
                    MainMenuFragment.shortcutList[buttonNumber].setConfigured(false);
                    MainMenuFragment.shortcutButton[buttonNumber].setBackgroundColor(Color.parseColor("#a3d9d1"));
                    MainMenuFragment.shortcutButton[buttonNumber].setImageResource(ar.com.klee.marvinSimulator.R.drawable.ic_plus);

                    // Creamos la instancia de "SharedPreferences"
                    // Y también la "SharedPreferences.Editor"
                    SharedPreferences settings = getActivity().getSharedPreferences("PREFERENCES", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("ButtonPack"+buttonNumber, "");
                    editor.putString("ButtonName"+buttonNumber, "");
                    editor.putBoolean("ButtonConfig"+buttonNumber, false);
                    editor.commit();

                }
            }
        });
        dialog.show();
    }

    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + packageName));
            context.startActivity(intent);
        }
    }
}
package ar.com.klee.marvinSimulator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.SimpleDateFormat;

import ar.com.klee.marvinSimulator.SlidingTabLayout;
import ar.com.klee.marvinSimulator.ViewPagerAdpater;
import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.applications.Application;
import ar.com.klee.marvinSimulator.gps.LocationSender;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;

public class MainMenuFragment extends Fragment {

    private static MainMenuFragment instance = null;
    public final int CANT_APPLICATION=12; //variable en que se definen la cantidad de aplicaciones disponibles

    private long date;
    private SlidingTabLayout tabs;

    public static ImageButton bt_play;
    public static ImageButton bt_next;
    public static ImageButton bt_previous;
    public static ImageButton bt_radioMusic;
    public static ProgressBar loadingRadio;
    public static TextView tv_song;
    public static TextView tv_artist;
    public static TextView spokenText;
    public static ImageView marvinImage;

    public static boolean isRadio;

    public static TextView mainStreet;
    public static TextView speed;

    public static Application[] shortcutList; //lista para almacenar las aplicaciones configuradas
    public static ImageButton[] shortcutButton;

    private ViewPager pager;
    private ViewPagerAdpater adapter;
    private CharSequence Titles[]={"Home","Aplicacion","Mapa"};
    int NumbOfTabs = 3;

    private Thread tTime = null;

    private View v;

    public MainMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(ar.com.klee.marvinSimulator.R.layout.fragment_main_menu, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("musicService", Context.MODE_PRIVATE);
        if(instance==null)
            isRadio = sharedPreferences.getBoolean("isRadio",false);

        mainStreet = (TextView)v.findViewById(ar.com.klee.marvinSimulator.R.id.mainStreet);

        bt_play = (ImageButton) v.findViewById(ar.com.klee.marvinSimulator.R.id.bt_play);
        bt_next = (ImageButton) v.findViewById(ar.com.klee.marvinSimulator.R.id.bt_next);
        bt_previous = (ImageButton) v.findViewById(ar.com.klee.marvinSimulator.R.id.bt_previous);
        bt_radioMusic = (ImageButton) v.findViewById(ar.com.klee.marvinSimulator.R.id.bt_radioMusic);
        loadingRadio = (ProgressBar) v.findViewById(ar.com.klee.marvinSimulator.R.id.loadingRadio);

        loadingRadio.setVisibility(ProgressBar.INVISIBLE);
        bt_play.setVisibility(ImageButton.VISIBLE);

        if(isRadio)
            bt_radioMusic.setImageResource(ar.com.klee.marvinSimulator.R.mipmap.ic_radio_white_48dp);
        else
            bt_radioMusic.setImageResource(ar.com.klee.marvinSimulator.R.mipmap.ic_audiotrack_white_48dp);

        tv_song = (TextView)v.findViewById(ar.com.klee.marvinSimulator.R.id.song);
        tv_artist = (TextView)v.findViewById(ar.com.klee.marvinSimulator.R.id.artist);

        spokenText = (TextView) v.findViewById(ar.com.klee.marvinSimulator.R.id.spokenText);

        spokenText.setText("Hablá, yo escucho...");

        marvinImage = (ImageView) v.findViewById(ar.com.klee.marvinSimulator.R.id.marvinImage);

        shortcutList = new Application[CANT_APPLICATION]; //creamos la lista para almacenar los accesos directos

        for (int i = 0; i < CANT_APPLICATION; i++)
            shortcutList[i] = new Application(null, null, null, false);//inicia los objetos

        shortcutButton = new ImageButton[CANT_APPLICATION]; //creamos la lista para almacenar los botones


        // RECUPERAR DATOS
        // Creamos la instancia de "SharedPreferences" en MODE_PRIVATE
        SharedPreferences settings = getActivity().getSharedPreferences("PREFERENCES", 0);
      /* Esta parte es para validar que se creo un acceso directo
      if(!settings.getBoolean("IsIconCreated",false)){
            addShortcut();
            getSharedPreferences("PREFERENCES", 0).edit().putBoolean("IsIconCreated", true);

        }*/


        //Se recupera la información en los arrays
        for(int i=0; i<CANT_APPLICATION;i++){
            shortcutList[i].setPackageName(settings.getString("ButtonPack" + i, ""));
            shortcutList[i].setName(settings.getString("ButtonName" + i, ""));
            shortcutList[i].setConfigured(settings.getBoolean("ButtonConfig" + i, false));
            try {
                switch (shortcutList[i].getName()) {
                    case "Marvin - Cámara":
                        shortcutList[i].setIcon(getResources().getDrawable(getResources().getIdentifier("drawable/ic_camera", null,getActivity().getPackageName())));
                        break;
                    case "Marvin - Comandos de voz":
                        shortcutList[i].setIcon(getResources().getDrawable(getResources().getIdentifier("drawable/ic_voice", null, getActivity().getPackageName())));
                        break;
                    case "Marvin - Configuración":
                        shortcutList[i].setIcon(getResources().getDrawable(getResources().getIdentifier("drawable/ic_configuration", null, getActivity().getPackageName())));
                        break;
                    case "Marvin - Dónde estacioné":
                        shortcutList[i].setIcon(getResources().getDrawable(getResources().getIdentifier("drawable/ic_parking", null, getActivity().getPackageName())));
                        break;
                    case "Marvin - Historial de llamadas":
                        shortcutList[i].setIcon(getResources().getDrawable(getResources().getIdentifier("drawable/ic_call_history", null, getActivity().getPackageName())));
                        break;
                    case "Marvin - Historial de sms":
                        shortcutList[i].setIcon(getResources().getDrawable(getResources().getIdentifier("drawable/ic_sms_history", null, getActivity().getPackageName())));
                        break;
                    case "Marvin - Historial de viajes":
                        shortcutList[i].setIcon(getResources().getDrawable(getResources().getIdentifier("drawable/ic_clock", null, getActivity().getPackageName())));
                        break;
                    case "Marvin - Mapa":
                        shortcutList[i].setIcon(getResources().getDrawable(getResources().getIdentifier("drawable/ic_map", null, getActivity().getPackageName())));
                        break;
                    case "Marvin - Mis sitios":
                        shortcutList[i].setIcon(getResources().getDrawable(getResources().getIdentifier("drawable/ic_places", null, getActivity().getPackageName())));
                        break;
                    case "Marvin - Perfil":
                        shortcutList[i].setIcon(getResources().getDrawable(getResources().getIdentifier("drawable/ic_profile", null, getActivity().getPackageName())));
                        break;
                    case "Marvin - Salir":
                        shortcutList[i].setIcon(getResources().getDrawable(getResources().getIdentifier("drawable/ic_close", null, getActivity().getPackageName())));
                        break;
                    default:
                        shortcutList[i].setIcon(getActivity().getPackageManager().getApplicationIcon(shortcutList[i].getPackageName()));
                        break;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("Shortcut","Botón no seteado");
            }

        }

        //definimos los tipos de letra
        Typeface fBariolBold = Typeface.createFromAsset(getActivity().getAssets(), "Bariol_Bold.otf");
        Typeface fBariolRegular = Typeface.createFromAsset(getActivity().getAssets(), "Bariol_Regular.otf");


        speed = (TextView)v.findViewById(ar.com.klee.marvinSimulator.R.id.speed);
        speed.setTypeface(fBariolBold);
        final TextView speedUnit = (TextView)v.findViewById(ar.com.klee.marvinSimulator.R.id.speedUnit);
        speedUnit.setTypeface(fBariolRegular);


        final TextView digitalClock = (TextView)v.findViewById(ar.com.klee.marvinSimulator.R.id.digitalClock);

        final TextView weekDay = MainMenuActivity.weekDay;
        weekDay.setTypeface(fBariolRegular);

        final TextView dateText = MainMenuActivity.dateText;
        dateText.setTypeface(fBariolRegular);


        final TextView anteMeridiem = (TextView)v.findViewById(ar.com.klee.marvinSimulator.R.id.anteMeridiem);
        date = System.currentTimeMillis();

        final SimpleDateFormat formatTime1 = new SimpleDateFormat("hh:mm");
        final SimpleDateFormat formatTime2 = new SimpleDateFormat("aa");

        digitalClock.setText(formatTime1.format(date));
        digitalClock.setTypeface(fBariolBold);

        anteMeridiem.setText(formatTime2.format(date));
        anteMeridiem.setTypeface(fBariolRegular);

        final SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        weekDay.setText(sdf.format(date));

        final SimpleDateFormat formatTime3 = new SimpleDateFormat("dd 'de' MMMM");
        dateText.setText(formatTime3.format(date));

        final SimpleDateFormat dateComplete = new SimpleDateFormat("hh:mm aa");

        tTime = new Thread() {

            @Override
            public void run() {
                try {

                    Thread.sleep(999);

                    final boolean tabletSize = getResources().getBoolean(ar.com.klee.marvinSimulator.R.bool.isTablet);

                    while (!isInterrupted()) {
                        if(!isInterrupted() && ((MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity()).getActualFragmentPosition() == 1) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // update TextView here!
                                    date = System.currentTimeMillis();
                                    digitalClock.setText(formatTime1.format(date));
                                    anteMeridiem.setText(formatTime2.format(date));
                                    if(tabletSize) {
                                        weekDay.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                                        dateText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                                    }else{
                                        weekDay.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                                        dateText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                                    }
                                    if (dateComplete.format(date).equals("12:00 a.m.")) {
                                        weekDay.setText(sdf.format(date));
                                        dateText.setText(formatTime3.format(date));

                                    }
                                }
                            });
                        }

                        Thread.sleep(999);
                    }
                } catch (InterruptedException e) {
                } catch (NullPointerException e) {
                }
            }
        };

        tTime.start();

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdpater(getActivity().getSupportFragmentManager(),Titles,NumbOfTabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) v.findViewById(ar.com.klee.marvinSimulator.R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) v.findViewById(ar.com.klee.marvinSimulator.R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(ar.com.klee.marvinSimulator.R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        if(instance == null) {

            instance = this;

            pager.setCurrentItem(2);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pager.setCurrentItem(0);
                }
            }, 1000);

        }else{

            LocationSender.getInstance().setEnableAppToOpen(false);

            CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();
            commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_MAIN,commandHandlerManager.getMainActivity());

            ((MainMenuActivity)CommandHandlerManager.getInstance().getMainActivity()).refreshMusicButtons();

        }
        return v;
    }



    public static MainMenuFragment getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getSettings");
        }
        return instance;
    }

    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    public void stopThread(){
        tTime.interrupt();
        instance = null;
    }

    public void setItem(int item){
        pager.setCurrentItem(item);
    }

    public boolean isRadio() {
        return isRadio;
    }

    public void setIsRadio(boolean isRadio) {
        this.isRadio = isRadio;
    }

    public ViewPager getPager() {
        return pager;
    }
}
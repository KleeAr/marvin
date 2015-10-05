package ar.com.klee.marvin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import java.util.ArrayList;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.ConfFavoriteAppActivity;
import ar.com.klee.marvin.activities.ConfHistoryTripActivity;
import ar.com.klee.marvin.activities.ConfHotSpotActivity;
import ar.com.klee.marvin.activities.ConfOrientationScreenActivity;
import ar.com.klee.marvin.activities.ConfSmsEmergencyActivity;
import ar.com.klee.marvin.activities.ConfSpeedAlertActivity;
import ar.com.klee.marvin.activities.ConfTermsActivity;
import ar.com.klee.marvin.activities.ListviewCategoriesAdapter;
import ar.com.klee.marvin.activities.MainMenuActivity;


public class ConfigureFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_configure, container, false);

        // Set title bar
       // ((MainMenuActivity) getActivity()).setActionBarTitle("CONFIGURACIÓN");

        final ArrayList<String> listCategories =  new ArrayList<String>();
        listCategories.add("Alertas de Velocidad");
        listCategories.add("Aplicación Favorita");
        listCategories.add("SMS Emergencia");
        listCategories.add("Historial de viajes");
        listCategories.add("Configuración HotSpot");
        listCategories.add("Orientación Pantalla");
        listCategories.add("Términos y Condiciones");


        ListView lv = (ListView) v.findViewById(R.id.listView);
        lv.setAdapter(new ListviewCategoriesAdapter(getActivity(), listCategories));



        // listening to single list item on click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //Toast.makeText(getActivity(), listCategories.get(position), Toast.LENGTH_SHORT).show();
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(getActivity(), ConfSpeedAlertActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getActivity(), ConfFavoriteAppActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getActivity(), ConfSmsEmergencyActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(getActivity(), ConfHistoryTripActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(getActivity(), ConfHotSpotActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(getActivity(), ConfOrientationScreenActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(getActivity(), ConfTermsActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;


                }
            }

        });

        return v;
    }





    }



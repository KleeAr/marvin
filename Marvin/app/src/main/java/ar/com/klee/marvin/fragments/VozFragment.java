package ar.com.klee.marvin.fragments;

/*
Fragmento para habilitar la lista del menu de ayuda
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.com.klee.marvin.Command;
import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.ExpandableListAdapter;
import ar.com.klee.marvin.activities.LoginActivity;
import ar.com.klee.marvin.activities.MainMenuActivity;

public class VozFragment extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<Command> commandList;
    public ImageView imgFlag;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        commandList = new ArrayList<Command>();
        //Menu Principal
        commandList.add(new Command("Abrir aplicación","Abrir [aplicación] (Ejemplo: Abrir Twitter)","Abre la aplicación indicada"));
        commandList.add(new Command("Activar HotSpot","Activar hotspot","Crea una señal WIFI a partir de los datos del dispositivo"));
        commandList.add(new Command("Activar reproducción aleatoria","Activar reproducción aleatoria","Reproduce las canciones de manera aleatoria"));
        commandList.add(new Command("Agregar evento","Agregar evento [evento] (Ejemplo: Agregar evento Salida al parque)","Agrega un evento en el calendario"));
        commandList.add(new Command("Anterior canción"," Anterior canción","Reproduce la canción anterior a la actual"));
        commandList.add(new Command("Anterior intersección","Anterior intersección","Indica cuál es la calle de la esquina anterior"));
        commandList.add(new Command("Anterior radio","Anterior radio","Reproduce la radio anterior a la actual"));
        commandList.add(new Command("Bajar volumen","Bajar volumen","Reduce el volumen en dos unidades"));
        commandList.add(new Command("Barrio","Barrio","Indica el barrio en el que te encontrás"));
        commandList.add(new Command("Buscar en Youtube","Buscar en Youtube [búsqueda] (Ejemplo: Buscar en Youtube \"Mejores goles\")","Realiza una búsqueda de videos en Youtube"));
        commandList.add(new Command("Cerrar sesión","Cerrar sesión","Cierra la sesión actual"));
        commandList.add(new Command("Desactivar hotspot","Desactivar hotspot","Desactiva una señal WIFI creada por el dispositivo"));
        commandList.add(new Command("Desactivar reproducción aleatoria","Desactivar reproducción aleatoria","Reproduce las canciones de manera secuencial"));
        commandList.add(new Command("Dirección","Dirección","Indica la dirección por la que te encontrás circulando"));
        commandList.add(new Command("Enviar mail a contacto","Enviar mail a [contacto] (Ejemplo: Enviar mail a Juan Pérez)","Envía un mail a una casilla asociada a un contacto"));
        commandList.add(new Command("Enviar sms a contacto","Enviar sms a [contacto] (Ejemplo: Enviar sms a Juan Pérez)","Envía un mensaje de texto al contacto indicado"));
        commandList.add(new Command("Enviar sms a número","Enviar sms al número [número] (Ejemplo: Enviar sms al número 1544443333)","Envía un mensaje de texto al número indicado"));
        commandList.add(new Command("Enviar whatsapp","Enviar whatsapp [mensaje] (Ejemplo: Enviar whatsapp  \"te mando un abrazo\")","Envía un mensaje de whatsapp, debiendo seleccionarse el contacto"));
        commandList.add(new Command("Establecer volumen","Establecer volumen [volumen] (Ejemplo: Establecer volumen 15)","Define un valor determinado para el volumen multimedia"));
        commandList.add(new Command("Llamar a contacto","Llamar a [contacto] (Ejemplo: Llamar a Juan Pérez)","Llama a un contacto determinado"));
        commandList.add(new Command("Llamar a número","Llamar al número [número] (Ejemplo: Llamar al número 1544443333)","Llama a un número determinado"));
        commandList.add(new Command("Pausar música","Pausar música","Detiene la reproducción de la canción que está sonando"));
        commandList.add(new Command("Publicar en facebook","Publicar en facebook [mensaje] (Ejemplo: Publicar en facebook Estoy usando Marvin)","Publica un mensaje en el muro de tu facebook, siempre que hayas asociado tu cuenta"));
        commandList.add(new Command("Reproducir artista","Reproducir artista [artista] (Ejemplo: Reproducir artista Coldplay)","Reproduce una canción del artista indicado"));
        commandList.add(new Command("Reproducir canción","Reproducir canción [canción] (Ejemplo: Reproducir canción Speed of sound)","Reproduce la canción indicada"));
        commandList.add(new Command("Reproducir estación","Reproducir estación [radio] (Ejemplo: Reproducir estación \"La 100\")","Reproduce la estación de radio indicada"));
        commandList.add(new Command("Reproducir frecuencia","Reproducir frecuencia [frecuencia] (Ejemplo: Reproducir frecuencia FM 99.9)","Reproduce la estación de radio correspondiente a la frecuencia indicada"));
        commandList.add(new Command("Reproducir música","Reproducir música","Comienza a reproducir una canción"));
        commandList.add(new Command("Reproducir radio","Reproducir radio","Comienza a reproducir una radio"));
        commandList.add(new Command("Siguiente canción","Siguiente canción","Reproduce la canción posterior a la actual"));
        commandList.add(new Command("Siguiente intersección","Siguiente intersección","Indica la dirección se la próxima esquina"));
        commandList.add(new Command("Siguiente radio","Siguiente radio","Reproduce la siguiente estación de radio"));
        commandList.add(new Command("SMS de emergencia","Sms de emergencia","Envía un sms preconfigurado (Ver menú de configuración)"));
        commandList.add(new Command("Subir volumen"," Subir volumen","Incrementa el volumen del dispositivo en dos unidades"));
        commandList.add(new Command("Twittear","Twittear [mensaje] (Ejemplo:  \"Twittear Estoy usando Marvin\")","Publica un mensaje en tu cuenta de Twitter asociada"));
        //Cámara
        commandList.add(new Command("Cancelar foto","Cancelar foto","Descarta una fotografía tomada"));
        commandList.add(new Command("Cerrar cámara","Cerrar cámara","Regresa al menú principal"));
        commandList.add(new Command("Compartir foto","Compartir foto","Comparte la foto en la red social indicada"));
        commandList.add(new Command("Guardar foto","Guardar foto","Guarda la foto en la carpeta MARVIN del dispositivo"));
        commandList.add(new Command("Guardar y compartir foto","Guardar y compartir foto","Guarda la foto en la carpeta MARVIN del dispositivo y la comparte en la red social indicada"));
        commandList.add(new Command("Sacar foto","Sacar foto","Saca una fotografía"));
        //Dónde estacioné
        commandList.add(new Command("Cerrar dónde estacioné","Cerrar dónde estacioné","Regresa al menú principal"));

        //Historial de llamadas
        commandList.add(new Command("Cerrar historial de llamadas","Cerrar historial de llamadas","Regresa al menú principal"));
        commandList.add(new Command("Consultar registro número","Consultar registro número [número] (Ejemplo: Consultar registro número 3)","Notifica la llamada registrada y da la posibilidad de contestarla"));
        commandList.add(new Command("Consultar último registro de contacto","Consultar último registro del contacto [contacto] (Ejemplo: Consultar último registro del contacto Juan Pérez)","Indica el último registro del contacto indicado y da la posibilidad de responder la llamada"));
        commandList.add(new Command("Consultar último registro de numero","Consultar último registro del número [número] (Ejemplo: Consultar último registro del número 1544445555)","Indica el último registro del número indicado y da la posibilidad de responder la llamada"));
        commandList.add(new Command("Consultar último registro","Consultar último registro","Indica la última llamada registrada y da la posibilidad de responderla"));

        //Historial de sms
        commandList.add(new Command("Cerrar historial de sms","Cerrar historial de sms","Regresa al menú principal"));
        commandList.add(new Command("Leer sms número","Leer sms número [número] (Ejemplo: Leer sms número 3)","Lee el mensaje indicado de la lista y da la posibilidad de responderlo"));
        commandList.add(new Command("Leer último sms de contacto","Leer último sms del contacto [contacto] (Ejemplo: Leer último sms del contacto Juan Pérez)","Lee el último mensaje del contacto indicado y da la posibilidad de responderlo"));
        commandList.add(new Command("Leer último sms de numero","Leer último sms del número [número] (Ejemplo: Leer último sms del número 1544445555)","Lee el último mensaje del número indicado y da la posibilidad de responderlo"));
        commandList.add(new Command("Leer último sms","Leer último sms","Lee el último mensaje recibido"));

        //Historial de viajes
        commandList.add(new Command("Abrir último viaje","Abrir último viaje","Abre el último viaje realizado partiendo desde la calle indicada"));
        commandList.add(new Command("Abrir viaje desde","Abrir viaje desde [dirección] (Ejemplo: Abrir viaje desde Tacuarí)","Abre el último viaje realizado partiendo desde la calle indicada"));
        commandList.add(new Command("Abrir viaje hasta","Abrir viaje hasta [dirección] (Ejemplo: Abrir viaje hasta Tacuarí)","Abre el último viaje realizado yendo hasta la calle indicada"));
        commandList.add(new Command("Abrir viaje número","Abrir viaje número [número] (Ejemplo: Abrir viaje número 3)","Abre el número de viaje indicado en la lista"));
        commandList.add(new Command("Cerrar historial de viajes","Cerrar historial de viajes","Regresa al menú principal de la aplicación"));

        //Mapa
        commandList.add(new Command("Aumentar zoom","Aumentar zoom","Aumenta el zoom realizado sobre el mapa"));
        commandList.add(new Command("Buscar en mapa","Buscar en mapa [dirección] (Ejemplo: Buscar en mapa Arieta 1500, San Justo)","Busca una dirección en el mapa"));
        commandList.add(new Command("Buscar en sitio","Buscar en sitio [sitio] (Ejemplo: Buscar sitio Casa)","Busca en el mapa un sitio previamente guardado"));
        commandList.add(new Command("Cerrar mapa","Cerrar mapa","Regresa al menú principal"));
        commandList.add(new Command("Establecer zoom","Establecer zoom [número] (Ejemplo: Establecer zoom 15)","Establece un zoom definido entre los valores 1 y 20"));
        commandList.add(new Command("Ir a dirección","Ir a [dirección] (Ejemplo: Ir a Arieta 1500, San Justo)","Activa la navegación hacia la dirección indicada"));
        commandList.add(new Command("Ir a sitio","Ir a sitio [sitio] (Ejemplo: Ir a Casa)","Activa la navegación hacia un sitio previamente guardado"));
        commandList.add(new Command("Reducir zoom","Reducir zoom","Reduce el zoom realizado sobre el mapa"));
        commandList.add(new Command("Ubicación actual","Ubicación actual","Regresa el mapa a la posición donde te encontrás en un momento dado"));

        //Mis sitios
        commandList.add(new Command("Abrir sitio","Abrir sitio [sitio] (Ejemplo: Abrir sitio Casa)","Accede a un menú donde se muestra el sitio en el mapa"));
        commandList.add(new Command("Borrar sitio","Borrar sitio [sitio] (Ejemplo: Borrar sitio Casa)","Eliminar un sitio previamente guardado"));
        commandList.add(new Command("Cerrar mis sitios","Cerrar mis sitios","Regresa al menú principal"));
        commandList.add(new Command("Guardar sitio","Guardar sitio [sitio] (Ejemplo: Guardar sitio Casa)","Guarda un sitio"));

        //Sitio:
        commandList.add(new Command("Cerrar sitio","Cerrar sitio","Regresa al menú principal"));
        commandList.add(new Command("Compartir sitio","Compartir sitio","Comparte el sitio en las redes sociales"));

        //Viaje
        commandList.add(new Command("Cerrar viaje","Cerrar viaje","Regresa al menú principal"));
        commandList.add(new Command("Compartir viaje ","Compartir viaje ","Comparte el viaje en las redes sociales"));



        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_voz, container, false);



        // Set title bar
       // ((MainMenuActivity) getActivity()).setActionBarTitle("COMANDOS DE VOZ");


        // get the listview
        expListView = (ExpandableListView) v.findViewById(R.id.lvExp);
       // expListView.setIndicatorBounds(15,15);


        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,int groupPosition, long id) {
                // Toast.makeText(getActivity(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
              //  Toast.makeText(getActivity(),listDataHeader.get(groupPosition) + " Expanded",Toast.LENGTH_SHORT).show();

            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
               // Toast.makeText(getActivity(),listDataHeader.get(groupPosition) + " Collapsed",Toast.LENGTH_SHORT).show();


            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
                int i=0;
                while(!commandList.get(i).description.equals(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition))){
                    i++;
                }

                dialogCommand(listDataHeader.get(groupPosition),commandList.get(i));
                return false;
            }
        });

        return v;
    }


    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Menú Principal");
        listDataHeader.add("Cámara");
        listDataHeader.add("Dónde estacioné?");
        listDataHeader.add("Historial de Llamadas");
        listDataHeader.add("Historial de SMS");
        listDataHeader.add("Historial de Viajes");
        listDataHeader.add("Mapa");
        listDataHeader.add("Mis Sitios");
        listDataHeader.add("Sitio");
        listDataHeader.add("Viaje");

        // Adding child data
        List<String> menuPrincipal = new ArrayList<String>();
        menuPrincipal.add(commandList.get(0).getDescription());
        menuPrincipal.add(commandList.get(1).getDescription());
        menuPrincipal.add(commandList.get(2).getDescription());
        menuPrincipal.add(commandList.get(3).getDescription());
        menuPrincipal.add(commandList.get(4).getDescription());
        menuPrincipal.add(commandList.get(5).getDescription());
        menuPrincipal.add(commandList.get(6).getDescription());
        menuPrincipal.add(commandList.get(7).getDescription());
        menuPrincipal.add(commandList.get(8).getDescription());
        menuPrincipal.add(commandList.get(9).getDescription());
        menuPrincipal.add(commandList.get(10).getDescription());
        menuPrincipal.add(commandList.get(11).getDescription());
        menuPrincipal.add(commandList.get(12).getDescription());
        menuPrincipal.add(commandList.get(13).getDescription());
        menuPrincipal.add(commandList.get(14).getDescription());
        menuPrincipal.add(commandList.get(15).getDescription());
        menuPrincipal.add(commandList.get(16).getDescription());
        menuPrincipal.add(commandList.get(17).getDescription());
        menuPrincipal.add(commandList.get(18).getDescription());
        menuPrincipal.add(commandList.get(19).getDescription());
        menuPrincipal.add(commandList.get(20).getDescription());
        menuPrincipal.add(commandList.get(21).getDescription());
        menuPrincipal.add(commandList.get(22).getDescription());
        menuPrincipal.add(commandList.get(23).getDescription());
        menuPrincipal.add(commandList.get(24).getDescription());
        menuPrincipal.add(commandList.get(25).getDescription());
        menuPrincipal.add(commandList.get(26).getDescription());
        menuPrincipal.add(commandList.get(27).getDescription());
        menuPrincipal.add(commandList.get(28).getDescription());
        menuPrincipal.add(commandList.get(29).getDescription());
        menuPrincipal.add(commandList.get(30).getDescription());
        menuPrincipal.add(commandList.get(31).getDescription());
        menuPrincipal.add(commandList.get(32).getDescription());
        menuPrincipal.add(commandList.get(33).getDescription());
        menuPrincipal.add(commandList.get(34).getDescription());

        List<String> camara = new ArrayList<String>();
        camara.add(commandList.get(35).getDescription());
        camara.add(commandList.get(36).getDescription());
        camara.add(commandList.get(37).getDescription());
        camara.add(commandList.get(38).getDescription());
        camara.add(commandList.get(39).getDescription());
        camara.add(commandList.get(40).getDescription());

        List<String> dondeEstacione = new ArrayList<String>();
        dondeEstacione.add(commandList.get(41).getDescription());

        List<String> historialLlamadas = new ArrayList<String>();
        historialLlamadas.add(commandList.get(42).getDescription());
        historialLlamadas.add(commandList.get(43).getDescription());
        historialLlamadas.add(commandList.get(44).getDescription());
        historialLlamadas.add(commandList.get(45).getDescription());
        historialLlamadas.add(commandList.get(46).getDescription());

        List<String> historialSms = new ArrayList<String>();
        historialSms.add(commandList.get(47).getDescription());
        historialSms.add(commandList.get(48).getDescription());
        historialSms.add(commandList.get(49).getDescription());
        historialSms.add(commandList.get(50).getDescription());
        historialSms.add(commandList.get(51).getDescription());

        List<String> historialViajes = new ArrayList<String>();
        historialViajes.add(commandList.get(52).getDescription());
        historialViajes.add(commandList.get(53).getDescription());
        historialViajes.add(commandList.get(54).getDescription());
        historialViajes.add(commandList.get(55).getDescription());
        historialViajes.add(commandList.get(56).getDescription());

        List<String> mapa = new ArrayList<String>();
        mapa.add(commandList.get(57).getDescription());
        mapa.add(commandList.get(58).getDescription());
        mapa.add(commandList.get(59).getDescription());
        mapa.add(commandList.get(60).getDescription());
        mapa.add(commandList.get(61).getDescription());
        mapa.add(commandList.get(62).getDescription());
        mapa.add(commandList.get(63).getDescription());
        mapa.add(commandList.get(64).getDescription());
        mapa.add(commandList.get(65).getDescription());

        List<String> misSitios = new ArrayList<String>();
        misSitios.add(commandList.get(66).getDescription());
        misSitios.add(commandList.get(67).getDescription());
        misSitios.add(commandList.get(68).getDescription());
        misSitios.add(commandList.get(69).getDescription());

        List<String> sitio = new ArrayList<String>();
        sitio.add(commandList.get(70).getDescription());
        sitio.add(commandList.get(71).getDescription());

        List<String> viaje = new ArrayList<String>();
        viaje.add(commandList.get(72).getDescription());
        viaje.add(commandList.get(73).getDescription());


        listDataChild.put(listDataHeader.get(0), menuPrincipal); // Header, Child data
        listDataChild.put(listDataHeader.get(1), camara);
        listDataChild.put(listDataHeader.get(2), dondeEstacione);
        listDataChild.put(listDataHeader.get(3), historialLlamadas);
        listDataChild.put(listDataHeader.get(4), historialSms);
        listDataChild.put(listDataHeader.get(5), historialViajes);
        listDataChild.put(listDataHeader.get(6), mapa);
        listDataChild.put(listDataHeader.get(7), misSitios);
        listDataChild.put(listDataHeader.get(8), sitio);
        listDataChild.put(listDataHeader.get(9), viaje);
    }

    public void dialogCommand(String group, Command command){

        AlertDialog.Builder builder =new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true);
        builder.setTitle(group+" - "+command.description);
       // builder.setIcon(R.drawable.marvin);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView commandName = new TextView(getActivity());
        commandName.setText("Comando: " + command.command);
        commandName.setPadding(10, 10, 0, 10); //(left, top, right, bottom);
        layout.addView(commandName);

        final TextView commandFunction = new TextView(getActivity());
        commandFunction.setText("Función: " + command.function);
        commandFunction.setPadding(10, 0, 0, 20);
        layout.addView(commandFunction);

        builder.setView(layout);

       /* builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });*/
          builder.show();

    }
}
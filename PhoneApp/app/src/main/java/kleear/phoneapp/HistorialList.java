package kleear.phoneapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/*
* Clase donde se maneja el historial de llamdas a traves de una lista
 */


public class HistorialList extends Activity implements AdapterView.OnItemClickListener {
    Button button;
    private ListView mylistView;
    private List<Call> mylist = new ArrayList<Call>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_list);

        mylistView = (ListView) findViewById(R.id.listviewshow);
        mylistView.setOnItemClickListener(this);

        //Acceso a la lista historica de llamadas
        Cursor cursorH = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = cursorH.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursorH.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursorH.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursorH.getColumnIndex(CallLog.Calls.DURATION);

        while (cursorH.moveToNext()) {
            String phNumber = cursorH.getString(number);
            String callType = cursorH.getString(type);
            String callDate = cursorH.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = cursorH.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "Saliente";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "Entrante";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "Perdida";
                    break;
            }


            Call objCall = new Call();

            objCall.setNumberPhone(phNumber);
            objCall.setDuration(callDuration);
            objCall.setDate(callDayTime);
            objCall.setType(dir);
            mylist.add(objCall); //se agrega el objeto a la lista


        }
        cursorH.close();

        HistorialAdapter objAdapter = new HistorialAdapter(HistorialList.this, R.layout.activity_historial_item, mylist);
        mylistView.setAdapter(objAdapter);

        if (null != mylist && mylist.size() != 0) {
            Collections.sort(mylist, new Comparator<Call>() {
                @Override
                public int compare(Call lhs, Call rhs) {
                    return rhs.getDate().compareTo(lhs.getDate());
                }
            });

            if(mylist.size()==0)
                Toast.makeText(this,"Registro de llamadas vacio!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> listview, View v, int position,
                            long id) {
        Call objetoCall = (Call) listview.getItemAtPosition(position);
        showCallDialog(objetoCall.getNumberPhone());
    }


/*
* Muesta el dialogo de consulta sobre si se quiere realizar alguna accion sobre un item de la lista de llamadas
* Recibe como parametro el numero del item selecccionado
 */
    public  void showCallDialog(final String number){
        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_inbox);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Typeface fontBold = Typeface.createFromAsset(getAssets(),"Bariol_Bold.otf");


        TextView textFor = (TextView) customDialog.findViewById(R.id.toCall);
        textFor.setTypeface(fontBold);
        textFor.setText("¿Quieres realizar alguna accion con el numero: " + number + " ?");


        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.responder).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(HistorialList.this,"Envia SMS", Toast.LENGTH_SHORT).show();
                //Llamar a la funcion que enviar mensajes con el numero fijo
                customDialog.dismiss();

            }
        });
        customDialog.findViewById(R.id.llamar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                startActivity(intent);

            }
        });

        customDialog.show();


    }


}




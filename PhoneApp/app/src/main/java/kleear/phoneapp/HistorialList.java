package kleear.phoneapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


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
            mylist.add(objCall);


        }
        cursorH.close();

        HistorialAdapter objAdapter = new HistorialAdapter(HistorialList.this, R.layout.activity_historial_item, mylist);
        mylistView.setAdapter(objAdapter);

        if (null != mylist && mylist.size() != 0) {
            Collections.sort(mylist, new Comparator<Call>() {
                @Override
                public int compare(Call lhs, Call rhs) {
                    return lhs.getDate().compareTo(rhs.getDate());
                }
            });

            if(mylist.size()==0)
                showToast("No se han encontrado llamadas!!!");
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> listview, View v, int position,
                            long id) {
        Call objetoCall = (Call) listview.getItemAtPosition(position);
        showCallDialog(objetoCall.getNumberPhone());
    }

    private void showCallDialog(final String number) {

        AlertDialog alert = new AlertDialog.Builder(HistorialList.this).create();
        alert.setTitle("Confirmar llamada");
        alert.setMessage("Estas seguro de llamar a " + number + " ?");
        alert.setButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setButton2("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                startActivity(intent);
            }
        });
        alert.show();
    }

}




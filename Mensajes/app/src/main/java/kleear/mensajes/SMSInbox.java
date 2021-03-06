package kleear.mensajes;


import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
Clase SMSInbox, muestra los mensajes de entrada
 */

public class SMSInbox extends Activity  {

    final List<Mensaje> smsMessagesList = new ArrayList<>();
    ListView smsListView;
    Mensaje mensaje;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_inbox);

        Typeface fontBold = Typeface.createFromAsset(getAssets(),"Bariol_Bold.otf");
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setTypeface(fontBold);

        smsListView = (ListView) findViewById(R.id.SMSList);
        refreshSmsInbox();
    }

    public void refreshSmsInbox() {

        //Obtención de los datos de los mensajes de entrada
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int inboxDate = smsInboxCursor.getColumnIndex("date");


        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        do {

            String contactName = ManagerMensaje.getContactName(getApplicationContext(), smsInboxCursor.getString(indexAddress));
            Mensaje iMensaje = new Mensaje(smsInboxCursor.getString(indexAddress), smsInboxCursor.getString(indexBody), contactName, (long) inboxDate);

            smsMessagesList.add(iMensaje);
        } while (smsInboxCursor.moveToNext());
        smsInboxCursor.close();

        final InboxAdaptor adapter = new InboxAdaptor(this, smsMessagesList);
        smsListView.setAdapter(adapter);


        smsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            mensaje = (Mensaje) parent.getAdapter().getItem(position); //recupera el objeto en la posición deseada

            showCallDialog(mensaje.getPhoneNumber());

            }
        });

    }

    private void showCallDialog(final String number) {

        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_inbox);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView notifiacion = (TextView) customDialog.findViewById(R.id.textView);
        notifiacion.setText("Desea realizar alguna acción con el contacto " + mensaje.getContactName() +"?");

        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.responder).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                displayRespuesta();
                customDialog.dismiss();

            }
        });
        customDialog.findViewById(R.id.llamar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
                //lanza un intent con el numero del contacto
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                startActivity(intent);
                customDialog.dismiss();
            }
        });

        customDialog.show();
    }

    //Es el mismo metodo que esta en ManagerMensaje, pero no lo puedo usar porque me tira error de contexto
    public  void displayRespuesta(){
        // con este tema personalizado evitamos los bordes por defecto
        final Dialog customDialog = new Dialog(this);
        //deshabilitamos el título por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setCancelable(false);
        //establecemos el contenido de nuestro dialog
        customDialog.setContentView(R.layout.dialog_popup);
        //setea el fondo transparente
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Typeface fontBold = Typeface.createFromAsset(getAssets(),"Bariol_Bold.otf");
        Typeface fontRegular = Typeface.createFromAsset(getAssets(),"Bariol_Bold.otf");

        TextView textFor = (TextView) customDialog.findViewById(R.id.textFor);
        textFor.setTypeface(fontRegular);

        TextView contact = (TextView) customDialog.findViewById(R.id.contact);
        contact.setText(mensaje.getContactName());
        contact.setTypeface(fontBold);
        TextView phone = (TextView) customDialog.findViewById(R.id.phone);
        phone.setText(mensaje.getPhoneNumber());
        phone.setTypeface(fontBold);

        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.enviar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Typeface fontRegular = Typeface.createFromAsset(getAssets(),"Bariol_Bold.otf");
                EditText contenido = (EditText) customDialog.findViewById(R.id.contenido);
                contenido.setTypeface(fontRegular);
                String smsBody=contenido.getText().toString();
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(mensaje.getPhoneNumber(), null, smsBody, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Enviado!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Fallo envió de SMS, intenta luego!",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Toast.makeText(SMSInbox.this,smsBody, Toast.LENGTH_SHORT).show();
                customDialog.dismiss();

            }
        });
        customDialog.findViewById(R.id.leer).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
                //metodo a desarrollar de lectura de mensaje por voz
                Toast.makeText(SMSInbox.this, "LEER", Toast.LENGTH_SHORT).show();

            }
        });

        customDialog.show();


    }



}


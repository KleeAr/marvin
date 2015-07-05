package kleear.mensajes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/*Clase principal para el manejo de mensajes
Permite el envio de mensajes
Contiene el boton para redireccionar al activity que tiene el historial de mensajes de entrada
*/


public class ManagerMensaje extends Activity {

    private Button buttonSend;
    private Button buttonInbox;
    private EditText phoneNo;
    private EditText messageBody;
    public static final String SMS_BUNDLE = "pdus";

    private static final String LOG_TAG = "SMSReceiver";
    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static Mensaje incomingMensaje; //variable para guardar el mensaje recibido

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje);

        IntentFilter filter = new IntentFilter(ACTION);
        this.registerReceiver(mReceivedSMSReceiver, filter);

        buttonSend = (Button) findViewById(R.id.send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySendSMS();
            }
        });

        //capturura el evento del boton para abril el historial de mensajes recibidos
        buttonInbox = (Button) findViewById(R.id.btnInbox);
        buttonInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SMSInbox.class );
                startActivity(i);

                Typeface font = Typeface.createFromAsset(getAssets(),"Bariol_Bold.otf");
                buttonSend.setTypeface(font);

            }
        });
    }

    private void displaySendSMS() {
        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_popup_outputsms);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        phoneNo = (EditText) findViewById(R.id.mobileNumber);
        messageBody = (EditText) findViewById(R.id.smsBody);

        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.enviar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String smsNumber = phoneNo.getText().toString();
                final String smsBody = messageBody.getText().toString();
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(smsNumber, null, smsBody, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Enviado!",Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Fallo envió de SMS, intenta luego!",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Toast.makeText(ManagerMensaje.this,smsBody, Toast.LENGTH_SHORT).show();
                customDialog.dismiss();

            }
        });
        customDialog.findViewById(R.id.leer).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
                //metodo a desarrollar de lectura de mensaje por voz
                Toast.makeText(ManagerMensaje.this, "LEER", Toast.LENGTH_SHORT).show();

            }
        });

        customDialog.show();

    }

    private void displayIncomingSMS()
    {
        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_incoming);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView contact = (TextView) customDialog.findViewById(R.id.contact);
        contact.setText(incomingMensaje.getContactName());
        TextView phone = (TextView) customDialog.findViewById(R.id.phone);
        phone.setText(incomingMensaje.getPhoneNumber());

        Date date= new Date(incomingMensaje.getDate());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        TextView dateTime = (TextView) customDialog.findViewById(R.id.date);
        dateTime.setText(format.format(date));

        TextView bodySMS = (TextView) customDialog.findViewById(R.id.contenido);
        bodySMS.setText(incomingMensaje.getBodyMessage());


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
        customDialog.findViewById(R.id.leer).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
                //metodo a desarrollar de lectura de mensaje por voz
                Toast.makeText(ManagerMensaje.this, "LEER", Toast.LENGTH_SHORT).show();

            }
        });

        customDialog.show();
    }

    //copiar archivos que estan en la carpeta drawable y colors de la carpeta values
    public  void displayRespuesta(){
        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_popup);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView contact = (TextView) customDialog.findViewById(R.id.contact);
        contact.setText(incomingMensaje.getContactName());
        TextView phone = (TextView) customDialog.findViewById(R.id.phone);
        phone.setText(incomingMensaje.getPhoneNumber());

        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.enviar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText contenido = (EditText) customDialog.findViewById(R.id.contenido);
                String smsBody=contenido.getText().toString();
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(incomingMensaje.getPhoneNumber(), null, smsBody, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Enviado!",Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Fallo envió de SMS, intenta luego!",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Toast.makeText(ManagerMensaje.this,smsBody, Toast.LENGTH_SHORT).show();
                customDialog.dismiss();

            }
        });
        customDialog.findViewById(R.id.leer).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
                //metodo a desarrollar de lectura de mensaje por voz
                Toast.makeText(ManagerMensaje.this, "LEER", Toast.LENGTH_SHORT).show();

            }
        });

        customDialog.show();


}

    private final BroadcastReceiver mReceivedSMSReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            Bundle intentExtras = intent.getExtras();
            if (intentExtras != null) {
                Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

                for (int i = 0; i < sms.length; ++i) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                    String nameContact=getContactName(context,smsMessage.getOriginatingAddress());
                    incomingMensaje = new Mensaje(smsMessage.getOriginatingAddress(),smsMessage.getMessageBody(),nameContact, smsMessage.getTimestampMillis());

                }

                if (ACTION.equals(action)) {
                    //your SMS processing code
                    displayIncomingSMS();
                }
            }

        }



    };

    /*
Funcion que permite buscar el nombre de un contacto
Retorna un string con el nombre
 */
    public static String getContactName(Context context, final String phoneNumber){
        Uri uri;
        String[] projection;

        if (Build.VERSION.SDK_INT >= 5)
        {
            uri = Uri.parse("content://com.android.contacts/phone_lookup");
            projection = new String[] { "display_name" };
        }
        else
        {
            uri = Uri.parse("content://contacts/phones/filter");
            projection = new String[] { "name" };
        }

        uri = Uri.withAppendedPath(uri, Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        String contactName = phoneNumber;

        if (cursor.moveToFirst())
        {
            contactName = cursor.getString(0);

        }

        cursor.close();

        return contactName;
    }



}
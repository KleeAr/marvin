package ar.com.klee.marvin.sms;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;

public class SMSDriver {

    public static final String SMS_BUNDLE = "pdus";
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static Mensaje incomingMensaje; //variable para guardar el mensaje recibido

    private static EditText phoneNo;
    private static EditText messageBody;

    private Context context;
    private CommandHandlerManager commandHandlerManager;

    public SMSDriver(Context context){

        IntentFilter filter = new IntentFilter(ACTION);
        context.registerReceiver(mReceivedSMSReceiver, filter);

        this.context = context;

        commandHandlerManager = CommandHandlerManager.getInstance();

    }

    public void displaySendSMS() {
        final Dialog customDialog = new Dialog(context);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_popup_outputsms);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Typeface fontBold = Typeface.createFromAsset(context.getAssets(),"Bariol_Bold.otf");
        Typeface fontRegular = Typeface.createFromAsset(context.getAssets(),"Bariol_Bold.otf");

        TextView contact = (TextView) customDialog.findViewById(R.id.contact);
        contact.setTypeface(fontBold);

        TextView titleMensaje = (TextView) customDialog.findViewById(R.id.titleMensaje);
        titleMensaje.setTypeface(fontBold);



        phoneNo = (EditText) customDialog.findViewById(R.id.mobileNumber);
        phoneNo.setTypeface(fontRegular);
        messageBody = (EditText) customDialog.findViewById(R.id.smsBody);
        messageBody.setTypeface(fontRegular);



        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

        customDialog.findViewById(R.id.enviar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String smsNumber = phoneNo.getText().toString();
                String smsBody = messageBody.getText().toString();
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(smsNumber, null, smsBody, null, null);
                    Toast.makeText(context, "SMS Enviado!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(context, "Fallo envió de SMS, intenta luego!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                customDialog.dismiss();


            }
        });
        customDialog.findViewById(R.id.leer).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
                //metodo a desarrollar de lectura de mensaje por voz
                Toast.makeText(context, "LEER", Toast.LENGTH_SHORT).show();

            }
        });

        customDialog.show();

    }


    public void displayIncomingSMS(){
        final Dialog customDialog = new Dialog(context);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_incoming);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Typeface fontBold = Typeface.createFromAsset(context.getAssets(),"Bariol_Bold.otf");
        Typeface fontRegular = Typeface.createFromAsset(context.getAssets(),"Bariol_Bold.otf");

        TextView title =(TextView) customDialog.findViewById(R.id.title);
        title.setTypeface(fontRegular);

        TextView contact = (TextView) customDialog.findViewById(R.id.contact);
        contact.setText(incomingMensaje.getContactName());
        contact.setTypeface(fontBold);


        TextView phone = (TextView) customDialog.findViewById(R.id.phone);
        phone.setText(incomingMensaje.getPhoneNumber());
        phone.setTypeface(fontRegular);


        Date date= new Date(incomingMensaje.getDate());
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        TextView dateTime = (TextView) customDialog.findViewById(R.id.date);
        dateTime.setText(format.format(date));
        dateTime.setTypeface(fontRegular);


        TextView bodySMS = (TextView) customDialog.findViewById(R.id.contenido);
        bodySMS.setText(incomingMensaje.getBodyMessage());
        bodySMS.setTypeface(fontRegular);


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
                Toast.makeText(context, "LEER", Toast.LENGTH_SHORT).show();

            }
        });

        customDialog.show();
    }

    //copiar archivos que estan en la carpeta drawable y colors de la carpeta values
    public  void displayRespuesta(){
        final Dialog customDialog = new Dialog(context);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_popup);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Typeface fontBold = Typeface.createFromAsset(context.getAssets(),"Bariol_Bold.otf");
        Typeface fontRegular = Typeface.createFromAsset(context.getAssets(),"Bariol_Bold.otf");

        TextView textFor = (TextView) customDialog.findViewById(R.id.textFor);
        textFor.setTypeface(fontRegular);

        TextView contact = (TextView) customDialog.findViewById(R.id.contact);
        contact.setText(incomingMensaje.getContactName());
        contact.setTypeface(fontBold);


        TextView phone = (TextView) customDialog.findViewById(R.id.phone);
        phone.setText(incomingMensaje.getPhoneNumber());
        phone.setTypeface(fontRegular);


        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.enviar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Typeface fontRegular = Typeface.createFromAsset(context.getAssets(),"Bariol_Bold.otf");
                EditText contenido = (EditText) customDialog.findViewById(R.id.contenido);
                contenido.setTypeface(fontRegular);
                String smsBody=contenido.getText().toString();
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(incomingMensaje.getPhoneNumber(), null, smsBody, null, null);
                    Toast.makeText(context.getApplicationContext(), "SMS Enviado!",Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(context.getApplicationContext(),"Fallo envió de SMS, intenta luego!",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Toast.makeText(context,smsBody, Toast.LENGTH_SHORT).show();
                customDialog.dismiss();

            }
        });
        customDialog.findViewById(R.id.leer).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
                //metodo a desarrollar de lectura de mensaje por voz
                Toast.makeText(context, "LEER", Toast.LENGTH_SHORT).show();

            }
        });

        customDialog.show();

    }

    public final BroadcastReceiver mReceivedSMSReceiver = new BroadcastReceiver() {

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

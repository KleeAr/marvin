package ar.com.klee.marvin.sms;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
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
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.configuration.UserConfig;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;
import ar.com.klee.marvin.voiceControl.handlers.ResponderSMSHandler;

public class SMSDriver {

    public static final String SMS_BUNDLE = "pdus";
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private ArrayList<Mensaje> inbox = new ArrayList<>();
    private Mensaje incomingMensaje;

    private static EditText phoneNo;
    private static EditText messageBody;
    private static EditText answer;
    private Dialog actualDialog;

    private Context context;
    private CommandHandlerManager commandHandlerManager;
    private static SMSDriver instance;

    public SMSDriver(Context context){

        IntentFilter filter = new IntentFilter(ACTION);
        context.registerReceiver(mReceivedSMSReceiver, filter);

        this.context = context;

        instance = this;

    }

    public void initializeCommandHandlerManager(){
        commandHandlerManager = CommandHandlerManager.getInstance();
    }

    public static SMSDriver getInstance(){
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getInstance");
        }
        return instance;
    }

    public static SMSDriver initializeInstance(Context context) {
        if(instance != null) {
            throw new IllegalStateException("Instance already initialized");
        }
        SMSDriver.instance = new SMSDriver(context);
        return instance;
    }

    public void displaySendSMS() {
        final Dialog customDialog = new Dialog(commandHandlerManager.getMainActivity());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_sms_send);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        actualDialog = customDialog;

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
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                ((MainMenuActivity)commandHandlerManager.getMainActivity()).setButtonsEnabled();
                customDialog.dismiss();
            }
        });

        customDialog.findViewById(R.id.enviar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String smsNumber = phoneNo.getText().toString();
                String smsBody = messageBody.getText().toString();
                if(smsNumber.equals(""))
                    Toast.makeText(context, "Ingresá un número", Toast.LENGTH_LONG).show();
                else if(smsBody.equals(""))
                    Toast.makeText(context, "Ingresá un mensaje", Toast.LENGTH_LONG).show();
                else {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(smsNumber, null, smsBody, null, null);
                        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                            saveInOutbox(smsNumber, smsBody);
                        Toast.makeText(context, "SMS enviado", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "El envío falló. Revisá los datos ingresados y reintentá.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    STTService.getInstance().setIsListening(false);
                    commandHandlerManager.setNullCommand();
                    ((MainMenuActivity) commandHandlerManager.getMainActivity()).setButtonsEnabled();
                    customDialog.dismiss();
                }
            }
        });
        customDialog.findViewById(R.id.leer).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                STTService.getInstance().stopListening();

                String smsBody = messageBody.getText().toString();

                if(smsBody.equals(""))
                    Toast.makeText(context, "Ingresá un mensaje", Toast.LENGTH_LONG).show();
                else {
                    customDialog.findViewById(R.id.cancelar).setEnabled(false);
                    customDialog.findViewById(R.id.leer).setEnabled(false);
                    customDialog.findViewById(R.id.enviar).setEnabled(false);

                    commandHandlerManager.getTextToSpeech().speakText("SMS - " + smsBody);

                }
            }
        });

        customDialog.show();

    }

    public void enableButtons(){
        actualDialog.findViewById(R.id.cancelar).setEnabled(true);
        actualDialog.findViewById(R.id.leer).setEnabled(true);
        actualDialog.findViewById(R.id.enviar).setEnabled(true);
    }

    public void setNumber(String number){

        phoneNo.setText(number);

    }

    public void setMessageBody(String message){

        messageBody.setText(message);

    }

    public void cancelMessage(){

        actualDialog.dismiss();

    }

    public String sendMessage(){

        String smsNumber = phoneNo.getText().toString();
        String smsBody = messageBody.getText().toString();
        if(smsNumber.equals("")){
            actualDialog.dismiss();
            return "El mensaje no pudo ser enviado. No debés blanquear el número ingresado.";
        }
        if(smsBody.equals("")){
            actualDialog.dismiss();
            return "El mensaje no pudo ser enviado. No debés blanquear el mensaje ingresado.";
        }
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(smsNumber, null, smsBody, null, null);
            if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                saveInOutbox(smsNumber, smsBody);
            actualDialog.dismiss();
            return "El mensaje fue enviado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            actualDialog.dismiss();
            return "El mensaje no pudo ser enviado. Reintentá luego";
        }
    }

    public String sendEmergencyMessage(String location){

        String smsNumber = UserConfig.getSettings().getEmergencyNumber();
        String smsBody = UserConfig.getSettings().getEmergencySMS();
        if(smsBody.equals("") || smsNumber.equals("")){
            smsBody += location;
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(smsNumber, null, smsBody, null, null);
                if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                    saveInOutbox(smsNumber, smsBody);
                return "Mensaje de emergencia enviado";
            } catch (Exception e) {
                e.printStackTrace();
                return "El mensaje no pudo ser enviado. Reintentá luego";
            }
        }
        return "El mensaje de emergencia no fue configurado";
    }


    public void displayIncomingSMS(){
        final Dialog customDialog = new Dialog(commandHandlerManager.getMainActivity());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_sms_incoming);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        actualDialog = customDialog;

        incomingMensaje = inbox.remove(0);

        int i = 0;
        int numberOfMessages = 1;

        while(i < inbox.size()){

            if(inbox.get(i).getContactName().equals(incomingMensaje.getContactName())){

                if(numberOfMessages == 1)
                    incomingMensaje.setBodyMessage("Mensaje 1: " + incomingMensaje.getBodyMessage());

                numberOfMessages++;

                incomingMensaje.setBodyMessage(incomingMensaje.getBodyMessage() + ". Mensaje " + ((Integer) numberOfMessages).toString() + ": " + inbox.remove(i));
            }
            i++;
        }

        Typeface fontBold = Typeface.createFromAsset(context.getAssets(),"Bariol_Bold.otf");
        Typeface fontRegular = Typeface.createFromAsset(context.getAssets(),"Bariol_Bold.otf");

        TextView title =(TextView) customDialog.findViewById(R.id.title);
        title.setTypeface(fontRegular);

        TextView contact = (TextView) customDialog.findViewById(R.id.contact);
        TextView phone = (TextView) customDialog.findViewById(R.id.phone);

        if(!incomingMensaje.getContactName().equals(incomingMensaje.getPhoneNumber())) {
            contact.setText(incomingMensaje.getContactName());
            contact.setTypeface(fontBold);
            phone.setText(incomingMensaje.getPhoneNumber());
            phone.setTypeface(fontRegular);
        }else{
            contact.setText(incomingMensaje.getPhoneNumber());
            contact.setTypeface(fontBold);
            phone.setText("Número no agendado");
            phone.setTypeface(fontRegular);
        }


        TextView dateTime = (TextView) customDialog.findViewById(R.id.date);
        dateTime.setText(incomingMensaje.getDate());
        dateTime.setTypeface(fontRegular);


        final TextView bodySMS = (TextView) customDialog.findViewById(R.id.contenido);
        bodySMS.setText(incomingMensaje.getBodyMessage());
        bodySMS.setTypeface(fontRegular);


        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                ((MainMenuActivity)commandHandlerManager.getMainActivity()).setButtonsEnabled();
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.responder).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                STTService.getInstance().stopListening();
                commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "si")));
            }
        });
        customDialog.findViewById(R.id.leer).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                STTService.getInstance().stopListening();

                customDialog.findViewById(R.id.cancelar).setEnabled(false);
                customDialog.findViewById(R.id.leer).setEnabled(false);
                customDialog.findViewById(R.id.responder).setEnabled(false);

                int delay = incomingMensaje.getBodyMessage().length()/5 + 1;
                delay = delay * 550;

                commandHandlerManager.getTextToSpeech().speakText(bodySMS.getText().toString());

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        customDialog.findViewById(R.id.cancelar).setEnabled(true);
                        customDialog.findViewById(R.id.leer).setEnabled(true);
                        customDialog.findViewById(R.id.responder).setEnabled(true);
                    }
                }, delay);

            }
        });

        customDialog.show();
        customDialog.findViewById(R.id.cancelar).setEnabled(false);
        customDialog.findViewById(R.id.leer).setEnabled(false);
        customDialog.findViewById(R.id.responder).setEnabled(false);

        int delay;

        if(numberOfMessages == 1){

            delay = CommandHandlerManager.getInstance().getTextToSpeech().speakTextWithoutStart(
                    incomingMensaje.getContactName() + " te envió el mensaje: " + incomingMensaje.getBodyMessage() + " ¿Querés responderle?");

        }else{

            delay = CommandHandlerManager.getInstance().getTextToSpeech().speakTextWithoutStart(
                    incomingMensaje.getContactName() + " te envió " + numberOfMessages + " mensajes. " + incomingMensaje.getBodyMessage() + " ¿Querés responderle?");

        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                customDialog.findViewById(R.id.cancelar).setEnabled(true);
                customDialog.findViewById(R.id.leer).setEnabled(true);
                customDialog.findViewById(R.id.responder).setEnabled(true);
            }
        }, delay);

        STTService.getInstance().setIsListening(true);
        ((MainMenuActivity)commandHandlerManager.getMainActivity()).setButtonsDisabled();
        commandHandlerManager.setCurrentCommandHandler(new ResponderSMSHandler(commandHandlerManager.getTextToSpeech(), commandHandlerManager.getContext(), commandHandlerManager));
        commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "responder sms")));

    }

    //copiar archivos que estan en la carpeta drawable y colors de la carpeta values
    public  void displayRespuesta(){
        final Dialog customDialog = new Dialog(commandHandlerManager.getMainActivity());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_sms_respond);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        actualDialog = customDialog;

        Typeface fontBold = Typeface.createFromAsset(context.getAssets(),"Bariol_Bold.otf");
        Typeface fontRegular = Typeface.createFromAsset(context.getAssets(),"Bariol_Bold.otf");

        answer = (EditText) customDialog.findViewById(R.id.contenido);
        answer.setTypeface(fontRegular);

        TextView textFor = (TextView) customDialog.findViewById(R.id.textFor);
        textFor.setTypeface(fontRegular);

        TextView contact = (TextView) customDialog.findViewById(R.id.contact);
        TextView phone = (TextView) customDialog.findViewById(R.id.phone);

        if(!incomingMensaje.getContactName().equals(incomingMensaje.getPhoneNumber())) {
            contact.setText(incomingMensaje.getContactName());
            contact.setTypeface(fontBold);
            phone.setText(incomingMensaje.getPhoneNumber());
            phone.setTypeface(fontRegular);
        }else{
            contact.setText(incomingMensaje.getPhoneNumber());
            contact.setTypeface(fontBold);
            phone.setText("Número no agendado");
            phone.setTypeface(fontRegular);
        }

        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                ((MainMenuActivity)commandHandlerManager.getMainActivity()).setButtonsEnabled();
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.enviar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String smsBody=answer.getText().toString();
                if(smsBody.equals(""))
                    Toast.makeText(context, "Ingresá un mensaje", Toast.LENGTH_LONG).show();
                else {
                    try {

                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(incomingMensaje.getPhoneNumber(), null, smsBody, null, null);
                        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                            saveInOutbox(incomingMensaje.getPhoneNumber(), smsBody);
                        Toast.makeText(context.getApplicationContext(), "SMS enviado", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(context.getApplicationContext(), "El envío falló. Reintentá luego", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    STTService.getInstance().setIsListening(false);
                    commandHandlerManager.setNullCommand();
                    ((MainMenuActivity) commandHandlerManager.getMainActivity()).setButtonsEnabled();
                    customDialog.dismiss();
                }
            }
        });
        customDialog.findViewById(R.id.leer).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                STTService.getInstance().stopListening();

                String smsBody = answer.getText().toString();

                if(smsBody.equals(""))
                    Toast.makeText(context, "Ingresá un mensaje", Toast.LENGTH_LONG).show();
                else {

                    customDialog.findViewById(R.id.cancelar).setEnabled(false);
                    customDialog.findViewById(R.id.leer).setEnabled(false);
                    customDialog.findViewById(R.id.enviar).setEnabled(false);

                    int delay = answer.length()/5 + 1;
                    delay = delay * 550;

                    commandHandlerManager.getTextToSpeech().speakText(smsBody);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            customDialog.findViewById(R.id.cancelar).setEnabled(true);
                            customDialog.findViewById(R.id.leer).setEnabled(true);
                            customDialog.findViewById(R.id.enviar).setEnabled(true);
                        }
                    }, delay);
                }
            }
        });

        customDialog.show();

    }

    public void setAnswer(String message){

        answer.setText(message);

    }

    public String respondMessage(){

        String smsBody = answer.getText().toString();
        if(smsBody.equals("")) {
            actualDialog.dismiss();
            return "El mensaje no pudo ser enviado. No debés blanquear el mensaje ingresado";
        }
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(incomingMensaje.getPhoneNumber(), null, smsBody, null, null);
            if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                saveInOutbox(incomingMensaje.getPhoneNumber(), smsBody);
            actualDialog.dismiss();
            return "El mensaje fue enviado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            actualDialog.dismiss();
            return "El mensaje no pudo ser enviado. Reintentá luego";
        }
    }


    public final BroadcastReceiver mReceivedSMSReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            Bundle intentExtras = intent.getExtras();
            if (intentExtras != null) {
                Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

                if (ACTION.equals(action)) {

                    for (int i = 0; i < sms.length; ++i) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                        String nameContact=getContactName(context,smsMessage.getOriginatingAddress());
                        Date date = new Date(smsMessage.getTimestampMillis());
                        String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
                        Mensaje incomingMensaje = new Mensaje(smsMessage.getOriginatingAddress(),smsMessage.getMessageBody(),nameContact, formattedDate);

                        inbox.add(incomingMensaje);

                        if(!STTService.getInstance().getIsListening() &&
                                commandHandlerManager.getCurrentActivity() == CommandHandlerManager.ACTIVITY_MAIN &&
                                commandHandlerManager.getCommandHandler() == null) {

                            displayIncomingSMS();
                        }
                    }
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

    public int getInboxSize(){

        return inbox.size();

    }

    public void saveInOutbox(String number, String message){

        ContentValues values = new ContentValues();
        values.put("address", number); // phone number to send
        values.put("date", System.currentTimeMillis());
        values.put("read", "1"); // if you want to mark is as unread set to 0
        values.put("type", "2"); // 2 means sent message
        values.put("body", message);

        Uri uri = Uri.parse("content://sms/");
        context.getContentResolver().insert(uri,values);

    }


    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    public static void destroyInstance() {
        instance = null;
    }

}
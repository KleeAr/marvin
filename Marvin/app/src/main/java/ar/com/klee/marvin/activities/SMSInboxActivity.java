package ar.com.klee.marvin.activities;

import android.app.Activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.configuration.UserConfig;
import ar.com.klee.marvin.sms.InboxAdaptor;
import ar.com.klee.marvin.sms.Mensaje;
import ar.com.klee.marvin.sms.SMSDriver;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;
import ar.com.klee.marvin.voiceControl.handlers.smsInbox.LeerSMSNumeroHandler;

public class SMSInboxActivity extends Activity {

    private final List<Mensaje> smsMessagesList = new ArrayList<>();
    private ListView smsListView;
    private Mensaje mensaje;
    private CommandHandlerManager commandHandlerManager;
    private Dialog actualDialog;
    private EditText answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(UserConfig.getInstance().getOrientation() == UserConfig.ORIENTATION_PORTRAIT)
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        setContentView(R.layout.activity_sms_inbox);

        Typeface fontBold = Typeface.createFromAsset(getAssets(),"Bariol_Bold.otf");
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setTypeface(fontBold);

        smsListView = (ListView) findViewById(R.id.SMSList);
        refreshSmsInbox();

        commandHandlerManager = CommandHandlerManager.getInstance();

        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_SMS_INBOX,this);
    }

    public void onBackPressed(){
        commandHandlerManager.setNullCommand();
        STTService.getInstance().setIsListening(false);
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_MAIN, commandHandlerManager.getMainActivity());
        this.finish();
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

            Date date = new Date(smsInboxCursor.getLong(inboxDate));
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);

            String contactName = SMSDriver.getContactName(getApplicationContext(), smsInboxCursor.getString(indexAddress));
            Mensaje iMensaje = new Mensaje(smsInboxCursor.getString(indexAddress), smsInboxCursor.getString(indexBody), contactName, formattedDate);

            smsMessagesList.add(iMensaje);
        } while (smsInboxCursor.moveToNext());
        smsInboxCursor.close();

        final InboxAdaptor adapter = new InboxAdaptor(this, smsMessagesList);
        smsListView.setAdapter(adapter);


        smsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                position++;

                commandHandlerManager.setNullCommand();
                STTService.getInstance().setIsListening(true);
                STTService.getInstance().stopListening();
                commandHandlerManager.setCurrentCommandHandler(new LeerSMSNumeroHandler(commandHandlerManager.getTextToSpeech(), commandHandlerManager.getContext(), commandHandlerManager));
                commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "leer sms número " + ((Integer) position).toString())));

            }
        });

    }

    public String getLastMessage(){

        Mensaje lastMessage = smsMessagesList.get(0);
        String toSpeak = "";

        toSpeak += lastMessage.getContactName() + " te envió un mensaje el ";

        String date = lastMessage.getDate();
        StringTokenizer stringTokenizer = new StringTokenizer(date);
        date = stringTokenizer.nextToken().replaceAll("/", " del ");
        date += " a las " + stringTokenizer.nextToken().replaceAll(":"," y ");

        toSpeak += date + ". El mensaje es: ";
        toSpeak += lastMessage.getBodyMessage();

        mensaje = lastMessage;

        return toSpeak;

    }

    public String getLastMessageOfContact(String contact){

        int i;

        String accents = "áéíóúÁÉÍÓÚ";
        String noAccents = "aeiouAEIOU";
        String contactWithoutAccent = contact;

        for(i=0;i<accents.length();i++)
            contactWithoutAccent = contactWithoutAccent.replace(accents.charAt(i),noAccents.charAt(i));

        i = 0;

        while(i < smsMessagesList.size()){

            Mensaje message = smsMessagesList.get(i);

            if(message.getContactName().toLowerCase().equals(contact) || message.getContactName().toLowerCase().equals(contactWithoutAccent)){
                String toSpeak = "";

                toSpeak += message.getContactName() + " te envió un mensaje el ";

                String date = message.getDate();
                StringTokenizer stringTokenizer = new StringTokenizer(date);
                date = stringTokenizer.nextToken().replaceAll("/", " del ");
                date += " a las " + stringTokenizer.nextToken().replaceAll(":"," y ");

                toSpeak += date + ". El mensaje es: ";
                toSpeak += message.getBodyMessage();

                mensaje = message;

                return toSpeak;
            }

            i++;
        }

        return "";
    }

    public String getLastMessageOfNumber(String number){

        int i = 0;

        while(i < smsMessagesList.size()){

            Mensaje message = smsMessagesList.get(i);

            if(message.getContactName().equals(number) || message.getPhoneNumber().equals(number)){
                String toSpeak = "";

                toSpeak += message.getContactName() + " te envió un mensaje el ";

                String date = message.getDate();
                StringTokenizer stringTokenizer = new StringTokenizer(date);
                date = stringTokenizer.nextToken().replaceAll("/", " del ");
                date += " a las " + stringTokenizer.nextToken().replaceAll(":"," y ");

                toSpeak += date + ". El mensaje es: ";
                toSpeak += message.getBodyMessage();

                mensaje = message;

                return toSpeak;
            }

            i++;
        }

        return "";
    }

    public String getMessageNro(int index){

        if(index >= 0 && index < smsMessagesList.size()){

            Mensaje message = smsMessagesList.get(index);

            String toSpeak = "";

            toSpeak += message.getContactName() + " te envió un mensaje el ";

            String date = message.getDate();
            StringTokenizer stringTokenizer = new StringTokenizer(date);
            date = stringTokenizer.nextToken().replaceAll("/", " del ");
            date += " a las " + stringTokenizer.nextToken().replaceAll(":"," y ");

            toSpeak += date + ". El mensaje es: ";
            toSpeak += message.getBodyMessage();

            mensaje = message;

            return toSpeak;

        }

        return ((Integer)smsMessagesList.size()).toString();

    }

    public void showCallDialog() {

        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_inbox);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        actualDialog = customDialog;

        Typeface fontBold = Typeface.createFromAsset(getAssets(),"Bariol_Regular.otf");

        TextView notifiacion = (TextView) customDialog.findViewById(R.id.responseHeader);
        notifiacion.setText("¿Querés realizar alguna acción con el contacto " + mensaje.getContactName() +"?");

        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.responder).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                STTService.getInstance().stopListening();
                commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "enviar sms")));
            }
        });
        customDialog.findViewById(R.id.llamar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
                STTService.getInstance().setIsListening(false);
                STTService.getInstance().stopListening();
                commandHandlerManager.setNullCommand();
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.setMode(AudioManager.MODE_IN_CALL);
                am.setSpeakerphoneOn(true);
                //lanza un intent con el numero del contacto
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mensaje.getPhoneNumber()));
                startActivity(intent);
            }
        });

        disableButtons();

        customDialog.show();
    }

    public void enableButtons(){
        commandHandlerManager.getMainActivity().runOnUiThread(new Runnable() {
            public void run() {
                actualDialog.findViewById(R.id.cancelar).setEnabled(true);
                actualDialog.findViewById(R.id.responder).setEnabled(true);
                actualDialog.findViewById(R.id.llamar).setEnabled(true);
            }
        });
    }

    public void disableButtons(){
        commandHandlerManager.getMainActivity().runOnUiThread(new Runnable() {
            public void run() {
                actualDialog.findViewById(R.id.cancelar).setEnabled(false);
                actualDialog.findViewById(R.id.responder).setEnabled(false);
                actualDialog.findViewById(R.id.llamar).setEnabled(false);
            }
        });
    }

    public void enableButtonsRespond(){
        commandHandlerManager.getMainActivity().runOnUiThread(new Runnable() {
            public void run() {
                actualDialog.findViewById(R.id.cancelar).setEnabled(true);
                actualDialog.findViewById(R.id.enviar).setEnabled(true);
                actualDialog.findViewById(R.id.leer).setEnabled(true);
            }
        });
    }

    public void disableButtonsRespond(){
        commandHandlerManager.getMainActivity().runOnUiThread(new Runnable() {
            public void run() {
                actualDialog.findViewById(R.id.cancelar).setEnabled(false);
                actualDialog.findViewById(R.id.enviar).setEnabled(false);
                actualDialog.findViewById(R.id.leer).setEnabled(false);
            }
        });
    }

    public void cancelDialog(){
        actualDialog.dismiss();
    }

    public void respond(){
        actualDialog.dismiss();
        displayRespuesta();
    }

    public void call(){
        actualDialog.dismiss();
        //lanza un intent con el numero del contacto
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_IN_CALL);
        am.setSpeakerphoneOn(true);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mensaje.getPhoneNumber()));
        startActivity(intent);
    }

    //Es el mismo metodo que esta en ManagerMensaje, pero no lo puedo usar porque me tira error de contexto
    public  void displayRespuesta(){
        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_sms_respond);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        actualDialog = customDialog;

        Typeface fontBold = Typeface.createFromAsset(getApplicationContext().getAssets(),"Bariol_Bold.otf");
        Typeface fontRegular = Typeface.createFromAsset(getApplicationContext().getAssets(),"Bariol_Bold.otf");

        answer = (EditText) customDialog.findViewById(R.id.contenido);
        answer.setTypeface(fontRegular);

        TextView textFor = (TextView) customDialog.findViewById(R.id.textFor);
        textFor.setTypeface(fontRegular);

        TextView contact = (TextView) customDialog.findViewById(R.id.contact);
        TextView phone = (TextView) customDialog.findViewById(R.id.phone);

        if(!mensaje.getContactName().equals(mensaje.getPhoneNumber())) {
            contact.setText(mensaje.getContactName());
            contact.setTypeface(fontBold);
            phone.setText(mensaje.getPhoneNumber());
            phone.setTypeface(fontRegular);
        }else{
            contact.setText(mensaje.getPhoneNumber());
            contact.setTypeface(fontBold);
            phone.setText("Número no agendado");
            phone.setTypeface(fontRegular);
        }


        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.enviar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String smsBody=answer.getText().toString();
                if(smsBody.equals(""))
                    Toast.makeText(getApplicationContext(), "Ingresá un mensaje", Toast.LENGTH_LONG).show();
                else {
                    try {

                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(mensaje.getPhoneNumber(), null, smsBody, null, null);
                        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                            saveInOutbox(mensaje.getPhoneNumber(), smsBody);
                        Toast.makeText(getApplicationContext(), "SMS enviado", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "El envío falló. Reintentá luego", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    STTService.getInstance().setIsListening(false);
                    commandHandlerManager.setNullCommand();
                    customDialog.dismiss();
                }
            }
        });
        customDialog.findViewById(R.id.leer).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {

                String smsBody = answer.getText().toString();

                if(smsBody.equals(""))
                    Toast.makeText(getApplicationContext(), "Ingresá un mensaje", Toast.LENGTH_LONG).show();
                else {

                    STTService.getInstance().stopListening();

                    customDialog.findViewById(R.id.cancelar).setEnabled(false);
                    customDialog.findViewById(R.id.leer).setEnabled(false);
                    customDialog.findViewById(R.id.enviar).setEnabled(false);

                    commandHandlerManager.getTextToSpeech().speakText("INBOXR - " + smsBody);

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
            smsManager.sendTextMessage(mensaje.getPhoneNumber(), null, smsBody, null, null);
            if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                saveInOutbox(mensaje.getPhoneNumber(), smsBody);
            actualDialog.dismiss();
            return "El mensaje fue enviado correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            actualDialog.dismiss();
            return "El mensaje no pudo ser enviado. Reintentá luego";
        }
    }

    public void saveInOutbox(String number, String message){

        ContentValues values = new ContentValues();
        values.put("address", number); // phone number to send
        values.put("date", System.currentTimeMillis());
        values.put("read", "1"); // if you want to mark is as unread set to 0
        values.put("type", "2"); // 2 means sent message
        values.put("body", message);

        Uri uri = Uri.parse("content://sms/");
        getApplicationContext().getContentResolver().insert(uri,values);

    }

    public Dialog getActualDialog() {
        return actualDialog;
    }
}

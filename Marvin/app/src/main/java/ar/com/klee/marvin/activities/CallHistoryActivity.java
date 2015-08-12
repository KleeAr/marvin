package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.CallLog;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.call.Call;
import ar.com.klee.marvin.call.CallDriver;
import ar.com.klee.marvin.call.HistoryAdapter;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;
import ar.com.klee.marvin.voiceControl.handlers.callHistory.ConsultarRegistroNumeroHandler;

/*
* Clase donde se maneja el historial de llamdas a traves de una lista
 */

public class CallHistoryActivity extends Activity implements AdapterView.OnItemClickListener {

    private Button button;
    private ListView callListView;
    private List<Call> callList = new ArrayList<Call>();
    private Dialog actualDialog;
    private CommandHandlerManager commandHandlerManager;
    private Call call;
    private EditText answer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);

        callListView = (ListView) findViewById(R.id.callListView);

        //Acceso a la lista historica de llamadas
        Cursor cursorH = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = cursorH.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursorH.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursorH.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursorH.getColumnIndex(CallLog.Calls.DURATION);

        while (cursorH.moveToNext()) {
            String phNumber = cursorH.getString(number);
            String callType = cursorH.getString(type);
            Date callDayTime = new Date(cursorH.getLong(date));
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

            String contactName = CallDriver.getContactName(getApplicationContext(), phNumber);

            String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(callDayTime);

            objCall.setContactName(contactName);
            objCall.setNumberPhone(phNumber);
            objCall.setDuration(callDuration);
            objCall.setDate(formattedDate);
            objCall.setUnformattedDate(callDayTime);
            objCall.setType(dir);
            callList.add(objCall); //se agrega el objeto a la lista

        }
        cursorH.close();

        HistoryAdapter objAdapter = new HistoryAdapter(CallHistoryActivity.this, R.layout.item_call_history, callList);
        callListView.setAdapter(objAdapter);
        callListView.setOnItemClickListener(this);

        if (null != callList && callList.size() != 0) {
            Collections.sort(callList, new Comparator<Call>() {
                @Override
                public int compare(Call lhs, Call rhs) {
                    return rhs.getUnformattedDate().compareTo(lhs.getUnformattedDate());
                }
            });

        }

        if(callList.size()==0)
            Toast.makeText(this,"Registro de llamadas vacío", Toast.LENGTH_SHORT).show();

        commandHandlerManager = CommandHandlerManager.getInstance();

        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_CALL_HISTORY,this);
    }

    public void onBackPressed(){
        commandHandlerManager.setNullCommand();
        STTService.getInstance().setIsListening(false);
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_MAIN, commandHandlerManager.getMainActivity());
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> listview, View v, int position, long id) {
        position++;

        commandHandlerManager.setNullCommand();
        STTService.getInstance().stopListening();
        STTService.getInstance().setIsListening(true);
        commandHandlerManager.setCurrentCommandHandler(new ConsultarRegistroNumeroHandler(commandHandlerManager.getTextToSpeech(), commandHandlerManager.getContext(), commandHandlerManager));
        commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), "consultar registro número " + ((Integer)position).toString())));
    }

    public String getLastCall(){

        Call lastCall = callList.get(0);
        String toSpeak = "";

        String type = lastCall.getType();

        String contact = lastCall.getContactName();
        String contactWithSpaces = contact;

        if(contact.equals(lastCall.getNumberPhone())) {
            int j = 0;
            contactWithSpaces = "";

            while(j < contact.length()){
                if(j%2 == 0)
                    contactWithSpaces += contact.charAt(j) + " ";
                else
                    contactWithSpaces += contact.charAt(j);
                j++;
            }
        }

        if(type.equals("Entrante") || type.equals("Perdida"))
            toSpeak += "Llamada " + lastCall.getType() + " de " + contactWithSpaces + " el ";
        else
            toSpeak += "Llamada " + lastCall.getType() + " a " + contactWithSpaces + " el ";

        String date = lastCall.getDate();
        StringTokenizer stringTokenizer = new StringTokenizer(date);
        date = stringTokenizer.nextToken().replaceAll("/", " del ");
        date += " a las " + stringTokenizer.nextToken().replaceAll(":"," y ");

        toSpeak += date;

        call = lastCall;

        return toSpeak;

    }

    public String getLastCallOfContact(String contact){

        int i;

        String accents = "áéíóúÁÉÍÓÚ";
        String noAccents = "aeiouAEIOU";
        String contactWithoutAccent = contact;

        for(i=0;i<accents.length();i++)
            contactWithoutAccent = contactWithoutAccent.replace(accents.charAt(i),noAccents.charAt(i));

        i = 0;

        while(i < callList.size()){

            Call selectedCall = callList.get(i);

            if(selectedCall.getContactName().toLowerCase().equals(contact) || selectedCall.getContactName().toLowerCase().equals(contactWithoutAccent)){
                String toSpeak = "";

                String type = selectedCall.getType();

                if(type.equals("Entrante") || type.equals("Perdida"))
                    toSpeak += "Llamada " + selectedCall.getType() + " de " + selectedCall.getContactName() + " el ";
                else
                    toSpeak += "Llamada " + selectedCall.getType() + " a " + selectedCall.getContactName() + " el ";

                String date = selectedCall.getDate();
                StringTokenizer stringTokenizer = new StringTokenizer(date);
                date = stringTokenizer.nextToken().replaceAll("/", " del ");
                date += " a las " + stringTokenizer.nextToken().replaceAll(":"," y ");

                toSpeak += date;

                call = selectedCall;

                return toSpeak;
            }

            i++;
        }

        return "";
    }

    public String getLastCallOfNumber(String number){

        int i = 0;

        while(i < callList.size()){

            Call selectedCall = callList.get(i);

            if(selectedCall.getContactName().equals(number) || selectedCall.getNumberPhone().equals(number)){
                String toSpeak = "";

                String type = selectedCall.getType();

                String contactWithSpaces = "";

                int j = 0;
                contactWithSpaces = "";

                while(j < number.length()){
                    if(j%2 == 0)
                        contactWithSpaces += number.charAt(j) + " ";
                    else
                        contactWithSpaces += number.charAt(j);
                    j++;
                }

                if(type.equals("Entrante") || type.equals("Perdida"))
                    toSpeak += "Llamada " + selectedCall.getType() + " de " + contactWithSpaces + " el ";
                else
                    toSpeak += "Llamada " + selectedCall.getType() + " a " + contactWithSpaces + " el ";

                String date = selectedCall.getDate();
                StringTokenizer stringTokenizer = new StringTokenizer(date);
                date = stringTokenizer.nextToken().replaceAll("/", " del ");
                date += " a las " + stringTokenizer.nextToken().replaceAll(":"," y ");

                toSpeak += date;

                call = selectedCall;

                return toSpeak;
            }

            i++;
        }

        return "";
    }

    public String getCallNro(int index){

        if(index >= 0 && index < callList.size()){

            Call selectedCall = callList.get(index);

            String toSpeak = "";

            String type = selectedCall.getType();
            String contact = selectedCall.getContactName();
            String contactWithSpaces = contact;

            if(contact.equals(selectedCall.getNumberPhone())) {
                int j = 0;
                contactWithSpaces = "";

                while(j < contact.length()){
                    if(j%2 == 0)
                        contactWithSpaces += contact.charAt(j) + " ";
                    else
                        contactWithSpaces += contact.charAt(j);
                    j++;
                }
            }

            if(type.equals("Entrante") || type.equals("Perdida"))
                toSpeak += "Llamada " + selectedCall.getType() + " de " + contactWithSpaces + " el ";
            else
                toSpeak += "Llamada " + selectedCall.getType() + " a " + contactWithSpaces + " el ";

            String date = selectedCall.getDate();
            StringTokenizer stringTokenizer = new StringTokenizer(date);
            date = stringTokenizer.nextToken().replaceAll("/", " del ");
            date += " a las " + stringTokenizer.nextToken().replaceAll(":"," y ");

            toSpeak += date;

            call = selectedCall;

            return toSpeak;

        }

        return ((Integer)callList.size()).toString();

    }

    /*
    * Muesta el dialogo de consulta sobre si se quiere realizar alguna accion sobre un item de la lista de llamadas
    * Recibe como parametro el numero del item selecccionado
     */
    public  void showCallDialog(){
        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_inbox);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        actualDialog = customDialog;

        String contact = call.getContactName();
        final String number = call.getNumberPhone();

        Typeface font = Typeface.createFromAsset(getAssets(),"Bariol_Regular.otf");

        TextView textFor = (TextView) customDialog.findViewById(R.id.responseHeader);
        textFor.setTypeface(font);
        if(contact.equals(number))
            textFor.setText("¿Querés realizar alguna acción con el número: " + contact + " ?");
        else
            textFor.setText("¿Querés realizar alguna acción con el contacto: " + contact + " ?");

        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
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
                //lanza un intent con el numero del contacto
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + call.getNumberPhone()));
                startActivity(intent);
            }
        });

        customDialog.show();

    }

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

        if(!call.getContactName().equals(call.getNumberPhone())) {
            contact.setText(call.getContactName());
            contact.setTypeface(fontBold);
            phone.setText(call.getNumberPhone());
            phone.setTypeface(fontRegular);
        }else{
            contact.setText(call.getNumberPhone());
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
                        smsManager.sendTextMessage(call.getNumberPhone(), null, smsBody, null, null);
                        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                            saveInOutbox(call.getNumberPhone(), smsBody);
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
                final boolean isListening = STTService.getInstance().getIsListening();
                STTService.getInstance().setIsListening(false);

                String smsBody = answer.getText().toString();

                if(smsBody.equals(""))
                    Toast.makeText(getApplicationContext(), "Ingresá un mensaje", Toast.LENGTH_LONG).show();
                else {

                    customDialog.findViewById(R.id.cancelar).setEnabled(false);
                    customDialog.findViewById(R.id.leer).setEnabled(false);
                    customDialog.findViewById(R.id.enviar).setEnabled(false);

                    int delay = commandHandlerManager.getTextToSpeech().speakTextWithoutStart(smsBody);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            STTService.getInstance().setIsListening(isListening);
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
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + call.getNumberPhone()));
        startActivity(intent);
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
            smsManager.sendTextMessage(call.getNumberPhone(), null, smsBody, null, null);
            if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                saveInOutbox(call.getNumberPhone(), smsBody);
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

}

package ar.com.klee.marvin.call;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;

public class CallDriver {

    private static final String TAG = CallDriver.class.getSimpleName();
    public static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    private EditText editPhone;
    private Dialog actualDialog;

    private Context context;
    private CommandHandlerManager commandHandlerManager;
    private static CallDriver instance;

    private String lastOutgoingCallNumber;

    public CallDriver(Context context) {

        this.context = context;

    }

    public void initializeCommandHandlerManager(){
        commandHandlerManager = CommandHandlerManager.getInstance();
    }

    public static CallDriver getInstance(){
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getInstance");
        }
        return instance;
    }

    public static CallDriver initializeInstance(Context context) {
        if(instance != null) {
            throw new IllegalStateException("Instance already initialized");
        }
        CallDriver.instance = new CallDriver(context);
        return instance;
    }


    /*
    Funciones de busqueda en la lista de contactos
     */

    public void retrieveContactNumber() {

        Cursor cursor;  // Cursor object
        String mime;    // MIME type
        int dataIdx;    // Index of DATA1 column
        int mimeIdx;    // Index of MIMETYPE column
        int nameIdx;    // Index of DISPLAY_NAME column
        String name = "";
        String number = "";

        // Get the name
        cursor = context.getContentResolver().query(uriContact,
                new String[] { ContactsContract.Contacts.DISPLAY_NAME },
                null, null, null);
        if (cursor.moveToFirst()) {
            nameIdx = cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME);
            name = cursor.getString(nameIdx);

            // Set up the projection
            String[] projection = {
                    ContactsContract.Data.DISPLAY_NAME,
                    ContactsContract.Contacts.Data.DATA1,
                    ContactsContract.Contacts.Data.MIMETYPE };

            // Query ContactsContract.Data
            cursor = context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI, projection,
                    ContactsContract.Data.DISPLAY_NAME + " = ?",
                    new String[] { name },
                    null);

            if (cursor.moveToFirst()) {
                // Get the indexes of the MIME type and data
                mimeIdx = cursor.getColumnIndex(
                        ContactsContract.Contacts.Data.MIMETYPE);
                dataIdx = cursor.getColumnIndex(
                        ContactsContract.Contacts.Data.DATA1);

                // Match the data to the MIME type, store in variables
                do {
                    mime = cursor.getString(mimeIdx);
                    if (ContactsContract.CommonDataKinds.Phone
                            .CONTENT_ITEM_TYPE.equalsIgnoreCase(mime)) {
                        number = cursor.getString(dataIdx);
                        number = PhoneNumberUtils.formatNumber(number);
                    }
                } while (cursor.moveToNext());
            }
        }


        STTService.getInstance().stopListening();
        editPhone.setText(number);
        commandHandlerManager.getCurrentContext().put("SET_NUMBER",true);
        commandHandlerManager.getCurrentContext().put("SET_CONTACT",true);
        commandHandlerManager.getCurrentContext().put("STEP",3);
        commandHandlerManager.getTextToSpeech().speakText("¿Querés llamar al contacto seleccionado?");
    }



    public void setUriContact(Uri uri){
        uriContact = uri;
    }

    /*
    Funcion que recibe el nombre de un contacto y devuelve el numero del contacto
     */
    public String getPhoneNumber(Context context, String name) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();
        if(ret==null)
            ret = "No encontrado";
        return ret;
    }

    public  void outgoingCallDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(commandHandlerManager.getMainActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true);
        builder.setTitle("Llamar a:");
        builder.setIcon(R.drawable.marvin);

        LinearLayout layout = new LinearLayout(commandHandlerManager.getMainActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        final Button buttonAgenda = new Button(commandHandlerManager.getMainActivity());
        buttonAgenda.setBackgroundResource(R.mipmap.ic_contact_phone_black_48dp);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
        params.gravity = Gravity.CENTER;
        buttonAgenda.setLayoutParams(params);
        buttonAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // using native contacts selection
                // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
                STTService.getInstance().stopListening();

                commandHandlerManager.getMainActivity().startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            }
        });
        layout.addView(buttonAgenda);



        final EditText editPhone = new EditText(commandHandlerManager.getMainActivity());
        editPhone.setHint("Ingresar telefono o contacto");
        editPhone.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30) });
        layout.addView(editPhone);




        builder.setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String number = editPhone.getText().toString();
                commandHandlerManager.setNullCommand();
                STTService.getInstance().setIsListening(false);
                STTService.getInstance().stopListening();
                lastOutgoingCallNumber = number;
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                commandHandlerManager.getMainActivity().startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                ((MainMenuActivity)commandHandlerManager.getMainActivity()).setButtonsEnabled();
                dialog.dismiss();
            }
        });


        builder.show();

    }

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

    public void setPhone(String number){
        editPhone.setText(number);
    }

    public void closeDialog(){
        actualDialog.dismiss();
    }

    public void callNumber(String number){
        STTService.getInstance().stopListening();
        lastOutgoingCallNumber = number;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        commandHandlerManager.getMainActivity().startActivity(intent);
        actualDialog.dismiss();
    }

    public String getLastOutgoingCallNumber(){
        return lastOutgoingCallNumber;
    }

    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    public static void destroyInstance() {
        instance = null;
    }
}

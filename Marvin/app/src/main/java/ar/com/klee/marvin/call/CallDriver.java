package ar.com.klee.marvin.call;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;

public class CallDriver {

    private static final String TAG = CallDriver.class.getSimpleName();
    public static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID
    private static EditText editPhone;
    private Dialog actualDialog;

    private Context context;
    private CommandHandlerManager commandHandlerManager;
    private static CallDriver instance;

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

        String number = null;

        // getting contacts ID
        Cursor cursorID = context.getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            number = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        editPhone.setText(number);
        commandHandlerManager.getCurrentContext().put("SET_NUMBER",true);
        commandHandlerManager.getCurrentContext().put("STEP",1);
        commandHandlerManager.setCurrentContext(commandHandlerManager.getCommandHandler().drive(commandHandlerManager.getCommandHandler().createContext(commandHandlerManager.getCurrentContext(), commandHandlerManager.getActivity(), number)));
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
        final Dialog customDialog = new Dialog(commandHandlerManager.getMainActivity());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.dialog_call_outgoing);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        actualDialog = customDialog;

        Typeface fontBold = Typeface.createFromAsset(context.getAssets(),"Bariol_Bold.otf");
        TextView toCall = (TextView) customDialog.findViewById(R.id.toCall);
        toCall.setTypeface(fontBold);

        editPhone = (EditText) customDialog.findViewById(R.id.editPhone);
        editPhone.setTypeface(fontBold);


        customDialog.findViewById(R.id.btnAgenda).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // using native contacts selection
                // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
                STTService.getInstance().stopListening();
                commandHandlerManager.getMainActivity().startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            }
        });
        customDialog.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                commandHandlerManager.setNullCommand();
                STTService.getInstance().setIsListening(false);
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.llamar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                String number = editPhone.getText().toString();
                commandHandlerManager.setNullCommand();
                STTService.getInstance().setIsListening(false);
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                commandHandlerManager.getMainActivity().startActivity(intent);
                customDialog.dismiss();

            }
        });

        customDialog.show();

    }

    public void setPhone(String number){
        editPhone.setText(number);
    }

    public void closeDialog(){
        actualDialog.dismiss();
    }

    public void callNumber(String number){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        commandHandlerManager.getMainActivity().startActivity(intent);
        actualDialog.dismiss();
    }

    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    public static void destroyInstance() {
        instance = null;
    }
}

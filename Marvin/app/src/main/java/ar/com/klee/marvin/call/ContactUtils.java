package ar.com.klee.marvin.call;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dalvik.annotation.TestTarget;

/**
 * @author msalerno
 */
public class ContactUtils {


    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("(\\+[0-9]+)|([0-9]+)");


    private ContactUtils() {
    }

    /**
     *
     * Returns a contact speech name. In case of a number, it's divided by spaces
     *
     * @param contact the contact's phone number or name
     * @return
     */
    public static String getContactSpeechName(String contact) {

        if(PHONE_NUMBER_PATTERN.matcher(contact).matches()) {
            int i=0;
            StringBuilder contactWithSpaces = new StringBuilder();
            while(i < contact.length()){
                contactWithSpaces.append(contact.charAt(i)).append(" ");
                i++;
            }
            return contactWithSpaces.toString();
        } else {
            return contact;
        }
    }

    /**
        Funcion que recibe como parametro un numero de telefono
        Retorna el nombre del contacto si esta registrado, sino devuelve el numero de telefono
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

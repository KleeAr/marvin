package ar.com.klee.marvin.call;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

/**
 *
 */
public class ContactUtils {

    private ContactUtils() {

    }

    /**
     * Retrieves the contact name for the given phoneNumber
     *
     * @param context
     * @param phoneNumber the phone number of the contact
     * @return the contact name if the contact is in the device. Else it returns phoneNumber
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

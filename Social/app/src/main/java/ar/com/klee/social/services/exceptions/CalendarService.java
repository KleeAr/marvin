package ar.com.klee.social.services.exceptions;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Salerno on 30/05/2015.
 */
public class CalendarService {
    private Context context;

    public CalendarService(Context context) {
        this.context = context;
    }

    public void createEvent(int year, int monthOfYear, int dayOfMonth) {
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, monthOfYear, dayOfMonth, 10, 0);
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, monthOfYear, dayOfMonth, 11, 30);
        long endMillis = endTime.getTimeInMillis();
        new CreateEventTask().execute(context, startMillis, endMillis);
    }

    private class CreateEventTask extends AsyncTask<Object, Integer, Long> {

        @Override
        protected Long doInBackground(Object... params) {

            Uri uri = CalendarContract.Calendars.CONTENT_URI;
            String[] projection = new String[] {
                    CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.ACCOUNT_NAME,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                    CalendarContract.Calendars.NAME,
                    CalendarContract.Calendars.CALENDAR_COLOR
            };
            String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                    + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                    + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
            String[] selectionArgs = new String[] {"matias.d.salerno@gmail.com", "com.google",
                    "matias.d.salerno@gmail.com"};
            // Submit the query and get a Cursor object back.
            Cursor cur = null;
            ContentResolver cr = ((Context)params[0]).getContentResolver();
            cur = cr.query(uri, projection, selection, selectionArgs, null);

            cur.moveToFirst();
            long calendarId = cur.getLong(0);
            cur.close();

            // Construct event details


            // Insert Event
            ContentValues values = new ContentValues();
            TimeZone timeZone = TimeZone.getDefault();

            values.put(CalendarContract.Events.DTSTART, (Long)params[1]);
            values.put(CalendarContract.Events.DTEND, (Long)params[2]);
            values.put(CalendarContract.Events.TITLE, "Ir a buscar a Iara");
            values.put(CalendarContract.Events.DESCRIPTION, "Tengo q pasar a buscar a Iara por Asunción y Lope de Vega");
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            Uri eventUri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            return Long.parseLong(eventUri.getLastPathSegment());
        }
    }
}

package ar.com.klee.marvinSimulator.social;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Class used to interact with a calendar
 *
 * @author msalerno
 */
public class CalendarService {

    public static final long DEFAULT_CALENDAR_ID = 1L;

    private Context context;

    public CalendarService(Context context) {
        this.context = context;
    }

    public void createEvent(String event, int dayOfMonth, int monthOfYear, int year, int hour, int minute) {
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, monthOfYear-1, dayOfMonth, hour, minute);
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, monthOfYear-1, dayOfMonth, hour, minute);
        long endMillis = endTime.getTimeInMillis();
        new CreateEventTask().execute(context, startMillis, endMillis, event);
    }

    private class CreateEventTask extends AsyncTask<Object, Integer, Long> {

        @Override
        protected Long doInBackground(Object... params) {

            // Insert Event
            ContentValues values = new ContentValues();
            TimeZone timeZone = TimeZone.getDefault();

            values.put(CalendarContract.Events.DTSTART, (Long)params[1]);
            values.put(CalendarContract.Events.DTEND, (Long)params[2]);
            values.put(CalendarContract.Events.TITLE, (String)params[3]);
            values.put(CalendarContract.Events.DESCRIPTION, (String)params[3]);
            values.put(CalendarContract.Events.CALENDAR_ID, DEFAULT_CALENDAR_ID);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            Uri eventUri = context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);

            return Long.parseLong(eventUri.getLastPathSegment());
        }
    }
}
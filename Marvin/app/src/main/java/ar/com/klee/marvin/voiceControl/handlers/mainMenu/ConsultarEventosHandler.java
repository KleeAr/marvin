package ar.com.klee.marvin.voiceControl.handlers.mainMenu;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.Date;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.fragments.MainMenuFragment;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandlerContext;

public class ConsultarEventosHandler extends CommandHandler {

    private ExpressionMatcher expressionMatcher;
    private String command;
    private TTS textToSpeech;

    public ConsultarEventosHandler(TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        super(Arrays.asList("consultar eventos"), textToSpeech, context, commandHandlerManager);
    }

    @Override
    public CommandHandlerContext drive(final CommandHandlerContext currentContext) {

        ContentResolver contentResolver = getContext().getContentResolver();

        Uri.Builder builder = Uri.parse(getCalendarUriBase(getCommandHandlerManager().getMainActivity()) + "/instances/when").buildUpon();
        long now = new Date().getTime();
        ContentUris.appendId(builder, now);
        ContentUris.appendId(builder, now + 3600000);

        Cursor eventCursor = contentResolver.query(builder.build(),
                new String[] { "event_id"}, "Calendars._id=" + 1,
                null, "startDay ASC, startMinute ASC");
        while (eventCursor.moveToNext()) {
            String uid2 = eventCursor.getString(0);
            Log.v("eventID : ", uid2);

        }

        getTextToSpeech().speakText("Cerrando sesión");

        currentContext.put(STEP, 0);
        return currentContext;
    }

    @Override
    protected void addSpecificCommandContext(CommandHandlerContext commandHandlerContext) {
        // TODO
    }

    private String getCalendarUriBase(Activity act) {

        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            managedCursor = act.managedQuery(calendars, null, null, null, null);
        } catch (Exception e) {
        }
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                managedCursor = act.managedQuery(calendars, null, null, null, null);
            } catch (Exception e) {
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }
        return calendarUriBase;
    }
}

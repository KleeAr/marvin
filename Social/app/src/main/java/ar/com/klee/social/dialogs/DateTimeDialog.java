package ar.com.klee.social.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import ar.com.klee.social.R;
import ar.com.klee.social.services.CalendarService;

/**
 * Dialog used to pick the date and time of an event
 *
 * @author msalerno
 */
public class DateTimeDialog extends Dialog {

    private final DatePicker datePicker;
    private CalendarService calendarService;

    public DateTimeDialog(Context context) {
        super(context);
        this.calendarService = new CalendarService(context);
        this.setContentView(R.layout.date_time_picker);
        this.datePicker = (DatePicker)findViewById(R.id.datePicker);
// TODO - enable time picker        this.timePicker = (TimePicker)findViewById(R.id.timePicker);
        Button confirmButton = (Button) findViewById(R.id.confirmDate);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarService.createEvent(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                dismiss();
            }
        });
    }
}

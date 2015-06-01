package ar.com.klee.social.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import ar.com.klee.social.PublishActivity;
import ar.com.klee.social.R;
import ar.com.klee.social.services.exceptions.CalendarService;

/**
 * Created by Salerno on 31/05/2015.
 */
public class DateTimeDialog extends Dialog {

    private final DatePicker datePicker;
    private final TimePicker timePicker;
    private CalendarService calendarService;

    public DateTimeDialog(Context context) {
        super(context);
        this.calendarService = new CalendarService(context);
        this.setContentView(R.layout.date_time_picker);
        this.datePicker = (DatePicker)findViewById(R.id.datePicker);
        this.timePicker = (TimePicker)findViewById(R.id.timePicker);
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

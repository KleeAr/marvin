package ar.com.klee.marvin.bluetooth;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import ar.com.klee.marvin.activities.IncomingCallActivity;
import ar.com.klee.marvin.activities.NoVoiceIncomingCallActivity;
import ar.com.klee.marvin.call.CallMode;
import ar.com.klee.marvin.call.ContactUtils;
import ar.com.klee.marvin.call.PhoneCall;

/**
 * @author msalerno
 */
public class BluetoothClientCallMode extends CallMode {


    private Gson gson;

    public BluetoothClientCallMode() {
        super(NoVoiceIncomingCallActivity.class);
        gson = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm").create();
    }

    @Override
    public void onIncomingCallStarted(Context ctx, String number, Date start) {
        number = ContactUtils.getContactName(ctx, number);
        PhoneCall call = new PhoneCall(number, start);
        BluetoothService.getInstance().write(gson.toJson(call).getBytes());
    }

    @Override
    public void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    public void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    public void onMissedCall(Context ctx, String number, Date start) {
        BluetoothService.getInstance().write(BluetoothConstants.MISSED_CALL.getBytes());
        NoVoiceIncomingCallActivity.getInstance().finish();
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}

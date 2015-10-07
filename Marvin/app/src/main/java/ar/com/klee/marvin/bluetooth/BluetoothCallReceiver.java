package ar.com.klee.marvin.bluetooth;

import android.content.Context;

import com.google.gson.Gson;

import java.util.Date;

import ar.com.klee.marvin.activities.NoVoiceIncommingCallActivity;
import ar.com.klee.marvin.call.AbstractCallReceiver;
import ar.com.klee.marvin.call.ContactUtils;
import ar.com.klee.marvin.call.PhoneCall;

/**
 * @author msalerno
 */
public class BluetoothCallReceiver extends AbstractCallReceiver {

    private BluetoothService bluetoothService;
    private Gson gson = new Gson();

    public BluetoothCallReceiver(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        number = ContactUtils.getContactName(ctx, number);
        PhoneCall call = new PhoneCall(number, start);
        bluetoothService.write(gson.toJson(call).getBytes());
        startIncomingCallActivity(ctx, number, start, NoVoiceIncommingCallActivity.class);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {

    }
}

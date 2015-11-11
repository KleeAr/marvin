package ar.com.klee.marvin.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;

import ar.com.klee.marvin.bluetooth.BluetoothConstants;
import ar.com.klee.marvin.bluetooth.BluetoothHandler;
import ar.com.klee.marvin.bluetooth.BluetoothService;
import ar.com.klee.marvin.voiceControl.STTService;

/**
 * @author msalerno
 */
public class BluetoothIncomingCallActivity extends IncomingCallActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        BluetoothService.getInstance().setmHandler(new BluetoothHandler(this) {
            @Override
            protected void onMessageRead(String readMessage) {
                if(BluetoothConstants.MISSED_CALL.equals(readMessage)) {
                    onMissedCall();
                }
            }

            @Override
            protected void onMessageWrote(String writeMessage) {

            }
        });
    }

    private void onMissedCall() {
        isRejected = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                instance = null;
                commandHandlerManager.setNullCommand();
                STTService.getInstance().setIsListening(false);
                commandHandlerManager.defineActivity(previousActivityId, previousActivity);
                wm.removeView(mTopView);
                Log.d("CALL", "reject");
                finish(); //Regresa a la activity anterior
            }
        }, 1000);
    }

    @Override
    protected void onAcceptCall() {
        BluetoothService.getInstance().write(BluetoothConstants.ACCEPT_CALL.getBytes());
    }

    @Override
    protected void onRejectCall() {
        BluetoothService.getInstance().write(BluetoothConstants.REJECT_CALL.getBytes());
    }


    @Override
    protected String getTag() {
        return "BluetoothIncomingCallActivity";
    }
}

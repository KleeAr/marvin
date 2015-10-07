package ar.com.klee.marvin.activities;

import ar.com.klee.marvin.bluetooth.BluetoothService;
import ar.com.klee.marvin.bluetooth.BluetoothConstants;

/**
 * @author msalerno
 */
public class BluetoothIncomingCallActivity extends IncomingCallActivity {

    @Override
    public void acceptCall() {
        BluetoothService.getInstance().write(BluetoothConstants.ACCEPT_CALL.getBytes());
    }

    @Override
    public void rejectCall() {
        BluetoothService.getInstance().write(BluetoothConstants.REJECT_CALL.getBytes());
        finish();
    }
}

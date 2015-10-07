package ar.com.klee.marvin.bluetooth;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * @author msalerno
 */
public abstract class BluetoothHandler extends Handler {

    protected Context context;
    private String mConnectedDeviceName;

    public  BluetoothHandler(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        Context activity = context;
        switch (msg.what) {
            case BluetoothConstants.MESSAGE_STATE_CHANGE:

                switch (msg.arg1) {
                    case BluetoothService.STATE_CONNECTED:
                        break;
                    case BluetoothService.STATE_CONNECTING:
                        break;
                    case BluetoothService.STATE_LISTEN:
                    case BluetoothService.STATE_NONE:
                        break;
                }
                break;
            case BluetoothConstants.MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                onMessageWrote(writeMessage);
                break;
            case BluetoothConstants.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                onMessageRead(readMessage);
                break;
            case BluetoothConstants.MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(BluetoothConstants.DEVICE_NAME);
                if (null != activity) {
                    Toast.makeText(activity, "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                }
                break;
            case BluetoothConstants.MESSAGE_TOAST:
                if (null != activity) {
                    Toast.makeText(activity, msg.getData().getString(BluetoothConstants.TOAST),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Handle message read
     * @param readMessage
     */
    protected abstract void onMessageRead(String readMessage);

    /**
     * Handle message wrote
     * @param writeMessage
     */
    protected abstract void onMessageWrote(String writeMessage);

}

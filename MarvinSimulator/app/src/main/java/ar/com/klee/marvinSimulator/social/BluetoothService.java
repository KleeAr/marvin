package ar.com.klee.marvinSimulator.social;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ar.com.klee.marvinSimulator.activities.BluetoothActivity;
import ar.com.klee.marvinSimulator.social.adapters.BluetoothDevicesListAdapter;

/**
 * @author msalerno
 */
public class BluetoothService {

    private static final int REQUEST_ENABLE_BT = 3000;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothActivity activity;
    private BluetoothHeadset headset;


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothDevicesListAdapter listAdapter = (BluetoothDevicesListAdapter) activity.getListAdapter();
                listAdapter.add(device);
                listAdapter.notifyDataSetChanged();
            }
        }
    };

    public BluetoothService(BluetoothActivity activity) {
        this.activity = activity;
        activity.unregisterReceiver(broadcastReceiver);
    }

    public void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void discoverDevices() {
        bluetoothAdapter.startDiscovery();
    }

    public void stopDiscovering() {
        bluetoothAdapter.cancelDiscovery();
    }

    public void connectToDevice(BluetoothDevice device) {

    }

    public void dial(String dial) {
        BluetoothDevice device;

    }
}

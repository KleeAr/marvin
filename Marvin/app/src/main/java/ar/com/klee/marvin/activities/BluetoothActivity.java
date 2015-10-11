package ar.com.klee.marvin.activities;

import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.social.BluetoothService;
import ar.com.klee.marvin.social.adapters.BluetoothDevicesListAdapter;

public class BluetoothActivity extends ListActivity {

    private BluetoothService bluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        setListAdapter(new BluetoothDevicesListAdapter(this, new ArrayList<BluetoothDevice>()));
        bluetoothService = new BluetoothService(this);
        bluetoothService.discoverDevices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

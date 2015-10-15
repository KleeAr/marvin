package ar.com.klee.marvinSimulator.social.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ar.com.klee.marvinSimulator.R;

/**
 * @author msalerno
 */
public class BluetoothDevicesListAdapter extends ArrayAdapter<BluetoothDevice> {


    public BluetoothDevicesListAdapter(Context context, List<BluetoothDevice> objects) {
        super(context, R.layout.bluetooth_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.bluetooth_item, null);
        }
        BluetoothDevice device = getItem(position);
        TextView address = (TextView) view.findViewById(R.id.address);
        address.setText(device.getAddress());
        return view;
    }
}

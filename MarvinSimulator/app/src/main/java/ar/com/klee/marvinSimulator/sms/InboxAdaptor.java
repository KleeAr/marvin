package ar.com.klee.marvinSimulator.sms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Clase para adaptar los items de la lista de mensajes Inbox
 */
public class InboxAdaptor extends BaseAdapter {

    private List<Mensaje> data;
    private Context context;

    public InboxAdaptor(Context context, List<Mensaje> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount(){
        return data.size();
    }

    @Override
    public Object getItem(int position){
        return data.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;

        if(view == null){
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(ar.com.klee.marvinSimulator.R.layout.row_sms_inbox, null);
        }

        TextView contact = (TextView) view.findViewById(ar.com.klee.marvinSimulator.R.id.textContact);
        TextView date = (TextView) view.findViewById(ar.com.klee.marvinSimulator.R.id.textDate);
        TextView mensaje = (TextView) view.findViewById(ar.com.klee.marvinSimulator.R.id.textSMS);

        final Mensaje item = data.get(position);
        contact.setText(item.getContactName());
        mensaje.setText(item.getBodyMessage());
        date.setText(item.getDate());

        return view;
    }

}

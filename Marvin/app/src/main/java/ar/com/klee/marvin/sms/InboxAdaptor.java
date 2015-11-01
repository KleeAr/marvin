package ar.com.klee.marvin.sms;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ar.com.klee.marvin.R;

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
            view = vi.inflate(R.layout.row_sms_inbox, null);
        }

        Typeface fBariolBold = Typeface.createFromAsset(context.getAssets(), "Bariol_Bold.otf");
        Typeface fBariolRegular = Typeface.createFromAsset(context.getAssets(), "Bariol_Regular.otf");

        TextView contact = (TextView) view.findViewById(R.id.textContact);
        TextView date = (TextView) view.findViewById(R.id.textDate);
        TextView mensaje = (TextView) view.findViewById(R.id.textSMS);

        final Mensaje item = data.get(position);
        contact.setText(item.getContactName());
        contact.setTypeface(fBariolBold);
        mensaje.setText(item.getBodyMessage());
        mensaje.setTypeface(fBariolRegular);
        date.setText(item.getDate());
        date.setTypeface(fBariolRegular);

        return view;
    }

}

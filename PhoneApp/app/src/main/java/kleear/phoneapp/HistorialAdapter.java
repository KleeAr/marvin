package kleear.phoneapp;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

/*
* Clase utilizada para adaptar los items de la lista de mensajes entrantes
 */

public class HistorialAdapter extends ArrayAdapter<Call> {

    private Activity activity;
    private List<Call> llamadas;
    private int row;
    private Call objCall;

    public HistorialAdapter(Activity act, int row, List<Call> items) {
        super(act, row, items);

        this.activity = act;
        this.row = row;
        this.llamadas = items;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(row, null);

            holder = new ViewHolder();
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if ((llamadas == null) || ((position + 1) > llamadas.size()))
            return view;

        objCall = llamadas.get(position);

       holder.numberPhone = (TextView) view.findViewById(R.id.numberPhone);
       holder.type = (TextView) view.findViewById(R.id.type);
       holder.date = (TextView) view.findViewById(R.id.date);
       holder.duration = (TextView) view.findViewById(R.id.duration);


        if (holder.numberPhone != null && null != objCall.getNumberPhone()&& objCall.getNumberPhone().trim().length() > 0) {
            holder.numberPhone.setText(Html.fromHtml(objCall.getNumberPhone()));
        }
        if (holder.type != null && null != objCall.getType()&& objCall.getType().trim().length() > 0) {
            holder.type.setText(Html.fromHtml(objCall.getType()));
        }
        if (holder.date != null && null != objCall.getDate()) {
            String stringDate = DateFormat.getDateTimeInstance().format(objCall.getDate());
            holder.date.setText(Html.fromHtml(stringDate));
        }
        if (holder.duration != null && null != objCall.getDuration()&& objCall.getDuration().trim().length() > 0) {
            int num,hor,min,seg;
            num=Integer.parseInt(objCall.getDuration());
            hor=num/3600;
            min=(num-(3600*hor))/60;
            seg=num-((hor*3600)+(min*60));
            String msg= min +" min "+ seg + " seg.";
            holder.duration.setText(Html.fromHtml(msg));
        }
        return view;
    }

    public class ViewHolder {
        public TextView numberPhone, date, type, duration;
    }
}


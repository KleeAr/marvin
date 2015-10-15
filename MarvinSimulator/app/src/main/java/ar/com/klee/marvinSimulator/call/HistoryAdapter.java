package ar.com.klee.marvinSimulator.call;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ar.com.klee.marvinSimulator.R;


/*
* Clase utilizada para adaptar los items de la lista de calls
 */
public class HistoryAdapter extends ArrayAdapter<Call> {
    private Activity activity;
    private List<Call> calls;
    private int row;
    private Call objCall;

    public HistoryAdapter(Activity act, int row, List<Call> items) {
        super(act, row, items);

        this.activity = act;
        this.row = row;
        this.calls = items;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(row, null);

            holder = new ViewHolder();
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if ((calls == null) || ((position + 1) > calls.size()))
            return view;

        objCall = calls.get(position);


        holder.numberPhone = (TextView) view.findViewById(R.id.numberPhone);
        holder.type = (TextView) view.findViewById(R.id.type);
        holder.date = (TextView) view.findViewById(R.id.date);
        holder.duration = (TextView) view.findViewById(R.id.duration);

        if (holder.numberPhone != null && null != objCall.getContactName() && objCall.getContactName().trim().length() > 0) {
            holder.numberPhone.setText(Html.fromHtml(objCall.getContactName()));
        }
        if (holder.type != null && null != objCall.getType()&& objCall.getType().trim().length() > 0) {
            holder.type.setText(Html.fromHtml(objCall.getType()));
        }
        if (holder.date != null && null != objCall.getDate()) {
            holder.date.setText(Html.fromHtml(objCall.getDate()));
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

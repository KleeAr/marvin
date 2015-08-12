package ar.com.klee.marvin.gps;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.call.Call;


public class TripAdapter extends ArrayAdapter<Trip> {
    private Activity activity;
    private List<Trip> trips;
    private int row;
    private Trip objTrip;

    public TripAdapter(Activity act, int row, List<Trip> items) {
        super(act, row, items);

        this.activity = act;
        this.row = row;
        this.trips = items;

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

        if ((trips == null) || ((position + 1) > trips.size()))
            return view;

        objTrip = trips.get(position);

        holder.beginningAddress = (TextView) view.findViewById(R.id.beginningAddress);
        holder.endingAddress = (TextView) view.findViewById(R.id.endingAddress);
        holder.beginningTime = (TextView) view.findViewById(R.id.beginningTime);
        holder.endingTime = (TextView) view.findViewById(R.id.endingTime);
        holder.distance = (TextView) view.findViewById(R.id.distance);
        holder.velocity = (TextView) view.findViewById(R.id.velocity);
        holder.time = (TextView) view.findViewById(R.id.time);

        holder.beginningAddress.setText(Html.fromHtml("Partida: " + objTrip.getBeginningAddress()));
        holder.endingAddress.setText(Html.fromHtml("Llegada: " + objTrip.getEndingAddress()));

        String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(objTrip.getStartTime());

        holder.beginningTime.setText(Html.fromHtml(formattedDate));

        formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(objTrip.getFinishTime());

        holder.beginningTime.setText(Html.fromHtml(formattedDate));

        holder.time.setText(Html.fromHtml(objTrip.getTime()));

        holder.distance.setText(Html.fromHtml(objTrip.getDistance()));

        holder.velocity.setText(Html.fromHtml(objTrip.getAverageVelocity()));

        return view;
    }

    public class ViewHolder {
        public TextView beginningAddress,endingAddress,beginningTime,endingTime,velocity,time,distance;
    }

}

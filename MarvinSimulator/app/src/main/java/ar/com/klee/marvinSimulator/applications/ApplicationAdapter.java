package ar.com.klee.marvinSimulator.applications;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import ar.com.klee.marvinSimulator.R;


public class ApplicationAdapter extends BaseAdapter {

        private List<Application> data;
        private Context context;

        public ApplicationAdapter(Context context, List<Application> data){
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
                view = vi.inflate(R.layout.custom_row_layout, null);
            }

            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            TextView text = (TextView) view.findViewById(R.id.text);

            final Application item = data.get(position);
            text.setText(item.name);
            icon.setImageDrawable(item.icon);

            return view;
        }
}
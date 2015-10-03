package ar.com.klee.marvin.activities;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.klee.marvin.R;

public class ListviewCategoriesAdapter extends BaseAdapter {
    private static ArrayList<String> listCategories;

    private LayoutInflater mInflater;

    public ListviewCategoriesAdapter(Context context, ArrayList<String> results){
        listCategories = results;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listCategories.size();
    }

    @Override
    public Object getItem(int arg0) {
        return listCategories.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.configure_list_item, null);
            holder = new ViewHolder();
            holder.txtCategorie = (TextView) convertView.findViewById(R.id.label);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtCategorie.setText(listCategories.get(position).toString());

        return convertView;
    }

    static class ViewHolder{
        TextView txtCategorie;
    }
}
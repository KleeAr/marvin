package ar.com.klee.marvin;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import ar.com.klee.marvin.fragments.MainMenuFragment;


public class DrawerMenuAdapter extends BaseAdapter{

    List<DrawerMenuItem> mItems;
    Context mContext;
    LayoutInflater mInflater;

    public DrawerMenuAdapter(Context context,List<DrawerMenuItem> mItems) {
        this.mItems = mItems;
        this.mContext = context;
    }



    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            mInflater = (LayoutInflater)mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        }

        if(position==2||position==6) {
            convertView = mInflater.inflate(R.layout.drawer_group_menu_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.header);
            DrawerMenuItem item = mItems.get(position);
            textView.setText(item.getText());
            convertView.setEnabled(false);
            convertView.setOnClickListener(null);
        }
        else{
                convertView = mInflater.inflate(R.layout.drawer_menu_item, null);
                ImageView imgIcon = (ImageView) convertView.findViewById(R.id.img_drawer_menu_item_icon);
                TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_drawer_menu_item_text);

                DrawerMenuItem item = mItems.get(position);

                imgIcon.setImageResource(item.getIcon());
                tvTitle.setText(item.getText());
        }


        return convertView;
    }
}
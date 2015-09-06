package ar.com.klee.marvin;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.List;

import ar.com.klee.marvin.activities.SiteActivity;
import ar.com.klee.marvin.activities.TripActivity;
import ar.com.klee.marvin.gps.Site;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>  {

    List<Site> mItems;
    private static CardAdapter instance;
    private Site chosenSite;

    public CardAdapter(List<Site> mItems) {
        super();

        instance = this;

        this.mItems = mItems;
        for(int i=0;i<mItems.size();i++) {
            Site site = mItems.get(i);
            if(site.getSiteThumbnail()==0)
                site.setSiteThumbnail(R.drawable.sample);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.site_recycler_view_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Site site = mItems.get(i);

        viewHolder.tvSite.setText(site.getSiteName() + " - " + site.getSiteAddress());
        if(site.getSiteThumbnail() == 0 || site.getSiteThumbnail() == R.drawable.sample)
            viewHolder.imgThumbnail.setImageResource(site.getSiteThumbnail());
        else {
            File imgFile = new  File("/sdcard/MARVIN/Sitios/" + site.getSiteName() + ".png");
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                viewHolder.imgThumbnail.setImageBitmap(myBitmap);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgThumbnail;
        public TextView tvSite;

        public ViewHolder(final View itemView) {
            super(itemView);
            imgThumbnail = (ImageView)itemView.findViewById(R.id.img_thumbnail);
            tvSite = (TextView)itemView.findViewById(R.id.tv_site);

            tvSite.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    chosenSite = mItems.get(getPosition());
                    CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();
                    Intent intent = new Intent(commandHandlerManager.getContext(), SiteActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    commandHandlerManager.getContext().startActivity(intent);

                }
            });

        }
    }

    public static CardAdapter getInstance(){
        return instance;
    }

    public Site getChosenSite() {
        return chosenSite;
    }
}
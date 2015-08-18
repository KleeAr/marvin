package ar.com.klee.marvin;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>  {

    List<Site> mItems;
    String siteName, siteAddress;

    public CardAdapter(List<Site> mItems, String siteName, String siteAddress) {
        super();

        this.mItems = mItems;
        Site site = new Site();
        site.setSiteName(siteName);
        site.setSiteThumbnail(R.drawable.sample);
        mItems.add(site);
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
        viewHolder.tvSite.setText(site.getSiteName());
        viewHolder.imgThumbnail.setImageResource(site.getSiteThumbnail());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgThumbnail;
        public TextView tvSite;
        public ImageButton ibShared;

        public ViewHolder(final View itemView) {
            super(itemView);
            imgThumbnail = (ImageView)itemView.findViewById(R.id.img_thumbnail);
            tvSite = (TextView)itemView.findViewById(R.id.tv_site);
            ibShared = (ImageButton)itemView.findViewById(R.id.ib_Shared);

            tvSite.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), " posicion " + getPosition(), Toast.LENGTH_SHORT).show();
                    //Link a una nueva pantalla que muestre el lugar marcado en un mapa
                }
            });

            ibShared.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Compartir", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

}
package ar.com.klee.marvinSimulator.multimedia.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ar.com.klee.marvinSimulator.multimedia.video.YouTubeVideo;

/**
 * @author msalerno
 */
public class YouTubeListAdapter extends ArrayAdapter<YouTubeVideo> {

    public YouTubeListAdapter(Context context, int resource, List<YouTubeVideo> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(ar.com.klee.marvinSimulator.R.layout.you_tube_video_item, null);
        }
        YouTubeVideo youTubeVideo = getItem(position);
        TextView videoTitle = (TextView) view.findViewById(ar.com.klee.marvinSimulator.R.id.video_title);
        videoTitle.setText(youTubeVideo.getTitle());
        TextView videoDesc = (TextView) view.findViewById(ar.com.klee.marvinSimulator.R.id.video_desc);
        videoDesc.setText(youTubeVideo.getDescription());
        ImageView imageView = (ImageView) view.findViewById(ar.com.klee.marvinSimulator.R.id.video_image);
        Picasso.with(getContext()).load(youTubeVideo.getThumbnailUrl()).placeholder(ar.com.klee.marvinSimulator.R.drawable.video_place_holder).into(imageView);
        return view;
    }

}

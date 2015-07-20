package ar.com.klee.marvin.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.multimedia.video.YouTubeVideo;
import ar.com.klee.marvin.multimedia.video.adapter.YouTubeListAdapter;

public class SearchResultActivity extends ListActivity implements AdapterView.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        List<YouTubeVideo> videos = getIntent().getExtras().getParcelableArrayList("videos");
        YouTubeListAdapter adapter = new YouTubeListAdapter(this,R.layout.you_tube_video_item,videos);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        YouTubeVideo video = (YouTubeVideo) getListAdapter().getItem(position);
        Intent intent = new Intent(this, YouTubePlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("video",video);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

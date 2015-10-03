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
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;

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

    public void onBackPressed(){
        CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();
        MainMenuActivity mainMenuActivity = ((MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity());
        mainMenuActivity.activate(commandHandlerManager.getmSpeechRecognizer(), commandHandlerManager.getmSpeechRecognizerIntent());
        commandHandlerManager.setNullCommand();
        STTService.getInstance().setIsListening(false);
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_MAIN, commandHandlerManager.getMainActivity());
        this.finish();
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

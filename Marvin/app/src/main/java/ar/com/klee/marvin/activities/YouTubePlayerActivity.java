package ar.com.klee.marvin.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.configuration.UserConfig;
import ar.com.klee.marvin.multimedia.video.YouTubeService;
import ar.com.klee.marvin.multimedia.video.YouTubeVideo;
import ar.com.klee.marvin.voiceControl.STTService;


public class YouTubePlayerActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    private YouTubeVideo video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(UserConfig.getInstance().getOrientation() == UserConfig.ORIENTATION_PORTRAIT)
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        setContentView(R.layout.activity_you_tube_player);

        STTService.getInstance().stopListening();

        this.video = getIntent().getExtras().getParcelable("video");
        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(YouTubeService.API_KEY, this);

        //definimos los tipos de letra
        Typeface fBariolBold = Typeface.createFromAsset(this.getAssets(), "Bariol_Bold.otf");
        Typeface fBariolRegular = Typeface.createFromAsset(this.getAssets(), "Bariol_Regular.otf");

        TextView titleVideo = (TextView)findViewById(R.id.title);
        TextView titleDesc = (TextView)findViewById(R.id.video_desc);

        titleVideo.setText(video.getTitle());
        titleVideo.setTypeface(fBariolBold);
        titleDesc.setText(video.getDescription());
        titleVideo.setTypeface(fBariolRegular);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_you_tube_player, menu);
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

    private static final int RECOVERY_DIALOG_REQUEST = 1;



    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {

        if (errorReason.isUserRecoverableError()) {

            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();

        } else {
            Toast.makeText(this, "error reproducing youtube video.", Toast.LENGTH_LONG).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {

            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(YouTubeService.API_KEY, this);

        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        if (!wasRestored) {
            player.loadVideo(video.getId());
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

}

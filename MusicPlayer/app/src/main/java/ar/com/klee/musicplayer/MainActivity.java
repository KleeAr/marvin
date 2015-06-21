package ar.com.klee.musicplayer;

import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    private static final String MEDIA_PATH = new String("/sdcard/Music/");
    private List<HashMap<String,String>> songs = new ArrayList<HashMap<String,String>>();
    private MediaPlayer mp = new MediaPlayer();
    private int currentSong = 0;
    private int currentDuration = 0;
    private int previousSong = 0;
    private boolean isRandom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateSongList(MEDIA_PATH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void updateSongList(String path) {
        File home = new File(path);
        if (home.listFiles(new Mp3Filter()).length > 0) {
            for (File file : home.listFiles(new Mp3Filter())) {
                if(file.isDirectory()){
                    updateSongList(file.getPath());
                }
                else {
                    songs.add(new HashMap<String, String>());
                    songs.get(songs.size()-1).put("Path",file.getPath());
                    loadFileData();
                }
            }
        }
    }

    public void loadFileData(){

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(songs.get(songs.size()-1).get("Path"));

        songs.get(songs.size()-1).put("Title", getSongData((songs.get(songs.size()-1).get("Path")),"Title"));
        songs.get(songs.size()-1).put("Artist", getSongData((songs.get(songs.size() - 1).get("Path")), "Artist"));

        Log.d("MSC",songs.get(songs.size()-1).get("Path"));
        if(songs.get(songs.size()-1).get("Title") != null)
            Log.d("MSC",songs.get(songs.size()-1).get("Title"));
        if(songs.get(songs.size()-1).get("Artist") != null)
            Log.d("MSC",songs.get(songs.size()-1).get("Artist"));

    }

    class Mp3Filter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            File path = new File(dir.getPath()+"/"+name);
            if(path.isDirectory())
                return true;
            return (name.endsWith(".mp3"));
        }
    }

    public void playSong(String songPath) {
        try {

            mp.reset();
            mp.setDataSource(songPath);
            mp.prepare();
            mp.seekTo(currentDuration);
            mp.start();

            // Setup listener so next song starts automatically
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer arg0) {
                    nextSong();
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPlaying(View view){

        if(songs.size() == 0) {
            //NO HAY CANCIONES
        }
        Integer i = songs.size();

        playSong(songs.get(currentSong).get("Path"));

    }

    public void startPlaying(){

        if(songs.size() == 0) {
            //NO HAY CANCIONES
        }

        playSong(songs.get(currentSong).get("Path"));

    }

    public void pause(View view){

        if(mp.isPlaying()) {
            currentDuration = mp.getCurrentPosition();
            mp.pause();
        }
    }

    public void pause(){

        if(mp.isPlaying())
            mp.pause();

    }

    public void nextSong(View view) {

        if(songs.size() == 0) {
            //NO HAY CANCIONES
        }

        currentDuration = 0;
        previousSong = currentSong;
        if(isRandom) {
            Random rn = new Random();
            currentSong = rn.nextInt() % songs.size();
        }else {
            currentSong++;
            if (currentSong == songs.size())
                currentSong = 0;
        }
        playSong(songs.get(currentSong).get("Path"));
    }

    public void nextSong() {

        if(songs.size() == 0) {
            //NO HAY CANCIONES
        }

        currentDuration = 0;
        previousSong = currentSong;
        if(isRandom) {
            Random rn = new Random();
            currentSong = rn.nextInt() % songs.size();
        }else {
            currentSong++;
            if (currentSong == songs.size())
                currentSong = 0;
        }
        playSong(songs.get(currentSong).get("Path"));
    }

    public void previousSong(View view) {

        if(songs.size() == 0) {
            //NO HAY CANCIONES
        }

        currentDuration = 0;
        if(currentSong == previousSong || !isRandom) {
            currentSong--;
            if(currentSong == -1)
                currentSong = songs.size()-1;
        }else
            currentSong = previousSong;
        playSong(songs.get(currentSong).get("Path"));
    }

    public void previousSong() {

        if(songs.size() == 0) {
            //NO HAY CANCIONES
        }

        currentDuration = 0;
        if(currentSong == previousSong || !isRandom) {
            currentSong--;
            if(currentSong == -1)
                currentSong = songs.size()-1;
        }else
            currentSong = previousSong;
        playSong(songs.get(currentSong).get("Path"));
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public String getSongData(String path, String data) {

        File src = new File(path);
        MusicMetadataSet src_set = null;
        try {
            //src_set = new MyID3().read(src);
            Log.d("MSC",path);
            MyID3 id3 = new MyID3();
            src_set = id3.read(src);
        } catch (Exception e1) {
            e1.printStackTrace();
        } // read metadata

        if (src_set != null) {
            try {
                IMusicMetadata metadata = src_set.getSimplified();
                if(data.equals("Artist"))
                    return metadata.getArtist();
                if(data.equals("Title"))
                    return metadata.getSongTitle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}


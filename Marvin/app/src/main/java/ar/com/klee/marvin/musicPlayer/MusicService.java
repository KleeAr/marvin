package ar.com.klee.marvin.musicPlayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {

    private static final String MEDIA_PATH = new String("/sdcard/Music/");
    private List<HashMap<String,String>> songs = new ArrayList<HashMap<String,String>>();
    private MediaPlayer mp = new MediaPlayer();
    private int currentSong = 0;
    private int currentDuration = 0;
    private int previousSong = 0;
    private boolean isRandom = false;
    private boolean isPlaying = false;

    LocalBroadcastManager broadcaster;
    public static final String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";
    public static final String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";

    public void onCreate(){
        super.onCreate();

        updateSongList(MEDIA_PATH);

        broadcaster = LocalBroadcastManager.getInstance(this);
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

    public void startPlaying(){

        if(songs.size() == 0) {
            //NO HAY CANCIONES
        }

        playSong(songs.get(currentSong).get("Path"));

    }

    public void pause(){

        if(mp.isPlaying())
            mp.pause();

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

    public String getTitle(){

        return songs.get(currentSong).get("Title");

    }

    public String getArtist(){

        return songs.get(currentSong).get("Artist");

    }

    public int findTitle(String title){

        int i = 0;

        title = title.toLowerCase();

        while(i < songs.size()){

            if(songs.get(i).get("Title").toLowerCase().equals(title))
                return i;

            i++;

        }

        return -1;

    }

    public int findArtist(String artist){

        int i = 0;

        artist = artist.toLowerCase();

        while(i < songs.size()){

            if(songs.get(i).get("Artist").toLowerCase().equals(artist))
                return i;

            i++;

        }

        return -1;

    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new MusicBinder();

}

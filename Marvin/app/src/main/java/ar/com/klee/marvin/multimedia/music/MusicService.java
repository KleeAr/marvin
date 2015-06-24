package ar.com.klee.marvin.multimedia.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

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
import java.util.Stack;

public class MusicService extends Service {

    private static final String MEDIA_PATH = new String("/sdcard/Music/");
    private List<HashMap<String,String>> songs = new ArrayList<HashMap<String,String>>();
    private MediaPlayer mp = new MediaPlayer();
    private int currentSong = 0;
    private int currentDuration = 0;
    private Stack previousSongs = new Stack();
    private boolean isRandom = false;

    LocalBroadcastManager broadcaster;
    public static final String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";
    public static final String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";

    public void onCreate(){
        super.onCreate();

        updateSongList(MEDIA_PATH);

        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    public void onStop(){

        if(mp.isPlaying()) {
            mp.stop();
        }
        mp.release();
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

            sendResult("SONG_TITLE " + songs.get(currentSong).get("Title"));
            sendResult("SONG_ARTIST " + songs.get(currentSong).get("Artist"));

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

        if(mp.isPlaying()) {
            currentDuration = mp.getCurrentPosition();
            mp.pause();
        }

        sendResult("SONG_TITLE " + " ");
        sendResult("SONG_ARTIST " + " ");

    }

    public void nextSong() {

        if(songs.size() == 0) {
            //NO HAY CANCIONES
        }

        currentDuration = 0;
        previousSongs.push(currentSong);
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

    public void nextSongSet() {

        if(songs.size() == 0) {
            //NO HAY CANCIONES
        }

        currentDuration = 0;
        previousSongs.push(currentSong);
        if(isRandom) {
            Random rn = new Random();
            currentSong = rn.nextInt() % songs.size();
        }else {
            currentSong++;
            if (currentSong == songs.size())
                currentSong = 0;
        }
    }

    public void previousSong() {

        if(songs.size() == 0) {
            //NO HAY CANCIONES
        }

        currentDuration = 0;
        if(!previousSongs.empty())
            currentSong = (int) previousSongs.pop();
        else {
            currentSong--;
            if(currentSong == -1)
                currentSong = songs.size()-1;
        }

        playSong(songs.get(currentSong).get("Path"));
    }

    public void previousSongSet() {

        if(songs.size() == 0) {
            //NO HAY CANCIONES
        }

        currentDuration = 0;
        if(!previousSongs.empty())
            currentSong = (int) previousSongs.pop();
        else {
            currentSong--;
            if(currentSong == -1)
                currentSong = songs.size()-1;
        }
    }

    public boolean setRandom(boolean random) {

        if((isRandom && random)||(!isRandom && !random))
            return false;

        isRandom = random;

        return true;
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

    public boolean findTitle(String title){

        int marker = currentSong + 1;

        title = title.toLowerCase();

        while(marker < songs.size()){

            if(songs.get(marker).get("Title").toLowerCase().equals(title)){

                currentSong = marker;
                currentDuration = 0;

                return true;
            }

            marker++;

        }

        marker = 0;

        while(marker <= currentSong){

            if(songs.get(marker).get("Title").toLowerCase().equals(title)){

                currentSong = marker;
                currentDuration = 0;

                return true;
            }

            marker++;

        }


        return false;

    }

    public boolean findArtist(String artist){

        int marker = currentSong + 1;

        artist = artist.toLowerCase();

        while(marker < songs.size()){

            if(songs.get(marker).get("Artist").toLowerCase().equals(artist)){

                currentSong = marker;
                currentDuration = 0;

                return true;
            }

            marker++;

        }

        marker = 0;

        while(marker <= currentSong){

            if(songs.get(marker).get("Artist").toLowerCase().equals(artist)){

                currentSong = marker;
                currentDuration = 0;

                return true;
            }

            marker++;

        }


        return false;

    }

    public boolean isPlaying(){

        return mp.isPlaying();

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

    public void sendResult(String message) {
        Intent intent = new Intent(COPA_RESULT);
        if(message != null)
            intent.putExtra(COPA_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

}

package ar.com.klee.marvin.multimedia.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.fragments.MainMenuFragment;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;
import wseemann.media.FFmpegMediaPlayer;

public class MusicService extends Service {

    private static final String MEDIA_PATH = new String("/sdcard/Music/");
    private List<HashMap<String,String>> songs = new ArrayList<HashMap<String,String>>();
    private List<Radio> radios;
    private MediaPlayer mpMusic = new MediaPlayer();
    private FFmpegMediaPlayer mpRadio = new FFmpegMediaPlayer();
    private int currentSong = 0;
    private int currentDuration = 0;
    private int currentRadio = 0;
    private Stack previousSongs = new Stack();
    private boolean isRandom = false;
    private boolean isRadio = false;

    LocalBroadcastManager broadcaster;
    public static final String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";
    public static final String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";

    private Thread isAlive;

    private static MusicService instance;

    public void onCreate(){
        super.onCreate();

        instance = this;

        mpMusic.setVolume((float) 0.5, (float) 0.5);
        mpRadio.setVolume((float) 0.5, (float) 0.5);

        mpRadio.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
            public void onPrepared(FFmpegMediaPlayer mp) {
                mp.start();
            }
        });

        updateSongList(MEDIA_PATH);

        initializeRadioList();

        getSharedPreferences();

        broadcaster = LocalBroadcastManager.getInstance(this);

        isAlive = new Thread() {

            @Override
            public void run() {
                try {

                    Thread.sleep(5000);

                    while (!isInterrupted()) {

                        try{
                            STTService.getInstance();
                        }catch (IllegalStateException e) {
                            ((MainMenuActivity)CommandHandlerManager.getInstance().getMainActivity()).initializeSTTService();
                        }

                        Thread.sleep(5000);

                    }
                } catch (InterruptedException e) {
                } catch (NullPointerException e) {
                }
            }
        };
        isAlive.start();
    }

    public static MusicService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getInstance");
        }
        return instance;
    }

    public void onStop(){

        isAlive.interrupt();

        instance = null;

        SharedPreferences sharedPreferences = getSharedPreferences("musicService",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(!songs.isEmpty()) {
            editor.putString("song", songs.get(currentSong).get("Title"));
            editor.putString("artist", songs.get(currentSong).get("Artist"));
            if(!isRadio)
                currentDuration = mpMusic.getCurrentPosition();
            editor.putInt("duration", currentDuration);
            editor.putInt("position", currentSong);
            editor.putBoolean("isRandom", isRandom);
        }
        editor.putInt("radio",currentRadio);
        editor.putBoolean("isRadio",isRadio);
        editor.commit();

        if(mpMusic.isPlaying()) {
            mpMusic.stop();
        }
        mpMusic.release();

        if(mpRadio.isPlaying()) {
            mpRadio.stop();
        }
        mpRadio.release();
    }

    public void initializeRadioList(){
        radios = Arrays.asList(
                new Radio("AM 570","ARGENTINA","http://am570.prodera.com.ar:8000"),
                new Radio("AM 590","CONTINENTAL","http://7309.live.streamtheworld.com/CONTINENTAL_SC?MSWMExt=.mp3 "),
                new Radio("AM 630","RIVADAVIA","http://200.110.145.2:8234/live?type=.mp3"),
                new Radio("AM 710","RADIO 10","rtmp://server1.stweb.tv/radio10//live"),
                new Radio("AM 790","MITRE","http://buecrplb01.cienradios.com.ar/Mitre790.aac"),
                new Radio("AM 910","LA RED","http://lsdlrhls-lh.akamaihd.net/i/laredHLS_1@59923/index_1_a-p.m3u8?sd=10&rebase=on"),
                new Radio("AM 1020","LT10","http://184.107.240.26:8555/vivo"),
                new Radio("FM 88.3","R80","http://ded35.server54.net:8010/radio_r80"),
                new Radio("FM 88.5","VIDA","http://50.7.181.186:8004/ "),
                new Radio("FM 89.1","MALENA","http://vivo.radioam750.com.ar/vivofm.mp3? "),
                new Radio("FM 89.9","CON VOS","http://vivo.radioconvos.com.ar/stream?MSWMExt=.mp3"),
                new Radio("FM 90.3","FM DELTA","http://2283.live.streamtheworld.com/DELTA_903_SC?"),
                new Radio("FM 93.1","FM LATE","http://streaming.radiolinksmedia.com:8252"),
                new Radio("FM 95.1","METRO","http://mp3.metroaudio1.stream.avstreaming.net:7200/metro?MSWMExt=.mp3"),
                new Radio("FM 95.5","IMAGINARIA","http://200.58.118.108:8023/stream?type=.flv"),
                new Radio("FM 97.9","RADIO CULTURA","http://1351.live.streamtheworld.com/RADIOCULTURA_SC?MSWMExt=.mp3 "),
                new Radio("FM 98.3","MEGA","rtmp://mega983.stweb.tv/mega983//live"),
                new Radio("FM 99.9","LA 100","http://buecrplb01.cienradios.com.ar/la100.aac"),
                new Radio("FM 101.1","LATINA FM","http://streaming.latina101.com.ar:8080/RadioLatina"),
                new Radio("FM 101.5","POP RADIO","rtmp://popradio.stweb.tv/popradio//live"),
                new Radio("FM 104.5","RADIO MARIA","http://50.7.181.186:8004/"),
                new Radio("FM 105.5","LOS 40 PRINCIPALES","http://69.31.54.140/LOS40_ARGENTINA.mp3?"),
                new Radio("FM 107.9","ESPN RADIO","mms://a183.l1318236841.c13182.l.lm.akamaistream.net/D/183/13182/v0001/reflector:36841")
        );
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

    public void getSharedPreferences(){

        String song, artist;
        Integer duration, position;
        boolean random;

        SharedPreferences sharedPreferences = getSharedPreferences("musicService", Context.MODE_PRIVATE);
        song = sharedPreferences.getString("song","");
        artist = sharedPreferences.getString("artist", "");
        duration = sharedPreferences.getInt("duration", 0);
        position = sharedPreferences.getInt("position", 0);
        random = sharedPreferences.getBoolean("isRandom", false);
        currentRadio = sharedPreferences.getInt("radio", 0);
        isRadio = sharedPreferences.getBoolean("isRadio", false);

        isRandom = random;

        if(!song.equals("") && !artist.equals("")) {
            if (!songs.get(position).get("Title").equals(song) || !songs.get(position).get("Artist").equals(artist)){
                int result = findSongAndArtist(song, artist);
                if(result!=-1){
                    currentSong = result;
                    currentDuration = duration;
                }
            }else{
                currentSong = position;
                currentDuration = duration;
            }
        }

    }

    public void playSong(String songPath) {

        try {

            sendResult("SONG_TITLE " + songs.get(currentSong).get("Title"));
            sendResult("SONG_ARTIST " + songs.get(currentSong).get("Artist"));

            mpMusic.reset();
            mpMusic.setDataSource(songPath);
            mpMusic.prepare();
            mpMusic.seekTo(currentDuration);
            mpMusic.start();

            mpMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    nextSong();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playRadio(String url){
        try {
            sendResult("SONG_TITLE " + radios.get(currentRadio).getName());
            sendResult("SONG_ARTIST " + radios.get(currentRadio).getFrequence());

            mpRadio.reset();
            mpRadio.setDataSource(url);
            mpRadio.prepareAsync();

            ((MainMenuActivity)CommandHandlerManager.getInstance().getMainActivity()).setButtonsDisabled();
            ((MainMenuActivity)CommandHandlerManager.getInstance().getMainActivity()).setProgressVisible();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    ((MainMenuActivity)CommandHandlerManager.getInstance().getMainActivity()).setButtonsEnabled();
                    ((MainMenuActivity)CommandHandlerManager.getInstance().getMainActivity()).setProgressInvisible();
                }
            }, 5000);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPlaying(){

        if(!isRadio) {
            if (songs.size() == 0) {
                return;
            }
            playSong(songs.get(currentSong).get("Path"));
        }else{
            playRadio(radios.get(currentRadio).getUrl());
        }

    }

    public void pause(){

        if(!isRadio) {
            if(mpMusic.isPlaying()) {
                currentDuration = mpMusic.getCurrentPosition();
                mpMusic.pause();
            }
        }else{
            if(mpRadio.isPlaying()) {
                mpRadio.pause();
            }
        }
    }

    public void nextSong() {

        currentDuration = 0;
        previousSongs.push(currentSong);
        if(isRandom) {
            Random rn = new Random();
            int choosenSong;
            choosenSong = rn.nextInt(songs.size());
            if(songs.size()!=1) {
                while (choosenSong == currentSong)
                    choosenSong = rn.nextInt(songs.size());
            }
            currentSong = choosenSong;
        }else {
            currentSong++;
            if (currentSong == songs.size())
                currentSong = 0;
        }
        playSong(songs.get(currentSong).get("Path"));
    }

    public void nextRadio(){

        mpRadio.pause();

        currentRadio++;
        if (currentRadio == radios.size())
            currentRadio = 0;

        sendResult("SONG_TITLE " + radios.get(currentRadio).getName());
        sendResult("SONG_ARTIST " + radios.get(currentRadio).getFrequence());
    }

    public void nextSongSet() {

        currentDuration = 0;
        previousSongs.push(currentSong);
        if(isRandom) {
            Random rn = new Random();
            int choosenSong = rn.nextInt(songs.size());
            if(songs.size()!=1) {
                while (choosenSong == currentSong)
                    choosenSong = rn.nextInt(songs.size());
            }
            currentSong = choosenSong;
        }else {
            currentSong++;
            if (currentSong == songs.size())
                currentSong = 0;
        }
    }

    public void nextRadioSet() {

        currentRadio++;
        if (currentRadio == songs.size())
            currentRadio = 0;

    }

    public void previousSong() {

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

    public void previousRadio(){
        mpRadio.pause();
        currentRadio--;
        if(currentRadio == -1)
            currentRadio = radios.size()-1;
        sendResult("SONG_TITLE " + radios.get(currentRadio).getName());
        sendResult("SONG_ARTIST " + radios.get(currentRadio).getFrequence());
    }

    public void previousSongSet() {

        currentDuration = 0;
        if(!previousSongs.empty())
            currentSong = (int) previousSongs.pop();
        else {
            currentSong--;
            if(currentSong == -1)
                currentSong = songs.size()-1;
        }
    }

    public void previousRadioSet(){
        currentRadio--;
        if(currentRadio == -1)
            currentRadio = radios.size()-1;
    }

    public boolean setRandom(boolean random) {

        if((isRandom && random)||(!isRandom && !random))
            return false;

        isRandom = random;

        return true;
    }

    public boolean isListEmpty(){
        return songs.isEmpty();
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

    public int findSongAndArtist(String song, String artist){

        int marker = 0;

        while(marker < songs.size()){

            if(songs.get(marker).get("Title").equals(song) && songs.get(marker).get("Artist").equals(artist)){
                return marker;
            }

            marker++;

        }

        return -1;

    }

    public boolean findRadioName(String name){

        int marker = currentRadio + 1;

        name = name.toLowerCase();

        while(marker < radios.size()){

            if(radios.get(marker).getName().toLowerCase().equals(name)){

                currentRadio = marker;

                return true;
            }

            marker++;

        }

        marker = 0;

        while(marker <= currentRadio){

            if(radios.get(marker).getName().toLowerCase().equals(name)){

                currentRadio = marker;

                return true;
            }

            marker++;

        }


        return false;

    }

    public boolean findRadioFrequence(String frequence){

        int marker = currentRadio + 1;

        frequence = frequence.toLowerCase();

        while(marker < radios.size()){

            if(radios.get(marker).getFrequence().toLowerCase().equals(frequence)){

                currentRadio = marker;

                return true;
            }

            marker++;

        }

        marker = 0;

        while(marker <= currentRadio){

            if(radios.get(marker).getFrequence().toLowerCase().equals(frequence)){

                currentRadio = marker;

                return true;
            }

            marker++;

        }


        return false;

    }


    public boolean isPlaying(){

        if(isRadio)
            return mpRadio.isPlaying();
        else
            return mpMusic.isPlaying();

    }

    public boolean getIsRadio(){
        return  isRadio;
    }

    public void setIsRadio(boolean isRadio){

        boolean play = false;

        if(isPlaying())
            play = true;

        if(isRadio)
            currentDuration = mpMusic.getCurrentPosition();

        mpRadio.pause();
        mpMusic.pause();
        this.isRadio = isRadio;

        if(play)
            startPlaying();

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

    public static void destroyInstance() {
        instance = null;
    }

}

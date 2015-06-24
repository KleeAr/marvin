package ar.com.klee.marvin.multimedia.video;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.youtube.model.SearchResult;

import java.lang.Override;
import java.lang.String;

/**
 * @author msalerno
 */
public class YouTubeVideo implements Parcelable {

    private final String thumbnailUrl;
    private final String description;
    private final String title;
    private final String channelName;
    private final String id;

    public YouTubeVideo(SearchResult item) {
        this.title = item.getSnippet().getTitle();
        this.thumbnailUrl = item.getSnippet().getThumbnails().getDefault().getUrl();
        this.description = item.getSnippet().getDescription();
        this.channelName = item.getSnippet().getChannelTitle();
        this.id = item.getId().getVideoId();
    }

    public YouTubeVideo(Parcel in) {
        String[] data = new String[5];
        in.readStringArray(data);
        this.title = data[0];
        this.description = data[1];
        this.channelName = data[2];
        this.thumbnailUrl = data[3];
        this.id = data[4];
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.title,
                this.description,
                this.channelName, this.thumbnailUrl, this.id});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public YouTubeVideo createFromParcel(Parcel in) {
            return new YouTubeVideo(in);
        }

        public YouTubeVideo[] newArray(int size) {
            return new YouTubeVideo[size];
        }
    };
}

package ar.com.klee.marvin.multimedia.video;

import android.os.AsyncTask;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.activities.DelegateTask;
import ar.com.klee.marvin.multimedia.video.exceptions.YouTubeException;

/**
 * @author msalerno
 */
public class YouTubeService {

    private static final String API_KEY = "AIzaSyCChQomu1xFifFz4qRVjoY4vX8UWc2sxf4";
    private static final Long NUMBER_OF_VIDEOS_RETURNED = 10L;
    private static final String VIDEO = "video";
    private static final String FIELDS_TO_GET = "items(id/kind,id/videoId,snippet/title,snippet/description,snippet/channelTitle,snippet/thumbnails/default/url)";
    private YouTube youTube;

    public YouTubeService() {
        this.youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {

            }
        }).setApplicationName("marvin").build();
    }

    public void searchVideos(String queryTerm, DelegateTask delegateTask) {
        new YouTubeSearchAsyncTask(delegateTask).execute(queryTerm);
    }

    class YouTubeSearchAsyncTask extends AsyncTask<String, Void, List<YouTubeVideo>> {

        private final DelegateTask<List<YouTubeVideo>> delegate;

        public YouTubeSearchAsyncTask(DelegateTask delegateTask) {
            this.delegate = delegateTask;
        }

        @Override
        protected List<YouTubeVideo> doInBackground(String... params) {
            try {
                YouTube.Search.List search = youTube.search().list("id,snippet");

                search.setKey(API_KEY);
                search.setQ(params[0]);

                search.setType(VIDEO);

                search.setFields(FIELDS_TO_GET);
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

                SearchListResponse searchResponse = search.execute();
                List<SearchResult> items = searchResponse.getItems();
                List<YouTubeVideo> videos = new ArrayList<>(items.size());
                for (SearchResult item : items) {
                    videos.add(new YouTubeVideo(item));
                }

                return videos;

            } catch (IOException e) {
                throw new YouTubeException("Error while searching videos with queryTerm: '" + params[0] + "'", e);
            }
        }

        @Override
        protected void onPostExecute(List<YouTubeVideo> youTubeVideos) {
            delegate.execute(youTubeVideos);
        }
    }
}

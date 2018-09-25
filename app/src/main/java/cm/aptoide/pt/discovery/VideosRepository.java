package cm.aptoide.pt.discovery;

import java.util.List;
import rx.Observable;

public class VideosRepository {

  public static final String VIDEO_KEY = "Video";
  private final VideoDataSource videoDataSource;

  public VideosRepository(VideoDataSource videoDataSource) {
    this.videoDataSource = videoDataSource;
  }

  public Observable<List<Video>> loadVideos() {
    return loadFreshVideos();
  }

  private Observable<List<Video>> loadFreshVideos() {
    return videoDataSource.loadFreshVideos(VIDEO_KEY);
  }
}

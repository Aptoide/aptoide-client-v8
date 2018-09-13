package cm.aptoide.pt.discovery;

import java.util.List;

public class VideosRepository {

  public static final String VIDEO_KEY = "Video";
  private final VideoDataSource videoDataSource;

  public VideosRepository(VideoDataSource videoDataSource) {
    this.videoDataSource = videoDataSource;
  }

  public List<Video> loadVideos() {
    return loadFreshVideos();
  }

  private List<Video> loadFreshVideos() {
    return videoDataSource.loadFreshVideos(VIDEO_KEY);
  }
}

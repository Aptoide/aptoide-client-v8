package cm.aptoide.pt.discovery;

import rx.Single;

public class VideosRepository {

  public static final int LIMIT = 10;
  private final VideoDataSource videoDataSource;

  public VideosRepository(VideoDataSource videoDataSource) {
    this.videoDataSource = videoDataSource;
  }

  public Single<VideosList> loadVideos() {
    return loadFreshVideos();
  }

  public Single<VideosList> loadMoreVideos(int offset) {
    return videoDataSource.loadNextVideos(offset, LIMIT);
  }

  private Single<VideosList> loadFreshVideos() {
    return videoDataSource.loadFreshVideos(LIMIT);
  }
}

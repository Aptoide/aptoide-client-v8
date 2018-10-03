package cm.aptoide.pt.discovery;

import rx.Observable;
import rx.Single;

/**
 * Created by franciscocalado on 27/09/2018.
 */

public class VideosManager {

  private VideosRepository videosRepository;
  private InfoVideoService infoVideoService;
  private int offset;

  public VideosManager(VideosRepository videosRepository, InfoVideoService infoVideoService) {
    this.videosRepository = videosRepository;
    this.infoVideoService = infoVideoService;
    this.offset = 0;
  }

  public Single<VideosList> loadVideos() {
    return videosRepository.loadVideos()
        .doOnSuccess(response -> offset = response.getOffset());
  }

  public Single<VideosList> loadMoreVideos() {
    return videosRepository.loadMoreVideos(offset);
  }

  public Observable<String> shouldShowVideos() {
    return infoVideoService.shouldShowVideos();
  }

  public boolean hasMore() {
    return videosRepository.hasMore(offset);
  }
}

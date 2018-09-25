package cm.aptoide.pt.discovery;

import java.util.List;
import rx.Observable;

public class RemoteVideoDataSource implements VideoDataSource {

  @Override public Observable<List<Video>> loadFreshVideos(String key) {
    return null;
  }

  @Override public Observable<List<Video>> loadNextVideos(int offset, int limit, String key) {
    return null;
  }

  @Override public boolean hasMore(Integer offset, String title) {
    return false;
  }
}

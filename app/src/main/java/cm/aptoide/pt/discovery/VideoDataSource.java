package cm.aptoide.pt.discovery;

import java.util.List;
import rx.Observable;

interface VideoDataSource {

  Observable<List<Video>> loadFreshVideos(String key);

  Observable<List<Video>> loadNextVideos(int offset, int limit, String key);

  boolean hasMore(Integer offset, String title);
}

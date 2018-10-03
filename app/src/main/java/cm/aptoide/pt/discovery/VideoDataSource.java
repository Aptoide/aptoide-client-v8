package cm.aptoide.pt.discovery;

import rx.Single;

interface VideoDataSource {

  Single<VideosList> loadFreshVideos(int limit);

  Single<VideosList> loadNextVideos(int offset, int limit);

  boolean hasMore(Integer offset);
}

package cm.aptoide.pt.discovery;

import java.util.List;

interface VideoDataSource {

  List<Video> loadFreshVideos(String key);

  List<Video> loadNextVideos(int offset, int limit, String key);

  boolean hasMore(Integer offset, String title);
}

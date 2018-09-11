package cm.aptoide.pt.discovery;

import java.util.List;

public class RemoteVideoDataSource implements VideoDataSource {


  @Override public List<Video> loadFreshVideos(String key) {
    return null;
  }

  @Override public List<Video> loadNextVideos(int offset, int limit, String key) {
    return null;
  }

  @Override public boolean hasMore(Integer offset, String title) {
    return false;
  }
}

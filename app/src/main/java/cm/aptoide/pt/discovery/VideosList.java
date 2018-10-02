package cm.aptoide.pt.discovery;

import java.util.List;

/**
 * Created by franciscocalado on 28/09/2018.
 */

public class VideosList {

  private List<Video> videoList;
  private int offset;

  public VideosList(List<Video> videos, int offset) {
    videoList = videos;
    this.offset = offset;
  }

  public List<Video> getVideoList() {
    return videoList;
  }

  public void setVideoList(List<Video> videoList) {
    this.videoList = videoList;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }
}

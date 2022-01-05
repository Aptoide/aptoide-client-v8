package cm.aptoide.pt.view.app;

/**
 * Created by D01 on 21/05/2018.
 */

public class AppVideo {
  private final String thumbnail;
  private final String type;
  private final String url;

  public AppVideo(String thumbnail, String type, String url) {
    this.thumbnail = thumbnail;
    this.type = type;
    this.url = url;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public String getType() {
    return type;
  }

  public String getUrl() {
    return url;
  }
}

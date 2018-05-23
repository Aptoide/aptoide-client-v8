package cm.aptoide.pt.app.view;

/**
 * Created by filipegoncalves on 5/23/18.
 */

public class AppBoughClickEvent {

  private String path;
  private long appId;

  public AppBoughClickEvent(String path, long appId) {
    this.path = path;
    this.appId = appId;
  }

  public String getPath() {
    return path;
  }

  public long getAppId() {
    return appId;
  }
}

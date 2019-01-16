package cm.aptoide.pt.view.app;

/**
 * Created by trinkes on 18/10/2017.
 */

public class AptoideApp extends Application {
  private final long appId;

  public AptoideApp(String name, String icon, float rating, int downloads, String packageName,
      long appId, String tag, boolean hasBilling, boolean hasAdvertising) {
    super(name, icon, rating, downloads, packageName, tag, hasBilling, hasAdvertising);
    this.appId = appId;
  }

  public AptoideApp() {
    super(null, null, -1, -1, null, "", false, false);
    this.appId = -1;
  }

  public long getAppId() {
    return appId;
  }
}

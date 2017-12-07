package cm.aptoide.pt.link;

/**
 * Created by trinkes on 04/12/2017.
 */

public class AptoideInstall {
  private final String storeName;
  private final String packageName;
  private final boolean showPopup;
  private final long appId;

  public AptoideInstall(long appId, String packageName, boolean showPopup) {
    this.appId = appId;
    this.packageName = packageName;
    this.showPopup = showPopup;
    storeName = null;
  }

  public AptoideInstall(String storeName, String packageName, boolean showPopup) {
    this.storeName = storeName;
    this.packageName = packageName;
    this.showPopup = showPopup;
    appId = -1;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getPackageName() {
    return packageName;
  }

  public boolean shouldShowPopup() {
    return showPopup;
  }

  public long getAppId() {
    return appId;
  }
}

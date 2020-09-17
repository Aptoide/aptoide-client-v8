package cm.aptoide.pt.link;

/**
 * Created by trinkes on 04/12/2017.
 */

public class AptoideInstall {
  private final String storeName;
  private final String packageName;
  private final String uname;
  private final String openType;
  private final long appId;

  public AptoideInstall(long appId, String packageName, String openType) {
    this.appId = appId;
    this.packageName = packageName;
    this.openType = openType;
    this.uname = null;
    storeName = null;
  }

  public AptoideInstall(String storeName, String packageName, String openType) {
    this.storeName = storeName;
    this.packageName = packageName;
    this.openType = openType;
    this.uname = null;
    appId = -1;
  }

  public AptoideInstall(String uname, String packageName) {
    this.uname = uname;
    this.packageName = packageName;
    this.openType = null;
    this.appId = -1;
    this.storeName = null;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getOpenType() {
    return openType;
  }

  public long getAppId() {
    return appId;
  }

  public String getUname() {
    return uname;
  }
}

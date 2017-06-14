package cm.aptoide.pt.database.realm;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.utils.AptoideUtils;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Installed extends RealmObject {

  //	public static final String ID = "id";
  public static final String ICON = "icon";
  public static final String PACKAGE_NAME = "packageName";
  public static final String NAME = "name";
  public static final String VERSION_CODE = "versionCode";
  public static final String VERSION_NAME = "versionName";
  public static final String SIGNATURE = "signature";
  public static final String STORE_NAME = "storeName";
  public static final int STATUS_UNINSTALLED = 1;
  public static final int STATUS_WAITING = 2;
  public static final int STATUS_INSTALLING = 3;
  public static final int STATUS_COMPLETED = 4;
  public static final int STATUS_ROOT_TIMEOUT = 5;
  public static final int TYPE_DEFAULT = 0;
  public static final int TYPE_ROOT = 1;
  public static final int TYPE_SYSTEM = 2;
  public static final int TYPE_UNKNOWN = -1;

  //	@PrimaryKey private int id = -1;
  @PrimaryKey private String packageAndVersionCode;
  private String icon;
  private String packageName;
  private String name;
  private int versionCode;
  private String versionName;
  private String signature;
  private boolean systemApp;
  private String storeName;
  private int status;
  private int type;

  public Installed() {
  }

  public Installed(@NonNull PackageInfo packageInfo) {
    this(packageInfo, null);
  }

  public Installed(@NonNull PackageInfo packageInfo, @Nullable String storeName) {
    setIcon(AptoideUtils.SystemU.getApkIconPath(packageInfo));
    setName(AptoideUtils.SystemU.getApkLabel(packageInfo));
    setPackageName(packageInfo.packageName);
    setVersionCode(packageInfo.versionCode);
    setVersionName(packageInfo.versionName);
    setStoreName(storeName);
    this.packageAndVersionCode = packageInfo.packageName + packageInfo.versionCode;
    setSystemApp((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
      setSignature(
          AptoideUtils.AlgorithmU.computeSha1WithColon(packageInfo.signatures[0].toByteArray()));
    }
    setStatus(STATUS_COMPLETED);
    setType(TYPE_UNKNOWN);
  }

  public void setPackageAndVersionCode(String packageAndVersionCode) {
    this.packageAndVersionCode = packageAndVersionCode;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public String getVersionName() {
    return versionName;
  }

  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public boolean isSystemApp() {
    return systemApp;
  }

  public void setSystemApp(boolean systemApp) {
    this.systemApp = systemApp;
  }

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }
}

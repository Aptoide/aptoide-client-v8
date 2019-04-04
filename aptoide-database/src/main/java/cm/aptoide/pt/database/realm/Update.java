/*
 * Copyright (c) 2016.
 * Modified on 24/08/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created on 12/05/16.
 */

public class Update extends RealmObject {

  public static final String APP_ID = "appId";
  public static final String LABEL = "label";
  public static final String ICON = "icon";
  public static final String PACKAGE_NAME = "packageName";
  public static final String VERSION_CODE = "versionCode";
  public static final String SIGNATURE = "signature";
  public static final String TIMESTAMP = "timestamp";
  public static final String MD5 = "md5";
  public static final String APK_PATH = "apkPath";
  public static final String FILE_SIZE = "fileSize";
  public static final String UPDATE_VERSION_NAME = "updateVersionName";
  public static final String ALTERNATIVE_URL = "alternativeApkPath";
  public static final String UPDATE_VERSION_CODE = "updateVersionCode";
  public static final String EXCLUDED = "excluded";
  public static final String APPC_UPGRADE = "appcUpgrade";

  @PrimaryKey private String packageName;
  private long appId;
  private String label;
  private String icon;
  private int versionCode;
  //	private String signature;
  private long timestamp;
  private String md5;
  private String apkPath;
  private double fileSize;
  private String updateVersionName;
  private int updateVersionCode;
  private boolean excluded;
  private String trustedBadge;
  private String alternativeApkPath;

  private boolean appcUpgrade;

  // Obb
  private String mainObbName;
  private String mainObbPath;
  private String mainObbMd5;
  private String patchObbName;
  private String patchObbPath;
  private String patchObbMd5;

  public Update() {
  }

  public Update(long id, String name, String icon, String packageName, String md5sum, String path,
      double fileSize, String versionName, String pathAlt, int versionCode, String rankName,
      String mainObbFileName, String mainObbPath, String mainObbMd5, String patchObbFileName,
      String patchObbPath, String patchObbMd5, boolean appcUpgrade) {
    this.appId = id;
    this.label = name;
    this.icon = icon;
    this.packageName = packageName;
    this.md5 = md5sum;
    this.apkPath = path;
    this.fileSize = fileSize;
    this.updateVersionName = versionName;
    this.alternativeApkPath = pathAlt;
    this.updateVersionCode = versionCode;
    this.trustedBadge = rankName;
    this.mainObbName = mainObbFileName;
    this.mainObbPath = mainObbPath;
    this.mainObbMd5 = mainObbMd5;
    this.patchObbName = patchObbFileName;
    this.patchObbPath = patchObbPath;
    this.patchObbMd5 = patchObbMd5;
    this.appcUpgrade = appcUpgrade;
  }

  public long getAppId() {
    return appId;
  }

  public void setAppId(long appId) {
    this.appId = appId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
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

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public String getApkPath() {
    return apkPath;
  }

  public void setApkPath(String apkPath) {
    this.apkPath = apkPath;
  }

  public double getFileSize() {
    return fileSize;
  }

  public void setFileSize(double fileSize) {
    this.fileSize = fileSize;
  }

  public String getUpdateVersionName() {
    return updateVersionName;
  }

  public void setUpdateVersionName(String updateVersionName) {
    this.updateVersionName = updateVersionName;
  }

  public String getAlternativeApkPath() {
    return alternativeApkPath;
  }

  public void setAlternativeApkPath(String alternativeApkPath) {
    this.alternativeApkPath = alternativeApkPath;
  }

  public int getUpdateVersionCode() {
    return updateVersionCode;
  }

  public void setUpdateVersionCode(int updateVersionCode) {
    this.updateVersionCode = updateVersionCode;
  }

  public String getMainObbPath() {
    return mainObbPath;
  }

  public void setMainObbPath(String mainObbPath) {
    this.mainObbPath = mainObbPath;
  }

  public String getMainObbMd5() {
    return mainObbMd5;
  }

  public void setMainObbMd5(String mainObbMd5) {
    this.mainObbMd5 = mainObbMd5;
  }

  public String getPatchObbPath() {
    return patchObbPath;
  }

  public void setPatchObbPath(String patchObbPath) {
    this.patchObbPath = patchObbPath;
  }

  public String getPatchObbMd5() {
    return patchObbMd5;
  }

  public void setPatchObbMd5(String patchObbMd5) {
    this.patchObbMd5 = patchObbMd5;
  }

  public boolean isExcluded() {
    return excluded;
  }

  public void setExcluded(boolean excluded) {
    this.excluded = excluded;
  }

  public String getMainObbName() {
    return mainObbName;
  }

  public void setMainObbName(String mainObbName) {
    this.mainObbName = mainObbName;
  }

  public String getPatchObbName() {
    return patchObbName;
  }

  public void setPatchObbName(String patchObbName) {
    this.patchObbName = patchObbName;
  }

  public String getTrustedBadge() {
    return trustedBadge;
  }

  public boolean isAppcUpgrade() {
    return appcUpgrade;
  }

  public void setAppcUpgrade(boolean appcUpgrade) {
    this.appcUpgrade = appcUpgrade;
  }
}

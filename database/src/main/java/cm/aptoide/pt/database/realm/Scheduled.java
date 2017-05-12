/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.database.realm;

import cm.aptoide.pt.database.AppAction;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Obb;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sithengineer on 12/05/16.
 */
public class Scheduled extends RealmObject {

  //public static final String APP_ID = "appId";
  public static final String NAME = "name";
  public static final String VERSION_NAME = "versionName";
  public static final String ICON = "icon";
  public static final String PATH = "path";
  public static final String MD5 = "md5";
  public static final String VER_CODE = "verCode";
  public static final String IS_DOWNLOADING = "isDownloading";
  public static final String APP_ACTION = "appAction";

  //private long appId; // not available in V7 database
  private String name;
  private String versionName;
  private String icon;
  private String path;
  private String md5; // FK
  private int verCode;
  @PrimaryKey private String packageName; // FK
  private String storeName;
  private String alternativeApkPath;

  // Obb
  private String mainObbName;
  private String mainObbPath;
  private String mainObbMd5;
  private String patchObbName;
  private String patchObbPath;
  private String patchObbMd5;

  // Meta fields
  private boolean isDownloading;

  //AppAction
  private String appAction;

  public Scheduled() {
  }

  public Scheduled(String name, String versionName, String icon, String path, String md5,
      int verCode, String packageName, String storeName, String alternativeApkPath,
      String mainObbName, String mainObbPath, String mainObbMd5, String patchObbName,
      String patchObbPath, String patchObbMd5, boolean isDownloading, String appAction) {
    this.name = name;
    this.versionName = versionName;
    this.icon = icon;
    this.path = path;
    this.md5 = md5;
    this.verCode = verCode;
    this.packageName = packageName;
    this.storeName = storeName;
    this.alternativeApkPath = alternativeApkPath;
    this.mainObbName = mainObbName;
    this.mainObbPath = mainObbPath;
    this.mainObbMd5 = mainObbMd5;
    this.patchObbName = patchObbName;
    this.patchObbPath = patchObbPath;
    this.patchObbMd5 = patchObbMd5;
    this.isDownloading = isDownloading;
    this.appAction = appAction;
  }

  public static Scheduled from(GetAppMeta.App app, AppAction appAction) {

    String mainObbName = null;
    String mainObbPath = null;
    String mainObbMd5 = null;

    String patchObbName = null;
    String patchObbPath = null;
    String patchObbMd5 = null;

    Obb obb = app.getObb();
    if (obb != null) {
      Obb.ObbItem obbMain = obb.getMain();
      Obb.ObbItem obbPatch = obb.getPatch();

      if (obbMain != null) {
        mainObbName = obbMain.getFilename();
        mainObbPath = obbMain.getPath();
        mainObbMd5 = obbMain.getMd5sum();
      }

      if (obbPatch != null) {
        patchObbName = obbPatch.getFilename();
        patchObbPath = obbPatch.getPath();
        patchObbMd5 = obbPatch.getMd5sum();
      }
    }

    return new Scheduled(app.getName(), app.getFile()
        .getVername(), app.getIcon(), app.getFile()
        .getPath(), app.getFile()
        .getMd5sum(), app.getFile()
        .getVercode(), app.getPackageName(), app.getStore()
        .getName(), app.getFile()
        .getPathAlt(), mainObbName, mainObbPath, mainObbMd5, patchObbName, patchObbPath,
        patchObbMd5, false, appAction.name());
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersionName() {
    return versionName;
  }

  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }

  public String getAppAction() {
    return appAction;
  }

  public void setAppAction(String appAction) {
    this.appAction = appAction;
  }

  public AppAction getAppActionAsEnum() {
    return AppAction.valueOf(appAction);
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public int getVerCode() {
    return verCode;
  }

  public void setVerCode(int verCode) {
    this.verCode = verCode;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public String getAlternativeApkPath() {
    return alternativeApkPath;
  }

  public void setAlternativeApkPath(String alternativeApkPath) {
    this.alternativeApkPath = alternativeApkPath;
  }

  public Obb getObb() {
    Obb obb = new Obb();
    Obb.ObbItem mainItem = new Obb.ObbItem();
    mainItem.setFilename(this.mainObbName);
    mainItem.setPath(this.mainObbPath);
    mainItem.setMd5sum(this.mainObbMd5);
    obb.setMain(mainItem);

    Obb.ObbItem patchItem = new Obb.ObbItem();
    patchItem.setFilename(this.patchObbName);
    patchItem.setPath(this.patchObbPath);
    patchItem.setMd5sum(this.patchObbMd5);
    obb.setPatch(patchItem);
    return null;
  }

  public boolean isDownloading() {
    return isDownloading;
  }

  public void setDownloading(boolean isDownloading) {
    this.isDownloading = isDownloading;
  }
}

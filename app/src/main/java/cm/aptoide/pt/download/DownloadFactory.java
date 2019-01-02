/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.download;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.install.AutoUpdate;
import io.realm.RealmList;

/**
 * Created by marcelobenites on 6/29/16.
 */
public class DownloadFactory {

  private static final String UPDATE_ACTION = "?action=update";
  private static final String INSTALL_ACTION = "?action=install";
  private static final String DOWNGRADE_ACTION = "?action=downgrade";
  private final String marketName;

  public DownloadFactory(String marketName) {
    this.marketName = marketName;
  }

  public Download create(GetAppMeta.App appToDownload, int downloadAction)
      throws IllegalArgumentException {
    final GetAppMeta.GetAppMetaFile file = appToDownload.getFile();

    validateApp(appToDownload.getMd5(), appToDownload.getObb(), appToDownload.getPackageName(),
        appToDownload.getName(), file != null ? file.getPath() : null,
        file != null ? file.getPathAlt() : null);

    String path = appToDownload.getFile()
        .getPath();
    String altPath = appToDownload.getFile()
        .getPathAlt();

    ApkPaths downloadPaths = getDownloadPaths(downloadAction, path, altPath);

    Download download = new Download();
    download.setMd5(appToDownload.getFile()
        .getMd5sum());
    download.setIcon(appToDownload.getIcon());
    download.setAppName(appToDownload.getName());
    download.setAction(downloadAction);
    download.setPackageName(appToDownload.getPackageName());
    download.setVersionCode(appToDownload.getFile()
        .getVercode());
    download.setVersionName(appToDownload.getFile()
        .getVername());

    download.setFilesToDownload(
        createFileList(appToDownload.getMd5(), appToDownload.getPackageName(),
            downloadPaths.getPath(), appToDownload.getFile()
                .getMd5sum(), appToDownload.getObb(), downloadPaths.getAltPath(),
            appToDownload.getFile()
                .getVercode(), appToDownload.getFile()
                .getVername()));

    return download;
  }

  private void validateApp(String md5, Obb appObb, String packageName, String appName,
      String filePath, String filePathAlt) throws IllegalArgumentException {
    if (TextUtils.isEmpty(md5)) {
      throw new IllegalArgumentException("Invalid App MD5");
    }
    if (TextUtils.isEmpty(filePath) && TextUtils.isEmpty(filePathAlt)) {
      throw new IllegalArgumentException("No download link provided");
    } else if (appObb != null && TextUtils.isEmpty(packageName)) {
      throw new IllegalArgumentException(
          "This app has an OBB and doesn't have the package name specified");
    } else if (TextUtils.isEmpty(appName)) {
      throw new IllegalArgumentException(
          "This app has an OBB and doesn't have the App name specified");
    }
  }

  ApkPaths getDownloadPaths(int downloadAction, String path, String altPath) {
    switch (downloadAction) {
      case Download.ACTION_INSTALL:
        path += INSTALL_ACTION;
        altPath += INSTALL_ACTION;
        break;
      case Download.ACTION_DOWNGRADE:
        path += DOWNGRADE_ACTION;
        altPath += DOWNGRADE_ACTION;
        break;
      case Download.ACTION_UPDATE:
        path += UPDATE_ACTION;
        altPath += UPDATE_ACTION;
        break;
    }
    return new ApkPaths(path, altPath);
  }

  private RealmList<FileToDownload> createFileList(String md5, String packageName, String filePath,
      String fileMd5, Obb appObb, @Nullable String altPathToApk, int versionCode,
      String versionName) {

    String mainObbPath = null;
    String mainObbMd5 = null;
    String patchObbPath = null;
    String patchObbMd5 = null;
    String mainObbName = null;
    String patchObbName = null;

    if (appObb != null) {
      Obb.ObbItem main = appObb.getMain();
      if (main != null) {
        mainObbPath = main.getPath();
        mainObbMd5 = main.getMd5sum();
        mainObbName = main.getFilename();
      }

      Obb.ObbItem patch = appObb.getPatch();
      if (patch != null) {
        patchObbPath = patch.getPath();
        patchObbMd5 = patch.getMd5sum();
        patchObbName = patch.getFilename();
      }
    }

    return createFileList(md5, packageName, filePath, altPathToApk, fileMd5, mainObbPath,
        mainObbMd5, patchObbPath, patchObbMd5, versionCode, versionName, mainObbName, patchObbName);
  }

  private RealmList<FileToDownload> createFileList(String md5, String packageName, String filePath,
      @Nullable String altPathToApk, String fileMd5, String mainObbPath, String mainObbMd5,
      String patchObbPath, String patchObbMd5, int versionCode, String versionName,
      String mainObbName, String patchObbName) {

    final RealmList<FileToDownload> downloads = new RealmList<>();

    downloads.add(FileToDownload.createFileToDownload(filePath, altPathToApk, md5, fileMd5,
        FileToDownload.APK, packageName, versionCode, versionName));

    if (mainObbPath != null) {
      downloads.add(FileToDownload.createFileToDownload(mainObbPath, null, mainObbMd5, mainObbName,
          FileToDownload.OBB, packageName, versionCode, versionName));
    }

    if (patchObbPath != null) {
      downloads.add(
          FileToDownload.createFileToDownload(patchObbPath, null, patchObbMd5, patchObbName,
              FileToDownload.OBB, packageName, versionCode, versionName));
    }

    return downloads;
  }

  public Download create(Update update) {
    validateApp(update.getMd5(), null, update.getPackageName(), update.getLabel(),
        update.getApkPath(), update.getAlternativeApkPath());
    Download download = new Download();
    download.setMd5(update.getMd5());
    download.setIcon(update.getIcon());
    download.setAppName(update.getLabel());
    download.setAction(Download.ACTION_UPDATE);
    download.setPackageName(update.getPackageName());
    download.setVersionCode(update.getUpdateVersionCode());
    download.setVersionName(update.getUpdateVersionName());
    download.setFilesToDownload(createFileList(update.getMd5(), update.getPackageName(),
        update.getApkPath() + UPDATE_ACTION, update.getAlternativeApkPath() + UPDATE_ACTION,
        update.getMd5(), update.getMainObbPath(), update.getMainObbMd5(), update.getPatchObbPath(),
        update.getPatchObbMd5(), update.getVersionCode(), update.getUpdateVersionName(),
        update.getMainObbName(), update.getPatchObbName()));
    return download;
  }

  public Download create(AutoUpdate.AutoUpdateInfo autoUpdateInfo) {
    Download download = new Download();
    download.setAppName(marketName);
    download.setMd5(autoUpdateInfo.md5);
    download.setVersionCode(autoUpdateInfo.vercode);
    //download.setVersionName(null); // no info available
    download.setPackageName(autoUpdateInfo.packageName);
    download.setAction(Download.ACTION_UPDATE);
    download.setFilesToDownload(
        createFileList(autoUpdateInfo.md5, null, autoUpdateInfo.path + UPDATE_ACTION,
            autoUpdateInfo.md5, null, null, autoUpdateInfo.vercode, null));
    return download;
  }

  public Download create(int downloadAction, String appName, String packageName, String md5,
      String icon, String versionName, int versionCode, String appPath, String appPathAlt,
      Obb obb) {
    validateApp(md5, obb, packageName, appName, appPath, appPathAlt);

    ApkPaths downloadPaths = getDownloadPaths(downloadAction, appPath, appPathAlt);

    Download download = new Download();
    download.setMd5(md5);
    download.setIcon(icon);
    download.setAppName(appName);
    download.setAction(downloadAction);
    download.setPackageName(packageName);
    download.setVersionCode(versionCode);
    download.setVersionName(versionName);

    download.setFilesToDownload(createFileList(md5, packageName, downloadPaths.getPath(), md5, obb,
        downloadPaths.getAltPath(), versionCode, versionName));

    return download;
  }
}

/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.download;

import androidx.annotation.Nullable;
import cm.aptoide.pt.aab.Split;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.database.realm.RealmString;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import io.realm.RealmList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcelobenites on 6/29/16.
 */
public class DownloadFactory {

  private final String marketName;
  private final DownloadApkPathsProvider downloadApkPathsProvider;
  private final String cachePath;
  private final AppValidator appValidator;

  public DownloadFactory(String marketName, DownloadApkPathsProvider downloadApkPathsProvider,
      String cachePath, AppValidator appValidator) {
    this.marketName = marketName;
    this.cachePath = cachePath;
    this.downloadApkPathsProvider = downloadApkPathsProvider;
    this.appValidator = appValidator;
  }

  private RealmList<FileToDownload> createFileList(String md5, String packageName, String filePath,
      String fileMd5, Obb appObb, @Nullable String altPathToApk, int versionCode,
      String versionName, List<Split> splits) {

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
        mainObbMd5, patchObbPath, patchObbMd5, versionCode, versionName, mainObbName, patchObbName,
        splits);
  }

  private RealmList<FileToDownload> createFileList(String md5, String packageName, String filePath,
      @Nullable String altPathToApk, String fileMd5, String mainObbPath, String mainObbMd5,
      String patchObbPath, String patchObbMd5, int versionCode, String versionName,
      String mainObbName, String patchObbName, List<Split> splits) {

    final RealmList<FileToDownload> downloads = new RealmList<>();
    downloads.add(FileToDownload.createFileToDownload(filePath, altPathToApk, md5, fileMd5,
        FileToDownload.APK, packageName, versionCode, versionName, cachePath));

    if (mainObbPath != null) {
      downloads.add(FileToDownload.createFileToDownload(mainObbPath, null, mainObbMd5, mainObbName,
          FileToDownload.OBB, packageName, versionCode, versionName, cachePath));
    }

    if (patchObbPath != null) {
      downloads.add(
          FileToDownload.createFileToDownload(patchObbPath, null, patchObbMd5, patchObbName,
              FileToDownload.OBB, packageName, versionCode, versionName, cachePath));
    }

    if (splits != null) {
      for (Split split : splits) {
        downloads.add(FileToDownload.createFileToDownload(split.getPath(), null, split.getMd5sum(),
            split.getMd5sum() + "." + split.getName(), FileToDownload.SPLIT, packageName,
            versionCode, versionName, cachePath));
      }
    }

    return downloads;
  }

  public Download create(Update update, boolean isAppcUpgrade) {
    List<Split> splits = map(update.getSplits());
    AppValidator.AppValidationResult validationResult =
        appValidator.validateApp(update.getMd5(), null, update.getPackageName(), update.getLabel(),
            update.getApkPath(), update.getAlternativeApkPath(), splits,
            mapRequiredSplits(update.getRequiredSplits()));

    if (validationResult == AppValidator.AppValidationResult.VALID_APP) {
      ApkPaths downloadPaths = downloadApkPathsProvider.getDownloadPaths(
          isAppcUpgrade ? Download.ACTION_DOWNGRADE : Download.ACTION_UPDATE, update.getApkPath(),
          update.getAlternativeApkPath());

      Download download = new Download();
      download.setMd5(update.getMd5());
      download.setIcon(update.getIcon());
      download.setAppName(update.getLabel());
      download.setAction(isAppcUpgrade ? Download.ACTION_DOWNGRADE : Download.ACTION_UPDATE);
      download.setPackageName(update.getPackageName());
      download.setVersionCode(update.getUpdateVersionCode());
      download.setVersionName(update.getUpdateVersionName());
      download.setHasAppc(update.hasAppc());
      download.setTrustedBadge(update.getTrustedBadge());
      download.setStoreName(update.getStoreName());
      download.setFilesToDownload(
          createFileList(update.getMd5(), update.getPackageName(), downloadPaths.getPath(),
              downloadPaths.getAltPath(), update.getMd5(), update.getMainObbPath(),
              update.getMainObbMd5(), update.getPatchObbPath(), update.getPatchObbMd5(),
              update.getUpdateVersionCode(), update.getUpdateVersionName(), update.getMainObbName(),
              update.getPatchObbName(), splits));
      download.setSize(update.getSize());
      return download;
    } else {
      throw new IllegalArgumentException(validationResult.getMessage());
    }
  }

  private List<String> mapRequiredSplits(RealmList<RealmString> requiredSplits) {
    ArrayList<String> requiredSplitsResult = new ArrayList<>();
    if (requiredSplits == null) return requiredSplitsResult;
    for (RealmString split : requiredSplits) {
      requiredSplitsResult.add(split.getString());
    }
    return requiredSplitsResult;
  }

  private List<Split> map(RealmList<cm.aptoide.pt.database.realm.Split> splits) {
    ArrayList<Split> splitsResult = new ArrayList<>();
    if (splits == null) return splitsResult;
    for (cm.aptoide.pt.database.realm.Split split : splits) {
      splitsResult.add(
          new Split(split.getName(), split.getType(), split.getPath(), split.getFileSize(),
              split.getMd5()));
    }
    return splitsResult;
  }

  public Download create(String md5, int versionCode, String packageName, String uri,
      boolean hasAppc) {
    ApkPaths downloadPaths =
        downloadApkPathsProvider.getDownloadPaths(Download.ACTION_UPDATE, uri, null);
    String versionName =
        "Auto-Update"; //This is needed since we're using the version name to compare installs
    Download download = new Download();
    download.setAppName(marketName);
    download.setMd5(md5);
    download.setVersionCode(versionCode);
    download.setPackageName(packageName);
    download.setVersionName(versionName);
    download.setAction(Download.ACTION_UPDATE);
    download.setHasAppc(hasAppc);
    download.setSize(0);
    download.setFilesToDownload(
        createFileList(md5, packageName, downloadPaths.getPath(), md5, null, null, versionCode,
            versionName, null)); // no splits : auto-update
    return download;
  }

  public Download create(int downloadAction, String appName, String packageName, String md5,
      String icon, String versionName, int versionCode, String appPath, String appPathAlt, Obb obb,
      boolean hasAppc, long size, List<Split> splits, List<String> requiredSplits,
      String trustedBadge, String storeName) {

    AppValidator.AppValidationResult validationResult =
        appValidator.validateApp(md5, obb, packageName, appName, appPath, appPathAlt, splits,
            requiredSplits);

    if (validationResult == AppValidator.AppValidationResult.VALID_APP) {

      ApkPaths downloadPaths =
          downloadApkPathsProvider.getDownloadPaths(downloadAction, appPath, appPathAlt);

      Download download = new Download();
      download.setMd5(md5);
      download.setIcon(icon);
      download.setAppName(appName);
      download.setAction(downloadAction);
      download.setPackageName(packageName);
      download.setHasAppc(hasAppc);
      download.setVersionCode(versionCode);
      download.setVersionName(versionName);
      download.setSize(size);
      download.setTrustedBadge(trustedBadge);
      download.setStoreName(storeName);
      download.setFilesToDownload(
          createFileList(md5, packageName, downloadPaths.getPath(), md5, obb,
              downloadPaths.getAltPath(), versionCode, versionName, splits));

      return download;
    } else {
      throw new IllegalArgumentException(validationResult.getMessage());
    }
  }
}

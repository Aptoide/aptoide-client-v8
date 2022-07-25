/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.download;

import androidx.annotation.Nullable;
import cm.aptoide.pt.aab.DynamicSplit;
import cm.aptoide.pt.aab.Split;
import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.database.room.RoomFileToDownload;
import cm.aptoide.pt.database.room.RoomSplit;
import cm.aptoide.pt.database.room.RoomUpdate;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
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
  private final SplitTypeSubFileTypeMapper splitTypeSubFileTypeMapper;

  public DownloadFactory(String marketName, DownloadApkPathsProvider downloadApkPathsProvider,
      String cachePath, AppValidator appValidator,
      SplitTypeSubFileTypeMapper splitTypeSubFileTypeMapper) {
    this.marketName = marketName;
    this.cachePath = cachePath;
    this.downloadApkPathsProvider = downloadApkPathsProvider;
    this.appValidator = appValidator;
    this.splitTypeSubFileTypeMapper = splitTypeSubFileTypeMapper;
  }

  private List<RoomFileToDownload> createFileList(String md5, String packageName, String filePath,
      String fileMd5, Obb appObb, @Nullable String altPathToApk, int versionCode,
      String versionName, List<Split> splits, List<DynamicSplit> dynamicSplitList) {

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
        splits, dynamicSplitList);
  }

  private List<RoomFileToDownload> createFileList(String md5, String packageName, String filePath,
      @Nullable String altPathToApk, String fileMd5, String mainObbPath, String mainObbMd5,
      String patchObbPath, String patchObbMd5, int versionCode, String versionName,
      String mainObbName, String patchObbName, List<Split> splits,
      List<DynamicSplit> dynamicSplitList) {

    final List<RoomFileToDownload> downloads = new ArrayList<>();
    downloads.add(RoomFileToDownload.createFileToDownload(filePath, altPathToApk, md5, fileMd5,
        RoomFileToDownload.APK, packageName, versionCode, versionName, cachePath,
        RoomFileToDownload.SUBTYPE_APK));

    if (mainObbPath != null) {
      downloads.add(
          RoomFileToDownload.createFileToDownload(mainObbPath, null, mainObbMd5, mainObbName,
              RoomFileToDownload.OBB, packageName, versionCode, versionName, cachePath,
              RoomFileToDownload.MAIN));
    }

    if (patchObbPath != null) {
      downloads.add(
          RoomFileToDownload.createFileToDownload(patchObbPath, null, patchObbMd5, patchObbName,
              RoomFileToDownload.OBB, packageName, versionCode, versionName, cachePath,
              RoomFileToDownload.PATCH));
    }

    if (splits != null) {
      for (Split split : splits) {
        downloads.add(
            RoomFileToDownload.createFileToDownload(split.getPath(), null, split.getMd5sum(),
                split.getMd5sum() + "." + split.getName(), RoomFileToDownload.SPLIT, packageName,
                versionCode, versionName, cachePath, RoomFileToDownload.BASE));
      }
    }

    if (dynamicSplitList != null) {
      for (DynamicSplit dynamicSplit : dynamicSplitList) {

        if (dynamicSplit.getDeliveryTypes()
            .contains("INSTALL_TIME")) {

          int splitType = splitTypeSubFileTypeMapper.mapSplitToSubFileType(dynamicSplit.getType());

          downloads.add(RoomFileToDownload.createFileToDownload(dynamicSplit.getPath(), null,
              dynamicSplit.getMd5Sum(), dynamicSplit.getMd5Sum() + "." + dynamicSplit.getName(),
              RoomFileToDownload.SPLIT, packageName, versionCode, versionName, cachePath,
              splitType));

          for (Split configSplit : dynamicSplit.getConfigSplits()) {
            downloads.add(RoomFileToDownload.createFileToDownload(configSplit.getPath(), null,
                configSplit.getMd5sum(), configSplit.getMd5sum() + "." + configSplit.getName(),
                RoomFileToDownload.SPLIT, packageName, versionCode, versionName, cachePath,
                splitType));
          }
        }
      }
    }

    return downloads;
  }

  public RoomDownload create(RoomUpdate update, boolean isAppcUpgrade,
      List<DynamicSplit> dynamicSplits) {

    AppValidator.AppValidationResult validationResult =
        appValidator.validateApp(update.getMd5(), null, update.getPackageName(), update.getLabel(),
            update.getApkPath(), update.getAlternativeApkPath(), map(update.getRoomSplits()),
            update.getRequiredSplits());

    if (validationResult == AppValidator.AppValidationResult.VALID_APP) {
      ApkPaths downloadPaths = downloadApkPathsProvider.getDownloadPaths(
          isAppcUpgrade ? RoomDownload.ACTION_DOWNGRADE : RoomDownload.ACTION_UPDATE,
          update.getApkPath(), update.getAlternativeApkPath());

      RoomDownload download = new RoomDownload();
      download.setMd5(update.getMd5());
      download.setIcon(update.getIcon());
      download.setAppName(update.getLabel());
      download.setAction(
          isAppcUpgrade ? RoomDownload.ACTION_DOWNGRADE : RoomDownload.ACTION_UPDATE);
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
              update.getPatchObbName(), map(update.getRoomSplits()), dynamicSplits));
      download.setSize(calculateAppSize(update.getSize(), dynamicSplits));
      return download;
    } else {
      throw new InvalidAppException(validationResult.getMessage());
    }
  }

  private List<Split> map(List<RoomSplit> roomSplits) {
    List<Split> splitsResult = new ArrayList<>();
    if (roomSplits == null) return splitsResult;
    for (RoomSplit roomSplit : roomSplits) {
      splitsResult.add(new Split(roomSplit.getName(), roomSplit.getType(), roomSplit.getPath(),
          roomSplit.getFileSize(), roomSplit.getMd5()));
    }
    return splitsResult;
  }

  public RoomDownload create(String md5, int versionCode, String packageName, String uri,
      boolean hasAppc) {
    ApkPaths downloadPaths =
        downloadApkPathsProvider.getDownloadPaths(RoomDownload.ACTION_UPDATE, uri, uri);
    String versionName =
        "Auto-Update"; //This is needed since we're using the version name to compare installs
    RoomDownload download = new RoomDownload();
    download.setAppName(marketName);
    download.setMd5(md5);
    download.setVersionCode(versionCode);
    download.setPackageName(packageName);
    download.setVersionName(versionName);
    download.setIcon("");
    download.setAction(RoomDownload.ACTION_UPDATE);
    download.setHasAppc(hasAppc);
    download.setSize(0);
    download.setFilesToDownload(createFileList(md5, packageName, downloadPaths.getPath(), md5, null,
        downloadPaths.getAltPath(), versionCode, versionName, null,
        null)); // no splits, no oemid : auto-update
    return download;
  }

  public RoomDownload create(int downloadAction, String appName, String packageName, String md5,
      String icon, String versionName, int versionCode, String appPath, String appPathAlt, Obb obb,
      boolean hasAppc, long size, List<Split> splits, List<String> requiredSplits,
      String trustedBadge, String storeName, List<DynamicSplit> dynamicSplits) {
    return create(downloadAction, appName, packageName, md5, icon, versionName, versionCode,
        appPath, appPathAlt, obb, hasAppc, size, splits, requiredSplits, trustedBadge, storeName,
        null, dynamicSplits);
  }

  public RoomDownload create(int downloadAction, String appName, String packageName, String md5,
      String icon, String versionName, int versionCode, String appPath, String appPathAlt, Obb obb,
      boolean hasAppc, long appBaseSize, List<Split> splits, List<String> requiredSplits,
      String trustedBadge, String storeName, String oemId, List<DynamicSplit> dynamicSplits) {

    AppValidator.AppValidationResult validationResult =
        appValidator.validateApp(md5, obb, packageName, appName, appPath, appPathAlt, splits,
            requiredSplits);

    if (validationResult == AppValidator.AppValidationResult.VALID_APP) {

      ApkPaths downloadPaths =
          downloadApkPathsProvider.getDownloadPaths(downloadAction, appPath, appPathAlt, oemId);

      long completeAppSize = calculateAppSize(appBaseSize, dynamicSplits);

      RoomDownload download = new RoomDownload();
      download.setMd5(md5);
      download.setIcon(icon);
      download.setAppName(appName);
      download.setAction(downloadAction);
      download.setPackageName(packageName);
      download.setHasAppc(hasAppc);
      download.setVersionCode(versionCode);
      download.setVersionName(versionName);
      download.setSize(completeAppSize);
      download.setTrustedBadge(trustedBadge);
      download.setStoreName(storeName);
      download.setAttributionId(oemId);
      download.setFilesToDownload(
          createFileList(md5, packageName, downloadPaths.getPath(), md5, obb,
              downloadPaths.getAltPath(), versionCode, versionName, splits, dynamicSplits));

      return download;
    } else {
      throw new InvalidAppException(validationResult.getMessage());
    }
  }

  private long calculateAppSize(long appBaseSize, List<DynamicSplit> dynamicSplits) {
    long dynamicSplitsSize = 0;
    for (DynamicSplit dynamicSplit : dynamicSplits) {

      if (dynamicSplit.getDeliveryTypes()
          .contains("INSTALL_TIME")) {

        dynamicSplitsSize += dynamicSplit.getFileSize();
        for (Split configSplit : dynamicSplit.getConfigSplits()) {
          dynamicSplitsSize += configSplit.getFilesize();
        }
      }
    }
    return dynamicSplitsSize + appBaseSize;
  }
}

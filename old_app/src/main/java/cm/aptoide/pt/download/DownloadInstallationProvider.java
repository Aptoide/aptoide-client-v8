/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.download;

import androidx.annotation.NonNull;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.database.RoomStoredMinimalAdPersistence;
import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.database.room.RoomInstalled;
import cm.aptoide.pt.database.room.RoomStoredMinimalAd;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadPersistence;
import cm.aptoide.pt.install.AptoideInstalledAppsRepository;
import cm.aptoide.pt.install.exception.InstallationException;
import cm.aptoide.pt.install.installer.DownloadInstallationAdapter;
import cm.aptoide.pt.install.installer.Installation;
import cm.aptoide.pt.install.installer.InstallationProvider;
import cm.aptoide.pt.logger.Logger;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 7/25/16.
 */
public class DownloadInstallationProvider implements InstallationProvider {

  private static final String TAG = "DownloadInstallationPro";
  private final AptoideDownloadManager downloadManager;
  private final DownloadPersistence downloadPersistence;
  private final MinimalAdMapper adMapper;
  private final AptoideInstalledAppsRepository aptoideInstalledAppsRepository;
  private final RoomStoredMinimalAdPersistence roomStoredMinimalAdPersistence;

  public DownloadInstallationProvider(AptoideDownloadManager downloadManager,
      DownloadPersistence downloadPersistence,
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository, MinimalAdMapper adMapper,
      RoomStoredMinimalAdPersistence roomStoredMinimalAdPersistence) {
    this.downloadManager = downloadManager;
    this.downloadPersistence = downloadPersistence;
    this.adMapper = adMapper;
    this.aptoideInstalledAppsRepository = aptoideInstalledAppsRepository;
    this.roomStoredMinimalAdPersistence = roomStoredMinimalAdPersistence;
  }

  @Override public Observable<Installation> getInstallation(String md5) {
    Logger.getInstance()
        .d(TAG, "Getting the installation " + md5);
    return downloadManager.getDownloadAsSingle(md5)
        .toObservable()
        .flatMap(download -> {
          if (download.getOverallDownloadStatus() == RoomDownload.COMPLETED) {
            return aptoideInstalledAppsRepository.get(download.getPackageName(),
                download.getVersionCode())
                .map(installed -> {
                  if (installed == null) {
                    installed = convertDownloadToInstalled(download);
                  }
                  return new DownloadInstallationAdapter(download, downloadPersistence,
                      aptoideInstalledAppsRepository, installed);
                })
                .doOnNext(downloadInstallationAdapter -> {
                  roomStoredMinimalAdPersistence.get(downloadInstallationAdapter.getPackageName())
                      .doOnNext(handleCpd())
                      .subscribeOn(Schedulers.io())
                      .subscribe(storedMinimalAd -> {
                      }, Throwable::printStackTrace);
                });
          }
          return Observable.error(new InstallationException(
              "Installation file not available. download is "
                  + download.getMd5()
                  + " and the state is : "
                  + download.getOverallDownloadStatus()));
        });
  }

  @NonNull private RoomInstalled convertDownloadToInstalled(RoomDownload download) {
    RoomInstalled installed = new RoomInstalled();
    installed.setPackageAndVersionCode(download.getPackageName() + download.getVersionCode());
    installed.setVersionCode(download.getVersionCode());
    installed.setVersionName(download.getVersionName());
    installed.setAppSize(download.getSize());
    installed.setStatus(RoomInstalled.STATUS_UNINSTALLED);
    installed.setType(RoomInstalled.TYPE_UNKNOWN);
    installed.setPackageName(download.getPackageName());
    return installed;
  }

  @NonNull private Action1<RoomStoredMinimalAd> handleCpd() {
    return storedMinimalAd -> {
      if (storedMinimalAd != null && storedMinimalAd.getCpdUrl() != null) {
        AdNetworkUtils.knockCpd(adMapper.map(storedMinimalAd));
        storedMinimalAd.setCpdUrl(null);
        roomStoredMinimalAdPersistence.insert(storedMinimalAd);
      }
    };
  }
}

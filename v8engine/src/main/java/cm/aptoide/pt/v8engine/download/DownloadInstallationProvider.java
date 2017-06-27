/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.v8engine.download;

import android.support.annotation.NonNull;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.accessors.StoredMinimalAdAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.v8engine.ads.MinimalAdMapper;
import cm.aptoide.pt.v8engine.install.exception.InstallationException;
import cm.aptoide.pt.v8engine.install.installer.InstallationProvider;
import cm.aptoide.pt.v8engine.install.installer.RollbackInstallation;
import cm.aptoide.pt.v8engine.install.rollback.DownloadInstallationAdapter;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 7/25/16.
 */
public class DownloadInstallationProvider implements InstallationProvider {

  private final AptoideDownloadManager downloadManager;
  private final DownloadAccessor downloadAccessor;
  private StoredMinimalAdAccessor storedMinimalAdAccessor;
  private final MinimalAdMapper adMapper;

  public DownloadInstallationProvider(AptoideDownloadManager downloadManager,
      DownloadAccessor downloadAccessor, MinimalAdMapper adMapper) {
    this.downloadManager = downloadManager;
    this.downloadAccessor = downloadAccessor;
    this.adMapper = adMapper;
    this.storedMinimalAdAccessor = AccessorFactory.getAccessorFor(StoredMinimalAd.class);
  }

  @Override public Observable<RollbackInstallation> getInstallation(String md5) {
    return downloadManager.getDownload(md5)
        .first()
        .flatMap(download -> {
          if (download.getOverallDownloadStatus() == Download.COMPLETED) {
            return Observable.just(new DownloadInstallationAdapter(download, downloadAccessor))
                .doOnNext(downloadInstallationAdapter -> {
                  storedMinimalAdAccessor.get(download.getPackageName())
                      .doOnNext(handleCpd())
                      .subscribeOn(Schedulers.io())
                      .subscribe(storedMinimalAd -> {
                      }, Throwable::printStackTrace);
                });
          }
          return Observable.error(new InstallationException("Installation file not available."));
        });
  }

  @NonNull private Action1<StoredMinimalAd> handleCpd() {
    return storedMinimalAd -> {
      if (storedMinimalAd != null && storedMinimalAd.getCpdUrl() != null) {
        AdNetworkUtils.knockCpd(adMapper.map(storedMinimalAd));
        storedMinimalAd.setCpdUrl(null);
        storedMinimalAdAccessor.insert(storedMinimalAd);
      }
    };
  }
}

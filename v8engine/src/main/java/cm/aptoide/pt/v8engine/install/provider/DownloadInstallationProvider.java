/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.v8engine.install.provider;

import android.support.annotation.NonNull;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.accessors.StoreMinimalAdAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.v8engine.install.exception.InstallationException;
import cm.aptoide.pt.v8engine.install.installer.InstallationProvider;
import cm.aptoide.pt.v8engine.install.installer.RollbackInstallation;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 7/25/16.
 */
public class DownloadInstallationProvider implements InstallationProvider {

  private final AptoideDownloadManager downloadManager;
  private final DownloadAccessor downloadAccessor;
  private StoreMinimalAdAccessor storeMinimalAdAccessor;

  public DownloadInstallationProvider(AptoideDownloadManager downloadManager,
      DownloadAccessor downloadAccessor) {
    this.downloadManager = downloadManager;
    this.downloadAccessor = downloadAccessor;
    this.storeMinimalAdAccessor = AccessorFactory.getAccessorFor(StoredMinimalAd.class);
  }

  @Override public Observable<RollbackInstallation> getInstallation(String md5) {
    return downloadManager.getDownload(md5).first().flatMap(download -> {
      if (download.getOverallDownloadStatus() == Download.COMPLETED) {
        return Observable.just(new DownloadInstallationAdapter(download, downloadAccessor))
            .doOnNext(downloadInstallationAdapter -> {
              storeMinimalAdAccessor.get(download.getPackageName())
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
        DataproviderUtils.AdNetworksUtils.knockCpd(storedMinimalAd);
        storedMinimalAd.setCpdUrl(null);
        storeMinimalAdAccessor.insert(storedMinimalAd);
      }
    };
  }
}

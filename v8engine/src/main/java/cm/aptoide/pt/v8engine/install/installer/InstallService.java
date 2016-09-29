/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.v8engine.install.installer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.install.provider.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.install.provider.RollbackFactory;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.repository.AppRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import rx.Observable;
import rx.Subscription;

/**
 * Created by marcelobenites on 9/29/16.
 */

public class InstallService extends Service {

  public static final String TAG = "InstallService";
  private Subscription subscription;
  private long currentDownloadId;
  private Installer installer;
  private AptoideDownloadManager downloadManager;
  private InstalledAccessor installedAccessor;
  private AppRepository appRepository;

  @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    downloadManager = AptoideDownloadManager.getInstance();
    DownloadInstallationProvider installationProvider = new DownloadInstallationProvider(
        new DownloadServiceHelper(downloadManager, new PermissionManager()));
    installer = new InstallerFactory().create(this, InstallerFactory.ROLLBACK);
    appRepository = new AppRepository(
        new NetworkOperatorManager((TelephonyManager) getSystemService(TELEPHONY_SERVICE)),
        new ProductFactory());
    installedAccessor = AccessorFactory.getAccessorFor(Installed.class);

    subscription = downloadManager.getDownloads()
        .flatMapIterable(downloads -> downloads)
        .doOnNext(download -> updateCurrentDownload(download))
        .filter(download -> download.getOverallDownloadStatus() == Download.COMPLETED
            && download.getAppId() == currentDownloadId)
        .doOnNext(download -> removeCurrentDownload(download))
        .flatMap(completedDownload -> installAction(completedDownload))
        .subscribe(success -> {}, throwable -> Logger.e(TAG, throwable.getMessage()));
  }

  private Observable<Void> installAction(Download completedDownload) {
    return installedAccessor.get(completedDownload.getPackageName()).flatMap(installed -> {
          if (installed == null) {
            return installer.install(this, completedDownload.getAppId());
          } else if (installed.getVersionCode() > completedDownload.getVersionCode()) {
            return installer.downgrade(this, completedDownload.getAppId());
          } else if (installed.getVersionCode() < completedDownload.getVersionCode()) {
            return installer.update(this, completedDownload.getAppId());
          }
          return Observable.empty();
        });
  }

  private void removeCurrentDownload(Download download) {
    if (currentDownloadId == download.getAppId()) {
      currentDownloadId = -1;
    }
  }

  private void updateCurrentDownload(Download download) {
    if (download.getOverallDownloadStatus() == Download.PROGRESS) {
      currentDownloadId = download.getAppId();
    }
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (subscription != null) {
      subscription.unsubscribe();
    }
  }
}

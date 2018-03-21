package cm.aptoide.pt.home.apps;

import android.content.Context;
import android.content.pm.PackageManager;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.InstallType;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.List;
import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsManager {

  private UpdatesManager updatesManager;
  private InstallManager installManager;
  private InstallToDownloadAppMapper installToDownloadAppMapper;
  private InstalledToInstalledAppMapper installedToInstalledAppMapper;
  private DownloadAnalytics downloadAnalytics;
  private InstallAnalytics installAnalytics;
  private PackageManager packageManager;
  private Context context;

  public AppsManager(UpdatesManager updatesManager, InstallManager installManager,
      InstallToDownloadAppMapper downloadsManager,
      InstalledToInstalledAppMapper installedToInstalledAppMapper,
      DownloadAnalytics downloadAnalytics, InstallAnalytics installAnalytics,
      PackageManager packageManager, Context context) {
    this.updatesManager = updatesManager;
    this.installManager = installManager;
    this.installToDownloadAppMapper = downloadsManager;
    this.installedToInstalledAppMapper = installedToInstalledAppMapper;
    this.downloadAnalytics = downloadAnalytics;
    this.installAnalytics = installAnalytics;
    this.packageManager = packageManager;
    this.context = context;
  }

  public Observable<List<App>> getUpdatesList() {
    //return updatesManager.getUpdatesList()
    //  .map(updatesList ->);
    // TODO: 3/7/18 map Displayables to updateApp
    return null;
  }

  public Observable<List<App>> getInstalledApps() {
    return installManager.fetchInstalled()
        .flatMapIterable(list -> list)
        .flatMap(item -> updatesManager.filterUpdates(item))
        .toList()
        .map(installeds -> installedToInstalledAppMapper.getInstalledApps(installeds));
  }

  public Observable<List<App>> getDownloadApps() {
    return installManager.getInstallations()
        .flatMap(installations -> {
          if (installations == null || installations.isEmpty()) {
            return Observable.empty();
          }
          return Observable.just(installations)
              .map(installedApps -> installToDownloadAppMapper.getDownloadApps(installedApps));
        });
  }

  public Completable retryDownload(App app) {
    return resumeDownload(app);
  }

  public Completable installApp(App app) {
    return installManager.getInstall(((DownloadApp) app).getMd5(),
        ((DownloadApp) app).getPackageName(), ((DownloadApp) app).getVersionCode())
        .first()
        .flatMapCompletable(installationProgress -> {
          if (installationProgress.getState() == Install.InstallationStatus.INSTALLED) {
            AptoideUtils.SystemU.openApp(((DownloadApp) app).getPackageName(), packageManager,
                context);
            return Completable.never();
          } else {
            return resumeDownload(app);
          }
        })
        .toCompletable();
  }

  public void cancelDownload(App app) {
    installManager.removeInstallationFile(((DownloadApp) app).getMd5(),
        ((DownloadApp) app).getPackageName(), ((DownloadApp) app).getVersionCode());
  }

  public Completable resumeDownload(App app) {
    return installManager.getDownload(((DownloadApp) app).getMd5())
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(subscription -> setupEvents(download)));
    /**
     .toObservable()
     .flatMap(downloadProgress -> installManager.getInstall(((DownloadApp) app).getMd5(),
     ((DownloadApp) app).getPackageName(), ((DownloadApp) app).getVersionCode()))
     .doOnSubscribe(() -> setupEvents(download)));
     **/
  }

  private void setupEvents(Download download) {
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.DOWNLOADS);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        getInstallType(download.getAction()), AnalyticsManager.Action.INSTALL, AppContext.DOWNLOADS,
        getOrigin(download.getAction()));
  }

  private Origin getOrigin(int action) {
    switch (action) {
      default:
      case Download.ACTION_INSTALL:
        return Origin.INSTALL;
      case Download.ACTION_UPDATE:
        return Origin.UPDATE;
      case Download.ACTION_DOWNGRADE:
        return Origin.DOWNGRADE;
    }
  }

  private InstallType getInstallType(int action) {
    switch (action) {
      default:
      case Download.ACTION_INSTALL:
        return InstallType.INSTALL;
      case Download.ACTION_UPDATE:
        return InstallType.UPDATE;
      case Download.ACTION_DOWNGRADE:
        return InstallType.DOWNGRADE;
    }
  }

  public void pauseDownload(App app) {
    installManager.stopInstallation(((DownloadApp) app).getMd5());
  }

  public void retryUpdate(App app) {

  }

  public void resumeUpdate(App app) {

  }

  public void cancelUpdate(App app) {

  }

  public void pauseUpdate(App app) {

  }

  public void updateApp(App app) {

  }
}

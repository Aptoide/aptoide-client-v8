package cm.aptoide.pt.home.apps;

import android.content.Context;
import android.content.pm.PackageManager;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.InstallType;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.updates.UpdatesAnalytics;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.List;
import rx.Completable;
import rx.Observable;

import static cm.aptoide.pt.install.Install.InstallationType.UPDATE;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsManager {

  private UpdatesManager updatesManager;
  private InstallManager installManager;
  private AppMapper appMapper;
  private DownloadAnalytics downloadAnalytics;
  private InstallAnalytics installAnalytics;
  private UpdatesAnalytics updatesAnalytics;
  private PackageManager packageManager;
  private Context context;
  private DownloadFactory downloadFactory;

  public AppsManager(UpdatesManager updatesManager, InstallManager installManager,
      AppMapper appMapper, DownloadAnalytics downloadAnalytics, InstallAnalytics installAnalytics,
      UpdatesAnalytics updatesAnalytics, PackageManager packageManager, Context context,
      DownloadFactory downloadFactory) {
    this.updatesManager = updatesManager;
    this.installManager = installManager;
    this.appMapper = appMapper;
    this.downloadAnalytics = downloadAnalytics;
    this.installAnalytics = installAnalytics;
    this.updatesAnalytics = updatesAnalytics;
    this.packageManager = packageManager;
    this.context = context;
    this.downloadFactory = downloadFactory;
  }

  public Observable<List<App>> getUpdatesList(boolean isExcluded) {
    return updatesManager.getUpdatesList(isExcluded)
        .map(updates -> appMapper.mapUpdateToUpdateAppList(updates));
  }

  public Observable<List<App>> getUpdateDownloadsList() {
    return installManager.getInstallations()
        .flatMap(installations -> {
          if (installations == null || installations.isEmpty()) {
            return Observable.empty();
          }
          return Observable.just(installations)
              .flatMapIterable(installs -> installs)
              .filter(install -> install.getType() == UPDATE)
              .toList()
              .map(updatesList -> appMapper.getUpdatesList(updatesList));
        });
  }

  public Observable<List<App>> getInstalledApps() {
    return installManager.fetchInstalled()
        .flatMapIterable(list -> list)
        .flatMap(item -> updatesManager.filterUpdates(item))
        .toList()
        .map(installeds -> appMapper.getInstalledApps(installeds));
  }

  public Observable<List<App>> getDownloadApps() {
    return installManager.getInstallations()
        .flatMap(installations -> {
          if (installations == null || installations.isEmpty()) {
            return Observable.empty();
          }
          return Observable.just(installations)
              .flatMapIterable(installs -> installs)
              .filter(install -> install.getType() != Install.InstallationType.UPDATE
                  && install.getType() != Install.InstallationType.INSTALLED)
              .toList()
              .map(installedApps -> appMapper.getDownloadApps(installedApps));
        });
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
            .doOnSubscribe(subscription -> setupDownloadEvents(download)));
  }

  private void setupDownloadEvents(Download download) {
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.DOWNLOADS);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        getInstallType(download.getAction()), AnalyticsManager.Action.INSTALL, AppContext.DOWNLOADS,
        getOrigin(download.getAction()));
  }

  private void setupUpdateEvents(Download download, Origin origin) {
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.UPDATE_TAB);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        InstallType.UPDATE, AnalyticsManager.Action.INSTALL, AppContext.UPDATE_TAB, origin);
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

  public Completable resumeUpdate(App app) {
    return installManager.getDownload(((UpdateApp) app).getMd5())
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(subscription -> setupDownloadEvents(download)));
  }

  public void cancelUpdate(App app) {
    installManager.removeInstallationFile(((UpdateApp) app).getMd5(),
        ((UpdateApp) app).getPackageName(), ((UpdateApp) app).getVersionCode());
  }

  public void pauseUpdate(App app) {
    installManager.stopInstallation(((UpdateApp) app).getMd5());
  }

  public Completable updateApp(App app) {
    String packageName = ((UpdateApp) app).getPackageName();
    return updatesManager.getUpdate(packageName)
        .flatMap(update -> {
          Download value = downloadFactory.create(update);
          return Observable.just(value);
        })
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(__ -> setupUpdateEvents(download, Origin.UPDATE)))
        .toCompletable();
  }

  public boolean showWarning() {
    return installManager.showWarning();
  }

  public void storeRootAnswer(boolean answer) {
    installManager.rootInstallAllowed(answer);
  }

  public Completable updateAll() {
    return updatesManager.updateAll()
        .first()
        .flatMapIterable(updatesList -> updatesList)
        .flatMap(update -> {
          Download download = downloadFactory.create(update);
          setupUpdateEvents(download, Origin.UPDATE_ALL);
          return Observable.just(download);
        })
        .toList()
        .flatMap(downloads -> installManager.startInstalls(downloads))
        .toCompletable();
  }

  public Observable<Void> excludeUpdate(App app) {
    return updatesManager.excludeUpdate(((UpdateApp) app).getPackageName());
  }

  public void setAppViewAnalyticsEvent() {
    updatesAnalytics.updates(UpdatesAnalytics.OPEN_APP_VIEW);
  }
}

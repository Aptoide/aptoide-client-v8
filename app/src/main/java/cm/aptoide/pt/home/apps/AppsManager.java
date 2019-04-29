package cm.aptoide.pt.home.apps;

import android.content.Context;
import android.content.pm.PackageManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.updates.UpdatesAnalytics;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;

import static cm.aptoide.pt.install.Install.InstallationType.INSTALL;
import static cm.aptoide.pt.install.Install.InstallationType.INSTALLED;
import static cm.aptoide.pt.install.Install.InstallationType.UPDATE;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsManager {

  private final UpdatesManager updatesManager;
  private final InstallManager installManager;
  private final AppMapper appMapper;
  private final DownloadAnalytics downloadAnalytics;
  private final InstallAnalytics installAnalytics;
  private final UpdatesAnalytics updatesAnalytics;
  private final PackageManager packageManager;
  private final Context context;
  private final DownloadFactory downloadFactory;
  private final MoPubAdsManager moPubAdsManager;

  public AppsManager(UpdatesManager updatesManager, InstallManager installManager,
      AppMapper appMapper, DownloadAnalytics downloadAnalytics, InstallAnalytics installAnalytics,
      UpdatesAnalytics updatesAnalytics, PackageManager packageManager, Context context,
      DownloadFactory downloadFactory, MoPubAdsManager moPubAdsManager) {
    this.updatesManager = updatesManager;
    this.installManager = installManager;
    this.appMapper = appMapper;
    this.downloadAnalytics = downloadAnalytics;
    this.installAnalytics = installAnalytics;
    this.updatesAnalytics = updatesAnalytics;
    this.packageManager = packageManager;
    this.context = context;
    this.downloadFactory = downloadFactory;
    this.moPubAdsManager = moPubAdsManager;
  }

  public Observable<List<App>> getUpdatesList(boolean isExcluded) {
    return updatesManager.getUpdatesList(isExcluded, true)
        .distinctUntilChanged()
        .map(updates -> appMapper.mapUpdateToUpdateAppList(updates));
  }

  public Observable<List<App>> getAppcUpgradesList(boolean isExcluded) {
    return updatesManager.getAppcUpgradesList(isExcluded)
        .distinctUntilChanged()
        .map(updates -> appMapper.mapUpdateToUpdateAppList(updates));
  }

  public Observable<List<App>> getUpdateDownloadsList() {
    return installManager.getInstallations()
        .distinctUntilChanged()
        .throttleLast(200, TimeUnit.MILLISECONDS)
        .flatMap(installations -> {
          if (installations == null || installations.isEmpty()) {
            return Observable.empty();
          }
          return Observable.just(installations)
              .flatMapIterable(installs -> installs)
              .filter(install -> install.getType() == UPDATE)
              .flatMap(install -> updatesManager.filterAppcUpgrade(install))
              .toList()
              .map(updatesList -> appMapper.getUpdatesList(updatesList));
        });
  }

  public Observable<List<App>> getAppcUpgradeDownloadsList() {
    return installManager.getInstallations()
        .distinctUntilChanged()
        .throttleLast(200, TimeUnit.MILLISECONDS)
        .flatMap(installations -> {
          if (installations == null || installations.isEmpty()) {
            return Observable.empty();
          }
          return Observable.just(installations)
              .flatMapIterable(installs -> installs)
              .flatMap(install -> updatesManager.filterNonAppcUpgrade(install))
              .toList()
              .map(updatesList -> appMapper.getUpdatesList(updatesList));
        });
  }

  public Observable<List<App>> getInstalledApps() {
    return installManager.fetchInstalled()
        .distinctUntilChanged()
        .flatMapIterable(list -> list)
        .flatMap(item -> updatesManager.filterUpdates(item))
        .toList()
        .map(installeds -> appMapper.mapInstalledToInstalledApps(installeds));
  }

  public Observable<List<App>> getDownloadApps() {
    return installManager.getInstallations()
        .distinctUntilChanged()
        .throttleLast(200, TimeUnit.MILLISECONDS)
        .flatMap(installations -> {
          if (installations == null || installations.isEmpty()) {
            return Observable.just(Collections.emptyList());
          }
          return Observable.just(installations)
              .flatMapIterable(installs -> installs)
              .filter(install -> install.getType() != Install.InstallationType.UPDATE)
              .flatMap(item -> installManager.filterInstalled(item))
              .flatMap(item -> updatesManager.filterAppcUpgrade(item))
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
        .flatMap(download -> moPubAdsManager.getAdsVisibilityStatus()
            .doOnSuccess(status -> setupDownloadEvents(download, status))
            .map(__ -> download))
        .flatMapCompletable(download -> installManager.install(download));
  }

  private void setupDownloadEvents(Download download,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus) {
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.APPS_FRAGMENT);
    downloadAnalytics.installClicked(download.getMd5(), download.getPackageName(),
        AnalyticsManager.Action.INSTALL, offerResponseStatus);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, AppContext.APPS_FRAGMENT, getOrigin(download.getAction()));
  }

  private void setupUpdateEvents(Download download, Origin origin,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus) {
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.APPS_FRAGMENT);
    downloadAnalytics.installClicked(download.getMd5(), download.getPackageName(),
        AnalyticsManager.Action.INSTALL, offerResponseStatus);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, AppContext.APPS_FRAGMENT, origin);
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

  public Completable pauseDownload(App app) {
    return Completable.fromAction(
        () -> installManager.stopInstallation(((DownloadApp) app).getMd5()));
  }

  public Completable resumeUpdate(App app) {
    return installManager.getDownload(((UpdateApp) app).getMd5())
        .flatMapCompletable(download -> installManager.install(download));
  }

  public void cancelUpdate(App app) {
    installManager.removeInstallationFile(((UpdateApp) app).getMd5(),
        ((UpdateApp) app).getPackageName(), ((UpdateApp) app).getVersionCode());
  }

  public Completable pauseUpdate(App app) {
    return Completable.fromAction(
        () -> installManager.stopInstallation(((UpdateApp) app).getMd5()));
  }

  public Completable updateApp(App app, boolean isAppcUpdate) {
    String packageName = ((UpdateApp) app).getPackageName();
    return updatesManager.getUpdate(packageName)
        .flatMap(update -> {
          Download value = downloadFactory.create(update, isAppcUpdate);
          return Observable.just(value);
        })
        .flatMapSingle(download -> moPubAdsManager.getAdsVisibilityStatus()
            .doOnSuccess(status -> setupUpdateEvents(download, Origin.UPDATE, status))
            .map(__ -> download))
        .flatMapCompletable(download -> installManager.install(download))
        .toCompletable();
  }

  public boolean showWarning() {
    return installManager.showWarning();
  }

  public void storeRootAnswer(boolean answer) {
    installManager.rootInstallAllowed(answer);
  }

  public Completable updateAll() {
    return updatesManager.getAllUpdates()
        .first()
        .filter(updatesList -> !updatesList.isEmpty())
        .flatMap(updates -> moPubAdsManager.getAdsVisibilityStatus()
            .flatMapObservable(offerResponseStatus -> Observable.just(offerResponseStatus)
                .map(showAds1 -> updates)
                .flatMapIterable(updatesList -> updatesList)
                .flatMap(update -> Observable.just(downloadFactory.create(update, false))
                    .doOnNext(download1 -> setupUpdateEvents(download1, Origin.UPDATE_ALL,
                        offerResponseStatus))
                    .toList()
                    .flatMap(installManager::startInstalls))))
        .toCompletable();
  }

  public Observable<Void> excludeUpdate(App app) {
    return updatesManager.excludeUpdate(((UpdateApp) app).getPackageName());
  }

  public void setAppViewAnalyticsEvent() {
    updatesAnalytics.updates(UpdatesAnalytics.OPEN_APP_VIEW);
  }

  public Observable<List<App>> getInstalledDownloads() {
    return installManager.fetchInstalled()
        .distinctUntilChanged()
        .flatMapIterable(installedAppsList -> installedAppsList)
        .flatMap(installedApp -> getDownloads(installedApp))
        .toList()
        .map(installedApps -> appMapper.getDownloadApps(installedApps));
  }

  private Observable<Install> getDownloads(Installed installedApp) {
    return installManager.getInstallations()
        .first()
        .flatMap(installations -> {
          if (installations == null || installations.isEmpty()) {
            return Observable.empty();
          }
          return Observable.just(installations)
              .flatMapIterable(installs -> installs)
              .filter(install -> install.getType() == INSTALL || install.getType() == INSTALLED)
              .toList()
              .flatMap(updates -> getMatchingInstalledUpdate(updates, installedApp));
        });
  }

  public Completable refreshAllUpdates() {
    return updatesManager.refreshUpdates();
  }

  private Observable<Install> getMatchingInstalledUpdate(List<Install> updates,
      Installed installedApp) {
    for (Install update : updates) {
      if (installedApp.getPackageName()
          .equals(update.getPackageName())
          && installedApp.getVersionName()
          .equals(update.getVersionName())
          && installedApp.getVersionCode() == update.getVersionCode()) {
        return Observable.just(update);
      }
    }
    return Observable.empty();
  }
}


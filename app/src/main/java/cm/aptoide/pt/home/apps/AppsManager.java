package cm.aptoide.pt.home.apps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Pair;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.promotions.PromotionsManager;
import cm.aptoide.pt.updates.UpdatesAnalytics;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;

import static cm.aptoide.pt.install.Install.InstallationType.UPDATE;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsManager {

  private static final String MIGRATION_PROMOTION = "BONUS_MIGRATION_19";

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
  private final PromotionsManager promotionsManager;

  public AppsManager(UpdatesManager updatesManager, InstallManager installManager,
      AppMapper appMapper, DownloadAnalytics downloadAnalytics, InstallAnalytics installAnalytics,
      UpdatesAnalytics updatesAnalytics, PackageManager packageManager, Context context,
      DownloadFactory downloadFactory, MoPubAdsManager moPubAdsManager,
      PromotionsManager promotionsManager) {
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
    this.promotionsManager = promotionsManager;
  }

  public Observable<List<App>> getUpdatesList(boolean isExcluded) {
    return updatesManager.getUpdatesList(isExcluded, true)
        .distinctUntilChanged()
        .map(updates -> appMapper.mapUpdateToUpdateAppList(updates));
  }

  public Observable<List<App>> getAppcUpgradesList(boolean isExcluded, boolean hasPromotion,
      float appcValue) {
    return updatesManager.getAppcUpgradesList(isExcluded)
        .distinctUntilChanged()
        .map(updates -> appMapper.mapUpdateToUpdateAppcAppList(updates, hasPromotion, appcValue));
  }

  public Observable<List<App>> getExcludedAppcUpgradesList() {
    return getAppcUpgradesList(true, false, 0);
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

  public Observable<Pair<Boolean, Float>> migrationPromotionActive() {
    return promotionsManager.getPromotionApps(MIGRATION_PROMOTION)
        .map(promotions -> new Pair<>(!promotions.isEmpty(),
            !promotions.isEmpty() ? promotions.get(0)
                .getAppcValue() : 0))
        .toObservable();
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
        .doOnNext(installs -> Logger.getInstance()
            .d("Apps", "emit list of installs from getDownloadApps - before throttle"))
        .throttleLast(200, TimeUnit.MILLISECONDS)
        .flatMap(installations -> {
          if (installations == null || installations.isEmpty()) {
            return Observable.just(Collections.emptyList());
          }
          return Observable.just(installations)
              .doOnNext(__ -> Logger.getInstance()
                  .d("Apps", "emit list of installs from getDownloadApps - after throttle"))
              .flatMapIterable(installs -> installs)
              .filter(install -> install.getType() != Install.InstallationType.UPDATE)
              .flatMap(item -> installManager.filterInstalled(item))
              .doOnNext(item -> Logger.getInstance()
                  .d("Apps", "filtered installed - is not installed -> " + item.getPackageName()))
              .flatMap(item -> updatesManager.filterAppcUpgrade(item))
              .doOnNext(item -> Logger.getInstance()
                  .d("Apps", "filtered upgrades - is not upgrade -> " + item.getPackageName()))
              .toList()
              .doOnNext(__ -> Logger.getInstance()
                  .d("Apps", "emit list of installs from getDownloadApps - after toList"))
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
        DownloadAnalytics.AppContext.APPS_FRAGMENT, false);
    downloadAnalytics.installClicked(download.getMd5(), download.getPackageName(),
        AnalyticsManager.Action.INSTALL, offerResponseStatus, false, download.hasAppc());
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, AppContext.APPS_FRAGMENT, getOrigin(download.getAction()),
        false, download.hasAppc());
  }

  private void setupUpdateEvents(Download download, Origin origin,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus, boolean updateAll) {
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.APPS_FRAGMENT, false, updateAll);
    downloadAnalytics.installClicked(download.getMd5(), download.getPackageName(),
        AnalyticsManager.Action.INSTALL, offerResponseStatus, false, download.hasAppc());
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, AppContext.APPS_FRAGMENT, origin, false,
        download.hasAppc());
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
            .doOnSuccess(status -> setupUpdateEvents(download, Origin.UPDATE, status, false))
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
                        offerResponseStatus, true))
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

  public void setMigrationAppViewAnalyticsEvent() {
    updatesAnalytics.updates(UpdatesAnalytics.OPEN_APP_VIEW_MIGRATIOM);
  }

  public Observable<List<App>> getInstalledDownloads() {
    return installManager.getInstalledApps()
        .distinctUntilChanged()
        .map(installedDownloads -> appMapper.getDownloadApps(installedDownloads));
  }

  public Completable refreshAllUpdates() {
    return updatesManager.refreshUpdates();
  }
}


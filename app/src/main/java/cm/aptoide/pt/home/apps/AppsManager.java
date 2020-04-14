package cm.aptoide.pt.home.apps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Pair;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.app.aptoideinstall.AptoideInstallManager;
import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.home.apps.model.AppcUpdateApp;
import cm.aptoide.pt.home.apps.model.DownloadApp;
import cm.aptoide.pt.home.apps.model.InstalledApp;
import cm.aptoide.pt.home.apps.model.StateApp;
import cm.aptoide.pt.home.apps.model.UpdateApp;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.promotions.PromotionsManager;
import cm.aptoide.pt.updates.UpdatesAnalytics;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Single;

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
  private final AptoideInstallManager aptoideInstallManager;

  public AppsManager(UpdatesManager updatesManager, InstallManager installManager,
      AppMapper appMapper, DownloadAnalytics downloadAnalytics, InstallAnalytics installAnalytics,
      UpdatesAnalytics updatesAnalytics, PackageManager packageManager, Context context,
      DownloadFactory downloadFactory, MoPubAdsManager moPubAdsManager,
      PromotionsManager promotionsManager, AptoideInstallManager aptoideInstallManager) {
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
    this.aptoideInstallManager = aptoideInstallManager;
  }

  public Observable<List<UpdateApp>> getUpdatesList() {
    return Observable.combineLatest(getAllUpdatesList(), getUpdateDownloadsList(),
        this::mergeUpdates);
  }

  private List<UpdateApp> mergeUpdates(List<UpdateApp> allUpdates,
      List<UpdateApp> updateDownloads) {
    List<UpdateApp> finalList = new ArrayList<>(allUpdates);
    for (int i = 0; i < finalList.size(); i++) {
      UpdateApp app1 = allUpdates.get(i);
      for (UpdateApp app2 : updateDownloads) {
        if (app1.getMd5()
            .equals(app2.getMd5())) {
          finalList.set(i, app2);
          break;
        }
      }
    }
    return finalList;
  }

  private Observable<List<UpdateApp>> getAllUpdatesList() {
    return updatesManager.getUpdatesList(true)
        .distinctUntilChanged()
        .flatMap(updates -> Observable.from(updates)
            .flatMapSingle(
                update -> aptoideInstallManager.isInstalledWithAptoide(update.getPackageName())
                    .map(isAptoideInstalled -> appMapper.mapUpdateToUpdateApp(update,
                        isAptoideInstalled)), false, 1)
            .toSortedList((updateApp, updateApp2) -> {
              if (updateApp.isInstalledWithAptoide() && !updateApp2.isInstalledWithAptoide()) {
                return -1;
              } else if (!updateApp.isInstalledWithAptoide()
                  && updateApp2.isInstalledWithAptoide()) {
                return 1;
              }
              return 0;
            }))
        .flatMap(list -> aptoideInstallManager.sendImpressionEvent()
            .andThen(Observable.just(list)));
  }

  private Observable<List<UpdateApp>> getUpdateDownloadsList() {
    return installManager.getInstallations()
        .distinctUntilChanged()
        .throttleLast(200, TimeUnit.MILLISECONDS)
        .flatMap(installations -> {
          if (installations == null || installations.isEmpty()) {
            return Observable.just(new ArrayList<>());
          }
          return Observable.just(installations)
              .flatMapIterable(installs -> installs)
              .filter(install -> install.getType() == UPDATE)
              .flatMapSingle(updatesManager::filterAppcUpgrade)
              .filter(upgrade -> upgrade != null)
              .flatMapSingle(
                  install -> aptoideInstallManager.isInstalledWithAptoide(install.getPackageName())
                      .map(isAptoideInstalled -> appMapper.mapInstallToUpdateApp(install,
                          isAptoideInstalled)))
              .toList();
        });
  }

  public Observable<List<AppcUpdateApp>> getAppcUpgradesList() {
    return migrationPromotionActive().flatMap(pair -> updatesManager.getAppcUpgradesList(false)
        .distinctUntilChanged()
        .map(updates -> appMapper.mapUpdateToUpdateAppcAppList(updates, pair.first, pair.second)));
  }

  public Observable<Pair<Boolean, Float>> migrationPromotionActive() {
    return promotionsManager.getPromotionApps(MIGRATION_PROMOTION)
        .map(promotions -> new Pair<>(!promotions.isEmpty(),
            !promotions.isEmpty() ? promotions.get(0)
                .getAppcValue() : 0))
        .toObservable();
  }

  public Observable<List<InstalledApp>> getInstalledApps() {
    return installManager.fetchInstalled()
        .distinctUntilChanged()
        .flatMapIterable(list -> list)
        .flatMapSingle(updatesManager::filterUpdates)
        .filter(update -> update != null)
        .toList()
        .map(appMapper::mapInstalledToInstalledApps);
  }

  public Observable<List<DownloadApp>> getDownloadApps() {
    return installManager.getInstallations()
        .throttleLast(200, TimeUnit.MILLISECONDS)
        .flatMap(installations -> {
          if (installations == null || installations.isEmpty()) {
            return Observable.just(Collections.emptyList());
          }
          return Observable.just(installations)
              .flatMapIterable(installs -> installs)
              .filter(install -> install.getType() != Install.InstallationType.UPDATE)
              .flatMap(installManager::filterInstalled)
              .doOnNext(item -> Logger.getInstance()
                  .d("Apps", "filtered installed - is not installed -> " + item.getPackageName()))
              .flatMapSingle(updatesManager::filterAppcUpgrade)
              .filter(upgrade -> upgrade != null)
              .doOnNext(item -> Logger.getInstance()
                  .d("Apps", "filtered upgrades - is not upgrade -> " + item.getPackageName()))
              .toList()
              .doOnNext(__ -> Logger.getInstance()
                  .d("Apps", "emit list of installs from getDownloadApps - after toList"))
              .map(appMapper::getDownloadApps);
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
            return resumeDownload(app, installationProgress.getType()
                .toString());
          }
        })
        .toCompletable();
  }

  public Completable cancelDownload(App app) {
    return installManager.cancelInstall(((StateApp) app).getMd5(),
        ((StateApp) app).getPackageName(), ((StateApp) app).getVersionCode());
  }

  public Completable resumeDownload(App app, String installType) {
    return installManager.getDownload(((StateApp) app).getMd5())
        .flatMap(download -> moPubAdsManager.getAdsVisibilityStatus()
            .doOnSuccess(status -> setupDownloadEvents(download, status, installType))
            .map(__ -> download))
        .flatMapCompletable(installManager::install);
  }

  private void setupDownloadEvents(RoomDownload download,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus, String installType) {
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.APPS_FRAGMENT, false);
    downloadAnalytics.installClicked(download.getMd5(), download.getPackageName(),
        AnalyticsManager.Action.INSTALL, offerResponseStatus, false, download.hasAppc(),
        download.hasSplits(), download.getTrustedBadge(), null, download.getStoreName(),
        installType);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, AppContext.APPS_FRAGMENT, getOrigin(download.getAction()),
        false, download.hasAppc(), download.hasSplits(), offerResponseStatus.toString(),
        download.getTrustedBadge(), download.getStoreName());
  }

  private void setupUpdateEvents(RoomDownload download, Origin origin,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus, String trustedBadge,
      String tag, String storeName, String installType) {
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.APPS_FRAGMENT, false, origin);
    downloadAnalytics.installClicked(download.getMd5(), download.getPackageName(),
        AnalyticsManager.Action.INSTALL, offerResponseStatus, false, download.hasAppc(),
        download.hasSplits(), trustedBadge, tag, storeName, installType);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, AppContext.APPS_FRAGMENT, origin, false,
        download.hasAppc(), download.hasSplits(), offerResponseStatus.toString(),
        download.getTrustedBadge(), download.getStoreName());
  }

  private Origin getOrigin(int action) {
    switch (action) {
      default:
      case RoomDownload.ACTION_INSTALL:
        return Origin.INSTALL;
      case RoomDownload.ACTION_UPDATE:
        return Origin.UPDATE;
      case RoomDownload.ACTION_DOWNGRADE:
        return Origin.DOWNGRADE;
    }
  }

  public Completable pauseDownload(App app) {
    return installManager.pauseInstall(((StateApp) app).getMd5());
  }

  public Completable updateApp(App app, boolean isAppcUpdate) {
    String packageName = ((UpdateApp) app).getPackageName();
    return updatesManager.getUpdate(packageName)
        .flatMapCompletable(update -> moPubAdsManager.getAdsVisibilityStatus()
            .flatMap(status -> {
              RoomDownload value = downloadFactory.create(update, isAppcUpdate);
              String type = isAppcUpdate ? "update_to_appc" : "update";
              updatesAnalytics.sendUpdateClickedEvent(packageName, update.hasSplits(),
                  update.hasAppc(), false, update.getTrustedBadge(), status.toString()
                      .toLowerCase(), null, update.getStoreName(), type);
              setupUpdateEvents(value, Origin.UPDATE, status, update.getTrustedBadge(), null,
                  update.getStoreName(), "update");
              return Single.just(value);
            })
            .flatMapCompletable(download -> installManager.install(download))
            .andThen(aptoideInstallManager.sendConversionEvent()))
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
            .doOnSuccess(__ -> updatesAnalytics.sendUpdateAllClickEvent())
            .flatMapObservable(offerResponseStatus -> Observable.just(offerResponseStatus)
                .map(showAds1 -> updates)
                .flatMapIterable(updatesList -> updatesList)
                .flatMap(update -> Observable.just(downloadFactory.create(update, false))
                    .doOnNext(download1 -> {
                      updatesAnalytics.sendUpdateClickedEvent(update.getPackageName(),
                          update.hasSplits(), update.hasAppc(), false, update.getTrustedBadge(),
                          offerResponseStatus.toString()
                              .toLowerCase(), null, update.getStoreName(), "update_all");
                      setupUpdateEvents(download1, Origin.UPDATE_ALL, offerResponseStatus, null,
                          update.getTrustedBadge(), update.getStoreName(), "update_all");
                    }))
                .toList()
                .flatMap(installManager::startInstalls)))
        .toCompletable();
  }

  public Completable excludeUpdate(App app) {
    return updatesManager.excludeUpdate(((UpdateApp) app).getPackageName());
  }

  public void setAppViewAnalyticsEvent() {
    updatesAnalytics.updates(UpdatesAnalytics.OPEN_APP_VIEW);
  }

  public void setMigrationAppViewAnalyticsEvent() {
    updatesAnalytics.updates(UpdatesAnalytics.OPEN_APP_VIEW_MIGRATIOM);
  }

  public Observable<List<DownloadApp>> getInstalledDownloads() {
    return installManager.getInstalledApps()
        .distinctUntilChanged()
        .map(installedDownloads -> appMapper.getDownloadApps(installedDownloads));
  }

  public Completable refreshAllUpdates() {
    return updatesManager.refreshUpdates();
  }
}


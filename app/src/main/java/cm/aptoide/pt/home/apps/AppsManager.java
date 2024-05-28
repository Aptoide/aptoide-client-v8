package cm.aptoide.pt.home.apps;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.aab.DynamicSplitsManager;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.app.aptoideinstall.AptoideInstallManager;
import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.download.SplitAnalyticsMapper;
import cm.aptoide.pt.home.apps.model.DownloadApp;
import cm.aptoide.pt.home.apps.model.InstalledApp;
import cm.aptoide.pt.home.apps.model.StateApp;
import cm.aptoide.pt.home.apps.model.UpdateApp;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.notification.UpdatesNotificationManager;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.updates.UpdatesAnalytics;
import cm.aptoide.pt.utils.AptoideUtils;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import rx.Completable;
import rx.Observable;
import rx.Single;

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
  private final AptoideInstallManager aptoideInstallManager;
  private final UpdatesNotificationManager updatesNotificationManager;
  private final SharedPreferences secureSharedPreferences;
  private final DynamicSplitsManager dynamicSplitsManager;
  private final SplitAnalyticsMapper splitAnalyticsMapper;

  public AppsManager(UpdatesManager updatesManager, InstallManager installManager,
      AppMapper appMapper, DownloadAnalytics downloadAnalytics, InstallAnalytics installAnalytics,
      UpdatesAnalytics updatesAnalytics, PackageManager packageManager, Context context,
      DownloadFactory downloadFactory, MoPubAdsManager moPubAdsManager,
      AptoideInstallManager aptoideInstallManager,
      UpdatesNotificationManager updatesNotificationManager,
      SharedPreferences secureSharedPreferences, DynamicSplitsManager dynamicSplitsManager,
      SplitAnalyticsMapper splitAnalyticsMapper) {
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
    this.aptoideInstallManager = aptoideInstallManager;
    this.updatesNotificationManager = updatesNotificationManager;
    this.secureSharedPreferences = secureSharedPreferences;
    this.dynamicSplitsManager = dynamicSplitsManager;
    this.splitAnalyticsMapper = splitAnalyticsMapper;
  }

  public Observable<List<UpdateApp>> getUpdatesList() {
    if (SecurePreferences.isUpdatesFirstLoad(secureSharedPreferences)) {
      return updatesManager.refreshUpdates()
          .startWith(updateFirstLoadUpdatesSettings())
          .andThen(startUpdatesNotification())
          .andThen(observeAllUpdates());
    } else {
      return observeAllUpdates();
    }
  }

  private Observable<List<UpdateApp>> observeAllUpdates() {
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
    return updatesManager.getUpdatesList()
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
            }));
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
              .flatMapSingle(
                  install -> aptoideInstallManager.isInstalledWithAptoide(install.getPackageName())
                      .map(isAptoideInstalled -> appMapper.mapInstallToUpdateApp(install,
                          isAptoideInstalled)))
              .toList();
        });
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
              .filter(install -> install.getType() != Install.InstallationType.UPDATE
                  || (install.getType() == Install.InstallationType.UPDATE
                  && install.getState() == Install.InstallationStatus.UNINSTALLED))
              .flatMapSingle(installManager::filterInstalled)
              .filter(installed -> installed != null)
              .doOnNext(item -> Logger.getInstance()
                  .d("Apps", "filtered installed - is not installed -> "
                      + item.getPackageName()
                      + " "
                      + item.getMd5()
                      + " "
                      + item.getVersionName()))
              .toList()
              .doOnNext(__ -> Logger.getInstance()
                  .d("Apps", "emit list of installs from getDownloadApps - after toList"))
              .map(appMapper::getDownloadApps);
        });
  }

  private void handleNotEnoughSpaceError(String md5) {
    downloadAnalytics.sendNotEnoughSpaceError(md5);
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
        .doOnSuccess(download -> setupDownloadEvents(download, installType))
        .flatMapCompletable(installManager::install);
  }

  private void setupDownloadEvents(RoomDownload download, String installType) {
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.APPS_FRAGMENT, false);
    downloadAnalytics.installClicked(download.getMd5(), download.getPackageName(),
        download.getVersionCode(), AnalyticsManager.Action.INSTALL, false,
        download.hasAppc(), download.hasSplits(), download.getTrustedBadge(), null,
        download.getStoreName(), installType, download.hasObbs(),
        splitAnalyticsMapper.getSplitTypesAsString(download.getSplits()),
        download.getStoreName().equals("catappult"), "");
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, DownloadAnalytics.AppContext.APPS_FRAGMENT,
        getOrigin(download.getAction()), false, download.hasAppc(), download.hasSplits(),
        download.getTrustedBadge(), download.getStoreName(),
        download.hasObbs(), splitAnalyticsMapper.getSplitTypesAsString(download.getSplits()),
        download.getStoreName().equals("catappult"), "");
  }

  private void setupUpdateEvents(RoomDownload download, Origin origin,
      String trustedBadge,
      String tag, String storeName, String installType) {
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.APPS_FRAGMENT, false, origin);
    downloadAnalytics.installClicked(download.getMd5(), download.getPackageName(),
        download.getVersionCode(), AnalyticsManager.Action.INSTALL, false,
        download.hasAppc(), download.hasSplits(), trustedBadge, tag, storeName, installType,
        download.hasObbs(), splitAnalyticsMapper.getSplitTypesAsString(download.getSplits()),
        download.getStoreName().equals("catappult"), "");
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, DownloadAnalytics.AppContext.APPS_FRAGMENT, origin, false,
        download.hasAppc(), download.hasSplits(),
        download.getTrustedBadge(), download.getStoreName(), download.hasObbs(),
        splitAnalyticsMapper.getSplitTypesAsString(download.getSplits()),
        download.getStoreName().equals("catappult"), "");
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

  public Completable updateApp(App app) {
    String packageName = ((UpdateApp) app).getPackageName();
    return updatesManager.getUpdate(packageName)
        .flatMap(update -> RxJavaInterop.toV1Single(
                dynamicSplitsManager.getAppSplitsByMd5(update.getMd5()))
            .flatMap(dynamicSplitsModel -> {
              RoomDownload value = downloadFactory.create(update, false,
                  dynamicSplitsModel.getDynamicSplitsList());
              String type = "update";
              updatesAnalytics.sendUpdateClickedEvent(packageName, update.hasSplits(),
                  update.hasAppc(), false, update.getTrustedBadge(), null, update.getStoreName(),
                  type,
                  update.getMainObbMd5() != null && !update.getMainObbMd5()
                      .isEmpty());
              setupUpdateEvents(value, Origin.UPDATE, update.getTrustedBadge(), null,
                  update.getStoreName(), "update");
              return Single.just(value);
            }))
        .flatMapCompletable(download -> installManager.install(download))
        .onErrorComplete();
  }

  public boolean showWarning() {
    return installManager.showWarning();
  }

  public void storeRootAnswer(boolean answer) {
    installManager.rootInstallAllowed(answer);
  }

  public Completable updateAll() {
    return updatesManager.getUpdatesList()
        .first()
        .filter(updatesList -> !updatesList.isEmpty())
        .doOnNext(__ -> updatesAnalytics.sendUpdateAllClickEvent())
        .flatMapIterable(updatesList -> updatesList)
        .flatMap(update -> RxJavaInterop.toV1Single(
                dynamicSplitsManager.getAppSplitsByMd5(update.getMd5()))
            .flatMapObservable(dynamicSplitsModel -> Observable.just(
                    downloadFactory.create(update, false,
                        dynamicSplitsModel.getDynamicSplitsList()))
                .doOnNext(download1 -> {
                  updatesAnalytics.sendUpdateClickedEvent(update.getPackageName(),
                      update.hasSplits(), update.hasAppc(), false, update.getTrustedBadge(), null,
                      update.getStoreName(), "update_all",
                      update.getMainObbMd5() != null && !update.getMainObbMd5()
                          .isEmpty());
                  setupUpdateEvents(download1, Origin.UPDATE_ALL, null,
                      update.getTrustedBadge(), update.getStoreName(), "update_all");
                })))
        .toList()
        .flatMap(installManager::startInstalls)
        .toCompletable();
  }

  public Completable excludeUpdate(App app) {
    return updatesManager.excludeUpdate(((UpdateApp) app).getPackageName());
  }

  public void setAppViewAnalyticsEvent() {
    updatesAnalytics.updates(UpdatesAnalytics.OPEN_APP_VIEW);
  }

  public Completable refreshAllUpdates() {
    return updatesManager.refreshUpdates();
  }

  private Completable startUpdatesNotification() {
    return updatesNotificationManager.setUpNotification();
  }

  @NotNull private Completable updateFirstLoadUpdatesSettings() {
    return Completable.fromAction(
        () -> SecurePreferences.setUpdatesFirstLoad(false, secureSharedPreferences));
  }

  public Observable<List<String>> observeOutOfSpaceApps() {
    return installManager.getDownloadOutOfSpaceMd5List()
        .distinctUntilChanged()
        .doOnNext(
            installList -> handleNotEnoughSpaceError(installList.get(installList.size() - 1)));
  }
}


package cm.aptoide.pt.home.apps;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;

import static cm.aptoide.pt.ads.WalletAdsOfferManager.OfferResponseStatus.NO_ADS;

public class SeeMoreAppcManager {

  private final UpdatesManager updatesManager;
  private final InstallManager installManager;
  private final AppMapper appMapper;
  private final DownloadFactory downloadFactory;
  private final DownloadAnalytics downloadAnalytics;
  private final InstallAnalytics installAnalytics;

  public SeeMoreAppcManager(UpdatesManager updatesManager, InstallManager installManager,
      AppMapper appMapper, DownloadFactory downloadFactory, DownloadAnalytics downloadAnalytics,
      InstallAnalytics installAnalytics) {
    this.updatesManager = updatesManager;
    this.installManager = installManager;
    this.appMapper = appMapper;
    this.downloadFactory = downloadFactory;
    this.downloadAnalytics = downloadAnalytics;
    this.installAnalytics = installAnalytics;
  }

  public Observable<List<App>> getAppcUpgradesList(boolean isExcluded) {
    return updatesManager.getAppcUpgradesList(isExcluded)
        .distinctUntilChanged()
        .map(updates -> appMapper.mapUpdateToUpdateAppList(updates));
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

  public Completable refreshAllUpdates() {
    return updatesManager.refreshUpdates();
  }

  public boolean showWarning() {
    return installManager.showWarning();
  }

  public void storeRootAnswer(boolean answer) {
    installManager.rootInstallAllowed(answer);
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

  public Completable updateApp(App app) {
    String packageName = ((UpdateApp) app).getPackageName();
    return updatesManager.getUpdate(packageName)
        .flatMap(update -> {
          Download value = downloadFactory.create(update, true);
          return Observable.just(value);
        })
        .doOnNext(download -> {
          setupUpdateEvents(download, Origin.UPDATE, NO_ADS);
        })
        .flatMapCompletable(download -> installManager.install(download, true))
        .toCompletable();
  }

  private void setupUpdateEvents(Download download, Origin origin,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus) {
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.APPS_MIGRATOR_SEE_MORE);
    downloadAnalytics.installClicked(download.getMd5(), download.getPackageName(),
        AnalyticsManager.Action.INSTALL, offerResponseStatus);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, AppContext.APPS_MIGRATOR_SEE_MORE, origin);
  }
}

package cm.aptoide.pt.app.view.googleplayservices;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.app.DownloadStateParser;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.notification.NotificationAnalytics;
import java.util.List;
import rx.Completable;
import rx.Observable;

public class PlayServicesManager {
  private InstallManager installManager;
  private DownloadFactory downloadFactory;
  private DownloadStateParser downloadStateParser;
  private InstalledRepository installedRepository;

  private DownloadAnalytics downloadAnalytics;
  private NotificationAnalytics notificationAnalytics;
  private InstallAnalytics installAnalytics;

  public PlayServicesManager(InstallManager installManager, DownloadFactory downloadFactory,
      DownloadStateParser downloadStateParser, InstalledRepository installedRepository,
      DownloadAnalytics downloadAnalytics, NotificationAnalytics notificationAnalytics,
      InstallAnalytics installAnalytics) {
    this.installManager = installManager;
    this.downloadFactory = downloadFactory;
    this.downloadStateParser = downloadStateParser;
    this.installedRepository = installedRepository;
    this.downloadAnalytics = downloadAnalytics;
    this.notificationAnalytics = notificationAnalytics;
    this.installAnalytics = installAnalytics;
  }

  public Completable downloadApps(List<PlayServicesApp> apps) {
    return Observable.just(apps)
        .flatMapIterable(list -> list)
        .flatMap(app -> Observable.just(downloadFactory.create(
            downloadStateParser.parseDownloadAction(DownloadModel.Action.INSTALL), app.getAppName(),
            app.getPackageName(), app.getMd5sum(), app.getIcon(), app.getVersionName(),
            (int) app.getVersionCode(), app.getPath(), app.getPathAlt(), app.getObb(), false,
            app.getSize()))
            .doOnNext(
                download -> setupDownloadEvents(download, DownloadModel.Action.INSTALL, app.getId(),
                    app.getPackageName(), app.getDeveloper())))
        .flatMapCompletable(download -> installManager.install(download))
        .toList()
        .toCompletable();
  }

  private void setupDownloadEvents(Download download, DownloadModel.Action downloadAction, long id,
      String packageName, String developer) {
    int campaignId = notificationAnalytics.getCampaignId(download.getPackageName(), id);
    String abTestGroup = notificationAnalytics.getAbTestingGroup(download.getPackageName(), id);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, AppContext.WALLET_INSTALL_ACTIVITY,
        downloadStateParser.getOrigin(download.getAction()), campaignId, abTestGroup,
        downloadAction != null && downloadAction == DownloadModel.Action.MIGRATE,
        download.hasAppc());

    downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
        DownloadAnalytics.AppContext.WALLET_INSTALL_ACTIVITY, AnalyticsManager.Action.CLICK, false);
    if (downloadAction == DownloadModel.Action.INSTALL) {
      downloadAnalytics.installClicked(download.getMd5(), download.getPackageName(),
          AnalyticsManager.Action.CLICK, WalletAdsOfferManager.OfferResponseStatus.NO_ADS, false,
          download.hasAppc());
    }
  }
}

package cm.aptoide.pt.promotions;

import android.support.annotation.NonNull;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.app.DownloadStateParser;
import cm.aptoide.pt.appview.PreferencesManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.notification.NotificationAnalytics;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;

public class PromotionsManager {

  private final PromotionViewAppMapper promotionViewAppMapper;
  private final InstallManager installManager;
  private final DownloadFactory downloadFactory;
  private final DownloadStateParser downloadStateParser;
  private final PromotionsAnalytics promotionsAnalytics;
  private final NotificationAnalytics notificationAnalytics;
  private final InstallAnalytics installAnalytics;
  private final PreferencesManager preferencesManager;

  public PromotionsManager(PromotionViewAppMapper promotionViewAppMapper,
      InstallManager installManager, DownloadFactory downloadFactory,
      DownloadStateParser downloadStateParser, PromotionsAnalytics promotionsAnalytics,
      NotificationAnalytics notificationAnalytics, InstallAnalytics installAnalytics,
      PreferencesManager preferencesManager) {
    this.promotionViewAppMapper = promotionViewAppMapper;
    this.installManager = installManager;
    this.downloadFactory = downloadFactory;
    this.downloadStateParser = downloadStateParser;
    this.promotionsAnalytics = promotionsAnalytics;
    this.notificationAnalytics = notificationAnalytics;
    this.installAnalytics = installAnalytics;
    this.preferencesManager = preferencesManager;
  }

  public Observable<List<PromotionApp>> getPromotionApps() {
    return Observable.just(getPromotionAppsMocked());
  }

  @NonNull private List<PromotionApp> getPromotionAppsMocked() {
    List<PromotionApp> promotionAppList = new ArrayList<>();

    promotionAppList.add(new PromotionApp("Ana's app", "cm.aptoide.pt.ana", 123,
        "http://pool.apk.aptoide.com/lordballiwns/com-facebook-orca-132958908-42161891-bfb0e8f4a51fcbaa16f1840322eb232a.apk",
        "http://pool.apk.aptoide.com/lordballiwns/alt/Y29tLWZhY2Vib29rLW9yY2EtMTMyOTU4OTA4LTQyMTYxODkxLWJmYjBlOGY0YTUxZmNiYWExNmYxODQwMzIyZWIyMzJh.apk",
        "http://pool.img.aptoide.com/lordballiwns/76e0376928b8393227a150fbed5d6b4a_icon.png",
        PromotionApp.PromotionAppState.DOWNLOAD, "This app belongs to ana. It is an app.", 123133,
        4.2f, 123012, "anamd5", 12314, true, "ana version", null));
    promotionAppList.add(new PromotionApp("Joao's app", "cm.aptoide.pt.joao", 123,
        "http://pool.apk.aptoide.com/bds-store/nzt-metal-shooter-commando-47-41200964-0e13c87fc172d3fa7ac0392ec12e72df.apk",
        "http://pool.apk.aptoide.com/bds-store/alt/bnp0LW1ldGFsLXNob290ZXItY29tbWFuZG8tNDctNDEyMDA5NjQtMGUxM2M4N2ZjMTcyZDNmYTdhYzAzOTJlYzEyZTcyZGY.apk",
        "http://pool.img.aptoide.com/bds-store/8335ae2d104ce4dcbfec66fc07c1e7ce_icon.png",
        PromotionApp.PromotionAppState.DOWNLOAD, "This app belongs to Joao. It is an app.", 12323,
        4.2f, 123123, "joaomd5", 123122, false, "joao version", null));

    return promotionAppList;
  }

  public Observable<PromotionViewApp> getDownload(PromotionApp promotionApp) {
    return installManager.getInstall(promotionApp.getMd5(), promotionApp.getPackageName(),
        promotionApp.getVersionCode())
        .map(install -> promotionViewAppMapper.mapInstallToPromotionApp(install, promotionApp));
  }

  public boolean shouldShowRootInstallWarningPopup() {
    return installManager.showWarning();
  }

  public void allowRootInstall(Boolean answer) {
    installManager.rootInstallAllowed(answer);
  }

  public Completable downloadApp(PromotionViewApp promotionViewApp) {
    increaseInstallClick();
    return Observable.just(downloadFactory.create(downloadStateParser.parseDownloadAction(
        promotionViewApp.getDownloadModel()
            .getAction()), promotionViewApp.getName(), promotionViewApp.getPackageName(),
        promotionViewApp.getMd5(), promotionViewApp.getAppIcon(), promotionViewApp.getVersionName(),
        promotionViewApp.getVersionCode(), promotionViewApp.getDownloadPath(),
        promotionViewApp.getAlternativePath(), promotionViewApp.getObb()))
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(__ -> setupDownloadEvents(download, promotionViewApp.getPackageName(),
                promotionViewApp.getAppId())))
        .toCompletable();
  }

  private void increaseInstallClick() {
    preferencesManager.increaseNotLoggedInInstallClicks();
  }

  private void setupDownloadEvents(Download download, String packageName, long appId) {
    int campaignId = notificationAnalytics.getCampaignId(packageName, appId);
    String abTestGroup = notificationAnalytics.getAbTestingGroup(packageName, appId);
    promotionsAnalytics.setupDownloadEvents(download, campaignId, abTestGroup,
        AnalyticsManager.Action.CLICK);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        downloadStateParser.getInstallType(download.getAction()), AnalyticsManager.Action.INSTALL,
        AppContext.EDITORIAL, downloadStateParser.getOrigin(download.getAction()), campaignId,
        abTestGroup);
  }

  public Completable pauseDownload(String md5) {
    return Completable.fromAction(() -> installManager.stopInstallation(md5));
  }
}
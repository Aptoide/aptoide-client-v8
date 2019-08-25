package cm.aptoide.pt.editorial;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.app.DownloadStateParser;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.reactions.ReactionsManager;
import cm.aptoide.pt.reactions.network.LoadReactionModel;
import cm.aptoide.pt.reactions.network.ReactionsResponse;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by D01 on 27/08/2018.
 */

public class EditorialManager {

  private final EditorialRepository editorialRepository;
  private final String cardId;
  private final InstallManager installManager;
  private final DownloadFactory downloadFactory;
  private final NotificationAnalytics notificationAnalytics;
  private final InstallAnalytics installAnalytics;
  private final EditorialAnalytics editorialAnalytics;
  private final ReactionsManager reactionsManager;
  private final MoPubAdsManager moPubAdsManager;
  private DownloadStateParser downloadStateParser;

  public EditorialManager(EditorialRepository editorialRepository, String cardId,
      InstallManager installManager, DownloadFactory downloadFactory,
      DownloadStateParser downloadStateParser, NotificationAnalytics notificationAnalytics,
      InstallAnalytics installAnalytics, EditorialAnalytics editorialAnalytics,
      ReactionsManager reactionsManager, MoPubAdsManager moPubAdsManager) {

    this.editorialRepository = editorialRepository;
    this.cardId = cardId;
    this.installManager = installManager;
    this.downloadFactory = downloadFactory;
    this.downloadStateParser = downloadStateParser;
    this.notificationAnalytics = notificationAnalytics;
    this.installAnalytics = installAnalytics;
    this.editorialAnalytics = editorialAnalytics;
    this.reactionsManager = reactionsManager;
    this.moPubAdsManager = moPubAdsManager;
  }

  public Single<EditorialViewModel> loadEditorialViewModel() {
    return editorialRepository.loadEditorialViewModel(cardId);
  }

  public boolean shouldShowRootInstallWarningPopup() {
    return installManager.showWarning();
  }

  public void allowRootInstall(Boolean answer) {
    installManager.rootInstallAllowed(answer);
  }

  public Completable downloadApp(EditorialDownloadEvent editorialDownloadEvent) {
    return Observable.just(downloadFactory.create(
        downloadStateParser.parseDownloadAction(editorialDownloadEvent.getAction()),
        editorialDownloadEvent.getAppName(), editorialDownloadEvent.getPackageName(),
        editorialDownloadEvent.getMd5(), editorialDownloadEvent.getIcon(),
        editorialDownloadEvent.getVerName(), editorialDownloadEvent.getVerCode(),
        editorialDownloadEvent.getPath(), editorialDownloadEvent.getPathAlt(),
        editorialDownloadEvent.getObb(), false, editorialDownloadEvent.getSize(), null, null))
        .flatMapSingle(download -> moPubAdsManager.getAdsVisibilityStatus()
            .doOnSuccess(offerResponseStatus -> setupDownloadEvents(download,
                editorialDownloadEvent.getPackageName(), editorialDownloadEvent.getAppId(),
                offerResponseStatus))
            .map(__ -> download))
        .flatMapCompletable(download -> installManager.install(download))
        .toCompletable();
  }

  private void setupDownloadEvents(Download download, String packageName, long appId,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus) {
    int campaignId = notificationAnalytics.getCampaignId(packageName, appId);
    String abTestGroup = notificationAnalytics.getAbTestingGroup(packageName, appId);
    editorialAnalytics.setupDownloadEvents(download, campaignId, abTestGroup,
        AnalyticsManager.Action.CLICK, offerResponseStatus);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, AppContext.EDITORIAL,
        downloadStateParser.getOrigin(download.getAction()), campaignId, abTestGroup, false,
        download.hasAppc());
  }

  public Observable<EditorialDownloadModel> loadDownloadModel(String md5, String packageName,
      int versionCode, boolean paidApp, GetAppMeta.Pay pay, int position) {
    return installManager.getInstall(md5, packageName, versionCode)
        .map(install -> new EditorialDownloadModel(
            downloadStateParser.parseDownloadType(install.getType(), paidApp,
                pay != null && pay.isPaid(), false), install.getProgress(),
            downloadStateParser.parseDownloadState(install.getState()), pay, position));
  }

  public Completable pauseDownload(String md5) {
    return Completable.fromAction(() -> installManager.stopInstallation(md5));
  }

  public Completable resumeDownload(String md5, String packageName, long appId) {
    return installManager.getDownload(md5)
        .flatMap(download -> moPubAdsManager.getAdsVisibilityStatus()
            .doOnSuccess(offerResponseStatus -> setupDownloadEvents(download, packageName, appId,
                offerResponseStatus))
            .map(__ -> download))
        .flatMapCompletable(download -> installManager.install(download));
  }

  public Completable cancelDownload(String md5, String packageName, int versionCode) {
    return Completable.fromAction(
        () -> installManager.removeInstallationFile(md5, packageName, versionCode));
  }

  public Single<LoadReactionModel> loadReactionModel(String cardId, String groupId) {
    return reactionsManager.loadReactionModel(cardId, groupId);
  }

  public Single<ReactionsResponse> setReaction(String cardId, String groupId, String reaction) {
    return reactionsManager.setReaction(cardId, groupId, reaction);
  }

  public Single<ReactionsResponse> deleteReaction(String cardId, String groupId) {
    return reactionsManager.deleteReaction(cardId, groupId);
  }

  public Single<Boolean> isFirstReaction(String cardId, String groupId) {
    return reactionsManager.isFirstReaction(cardId, groupId);
  }
}

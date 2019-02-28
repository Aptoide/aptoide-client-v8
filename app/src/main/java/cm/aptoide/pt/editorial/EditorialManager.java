package cm.aptoide.pt.editorial;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.app.DownloadStateParser;
import cm.aptoide.pt.appview.PreferencesManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.notification.NotificationAnalytics;
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
  private final PreferencesManager preferencesManager;
  private final DownloadFactory downloadFactory;
  private final NotificationAnalytics notificationAnalytics;
  private final InstallAnalytics installAnalytics;
  private final EditorialAnalytics editorialAnalytics;
  private DownloadStateParser downloadStateParser;

  public EditorialManager(EditorialRepository editorialRepository, String cardId,
      InstallManager installManager, PreferencesManager preferencesManager,
      DownloadFactory downloadFactory, DownloadStateParser downloadStateParser,
      NotificationAnalytics notificationAnalytics, InstallAnalytics installAnalytics,
      EditorialAnalytics editorialAnalytics) {

    this.editorialRepository = editorialRepository;
    this.cardId = cardId;
    this.installManager = installManager;
    this.preferencesManager = preferencesManager;
    this.downloadFactory = downloadFactory;
    this.downloadStateParser = downloadStateParser;
    this.notificationAnalytics = notificationAnalytics;
    this.installAnalytics = installAnalytics;
    this.editorialAnalytics = editorialAnalytics;
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
    increaseInstallClick();
    return Observable.just(downloadFactory.create(
        downloadStateParser.parseDownloadAction(editorialDownloadEvent.getAction()),
        editorialDownloadEvent.getAppName(), editorialDownloadEvent.getPackageName(),
        editorialDownloadEvent.getMd5(), editorialDownloadEvent.getIcon(),
        editorialDownloadEvent.getVerName(), editorialDownloadEvent.getVerCode(),
        editorialDownloadEvent.getPath(), editorialDownloadEvent.getPathAlt(),
        editorialDownloadEvent.getObb(), false))
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(
                __ -> setupDownloadEvents(download, editorialDownloadEvent.getPackageName(),
                    editorialDownloadEvent.getAppId())))
        .toCompletable();
  }

  private void increaseInstallClick() {
    preferencesManager.increaseNotLoggedInInstallClicks();
  }

  private void setupDownloadEvents(Download download, String packageName, long appId) {
    int campaignId = notificationAnalytics.getCampaignId(packageName, appId);
    String abTestGroup = notificationAnalytics.getAbTestingGroup(packageName, appId);
    editorialAnalytics.setupDownloadEvents(download, campaignId, abTestGroup,
        AnalyticsManager.Action.CLICK);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, AppContext.EDITORIAL,
        downloadStateParser.getOrigin(download.getAction()), campaignId, abTestGroup);
  }

  public Observable<EditorialDownloadModel> loadDownloadModel(String md5, String packageName,
      int versionCode, boolean paidApp, GetAppMeta.Pay pay, int position) {
    return installManager.getInstall(md5, packageName, versionCode)
        .map(install -> new EditorialDownloadModel(
            downloadStateParser.parseDownloadType(install.getType(), paidApp,
                pay != null && pay.isPaid()), install.getProgress(),
            downloadStateParser.parseDownloadState(install.getState()), pay, position));
  }

  public Completable pauseDownload(String md5) {
    return Completable.fromAction(() -> installManager.stopInstallation(md5));
  }

  public Completable resumeDownload(String md5, String packageName, long appId) {
    return installManager.getDownload(md5)
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(__ -> setupDownloadEvents(download, packageName, appId)));
  }

  public Completable cancelDownload(String md5, String packageName, int versionCode) {
    return Completable.fromAction(
        () -> installManager.removeInstallationFile(md5, packageName, versionCode));
  }
}

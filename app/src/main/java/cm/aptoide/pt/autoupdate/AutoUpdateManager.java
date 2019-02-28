package cm.aptoide.pt.autoupdate;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallManager;
import rx.Observable;
import rx.Single;

public class AutoUpdateManager {
  private final DownloadFactory downloadFactory;
  private final PermissionManager permissionManager;
  private final InstallManager installManager;
  private final DownloadAnalytics downloadAnalytics;
  private final int localVersionCode;
  private final AutoUpdateRepository autoUpdateRepository;
  private final int localVersionSdk;

  public AutoUpdateManager(DownloadFactory downloadFactory, PermissionManager permissionManager,
      InstallManager installManager, DownloadAnalytics downloadAnalytics, int localVersionCode,
      AutoUpdateRepository autoUpdateRepository, int localVersionSdk) {
    this.downloadFactory = downloadFactory;
    this.permissionManager = permissionManager;
    this.installManager = installManager;
    this.downloadAnalytics = downloadAnalytics;
    this.localVersionCode = localVersionCode;
    this.autoUpdateRepository = autoUpdateRepository;
    this.localVersionSdk = localVersionSdk;
  }

  private Single<AutoUpdateModel> loadAutoUpdateModel() {
    return autoUpdateRepository.loadFreshAutoUpdateModel()
        .flatMap(autoUpdateModel -> {
          if (autoUpdateModel.hasError()) {
            return Single.error(new Throwable(autoUpdateModel.getError()
                .toString()));
          }
          if (autoUpdateModel.getVersionCode() > localVersionCode
              && localVersionSdk >= Integer.parseInt(autoUpdateModel.getMinSdk())) {
            autoUpdateModel = new AutoUpdateModel(autoUpdateModel, true);
          }
          return Single.just(autoUpdateModel);
        });
  }

  public Observable<Boolean> shouldUpdate() {
    return loadAutoUpdateModel().toObservable()
        .map(AutoUpdateModel::shouldUpdate);
  }

  public Observable<Void> requestPermissions(PermissionService permissionService) {
    return permissionManager.requestDownloadAccess(permissionService)
        .flatMap(permissionGranted -> permissionManager.requestExternalStoragePermission(
            permissionService));
  }

  public Observable<Install> startUpdate() {
    return getAutoUpdateModel().flatMap(autoUpdateModel -> Observable.just(
        downloadFactory.create(autoUpdateModel.getMd5(), autoUpdateModel.getVersionCode(),
            autoUpdateModel.getPackageName(), autoUpdateModel.getUri(), false))
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(
                __ -> downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
                    DownloadAnalytics.AppContext.AUTO_UPDATE))))
        .toCompletable()
        .andThen(getInstall());
  }

  private Observable<AutoUpdateModel> getAutoUpdateModel() {
    return autoUpdateRepository.loadAutoUpdateModel()
        .toObservable();
  }

  private Observable<Install> getInstall() {
    return getAutoUpdateModel().flatMap(
        autoUpdateViewModel -> installManager.getInstall(autoUpdateViewModel.getMd5(),
            autoUpdateViewModel.getPackageName(), autoUpdateViewModel.getVersionCode())
            .first(Install::hasDownloadStarted));
  }
}

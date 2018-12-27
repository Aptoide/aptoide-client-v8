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

  public Single<AutoUpdateViewModel> loadAutoUpdateModel() {
    return autoUpdateRepository.loadFreshAutoUpdateViewModel()
        .flatMap(autoUpdateViewModel -> {
          if (autoUpdateViewModel.hasError()) {
            return Single.error(new Throwable(autoUpdateViewModel.getError()
                .toString()));
          }
          if (autoUpdateViewModel.getVersionCode() > localVersionCode
              && localVersionSdk >= Integer.parseInt(autoUpdateViewModel.getMinSdk())) {
            autoUpdateViewModel = new AutoUpdateViewModel(autoUpdateViewModel, true);
          }
          return Single.just(autoUpdateViewModel);
        });
  }

  public Observable<Void> requestPermissions(PermissionService permissionService) {
    return permissionManager.requestDownloadAccess(permissionService)
        .flatMap(permissionGranted -> permissionManager.requestExternalStoragePermission(
            permissionService));
  }

  public Observable<Install> startUpdate(AutoUpdateViewModel autoUpdateViewModel) {
    return Observable.just(
        downloadFactory.create(autoUpdateViewModel.getMd5(), autoUpdateViewModel.getVersionCode(),
            autoUpdateViewModel.getPackageName(), autoUpdateViewModel.getUri()))
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(
                __ -> downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
                    DownloadAnalytics.AppContext.AUTO_UPDATE)))
        .toCompletable()
        .andThen(getInstall(autoUpdateViewModel));
  }

  public Single<AutoUpdateViewModel> getAutoUpdateModel() {
    return autoUpdateRepository.loadAutoUpdateViewModel();
  }

  private Observable<Install> getInstall(AutoUpdateViewModel autoUpdateViewModel) {
    return installManager.getInstall(autoUpdateViewModel.getMd5(),
        autoUpdateViewModel.getPackageName(), autoUpdateViewModel.getVersionCode())
        .first(install -> install.getState() != Install.InstallationStatus.INSTALLING);
  }
}

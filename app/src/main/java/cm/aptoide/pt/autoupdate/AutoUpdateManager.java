package cm.aptoide.pt.autoupdate;

import android.os.Build;
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
  //TODO 11/12/18 This class is incomplete
  private final DownloadFactory downloadFactory;
  private final PermissionManager permissionManager;
  private final InstallManager installManager;
  private final boolean alwaysUpdate;
  private final String marketName; //Probably doesn't need to be here
  private final DownloadAnalytics downloadAnalytics;
  private final int localVersionCode;
  private final AutoUpdateRepository autoUpdateRepository;
  private AutoUpdateViewModel autoUpdateViewModel;

  public AutoUpdateManager(DownloadFactory downloadFactory, PermissionManager permissionManager,
      InstallManager installManager, boolean alwaysUpdate, String marketName,
      DownloadAnalytics downloadAnalytics, int localVersionCode,
      AutoUpdateRepository autoUpdateRepository) {
    this.downloadFactory = downloadFactory;
    this.permissionManager = permissionManager;
    this.installManager = installManager;
    this.alwaysUpdate = alwaysUpdate;
    this.marketName = marketName;
    this.downloadAnalytics = downloadAnalytics;
    this.localVersionCode = localVersionCode;
    this.autoUpdateRepository = autoUpdateRepository;
  }

  public Single<AutoUpdateViewModel> getAutoUpdateModel() {
    return autoUpdateRepository.loadFreshAutoUpdateViewModel()
        .flatMap(autoUpdateViewModel -> {
          this.autoUpdateViewModel = autoUpdateViewModel;
          if (autoUpdateViewModel.getVersionCode() > localVersionCode
              && Build.VERSION.SDK_INT >= Integer.parseInt(autoUpdateViewModel.getMinSdk())
              || alwaysUpdate) {
            autoUpdateViewModel.setShouldUpdate(true);
          }
          return Single.just(autoUpdateViewModel);
        });
  }

  public Observable<Void> requestPermissions(PermissionService permissionService) {
    return permissionManager.requestDownloadAccess(permissionService)
        .flatMap(permissionGranted -> permissionManager.requestExternalStoragePermission(
            permissionService));
  }

  public Observable<Install> startUpdate() {
    return Observable.just(downloadFactory.create(autoUpdateViewModel))
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(
                __ -> downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
                    DownloadAnalytics.AppContext.AUTO_UPDATE)))
        .flatMap(__ -> installManager.getInstall(autoUpdateViewModel.getMd5(),
            autoUpdateViewModel.getPackageName(), autoUpdateViewModel.getVersionCode()))
        .skipWhile(installationProgress -> installationProgress.getState()
            != Install.InstallationStatus.INSTALLING)
        .first(progress -> progress.getState() != Install.InstallationStatus.INSTALLING);
  }
}

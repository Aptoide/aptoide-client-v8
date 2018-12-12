package cm.aptoide.pt.autoupdate;

import android.os.Build;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallManager;
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
  private final AutoUpdateService autoUpdateService;

  public AutoUpdateManager(DownloadFactory downloadFactory, PermissionManager permissionManager,
      InstallManager installManager, boolean alwaysUpdate, String marketName,
      DownloadAnalytics downloadAnalytics, int localVersionCode,
      AutoUpdateService autoUpdateService) {
    this.downloadFactory = downloadFactory;
    this.permissionManager = permissionManager;
    this.installManager = installManager;
    this.alwaysUpdate = alwaysUpdate;
    this.marketName = marketName;
    this.downloadAnalytics = downloadAnalytics;
    this.localVersionCode = localVersionCode;
    this.autoUpdateService = autoUpdateService;
  }

  public Single<AutoUpdateViewModel> getAutoUpdateModel() {
    return autoUpdateService.loadAutoUpdateViewModel()
        .flatMap(autoUpdateViewModel -> {
          if (autoUpdateViewModel.getVersionCode() > localVersionCode
              && Build.VERSION.SDK_INT >= Integer.parseInt(autoUpdateViewModel.getMinSdk())
              || alwaysUpdate) {
            autoUpdateViewModel.setShouldUpdate(true);
          }
          return Single.just(autoUpdateViewModel);
        });
  }
}

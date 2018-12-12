package cm.aptoide.pt.autoupdate;

import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.view.ActivityView;
import rx.Single;

public class AutoUpdateManager {
  //TODO 11/12/18 This class is incomplete
  private final ActivityView activity;
  private final DownloadFactory downloadFactory;
  private final PermissionManager permissionManager;
  private final InstallManager installManager;
  private final Resources resources;
  private final String autoUpdateUrl;
  private final int updateDialogIcon;
  private final boolean alwaysUpdate;
  private final String marketName;
  private final DownloadAnalytics downloadAnalytics;
  private final AutoUpdateViewModel autoUpdateViewModel;
  private final AutoUpdateService autoUpdateService;

  public AutoUpdateManager(ActivityView activity, DownloadFactory downloadFactory,
      PermissionManager permissionManager, InstallManager installManager, Resources resources,
      String autoUpdateUrl, @DrawableRes int updateDialogIcon, boolean alwaysUpdate,
      String marketName, DownloadAnalytics downloadAnalytics,
      AutoUpdateViewModel autoUpdateViewModel, AutoUpdateService autoUpdateService) {
    this.activity = activity;
    this.downloadFactory = downloadFactory;
    this.permissionManager = permissionManager;
    this.installManager = installManager;
    this.resources = resources;
    this.autoUpdateUrl = autoUpdateUrl;
    this.updateDialogIcon = updateDialogIcon;
    this.alwaysUpdate = alwaysUpdate;
    this.marketName = marketName;
    this.downloadAnalytics = downloadAnalytics;
    this.autoUpdateViewModel = autoUpdateViewModel;
    this.autoUpdateService = autoUpdateService;
  }

  public Single<AutoUpdateModel> getAutoUpdateModel() {
    return autoUpdateService.loadAutoUpdateModel();
  }
}

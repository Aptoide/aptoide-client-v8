package cm.aptoide.pt.v8engine.view.downloads.completed;

import android.content.Context;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.InstallationProgress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.download.DownloadEvent;
import cm.aptoide.pt.v8engine.download.DownloadEventConverter;
import cm.aptoide.pt.v8engine.download.DownloadInstallBaseEvent;
import cm.aptoide.pt.v8engine.download.InstallEvent;
import cm.aptoide.pt.v8engine.download.InstallEventConverter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import rx.Observable;

/**
 * Created by trinkes on 7/15/16.
 */
public class CompletedDownloadDisplayable extends Displayable {

  private final InstallManager installManager;
  private final DownloadEventConverter converter;
  private final Analytics analytics;
  private final InstallEventConverter installConverter;

  private final InstallationProgress installation;

  public CompletedDownloadDisplayable() {
    this.installManager = null;
    this.converter = null;
    this.analytics = null;
    this.installConverter = null;
    this.installation = null;
  }

  public CompletedDownloadDisplayable(InstallationProgress installation,
      InstallManager installManager, DownloadEventConverter converter, Analytics analytics,
      InstallEventConverter installConverter) {
    this.installation = installation;
    this.installManager = installManager;
    this.converter = converter;
    this.analytics = analytics;
    this.installConverter = installConverter;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.completed_donwload_row_layout;
  }

  public void removeDownload() {
    installManager.removeInstallationFile(installation.getMd5(), installation.getPackageName(),
        installation.getVersionCode());
  }

  public Observable<InstallationProgress.InstallationStatus> downloadStatus() {
    return installManager.getInstallationProgress(installation.getMd5(),
        installation.getPackageName(), installation.getVersionCode())
        .map(installationProgress -> installationProgress.getState())
        .onErrorReturn(throwable -> InstallationProgress.InstallationStatus.UNINSTALLED);
  }

  public Observable<InstallationProgress> installOrOpenDownload(Context context,
      PermissionService permissionRequest) {
    return installManager.getInstallationProgress(installation.getMd5(),
        installation.getPackageName(), installation.getVersionCode())
        .first()
        .flatMap(installationProgress -> {
          if (installationProgress.getState()
              == InstallationProgress.InstallationStatus.INSTALLED) {
            AptoideUtils.SystemU.openApp(installation.getPackageName(), context.getPackageManager(),
                context);
            return Observable.empty();
          } else {
            return resumeDownload(context, permissionRequest);
          }
        });
  }

  public Observable<InstallationProgress> resumeDownload(Context context,
      PermissionService permissionRequest) {
    PermissionManager permissionManager = new PermissionManager();
    return installManager.getDownload(installation.getMd5())
        .toObservable()
        .flatMap(download -> permissionManager.requestExternalStoragePermission(permissionRequest)
            .flatMap(success -> permissionManager.requestDownloadAccess(permissionRequest))
            .flatMap(success -> installManager.install(download)
                .toObservable()
                .flatMap(downloadProgress -> installManager.getInstallationProgress(
                    installation.getMd5(), installation.getPackageName(),
                    installation.getVersionCode()))
                .doOnSubscribe(() -> setupEvents(download))));
  }

  private void setupEvents(Download download) {
    DownloadEvent report =
        converter.create(download, DownloadEvent.Action.CLICK, DownloadEvent.AppContext.DOWNLOADS);
    analytics.save(download.getPackageName() + download.getVersionCode(), report);

    InstallEvent installEvent =
        installConverter.create(download, DownloadInstallBaseEvent.Action.CLICK,
            DownloadInstallBaseEvent.AppContext.DOWNLOADS);
    analytics.save(download.getPackageName() + download.getVersionCode(), installEvent);
  }

  public InstallationProgress getInstallation() {
    return installation;
  }

  public String getStatusName(Context context) {
    switch (installation.getState()) {
      case INSTALLING:
        return context.getString(cm.aptoide.pt.database.R.string.download_progress);
      case PAUSED:
        return context.getString(cm.aptoide.pt.database.R.string.download_paused);
      case INSTALLED:
        return context.getString(cm.aptoide.pt.database.R.string.download_completed);
      case UNINSTALLED:
        return "";
      case FAILED:
        switch (installation.getError()) {
          case GENERIC_ERROR:
            return context.getString(R.string.simple_error_occured);
          case NOT_ENOUGH_SPACE_ERROR:
            return context.getString(cm.aptoide.pt.database.R.string.out_of_space_error);
          default:
            throw new RuntimeException("Unknown error");
        }
      default:
        throw new RuntimeException("Unknown status");
    }
  }
}

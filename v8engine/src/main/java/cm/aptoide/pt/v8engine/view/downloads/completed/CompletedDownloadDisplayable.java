package cm.aptoide.pt.v8engine.view.downloads.completed;

import android.content.Context;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.DownloadEvent;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.DownloadEventConverter;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.DownloadInstallBaseEvent;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.InstallEvent;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.InstallEventConverter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import rx.Observable;
import rx.functions.Action0;

/**
 * Created by trinkes on 7/15/16.
 */
public class CompletedDownloadDisplayable extends Displayable {

  private final InstallManager installManager;
  private final DownloadEventConverter converter;
  private final Analytics analytics;
  private final InstallEventConverter installConverter;

  private final Download download;

  private Action0 onPauseAction;
  private Action0 onResumeAction;

  public CompletedDownloadDisplayable() {
    this.installManager = null;
    this.converter = null;
    this.analytics = null;
    this.installConverter = null;
    this.download = null;
  }

  public CompletedDownloadDisplayable(Download download, InstallManager installManager,
      DownloadEventConverter converter, Analytics analytics,
      InstallEventConverter installConverter) {
    this.download = download;
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

  @Override public void onResume() {
    super.onResume();
    if (onResumeAction != null) {
      onResumeAction.call();
    }
  }

  @Override public void onPause() {
    if (onPauseAction != null) {
      onResumeAction.call();
    }
    super.onPause();
  }

  public void removeDownload(Context context) {
    installManager.removeInstallationFile(download.getMd5(), context);
  }

  public Observable<Integer> downloadStatus() {
    return installManager.getInstallation(download.getMd5())
        .map(installationProgress -> installationProgress.getRequest().getOverallDownloadStatus())
        .onErrorReturn(throwable -> Download.NOT_DOWNLOADED);
  }

  public Observable<Progress<Download>> installOrOpenDownload(Context context,
      PermissionService permissionRequest) {
    return installManager.getInstallation(download.getMd5()).flatMap(installed -> {
      if (installed.getState() == Progress.DONE) {
        AptoideUtils.SystemU.openApp(download.getFilesToDownload().get(0).getPackageName());
        return Observable.empty();
      }
      return resumeDownload(context, permissionRequest);
    });
  }

  public Observable<Progress<Download>> resumeDownload(Context context,
      PermissionService permissionRequest) {
    PermissionManager permissionManager = new PermissionManager();
    return permissionManager.requestExternalStoragePermission(permissionRequest)
        .flatMap(success -> permissionManager.requestDownloadAccess(permissionRequest))
        .flatMap(success -> installManager.install(context, download)
            .doOnSubscribe(() -> setupEvents(download)));
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

  public Download getDownload() {
    return download;
  }

  public void setOnPauseAction(Action0 onPauseAction) {
    this.onPauseAction = onPauseAction;
  }

  public void setOnResumeAction(Action0 onResumeAction) {
    this.onResumeAction = onResumeAction;
  }
}

/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Setter;
import rx.Observable;
import rx.functions.Action0;

/**
 * Created by trinkes on 7/15/16.
 */
public class CompletedDownloadDisplayable extends DisplayablePojo<Progress<Download>> {

  private InstallManager installManager;
  @Setter private Action0 onResumeAction;
  @Setter private Action0 onPauseAction;

  public CompletedDownloadDisplayable() {
    super();
  }

  public CompletedDownloadDisplayable(Progress<Download> pojo, InstallManager installManager) {
    super(pojo);
    this.installManager = installManager;
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

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.completed_donwload_row_layout;
  }

  public void removeDownload(Context context) {
    installManager.removeInstallationFile(context, getPojo().getRequest().getMd5());
  }

  public Observable<Integer> downloadStatus() {
    return installManager.getInstallation(getPojo().getRequest().getMd5())
        .map(installationProgress -> installationProgress.getRequest().getOverallDownloadStatus())
        .onErrorReturn(throwable -> Download.NOT_DOWNLOADED);
  }

  public Observable<Progress<Download>> resumeDownload(Context context,
      PermissionRequest permissionRequest) {
    PermissionManager permissionManager = new PermissionManager();
    return permissionManager.requestExternalStoragePermission(permissionRequest)
        .flatMap(success -> permissionManager.requestDownloadAccess(permissionRequest))
        .flatMap(success -> installManager.install(context, getPojo().getRequest()));
  }

  public Observable<Progress<Download>> installOrOpenDownload(Context context,
      PermissionRequest permissionRequest) {
    return installManager.getInstallation(getPojo().getRequest().getMd5()).flatMap(installed -> {
      if (installed.getState() == Progress.DONE) {
        AptoideUtils.SystemU.openApp(
            getPojo().getRequest().getFilesToDownload().get(0).getPackageName());
        return Observable.empty();
      }
      return resumeDownload(context, permissionRequest);
    });
  }
}

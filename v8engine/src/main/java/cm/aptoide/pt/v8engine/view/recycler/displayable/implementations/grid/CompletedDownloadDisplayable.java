/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import rx.Observable;

/**
 * Created by trinkes on 7/15/16.
 */
public class CompletedDownloadDisplayable extends DisplayablePojo<Progress<Download>> {

  private InstallManager installManager;

  public CompletedDownloadDisplayable() {
    super();
  }

  public CompletedDownloadDisplayable(Progress<Download> pojo, InstallManager installManager) {
    super(pojo);
    this.installManager = installManager;
  }

  public CompletedDownloadDisplayable(Progress<Download> pojo, boolean fixedPerLineCount) {
    super(pojo, fixedPerLineCount);
  }

  @Override public Type getType() {
    return Type.COMPLETED_DOWNLOAD;
  }

  @Override public int getViewLayout() {
    return R.layout.completed_donwload_row_layout;
  }

  public void removeDownload() {
    installManager.removeInstallationFile(getPojo().getRequest().getAppId());
  }

  public Observable<Integer> downloadStatus() {
    return installManager.getInstallation(getPojo().getRequest().getAppId())
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
    return installManager.getInstallation(getPojo().getRequest().getAppId()).flatMap(installed -> {
      if (installed.getState() == Progress.DONE) {
        AptoideUtils.SystemU.openApp(
            getPojo().getRequest().getFilesToDownload().get(0).getPackageName());
        return Observable.empty();
      }
      return resumeDownload(context, permissionRequest);
    });
  }
}

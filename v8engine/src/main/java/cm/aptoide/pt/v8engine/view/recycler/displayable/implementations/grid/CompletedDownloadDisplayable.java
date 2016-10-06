/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.model.v7.Type;
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

  public CompletedDownloadDisplayable(Progress<Download> pojo, boolean fixedPerLineCount) {
    super(pojo, fixedPerLineCount);
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

  public Observable<Progress<Download>> resumeDownload(Context context) {
    return installManager.install(context, getPojo().getRequest());
  }

  public Observable<Progress<Download>> installOrOpenDownload(Context context) {
    return installManager.getInstallation(getPojo().getRequest().getAppId()).flatMap(installed -> {
      if (installed.getState() == Progress.DONE) {
        AptoideUtils.SystemU.openApp(
            getPojo().getRequest().getFilesToDownload().get(0).getPackageName());
        return Observable.empty();
      }
      return installManager.install(context, getPojo().getRequest());
    });
  }
}

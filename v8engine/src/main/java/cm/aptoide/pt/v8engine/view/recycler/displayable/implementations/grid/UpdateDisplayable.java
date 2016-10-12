/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rx.Observable;

/**
 * Created by neuro on 17-05-2016.
 */
@AllArgsConstructor public class UpdateDisplayable extends Displayable {

  @Getter private String packageName;
  @Getter private long appId;
  @Getter private String label;
  @Getter private String icon;
  @Getter private int versionCode;
  @Getter private String md5;
  @Getter private String apkPath;
  @Getter private String alternativeApkPath;
  @Getter private String updateVersionName;
  // Obb
  @Getter private String mainObbName;
  @Getter private String mainObbPath;
  @Getter private String mainObbMd5;
  @Getter private String patchObbName;
  @Getter private String patchObbPath;
  @Getter private String patchObbMd5;

  @Getter private Installer installManager;
  private Download download;
  @Getter private DownloadServiceHelper downloadManager;

  public UpdateDisplayable() {
  }

  public static UpdateDisplayable create(Update update, Installer installManager,
      DownloadFactory downloadFactory, DownloadServiceHelper downloadManager) {

    return new UpdateDisplayable(update.getPackageName(), update.getAppId(), update.getLabel(),
        update.getIcon(), update.getVersionCode(), update.getMd5(), update.getApkPath(),
        update.getAlternativeApkPath(), update.getUpdateVersionName(), update.getMainObbName(),
        update.getMainObbPath(), update.getMainObbMd5(), update.getPatchObbName(),
        update.getPatchObbPath(), update.getPatchObbMd5(), installManager,
        downloadFactory.create(update), downloadManager);
  }

  public Observable<Void> downloadAndInstall(Context context) {
    Analytics.Updates.update();

    return downloadManager.startDownload((PermissionRequest) context, download)
        .first(download -> download.getOverallDownloadStatus() == Download.COMPLETED)
        .concatMap(downloadCompleted -> installManager.update(context, (PermissionRequest) context,
            download.getMd5()));
  }

  @Override public int getViewLayout() {
    return R.layout.update_row;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }
}

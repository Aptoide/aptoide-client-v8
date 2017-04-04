/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.view.updates;

import android.content.Context;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.DownloadEvent;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.DownloadEventConverter;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.DownloadInstallBaseEvent;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.InstallEvent;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.InstallEventConverter;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.Getter;
import rx.Observable;

import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

/**
 * Created by neuro on 17-05-2016.
 */
public class UpdateDisplayable extends Displayable {

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

  @Getter private Download download;
  @Getter private InstallManager installManager;
  private Analytics analytics;
  private DownloadEventConverter converter;
  private InstallEventConverter installConverter;

  public UpdateDisplayable() {
  }

  private UpdateDisplayable(String packageName, long appId, String label, String icon,
      int versionCode, String md5, String apkPath, String alternativeApkPath,
      String updateVersionName, String mainObbName, String mainObbPath, String mainObbMd5,
      String patchObbName, String patchObbPath, String patchObbMd5, Download download,
      InstallManager installManager, Analytics analytics,
      DownloadEventConverter downloadInstallEventConverter,
      InstallEventConverter installConverter) {
    this.packageName = packageName;
    this.appId = appId;
    this.label = label;
    this.icon = icon;
    this.versionCode = versionCode;
    this.md5 = md5;
    this.apkPath = apkPath;
    this.alternativeApkPath = alternativeApkPath;
    this.updateVersionName = updateVersionName;
    this.mainObbName = mainObbName;
    this.mainObbPath = mainObbPath;
    this.mainObbMd5 = mainObbMd5;
    this.patchObbName = patchObbName;
    this.patchObbPath = patchObbPath;
    this.patchObbMd5 = patchObbMd5;
    this.download = download;
    this.installManager = installManager;
    this.analytics = analytics;
    this.converter = downloadInstallEventConverter;
    this.installConverter = installConverter;
  }

  public static UpdateDisplayable newInstance(Update update, InstallManager installManager,
      DownloadFactory downloadFactory, Analytics analytics,
      DownloadEventConverter downloadInstallEventConverter,
      InstallEventConverter installConverter) {

    return new UpdateDisplayable(update.getPackageName(), update.getAppId(), update.getLabel(),
        update.getIcon(), update.getVersionCode(), update.getMd5(), update.getApkPath(),
        update.getAlternativeApkPath(), update.getUpdateVersionName(), update.getMainObbName(),
        update.getMainObbPath(), update.getMainObbMd5(), update.getPatchObbName(),
        update.getPatchObbPath(), update.getPatchObbMd5(), downloadFactory.create(update),
        installManager, analytics, downloadInstallEventConverter, installConverter);
  }

  public Observable<Progress<Download>> downloadAndInstall(Context context,
      PermissionService permissionRequest) {
    Analytics.Updates.update();
    PermissionManager permissionManager = new PermissionManager();
    return permissionManager.requestExternalStoragePermission(permissionRequest)
        .flatMap(sucess -> {
          if (installManager.showWarning()) {
            return GenericDialogs.createGenericYesNoCancelMessage(context, "",
                AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog))
                .map(answer -> (answer.equals(YES)))
                .doOnNext(answer -> installManager.rootInstallAllowed(answer));
          }
          return Observable.just(true);
        })
        .flatMap(success -> permissionManager.requestDownloadAccess(permissionRequest))
        .flatMap(success -> installManager.install(context, download)
            .doOnSubscribe(() -> setupEvents(download)));
  }

  private void setupEvents(Download download) {
    DownloadEvent report =
        converter.create(download, DownloadEvent.Action.CLICK, DownloadEvent.AppContext.UPDATE_TAB);
    analytics.save(download.getPackageName() + download.getVersionCode(), report);
    InstallEvent installEvent =
        installConverter.create(download, DownloadInstallBaseEvent.Action.CLICK,
            DownloadInstallBaseEvent.AppContext.UPDATE_TAB);
    analytics.save(download.getPackageName() + download.getVersionCode(), installEvent);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.update_row;
  }
}

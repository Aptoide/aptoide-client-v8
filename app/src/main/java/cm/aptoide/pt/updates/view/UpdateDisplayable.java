/*
 * Copyright (c) 2016.
 * Modified on 27/07/2016.
 */

package cm.aptoide.pt.updates.view;

import android.content.Context;
import android.content.res.Resources;
import cm.aptoide.pt.Install;
import cm.aptoide.pt.InstallManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.download.DownloadEvent;
import cm.aptoide.pt.download.DownloadEventConverter;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.DownloadInstallBaseEvent;
import cm.aptoide.pt.download.InstallEvent;
import cm.aptoide.pt.download.InstallEventConverter;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import lombok.Getter;
import rx.Completable;
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
  private int updateVersionCode;
  private InstalledRepository installedRepository;
  private PermissionManager permissionManager;

  public UpdateDisplayable() {
  }

  private UpdateDisplayable(String packageName, long appId, String label, String icon,
      int versionCode, String md5, String apkPath, String alternativeApkPath,
      String updateVersionName, String mainObbName, String mainObbPath, String mainObbMd5,
      String patchObbName, String patchObbPath, String patchObbMd5, Download download,
      InstallManager installManager, Analytics analytics,
      DownloadEventConverter downloadInstallEventConverter, InstallEventConverter installConverter,
      int updateVersionCode, InstalledRepository installedRepository,
      PermissionManager permissionManager) {
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
    this.updateVersionCode = updateVersionCode;
    this.installedRepository = installedRepository;
    this.permissionManager = permissionManager;
  }

  public static UpdateDisplayable newInstance(Update update, InstallManager installManager,
      DownloadFactory downloadFactory, Analytics analytics,
      DownloadEventConverter downloadInstallEventConverter, InstallEventConverter installConverter,
      InstalledRepository installedRepository, PermissionManager permissionManager) {

    return new UpdateDisplayable(update.getPackageName(), update.getAppId(), update.getLabel(),
        update.getIcon(), update.getVersionCode(), update.getMd5(), update.getApkPath(),
        update.getAlternativeApkPath(), update.getUpdateVersionName(), update.getMainObbName(),
        update.getMainObbPath(), update.getMainObbMd5(), update.getPatchObbName(),
        update.getPatchObbPath(), update.getPatchObbMd5(), downloadFactory.create(update),
        installManager, analytics, downloadInstallEventConverter, installConverter,
        update.getUpdateVersionCode(), installedRepository, permissionManager);
  }

  public Completable downloadAndInstall(Context context, PermissionService permissionRequest,
      Resources resources) {
    PermissionManager permissionManager = new PermissionManager();
    return permissionManager.requestExternalStoragePermission(permissionRequest)
        .flatMap(sucess -> {
          if (installManager.showWarning()) {
            return GenericDialogs.createGenericYesNoCancelMessage(context, "",
                AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog, resources))
                .map(answer -> (answer.equals(YES)))
                .doOnNext(answer -> installManager.rootInstallAllowed(answer));
          }
          return Observable.just(true);
        })
        .flatMap(success -> permissionManager.requestDownloadAccess(permissionRequest))
        .flatMap(success -> installManager.install(download)
            .toObservable()
            .doOnSubscribe(() -> setupEvents(download)))
        .toCompletable();
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

  public int getUpdateVersionCode() {
    return updateVersionCode;
  }

  public Observable<Boolean> shouldShowProgress() {
    return installManager.getInstall(getMd5(), getPackageName(), getUpdateVersionCode())
        .map(installationProgress -> installationProgress.getState()
            == Install.InstallationStatus.INSTALLING || installationProgress.isIndeterminate());
  }

  public InstalledRepository getInstalledRepository() {
    return installedRepository;
  }
}

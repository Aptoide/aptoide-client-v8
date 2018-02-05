/*
 * Copyright (c) 2016.
 * Modified on 27/07/2016.
 */

package cm.aptoide.pt.updates.view;

import android.content.Context;
import android.content.res.Resources;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.InstallType;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import rx.Completable;
import rx.Observable;

import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

/**
 * Created by neuro on 17-05-2016.
 */
public class UpdateDisplayable extends Displayable {

  private String packageName;
  private long appId;
  private String label;
  private String icon;
  private int versionCode;
  private String md5;
  private String apkPath;
  private String alternativeApkPath;
  private String updateVersionName;
  // Obb
  private String mainObbName;
  private String mainObbPath;
  private String mainObbMd5;
  private String patchObbName;
  private String patchObbPath;
  private String patchObbMd5;

  private Download download;
  private InstallManager installManager;
  private DownloadAnalytics downloadAnalytics;
  private int updateVersionCode;
  private InstalledRepository installedRepository;
  private InstallAnalytics installAnalytics;

  public UpdateDisplayable() {
  }

  private UpdateDisplayable(String packageName, long appId, String label, String icon,
      int versionCode, String md5, String apkPath, String alternativeApkPath,
      String updateVersionName, String mainObbName, String mainObbPath, String mainObbMd5,
      String patchObbName, String patchObbPath, String patchObbMd5, Download download,
      InstallManager installManager, DownloadAnalytics downloadAnalytics, int updateVersionCode,
      InstalledRepository installedRepository, InstallAnalytics installAnalytics) {
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
    this.downloadAnalytics = downloadAnalytics;
    this.updateVersionCode = updateVersionCode;
    this.installedRepository = installedRepository;
    this.installAnalytics = installAnalytics;
  }

  public static UpdateDisplayable newInstance(Update update, InstallManager installManager,
      DownloadFactory downloadFactory, DownloadAnalytics downloadAnalytics,
      InstalledRepository installedRepository, InstallAnalytics installAnalytics) {

    return new UpdateDisplayable(update.getPackageName(), update.getAppId(), update.getLabel(),
        update.getIcon(), update.getVersionCode(), update.getMd5(), update.getApkPath(),
        update.getAlternativeApkPath(), update.getUpdateVersionName(), update.getMainObbName(),
        update.getMainObbPath(), update.getMainObbMd5(), update.getPatchObbName(),
        update.getPatchObbPath(), update.getPatchObbMd5(), downloadFactory.create(update),
        installManager, downloadAnalytics, update.getUpdateVersionCode(), installedRepository,
        installAnalytics);
  }

  public String getPackageName() {
    return packageName;
  }

  public long getAppId() {
    return appId;
  }

  public String getLabel() {
    return label;
  }

  public String getIcon() {
    return icon;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public String getMd5() {
    return md5;
  }

  public String getApkPath() {
    return apkPath;
  }

  public String getAlternativeApkPath() {
    return alternativeApkPath;
  }

  public String getUpdateVersionName() {
    return updateVersionName;
  }

  public String getMainObbName() {
    return mainObbName;
  }

  public String getMainObbPath() {
    return mainObbPath;
  }

  public String getMainObbMd5() {
    return mainObbMd5;
  }

  public String getPatchObbName() {
    return patchObbName;
  }

  public String getPatchObbPath() {
    return patchObbPath;
  }

  public String getPatchObbMd5() {
    return patchObbMd5;
  }

  public Download getDownload() {
    return download;
  }

  public InstallManager getInstallManager() {
    return installManager;
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
    downloadAnalytics.downloadStartEvent(download, AnalyticsManager.Action.CLICK,
        DownloadAnalytics.AppContext.UPDATE_TAB);

    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        InstallType.UPDATE, AnalyticsManager.Action.INSTALL,
        AppContext.UPDATE_TAB, Origin.UPDATE);
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

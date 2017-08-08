/*
 * Copyright (c) 2016.
 * Modified on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.view.app.displayable;

import android.widget.Button;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.v8engine.Install;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.app.AppViewAnalytics;
import cm.aptoide.pt.v8engine.download.DownloadFactory;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.view.app.AppViewFragment;
import com.jakewharton.rxrelay.PublishRelay;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

/**
 * Created on 06/05/16.
 */
public class AppViewInstallDisplayable extends AppViewDisplayable {

  private final Observable<Void> installAppRelay;
  private int versionCode;
  @Getter @Setter private boolean shouldInstall;
  @Getter private MinimalAd minimalAd;

  private InstallManager installManager;
  private String md5;
  private String packageName;
  private InstalledRepository installedRepository;
  private Button installButton;
  private DownloadFactory downloadFactory;
  private TimelineAnalytics timelineAnalytics;
  @Getter private AppViewFragment appViewFragment;

  public AppViewInstallDisplayable() {
    super();
    installAppRelay = PublishRelay.empty();
  }

  public AppViewInstallDisplayable(InstallManager installManager, GetApp getApp,
      MinimalAd minimalAd, boolean shouldInstall, InstalledRepository installedRepository,
      TimelineAnalytics timelineAnalytics, AppViewAnalytics appViewAnalytics,
      PublishRelay installAppRelay, DownloadFactory downloadFactory,
      AppViewFragment appViewFragment) {
    super(getApp, appViewAnalytics);
    this.installManager = installManager;
    this.md5 = getApp.getNodes()
        .getMeta()
        .getData()
        .getFile()
        .getMd5sum();
    this.packageName = getApp.getNodes()
        .getMeta()
        .getData()
        .getPackageName();
    this.versionCode = getApp.getNodes()
        .getMeta()
        .getData()
        .getFile()
        .getVercode();
    this.minimalAd = minimalAd;
    this.shouldInstall = shouldInstall;
    this.downloadFactory = downloadFactory;
    this.installAppRelay = installAppRelay;
    this.installedRepository = installedRepository;
    this.timelineAnalytics = timelineAnalytics;
    this.appViewFragment = appViewFragment;
  }

  public static AppViewInstallDisplayable newInstance(GetApp getApp, InstallManager installManager,
      MinimalAd minimalAd, boolean shouldInstall, InstalledRepository installedRepository,
      DownloadFactory downloadFactory, TimelineAnalytics timelineAnalytics,
      AppViewAnalytics appViewAnalytics, PublishRelay installAppRelay,
      AppViewFragment appViewFragment) {
    return new AppViewInstallDisplayable(installManager, getApp, minimalAd, shouldInstall,
        installedRepository, timelineAnalytics, appViewAnalytics, installAppRelay, downloadFactory,
        appViewFragment);
  }

  public void startInstallationProcess() {
    if (installButton != null) {
      installButton.performClick();
    }
  }

  public void setInstallButton(Button installButton) {
    this.installButton = installButton;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_app_view_install;
  }

  public Observable<Install> getInstallState() {
    return installManager.getInstall(md5, packageName, versionCode);
  }

  public DownloadFactory getDownloadFactory() {
    return downloadFactory;
  }

  public TimelineAnalytics getTimelineAnalytics() {
    return timelineAnalytics;
  }

  public Observable<Void> getInstallAppRelay() {
    return installAppRelay;
  }
}

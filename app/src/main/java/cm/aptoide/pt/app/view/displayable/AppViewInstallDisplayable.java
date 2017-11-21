/*
 * Copyright (c) 2016.
 * Modified on 25/08/2016.
 */

package cm.aptoide.pt.app.view.displayable;

import android.widget.Button;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.download.DownloadCompleteAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

/**
 * Created on 06/05/16.
 */
public class AppViewInstallDisplayable extends AppViewDisplayable {

  private final Observable<Void> installAppRelay;
  private int versionCode;
  private boolean shouldInstall;
  private SearchAdResult searchAdResult;

  private InstallManager installManager;
  private String md5;
  private String packageName;
  private Button installButton;
  private DownloadFactory downloadFactory;
  private TimelineAnalytics timelineAnalytics;
  private AppViewFragment appViewFragment;
  private DownloadCompleteAnalytics analytics;
  private NavigationTracker navigationTracker;
  private String editorsChoiceBrickPosition;
  private InstallAnalytics installAnalytics;

  public AppViewInstallDisplayable() {
    super();
    installAppRelay = PublishRelay.empty();
  }

  public AppViewInstallDisplayable(InstallManager installManager, GetApp getApp,
      SearchAdResult searchAdResult, boolean shouldInstall, TimelineAnalytics timelineAnalytics,
      AppViewAnalytics appViewAnalytics, PublishRelay installAppRelay,
      DownloadFactory downloadFactory, AppViewFragment appViewFragment,
      DownloadCompleteAnalytics analytics, NavigationTracker navigationTracker,
      String editorsChoiceBrickPosition, InstallAnalytics installAnalytics) {
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
    this.searchAdResult = searchAdResult;
    this.shouldInstall = shouldInstall;
    this.downloadFactory = downloadFactory;
    this.installAppRelay = installAppRelay;
    this.timelineAnalytics = timelineAnalytics;
    this.appViewFragment = appViewFragment;
    this.analytics = analytics;
    this.navigationTracker = navigationTracker;
    this.editorsChoiceBrickPosition = editorsChoiceBrickPosition;
    this.installAnalytics = installAnalytics;
  }

  public static AppViewInstallDisplayable newInstance(GetApp getApp, InstallManager installManager,
      SearchAdResult searchAdResult, boolean shouldInstall, InstalledRepository installedRepository,
      DownloadFactory downloadFactory, TimelineAnalytics timelineAnalytics,
      AppViewAnalytics appViewAnalytics, PublishRelay installAppRelay,
      AppViewFragment appViewFragment, DownloadCompleteAnalytics analytics,
      NavigationTracker navigationTracker, String editorsBrickPosition,
      InstallAnalytics installAnalytics) {
    return new AppViewInstallDisplayable(installManager, getApp, searchAdResult, shouldInstall,
        timelineAnalytics, appViewAnalytics, installAppRelay, downloadFactory, appViewFragment,
        analytics, navigationTracker, editorsBrickPosition, installAnalytics);
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

  public void installAppClicked(DownloadCompleteAnalytics.InstallType installType) {
    GetAppMeta.App app = getPojo().getNodes()
        .getMeta()
        .getData();
    installAnalytics.installStarted(navigationTracker.getPreviousScreen(),
        navigationTracker.getCurrentScreen(), app.getPackageName(), versionCode,
        InstallAnalytics.InstallType.valueOf(installType.name()));
    analytics.installClicked(navigationTracker.getPreviousScreen(),
        navigationTracker.getCurrentScreen(), app.getMd5(), app.getPackageName(), app.getFile()
            .getMalware()
            .getRank()
            .name(), editorsChoiceBrickPosition, installType);
  }

  public boolean isShouldInstall() {
    return this.shouldInstall;
  }

  public void setShouldInstall(boolean shouldInstall) {
    this.shouldInstall = shouldInstall;
  }

  public SearchAdResult getSearchAdResult() {
    return this.searchAdResult;
  }

  public AppViewFragment getAppViewFragment() {
    return this.appViewFragment;
  }
}

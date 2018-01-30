/*
 * Copyright (c) 2016.
 * Modified on 25/08/2016.
 */

package cm.aptoide.pt.app.view.displayable;

import android.widget.Button;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.InstallType;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;
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
  private DownloadAnalytics analytics;
  private NavigationTracker navigationTracker;
  private String editorsChoiceBrickPosition;
  private InstallAnalytics installAnalytics;
  private int campaignId;
  private String abTestingGroup;
  private List<String> fragments;

  public AppViewInstallDisplayable() {
    super();
    this.installAppRelay = PublishRelay.empty();
  }

  public AppViewInstallDisplayable(InstallManager installManager, GetApp getApp,
      SearchAdResult searchAdResult, boolean shouldInstall, TimelineAnalytics timelineAnalytics,
      AppViewAnalytics appViewAnalytics, PublishRelay installAppRelay,
      DownloadFactory downloadFactory, AppViewFragment appViewFragment, DownloadAnalytics analytics,
      NavigationTracker navigationTracker, String editorsChoiceBrickPosition,
      InstallAnalytics installAnalytics, int campaignId, String abTestingGroup,
      List<String> fragments) {
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
    this.campaignId = campaignId;
    this.abTestingGroup = abTestingGroup;
    this.fragments = fragments;
  }

  public static AppViewInstallDisplayable newInstance(GetApp getApp, InstallManager installManager,
      SearchAdResult searchAdResult, boolean shouldInstall, DownloadFactory downloadFactory,
      TimelineAnalytics timelineAnalytics, AppViewAnalytics appViewAnalytics,
      PublishRelay installAppRelay, AppViewFragment appViewFragment, DownloadAnalytics analytics,
      NavigationTracker navigationTracker, String editorsBrickPosition,
      InstallAnalytics installAnalytics, int campaignId, String abTestingGroup,
      List<String> fragments) {
    return new AppViewInstallDisplayable(installManager, getApp, searchAdResult, shouldInstall,
        timelineAnalytics, appViewAnalytics, installAppRelay, downloadFactory, appViewFragment,
        analytics, navigationTracker, editorsBrickPosition, installAnalytics, campaignId,
        abTestingGroup, fragments);
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

  public void installAppClicked(InstallType installType) {
    GetAppMeta.App app = getPojo().getNodes()
        .getMeta()
        .getData();
    installAnalytics.installStarted(navigationTracker.getPreviousScreen(),
        navigationTracker.getCurrentScreen(), app.getPackageName(), versionCode, installType,
        fragments);
    analytics.installClicked(navigationTracker.getPreviousScreen(),
        navigationTracker.getCurrentScreen(), app.getMd5(), app.getPackageName(), app.getFile()
            .getMalware()
            .getRank()
            .name(), editorsChoiceBrickPosition, installType, AnalyticsManager.Action.CLICK,
        navigationTracker.getPreviousScreen()
            .getFragment(), navigationTracker.getCurrentScreen()
            .getFragment());
  }

  public int getCampaignId() {
    return campaignId;
  }

  public String getAbTestingGroup() {
    return abTestingGroup;
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

/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.TextUtils;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports.DownloadEvent;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports.DownloadEventConverter;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineMetricsManager;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
import java.util.Date;
import lombok.Getter;
import rx.Observable;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class AppUpdateDisplayable extends CardDisplayable {

  @Getter private String appIconUrl;
  @Getter private String storeIconUrl;
  @Getter private String storeName;

  private Date dateUpdated;
  private String appVersionName;
  private SpannableFactory spannableFactory;
  private String appName;
  @Getter private String packageName;
  private Download download;
  private DateCalculator dateCalculator;
  private long appId;
  @Getter private String abUrl;
  private InstallManager installManager;
  private PermissionManager permissionManager;
  private TimelineMetricsManager timelineMetricsManager;
  private SocialRepository socialRepository;
  private DownloadEventConverter downloadConverter;
  private Analytics analytics;

  public AppUpdateDisplayable() {
  }

  public AppUpdateDisplayable(AppUpdate appUpdate, String appIconUrl, String storeIconUrl,
      String storeName, Date dateUpdated, String appVersionName, SpannableFactory spannableFactory,
      String appName, String packageName, Download download, DateCalculator dateCalculator,
      long appId, String abUrl, InstallManager installManager, PermissionManager permissionManager,
      TimelineMetricsManager timelineMetricsManager, SocialRepository socialRepository,
      DownloadEventConverter downloadConverter, Analytics analytics) {
    super(appUpdate);
    this.appIconUrl = appIconUrl;
    this.storeIconUrl = storeIconUrl;
    this.storeName = storeName;
    this.dateUpdated = dateUpdated;
    this.appVersionName = appVersionName;
    this.spannableFactory = spannableFactory;
    this.appName = appName;
    this.packageName = packageName;
    this.download = download;
    this.dateCalculator = dateCalculator;
    this.appId = appId;
    this.abUrl = abUrl;
    this.installManager = installManager;
    this.permissionManager = permissionManager;
    this.timelineMetricsManager = timelineMetricsManager;
    this.socialRepository = socialRepository;
    this.downloadConverter = downloadConverter;
    this.analytics = analytics;
  }

  public static AppUpdateDisplayable from(AppUpdate appUpdate, SpannableFactory spannableFactory,
      DownloadFactory downloadFactory, DateCalculator dateCalculator, InstallManager installManager,
      PermissionManager permissionManager, TimelineMetricsManager timelineMetricsManager,
      SocialRepository socialRepository) {
    String abTestingURL = null;

    if (appUpdate.getAb() != null
        && appUpdate.getAb().getConversion() != null
        && appUpdate.getAb().getConversion().getUrl() != null) {
      abTestingURL = appUpdate.getAb().getConversion().getUrl();
    }
    return new AppUpdateDisplayable(appUpdate, appUpdate.getIcon(),
        appUpdate.getStore().getAvatar(), appUpdate.getStore().getName(), appUpdate.getAdded(),
        appUpdate.getFile().getVername(), spannableFactory, appUpdate.getName(),
        appUpdate.getPackageName(), downloadFactory.create(appUpdate, Download.ACTION_UPDATE),
        dateCalculator, appUpdate.getId(), abTestingURL, installManager, permissionManager,
        timelineMetricsManager, socialRepository, new DownloadEventConverter(),
        Analytics.getInstance());
  }

  public Observable<Progress<Download>> update(Context context) {
    if (installManager.showWarning()) {
      GenericDialogs.createGenericYesNoCancelMessage(context, null,
          AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog))
          .subscribe(eResponse -> {
            switch (eResponse) {
              case YES:
                installManager.rootInstallAllowed(true);
                break;
              case NO:
                installManager.rootInstallAllowed(false);
                break;
            }
          });
    }
    return installManager.install(context, download).doOnSubscribe(() -> setupDownloadEvent());
  }

  private void setupDownloadEvent() {
    DownloadEvent report = downloadConverter.create(download, DownloadEvent.Action.CLICK,
        DownloadEvent.AppContext.TIMELINE);
    analytics.save(packageName + download.getVersionCode(), report);
  }

  public Observable<Progress<Download>> updateProgress() {
    return installManager.getInstallations()
        .filter(downloadProgress -> (!TextUtils.isEmpty(downloadProgress.getRequest().getMd5())
            && downloadProgress.getRequest().getMd5().equals(download.getMd5())));
  }

  public String getAppName() {
    return this.appName;
  }

  public Spannable getAppTitle(Context context) {
    return spannableFactory.createColorSpan(appName, ContextCompat.getColor(context, R.color.black),
        appName);
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, dateUpdated);
  }

  public Spannable getHasUpdateText(Context context) {
    final String update = context.getString(R.string.displayable_social_timeline_app_update);
    return spannableFactory.createStyleSpan(
        context.getString(R.string.displayable_social_timeline_app_has_update, update),
        Typeface.BOLD, update);
  }

  public Spannable getVersionText(Context context) {
    return spannableFactory.createStyleSpan(
        context.getString(R.string.displayable_social_timeline_app_update_version, appVersionName),
        Typeface.BOLD, appVersionName);
  }

  public Spannable getUpdateAppText(Context context) {
    String application = context.getString(R.string.appstimeline_update_app);
    return spannableFactory.createStyleSpan(
        context.getString(R.string.displayable_social_timeline_app_update_button, application),
        Typeface.NORMAL, application);
  }

  public String getCompletedText(Context context) {
    return context.getString(R.string.displayable_social_timeline_app_update_updated);
  }

  public String getUpdatingText(Context context) {
    return context.getString(R.string.displayable_social_timeline_app_update_updating);
  }

  public String getUpdateErrorText(Context context) {
    return context.getString(R.string.displayable_social_timeline_app_update_error);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_app_update;
  }

  public long getAppId() {
    return appId;
  }

  public Observable<Void> requestPermission(Context context) {
    return permissionManager.requestExternalStoragePermission(((PermissionRequest) context));
  }

  public boolean isInstalling(Progress<Download> downloadProgress) {
    return installManager.isInstalling(downloadProgress);
  }

  public boolean isDownloading(Progress<Download> downloadProgress) {
    return installManager.isDownloading(downloadProgress);
  }

  public void sendClickEvent(SendEventRequest.Body.Data data, String eventName) {
    timelineMetricsManager.sendEvent(data, eventName);
  }

  @Override public void share(Context context, boolean privacyResult) {
    socialRepository.share(getTimelineCard(), context, privacyResult);
  }
}

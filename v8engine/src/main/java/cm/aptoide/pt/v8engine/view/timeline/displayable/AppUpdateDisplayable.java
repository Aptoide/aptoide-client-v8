/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.view.timeline.displayable;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.TextUtils;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.model.v7.timeline.AppUpdate;
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
import cm.aptoide.pt.v8engine.interfaces.ShareCardCallback;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineAnalytics;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import java.util.Date;
import lombok.Getter;
import rx.Observable;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class AppUpdateDisplayable extends CardDisplayable {

  public static final String CARD_TYPE_NAME = "APP_UPDATE";
  @Getter private String appIconUrl;
  @Getter private String storeIconUrl;
  @Getter private String storeName;
  @Getter private String storeTheme;

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
  private TimelineAnalytics timelineAnalytics;
  private SocialRepository socialRepository;
  private DownloadEventConverter downloadConverter;
  private InstallEventConverter installConverter;
  private Analytics analytics;

  public AppUpdateDisplayable() {
  }

  public AppUpdateDisplayable(AppUpdate appUpdate, String appIconUrl, String storeIconUrl,
      String storeName, Date dateUpdated, String appVersionName, SpannableFactory spannableFactory,
      String appName, String packageName, Download download, DateCalculator dateCalculator,
      long appId, String abUrl, InstallManager installManager, PermissionManager permissionManager,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      DownloadEventConverter downloadConverter, InstallEventConverter installConverter,
      Analytics analytics, String storeTheme) {
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
    this.timelineAnalytics = timelineAnalytics;
    this.socialRepository = socialRepository;
    this.downloadConverter = downloadConverter;
    this.installConverter = installConverter;
    this.analytics = analytics;
    this.storeTheme = storeTheme;
  }

  public static AppUpdateDisplayable from(AppUpdate appUpdate, SpannableFactory spannableFactory,
      DownloadFactory downloadFactory, DateCalculator dateCalculator, InstallManager installManager,
      PermissionManager permissionManager, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository, InstallEventConverter installConverter,
      Analytics analytics, DownloadEventConverter downloadConverter) {
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
        timelineAnalytics, socialRepository, downloadConverter, installConverter, analytics,
        appUpdate.getStore().getAppearance().getTheme());
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
    return installManager.install(context, download).doOnSubscribe(() -> setupEvents());
  }

  private void setupEvents() {
    DownloadEvent report = downloadConverter.create(download, DownloadEvent.Action.CLICK,
        DownloadEvent.AppContext.TIMELINE);
    analytics.save(packageName + download.getVersionCode(), report);

    InstallEvent installEvent =
        installConverter.create(download, DownloadInstallBaseEvent.Action.CLICK,
            DownloadInstallBaseEvent.AppContext.TIMELINE);
    analytics.save(packageName + download.getVersionCode(), installEvent);
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

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_app_update;
  }

  public long getAppId() {
    return appId;
  }

  public Observable<Void> requestPermission(Context context) {
    return permissionManager.requestExternalStoragePermission(((PermissionService) context));
  }

  public boolean isInstalling(Progress<Download> downloadProgress) {
    return installManager.isInstalling(downloadProgress);
  }

  public boolean isDownloading(Progress<Download> downloadProgress) {
    return installManager.isDownloading(downloadProgress);
  }

  public void sendOpenAppEvent() {
    timelineAnalytics.sendOpenAppEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        getPackageName());
  }

  public void sendUpdateAppEvent() {
    timelineAnalytics.sendUpdateAppEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        getPackageName());
  }

  public void sendOpenStoreEvent() {
    timelineAnalytics.sendAppUpdateOpenStoreEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        getPackageName(), getStoreName());
  }

  @Override
  public void share(Context context, boolean privacyResult, ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard(), context, privacyResult, shareCardCallback);
  }

  @Override public void share(Context context, ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard(), context, shareCardCallback);
  }

  @Override public void like(Context context, String cardType, int rating) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating);
  }

  @Override public void like(Context context, String cardId, String cardType, int rating) {
    socialRepository.like(cardId, cardType, "", rating);
  }

  public String getErrorMessage(Context context, int error) {
    String toReturn = null;
    switch (error) {
      case Download.GENERIC_ERROR:
        toReturn = getUpdateErrorText(context);
        break;
      case Download.NOT_ENOUGH_SPACE_ERROR:
        toReturn = getUpdateNoSpaceErrorText(context);
        break;
    }
    return toReturn;
  }

  public String getUpdateErrorText(Context context) {
    return context.getString(R.string.displayable_social_timeline_app_update_error);
  }

  private String getUpdateNoSpaceErrorText(Context context) {
    return context.getString(R.string.out_of_space_error);
  }
}

/*
 * Copyright (c) 2016.
 * Modified on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.WindowManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.Install;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.download.DownloadEvent;
import cm.aptoide.pt.v8engine.download.DownloadEventConverter;
import cm.aptoide.pt.v8engine.download.DownloadFactory;
import cm.aptoide.pt.v8engine.download.DownloadInstallBaseEvent;
import cm.aptoide.pt.v8engine.download.InstallEvent;
import cm.aptoide.pt.v8engine.download.InstallEventConverter;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.TimelineSocialActionData;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.Date;
import lombok.Getter;
import rx.Observable;

import static cm.aptoide.pt.v8engine.analytics.Analytics.AppsTimeline.BLANK;

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
  private TimelineSocialActionData timelineSocialActionData;
  @Getter private float appRating;
  @Getter private Long updateStoreId;
  private Resources resources;

  public AppUpdateDisplayable() {
  }

  public AppUpdateDisplayable(AppUpdate appUpdate, String appIconUrl, String storeIconUrl,
      String storeName, Date dateUpdated, String appVersionName, SpannableFactory spannableFactory,
      String appName, String packageName, Download download, DateCalculator dateCalculator,
      long appId, String abUrl, InstallManager installManager, PermissionManager permissionManager,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      DownloadEventConverter downloadConverter, InstallEventConverter installConverter,
      Analytics analytics, String storeTheme, Resources resources, WindowManager windowManager) {
    super(appUpdate, timelineAnalytics, windowManager);
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
    this.appRating = appUpdate.getStats()
        .getRating()
        .getAvg();
    this.updateStoreId = appUpdate.getStore()
        .getId();
    this.resources = resources;
  }

  public static AppUpdateDisplayable from(AppUpdate appUpdate, SpannableFactory spannableFactory,
      DownloadFactory downloadFactory, DateCalculator dateCalculator, InstallManager installManager,
      PermissionManager permissionManager, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository, InstallEventConverter installConverter,
      Analytics analytics, DownloadEventConverter downloadConverter, Resources resources,
      WindowManager windowManager) {
    String abTestingURL = null;

    if (appUpdate.getAb() != null
        && appUpdate.getAb()
        .getConversion() != null
        && appUpdate.getAb()
        .getConversion()
        .getUrl() != null) {
      abTestingURL = appUpdate.getAb()
          .getConversion()
          .getUrl();
    }
    return new AppUpdateDisplayable(appUpdate, appUpdate.getIcon(), appUpdate.getStore()
        .getAvatar(), appUpdate.getStore()
        .getName(), appUpdate.getAdded(), appUpdate.getFile()
        .getVername(), spannableFactory, appUpdate.getName(), appUpdate.getPackageName(),
        downloadFactory.create(appUpdate, Download.ACTION_UPDATE), dateCalculator,
        appUpdate.getId(), abTestingURL, installManager, permissionManager, timelineAnalytics,
        socialRepository, downloadConverter, installConverter, analytics, appUpdate.getStore()
        .getAppearance()
        .getTheme(), resources, windowManager);
  }

  public Observable<Install> update(Context context) {
    if (installManager.showWarning()) {
      GenericDialogs.createGenericYesNoCancelMessage(context, null,
          AptoideUtils.StringU.getFormattedString(R.string.root_access_dialog, resources))
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
    return installManager.install(download)
        .andThen(installManager.getInstall(download.getMd5(), download.getPackageName(),
            download.getVersionCode()))
        .doOnSubscribe(() -> setupEvents());
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

  public void sendAppUpdateCardClickEvent(String action, String socialAction) {
    timelineAnalytics.sendAppUpdateCardClickEvent(CARD_TYPE_NAME, action, socialAction,
        getPackageName(), getStoreName());
  }

  @Override
  public void share(String cardId, boolean privacyResult, ShareCardCallback shareCardCallback,
      Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), updateStoreId, privacyResult,
        shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, getPackageName(),
            getStoreName(), BLANK));
  }

  @Override
  public void share(String cardId, ShareCardCallback shareCardCallback, Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), updateStoreId, shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, getPackageName(),
            getStoreName(), BLANK));
  }

  @Override public void like(Context context, String cardType, int rating, Resources resources) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, getPackageName(), getStoreName(),
            BLANK));
  }

  @Override public void like(Context context, String cardId, String cardType, int rating,
      Resources resources) {
    socialRepository.like(cardId, cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, getPackageName(), getStoreName(),
            BLANK));
  }

  public @StringRes int getUpdateErrorText() {
    return R.string.displayable_social_timeline_app_update_error;
  }

  public @StringRes int getUpdateNoSpaceErrorText() {
    return R.string.out_of_space_error;
  }

  public Spannable getStyledTitle(Context context) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_title_card_title_has_update_present_singular,
            storeName), ContextCompat.getColor(context, R.color.black_87_alpha), storeName);
  }
}

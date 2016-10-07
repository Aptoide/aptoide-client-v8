/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rx.Observable;

/**
 * Created by marcelobenites on 6/17/16.
 */
@AllArgsConstructor public class AppUpdateDisplayable extends Displayable {

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
  private InstallManager installManager;
  private PermissionManager permissionManager;

  public AppUpdateDisplayable() {
  }

  public static AppUpdateDisplayable from(AppUpdate appUpdate, SpannableFactory spannableFactory,
      DownloadFactory downloadFactory, DateCalculator dateCalculator, InstallManager installManager,
      PermissionManager permissionManager) {
    return new AppUpdateDisplayable(appUpdate.getIcon(), appUpdate.getStore().getAvatar(),
        appUpdate.getStore().getName(), appUpdate.getAdded(), appUpdate.getFile().getVername(),
        spannableFactory, appUpdate.getName(), appUpdate.getPackageName(),
        downloadFactory.create(appUpdate, Download.ACTION_UPDATE), dateCalculator,
        appUpdate.getId(), installManager, permissionManager);
  }

  public Observable<Progress<Download>> update(Context context) {
    return installManager.install(context, download);
  }

  public Observable<Progress<Download>> updateProgress() {
    return installManager.getInstallations()
        .filter(
            downloadProgress -> downloadProgress.getRequest().getAppId() == download.getAppId());
  }

  public int getMarginWidth(Context context, int orientation) {
    if (!context.getResources().getBoolean(R.bool.is_this_a_tablet_device)) {
      return 0;
    }

    int width = AptoideUtils.ScreenU.getCachedDisplayWidth(orientation);

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
      return (int) (width * 0.2);
    } else {
      return (int) (width * 0.1);
    }
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

  @Override public Type getType() {
    return Type.SOCIAL_TIMELINE;
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
}

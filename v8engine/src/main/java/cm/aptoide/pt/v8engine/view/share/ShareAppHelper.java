package cm.aptoide.pt.v8engine.view.share;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.repository.InstalledRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.spotandshare.SpotAndShareAnalytics;
import cm.aptoide.pt.v8engine.spotandshare.view.RadarActivity;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.dialog.SharePreviewDialog;
import rx.Observable;

/**
 * Created by neuro on 16-05-2017.
 */

public class ShareAppHelper {

  private final InstalledRepository installedRepository;
  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;
  private final SpotAndShareAnalytics spotAndShareAnalytics;
  private final Activity activity;
  private final TimelineAnalytics timelineAnalytics;
  private final SharedPreferences sharedPreferences;

  public ShareAppHelper(InstalledRepository installedRepository,
      AptoideAccountManager accountManager, AccountNavigator accountNavigator, Activity activity,
      SpotAndShareAnalytics spotAndShareAnalytics, TimelineAnalytics timelineAnalytics,
      SharedPreferences sharedPreferences) {
    this.installedRepository = installedRepository;
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.activity = activity;
    this.spotAndShareAnalytics = spotAndShareAnalytics;
    this.timelineAnalytics = timelineAnalytics;
    this.sharedPreferences = sharedPreferences;
  }

  private boolean isInstalled(String packageName) {
    return installedRepository.contains(packageName);
  }

  public void shareApp(String appName, String packageName, String wUrl, String iconPath,
      float averageRating, String origin) {

    String title = activity.getString(R.string.share);

    Observable<ShareDialogs.ShareResponse> genericAppviewShareDialog =
        isInstalled(packageName) ? ShareDialogs.createAppviewShareWithSpotandShareDialog(activity,
            title) : ShareDialogs.createAppviewShareDialog(activity, title);

    genericAppviewShareDialog.subscribe(eResponse -> {
      if (ShareDialogs.ShareResponse.SHARE_EXTERNAL == eResponse) {
        caseDefaultShare(appName, wUrl);
      } else if (ShareDialogs.ShareResponse.SHARE_TIMELINE == eResponse) {
        caseAppsTimelineShare(appName, packageName, iconPath, averageRating);
      } else if (ShareDialogs.ShareResponse.SHARE_SPOT_AND_SHARE == eResponse) {
        caseSpotAndShareShare(appName, packageName, origin);
      }
    }, CrashReport.getInstance()::log);
  }

  public void shareApp(String appName, String packageName, String iconPath, String origin) {
    ShareDialogs.createInstalledShareWithSpotandShareDialog(activity,
        activity.getString(R.string.share))
        .subscribe(shareResponse -> {
          if (ShareDialogs.ShareResponse.SHARE_TIMELINE == shareResponse) {
            caseAppsTimelineShare(appName, packageName, iconPath, 0);
          } else if (ShareDialogs.ShareResponse.SHARE_SPOT_AND_SHARE == shareResponse) {
            caseSpotAndShareShare(appName, packageName, origin);
          }
        }, CrashReport.getInstance()::log);
  }

  @Partners private void caseDefaultShare(String appName, String wUrl) {
    if (wUrl != null) {
      Intent sharingIntent = new Intent(Intent.ACTION_SEND);
      sharingIntent.setType("text/plain");
      sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
          activity.getString(R.string.install) + " \"" + appName + "\"");
      sharingIntent.putExtra(Intent.EXTRA_TEXT, wUrl);
      activity.startActivity(
          Intent.createChooser(sharingIntent, activity.getString(R.string.share)));
    }
  }

  private void caseAppsTimelineShare(String appName, String packageName, String iconPath,
      float averageRating) {
    if (!accountManager.isLoggedIn()) {
      ShowMessage.asSnack(activity, R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> accountNavigator.navigateToAccountView(
              Analytics.Account.AccountOrigins.APP_VIEW_SHARE));
      return;
    }
    if (Application.getConfiguration()
        .isCreateStoreAndSetUserPrivacyAvailable()) {
      SharePreviewDialog sharePreviewDialog = new SharePreviewDialog(accountManager, false,
          SharePreviewDialog.SharePreviewOpenMode.SHARE, timelineAnalytics, sharedPreferences);
      AlertDialog.Builder alertDialog =
          sharePreviewDialog.getCustomRecommendationPreviewDialogBuilder(activity, appName,
              iconPath, averageRating);
      SocialRepository socialRepository =
          RepositoryFactory.getSocialRepository(activity, timelineAnalytics, sharedPreferences);

      sharePreviewDialog.showShareCardPreviewDialog(packageName, null, "app", activity,
          sharePreviewDialog, alertDialog, socialRepository);
    }
  }

  private void caseSpotAndShareShare(String appName, String packageName, String origin) {
    spotAndShareAnalytics.clickShareApps(origin);

    String filepath = getFilepath(packageName);
    String appNameToShare = filterAppName(appName);

    Intent intent = RadarActivity.buildIntent(activity, filepath, appNameToShare);

    activity.startActivity(intent);
  }

  private String getFilepath(String packageName) {
    PackageManager packageManager = activity.getPackageManager();
    PackageInfo packageInfo = null;
    try {
      packageInfo = packageManager.getPackageInfo(packageName, 0);
      return packageInfo.applicationInfo.sourceDir;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Required packageName not installed! " + packageName);
    }
  }

  private String filterAppName(String appName) {
    if (!TextUtils.isEmpty(appName) && appName.length() > 17) {
      appName = appName.substring(0, 17);
    }
    if (!TextUtils.isEmpty(appName) && appName.contains("_")) {
      appName = appName.replace("_", " ");
    }
    return appName;
  }
}

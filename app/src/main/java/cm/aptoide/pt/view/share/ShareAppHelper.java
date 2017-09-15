package cm.aptoide.pt.view.share;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.timeline.SocialRepository;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.account.AccountNavigator;
import cm.aptoide.pt.view.dialog.SharePreviewDialog;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

/**
 * Created by neuro on 16-05-2017.
 */

public class ShareAppHelper {

  public static final String SPOTANDSHARE_ORIGIN_UPDATES = "UPDATES";
  public static final String SPOTANDSHARE_ORIGIN_APPVIEW = "APPVIEW";
  private final InstalledRepository installedRepository;
  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;
  private final Activity activity;
  private final TimelineAnalytics timelineAnalytics;
  private final SharedPreferences sharedPreferences;
  private final PublishRelay installAppRelay;
  private final boolean createStoreUserPrivacyEnabled;

  public ShareAppHelper(InstalledRepository installedRepository,
      AptoideAccountManager accountManager, AccountNavigator accountNavigator, Activity activity,
      TimelineAnalytics timelineAnalytics, PublishRelay installAppRelay,
      SharedPreferences sharedPreferences, boolean createStoreUserPrivacyEnabled) {
    this.installedRepository = installedRepository;
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.activity = activity;
    this.timelineAnalytics = timelineAnalytics;
    this.sharedPreferences = sharedPreferences;
    this.installAppRelay = installAppRelay;
    this.createStoreUserPrivacyEnabled = createStoreUserPrivacyEnabled;
  }

  private boolean isInstalled(String packageName) {
    return installedRepository.contains(packageName);
  }

  public void shareApp(String appName, String packageName, String wUrl, String iconPath,
      float averageRating, String origin, Long storeId) {

    String title = activity.getString(R.string.share);

    Observable<ShareDialogs.ShareResponse> genericAppviewShareDialog =
        ShareDialogs.createAppviewShareWithSpotandShareDialog(activity, title);

    genericAppviewShareDialog.subscribe(eResponse -> {
      if (ShareDialogs.ShareResponse.SHARE_EXTERNAL == eResponse) {
        caseDefaultShare(appName, wUrl);
      } else if (ShareDialogs.ShareResponse.SHARE_TIMELINE == eResponse) {
        caseAppsTimelineShare(appName, packageName, iconPath, averageRating, storeId);
      } else if (ShareDialogs.ShareResponse.SHARE_SPOT_AND_SHARE == eResponse) {
        if (isInstalled(packageName)) {
          caseSpotAndShareShare(appName, packageName, origin);
        } else {
          showInstallSnackbar(installAppRelay);
        }
      }
    }, CrashReport.getInstance()::log);
  }

  private void showInstallSnackbar(PublishRelay installAppRelay) {
    ShowMessage.asSnack(activity, R.string.appview_message_install_before_share_spotandshare,
        R.string.appview_button_install_before_share_spotandshare, new View.OnClickListener() {
          @Override public void onClick(View v) {
            installAppRelay.call(null);
          }
        }, Snackbar.LENGTH_INDEFINITE);
  }

  public void shareApp(String appName, String packageName, String iconPath, String origin) {
    ShareDialogs.createInstalledShareWithSpotandShareDialog(activity,
        activity.getString(R.string.share))
        .subscribe(shareResponse -> {
          if (ShareDialogs.ShareResponse.SHARE_TIMELINE == shareResponse) {
            caseAppsTimelineShare(appName, packageName, iconPath, 0, null);
          } else if (ShareDialogs.ShareResponse.SHARE_SPOT_AND_SHARE == shareResponse) {
            caseSpotAndShareShare(appName, packageName, origin);
          }
        }, CrashReport.getInstance()::log);
  }

  private void caseDefaultShare(String appName, String wUrl) {
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
      float averageRating, Long storeId) {
    if (!accountManager.isLoggedIn()) {
      ShowMessage.asSnack(activity, R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> accountNavigator.navigateToAccountView(
              Analytics.Account.AccountOrigins.APP_VIEW_SHARE), Snackbar.LENGTH_SHORT);
      return;
    }
    if (createStoreUserPrivacyEnabled) {
      SharePreviewDialog sharePreviewDialog = new SharePreviewDialog(accountManager, false,
          SharePreviewDialog.SharePreviewOpenMode.SHARE, timelineAnalytics, sharedPreferences);
      AlertDialog.Builder alertDialog =
          sharePreviewDialog.getCustomRecommendationPreviewDialogBuilder(activity, appName,
              iconPath, averageRating);
      SocialRepository socialRepository =
          RepositoryFactory.getSocialRepository(activity, timelineAnalytics, sharedPreferences);

      sharePreviewDialog.showShareCardPreviewDialog(packageName, storeId, "app", activity,
          sharePreviewDialog, alertDialog, socialRepository);
    }
  }

  private void caseSpotAndShareShare(String appName, String packageName, String origin) {
    String filepath = getFilepath(packageName);
    String appNameToShare = filterAppName(appName);

    //Intent intent = RadarActivity.buildIntent(activity, filepath, appNameToShare);
    // FIXME: 10-08-2017 implement this way to spotandshare v2
    //activity.startActivity(intent);
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

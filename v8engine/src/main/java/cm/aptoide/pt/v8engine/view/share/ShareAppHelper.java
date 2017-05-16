package cm.aptoide.pt.v8engine.view.share;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.spotandshareandroid.HighwayActivity;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.repository.InstalledRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.spotandshare.SpotAndShareAnalytics;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.dialog.SharePreviewDialog;

/**
 * Created by neuro on 16-05-2017.
 */

public class ShareAppHelper {

  private final InstalledRepository installedRepository;
  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;
  private final ActivityStarter activityStarter;
  private final SpotAndShareAnalytics spotAndShareAnalytics;
  private final Activity activity;

  public ShareAppHelper(InstalledRepository installedRepository,
      AptoideAccountManager accountManager, AccountNavigator accountNavigator,
      ActivityStarter activityStarter, Activity activity,
      SpotAndShareAnalytics spotAndShareAnalytics) {
    this.installedRepository = installedRepository;
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.activityStarter = activityStarter;
    this.activity = activity;
    this.spotAndShareAnalytics = spotAndShareAnalytics;
  }

  public void shareApp(String appName, String packageName, String wUrl, String iconPath) {
    GenericDialogs.createGenericShareDialog(activity, activity.getString(R.string.share),
        installedRepository.contains(packageName))
        .subscribe(eResponse -> {
          if (GenericDialogs.EResponse.SHARE_EXTERNAL == eResponse) {

            shareDefault(appName, packageName, wUrl);
          } else if (GenericDialogs.EResponse.SHARE_TIMELINE == eResponse) {
            if (!accountManager.isLoggedIn()) {
              ShowMessage.asSnack(activity, R.string.you_need_to_be_logged_in, R.string.login,
                  snackView -> accountNavigator.navigateToAccountView(
                      Analytics.Account.AccountOrigins.APP_VIEW_SHARE));
              return;
            }
            if (Application.getConfiguration()
                .isCreateStoreAndSetUserPrivacyAvailable()) {
              SharePreviewDialog sharePreviewDialog = new SharePreviewDialog(accountManager, false,
                  SharePreviewDialog.SharePreviewOpenMode.SHARE);
              AlertDialog.Builder alertDialog =
                  sharePreviewDialog.getCustomRecommendationPreviewDialogBuilder(activity, appName,
                      iconPath);
              SocialRepository socialRepository = RepositoryFactory.getSocialRepository(activity);

              sharePreviewDialog.showShareCardPreviewDialog(packageName, "app", activity,
                  sharePreviewDialog, alertDialog, socialRepository);
            }
          } else if (GenericDialogs.EResponse.SHARE_SPOT_AND_SHARE == eResponse) {

            spotAndShareAnalytics.clickShareApps(
                SpotAndShareAnalytics.SPOT_AND_SHARE_START_CLICK_ORIGIN_APPVIEW);

            String filepath = getFilepath(packageName);
            String appNameToShare = filterAppName(appName);
            Intent intent = new Intent(activity, HighwayActivity.class);
            intent.setAction("APPVIEW_SHARE");
            intent.putExtra("APPVIEW_SHARE_FILEPATH", filepath);
            intent.putExtra("APPVIEW_SHARE_APPNAME", appNameToShare);
            activityStarter.startActivity(intent);
          }
        }, err -> err.printStackTrace());
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

  @Partners protected void shareDefault(String appName, String packageName, String wUrl) {
    if (wUrl != null) {
      Intent sharingIntent = new Intent(Intent.ACTION_SEND);
      sharingIntent.setType("text/plain");
      sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
          activity.getString(R.string.install) + " \"" + appName + "\"");
      sharingIntent.putExtra(Intent.EXTRA_TEXT, wUrl);
      activityStarter.startActivity(
          Intent.createChooser(sharingIntent, activity.getString(R.string.share)));
    }
  }
}

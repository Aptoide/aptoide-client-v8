package cm.aptoide.pt.share;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.timeline.SocialRepository;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.utils.design.ShowMessage;
import rx.Observable;

/**
 * Created by neuro on 16-05-2017.
 */

public class ShareAppHelper {

  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;
  private final Activity activity;
  private final TimelineAnalytics timelineAnalytics;
  private final SharedPreferences sharedPreferences;
  private final boolean createStoreUserPrivacyEnabled;

  public ShareAppHelper(AptoideAccountManager accountManager, AccountNavigator accountNavigator,
      Activity activity, TimelineAnalytics timelineAnalytics, SharedPreferences sharedPreferences,
      boolean createStoreUserPrivacyEnabled) {
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.activity = activity;
    this.timelineAnalytics = timelineAnalytics;
    this.sharedPreferences = sharedPreferences;
    this.createStoreUserPrivacyEnabled = createStoreUserPrivacyEnabled;
  }

  public void shareApp(String appName, String packageName, String wUrl, Long storeId) {

    String title = activity.getString(R.string.share);

    Observable<ShareDialogs.ShareResponse> genericAppviewShareDialog =
        ShareDialogs.createAppviewShareDialog(activity, title);

    genericAppviewShareDialog.subscribe(eResponse -> {
      if (ShareDialogs.ShareResponse.SHARE_EXTERNAL == eResponse) {
        caseDefaultShare(appName, wUrl);
      } else if (ShareDialogs.ShareResponse.SHARE_TIMELINE == eResponse) {
        caseAppsTimelineShare(packageName, storeId);
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

  private void caseAppsTimelineShare(String packageName, Long storeId) {
    if (!accountManager.isLoggedIn()) {
      ShowMessage.asSnack(activity, R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> accountNavigator.navigateToAccountView(
              AccountAnalytics.AccountOrigins.APP_VIEW_SHARE), Snackbar.LENGTH_SHORT);
      return;
    }
    if (createStoreUserPrivacyEnabled) {
      SocialRepository socialRepository =
          RepositoryFactory.getSocialRepository(activity, timelineAnalytics, sharedPreferences);
      LayoutInflater inflater = LayoutInflater.from(activity);
      AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
      View alertDialogView = inflater.inflate(R.layout.logged_in_share, null);
      alertDialog.setView(alertDialogView);

      alertDialogView.findViewById(R.id.continue_button)
          .setOnClickListener(view -> {
            socialRepository.share(packageName, storeId, "app");
            ShowMessage.asSnack(activity, R.string.social_timeline_share_dialog_title);
            timelineAnalytics.sendRecommendedAppInteractEvent(packageName, "Recommend");
            timelineAnalytics.sendSocialCardPreviewActionEvent(
                TimelineAnalytics.SOCIAL_CARD_ACTION_SHARE_CONTINUE);
            alertDialog.dismiss();
          });

      alertDialogView.findViewById(R.id.skip_button)
          .setOnClickListener(view -> {
            timelineAnalytics.sendRecommendedAppInteractEvent(packageName, "Skip");
            timelineAnalytics.sendSocialCardPreviewActionEvent(
                TimelineAnalytics.SOCIAL_CARD_ACTION_SHARE_CANCEL);
            alertDialog.dismiss();
          });

      alertDialogView.findViewById(R.id.dont_show_button)
          .setVisibility(View.GONE);

      alertDialog.show();
      timelineAnalytics.sendRecommendedAppImpressionEvent(packageName);
    }
  }
}

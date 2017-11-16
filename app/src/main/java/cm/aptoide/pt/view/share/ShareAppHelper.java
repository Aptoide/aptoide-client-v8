package cm.aptoide.pt.view.share;

/**
 * Created by danielchen on 16/11/2017.
 */

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
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.timeline.SocialRepository;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.dialog.SharePreviewDialog;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

/**
 * Created by neuro on 16-05-2017.
 */

public class ShareAppHelper {

  private final InstalledRepository installedRepository;
  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;
  private final Activity activity;
  private final SharedPreferences sharedPreferences;
  private final PublishRelay installAppRelay;
  private final boolean createStoreUserPrivacyEnabled;

  public ShareAppHelper(InstalledRepository installedRepository,
      AptoideAccountManager accountManager, AccountNavigator accountNavigator, Activity activity,
      PublishRelay installAppRelay, SharedPreferences sharedPreferences,
      boolean createStoreUserPrivacyEnabled) {
    this.installedRepository = installedRepository;
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.activity = activity;
    this.sharedPreferences = sharedPreferences;
    this.installAppRelay = installAppRelay;
    this.createStoreUserPrivacyEnabled = createStoreUserPrivacyEnabled;
  }

  private boolean isInstalled(String packageName) {
    return installedRepository.contains(packageName);
  }

  public void shareApp(String appName, String wUrl) {
    caseDefaultShare(appName, wUrl);
  }

  public void caseDefaultShare(String appName, String wUrl) {
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
}
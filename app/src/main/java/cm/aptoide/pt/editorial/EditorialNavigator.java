package cm.aptoide.pt.editorial;

import android.net.Uri;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.navigator.ActivityNavigator;

/**
 * Created by D01 on 27/08/2018.
 */

public class EditorialNavigator {
  private final ActivityNavigator activityNavigator;
  private final AppNavigator appNavigator;
  private final AccountNavigator accountNavigator;

  public EditorialNavigator(ActivityNavigator activityNavigator, AppNavigator appNavigator,
      AccountNavigator accountNavigator) {
    this.activityNavigator = activityNavigator;
    this.appNavigator = appNavigator;
    this.accountNavigator = accountNavigator;
  }

  public void navigateToUri(String uri) {
    activityNavigator.navigateTo(Uri.parse(uri));
  }

  public void navigateToAppView(long appId, String packageName) {
    appNavigator.navigateWithAppId(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, "");
  }

  public void navigateToLogIn() {
    accountNavigator.navigateToAccountView(AccountAnalytics.AccountOrigins.EDITORIAL);
  }
}

package cm.aptoide.pt.app.view;

import android.net.Uri;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.navigator.ActivityNavigator;

/**
 * Created by D01 on 27/08/2018.
 */

public class EditorialNavigator {
  private final ActivityNavigator activityNavigator;
  private final AppNavigator appNavigator;

  public EditorialNavigator(ActivityNavigator activityNavigator, AppNavigator appNavigator) {
    this.activityNavigator = activityNavigator;
    this.appNavigator = appNavigator;
  }

  public void navigateToUri(String uri) {
    activityNavigator.navigateTo(Uri.parse(uri));
  }

  public void navigateToAppView(long appId, String packageName) {
    appNavigator.navigateWithAppId(appId, packageName, NewAppViewFragment.OpenType.OPEN_ONLY, "");
  }
}

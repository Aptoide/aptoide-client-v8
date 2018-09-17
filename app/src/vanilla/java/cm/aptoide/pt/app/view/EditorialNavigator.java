package cm.aptoide.pt.app.view;

import android.net.Uri;
import cm.aptoide.pt.navigator.ActivityNavigator;

/**
 * Created by D01 on 27/08/2018.
 */

public class EditorialNavigator {
  private final ActivityNavigator activityNavigator;

  public EditorialNavigator(ActivityNavigator activityNavigator) {
    this.activityNavigator = activityNavigator;
  }

  public void navigateToUri(String uri) {
    activityNavigator.navigateTo(Uri.parse(uri));
  }
}

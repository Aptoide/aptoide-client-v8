package cm.aptoide.pt.v8engine.view.app;

import android.net.Uri;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.v8engine.view.app.screenshots.ScreenshotsViewerFragment;
import cm.aptoide.pt.v8engine.view.navigator.ActivityNavigator;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import java.util.ArrayList;

public class AppViewNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final ActivityNavigator activityNavigator;

  public AppViewNavigator(FragmentNavigator fragmentNavigator,
      ActivityNavigator activityNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.activityNavigator = activityNavigator;
  }

  public void navigateToScreenshots(ArrayList<String> imagesUris, int currentPosition) {
    Fragment fragment = ScreenshotsViewerFragment.newInstance(imagesUris, currentPosition);
    fragmentNavigator.navigateTo(fragment);
  }

  public void navigateToUri(Uri uri) {
    activityNavigator.navigateTo(uri);
  }
}

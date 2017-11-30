package cm.aptoide.pt.app.view;

import android.net.Uri;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.search.view.SearchResultFragment;
import cm.aptoide.pt.app.view.screenshots.ScreenshotsViewerFragment;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
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
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateToUri(Uri uri) {
    activityNavigator.navigateTo(uri);
  }

  public void navigateToOtherVersions(String appName, String icon, String packageName) {
    Fragment fragment = AptoideApplication.getFragmentProvider()
        .newOtherVersionsFragment(appName, icon, packageName);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateToAppView(long appId, String packageName, String tag) {
    Fragment fragment = AptoideApplication.getFragmentProvider()
        .newAppViewFragment(appId, packageName, tag);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateToSearch(String appName, boolean onlyShowTrustedApps) {
    Fragment fragment = SearchResultFragment.newInstance(appName, onlyShowTrustedApps);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void buyApp(GetAppMeta.App app) {
    Fragment fragment = fragmentNavigator.peekLast();
    if (fragment != null && AppViewFragment.class.isAssignableFrom(fragment.getClass())) {
      ((AppViewFragment) fragment).buyApp(app);
    }
  }
}

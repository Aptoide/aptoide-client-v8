package cm.aptoide.pt.app.view;

import android.net.Uri;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.app.view.screenshots.ScreenshotsViewerFragment;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.search.view.SearchResultFragment;
import java.util.ArrayList;

public class AppViewNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final ActivityNavigator activityNavigator;
  private final boolean hasMultiStoreSearch;
  private final String defaultStoreName;

  public AppViewNavigator(FragmentNavigator fragmentNavigator, ActivityNavigator activityNavigator,
      boolean hasMultiStoreSearch, String defaultStoreName) {
    this.fragmentNavigator = fragmentNavigator;
    this.activityNavigator = activityNavigator;
    this.hasMultiStoreSearch = hasMultiStoreSearch;
    this.defaultStoreName = defaultStoreName;
  }

  public void navigateToScreenshots(ArrayList<String> imagesUris, int currentPosition) {
    Fragment fragment = ScreenshotsViewerFragment.newInstance(imagesUris, currentPosition);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateToUri(Uri uri) {
    activityNavigator.navigateTo(uri);
  }

  public void navigateToOtherVersions(String appName, String icon, String packageName) {
    final Fragment fragment;
    if (hasMultiStoreSearch) {
      fragment = OtherVersionsFragment.newInstance(appName, icon, packageName);
    } else {
      fragment = OtherVersionsFragment.newInstance(appName, icon, packageName, defaultStoreName);
    }
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateToAppView(long appId, String packageName, String tag) {
    Fragment fragment = AptoideApplication.getFragmentProvider()
        .newAppViewFragment(appId, packageName, tag);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateToDescriptionReadMore(String name, String description, String theme) {
    Fragment fragment = AptoideApplication.getFragmentProvider()
        .newDescriptionFragment(name, description, theme);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateToSearch(String appName, boolean onlyShowTrustedApps) {
    Fragment fragment =
        SearchResultFragment.newInstance(appName, onlyShowTrustedApps, defaultStoreName);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void buyApp(GetAppMeta.App app) {
    Fragment fragment = fragmentNavigator.peekLast();
    if (fragment != null && AppViewFragment.class.isAssignableFrom(fragment.getClass())) {
      ((AppViewFragment) fragment).buyApp(app);
    }
  }

  public void navigateToStore(Store store) {
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newStoreFragment(store.getName(), store.getAppearance()
            .getTheme()), true);
  }
}

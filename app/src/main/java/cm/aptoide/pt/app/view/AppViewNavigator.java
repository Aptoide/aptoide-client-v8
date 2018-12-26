package cm.aptoide.pt.app.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.ads.data.AptoideNativeAd;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.donations.view.DonateDialogFragment;
import cm.aptoide.pt.app.view.screenshots.ScreenshotsViewerFragment;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.reviews.RateAndReviewsFragment;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.share.NotLoggedInShareFragment;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

public class AppViewNavigator {

  private final int NOT_LOGGED_IN_SHARE_REQUEST_CODE = 13;
  private final FragmentNavigator fragmentNavigator;
  private final ActivityNavigator activityNavigator;
  private final boolean hasMultiStoreSearch;
  private final String defaultStoreName;
  private final AppNavigator appNavigator;

  public AppViewNavigator(FragmentNavigator fragmentNavigator, ActivityNavigator activityNavigator,
      boolean hasMultiStoreSearch, String defaultStoreName, AppNavigator appNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.activityNavigator = activityNavigator;
    this.hasMultiStoreSearch = hasMultiStoreSearch;
    this.defaultStoreName = defaultStoreName;
    this.appNavigator = appNavigator;
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
    appNavigator.navigateWithAppId(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, tag);
  }

  public void navigateToAd(AptoideNativeAd ad, String tag) {
    appNavigator.navigateWithAd(new SearchAdResult(ad), tag);
  }

  public void navigateToDescriptionReadMore(String name, String description, String theme) {
    Fragment fragment = AptoideApplication.getFragmentProvider()
        .newDescriptionFragment(name, description, theme);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void buyApp(long appId) {
    Fragment fragment = fragmentNavigator.peekLast();
    if (fragment != null && AppViewFragment.class.isAssignableFrom(fragment.getClass())) {
      ((AppViewFragment) fragment).buyApp(appId);
    }
  }

  public void navigateToStore(Store store) {
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newStoreFragment(store.getName(), store.getAppearance()
            .getTheme()), true);
  }

  public void navigateToRateAndReview(long appId, String appName, String storeName,
      String packageName, String storeTheme) {
    fragmentNavigator.navigateTo(
        RateAndReviewsFragment.newInstance(appId, appName, storeName, packageName, storeTheme),
        true);
  }

  public void navigateToNotLoggedInShareFragmentForResult(String packageName) {
    fragmentNavigator.navigateForResult(NotLoggedInShareFragment.newInstance(packageName),
        NOT_LOGGED_IN_SHARE_REQUEST_CODE, false);
  }

  public void navigateToAppCoinsInfo() {
    fragmentNavigator.navigateTo(new AppCoinsInfoFragment(), true);
  }

  public Observable<Boolean> notLoggedInViewResults() {
    return fragmentNavigator.results(NOT_LOGGED_IN_SHARE_REQUEST_CODE)
        .map(result -> result.getResultCode() == Activity.RESULT_OK);
  }

  public void navigateToDonationsDialog(String packageName, String tag) {
    boolean hasWallet = hasWallet();
    fragmentNavigator.navigateToDialogFragment(
        DonateDialogFragment.newInstance(packageName, hasWallet));
  }

  private boolean hasWallet() {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    Uri uri = Uri.parse("ethereum:");
    intent.setData(uri);

    PackageManager manager = activityNavigator.getActivity()
        .getPackageManager();
    List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
    return !infos.isEmpty();
  }
}

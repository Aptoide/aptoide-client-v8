package cm.aptoide.pt.app.view;

import android.app.Activity;
import android.net.Uri;
import androidx.fragment.app.Fragment;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.CatappultNavigator;
import cm.aptoide.pt.ads.data.AptoideNativeAd;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.screenshots.ScreenshotsViewerFragment;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.download.view.outofspace.OutOfSpaceDialogFragment;
import cm.aptoide.pt.download.view.outofspace.OutOfSpaceNavigatorWrapper;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.reviews.RateAndReviewsFragment;
import cm.aptoide.pt.search.model.SearchAdResult;
import java.util.ArrayList;
import rx.Observable;

public class AppViewNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final ActivityNavigator activityNavigator;
  private final AppNavigator appNavigator;
  private final CatappultNavigator catappultNavigator;

  public AppViewNavigator(FragmentNavigator fragmentNavigator, ActivityNavigator activityNavigator,
      AppNavigator appNavigator, CatappultNavigator catappultNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.activityNavigator = activityNavigator;
    this.appNavigator = appNavigator;
    this.catappultNavigator = catappultNavigator;
  }

  public void navigateToScreenshots(ArrayList<String> imagesUris, int currentPosition) {
    Fragment fragment = ScreenshotsViewerFragment.newInstance(imagesUris, currentPosition);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateToUri(Uri uri) {
    activityNavigator.navigateTo(uri);
  }

  public void navigateToOtherVersions(String appName, String icon, String packageName) {
    fragmentNavigator.navigateTo(OtherVersionsFragment.newInstance(appName, icon, packageName),
        true);
  }

  public void navigateToAppView(long appId, String packageName, String tag) {
    appNavigator.navigateWithAppId(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, tag);
  }

  public void navigateToAd(AptoideNativeAd ad, String tag) {
    appNavigator.navigateWithAd(new SearchAdResult(ad), tag);
  }

  public void navigateToDescriptionReadMore(String name, String description, boolean hasAppc) {
    Fragment fragment = AptoideApplication.getFragmentProvider()
        .newDescriptionFragment(name, description, hasAppc);
    fragmentNavigator.navigateTo(fragment, true);
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

  public void navigateToAppCoinsInfo() {
    fragmentNavigator.navigateTo(AppCoinsInfoFragment.newInstance(true), true);
  }

  public void navigateToCatappultWebsite() {
    catappultNavigator.navigateToCatappultWebsite();
  }

  public void navigateToOutOfSpaceDialog(long appSize, String packageName) {
    fragmentNavigator.navigateToDialogForResult(
        OutOfSpaceDialogFragment.Companion.newInstance(appSize, packageName),
        OutOfSpaceDialogFragment.OUT_OF_SPACE_REQUEST_CODE);
  }

  public Observable<OutOfSpaceNavigatorWrapper> outOfSpaceDialogResult() {
    return fragmentNavigator.results(OutOfSpaceDialogFragment.OUT_OF_SPACE_REQUEST_CODE)
        .map(result -> new OutOfSpaceNavigatorWrapper(result.getResultCode() == Activity.RESULT_OK,
            result.getData() != null ? result.getData()
                .getPackage() : ""));
  }

  public void navigateToESkillsSectionOnAppCoinsInfoView() {
    appNavigator.navigateToESkillsSectionOfAppCoinsInfoView();
  }
}

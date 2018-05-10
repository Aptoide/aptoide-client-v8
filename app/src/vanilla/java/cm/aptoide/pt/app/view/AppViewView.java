package cm.aptoide.pt.app.view;

import cm.aptoide.pt.app.AdsViewModel;
import cm.aptoide.pt.app.DetailedAppViewModel;
import cm.aptoide.pt.app.ReviewsViewModel;
import cm.aptoide.pt.app.view.screenshots.ScreenShotClickEvent;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.app.DetailedApp;
import rx.Observable;
import rx.Single;

/**
 * Created by franciscocalado on 08/05/18.
 */

public interface AppViewView extends View {

  void showLoading();

  void showAppview();

  long getAppId();

  String getPackageName();

  void populateAppDetails(DetailedAppViewModel detailedApp);

  Observable<ScreenShotClickEvent> getScreenshotClickEvent();

  Observable<ReadMoreClickEvent> clickedReadMore();

  Single<Void> populateReviewsAndAds(ReviewsViewModel reviews, AdsViewModel ads);

  Observable<Void> clickWorkingFlag();

  Observable<Void> clickLicenseFlag();

  Observable<Void> clickFakeFlag();

  Observable<Void> clickVirusFlag();

  void displayNotLoggedInSnack();

  void displayStoreFollowedSnack(String storeName);

  Observable<Void> clickDeveloperWebsite();

  Observable<Void> clickDeveloperEmail();

  Observable<Void> clickDeveloperPrivacy();

  Observable<Void> clickDeveloperPermissions();

  Observable<Void> clickStoreLayout();

  Observable<Void> clickFollowStore();

  Observable<Void> clickOtherVersions();

  Observable<Void> clickTrustedBadge();

  void navigateToDeveloperWebsite(DetailedApp app);

  void navigateToDeveloperEmail(DetailedApp app);

  void navigateToDeveloperPrivacy(DetailedApp app);

  void navigateToDeveloperPermissions(DetailedApp app);

  void setFollowButton(boolean isFollowing);

  void showTrustedDialog(DetailedApp app);

  String getLanguageFilter();
}

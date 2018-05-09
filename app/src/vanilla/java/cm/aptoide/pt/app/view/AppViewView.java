package cm.aptoide.pt.app.view;

import cm.aptoide.pt.app.AdsViewModel;
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

  void populateAppDetails(DetailedApp detailedApp);

  Observable<ScreenShotClickEvent> getScreenshotClickEvent();

  Observable<ReadMoreClickEvent> clickedReadMore();

  Single<Void> populateReviewsAndAds(ReviewsViewModel reviews, AdsViewModel ads);

  Observable<Void> clickWorkingFlag();

  Observable<Void> clickLicenseFlag();

  Observable<Void> clickFakeFlag();

  Observable<Void> clickVirusFlag();

  void displayNotLoggedInSnack();
}

package cm.aptoide.pt.app.view;

import android.view.MenuItem;
import cm.aptoide.pt.app.DetailedAppViewModel;
import cm.aptoide.pt.app.ReviewsViewModel;
import cm.aptoide.pt.app.SimilarAppsViewModel;
import cm.aptoide.pt.app.view.screenshots.ScreenShotClickEvent;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.share.ShareDialogs;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.app.DetailedApp;
import rx.Observable;

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

  Void populateReviewsAndAds(ReviewsViewModel reviews, SimilarAppsViewModel ads, DetailedApp app);

  Observable<GetAppMeta.GetAppMetaFile.Flags.Vote.Type> clickWorkingFlag();

  Observable<GetAppMeta.GetAppMetaFile.Flags.Vote.Type> clickLicenseFlag();

  Observable<GetAppMeta.GetAppMetaFile.Flags.Vote.Type> clickFakeFlag();

  Observable<GetAppMeta.GetAppMetaFile.Flags.Vote.Type> clickVirusFlag();

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

  Observable<Void> clickRateApp();

  Observable<Void> clickRateAppLarge();

  Observable<Void> clickRateAppLayout();

  Observable<Void> clickCommentsLayout();

  Observable<Void> clickReadAllComments();

  Observable<Void> clickLoginSnack();

  Observable<SimilarAppClickEvent> clickSimilarApp();

  Observable<MenuItem> clickToolbar();

  Observable<ShareDialogs.ShareResponse> shareDialogResponse();

  Observable<Integer> scrollReviewsResponse();

  void navigateToDeveloperWebsite(DetailedApp app);

  void navigateToDeveloperEmail(DetailedApp app);

  void navigateToDeveloperPrivacy(DetailedApp app);

  void navigateToDeveloperPermissions(DetailedApp app);

  void setFollowButton(boolean isFollowing);

  void showTrustedDialog(DetailedApp app);

  String getLanguageFilter();

  Observable<GenericDialogs.EResponse> showRateDialog(String appName, String packageName,
      String storeName);

  void disableFlags();

  void enableFlags();

  void incrementFlags(GetAppMeta.GetAppMetaFile.Flags.Vote.Type type);

  void showFlagVoteSubmittedMessage();

  void showShareDialog();

  void showShareOnTvDialog();

  void defaultShare(String appName, String wUrl);

  void recommendsShare(String packageName, Long storeId);

  void scrollReviews(Integer position);
}

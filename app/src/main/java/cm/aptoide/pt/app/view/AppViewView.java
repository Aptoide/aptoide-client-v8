package cm.aptoide.pt.app.view;

import android.util.Pair;
import android.view.MenuItem;
import cm.aptoide.pt.app.AppModel;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.app.ReviewsViewModel;
import cm.aptoide.pt.app.view.screenshots.ScreenShotClickEvent;
import cm.aptoide.pt.app.view.similar.SimilarAppClickEvent;
import cm.aptoide.pt.app.view.similar.SimilarAppsBundle;
import cm.aptoide.pt.appview.InstallAppView;
import cm.aptoide.pt.bonus.BonusAppcModel;
import cm.aptoide.pt.promotions.Promotion;
import cm.aptoide.pt.promotions.WalletApp;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.app.DetailedAppRequestResult;
import cm.aptoide.pt.view.app.FlagsVote;
import com.jakewharton.rxbinding.view.ViewScrollChangeEvent;
import java.util.List;
import rx.Observable;

/**
 * Created by franciscocalado on 08/05/18.
 */

public interface AppViewView extends InstallAppView {

  void showLoading();

  void showAppView(AppModel detailedApp);

  void handleError(DetailedAppRequestResult.Error error);

  Observable<ScreenShotClickEvent> getScreenshotClickEvent();

  Observable<ReadMoreClickEvent> clickedReadMore();

  void populateReviews(ReviewsViewModel reviews, AppModel app);

  void populateSimilar(List<SimilarAppsBundle> similarAppsViewModel);

  Observable<FlagsVote.VoteType> clickWorkingFlag();

  Observable<FlagsVote.VoteType> clickLicenseFlag();

  Observable<FlagsVote.VoteType> clickFakeFlag();

  Observable<FlagsVote.VoteType> clickVirusFlag();

  Observable<Void> clickGetAppcInfo();

  Observable<Void> clickBonusAppcFlair();

  Observable<Void> clickCatappultCard();

  void displayNotLoggedInSnack();

  void displayStoreFollowedSnack(String storeName);

  Observable<ViewScrollChangeEvent> scrollVisibleSimilarApps();

  Observable<Boolean> similarAppsVisibilityFromInstallClick();

  boolean isSimilarAppsVisible();

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

  Observable<Void> clickReviewsLayout();

  Observable<Void> clickReadAllReviews();

  Observable<Void> clickLoginSnack();

  Observable<SimilarAppClickEvent> clickSimilarApp();

  Observable<MenuItem> clickToolbar();

  Observable<Void> clickErrorRetry();

  Observable<String> apkfyDialogPositiveClick();

  Observable<Integer> scrollReviewsResponse();

  void navigateToDeveloperWebsite(AppModel app);

  void navigateToDeveloperEmail(AppModel app);

  void navigateToDeveloperPrivacy(AppModel app);

  void navigateToDeveloperPermissions(AppModel app);

  void setFollowButton(boolean isFollowing);

  void showTrustedDialog(AppModel app);

  String getLanguageFilter();

  Observable<GenericDialogs.EResponse> showRateDialog(String appName, String packageName,
      String storeName);

  void disableFlags();

  void enableFlags();

  void incrementFlags(FlagsVote.VoteType type);

  void showFlagVoteSubmittedMessage();

  void showShareOnTvDialog(long appId);

  void defaultShare(String appName, String wUrl);

  void scrollReviews(Integer position);

  void hideReviews();

  void hideSimilarApps();

  void extractReferrer(SearchAdResult searchAdResult);

  void recoverScrollViewState();

  Observable<Void> showOpenAndInstallDialog(String title, String appName);

  Observable<Void> showOpenAndInstallApkFyDialog(String title, String appName, double appc,
      float rating, String icon, int downloads);

  void showApkfyElement(String appName);

  void setupAppcAppView(boolean hasBilling, BonusAppcModel bonusAppcModel);

  void showAppcWalletPromotionView(Promotion promotionViewModel, WalletApp walletApp,
      Promotion.ClaimAction action, DownloadModel appDownloadModel);

  void showEskillsWalletView(WalletApp walletApp);

  void setupEskillsAppView(String appName);

  Observable<Promotion> dismissWalletPromotionClick();

  void dismissWalletPromotionView();

  Observable<Pair<Promotion, WalletApp>> installWalletButtonClick();

  Observable<WalletApp> pausePromotionDownload();

  Observable<WalletApp> cancelPromotionDownload();

  Observable<WalletApp> resumePromotionDownload();

  Observable<Promotion> claimAppClick();

  Observable<Void> iabInfoClick();

  void showDownloadingSimilarApps(boolean hasSimilarApps);

  void setInstallButton(AppCoinsViewModel appCoinsViewModel);

  void showDownloadError(DownloadModel downloadModel);

  Observable<Void> eSkillsCardClick();
}

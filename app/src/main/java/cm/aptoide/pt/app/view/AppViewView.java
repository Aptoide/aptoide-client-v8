package cm.aptoide.pt.app.view;

import android.view.MenuItem;
import cm.aptoide.pt.ads.MoPubInterstitialAdClickType;
import cm.aptoide.pt.app.AppViewViewModel;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.app.ReviewsViewModel;
import cm.aptoide.pt.app.WalletPromotionViewModel;
import cm.aptoide.pt.app.view.donations.Donation;
import cm.aptoide.pt.app.view.screenshots.ScreenShotClickEvent;
import cm.aptoide.pt.app.view.similar.SimilarAppClickEvent;
import cm.aptoide.pt.app.view.similar.SimilarAppsBundle;
import cm.aptoide.pt.appview.InstallAppView;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.share.ShareDialogs;
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

  void showAppView(AppViewViewModel detailedApp);

  void handleError(DetailedAppRequestResult.Error error);

  Observable<ScreenShotClickEvent> getScreenshotClickEvent();

  Observable<ReadMoreClickEvent> clickedReadMore();

  void populateReviews(ReviewsViewModel reviews, AppViewViewModel app);

  void populateSimilar(List<SimilarAppsBundle> similarAppsViewModel);

  Observable<FlagsVote.VoteType> clickWorkingFlag();

  Observable<FlagsVote.VoteType> clickLicenseFlag();

  Observable<FlagsVote.VoteType> clickFakeFlag();

  Observable<FlagsVote.VoteType> clickVirusFlag();

  Observable<Void> clickGetAppcInfo();

  void displayNotLoggedInSnack();

  void displayStoreFollowedSnack(String storeName);

  Observable<ViewScrollChangeEvent> scrollVisibleSimilarApps();

  Observable<Boolean> similarAppsVisibility();

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

  Observable<Void> clickNoNetworkRetry();

  Observable<Void> clickGenericRetry();

  Observable<Void> clickTopDonorsDonateButton();

  Observable<ShareDialogs.ShareResponse> shareDialogResponse();

  Observable<String> apkfyDialogPositiveClick();

  Observable<Integer> scrollReviewsResponse();

  void navigateToDeveloperWebsite(AppViewViewModel app);

  void navigateToDeveloperEmail(AppViewViewModel app);

  void navigateToDeveloperPrivacy(AppViewViewModel app);

  void navigateToDeveloperPermissions(AppViewViewModel app);

  void setFollowButton(boolean isFollowing);

  void showTrustedDialog(AppViewViewModel app);

  String getLanguageFilter();

  Observable<GenericDialogs.EResponse> showRateDialog(String appName, String packageName,
      String storeName);

  void disableFlags();

  void enableFlags();

  void incrementFlags(FlagsVote.VoteType type);

  void showFlagVoteSubmittedMessage();

  void showShareDialog();

  void showShareOnTvDialog(long appId);

  void defaultShare(String appName, String wUrl);

  void recommendsShare(String packageName, Long storeId);

  void scrollReviews(Integer position);

  void hideReviews();

  void hideSimilarApps();

  void extractReferrer(SearchAdResult searchAdResult);

  void recoverScrollViewState();

  Observable<DownloadModel.Action> showOpenAndInstallDialog(String title, String appName);

  Observable<DownloadModel.Action> showOpenAndInstallApkFyDialog(String title, String appName,
      double appc, float rating, String icon, int downloads);

  void showApkfyElement(String appName);

  void showDonations(List<Donation> donations);

  void initInterstitialAd();

  Observable<MoPubInterstitialAdClickType> InterstitialAdClicked();

  Observable<MoPubInterstitialAdClickType> interstitialAdLoaded();

  void showInterstitialAd();

  void showBannerAd();

  void setupAppcAppView();

  void showAppcWalletPromotionView(WalletPromotionViewModel walletPromotionViewModel);

  Observable<Void> dismissWalletPromotionClick();

  void dismissWalletPromotionView();

  Observable<WalletPromotionViewModel> installWalletButtonClick();

  Observable<WalletPromotionViewModel> pausePromotionDownload();

  Observable<WalletPromotionViewModel> cancelPromotionDownload();

  Observable<WalletPromotionViewModel> resumePromotionDownload();

  Observable<WalletPromotionViewModel> claimAppClick();

  void showDownloadingSimilarApps(boolean hasSimilarApps);
}

package cm.aptoide.pt.app.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.ads.MoPubBannerAdListener;
import cm.aptoide.pt.ads.MoPubInterstitialAdClickType;
import cm.aptoide.pt.ads.MoPubInterstitialAdListener;
import cm.aptoide.pt.app.AppBoughtReceiver;
import cm.aptoide.pt.app.AppReview;
import cm.aptoide.pt.app.AppViewViewModel;
import cm.aptoide.pt.app.DownloadAppViewModel;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.app.ReviewsViewModel;
import cm.aptoide.pt.app.WalletPromotionViewModel;
import cm.aptoide.pt.app.view.donations.Donation;
import cm.aptoide.pt.app.view.donations.DonationsAdapter;
import cm.aptoide.pt.app.view.screenshots.ScreenShotClickEvent;
import cm.aptoide.pt.app.view.screenshots.ScreenshotsAdapter;
import cm.aptoide.pt.app.view.similar.SimilarAppClickEvent;
import cm.aptoide.pt.app.view.similar.SimilarAppsBundle;
import cm.aptoide.pt.app.view.similar.SimilarAppsBundleAdapter;
import cm.aptoide.pt.billing.exception.BillingException;
import cm.aptoide.pt.billing.purchase.PaidAppPurchase;
import cm.aptoide.pt.billing.view.BillingActivity;
import cm.aptoide.pt.billing.view.PurchaseBundleMapper;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.home.SnapToStartHelper;
import cm.aptoide.pt.install.view.remote.RemoteInstallDialog;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.permission.DialogPermissions;
import cm.aptoide.pt.reviews.LanguageFilterHelper;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.share.ShareDialogs;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.util.AppUtils;
import cm.aptoide.pt.util.ReferrerUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.view.app.AppDeveloper;
import cm.aptoide.pt.view.app.AppFlags;
import cm.aptoide.pt.view.app.AppMedia;
import cm.aptoide.pt.view.app.DetailedAppRequestResult;
import cm.aptoide.pt.view.app.FlagsVote;
import cm.aptoide.pt.view.dialog.DialogBadgeV7;
import cm.aptoide.pt.view.dialog.DialogUtils;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import cm.aptoide.pt.view.recycler.LinearLayoutManagerWithSmoothScroller;
import com.jakewharton.rxbinding.support.v4.widget.RxNestedScrollView;
import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewScrollChangeEvent;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;
import rx.subjects.PublishSubject;
import rx.subscriptions.Subscriptions;

import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

/**
 * Created by franciscocalado on 07/05/18.
 */

public class AppViewFragment extends NavigationTrackFragment implements AppViewView {
  private static final int DOWNLOADING = 1;
  private static final int DOWNGRADE = 2;
  private static final int INSTALL = 3;
  private static final int CLAIM = 4;
  private static final int UPDATE = 5;
  private static final int DOWNLOAD = 6;
  private static final String KEY_SCROLL_Y = "y";
  private static final String BADGE_DIALOG_TAG = "badgeDialog";
  private static final int PAY_APP_REQUEST_CODE = 12;
  private static final int APPC_TRANSITION_MS = 1000;
  @Inject AppViewPresenter presenter;
  @Inject DialogUtils dialogUtils;
  @Inject @Named("marketName") String marketName;
  @Inject @Named("aptoide-theme") String theme;
  @Inject @Named("rating-one-decimal-format") DecimalFormat oneDecimalFormat;
  private Menu menu;
  private Toolbar toolbar;
  private ActionBar actionBar;
  private ScreenshotsAdapter screenshotsAdapter;
  private TopReviewsAdapter reviewsAdapter;
  private DonationsAdapter donationsAdapter;
  private SimilarAppsBundleAdapter similarListAdapter;
  private PublishSubject<ScreenShotClickEvent> screenShotClick;
  private PublishSubject<ReadMoreClickEvent> readMoreClick;
  private PublishSubject<Void> loginSnackClick;
  private PublishSubject<SimilarAppClickEvent> similarAppClick;
  private PublishSubject<ShareDialogs.ShareResponse> shareDialogClick;
  private PublishSubject<Integer> reviewsAutoScroll;
  private PublishSubject<Void> noNetworkRetryClick;
  private PublishSubject<Void> genericRetryClick;
  private PublishSubject<Void> ready;
  private PublishSubject<Void> shareRecommendsDialogClick;
  private PublishSubject<Void> skipRecommendsDialogClick;
  private PublishSubject<Void> dontShowAgainRecommendsDialogClick;
  private PublishSubject<AppBoughClickEvent> appBought;
  private PublishSubject<String> apkfyDialogConfirmSubject;
  private PublishSubject<Boolean> similarAppsVisibilitySubject;
  private PublishSubject<DownloadModel.Action> installClickSubject;
  private PublishSubject<MoPubInterstitialAdClickType> interstitialClick;

  //Views
  private View noNetworkErrorView;
  private View genericErrorView;
  private View genericRetryButton;
  private View noNetworkRetryButton;
  private View reviewsLayout;
  private View downloadControlsLayout;
  private ImageView appIcon;
  private TextView appName;
  private View trustedLayout;
  private ImageView trustedBadge;
  private TextView trustedText;
  private TextView downloadsTop;
  private TextView sizeInfo;
  private TextView ratingInfo;
  private View appcRewardView;
  private View appcMigrationWarningMessage;
  private TextView appcRewardValue;
  private View versionsLayout;
  private TextView latestVersionTitle;
  private TextView latestVersion;
  private TextView rewardAppLatestVersion;
  private TextView otherVersions;
  private RecyclerView screenshots;
  private TextView descriptionText;
  private Button descriptionReadMore;
  private ContentLoadingProgressBar topReviewsProgress;
  private View ratingLayout;
  private View emptyReviewsLayout;
  private View topReviewsLayout;
  private Button rateAppButtonLarge;
  private TextView emptyReviewTextView;
  private TextView reviewUsers;
  private TextView avgReviewScore;
  private RatingBar avgReviewScoreBar;
  private RecyclerView reviewsView;
  private Button rateAppButton;
  private Button showAllReviewsButton;
  private View goodAppLayoutWrapper;
  private View flagsLayoutWrapper;
  private View workingWellLayout;
  private View needsLicenseLayout;
  private View fakeAppLayout;
  private View virusLayout;
  private TextView workingWellText;
  private TextView needsLicenceText;
  private TextView fakeAppText;
  private TextView virusText;
  private View storeLayout;
  private ImageView storeIcon;
  private TextView storeName;
  private TextView storeFollowers;
  private TextView storeDownloads;
  private Button storeFollow;
  private RecyclerView similarListRecyclerView;
  private View similarDownloadPlaceholder;
  private View similarBottomPlaceholder;
  private View infoWebsite;
  private View infoEmail;
  private View infoPrivacy;
  private View infoPermissions;
  private ProgressBar viewProgress;
  private View appview;
  private Button install;
  private LinearLayout downloadInfoLayout;
  private ProgressBar downloadProgressBar;
  private TextView downloadProgressValue;
  private ImageView cancelDownload;
  private ImageView pauseDownload;
  private ImageView resumeDownload;
  private DownloadModel.Action action;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private AdsRepository adsRepository;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private QManager qManager;
  private PurchaseBundleMapper purchaseBundleMapper;
  private Subscription errorMessageSubscription;
  private NestedScrollView scrollView;
  private int scrollViewY;
  private AppViewAppcInfoViewHolder appcInfoView;
  private View apkfyElement;
  private View donationsElement;
  private RecyclerView donationsList;
  private View donationsListEmptyState;
  private View donationsListLayout;
  private ProgressBar donationsProgress;
  private Button listDonateButton;
  private MoPubInterstitial interstitialAd;
  private MoPubView bannerAd;
  private View flagThisAppSection;
  private View collapsingAppcBackground;

  //wallet promotion
  private View promotionView;
  private View walletPromotionDownloadLayout;
  private View walletPromotionClaimLayout;
  private View walletPromotionIcon;
  private Button walletPromotionClaimButton;
  private View walletPromotionInstallDisableLayout;
  private Button walletPromotionInstallDisableButton;
  private TextView walletPromotionTitle;
  private TextView walletPromotionMessage;
  private View walletPromotionButtonsLayout;
  private Button walletPromotionCancelButton;
  private Button walletPromotionDownloadButton;
  private ProgressBar downloadWalletProgressBar;
  private TextView downloadWalletProgressValue;
  private ImageView cancelWalletDownload;
  private ImageView pauseWalletDownload;
  private ImageView resumeWalletDownload;
  private View walletDownloadControlsLayout;
  private PublishSubject<PromotionEvent> promotionAppClick;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    screenShotClick = PublishSubject.create();
    readMoreClick = PublishSubject.create();
    loginSnackClick = PublishSubject.create();
    similarAppClick = PublishSubject.create();
    shareDialogClick = PublishSubject.create();
    ready = PublishSubject.create();
    reviewsAutoScroll = PublishSubject.create();
    noNetworkRetryClick = PublishSubject.create();
    genericRetryClick = PublishSubject.create();
    apkfyDialogConfirmSubject = PublishSubject.create();
    similarAppsVisibilitySubject = PublishSubject.create();
    shareRecommendsDialogClick = PublishSubject.create();
    skipRecommendsDialogClick = PublishSubject.create();
    dontShowAgainRecommendsDialogClick = PublishSubject.create();
    appBought = PublishSubject.create();
    installClickSubject = PublishSubject.create();
    interstitialClick = PublishSubject.create();
    promotionAppClick = PublishSubject.create();

    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    qManager = application.getQManager();
    httpClient = application.getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    purchaseBundleMapper = application.getPurchaseBundleMapper();
    adsRepository = application.getAdsRepository();
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    scrollView = (NestedScrollView) view.findViewById(R.id.scroll_view_app);
    noNetworkErrorView = view.findViewById(R.id.no_network_connection);
    genericErrorView = view.findViewById(R.id.generic_error);
    genericRetryButton = genericErrorView.findViewById(R.id.retry);
    noNetworkRetryButton = noNetworkErrorView.findViewById(R.id.retry);
    reviewsLayout = view.findViewById(R.id.reviews_layout);
    downloadControlsLayout = view.findViewById(R.id.install_controls_layout);
    noNetworkRetryButton.setOnClickListener(click -> noNetworkRetryClick.onNext(null));
    genericRetryButton.setOnClickListener(click -> genericRetryClick.onNext(null));
    appIcon = view.findViewById(R.id.app_icon);
    trustedBadge = view.findViewById(R.id.trusted_badge);
    appName = view.findViewById(R.id.app_name);
    trustedLayout = view.findViewById(R.id.trusted_layout);
    trustedText = view.findViewById(R.id.trusted_text);
    downloadsTop = view.findViewById(R.id.header_downloads);
    sizeInfo = view.findViewById(R.id.header_size);
    ratingInfo = view.findViewById(R.id.header_rating);
    appcRewardView = view.findViewById(R.id.appc_layout);
    appcMigrationWarningMessage = view.findViewById(R.id.migration_warning);
    appcRewardValue = view.findViewById(R.id.appcoins_reward_message);
    appcInfoView =
        new AppViewAppcInfoViewHolder((LinearLayout) view.findViewById(R.id.iap_appc_label),
            appcRewardView, appcRewardValue,
            (TextView) appcRewardView.findViewById(R.id.appc_billing_text_secondary));
    versionsLayout = view.findViewById(R.id.versions_layout);
    latestVersionTitle = (TextView) view.findViewById(R.id.latest_version_title);
    latestVersion = versionsLayout.findViewById(R.id.latest_version);
    rewardAppLatestVersion = view.findViewById(R.id.appview_reward_app_versions_element);
    otherVersions = (TextView) view.findViewById(R.id.other_versions);

    screenshots = (RecyclerView) view.findViewById(R.id.screenshots_list);
    screenshots.setLayoutManager(
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
    screenshots.setNestedScrollingEnabled(false);

    descriptionText = (TextView) view.findViewById(R.id.description_text);
    descriptionReadMore = (Button) view.findViewById(R.id.description_see_more);
    topReviewsProgress = (ContentLoadingProgressBar) view.findViewById(R.id.top_comments_progress);
    ratingLayout = view.findViewById(R.id.rating_layout);
    emptyReviewsLayout = view.findViewById(R.id.empty_reviews_layout);
    topReviewsLayout = view.findViewById(R.id.comments_layout);
    rateAppButtonLarge = (Button) view.findViewById(R.id.rate_this_button2);
    emptyReviewTextView = (TextView) view.findViewById(R.id.empty_review_text);
    reviewUsers = (TextView) view.findViewById(R.id.users_voted);
    avgReviewScore = (TextView) view.findViewById(R.id.rating_value);
    avgReviewScoreBar = (RatingBar) view.findViewById(R.id.rating_bar);
    reviewsView = (RecyclerView) view.findViewById(R.id.top_comments_list);
    rateAppButton = (Button) view.findViewById(R.id.rate_this_button);
    showAllReviewsButton = (Button) view.findViewById(R.id.read_all_button);
    apkfyElement = view.findViewById(R.id.apkfy_element);

    flagThisAppSection = view.findViewById(R.id.flag_this_app_section);
    goodAppLayoutWrapper = view.findViewById(R.id.good_app_layout);
    flagsLayoutWrapper = view.findViewById(R.id.rating_flags_layout);
    workingWellLayout = view.findViewById(R.id.working_well_layout);
    needsLicenseLayout = view.findViewById(R.id.needs_licence_layout);
    fakeAppLayout = view.findViewById(R.id.fake_app_layout);
    virusLayout = view.findViewById(R.id.virus_layout);
    donationsElement = view.findViewById(R.id.donations_element);
    donationsList = view.findViewById(R.id.donations_list);
    donationsListEmptyState = view.findViewById(R.id.donations_list_empty_state);
    donationsProgress = view.findViewById(R.id.donations_progress);
    donationsListLayout = view.findViewById(R.id.donations_list_layout);
    listDonateButton = view.findViewById(R.id.donate_button);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()) {
      @Override public boolean canScrollVertically() {
        return false;
      }
    };
    donationsList.setLayoutManager(linearLayoutManager);

    workingWellText = (TextView) view.findViewById(R.id.working_well_count);
    needsLicenceText = (TextView) view.findViewById(R.id.needs_licence_count);
    fakeAppText = (TextView) view.findViewById(R.id.fake_app_count);
    virusText = (TextView) view.findViewById(R.id.virus_count);
    storeLayout = view.findViewById(R.id.store_uploaded_layout);
    storeIcon = (ImageView) view.findViewById(R.id.store_icon);
    storeName = (TextView) view.findViewById(R.id.store_name);
    storeFollowers = (TextView) view.findViewById(R.id.user_count);
    storeDownloads = (TextView) view.findViewById(R.id.download_count);
    storeFollow = (Button) view.findViewById(R.id.follow_button);
    similarListRecyclerView = view.findViewById(R.id.similar_list);
    similarDownloadPlaceholder = (View) view.findViewById(R.id.similar_download_placeholder);
    similarBottomPlaceholder = (View) view.findViewById(R.id.similar_bottom_placeholder);
    infoWebsite = view.findViewById(R.id.website_label);
    infoEmail = view.findViewById(R.id.email_label);
    infoPrivacy = view.findViewById(R.id.privacy_policy_label);
    infoPermissions = view.findViewById(R.id.permissions_label);

    viewProgress = (ProgressBar) view.findViewById(R.id.appview_progress);
    appview = view.findViewById(R.id.appview_full);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    collapsingAppcBackground = view.findViewById(R.id.collapsing_appc_coins_background);

    install = ((Button) view.findViewById(R.id.appview_install_button));
    downloadInfoLayout = ((LinearLayout) view.findViewById(R.id.appview_transfer_info));
    downloadProgressBar = ((ProgressBar) view.findViewById(R.id.appview_download_progress_bar));
    downloadProgressValue = (TextView) view.findViewById(R.id.appview_download_progress_number);
    cancelDownload = ((ImageView) view.findViewById(R.id.appview_download_cancel_button));
    resumeDownload = ((ImageView) view.findViewById(R.id.appview_download_resume_download));
    pauseDownload = ((ImageView) view.findViewById(R.id.appview_download_pause_download));

    promotionView = view.findViewById(R.id.wallet_install_promotion);
    walletPromotionTitle = promotionView.findViewById(R.id.wallet_title);
    walletPromotionMessage = promotionView.findViewById(R.id.wallet_message);
    walletPromotionButtonsLayout = promotionView.findViewById(R.id.buttons_layout);
    walletPromotionCancelButton = promotionView.findViewById(R.id.cancel_button);
    walletPromotionDownloadButton = promotionView.findViewById(R.id.download_button);
    walletPromotionDownloadLayout = view.findViewById(R.id.wallet_download_info);
    downloadWalletProgressBar =
        walletPromotionDownloadLayout.findViewById(R.id.wallet_download_progress_bar);
    downloadWalletProgressValue =
        walletPromotionDownloadLayout.findViewById(R.id.wallet_download_progress_number);
    cancelWalletDownload =
        walletPromotionDownloadLayout.findViewById(R.id.wallet_download_cancel_button);
    pauseWalletDownload =
        walletPromotionDownloadLayout.findViewById(R.id.wallet_download_pause_download);
    resumeWalletDownload =
        walletPromotionDownloadLayout.findViewById(R.id.wallet_download_resume_download);
    walletPromotionClaimLayout = view.findViewById(R.id.wallet_claim_appc_layout);
    walletPromotionIcon = view.findViewById(R.id.wallet_icon);
    walletPromotionClaimButton = view.findViewById(R.id.wallet_claim_appc_button);
    walletDownloadControlsLayout = view.findViewById(R.id.wallet_install_controls_layout);
    walletPromotionInstallDisableLayout = view.findViewById(R.id.wallet_install_disabled_layout);
    walletPromotionInstallDisableButton = view.findViewById(R.id.wallet_install_disabled_button);

    donationsAdapter = new DonationsAdapter(new ArrayList<>());
    donationsList.setAdapter(donationsAdapter);

    screenshotsAdapter =
        new ScreenshotsAdapter(new ArrayList<>(), new ArrayList<>(), screenShotClick);
    screenshots.setAdapter(screenshotsAdapter);

    LinearLayoutManagerWithSmoothScroller layoutManager =
        new LinearLayoutManagerWithSmoothScroller(getContext(), LinearLayoutManager.HORIZONTAL,
            false);

    LinearLayoutManager similarBundlesLayout =
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

    similarListRecyclerView.setLayoutManager(similarBundlesLayout);
    similarListRecyclerView.setNestedScrollingEnabled(false);
    setSimilarAppsAdapters();

    reviewsView.setLayoutManager(layoutManager);
    // because otherwise the AppBar won't be collapsed
    reviewsView.setNestedScrollingEnabled(false);

    SnapHelper commentsSnap = new SnapToStartHelper();
    SnapHelper screenshotsSnap = new SnapToStartHelper();
    commentsSnap.attachToRecyclerView(reviewsView);
    screenshotsSnap.attachToRecyclerView(screenshots);

    setupToolbar();

    ((AppBarLayout) view.findViewById(R.id.app_bar_layout)).addOnOffsetChangedListener(
        (appBarLayout, verticalOffset) -> {
          float percentage =
              ((float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange());
          view.findViewById(R.id.app_icon)
              .setAlpha(1 - (percentage * 1.20f));
          view.findViewById(R.id.app_name)
              .setAlpha(1 - (percentage * 1.20f));
          ((ToolbarArcBackground) view.findViewById(R.id.toolbar_background_arc)).setScale(
              percentage);
          collapsingAppcBackground.setAlpha(1 - percentage);
        });

    if (savedInstanceState != null) {
      scrollViewY = savedInstanceState.getInt(KEY_SCROLL_Y, 0);
    }

    collapsingToolbarLayout =
        ((CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar_layout));
    collapsingToolbarLayout.setExpandedTitleColor(
        getResources().getColor(android.R.color.transparent));

    bannerAd = view.findViewById(R.id.mopub_banner);
    attachPresenter(presenter);
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build("AppViewFragment",
        getArguments().getString(BundleKeys.ORIGIN_TAG.name(), ""), StoreContext.meta.name());
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (errorMessageSubscription != null && !errorMessageSubscription.isUnsubscribed()) {
      errorMessageSubscription.unsubscribe();
    }
    screenShotClick = null;
    readMoreClick = null;
    loginSnackClick = null;
    similarAppClick = null;
    shareDialogClick = null;
    ready = null;
    reviewsAutoScroll = null;
    noNetworkRetryClick = null;
    genericRetryClick = null;
    dialogUtils = null;
    presenter = null;
    similarAppsVisibilitySubject = null;
    interstitialClick = null;
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    this.menu = menu;
    inflater.inflate(R.menu.fragment_appview, menu);
    showHideOptionsMenu(true);
  }

  private void destroyAdapter(MoPubRecyclerAdapter adapter) {
    if (adapter != null) {
      adapter.destroy();
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    scrollViewY = scrollView.getScrollY();
    noNetworkErrorView = null;
    genericErrorView = null;
    genericRetryButton = null;
    noNetworkRetryButton = null;
    appIcon = null;
    trustedBadge = null;
    appName = null;
    trustedLayout = null;
    trustedText = null;
    downloadsTop = null;
    sizeInfo = null;
    ratingInfo = null;
    appcRewardView = null;
    appcRewardValue = null;
    latestVersion = null;
    otherVersions = null;
    screenshots = null;
    descriptionText = null;
    reviewsAdapter = null;
    descriptionReadMore = null;
    topReviewsProgress = null;
    ratingLayout = null;
    emptyReviewsLayout = null;
    topReviewsLayout = null;
    rateAppButtonLarge = null;
    emptyReviewTextView = null;
    reviewUsers = null;
    avgReviewScore = null;
    avgReviewScoreBar = null;
    reviewsView = null;
    rateAppButton = null;
    showAllReviewsButton = null;
    goodAppLayoutWrapper = null;
    flagsLayoutWrapper = null;
    workingWellLayout = null;
    needsLicenseLayout = null;
    fakeAppLayout = null;
    virusLayout = null;
    workingWellText = null;
    needsLicenceText = null;
    fakeAppText = null;
    virusText = null;
    storeLayout = null;
    storeIcon = null;
    storeName = null;
    storeFollowers = null;
    storeDownloads = null;
    storeFollow = null;
    infoWebsite = null;
    infoEmail = null;
    infoPrivacy = null;
    infoPermissions = null;
    viewProgress = null;
    appview = null;
    screenshotsAdapter = null;
    menu = null;
    toolbar = null;
    actionBar = null;
    scrollView = null;
    collapsingToolbarLayout = null;
    donationsAdapter = null;
    donationsElement = null;
    donationsList = null;
    interstitialAd = null;
    if (bannerAd != null) {
      bannerAd.destroy();
      bannerAd = null;
    }
  }

  @Override public void showLoading() {
    viewProgress.setVisibility(View.VISIBLE);
    appview.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
    noNetworkErrorView.setVisibility(View.GONE);
  }

  @Override public void showAppView(AppViewViewModel model) {
    collapsingToolbarLayout.setTitle(model.getAppName());

    appName.setText(model.getAppName());
    ImageLoader.with(getContext())
        .load(model.getIcon(), appIcon);
    downloadsTop.setText(
        String.format("%s", AptoideUtils.StringU.withSuffix(model.getPackageDownloads())));
    sizeInfo.setText(AptoideUtils.StringU.formatBytes(model.getSize(), false));
    if (model.getRating()
        .getAverage() == 0) {
      ratingInfo.setText(R.string.appcardview_title_no_stars);
    } else {
      ratingInfo.setText(oneDecimalFormat.format(model.getRating()
          .getAverage()));
    }

    if (getArguments().getFloat(BundleKeys.APPC.name(), -1) != -1f) {
      versionsLayout.setVisibility(View.GONE);
      rewardAppLatestVersion.setVisibility(View.VISIBLE);
      String versionName = model.getVersionName();
      String message =
          String.format(getResources().getString(R.string.appview_latest_version_with_value),
              versionName);
      SpannableString spannable = new SpannableString(message);
      spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey_medium)),
          message.indexOf(versionName), message.indexOf(versionName) + versionName.length(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      rewardAppLatestVersion.setText(spannable);
    } else {
      latestVersion.setText(model.getVersionName());
      if (!model.isLatestTrustedVersion()) {
        latestVersionTitle.setText(getString(R.string.appview_version_text));
        otherVersions.setText(getString(R.string.newer_version_available));
      }
    }
    storeName.setText(model.getStore()
        .getName());
    ImageLoader.with(getContext())
        .loadWithShadowCircleTransform(model.getStore()
            .getAvatar(), storeIcon);
    storeDownloads.setText(String.format("%s", AptoideUtils.StringU.withSuffix(model.getStore()
        .getStats()
        .getDownloads())));
    storeFollowers.setText(String.format("%s", AptoideUtils.StringU.withSuffix(model.getStore()
        .getStats()
        .getSubscribers())));

    if (model.isStoreFollowed()) {
      storeFollow.setText(R.string.followed);
    } else {
      storeFollow.setText(R.string.follow);
    }

    if (model.hasDonations()) {//after getApk webservice is updated
      donationsElement.setVisibility(View.VISIBLE);
      donationsListLayout.setVisibility(View.VISIBLE);
    }

    if ((model.getMedia()
        .getScreenshots() != null && !model.getMedia()
        .getScreenshots()
        .isEmpty()) || (model.getMedia()
        .getVideos() != null && !model.getMedia()
        .getVideos()
        .isEmpty())) {
      screenshotsAdapter.updateScreenshots(model.getMedia()
          .getScreenshots());
      screenshotsAdapter.updateVideos(model.getMedia()
          .getVideos());
    } else {
      screenshots.setVisibility(View.GONE);
    }
    setTrustedBadge(model.getMalware());
    setDescription(model.getMedia()
        .getDescription());
    setAppFlags(model.isGoodApp(), model.getAppFlags());
    setReadMoreClickListener(model.getAppName(), model.getMedia(), model.getStore());
    setDeveloperDetails(model.getDeveloper());
    showAppViewLayout();
    downloadInfoLayout.setVisibility(View.GONE);
    install.setVisibility(View.VISIBLE);
    install.setOnClickListener(click -> installClickSubject.onNext(action));
  }

  @Override public void handleError(DetailedAppRequestResult.Error error) {
    viewProgress.setVisibility(View.GONE);
    switch (error) {
      case NETWORK:
        noNetworkErrorView.setVisibility(View.VISIBLE);
        break;
      case GENERIC:
        genericErrorView.setVisibility(View.VISIBLE);
        break;
    }
  }

  @Override public Observable<ScreenShotClickEvent> getScreenshotClickEvent() {
    return screenShotClick;
  }

  @Override public Observable<ReadMoreClickEvent> clickedReadMore() {
    return readMoreClick;
  }

  @Override public void populateReviews(ReviewsViewModel reviewsModel, AppViewViewModel app) {
    List<AppReview> reviews = reviewsModel.getReviewsList();

    if (reviews != null && !reviews.isEmpty()) {
      showReviews(true, app.getRating()
          .getTotal(), app.getRating()
          .getAverage());

      reviewsAdapter = new TopReviewsAdapter(reviews.toArray(new AppReview[reviews.size()]));
    } else {
      showReviews(false, app.getRating()
          .getTotal(), app.getRating()
          .getAverage());
      reviewsAdapter = new TopReviewsAdapter();
    }

    reviewsView.setAdapter(reviewsAdapter);
    reviewsAutoScroll.onNext(reviewsAdapter.getItemCount());
  }

  @Override public void populateSimilar(List<SimilarAppsBundle> similarApps) {
    similarListAdapter.add(similarApps);
    manageSimilarAppsVisibility(true, false);
  }

  @Override public Observable<FlagsVote.VoteType> clickWorkingFlag() {
    return RxView.clicks(workingWellLayout)
        .flatMap(__ -> Observable.just(FlagsVote.VoteType.GOOD));
  }

  @Override public Observable<FlagsVote.VoteType> clickLicenseFlag() {
    return RxView.clicks(needsLicenseLayout)
        .flatMap(__ -> Observable.just(FlagsVote.VoteType.LICENSE));
  }

  @Override public Observable<FlagsVote.VoteType> clickFakeFlag() {
    return RxView.clicks(fakeAppLayout)
        .flatMap(__ -> Observable.just(FlagsVote.VoteType.FAKE));
  }

  @Override public Observable<FlagsVote.VoteType> clickVirusFlag() {
    return RxView.clicks(virusLayout)
        .flatMap(__ -> Observable.just(FlagsVote.VoteType.VIRUS));
  }

  @Override public Observable<Void> clickGetAppcInfo() {
    return RxView.clicks(appcRewardView);
  }

  @Override public void displayNotLoggedInSnack() {
    Snackbar.make(getView(), R.string.you_need_to_be_logged_in, Snackbar.LENGTH_SHORT)
        .setAction(R.string.login, snackView -> loginSnackClick.onNext(null))
        .show();
  }

  @Override public void displayStoreFollowedSnack(String storeName) {
    String messageToDisplay = String.format(getString(R.string.store_followed), storeName);
    Toast.makeText(getContext(), messageToDisplay, Toast.LENGTH_SHORT)
        .show();
  }

  @Override public Observable<ViewScrollChangeEvent> scrollVisibleSimilarApps() {
    return RxNestedScrollView.scrollChangeEvents(scrollView)
        .filter(__ -> isSimilarAppsVisible());
  }

  @Override public Observable<Boolean> similarAppsVisibility() {
    return similarAppsVisibilitySubject;
  }

  @Override public boolean isSimilarAppsVisible() {
    Rect scrollBounds = new Rect();
    scrollView.getHitRect(scrollBounds);
    return similarListRecyclerView.getLocalVisibleRect(scrollBounds);
  }

  @Override public Observable<Void> clickDeveloperWebsite() {
    return RxView.clicks(infoWebsite);
  }

  @Override public Observable<Void> clickDeveloperEmail() {
    return RxView.clicks(infoEmail);
  }

  @Override public Observable<Void> clickDeveloperPrivacy() {
    return RxView.clicks(infoPrivacy);
  }

  @Override public Observable<Void> clickDeveloperPermissions() {
    return RxView.clicks(infoPermissions);
  }

  @Override public Observable<Void> clickStoreLayout() {
    return RxView.clicks(storeLayout);
  }

  @Override public Observable<Void> clickFollowStore() {
    return RxView.clicks(storeFollow);
  }

  @Override public Observable<Void> clickOtherVersions() {
    return RxView.clicks(otherVersions);
  }

  @Override public Observable<Void> clickTrustedBadge() {
    return RxView.clicks(trustedLayout);
  }

  @Override public Observable<Void> clickRateApp() {
    return RxView.clicks(rateAppButton);
  }

  @Override public Observable<Void> clickRateAppLarge() {
    return RxView.clicks(rateAppButtonLarge);
  }

  @Override public Observable<Void> clickRateAppLayout() {
    return RxView.clicks(ratingLayout);
  }

  @Override public Observable<Void> clickReviewsLayout() {
    return RxView.clicks(topReviewsLayout);
  }

  @Override public Observable<Void> clickReadAllReviews() {
    return RxView.clicks(showAllReviewsButton);
  }

  @Override public Observable<Void> clickLoginSnack() {
    return loginSnackClick;
  }

  @Override public Observable<SimilarAppClickEvent> clickSimilarApp() {
    return similarAppClick;
  }

  @Override public Observable<MenuItem> clickToolbar() {
    return RxToolbar.itemClicks(toolbar);
  }

  @Override public Observable<Void> clickNoNetworkRetry() {
    return noNetworkRetryClick;
  }

  @Override public Observable<Void> clickGenericRetry() {
    return genericRetryClick;
  }

  @Override public Observable<Void> clickTopDonorsDonateButton() {
    return RxView.clicks(listDonateButton);
  }

  @Override public Observable<ShareDialogs.ShareResponse> shareDialogResponse() {
    return shareDialogClick;
  }

  @Override public Observable<String> apkfyDialogPositiveClick() {
    return apkfyDialogConfirmSubject;
  }

  @Override public Observable<Integer> scrollReviewsResponse() {
    return reviewsAutoScroll;
  }

  @Override public void navigateToDeveloperWebsite(AppViewViewModel app) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(app.getDeveloper()
        .getWebsite()));
    getContext().startActivity(browserIntent);
  }

  @Override public void navigateToDeveloperEmail(AppViewViewModel app) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    Uri data = Uri.parse("mailto:" + app.getDeveloper()
        .getEmail() + "?subject=" + "Feedback" + "&body=" + "");
    intent.setData(data);
    getContext().startActivity(intent);
  }

  @Override public void navigateToDeveloperPrivacy(AppViewViewModel app) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(app.getDeveloper()
        .getPrivacy()));
    getContext().startActivity(browserIntent);
  }

  @Override public void navigateToDeveloperPermissions(AppViewViewModel app) {
    DialogPermissions dialogPermissions =
        DialogPermissions.newInstance(app.getAppName(), app.getVersionName(), app.getIcon(),
            AptoideUtils.StringU.formatBytes(AppUtils.sumFileSizes(app.getFileSize(), app.getObb()),
                false), app.getUsedPermissions());
    dialogPermissions.show(getActivity().getSupportFragmentManager(), "");
  }

  @Override public void setFollowButton(boolean isFollowing) {
    if (!isFollowing) storeFollow.setText(R.string.followed);
  }

  @Override public void showTrustedDialog(AppViewViewModel app) {
    DialogBadgeV7.newInstance(marketName, app.getMalware(), app.getAppName(), app.getMalware()
        .getRank())
        .show(getFragmentManager(), BADGE_DIALOG_TAG);
  }

  @Override public String getLanguageFilter() {
    List<String> countryCodes =
        new LanguageFilterHelper(getContext().getResources()).getCurrentLanguageFirst()
            .getCountryCodes();
    return countryCodes.get(0);
  }

  @Override
  public Observable<GenericDialogs.EResponse> showRateDialog(String appName, String packageName,
      String storeName) {
    return dialogUtils.showRateDialog(getActivity(), appName, packageName, storeName);
  }

  @Override public void disableFlags() {
    workingWellLayout.setClickable(false);
    needsLicenseLayout.setClickable(false);
    fakeAppLayout.setClickable(false);
    virusLayout.setClickable(false);
  }

  @Override public void enableFlags() {
    workingWellLayout.setClickable(true);
    needsLicenseLayout.setClickable(true);
    fakeAppLayout.setClickable(true);
    virusLayout.setClickable(true);
  }

  @Override public void incrementFlags(FlagsVote.VoteType type) {
    disableFlags();
    switch (type) {
      case GOOD:
        workingWellText.setText(NumberFormat.getIntegerInstance()
            .format(Double.parseDouble(String.valueOf(new BigDecimal(workingWellText.getText()
                .toString()))) + 1));
        workingWellLayout.setSelected(true);
        workingWellLayout.setPressed(false);
        break;

      case LICENSE:
        needsLicenceText.setText(NumberFormat.getIntegerInstance()
            .format(Double.parseDouble(String.valueOf(new BigDecimal(needsLicenceText.getText()
                .toString()))) + 1));
        needsLicenceText.setSelected(true);
        needsLicenceText.setPressed(false);

        break;

      case FAKE:
        fakeAppText.setText(NumberFormat.getIntegerInstance()
            .format(Double.parseDouble(String.valueOf(new BigDecimal(fakeAppText.getText()
                .toString()))) + 1));
        fakeAppLayout.setSelected(true);
        fakeAppLayout.setPressed(false);
        break;

      case VIRUS:
        virusText.setText(NumberFormat.getIntegerInstance()
            .format(Double.parseDouble(String.valueOf(new BigDecimal(virusText.getText()
                .toString()))) + 1));
        virusLayout.setSelected(true);
        virusLayout.setPressed(false);
        break;

      default:
        throw new IllegalArgumentException("Unable to find Type " + type.name());
    }
  }

  @Override public void showFlagVoteSubmittedMessage() {
    Toast.makeText(getContext(), R.string.vote_submitted, Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void showShareDialog() {
    String title = getActivity().getString(R.string.share);

    ShareDialogs.createAppviewShareDialog(getActivity(), title)
        .subscribe(response -> shareDialogClick.onNext(response));
  }

  @Override public void showShareOnTvDialog(long appId) {
    if (AptoideUtils.SystemU.getConnectionType(
        (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE))
        .equals("mobile")) {
      GenericDialogs.createGenericOkMessage(getContext(),
          getContext().getString(R.string.remote_install_menu_title),
          getContext().getString(R.string.install_on_tv_mobile_error))
          .subscribe(__ -> {
          }, err -> CrashReport.getInstance()
              .log(err));
    } else {
      DialogFragment newFragment = RemoteInstallDialog.newInstance(appId);
      newFragment.show(getActivity().getSupportFragmentManager(),
          RemoteInstallDialog.class.getSimpleName());
    }
  }

  @Override public void defaultShare(String appName, String wUrl) {
    if (wUrl != null) {
      Intent sharingIntent = new Intent(Intent.ACTION_SEND);
      sharingIntent.setType("text/plain");
      sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
          getActivity().getString(R.string.install) + " \"" + appName + "\"");
      sharingIntent.putExtra(Intent.EXTRA_TEXT, wUrl);
      getActivity().startActivity(
          Intent.createChooser(sharingIntent, getActivity().getString(R.string.share)));
    }
  }

  @Override public void recommendsShare(String packageName, Long storeId) {

    AptoideApplication application = (AptoideApplication) getContext().getApplicationContext();
    TimelineAnalytics analytics = application.getTimelineAnalytics();
    if (application.isCreateStoreUserPrivacyEnabled()) {
      LayoutInflater inflater = LayoutInflater.from(getActivity());
      AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
      View alertDialogView = inflater.inflate(R.layout.logged_in_share, null);
      alertDialog.setView(alertDialogView);

      alertDialogView.findViewById(R.id.recommend_button)
          .setOnClickListener(view -> {
            Snackbar.make(getView(), R.string.social_timeline_share_dialog_title,
                Snackbar.LENGTH_SHORT)
                .show();
            analytics.sendRecommendedAppInteractEvent(packageName, "Recommend");
            analytics.sendSocialCardPreviewActionEvent(
                TimelineAnalytics.SOCIAL_CARD_ACTION_SHARE_CONTINUE);
            alertDialog.dismiss();
          });

      alertDialogView.findViewById(R.id.skip_button)
          .setOnClickListener(view -> {
            analytics.sendRecommendedAppInteractEvent(packageName, "Skip");
            analytics.sendSocialCardPreviewActionEvent(
                TimelineAnalytics.SOCIAL_CARD_ACTION_SHARE_CANCEL);
            alertDialog.dismiss();
          });

      alertDialogView.findViewById(R.id.dont_show_button)
          .setVisibility(View.GONE);

      alertDialog.show();
    }
  }

  @Override public void scrollReviews(Integer position) {
    if (reviewsView != null) reviewsView.smoothScrollToPosition(position);
  }

  @Override public void hideReviews() {
    reviewsLayout.setVisibility(View.GONE);
  }

  @Override public void hideSimilarApps() {
    similarListRecyclerView.setVisibility(View.GONE);
  }

  @Override public void extractReferrer(SearchAdResult searchAdResult) {
    AptoideUtils.ThreadU.runOnUiThread(
        () -> ReferrerUtils.extractReferrer(searchAdResult, ReferrerUtils.RETRIES, false,
            adsRepository, httpClient, converterFactory, qManager,
            getContext().getApplicationContext(),
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            new MinimalAdMapper()));
  }

  @Override public void recoverScrollViewState() {
    // TODO: 25/05/2018 remove this hack and find a better way to do it.

    scrollView.post(() -> {
      if (scrollView != null) scrollView.scrollTo(0, scrollViewY);
    });
  }

  @Override
  public Observable<DownloadModel.Action> showOpenAndInstallDialog(String title, String appName) {
    return GenericDialogs.createGenericOkCancelMessage(getContext(), title,
        getContext().getString(R.string.installapp_alrt, appName))
        .filter(response -> response.equals(YES))
        .map(__ -> action);
  }

  @Override public Observable<DownloadModel.Action> showOpenAndInstallApkFyDialog(String title,
      String appName, double appc, float rating, String icon, int downloads) {
    return createCustomDialogForApkfy(appName, appc, rating, icon, downloads).filter(
        response -> response.equals(YES))
        .map(__ -> action);
  }

  @Override public void showApkfyElement(String appName) {
    apkfyElement.setVisibility(View.VISIBLE);
    String message = getString(R.string.appview_message_apkfy_1);
    ((TextView) apkfyElement.findViewById(R.id.apkfy_message_1)).setText(
        String.format(message, appName));
    ((TextView) apkfyElement.findViewById(R.id.apkfy_title)).setText(
        getResources().getString(R.string.appview_title_apkfy));
  }

  @Override public void showDonations(List<Donation> donations) {
    donationsProgress.setVisibility(View.GONE);
    if (donations != null && !donations.isEmpty()) {
      donationsAdapter.setDonations(donations);
      donationsList.setVisibility(View.VISIBLE);
    } else {
      donationsListEmptyState.setVisibility(View.VISIBLE);
    }
  }

  @Override public void initInterstitialAd() {
    interstitialAd =
        new MoPubInterstitial(getActivity(), BuildConfig.MOPUB_VIDEO_APPVIEW_PLACEMENT_ID);
    interstitialAd.setInterstitialAdListener(new MoPubInterstitialAdListener(interstitialClick));
    interstitialAd.load();
  }

  @Override public Observable<MoPubInterstitialAdClickType> InterstitialAdClicked() {
    return interstitialClick.filter(
        clickType -> clickType == MoPubInterstitialAdClickType.INTERSTITIAL_CLICKED);
  }

  @Override public Observable<MoPubInterstitialAdClickType> interstitialAdLoaded() {
    return interstitialClick.filter(
        clickType -> clickType == MoPubInterstitialAdClickType.INTERSTITIAL_LOADED);
  }

  @Override public void showInterstitialAd() {
    interstitialAd.show();
  }

  @Override public void showBannerAd() {
    bannerAd.setBannerAdListener(new MoPubBannerAdListener());
    bannerAd.setAdUnitId(BuildConfig.MOPUB_BANNER_50_APPVIEW_PLACEMENT_ID);
    bannerAd.setVisibility(View.VISIBLE);
    bannerAd.loadAd();
  }

  @Override public void setupAppcAppView() {
    TransitionDrawable transition = (TransitionDrawable) ContextCompat.getDrawable(getContext(),
        R.drawable.appc_gradient_transition);
    collapsingToolbarLayout.setBackgroundDrawable(transition);
    transition.startTransition(APPC_TRANSITION_MS);

    AlphaAnimation animation1 = new AlphaAnimation(0f, 1.0f);
    animation1.setDuration(APPC_TRANSITION_MS);
    collapsingAppcBackground.setAlpha(1f);
    collapsingAppcBackground.setVisibility(View.VISIBLE);
    collapsingAppcBackground.startAnimation(animation1);

    install.setBackgroundDrawable(getContext().getResources()
        .getDrawable(R.drawable.appc_gradient_rounded));
    downloadProgressBar.setProgressDrawable(
        ContextCompat.getDrawable(getContext(), R.drawable.appc_progress));
    flagThisAppSection.setVisibility(View.GONE);
  }

  @Override public void showAppcWalletPromotionView(WalletPromotionViewModel viewModel) {
    if (viewModel.isWalletInstalled()) {
      if (!viewModel.isAppViewAppInstalled()) {
        setupInstallDependencyApp(viewModel);
      } else {
        setupClaimWalletPromotion(viewModel);
      }
    } else {
      if (viewModel.getDownloadModel()
          .isDownloading()) {
        setupActiveWalletPromotion(viewModel);
      } else {
        setupInactiveWalletPromotion(viewModel);
      }
    }
    promotionView.setVisibility(View.VISIBLE);
  }

  @Override public Observable<Void> dismissWalletPromotionClick() {
    return RxView.clicks(walletPromotionCancelButton);
  }

  @Override public void dismissWalletPromotionView() {
    promotionView.setVisibility(View.GONE);
  }

  @Override public Observable<WalletPromotionViewModel> installWalletButtonClick() {
    return promotionAppClick.filter(
        promotionAppClick -> promotionAppClick.getClickType() == PromotionEvent.ClickType.UPDATE
            || promotionAppClick.getClickType() == PromotionEvent.ClickType.INSTALL_APP
            || promotionAppClick.getClickType() == PromotionEvent.ClickType.DOWNLOAD
            || promotionAppClick.getClickType() == PromotionEvent.ClickType.DOWNGRADE)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public Observable<WalletPromotionViewModel> pausePromotionDownload() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        == PromotionEvent.ClickType.PAUSE_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public Observable<WalletPromotionViewModel> cancelPromotionDownload() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        == PromotionEvent.ClickType.CANCEL_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public Observable<WalletPromotionViewModel> resumePromotionDownload() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        == PromotionEvent.ClickType.RESUME_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public Observable<WalletPromotionViewModel> claimAppClick() {
    return promotionAppClick.filter(
        promotionAppClick -> promotionAppClick.getClickType() == PromotionEvent.ClickType.CLAIM)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public void showDownloadingSimilarApps(boolean hasSimilarApps) {
    manageSimilarAppsVisibility(hasSimilarApps, true);
  }

  private void setupInstallDependencyApp(WalletPromotionViewModel viewModel) {
    setupWalletPromotionText(viewModel, R.string.wallet_promotion_wallet_installed_message);
    walletPromotionInstallDisableButton.setText(
        String.format(getString(R.string.wallet_promotion_button_install_disabled),
            String.valueOf(viewModel.getAppcValue())));
    walletPromotionInstallDisableLayout.setVisibility(View.VISIBLE);
    walletPromotionDownloadLayout.setVisibility(View.GONE);
    walletPromotionButtonsLayout.setVisibility(View.GONE);
    walletPromotionClaimLayout.setVisibility(View.GONE);
    walletPromotionIcon.setVisibility(View.GONE);
  }

  private void setupClaimWalletPromotion(WalletPromotionViewModel viewModel) {
    setupWalletPromotionText(viewModel, R.string.wallet_promotion_wallet_claim_message);
    walletPromotionClaimButton.setText(
        String.format(getString(R.string.wallet_promotion_button_claim),
            String.valueOf(viewModel.getAppcValue())));
    walletPromotionDownloadLayout.setVisibility(View.GONE);
    walletPromotionInstallDisableLayout.setVisibility(View.GONE);
    walletPromotionButtonsLayout.setVisibility(View.GONE);
    walletPromotionClaimLayout.setVisibility(View.VISIBLE);
    walletPromotionClaimButton.setOnClickListener(__ -> promotionAppClick.onNext(
        new PromotionEvent(viewModel, getClickType(getState(viewModel)))));
    walletPromotionIcon.setVisibility(View.VISIBLE);
  }

  private void setupWalletPromotionText(WalletPromotionViewModel viewModel,
      int walletMessageStringId) {
    walletPromotionTitle.setText(String.format(getString(R.string.wallet_promotion_title),
        String.valueOf(viewModel.getAppcValue())));
    walletPromotionMessage.setText(
        String.format(getString(walletMessageStringId), String.valueOf(viewModel.getAppcValue())));
  }

  private int getState(WalletPromotionViewModel app) {
    int state;
    DownloadModel downloadModel = app.getDownloadModel();
    if (downloadModel.isDownloading()) {
      return DOWNLOADING;
    } else {
      switch (downloadModel.getAction()) {
        case DOWNGRADE:
          state = DOWNGRADE;
          break;
        case INSTALL:
          state = INSTALL;
          break;
        case OPEN:
          state = CLAIM;
          break;
        case UPDATE:
          state = UPDATE;
          break;
        default:
          throw new IllegalArgumentException("Invalid type of download action");
      }
      return state;
    }
  }

  private PromotionEvent.ClickType getClickType(int appState) {
    PromotionEvent.ClickType clickType;
    switch (appState) {
      case DOWNGRADE:
        clickType = PromotionEvent.ClickType.DOWNGRADE;
        break;
      case UPDATE:
        clickType = PromotionEvent.ClickType.UPDATE;
        break;
      case DOWNLOAD:
        clickType = PromotionEvent.ClickType.DOWNLOAD;
        break;
      case INSTALL:
        clickType = PromotionEvent.ClickType.INSTALL_APP;
        break;
      case CLAIM:
        clickType = PromotionEvent.ClickType.CLAIM;
        break;
      default:
        throw new IllegalArgumentException("Wrong view type of promotion app");
    }
    return clickType;
  }

  private void setupInactiveWalletPromotion(WalletPromotionViewModel viewModel) {
    setupWalletPromotionText(viewModel, R.string.wallet_promotion_wallet_notinstalled_message);

    walletPromotionDownloadLayout.setVisibility(View.GONE);
    walletPromotionInstallDisableLayout.setVisibility(View.GONE);
    walletPromotionClaimLayout.setVisibility(View.GONE);
    walletPromotionButtonsLayout.setVisibility(View.VISIBLE);
    walletPromotionIcon.setVisibility(View.VISIBLE);
    walletPromotionDownloadButton.setOnClickListener(__ -> promotionAppClick.onNext(
        new PromotionEvent(viewModel, PromotionEvent.ClickType.INSTALL_APP)));
  }

  private void setupActiveWalletPromotion(WalletPromotionViewModel viewModel) {
    setupWalletPromotionText(viewModel, R.string.wallet_promotion_wallet_notinstalled_message);

    walletPromotionDownloadLayout.setVisibility(View.VISIBLE);
    walletPromotionButtonsLayout.setVisibility(View.GONE);
    walletPromotionIcon.setVisibility(View.VISIBLE);

    DownloadModel.DownloadState downloadState = viewModel.getDownloadModel()
        .getDownloadState();

    LinearLayout.LayoutParams pauseShowing =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 4f);
    LinearLayout.LayoutParams pauseHidden =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 2f);
    switch (downloadState) {
      case ACTIVE:
        downloadWalletProgressBar.setIndeterminate(false);
        downloadWalletProgressBar.setProgress(viewModel.getDownloadModel()
            .getProgress());
        downloadWalletProgressValue.setText(String.valueOf(viewModel.getDownloadModel()
            .getProgress()) + "%");
        pauseWalletDownload.setVisibility(View.VISIBLE);
        pauseWalletDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionEvent(viewModel, PromotionEvent.ClickType.PAUSE_DOWNLOAD)));
        cancelWalletDownload.setVisibility(View.GONE);
        resumeWalletDownload.setVisibility(View.GONE);
        walletDownloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case INDETERMINATE:
        downloadWalletProgressBar.setIndeterminate(true);
        pauseWalletDownload.setVisibility(View.VISIBLE);
        pauseWalletDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionEvent(viewModel, PromotionEvent.ClickType.PAUSE_DOWNLOAD)));
        cancelWalletDownload.setVisibility(View.GONE);
        resumeWalletDownload.setVisibility(View.GONE);
        walletDownloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case PAUSE:
        downloadWalletProgressBar.setIndeterminate(false);
        downloadWalletProgressBar.setProgress(viewModel.getDownloadModel()
            .getProgress());
        downloadWalletProgressValue.setText(String.valueOf(viewModel.getDownloadModel()
            .getProgress()) + "%");
        pauseWalletDownload.setVisibility(View.GONE);
        cancelWalletDownload.setVisibility(View.VISIBLE);
        cancelWalletDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionEvent(viewModel, PromotionEvent.ClickType.CANCEL_DOWNLOAD)));
        resumeWalletDownload.setVisibility(View.VISIBLE);
        resumeWalletDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionEvent(viewModel, PromotionEvent.ClickType.RESUME_DOWNLOAD)));
        walletDownloadControlsLayout.setLayoutParams(pauseHidden);
        break;
      case COMPLETE:
        downloadWalletProgressBar.setIndeterminate(true);
        pauseWalletDownload.setVisibility(View.VISIBLE);
        pauseWalletDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionEvent(viewModel, PromotionEvent.ClickType.PAUSE_DOWNLOAD)));
        cancelWalletDownload.setVisibility(View.GONE);
        resumeWalletDownload.setVisibility(View.GONE);
        walletDownloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case ERROR:
        showErrorDialog("", getContext().getString(R.string.error_occured));
        break;
      case NOT_ENOUGH_STORAGE_ERROR:
        showErrorDialog(getContext().getString(R.string.out_of_space_dialog_title),
            getContext().getString(R.string.out_of_space_dialog_message));
        break;
    }
  }

  private void setSimilarAppsAdapters() {
    similarListAdapter =
        new SimilarAppsBundleAdapter(new ArrayList<>(), oneDecimalFormat, similarAppClick);
    similarListRecyclerView.setAdapter(similarListAdapter);
  }

  private void manageSimilarAppsVisibility(boolean hasSimilarApps, boolean isDownloading) {
    if (!hasSimilarApps) {
      hideSimilarApps();
    } else {
      similarListRecyclerView.setVisibility(View.VISIBLE);
      LinearLayout similarParentView = ((LinearLayout) similarListRecyclerView.getParent());
      if (isDownloading) {
        similarParentView.removeView(similarListRecyclerView);
        LinearLayout parentLayout = (LinearLayout) similarDownloadPlaceholder.getParent();
        int downloadIndex = parentLayout.indexOfChild(similarDownloadPlaceholder);
        parentLayout.addView(similarListRecyclerView, downloadIndex);
        similarAppsVisibilitySubject.onNext(true);
      } else {
        similarParentView.removeView(similarListRecyclerView);
        LinearLayout parentLayout = (LinearLayout) similarBottomPlaceholder.getParent();
        int downloadIndex = parentLayout.indexOfChild(similarBottomPlaceholder);
        parentLayout.addView(similarListRecyclerView, downloadIndex);
      }
    }
  }

  private void showAppViewLayout() {
    appview.setVisibility(View.VISIBLE);
    viewProgress.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
    noNetworkErrorView.setVisibility(View.GONE);
  }

  private void setTrustedBadge(Malware malware) {
    @DrawableRes int badgeResId;
    @StringRes int badgeMessageId;

    Malware.Rank rank = malware.getRank() == null ? Malware.Rank.UNKNOWN : malware.getRank();
    switch (rank) {
      case TRUSTED:
        badgeResId = R.drawable.ic_badge_trusted;
        badgeMessageId = R.string.appview_header_trusted_text;
        break;

      case WARNING:
        badgeResId = R.drawable.ic_badge_warning;
        badgeMessageId = R.string.warning;
        break;

      case CRITICAL:
        badgeResId = R.drawable.ic_badge_critical;
        badgeMessageId = R.string.critical;
        break;

      default:
      case UNKNOWN:
        badgeResId = R.drawable.ic_badge_unknown;
        badgeMessageId = R.string.unknown;
        break;
    }
    Drawable icon = ContextCompat.getDrawable(getContext(), badgeResId);
    trustedBadge.setImageDrawable(icon);
    trustedText.setText(badgeMessageId);
  }

  private void setDescription(String description) {
    if (!TextUtils.isEmpty(description)) {
      descriptionText.setText(AptoideUtils.HtmlU.parse(description));
    } else {
      // only show "default" description if the app doesn't have one
      descriptionText.setText(R.string.description_not_available);
      descriptionReadMore.setVisibility(View.GONE);
    }
  }

  private void setReadMoreClickListener(String appName, AppMedia media, Store store) {
    descriptionReadMore.setOnClickListener(view -> readMoreClick.onNext(
        new ReadMoreClickEvent(appName, media.getDescription(), store.getAppearance()
            .getTheme())));
  }

  private void setAppFlags(boolean isGoodFile, AppFlags appFlags) {
    if (isGoodFile) {
      goodAppLayoutWrapper.setVisibility(View.VISIBLE);
      flagsLayoutWrapper.setVisibility(View.GONE);
    } else {
      goodAppLayoutWrapper.setVisibility(View.GONE);
      flagsLayoutWrapper.setVisibility(View.VISIBLE);
      setFlagValues(appFlags);
    }
  }

  private void setFlagValues(AppFlags appFlags) {
    try {
      if (appFlags != null && appFlags.getVotes() != null && !appFlags.getVotes()
          .isEmpty()) {
        for (final FlagsVote vote : appFlags.getVotes()) {
          applyCount(vote.getVoteType(), vote.getCount());
        }
      }
    } catch (NullPointerException ex) {
      CrashReport.getInstance()
          .log(ex);
    }
  }

  private void applyCount(FlagsVote.VoteType type, int count) {
    String countAsString = Integer.toString(count);
    switch (type) {
      case GOOD:
        workingWellText.setText(NumberFormat.getIntegerInstance()
            .format(Double.parseDouble(countAsString)));
        break;

      case VIRUS:
        virusText.setText(NumberFormat.getIntegerInstance()
            .format(Double.parseDouble(countAsString)));
        break;

      case FAKE:
        fakeAppText.setText(NumberFormat.getIntegerInstance()
            .format(Double.parseDouble(countAsString)));
        break;

      case LICENSE:
        needsLicenceText.setText(NumberFormat.getIntegerInstance()
            .format(Double.parseDouble(countAsString)));
        break;

      case FREEZE:
        break;

      default:
        throw new IllegalArgumentException("Unable to find Type " + type.name());
    }
  }

  private void setDeveloperDetails(AppDeveloper developer) {
    if (!TextUtils.isEmpty(developer.getWebsite())) {
      infoWebsite.setVisibility(View.VISIBLE);
    } else {
      infoWebsite.setVisibility(View.GONE);
    }

    if (!TextUtils.isEmpty(developer.getEmail())) {
      infoEmail.setVisibility(View.VISIBLE);
    } else {
      infoEmail.setVisibility(View.GONE);
    }

    if (!TextUtils.isEmpty(developer.getPrivacy())) {
      infoPrivacy.setVisibility(View.VISIBLE);
    } else {
      infoPrivacy.setVisibility(View.GONE);
    }
  }

  private void showReviews(boolean hasReviews, int gRating, float avgRating) {
    topReviewsProgress.setVisibility(View.GONE);

    reviewUsers.setText(AptoideUtils.StringU.withSuffix(gRating));
    if (avgRating == 0) {
      avgReviewScore.setText(R.string.appcardview_title_no_stars);
    } else {
      avgReviewScore.setText(String.format(Locale.getDefault(), "%.1f", avgRating));
    }
    avgReviewScoreBar.setRating(avgRating);

    if (hasReviews) {
      ratingLayout.setVisibility(View.VISIBLE);
      emptyReviewsLayout.setVisibility(View.GONE);
      topReviewsLayout.setVisibility(View.VISIBLE);
      rateAppButtonLarge.setVisibility(View.GONE);
      rateAppButton.setVisibility(View.VISIBLE);
    } else {
      ratingLayout.setVisibility(View.VISIBLE);
      emptyReviewsLayout.setVisibility(View.VISIBLE);
      topReviewsLayout.setVisibility(View.GONE);
      rateAppButtonLarge.setVisibility(View.VISIBLE);
      rateAppButton.setVisibility(View.GONE);

      if (gRating == 0) {
        emptyReviewTextView.setText(R.string.appview_rate_this_app);
      }
    }
  }

  private void showHideOptionsMenu(boolean visible) {
    for (int i = 0; i < menu.size(); i++) {
      MenuItem item = menu.getItem(i);
      showHideOptionsMenu(item, visible);
    }
  }

  protected void showHideOptionsMenu(@Nullable MenuItem item, boolean visible) {
    if (item != null) {
      item.setVisible(visible);
    }
  }

  public void setupToolbar() {

    toolbar.setTitle("");

    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);
    actionBar = activity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(toolbar.getTitle());
    }
  }

  private SpannableString formatAppCoinsRewardMessage() {
    String appcValue = String.valueOf(getArguments().getFloat(BundleKeys.APPC.name(), -1));
    String reward = "APPC";
    String tryAppMessage;
    SpannableString spannable;

    if (!appcValue.equals("-1.0")) {
      tryAppMessage =
          getResources().getString(R.string.appc_message_appview_appcoins_reward_with_value,
              appcValue, reward);
      spannable = new SpannableString(tryAppMessage);
      spannable.setSpan(new ForegroundColorSpan(getResources().getColor(StoreTheme.get(theme)
              .getPrimaryColor())), tryAppMessage.indexOf(appcValue),
          tryAppMessage.indexOf(appcValue) + appcValue.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    } else {
      tryAppMessage =
          getResources().getString(R.string.appc_message_appview_appcoins_reward, reward);
      spannable = new SpannableString(tryAppMessage);
    }

    spannable.setSpan(new ForegroundColorSpan(getResources().getColor(StoreTheme.get(theme)
            .getPrimaryColor())), tryAppMessage.indexOf(reward),
        tryAppMessage.indexOf(reward) + reward.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    return spannable;
  }

  @Override public Observable<DownloadModel.Action> installAppClick() {
    return installClickSubject;
  }

  @Override public Observable<Boolean> showRootInstallWarningPopup() {
    return GenericDialogs.createGenericYesNoCancelMessage(this.getContext(), null,
        getResources().getString(R.string.root_access_dialog))
        .map(response -> (response.equals(YES)));
  }

  @Override public void showDownloadAppModel(DownloadAppViewModel model, boolean hasDonations) {
    DownloadModel downloadModel = model.getDownloadModel();
    AppCoinsViewModel appCoinsViewModel = model.getAppCoinsViewModel();
    this.action = downloadModel.getAction();
    if (downloadModel.getAction() == DownloadModel.Action.PAY) {
      registerPaymentResult();
    }
    if (downloadModel.isDownloading()) {
      appcInfoView.hideInfo();
      downloadInfoLayout.setVisibility(View.VISIBLE);
      install.setVisibility(View.GONE);
      setDownloadState(downloadModel.getProgress(), downloadModel.getDownloadState());
    } else {
      if (!action.equals(DownloadModel.Action.MIGRATE)) {
        appcInfoView.showInfo(appCoinsViewModel.hasAdvertising(), appCoinsViewModel.hasBilling(),
            formatAppCoinsRewardMessage());
      } else {
        appcRewardView.setVisibility(View.GONE);
        appcMigrationWarningMessage.setVisibility(View.VISIBLE);
      }
      downloadInfoLayout.setVisibility(View.GONE);
      install.setVisibility(View.VISIBLE);
      setButtonText(downloadModel);
      if (downloadModel.hasError()) {
        handleDownloadError(downloadModel.getDownloadState());
      }
    }
  }

  @Override public void openApp(String packageName) {
    AptoideUtils.SystemU.openApp(packageName, getContext().getPackageManager(), getContext());
  }

  @Override public Observable<Boolean> showDowngradeMessage() {
    return GenericDialogs.createGenericContinueCancelMessage(getContext(), null,
        getContext().getResources()
            .getString(R.string.downgrade_warning_dialog))
        .map(eResponse -> eResponse.equals(YES));
  }

  @Override public void showDowngradingMessage() {
    Snackbar.make(getView(), R.string.downgrading_msg, Snackbar.LENGTH_SHORT)
        .show();
  }

  @Override public Observable<Void> pauseDownload() {
    return RxView.clicks(pauseDownload);
  }

  @Override public Observable<Void> resumeDownload() {
    return RxView.clicks(resumeDownload);
  }

  @Override public Observable<Void> cancelDownload() {
    return RxView.clicks(cancelDownload);
  }

  @Override public Observable<Void> isAppViewReadyToDownload() {
    return ready;
  }

  @Override public void readyToDownload() {
    ready.onNext(null);
  }

  @Override public void showRecommendsDialog() {
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    View dialogView = inflater.inflate(R.layout.logged_in_share, null);
    alertDialog.setView(dialogView);

    dialogView.findViewById(R.id.recommend_button)
        .setOnClickListener(__ -> {
          shareRecommendsDialogClick.onNext(null);
          alertDialog.dismiss();
        });

    dialogView.findViewById(R.id.skip_button)
        .setOnClickListener(__ -> {
          skipRecommendsDialogClick.onNext(null);
          alertDialog.dismiss();
        });

    dialogView.findViewById(R.id.dont_show_button)
        .setOnClickListener(__ -> {
          dontShowAgainRecommendsDialogClick.onNext(null);
          alertDialog.dismiss();
        });
    alertDialog.show();
  }

  @Override public Observable<Void> shareLoggedInRecommendsDialogClick() {
    return shareRecommendsDialogClick;
  }

  @Override public void showRecommendsThanksMessage() {
    Snackbar.make(getView(), R.string.social_timeline_share_dialog_title, Snackbar.LENGTH_SHORT)
        .show();
  }

  @Override public Observable<Void> skipLoggedInRecommendsDialogClick() {
    return skipRecommendsDialogClick;
  }

  @Override public Observable<Void> dontShowAgainLoggedInRecommendsDialogClick() {
    return dontShowAgainRecommendsDialogClick;
  }

  @Override public Observable<AppBoughClickEvent> appBought() {
    return appBought;
  }

  private void handleDownloadError(DownloadModel.DownloadState downloadState) {
    switch (downloadState) {
      case ERROR:
        showErrorDialog("", getContext().getString(R.string.error_occured));
        break;
      case NOT_ENOUGH_STORAGE_ERROR:
        showErrorDialog(getContext().getString(R.string.out_of_space_dialog_title),
            getContext().getString(R.string.out_of_space_dialog_message));
        break;
      default:
        throw new IllegalStateException("Invalid Download State " + downloadState);
    }
  }

  private void registerPaymentResult() {
    AppBoughtReceiver appBoughtReceiver = new AppBoughtReceiver() {
      @Override public void appBought(long appId, String path) {
        appBought.onNext(new AppBoughClickEvent(path, appId));
      }
    };
    getContext().registerReceiver(appBoughtReceiver,
        new IntentFilter(AppBoughtReceiver.APP_BOUGHT));
  }

  private void setDownloadState(int progress, DownloadModel.DownloadState downloadState) {

    LinearLayout.LayoutParams pauseShowing =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 4f);
    LinearLayout.LayoutParams pauseHidden =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 2f);
    switch (downloadState) {
      case ACTIVE:
        downloadProgressBar.setIndeterminate(false);
        downloadProgressBar.setProgress(progress);
        downloadProgressValue.setText(String.valueOf(progress) + "%");
        pauseDownload.setVisibility(View.VISIBLE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case INDETERMINATE:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.VISIBLE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case PAUSE:
        downloadProgressBar.setIndeterminate(false);
        downloadProgressBar.setProgress(progress);
        downloadProgressValue.setText(String.valueOf(progress) + "%");
        pauseDownload.setVisibility(View.GONE);
        cancelDownload.setVisibility(View.VISIBLE);
        resumeDownload.setVisibility(View.VISIBLE);
        downloadControlsLayout.setLayoutParams(pauseHidden);
        break;
      case COMPLETE:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.VISIBLE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case ERROR:
        showErrorDialog("", getContext().getString(R.string.error_occured));
        break;
      case NOT_ENOUGH_STORAGE_ERROR:
        showErrorDialog(getContext().getString(R.string.out_of_space_dialog_title),
            getContext().getString(R.string.out_of_space_dialog_message));
        break;
    }
  }

  private void showErrorDialog(String title, String message) {
    errorMessageSubscription = GenericDialogs.createGenericOkMessage(getContext(), title, message)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(eResponse -> {
        }, error -> new OnErrorNotImplementedException(error));
  }

  private void setButtonText(DownloadModel model) {
    DownloadModel.Action action = model.getAction();
    switch (action) {
      case UPDATE:
        install.setText(getResources().getString(R.string.appview_button_update));
        break;
      case INSTALL:
        install.setText(getResources().getString(R.string.appview_button_install));
        break;
      case OPEN:
        install.setText(getResources().getString(R.string.appview_button_open));
        break;
      case DOWNGRADE:
        install.setText(getResources().getString(R.string.appview_button_downgrade));
        break;
      case PAY:
        install.setText(
            String.format("%s (%s %s)", getContext().getString(R.string.appview_button_buy),
                model.getPay()
                    .getSymbol(), model.getPay()
                    .getPrice()));
        break;
      case MIGRATE:
        install.setText(getResources().getString(R.string.promo_update2appc_appview_update_button));
    }
  }

  public void buyApp(long appId) {
    startActivityForResult(
        BillingActivity.getIntent(getActivity(), appId, BuildConfig.APPLICATION_ID),
        PAY_APP_REQUEST_CODE);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == PAY_APP_REQUEST_CODE) {
      try {
        final Bundle data = (intent != null) ? intent.getExtras() : null;
        final PaidAppPurchase purchase =
            (PaidAppPurchase) purchaseBundleMapper.map(resultCode, data);

        FragmentActivity fragmentActivity = getActivity();
        Intent installApp = new Intent(AppBoughtReceiver.APP_BOUGHT);
        installApp.putExtra(AppBoughtReceiver.APP_ID, purchase.getProductId());
        installApp.putExtra(AppBoughtReceiver.APP_PATH, purchase.getApkPath());
        fragmentActivity.sendBroadcast(installApp);
      } catch (Throwable throwable) {
        if (throwable instanceof BillingException) {
          Snackbar.make(getView(), R.string.user_cancelled, Snackbar.LENGTH_SHORT);
        } else {
          Snackbar.make(getView(), R.string.unknown_error, Snackbar.LENGTH_SHORT);
        }
      }
    } else {
      super.onActivityResult(requestCode, resultCode, intent);
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_app_view, container, false);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (scrollView != null) {
      outState.putInt(KEY_SCROLL_Y, scrollView.getScrollY());
    }
  }

  private Observable<GenericDialogs.EResponse> createCustomDialogForApkfy(String appName,
      double appc, float rating, String icon, int downloads) {
    return Observable.create((Subscriber<? super GenericDialogs.EResponse> subscriber) -> {
      LayoutInflater inflater = LayoutInflater.from(getContext());
      View dialogLayout = inflater.inflate(R.layout.apkfy_new_dialog, null);
      final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogLayout)
          .create();
      ((TextView) dialogLayout.findViewById(R.id.app_name)).setText(appName);
      ((TextView) dialogLayout.findViewById(R.id.app_rating)).setText(
          oneDecimalFormat.format(rating));
      if (appc > 0) {
        ((TextView) dialogLayout.findViewById(R.id.appc_value)).setText(
            new DecimalFormat("0.00").format(appc));
      } else {
        dialogLayout.findViewById(R.id.appc_layout)
            .setVisibility(View.GONE);
      }

      ((TextView) dialogLayout.findViewById(R.id.app_downloads)).setText(
          String.format("%s %s", AptoideUtils.StringU.withSuffix(downloads),
              getResources().getString(R.string.downloads)));

      ImageLoader.with(getContext())
          .load(icon, dialogLayout.findViewById(R.id.app_icon));

      dialogLayout.findViewById(R.id.positive_button)
          .setOnClickListener(listener -> {
            subscriber.onNext(GenericDialogs.EResponse.YES);
            subscriber.onCompleted();
            apkfyDialogConfirmSubject.onNext(appName);
          });
      dialogLayout.findViewById(R.id.negative_button)

          .setOnClickListener(listener -> {
            subscriber.onNext(GenericDialogs.EResponse.CANCEL);
            subscriber.onCompleted();
          });
      subscriber.add(Subscriptions.create(dialog::dismiss));
      dialog.show();
    });
  }

  public enum BundleKeys {
    APP_ID, STORE_NAME, STORE_THEME, MINIMAL_AD, PACKAGE_NAME, SHOULD_INSTALL, MD5, UNAME, DOWNLOAD_CONVERSION_URL, APPC, EDITORS_CHOICE_POSITION, ORIGIN_TAG,
  }

  public enum OpenType {
    /**
     * Only open the appview
     */
    OPEN_ONLY,
    /**
     * opens the appView and starts the installation
     */
    OPEN_AND_INSTALL,
    /**
     * open the appView and ask user if want to install the app
     */
    OPEN_WITH_INSTALL_POPUP,
    /**
     * open the appView and ask user if want to install the app
     */
    APK_FY_INSTALL_POPUP
  }
}

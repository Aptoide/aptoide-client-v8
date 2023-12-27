package cm.aptoide.pt.app.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.aptoideviews.appcoins.BonusAppcView;
import cm.aptoide.aptoideviews.errors.ErrorView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.app.AppModel;
import cm.aptoide.pt.app.AppReview;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.app.ReviewsViewModel;
import cm.aptoide.pt.app.view.screenshots.ScreenShotClickEvent;
import cm.aptoide.pt.app.view.screenshots.ScreenshotsAdapter;
import cm.aptoide.pt.app.view.similar.SimilarAppClickEvent;
import cm.aptoide.pt.app.view.similar.SimilarAppsBundle;
import cm.aptoide.pt.app.view.similar.SimilarAppsBundleAdapter;
import cm.aptoide.pt.bonus.BonusAppcModel;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.RoomStoredMinimalAdPersistence;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.home.SnapToStartHelper;
import cm.aptoide.pt.install.view.remote.RemoteInstallDialog;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.permission.DialogPermissions;
import cm.aptoide.pt.promotions.Promotion;
import cm.aptoide.pt.promotions.WalletApp;
import cm.aptoide.pt.reviews.LanguageFilterHelper;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.themes.ThemeManager;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding.support.v4.widget.RxNestedScrollView;
import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewScrollChangeEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
  private static final String KEY_SCROLL_Y = "y";
  private static final String BADGE_DIALOG_TAG = "badgeDialog";
  private static final int APPC_TRANSITION_MS = 1000;
  @Inject AppViewPresenter presenter;
  @Inject DialogUtils dialogUtils;
  @Inject @Named("marketName") String marketName;
  @Inject @Named("rating-one-decimal-format") DecimalFormat oneDecimalFormat;
  @Inject ThemeManager themeManager;
  @Inject RoomStoredMinimalAdPersistence roomStoredMinimalAdPersistence;
  private Menu menu;
  private Toolbar toolbar;
  private ActionBar actionBar;
  private ScreenshotsAdapter screenshotsAdapter;
  private TopReviewsAdapter reviewsAdapter;
  private SimilarAppsBundleAdapter similarListAdapter;
  private PublishSubject<ScreenShotClickEvent> screenShotClick;
  private PublishSubject<ReadMoreClickEvent> readMoreClick;
  private PublishSubject<Void> loginSnackClick;
  private PublishSubject<SimilarAppClickEvent> similarAppClick;
  private PublishSubject<Integer> reviewsAutoScroll;
  private PublishSubject<String> apkfyDialogConfirmSubject;
  private PublishSubject<Boolean> similarAppsVisibilitySubject;
  private PublishSubject<DownloadModel.Action> installClickSubject;
  private PublishSubject<Void> cancelClickSubject;
  private PublishSubject<DownloadModel.Action> resumeClickSubject;
  private PublishSubject<Void> pauseClickSubject;
  private boolean isEskills;

  //Views
  private ErrorView errorView;
  private View reviewsLayout;
  private View downloadControlsLayout;
  private ImageView appIcon;
  private TextView appName;
  private View trustedLayout;
  private ImageView trustedBadge;
  private TextView trustedText;
  private TextView downloadsTop;
  private TextView sizeInfo;
  private ImageView iconSizeInfo;
  private TextView ratingInfo;
  private View appcMigrationWarningMessage;
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
  private Subscription errorMessageSubscription;
  private NestedScrollView scrollView;
  private int scrollViewY;
  private ViewStub appviewInstall;
  private ViewStub poaInstall;
  private View otherVersionsTopSeparator;
  private View appcInfoView;
  private ImageView poaCoinsIcon;
  private View poaIabInfo;
  private TextView poaOfferValue;
  private View poaBudgetElement;
  private TextView poaBudgetMessage;
  private View poaCountdownMessage;
  private TextView poaCountdownHours;
  private TextView poaCountdownMinutes;
  private TextView poaCountdownSeconds;
  private View iabInfo;
  private View apkfyElement;
  private View flagThisAppSection;
  private View collapsingAppcBackground;
  private TextView installStateText;
  private View catappultCard;

  private BonusAppcView bonusAppcView;

  //eSkills
  private View eSkillsInstallWalletView;
  private TextView eSkillsWalletBodyText;
  private ProgressBar eSkillsWalletProgressBar;
  private TextView eSkillsWalletProgressValue;
  private ImageView eSkillsPauseWalletDownload;
  private ImageView eSkillsCancelWalletDownload;
  private ImageView eSkillsResumeWalletDownload;
  private TextView eSkillsWalletInstallStateText;
  private View eSkillsWalletDownloadControlsLayout;
  private View eSkillsWalletDownloadInfo;
  private View poweredByLayout;

  //wallet promotions
  private View promotionView;
  private View walletPromotionDownloadLayout;
  private View walletPromotionClaimLayout;
  private ImageView walletPromotionIcon;
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
  private PublishSubject<EskillsPromotionEvent> promotionEskillsClick;
  private DecimalFormat poaFiatDecimalFormat;
  private CountDownTimer poaCountdownTimer;
  private boolean bumpedUp;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    screenShotClick = PublishSubject.create();
    readMoreClick = PublishSubject.create();
    loginSnackClick = PublishSubject.create();
    similarAppClick = PublishSubject.create();
    reviewsAutoScroll = PublishSubject.create();
    apkfyDialogConfirmSubject = PublishSubject.create();
    similarAppsVisibilitySubject = PublishSubject.create();
    installClickSubject = PublishSubject.create();
    resumeClickSubject = PublishSubject.create();
    cancelClickSubject = PublishSubject.create();
    pauseClickSubject = PublishSubject.create();
    promotionAppClick = PublishSubject.create();
    promotionEskillsClick = PublishSubject.create();
    poaFiatDecimalFormat = new DecimalFormat("0.00");
    isEskills = requireArguments().getBoolean(BundleKeys.ESKILLS.name(), false);
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    qManager = application.getQManager();
    httpClient = application.getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    adsRepository = application.getAdsRepository();
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ViewStub.OnInflateListener installInflateListener = (viewStub, view1) -> {
      install = view1.findViewById(R.id.appview_install_button);
      downloadInfoLayout = view1.findViewById(R.id.appview_transfer_info);
      downloadProgressBar = view1.findViewById(R.id.appview_download_progress_bar);
      downloadProgressValue = view1.findViewById(R.id.appview_download_progress_number);
      cancelDownload = view1.findViewById(R.id.appview_download_cancel_button);
      resumeDownload = view1.findViewById(R.id.appview_download_resume_download);
      pauseDownload = view1.findViewById(R.id.appview_download_pause_download);
      installStateText = view1.findViewById(R.id.appview_download_download_state);
      downloadControlsLayout = view1.findViewById(R.id.install_controls_layout);

      install.setOnClickListener(click -> installClickSubject.onNext(null));
      resumeDownload.setOnClickListener(click -> resumeClickSubject.onNext(null));
      cancelDownload.setOnClickListener(click -> cancelClickSubject.onNext(null));
      pauseDownload.setOnClickListener(click -> pauseClickSubject.onNext(null));
    };

    appviewInstall = view.findViewById(R.id.appview_install_element);
    appviewInstall.setLayoutResource(R.layout.install_app_view);
    appviewInstall.setOnInflateListener(installInflateListener);
    poaInstall = view.findViewById(R.id.poa_install_element);
    poaInstall.setLayoutResource(R.layout.install_app_view);
    poaInstall.setOnInflateListener(installInflateListener);

    scrollView = view.findViewById(R.id.scroll_view_app);
    errorView = view.findViewById(R.id.error_view);
    reviewsLayout = view.findViewById(R.id.reviews_layout);
    appIcon = view.findViewById(R.id.app_icon);
    trustedBadge = view.findViewById(R.id.trusted_badge);
    appName = view.findViewById(R.id.app_name);
    trustedLayout = view.findViewById(R.id.trusted_layout);
    trustedText = view.findViewById(R.id.trusted_text);
    downloadsTop = view.findViewById(R.id.header_downloads);
    sizeInfo = view.findViewById(R.id.header_size);
    iconSizeInfo = view.findViewById(R.id.header_size_icon);
    ratingInfo = view.findViewById(R.id.header_rating);
    appcMigrationWarningMessage = view.findViewById(R.id.migration_warning);
    otherVersionsTopSeparator = view.findViewById(R.id.other_versions_top_separator);
    appcInfoView = view.findViewById(R.id.poa_appc_layout);
    poaCoinsIcon = view.findViewById(R.id.coins_icon);
    poaIabInfo = view.findViewById(R.id.inapp_purchases);
    poaOfferValue = view.findViewById(R.id.offer_value);
    poaBudgetElement = view.findViewById(R.id.budget_element);
    poaBudgetMessage = view.findViewById(R.id.budget_left_message);
    poaCountdownMessage = view.findViewById(R.id.countdown_element);
    poaCountdownHours = view.findViewById(R.id.hours);
    poaCountdownMinutes = view.findViewById(R.id.minutes);
    poaCountdownSeconds = view.findViewById(R.id.seconds);
    iabInfo = view.findViewById(R.id.iap_appc_label);
    versionsLayout = view.findViewById(R.id.versions_layout);
    latestVersionTitle = view.findViewById(R.id.latest_version_title);
    latestVersion = versionsLayout.findViewById(R.id.latest_version);
    rewardAppLatestVersion = view.findViewById(R.id.appview_reward_app_versions_element);
    otherVersions = view.findViewById(R.id.other_versions);

    screenshots = view.findViewById(R.id.screenshots_list);
    screenshots.setLayoutManager(
        new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
    screenshots.setNestedScrollingEnabled(false);

    descriptionText = view.findViewById(R.id.description_text);
    descriptionReadMore = view.findViewById(R.id.description_see_more);
    topReviewsProgress = view.findViewById(R.id.top_comments_progress);
    ratingLayout = view.findViewById(R.id.rating_layout);
    emptyReviewsLayout = view.findViewById(R.id.empty_reviews_layout);
    topReviewsLayout = view.findViewById(R.id.comments_layout);
    rateAppButtonLarge = view.findViewById(R.id.rate_this_button2);
    emptyReviewTextView = view.findViewById(R.id.empty_review_text);
    reviewUsers = view.findViewById(R.id.users_voted);
    avgReviewScore = view.findViewById(R.id.rating_value);
    avgReviewScoreBar = view.findViewById(R.id.rating_bar);
    reviewsView = view.findViewById(R.id.top_comments_list);
    rateAppButton = view.findViewById(R.id.rate_this_button);
    showAllReviewsButton = view.findViewById(R.id.read_all_button);
    apkfyElement = view.findViewById(R.id.apkfy_element);

    flagThisAppSection = view.findViewById(R.id.flag_this_app_section);
    goodAppLayoutWrapper = view.findViewById(R.id.good_app_layout);
    flagsLayoutWrapper = view.findViewById(R.id.rating_flags_layout);
    workingWellLayout = view.findViewById(R.id.working_well_layout);
    needsLicenseLayout = view.findViewById(R.id.needs_licence_layout);
    fakeAppLayout = view.findViewById(R.id.fake_app_layout);
    virusLayout = view.findViewById(R.id.virus_layout);
    workingWellText = view.findViewById(R.id.working_well_count);
    needsLicenceText = view.findViewById(R.id.needs_licence_count);
    fakeAppText = view.findViewById(R.id.fake_app_count);
    virusText = view.findViewById(R.id.virus_count);
    storeLayout = view.findViewById(R.id.store_uploaded_layout);
    storeIcon = view.findViewById(R.id.store_icon);
    storeName = view.findViewById(R.id.store_name);
    storeFollowers = view.findViewById(R.id.user_count);
    storeDownloads = view.findViewById(R.id.download_count);
    storeFollow = view.findViewById(R.id.follow_button);
    similarListRecyclerView = view.findViewById(R.id.similar_list);
    similarDownloadPlaceholder = view.findViewById(R.id.similar_download_placeholder);
    similarBottomPlaceholder = view.findViewById(R.id.similar_bottom_placeholder);
    infoWebsite = view.findViewById(R.id.website_label);
    infoEmail = view.findViewById(R.id.email_label);
    infoPrivacy = view.findViewById(R.id.privacy_policy_label);
    infoPermissions = view.findViewById(R.id.permissions_label);
    catappultCard = view.findViewById(R.id.catappult_card);

    viewProgress = view.findViewById(R.id.appview_progress);
    appview = view.findViewById(R.id.appview_full);
    toolbar = view.findViewById(R.id.toolbar);
    collapsingAppcBackground = view.findViewById(R.id.collapsing_appc_coins_background);

    bonusAppcView = view.findViewById(R.id.bonus_appc_view);

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

    // eSkills
    eSkillsInstallWalletView = view.findViewById(R.id.eskills_install_wallet_card);
    eSkillsWalletBodyText = eSkillsInstallWalletView.findViewById(R.id.eskills_wallet_body);
    eSkillsWalletInstallStateText =
        eSkillsInstallWalletView.findViewById(R.id.eskills_wallet_download_state);
    eSkillsWalletProgressBar =
        eSkillsInstallWalletView.findViewById(R.id.eskills_wallet_download_progress_bar);
    eSkillsWalletProgressValue =
        eSkillsInstallWalletView.findViewById(R.id.eskills_wallet_download_progress_number);
    eSkillsPauseWalletDownload =
        eSkillsInstallWalletView.findViewById(R.id.eskills_wallet_download_pause_download);
    eSkillsCancelWalletDownload =
        eSkillsInstallWalletView.findViewById(R.id.eskills_wallet_download_cancel_button);
    eSkillsResumeWalletDownload =
        eSkillsInstallWalletView.findViewById(R.id.eskills_wallet_download_resume_download);
    eSkillsWalletDownloadControlsLayout =
        eSkillsInstallWalletView.findViewById(R.id.eskills_wallet_install_controls_layout);
    eSkillsWalletDownloadInfo =
        eSkillsInstallWalletView.findViewById(R.id.eskills_wallet_download_info);
    poweredByLayout = view.findViewById(R.id.powered_by_layout);

    screenshotsAdapter =
        new ScreenshotsAdapter(new ArrayList<>(), new ArrayList<>(), screenShotClick);
    screenshots.setAdapter(screenshotsAdapter);

    LinearLayoutManagerWithSmoothScroller layoutManager =
        new LinearLayoutManagerWithSmoothScroller(getContext(), RecyclerView.HORIZONTAL, false);

    LinearLayoutManager similarBundlesLayout =
        new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

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
          bonusAppcView.setAlpha(1 - (percentage * 1.20f));
          bonusAppcView.setTranslationX(-(percentage * 1.15f) * bonusAppcView.getMeasuredWidth());
          ((ToolbarArcBackground) view.findViewById(R.id.toolbar_background_arc)).setScale(
              percentage);
          collapsingAppcBackground.setAlpha(1 - percentage);
        });

    if (savedInstanceState != null) {
      scrollViewY = savedInstanceState.getInt(KEY_SCROLL_Y, 0);
    }

    collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);
    collapsingToolbarLayout.setExpandedTitleColor(
        getResources().getColor(android.R.color.transparent));

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
    reviewsAutoScroll = null;
    dialogUtils = null;
    presenter = null;
    similarAppsVisibilitySubject = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getActivity().getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));
    }
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    this.menu = menu;
    inflater.inflate(R.menu.fragment_appview, menu);
    showHideOptionsMenu(true);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    scrollViewY = scrollView.getScrollY();
    appviewInstall = null;
    poaInstall = null;
    appcInfoView = null;
    poaIabInfo = null;
    poaOfferValue = null;
    iabInfo = null;
    errorView = null;
    appIcon = null;
    trustedBadge = null;
    appName = null;
    trustedLayout = null;
    trustedText = null;
    downloadsTop = null;
    sizeInfo = null;
    iconSizeInfo = null;
    ratingInfo = null;
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
    catappultCard = null;
    menu = null;
    toolbar = null;
    actionBar = null;
    scrollView = null;
    collapsingToolbarLayout = null;

    if (poaCountdownTimer != null) {
      poaCountdownTimer.cancel();
      poaCountdownTimer = null;
    }

    eSkillsInstallWalletView = null;
    eSkillsWalletBodyText = null;
    eSkillsWalletProgressBar = null;
    eSkillsWalletProgressValue = null;
    eSkillsPauseWalletDownload = null;
    eSkillsResumeWalletDownload = null;
    eSkillsWalletInstallStateText = null;
    eSkillsWalletDownloadControlsLayout = null;
    eSkillsWalletDownloadInfo = null;
    poweredByLayout = null;
  }

  @Override public void showLoading() {
    viewProgress.setVisibility(View.VISIBLE);
    appview.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
  }

  @Override public void showAppView(AppModel model) {
    collapsingToolbarLayout.setTitle(model.getAppName());

    appName.setText(model.getAppName());
    ImageLoader.with(getContext())
        .load(model.getIcon(), appIcon);
    downloadsTop.setText(
        String.format("%s", AptoideUtils.StringU.withSuffix(model.getPackageDownloads())));
    if (!model.hasSplits()) {
      sizeInfo.setText(AptoideUtils.StringU.formatBytes(model.getSize(), false));
    } else {
      sizeInfo.setVisibility(View.GONE);
      iconSizeInfo.setVisibility(View.GONE);
    }
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
      if (model.isBeta()) {
        latestVersion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_beta, 0, 0, 0);
        latestVersion.setCompoundDrawablePadding(8);
      }
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

    if (model.hasBilling() && !model.isEskills()) {
      iabInfo.setVisibility(View.VISIBLE);
    }
    setTrustedBadge(model.getMalware());
    setDescription(model.getMedia()
        .getDescription());
    setAppFlags(model.isGoodApp(), model.getAppFlags());
    setReadMoreClickListener(model.getAppName(), model.getMedia(), model.getStore(),
        model.isAppCoinApp());
    setDeveloperDetails(model.getDeveloper());
    showAppViewLayout();
  }

  @Override public void handleError(DetailedAppRequestResult.Error error) {
    viewProgress.setVisibility(View.GONE);
    switch (error) {
      case NETWORK:
        errorView.setError(ErrorView.Error.NO_NETWORK);
        errorView.setVisibility(View.VISIBLE);
        break;
      case GENERIC:
        errorView.setError(ErrorView.Error.GENERIC);
        errorView.setVisibility(View.VISIBLE);
        break;
    }
  }

  @Override public Observable<ScreenShotClickEvent> getScreenshotClickEvent() {
    return screenShotClick;
  }

  @Override public Observable<ReadMoreClickEvent> clickedReadMore() {
    return readMoreClick;
  }

  @Override public void populateReviews(ReviewsViewModel reviewsModel, AppModel app) {
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
    return RxView.clicks(poaCoinsIcon);
  }

  @Override public Observable<Void> clickBonusAppcFlair() {
    return RxView.clicks(bonusAppcView);
  }

  @Override public Observable<Void> clickCatappultCard() {
    return RxView.clicks(catappultCard);
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

  @Override public Observable<Boolean> similarAppsVisibilityFromInstallClick() {
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

  @Override public Observable<Void> clickErrorRetry() {
    return errorView.retryClick();
  }

  @Override public Observable<String> apkfyDialogPositiveClick() {
    return apkfyDialogConfirmSubject;
  }

  @Override public Observable<Integer> scrollReviewsResponse() {
    return reviewsAutoScroll;
  }

  @Override public void navigateToDeveloperWebsite(AppModel app) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(app.getDeveloper()
        .getWebsite()));
    getContext().startActivity(browserIntent);
  }

  @Override public void navigateToDeveloperEmail(AppModel app) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    Uri data = Uri.parse("mailto:" + app.getDeveloper()
        .getEmail() + "?subject=" + "Feedback" + "&body=" + "");
    intent.setData(data);
    getContext().startActivity(intent);
  }

  @Override public void navigateToDeveloperPrivacy(AppModel app) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(app.getDeveloper()
        .getPrivacy()));
    getContext().startActivity(browserIntent);
  }

  @Override public void navigateToDeveloperPermissions(AppModel app) {
    DialogPermissions dialogPermissions =
        DialogPermissions.newInstance(app.getAppName(), app.getVersionName(), app.getIcon(),
            AptoideUtils.StringU.formatBytes(AppUtils.sumFileSizes(app.getFileSize(), app.getObb()),
                false), app.getUsedPermissions());
    dialogPermissions.show(getActivity().getSupportFragmentManager(), "");
  }

  @Override public void setFollowButton(boolean isFollowing) {
    if (!isFollowing) storeFollow.setText(R.string.followed);
  }

  @Override public void showTrustedDialog(AppModel app) {
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

  @Override public void showShareOnTvDialog(long appId) {
    if (AptoideUtils.SystemU.getConnectionType(
        (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE))
        .equals("mobile")) {
      GenericDialogs.createGenericOkMessage(getContext(),
          getContext().getString(R.string.remote_install_menu_title),
          getContext().getString(R.string.install_on_tv_mobile_error),
          themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId)
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
            new MinimalAdMapper(), roomStoredMinimalAdPersistence));
  }

  @Override public void recoverScrollViewState() {
    // TODO: 25/05/2018 remove this hack and find a better way to do it.

    scrollView.post(() -> {
      if (scrollView != null) scrollView.scrollTo(0, scrollViewY);
    });
  }

  @Override public Observable<Void> showOpenAndInstallDialog(String title, String appName) {
    return GenericDialogs.createGenericOkCancelMessage(getContext(), title,
        getContext().getString(R.string.installapp_alrt, appName),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId)
        .filter(response -> response.equals(YES))
        .map(__ -> null);
  }

  @Override
  public Observable<Void> showOpenAndInstallApkFyDialog(String title, String appName, double appc,
      float rating, String icon, int downloads) {
    return createCustomDialogForApkfy(appName, appc, rating, icon, downloads).filter(
        response -> response.equals(YES))
        .map(__ -> null);
  }

  @Override public void showApkfyElement(String appName) {
    apkfyElement.setVisibility(View.VISIBLE);
    String message = getString(R.string.appview_message_apkfy_1);
    ((TextView) apkfyElement.findViewById(R.id.apkfy_message_1)).setText(
        String.format(message, appName));
    ((TextView) apkfyElement.findViewById(R.id.apkfy_title)).setText(
        getResources().getString(R.string.appview_title_apkfy));
  }

  @Override public void setupAppcAppView(boolean hasBilling, BonusAppcModel bonusAppcModel) {
    TypedValue value = new TypedValue();
    this.getContext()
        .getTheme()
        .resolveAttribute(R.attr.appview_toolbar_bg_appc, value, true);
    int drawableId = value.resourceId;

    TransitionDrawable transition =
        (TransitionDrawable) ContextCompat.getDrawable(getContext(), drawableId);
    collapsingToolbarLayout.setBackgroundDrawable(transition);
    transition.startTransition(APPC_TRANSITION_MS);

    if (hasBilling && bonusAppcModel.getHasBonusAppc()) {
      bonusAppcView.setPercentage(bonusAppcModel.getBonusPercentage());
      bonusAppcView.setVisibility(View.VISIBLE);
    } else {
      AlphaAnimation animation1 = new AlphaAnimation(0f, 1.0f);
      animation1.setDuration(APPC_TRANSITION_MS);
      collapsingAppcBackground.setAlpha(1f);
      collapsingAppcBackground.setVisibility(View.VISIBLE);
      collapsingAppcBackground.startAnimation(animation1);
    }

    install.setBackgroundDrawable(getContext().getResources()
        .getDrawable(R.drawable.appc_gradient_rounded));
    downloadProgressBar.setProgressDrawable(
        ContextCompat.getDrawable(getContext(), R.drawable.appc_progress));
    flagThisAppSection.setVisibility(View.GONE);
  }

  @Override public void showAppcWalletPromotionView(Promotion promotion, WalletApp walletApp,
      Promotion.ClaimAction action, DownloadModel appDownloadModel) {
    walletPromotionCancelButton.setOnClickListener(__ -> promotionAppClick.onNext(
        new PromotionEvent(promotion, walletApp, PromotionEvent.ClickType.DISMISS)));
    if (walletApp.isInstalled()) {
      if (!appDownloadModel.getAction()
          .equals(DownloadModel.Action.OPEN)) {
        setupInstallDependencyApp(promotion, appDownloadModel);
      } else {
        setupClaimWalletPromotion(promotion, walletApp);
      }
    } else {
      if (walletApp.getDownloadModel()
          .isDownloading()) {
        setupActiveWalletPromotion(promotion, walletApp, appDownloadModel);
      } else {
        setupInactiveWalletPromotion(promotion, walletApp, appDownloadModel);
      }
    }
    promotionView.setVisibility(View.VISIBLE);
  }

  @Override public void setupEskillsAppView() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getActivity().getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(getResources().getColor(R.color.purple_bg_eskills));
    }
    downloadProgressBar.setProgressDrawable(
        ContextCompat.getDrawable(getContext(), R.drawable.eskills_progress_bar));
    trustedLayout.setVisibility(View.GONE);
    poweredByLayout.setVisibility(View.VISIBLE);
    collapsingToolbarLayout.findViewById(R.id.collapsing_eskills_background)
        .setVisibility(View.VISIBLE);
    install.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.eskills_light_purple_gradient));
    eSkillsInstallWalletView.setVisibility(View.VISIBLE);
    iabInfo.setVisibility(View.GONE);
    bonusAppcView.setVisibility(View.GONE);
  }

  @Override public void showEskillsWalletView(String appName, WalletApp walletApp) {
    if (walletApp.isInstalled()) {
      eSkillsWalletDownloadInfo.setVisibility(View.GONE);
      if (eSkillsWalletBodyText.getText().toString()
          .equals(getString(R.string.eskills_v2, appName))) {
        eSkillsWalletBodyText.setText(R.string.eskills_v2_wallet_installed_disclaimer_body);          // wallet was installed successfully
      }
      else {
        eSkillsWalletBodyText.setText(R.string.eskills_v2_wallet_already_installed_disclaimer_body);  // wallet was already installed
      }
    } else {
      eSkillsWalletBodyText.setText(
          getString(R.string.eskills_v2_wallet_install_disclaimer_body, appName));                    // wallet is not installed
      DownloadModel walletDownloadModel = walletApp.getDownloadModel();
      if (walletDownloadModel.isDownloadingOrInstalling()) {                                          // wallet is downloading or installing
        eSkillsWalletDownloadInfo.setVisibility(View.VISIBLE);
        setEskillsWalletDownloadState(walletApp, walletDownloadModel.getProgress(),
            walletDownloadModel.getDownloadState());
      } else {
        eSkillsWalletDownloadInfo.setVisibility(View.GONE);
      }
    }
  }

  @Override public Observable<Promotion> dismissWalletPromotionClick() {
    return promotionAppClick.filter(
        promotionAppClick -> promotionAppClick.getClickType() == PromotionEvent.ClickType.DISMISS)
        .map(promotionAppClick -> promotionAppClick.getPromotion());
  }

  @Override public void dismissWalletPromotionView() {
    promotionView.setVisibility(View.GONE);
  }

  @Override public Observable<Pair<Promotion, WalletApp>> installWalletButtonClick() {
    return promotionAppClick.filter(
        promotionAppClick -> promotionAppClick.getClickType() == PromotionEvent.ClickType.UPDATE
            || promotionAppClick.getClickType() == PromotionEvent.ClickType.INSTALL_APP
            || promotionAppClick.getClickType() == PromotionEvent.ClickType.DOWNLOAD
            || promotionAppClick.getClickType() == PromotionEvent.ClickType.DOWNGRADE)
        .map(promotionAppClick -> Pair.create(promotionAppClick.getPromotion(),
            promotionAppClick.getWallet()));
  }

  @Override public Observable<WalletApp> pausePromotionDownload() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        == PromotionEvent.ClickType.PAUSE_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getWallet());
  }

  @Override public Observable<WalletApp> pauseEskillsPromotionDownload() {
    return promotionEskillsClick.filter(promotionAppClick -> promotionAppClick.getClickType()
            == EskillsPromotionEvent.ClickType.PAUSE_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getWallet());
  }
  @Override public Observable<WalletApp> resumeEskillsPromotionDownload() {
    return promotionEskillsClick.filter(promotionAppClick -> promotionAppClick.getClickType()
            == EskillsPromotionEvent.ClickType.RESUME_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getWallet());
  }

  @Override public Observable<WalletApp> cancelEskillsPromotionDownload() {
    return promotionEskillsClick.filter(promotionAppClick -> promotionAppClick.getClickType()
            == EskillsPromotionEvent.ClickType.CANCEL_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getWallet());
  }

  @Override public Observable<WalletApp> cancelPromotionDownload() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        == PromotionEvent.ClickType.CANCEL_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getWallet());
  }

  @Override public Observable<WalletApp> resumePromotionDownload() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        == PromotionEvent.ClickType.RESUME_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getWallet());
  }

  @Override public Observable<Promotion> claimAppClick() {
    return promotionAppClick.filter(
        promotionAppClick -> promotionAppClick.getClickType() == PromotionEvent.ClickType.CLAIM)
        .map(promotionAppClick -> promotionAppClick.getPromotion());
  }

  @Override public Observable<Void> iabInfoClick() {
    return Observable.merge(RxView.clicks(poaIabInfo), RxView.clicks(iabInfo));
  }

  @Override public void showDownloadingSimilarApps(boolean hasSimilarApps) {
    manageSimilarAppsVisibility(hasSimilarApps, true);
  }

  @Override public void setInstallButton(AppCoinsViewModel appCoinsViewModel) {
    if (appCoinsViewModel.hasAdvertising()) {
      poaInstall.inflate();
      otherVersionsTopSeparator.setVisibility(View.INVISIBLE);
    } else {
      appviewInstall.inflate();
    }
  }

  @Override public void showDownloadError(DownloadModel downloadModel) {
    if (downloadModel.hasError()) {
      handleDownloadError(downloadModel.getDownloadState());
    }
  }

  @Override public Observable<Void> eSkillsCardClick() {
    return RxView.clicks(eSkillsInstallWalletView);
  }

  private void setupInstallDependencyApp(Promotion promotion, DownloadModel appDownloadModel) {
    int stringId = R.string.wallet_promotion_wallet_installed_message;
    if (appDownloadModel.getAction() == DownloadModel.Action.MIGRATE
        || appDownloadModel.getAction() == DownloadModel.Action.UPDATE) {
      stringId = R.string.wallet_promotion_wallet_installed_update_message;
    }
    setupWalletPromotionText(promotion, stringId);
    walletPromotionInstallDisableButton.setText(
        String.format(getString(R.string.wallet_promotion_button_install_disabled),
            promotion.getAppc()));
    walletPromotionInstallDisableLayout.setVisibility(View.VISIBLE);
    walletPromotionDownloadLayout.setVisibility(View.GONE);
    walletPromotionButtonsLayout.setVisibility(View.GONE);
    walletPromotionClaimLayout.setVisibility(View.GONE);
    walletPromotionIcon.setImageResource(R.drawable.ic_promotion_coins);
  }

  private void setupClaimWalletPromotion(Promotion promotion, WalletApp walletApp) {
    setupWalletPromotionText(promotion, R.string.wallet_promotion_wallet_claim_message);
    walletPromotionClaimButton.setText(
        String.format(getString(R.string.wallet_promotion_button_claim), promotion.getAppc()));
    walletPromotionDownloadLayout.setVisibility(View.GONE);
    walletPromotionInstallDisableLayout.setVisibility(View.GONE);
    walletPromotionButtonsLayout.setVisibility(View.GONE);
    walletPromotionClaimLayout.setVisibility(View.VISIBLE);
    walletPromotionClaimButton.setOnClickListener(__ -> promotionAppClick.onNext(
        new PromotionEvent(promotion, walletApp, PromotionEvent.ClickType.CLAIM)));
    walletPromotionIcon.setVisibility(View.VISIBLE);
  }

  private void setupWalletPromotionText(Promotion promotion, @StringRes int walletMessageStringId) {
    walletPromotionTitle.setText(
        String.format(getString(R.string.wallet_promotion_title), promotion.getAppc()));
    walletPromotionMessage.setText(
        String.format(getString(walletMessageStringId), promotion.getAppc()));
  }

  private int getPromotionMessage(DownloadModel appDownloadModel) {
    int messageStringId = R.string.wallet_promotion_wallet_notinstalled_message;
    if (appDownloadModel.getAction()
        .equals(DownloadModel.Action.MIGRATE) || appDownloadModel.getAction()
        .equals(DownloadModel.Action.UPDATE)) {
      messageStringId = R.string.wallet_promotion_wallet_installed_update_message;
    }
    return messageStringId;
  }

  private void setupInactiveWalletPromotion(Promotion promotion, WalletApp walletApp,
      DownloadModel appDownloadModel) {
    setupWalletPromotionText(promotion, getPromotionMessage(appDownloadModel));

    walletPromotionDownloadLayout.setVisibility(View.GONE);
    walletPromotionInstallDisableLayout.setVisibility(View.GONE);
    walletPromotionClaimLayout.setVisibility(View.GONE);
    walletPromotionButtonsLayout.setVisibility(View.VISIBLE);
    walletPromotionIcon.setVisibility(View.VISIBLE);
    walletPromotionDownloadButton.setOnClickListener(__ -> promotionAppClick.onNext(
        new PromotionEvent(promotion, walletApp, PromotionEvent.ClickType.INSTALL_APP)));
  }

  private void setupActiveWalletPromotion(Promotion promotion, WalletApp walletApp,
      DownloadModel appDownloadModel) {
    setupWalletPromotionText(promotion, getPromotionMessage(appDownloadModel));

    walletPromotionDownloadLayout.setVisibility(View.VISIBLE);
    walletPromotionButtonsLayout.setVisibility(View.GONE);
    walletPromotionIcon.setVisibility(View.VISIBLE);

    DownloadModel.DownloadState downloadState = walletApp.getDownloadModel()
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
        downloadWalletProgressBar.setProgress(walletApp.getDownloadModel()
            .getProgress());
        downloadWalletProgressValue.setText(walletApp.getDownloadModel()
            .getProgress() + "%");
        pauseWalletDownload.setVisibility(View.VISIBLE);
        pauseWalletDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionEvent(promotion, walletApp, PromotionEvent.ClickType.PAUSE_DOWNLOAD)));
        cancelWalletDownload.setVisibility(View.GONE);
        resumeWalletDownload.setVisibility(View.GONE);
        walletDownloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case INDETERMINATE:
        downloadWalletProgressBar.setIndeterminate(true);
        pauseWalletDownload.setVisibility(View.VISIBLE);
        pauseWalletDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionEvent(promotion, walletApp, PromotionEvent.ClickType.PAUSE_DOWNLOAD)));
        cancelWalletDownload.setVisibility(View.GONE);
        resumeWalletDownload.setVisibility(View.GONE);
        walletDownloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case PAUSE:
        downloadWalletProgressBar.setIndeterminate(false);
        downloadWalletProgressBar.setProgress(walletApp.getDownloadModel()
            .getProgress());
        downloadWalletProgressValue.setText(walletApp.getDownloadModel()
            .getProgress() + "%");
        pauseWalletDownload.setVisibility(View.GONE);
        cancelWalletDownload.setVisibility(View.VISIBLE);
        cancelWalletDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionEvent(promotion, walletApp, PromotionEvent.ClickType.CANCEL_DOWNLOAD)));
        resumeWalletDownload.setVisibility(View.VISIBLE);
        resumeWalletDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionEvent(promotion, walletApp, PromotionEvent.ClickType.RESUME_DOWNLOAD)));
        walletDownloadControlsLayout.setLayoutParams(pauseHidden);
        break;
      case COMPLETE:
        downloadWalletProgressBar.setIndeterminate(true);
        pauseWalletDownload.setVisibility(View.VISIBLE);
        pauseWalletDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionEvent(promotion, walletApp, PromotionEvent.ClickType.PAUSE_DOWNLOAD)));
        cancelWalletDownload.setVisibility(View.GONE);
        resumeWalletDownload.setVisibility(View.GONE);
        walletDownloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case ERROR:
        showErrorDialog("", getContext().getString(R.string.error_occured));
        break;
    }
  }

  private void setSimilarAppsAdapters() {
    similarListAdapter =
        new SimilarAppsBundleAdapter(new ArrayList<>(), oneDecimalFormat, similarAppClick);
    similarListRecyclerView.setAdapter(similarListAdapter);
  }

  private void manageSimilarAppsVisibility(boolean hasSimilarApps, boolean isDownloading) {
    if (!bumpedUp) {
      if (isDownloading) {
        bumpedUp = true;
      }
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
  }

  private void showAppViewLayout() {
    appview.setVisibility(View.VISIBLE);
    viewProgress.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
  }

  private void setTrustedBadge(Malware malware) {
    @DrawableRes int badgeResId;
    @StringRes int badgeMessageId;

    Malware.Rank rank = malware.getRank() == null ? Malware.Rank.UNKNOWN : malware.getRank();
    switch (rank) {
      case TRUSTED:
        badgeResId = R.drawable.ic_badges_trusted;
        badgeMessageId = R.string.appview_header_trusted_text;
        break;

      case WARNING:
        badgeResId = R.drawable.ic_badges_warning;
        badgeMessageId = R.string.warning;
        break;

      case CRITICAL:
        badgeResId = R.drawable.ic_badges_critical;
        badgeMessageId = R.string.critical;
        break;

      default:
      case UNKNOWN:
        badgeResId = R.drawable.ic_badges_unknown;
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

  private void setReadMoreClickListener(String appName, AppMedia media, Store store,
      boolean hasAppc) {
    descriptionReadMore.setOnClickListener(view -> readMoreClick.onNext(
        new ReadMoreClickEvent(appName, media.getDescription(), store.getAppearance()
            .getTheme(), hasAppc)));
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

  private SpannableString formatAppCoinsRewardMessage(String rewardValue) {
    String reward = "AppCoins Credits";
    String tryAppMessage;
    SpannableString spannable;

    if (!rewardValue.equals("-1.0")) {
      tryAppMessage =
          getResources().getString(R.string.appc_message_appview_appcoins_reward_with_value,
              rewardValue, reward);
      spannable = new SpannableString(tryAppMessage);
      spannable.setSpan(
          new ForegroundColorSpan(getResources().getColor(R.color.default_orange_gradient_end)),
          tryAppMessage.indexOf(rewardValue),
          tryAppMessage.indexOf(rewardValue) + rewardValue.length(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    } else {
      tryAppMessage =
          getResources().getString(R.string.appc_message_appview_appcoins_reward, reward);
      spannable = new SpannableString(tryAppMessage);
    }

    spannable.setSpan(
        new ForegroundColorSpan(getResources().getColor(R.color.default_orange_gradient_end)),
        tryAppMessage.indexOf(reward), tryAppMessage.indexOf(reward) + reward.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    return spannable;
  }

  @Override public Observable<DownloadModel.Action> installAppClick() {
    return installClickSubject.map(__ -> action);
  }

  @Override public Observable<Boolean> showRootInstallWarningPopup() {
    return GenericDialogs.createGenericYesNoCancelMessage(this.getContext(), null,
        getResources().getString(R.string.root_access_dialog),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId)
        .map(response -> (response.equals(YES)));
  }

  @Override
  public void showDownloadAppModel(DownloadModel downloadModel, AppCoinsViewModel appCoinsViewModel,
      boolean isAppBundle) {
    this.action = downloadModel.getAction();

    if (!action.equals(DownloadModel.Action.MIGRATE)) {
      showAppcInfo(appCoinsViewModel.getAdvertisingModel()
              .getHasAdvertising(), appCoinsViewModel.hasBilling(),
          appCoinsViewModel.getAdvertisingModel()
              .getAppcReward(), appCoinsViewModel.getAdvertisingModel()
              .getFiatReward(), appCoinsViewModel.getAdvertisingModel()
              .getFiatCurrency(), appCoinsViewModel.getAdvertisingModel()
              .getAppcBudget(), appCoinsViewModel.getAdvertisingModel()
              .getEndDate());
    }

    if (downloadModel.isDownloadingOrInstalling()) {
      downloadInfoLayout.setVisibility(View.VISIBLE);
      install.setVisibility(View.GONE);
      setDownloadState(downloadModel.getProgress(), downloadModel.getDownloadState(),
          downloadModel.getAppSize(), isAppBundle);
    } else {
      if (action.equals(DownloadModel.Action.MIGRATE)) {
        appcInfoView.setVisibility(View.GONE);
        appcMigrationWarningMessage.setVisibility(View.VISIBLE);
      }
      downloadInfoLayout.setVisibility(View.GONE);
      install.setVisibility(View.VISIBLE);
      setButtonText(downloadModel);
    }
  }

  @Override public void openApp(String packageName) {
    AptoideUtils.SystemU.openApp(packageName, getContext().getPackageManager(), getContext());
  }

  @Override public Observable<Boolean> showDowngradeMessage() {
    return GenericDialogs.createGenericContinueCancelMessage(getContext(), null,
        getContext().getResources()
            .getString(R.string.downgrade_warning_dialog),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId)
        .map(eResponse -> eResponse.equals(YES));
  }

  @Override public void showDowngradingMessage() {
    Snackbar.make(getView(), R.string.downgrading_msg, Snackbar.LENGTH_SHORT)
        .show();
  }

  @Override public Observable<Void> pauseDownload() {
    return pauseClickSubject;
  }

  @Override public Observable<DownloadModel.Action> resumeDownload() {
    return resumeClickSubject.map(__ -> action);
  }

  @Override public Observable<Void> cancelDownload() {
    return cancelClickSubject;
  }

  public void showGenericErrorDialog() {
    showErrorDialog("", getContext().getString(R.string.error_occured));
  }

  @Override public void showInvalidAppInfoErrorDialog() {
    showErrorDialog("", getContext().getString(R.string.appview_download_error_missing_splits));
  }

  private void showAppcInfo(boolean hasAdvertising, boolean hasBilling, double appcReward,
      double fiatReward, String fiatCurrency, double appcBudget, String date) {
    if (hasAdvertising) {
      String formatedFiatCurrency = fiatCurrency + poaFiatDecimalFormat.format(fiatReward);
      appcInfoView.setVisibility(View.VISIBLE);
      poaOfferValue.setText(
          String.format(getResources().getString(R.string.poa_app_view_card_body_2), appcReward,
              formatedFiatCurrency));
      if (!date.equals("")) {
        poaCountdownMessage.setVisibility(View.VISIBLE);
        setCountdownTimer(date);
      } else if (appcBudget != -1.0) {
        int transactionsLeft = (int) (appcBudget / appcReward);
        poaBudgetElement.setVisibility(View.VISIBLE);
        poaBudgetMessage.setText(
            String.format(getResources().getString(R.string.poa_APPCC_left_body),
                transactionsLeft));
      }
      if (hasBilling) poaIabInfo.setVisibility(View.VISIBLE);
    }
    /*else {
      if (hasBilling) iabInfo.setVisibility(View.VISIBLE);
    }*/
  }

  private void setCountdownTimer(String date) {
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DecimalFormat countdownNumberFormat = new DecimalFormat("00");
    dateFormatter.setLenient(false);
    final long now = System.currentTimeMillis();
    long dateMillis = 0;
    Date endDate;
    try {
      endDate = dateFormatter.parse(date);
      dateMillis = endDate.getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    long timeToDisplay = dateMillis - now;

    poaCountdownHours.setText(countdownNumberFormat.format(0));
    poaCountdownMinutes.setText(countdownNumberFormat.format(0));
    poaCountdownSeconds.setText(countdownNumberFormat.format(0));

    if (timeToDisplay >= 0) {
      poaCountdownTimer = new CountDownTimer(timeToDisplay, 1000) {
        @Override public void onTick(long millisUntilFinished) {
          String hoursLeft = countdownNumberFormat.format(millisUntilFinished / 3600000);
          poaCountdownHours.setText(hoursLeft);
          String minutesLeft =
              countdownNumberFormat.format((millisUntilFinished % 3600000) / 60000);
          poaCountdownMinutes.setText(minutesLeft);
          String secondsLeft =
              countdownNumberFormat.format(((millisUntilFinished % 360000) % 60000) / 1000);
          poaCountdownSeconds.setText(secondsLeft);
        }

        @Override public void onFinish() {
        }
      }.start();
    }
  }

  private void handleDownloadError(DownloadModel.DownloadState downloadState) {
    if (downloadState == DownloadModel.DownloadState.ERROR) {
      showGenericErrorDialog();
    } else {
      throw new IllegalStateException("Invalid Download State " + downloadState);
    }
  }

  private void setDownloadState(int progress, DownloadModel.DownloadState downloadState,
      long appSize, boolean isAppBundle) {

    String formatedAppSize = AptoideUtils.StringU.formatBytes(appSize, false);
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
        if (isAppBundle) {
          downloadProgressValue.setText(progress + "% of " + formatedAppSize);
        } else {
          downloadProgressValue.setText(progress + "%");
        }
        downloadProgressValue.setVisibility(View.VISIBLE);
        pauseDownload.setVisibility(View.VISIBLE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setVisibility(View.VISIBLE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        installStateText.setText(getString(R.string.appview_short_downloading));
        break;
      case INDETERMINATE:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.VISIBLE);
        downloadProgressValue.setVisibility(View.GONE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setVisibility(View.VISIBLE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        installStateText.setText(getString(R.string.appview_short_downloading));
        break;
      case PAUSE:
        downloadProgressBar.setIndeterminate(false);
        downloadProgressBar.setProgress(progress);
        if (isAppBundle) {
          downloadProgressValue.setText(progress + "% of " + formatedAppSize);
        } else {
          downloadProgressValue.setText(progress + "%");
        }
        downloadProgressValue.setVisibility(View.VISIBLE);
        pauseDownload.setVisibility(View.GONE);
        cancelDownload.setVisibility(View.VISIBLE);
        resumeDownload.setVisibility(View.VISIBLE);
        downloadControlsLayout.setVisibility(View.VISIBLE);
        downloadControlsLayout.setLayoutParams(pauseHidden);
        installStateText.setText(getString(R.string.appview_short_downloading));
        break;
      case COMPLETE:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.VISIBLE);
        downloadProgressValue.setVisibility(View.GONE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setVisibility(View.VISIBLE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        installStateText.setText(getString(R.string.appview_short_downloading));
        break;
      case INSTALLING:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.GONE);
        downloadProgressValue.setVisibility(View.GONE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setVisibility(View.GONE);
        installStateText.setText(getString(R.string.appview_short_installing));
        break;
    }
  }

  private void setEskillsWalletDownloadState(WalletApp walletApp,int progress, DownloadModel.DownloadState downloadState) {
    LinearLayout.LayoutParams pauseShowing =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 4f);
    LinearLayout.LayoutParams pauseHidden =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 4f);
    switch (downloadState) {
      case ACTIVE:
        eSkillsWalletProgressBar.setIndeterminate(false);
        eSkillsWalletProgressBar.setProgress(progress);
        eSkillsWalletProgressValue.setText(progress + "%");
        eSkillsWalletProgressValue.setVisibility(View.VISIBLE);
        eSkillsPauseWalletDownload.setOnClickListener(__ -> promotionEskillsClick.onNext(
            new EskillsPromotionEvent( walletApp, EskillsPromotionEvent.ClickType.PAUSE_DOWNLOAD)));
        eSkillsPauseWalletDownload.setVisibility(View.VISIBLE);
        eSkillsResumeWalletDownload.setVisibility(View.GONE);
        eSkillsWalletDownloadControlsLayout.setVisibility(View.VISIBLE);
        eSkillsWalletDownloadControlsLayout.setLayoutParams(pauseShowing);
        eSkillsWalletInstallStateText.setText(getString(R.string.appview_short_downloading));
        break;
      case INDETERMINATE:
        eSkillsWalletProgressBar.setIndeterminate(true);
        eSkillsPauseWalletDownload.setVisibility(View.VISIBLE);
        eSkillsWalletProgressValue.setVisibility(View.GONE);
        eSkillsResumeWalletDownload.setVisibility(View.GONE);
        eSkillsWalletDownloadControlsLayout.setVisibility(View.VISIBLE);
        eSkillsWalletDownloadControlsLayout.setLayoutParams(pauseShowing);
        eSkillsWalletInstallStateText.setText(getString(R.string.appview_short_downloading));
        break;
      case PAUSE:
        eSkillsWalletProgressBar.setIndeterminate(false);
        eSkillsWalletProgressBar.setProgress(progress);
        eSkillsWalletProgressValue.setText(progress + "%");
        eSkillsWalletDownloadControlsLayout.setVisibility(View.VISIBLE);
        eSkillsWalletProgressValue.setVisibility(View.VISIBLE);
        eSkillsPauseWalletDownload.setVisibility(View.GONE);
        eSkillsResumeWalletDownload.setVisibility(View.VISIBLE);
        eSkillsResumeWalletDownload.setOnClickListener(__ -> promotionEskillsClick.onNext(
            new EskillsPromotionEvent( walletApp, EskillsPromotionEvent.ClickType.RESUME_DOWNLOAD)));
        eSkillsCancelWalletDownload.setVisibility(View.VISIBLE);
        eSkillsCancelWalletDownload.setOnClickListener(__ -> promotionEskillsClick.onNext(
            new EskillsPromotionEvent( walletApp, EskillsPromotionEvent.ClickType.CANCEL_DOWNLOAD)));
        eSkillsWalletDownloadControlsLayout.setLayoutParams(pauseHidden);
        eSkillsWalletInstallStateText.setText(getString(R.string.appview_short_downloading));
        break;
      case COMPLETE:
        eSkillsWalletProgressBar.setIndeterminate(true);
        eSkillsPauseWalletDownload.setVisibility(View.VISIBLE);
        eSkillsWalletProgressValue.setVisibility(View.GONE);
        eSkillsResumeWalletDownload.setVisibility(View.GONE);
        eSkillsWalletDownloadControlsLayout.setVisibility(View.VISIBLE);
        eSkillsWalletDownloadControlsLayout.setLayoutParams(pauseShowing);
        eSkillsWalletInstallStateText.setText(getString(R.string.appview_short_downloading));
        break;
      case INSTALLING:
        eSkillsWalletProgressBar.setIndeterminate(true);
        eSkillsPauseWalletDownload.setVisibility(View.GONE);
        eSkillsWalletProgressValue.setVisibility(View.GONE);
        eSkillsResumeWalletDownload.setVisibility(View.GONE);
        eSkillsWalletDownloadControlsLayout.setVisibility(View.GONE);
        eSkillsWalletInstallStateText.setText(getString(R.string.appview_short_installing));
        break;
    }
  }
  private void showErrorDialog(String title, String message) {
    errorMessageSubscription = GenericDialogs.createGenericOkMessage(getContext(), title, message,
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId)
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
      case MIGRATE:
        install.setText(getResources().getString(R.string.promo_update2appc_appview_update_button));
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    View view = inflater.inflate(R.layout.fragment_app_view, container, false);
    if (isEskills) {
      view.getContext().setTheme(R.style.AppBaseThemeDark);
    }
    return view;
  }

  @Override
  public LayoutInflater onGetLayoutInflater(Bundle savedInstanceState) {
    LayoutInflater inflater = super.onGetLayoutInflater(savedInstanceState);
    if (isEskills) {
      inflater = inflater.cloneInContext(new ContextThemeWrapper(getContext(), R.style.AppBaseThemeDark));
    }
    return inflater;
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
            apkfyDialogConfirmSubject.onNext(appName);
            subscriber.onCompleted();
            dialog.dismiss();
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
    APP_ID, STORE_NAME, STORE_THEME, MINIMAL_AD, PACKAGE_NAME, SHOULD_INSTALL, MD5, UNAME, DOWNLOAD_CONVERSION_URL, APPC, EDITORS_CHOICE_POSITION, ORIGIN_TAG, OEM_ID, ESKILLS
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

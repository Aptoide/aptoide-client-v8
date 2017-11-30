/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.app.view;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.DownloadCompleteAnalytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.app.AppBoughtReceiver;
import cm.aptoide.pt.app.AppRepository;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.AppViewSimilarAppAnalytics;
import cm.aptoide.pt.app.view.displayable.AppViewDescriptionDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewDeveloperDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewFlagThisDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewInstallDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewRateAndCommentsDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewScreenshotsDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewStoreDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewSuggestedAppsDisplayable;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.exception.BillingException;
import cm.aptoide.pt.billing.purchase.PaidAppPurchase;
import cm.aptoide.pt.billing.view.BillingActivity;
import cm.aptoide.pt.billing.view.PurchaseBundleMapper;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.crashreports.IssuesAnalytics;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.RollbackAccessor;
import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.accessors.StoredMinimalAdAccessor;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.Group;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.AppAction;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.install.InstallerFactory;
import cm.aptoide.pt.install.view.remote.RemoteInstallDialog;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.search.ReferrerUtils;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.view.SearchBuilder;
import cm.aptoide.pt.share.ShareAppHelper;
import cm.aptoide.pt.spotandshare.SpotAndShareAnalytics;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.timeline.SocialRepository;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.SimpleSubscriber;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.view.ThemeUtils;
import cm.aptoide.pt.view.dialog.DialogBadgeV7;
import cm.aptoide.pt.view.fragment.AptoideBaseFragment;
import cm.aptoide.pt.view.recycler.BaseAdapter;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.share.NotLoggedInShareAnalytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.appevents.AppEventsLogger;
import com.jakewharton.rxrelay.PublishRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import org.parceler.Parcels;
import retrofit2.Converter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created on 04/05/16.
 */
public class AppViewFragment extends AptoideBaseFragment<BaseAdapter>
    implements Scrollable, AppMenuOptions {
  public static final int VIEW_ID = R.layout.fragment_app_view;
  public static final int LOGIN_REQUEST_CODE = 13;
  private static final String TAG = AppViewFragment.class.getSimpleName();
  private static final int PAY_APP_REQUEST_CODE = 12;
  private static final String ORIGIN_TAG = "TAG";

  private final String key_appId = "appId";
  private final String key_packageName = "packageName";
  private final String key_uname = "uname";

  private String packageName;
  private String appName;
  private String wUrl;
  private AppViewHeader header;
  private long appId;
  private OpenType openType;
  private String storeTheme;
  private SearchAdResult searchAdResult;
  private InstallManager installManager;
  private Action0 unInstallAction;
  private MenuItem uninstallMenuItem;
  private AppRepository appRepository;
  private Subscription subscription;
  private AdsRepository adsRepository;
  private boolean sponsored;
  private String storeName;
  private AppViewInstallDisplayable installDisplayable;
  private String md5;
  private String uname;
  private Menu menu;
  private GetAppMeta.App app;
  private AppAction appAction = AppAction.OPEN;
  private InstalledRepository installedRepository;
  private StoreCredentialsProvider storeCredentialsProvider;
  private SocialRepository socialRepository;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private StoredMinimalAdAccessor storedMinimalAdAccessor;
  private BillingAnalytics billingAnalytics;
  private PurchaseBundleMapper purchaseBundleMapper;
  private ShareAppHelper shareAppHelper;
  private QManager qManager;
  private DownloadFactory downloadFactory;
  private TimelineAnalytics timelineAnalytics;
  private AppViewAnalytics appViewAnalytics;
  private StoreAnalytics storeAnalytics;
  private AppViewSimilarAppAnalytics appViewSimilarAppAnalytics;
  private MinimalAdMapper adMapper;
  private PublishRelay installAppRelay;
  private boolean suggestedShowing;
  private List<String> keywords;
  private BillingIdResolver billingIdResolver;
  private String marketName;
  private String defaultTheme;
  private long storeId;
  private AccountNavigator accountNavigator;
  private NotLoggedInShareAnalytics notLoggedInShareAnalytics;
  private CrashReport crashReport;
  private NavigationTracker navigationTracker;
  private SearchBuilder searchBuilder;
  private IssuesAnalytics issuesAnalytics;

  public static AppViewFragment newInstanceUname(String uname) {
    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.UNAME.name(), uname);

    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  public static AppViewFragment newInstance(String md5) {
    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.MD5.name(), md5);

    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  public static AppViewFragment newInstance(long appId, String packageName, OpenType openType,
      String tag) {
    Bundle bundle = new Bundle();
    bundle.putString(ORIGIN_TAG, tag);
    bundle.putLong(BundleKeys.APP_ID.name(), appId);
    bundle.putString(BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putSerializable(BundleKeys.SHOULD_INSTALL.name(), openType);

    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  public static AppViewFragment newInstance(long appId, String packageName, String storeTheme,
      String storeName) {
    Bundle bundle = new Bundle();
    bundle.putLong(BundleKeys.APP_ID.name(), appId);
    bundle.putString(BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putString(BundleKeys.STORE_NAME.name(), storeName);
    bundle.putString(StoreFragment.BundleCons.STORE_THEME, storeTheme);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  public static AppViewFragment newInstance(long appId, String packageName, String storeTheme,
      String storeName, String tag) {
    Bundle bundle = new Bundle();
    bundle.putString(ORIGIN_TAG, tag);
    bundle.putLong(BundleKeys.APP_ID.name(), appId);
    bundle.putString(BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putString(BundleKeys.STORE_NAME.name(), storeName);
    bundle.putString(StoreFragment.BundleCons.STORE_THEME, storeTheme);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  public static AppViewFragment newInstance(SearchAdResult searchAdResult) {
    Bundle bundle = new Bundle();
    bundle.putLong(BundleKeys.APP_ID.name(), searchAdResult.getAppId());
    bundle.putString(BundleKeys.PACKAGE_NAME.name(), searchAdResult.getPackageName());
    bundle.putParcelable(BundleKeys.MINIMAL_AD.name(), Parcels.wrap(searchAdResult));
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);

    return fragment;
  }

  public static AppViewFragment newInstance(SearchAdResult searchAdResult, String tag) {
    Bundle bundle = new Bundle();
    bundle.putLong(BundleKeys.APP_ID.name(), searchAdResult.getAppId());
    bundle.putString(BundleKeys.PACKAGE_NAME.name(), searchAdResult.getPackageName());
    bundle.putParcelable(BundleKeys.MINIMAL_AD.name(), Parcels.wrap(searchAdResult));
    bundle.putString(ORIGIN_TAG, tag);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);

    return fragment;
  }

  public static AppViewFragment newInstance(SearchAdResult searchAdResult, String storeTheme,
      String tag) {
    Bundle bundle = new Bundle();
    bundle.putLong(BundleKeys.APP_ID.name(), searchAdResult.getAppId());
    bundle.putString(BundleKeys.PACKAGE_NAME.name(), searchAdResult.getPackageName());
    bundle.putParcelable(BundleKeys.MINIMAL_AD.name(), Parcels.wrap(searchAdResult));
    bundle.putString(StoreFragment.BundleCons.STORE_THEME, storeTheme);
    bundle.putString(ORIGIN_TAG, tag);

    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);

    return fragment;
  }

  public static Fragment newInstance(String packageName, OpenType openType) {
    return newInstance(packageName, null, openType);
  }

  public static AppViewFragment newInstance(String packageName, String storeName,
      OpenType openType) {
    Bundle bundle = new Bundle();
    if (!TextUtils.isEmpty(packageName)) {
      bundle.putString(BundleKeys.PACKAGE_NAME.name(), packageName);
    }
    bundle.putSerializable(BundleKeys.SHOULD_INSTALL.name(), openType);
    bundle.putString(BundleKeys.STORE_NAME.name(), storeName);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  public boolean isSuggestedShowing() {
    return suggestedShowing;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getAppName() {
    return appName;
  }

  public String getwUrl() {
    return wUrl;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    handleSavedInstance(savedInstanceState);

    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    this.appViewModel.setDefaultTheme(application.getDefaultThemeName());
    this.appViewModel.setMarketName(application.getMarketName());

    final SearchManager searchManager =
        (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);

    final SearchNavigator searchNavigator =
        new SearchNavigator(getFragmentNavigator(), application.getDefaultStoreName());

    searchBuilder = new SearchBuilder(searchManager, searchNavigator);

    adMapper = new MinimalAdMapper();
    qManager = ((AptoideApplication) getContext().getApplicationContext()).getQManager();
    purchaseBundleMapper =
        ((AptoideApplication) getContext().getApplicationContext()).getPurchaseBundleMapper();
    final AptoideAccountManager accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    accountNavigator = ((ActivityResultNavigator) getContext()).getAccountNavigator();

    installManager = ((AptoideApplication) getContext().getApplicationContext()).getInstallManager(
        InstallerFactory.ROLLBACK);
    final BodyInterceptor<BaseBody> bodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    billingAnalytics =
        ((AptoideApplication) getContext().getApplicationContext()).getBillingAnalytics();
    final TokenInvalidator tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    Analytics analytics = Analytics.getInstance();
    issuesAnalytics = new IssuesAnalytics(analytics, Answers.getInstance());
    timelineAnalytics = new TimelineAnalytics(analytics,
        AppEventsLogger.newLogger(getContext().getApplicationContext()), bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, BuildConfig.APPLICATION_ID,
        application.getDefaultSharedPreferences(), new NotificationAnalytics(httpClient, analytics),
        navigationTracker);
    socialRepository =
        new SocialRepository(accountManager, bodyInterceptor, converterFactory, httpClient,
            timelineAnalytics, tokenInvalidator,
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
    appRepository = RepositoryFactory.getAppRepository(getContext(),
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
    adsRepository = ((AptoideApplication) getContext().getApplicationContext()).getAdsRepository();
    installedRepository =
        RepositoryFactory.getInstalledRepository(getContext().getApplicationContext());
    storeCredentialsProvider = new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class));
    storedMinimalAdAccessor = AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), StoredMinimalAd.class);
    final SpotAndShareAnalytics spotAndShareAnalytics = new SpotAndShareAnalytics(analytics);
    appViewAnalytics = new AppViewAnalytics(analytics,
        AppEventsLogger.newLogger(getContext().getApplicationContext()));
    appViewSimilarAppAnalytics = new AppViewSimilarAppAnalytics(analytics,
        AppEventsLogger.newLogger(getContext().getApplicationContext()));

    installAppRelay = PublishRelay.create();
    shareAppHelper =
        new ShareAppHelper(installedRepository, accountManager, accountNavigator, getActivity(),
            spotAndShareAnalytics, timelineAnalytics, installAppRelay,
            application.getDefaultSharedPreferences(),
            application.isCreateStoreUserPrivacyEnabled());
    downloadFactory = new DownloadFactory(getMarketName());
    appViewAnalytics = new AppViewAnalytics(analytics,
        AppEventsLogger.newLogger(getContext().getApplicationContext()));
    storeAnalytics =
        new StoreAnalytics(AppEventsLogger.newLogger(getContext().getApplicationContext()),
            analytics);
    notLoggedInShareAnalytics = application.getNotLoggedInShareAnalytics();
    navigationTracker = application.getNavigationTracker();
    setHasOptionsMenu(true);
  }

  private void handleSavedInstance(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      suggestedShowing = savedInstanceState.getBoolean(Keys.SUGGESTED_SHOWING);
    }
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    appId = args.getLong(BundleKeys.APP_ID.name(), -1);
    packageName = args.getString(BundleKeys.PACKAGE_NAME.name(), null);
    md5 = args.getString(BundleKeys.MD5.name(), null);
    uname = args.getString(BundleKeys.UNAME.name(), null);
    openType = (OpenType) args.getSerializable(BundleKeys.SHOULD_INSTALL.name());
    if (openType == null) {
      openType = OpenType.OPEN_ONLY;
    } else {
      args.remove(BundleKeys.SHOULD_INSTALL.name());
    }
    searchAdResult = Parcels.unwrap(args.getParcelable(BundleKeys.MINIMAL_AD.name()));
    storeName = args.getString(BundleKeys.STORE_NAME.name());
    sponsored = searchAdResult != null;
    storeTheme = args.getString(StoreFragment.BundleCons.STORE_THEME);
    originTag = args.getString(ORIGIN_TAG, null);
  }

  @Override public int getContentViewId() {
    return VIEW_ID;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    header = new AppViewHeader(view);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    header = null;
    suggestedShowing = false;
    if (storeTheme != null) {
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreTheme.get(defaultTheme));
      ThemeUtils.setAptoideTheme(getActivity(), defaultTheme);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getLifecycle().filter(lifecycleEvent -> lifecycleEvent.equals(LifecycleEvent.CREATE))
        .flatMap(viewCreated -> accountNavigator.notLoggedInViewResults(LOGIN_REQUEST_CODE)
            .filter(success -> success)
            .flatMapCompletable(result -> socialRepository.asyncShare(packageName, storeId, "app")
                .doOnCompleted(() -> notLoggedInShareAnalytics.sendShareSuccess()))
            .doOnError(throwable -> {
              notLoggedInShareAnalytics.sendShareFail();
              crashReport.log(throwable);
            })
            .retry())
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(result -> {
        }, throwable -> {
          crashReport = CrashReport.getInstance();
          crashReport.log(throwable);
        });
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);

    if (subscription != null) {
      subscription.unsubscribe();
    }

    if (appId >= 0) {
      Logger.d(TAG, "loading app info using app ID");
      subscription = appRepository.getApp(appId, refresh, sponsored, storeName, packageName)
          .map(getApp -> getApp)
          .flatMap(getApp -> manageOrganicAds(getApp))
          .flatMap(getApp -> setKeywords(getApp).onErrorReturn(throwable -> getApp))
          .observeOn(AndroidSchedulers.mainThread())
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
          .subscribe(getApp -> {
            setupAppView(getApp);
          }, throwable -> finishLoading(throwable));
    } else if (!TextUtils.isEmpty(md5)) {
      subscription = appRepository.getAppFromMd5(md5, refresh, sponsored)
          .map(getApp -> getApp)
          .flatMap(getApp -> manageOrganicAds(getApp))
          .flatMap(getApp -> setKeywords(getApp).onErrorReturn(throwable -> getApp))
          .observeOn(AndroidSchedulers.mainThread())
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
          .subscribe(getApp -> {
            setupAppView(getApp);
          }, throwable -> {
            finishLoading(throwable);
          });
    } else if (!TextUtils.isEmpty(uname)) {
      subscription = appRepository.getAppFromUname(uname, refresh, sponsored)
          .map(getApp -> getApp)
          .flatMap(getApp -> manageOrganicAds(getApp))
          .flatMap(getApp -> setKeywords(getApp).onErrorReturn(throwable -> getApp))
          .observeOn(AndroidSchedulers.mainThread())
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
          .subscribe(getApp -> {
            setupAppView(getApp);
          }, throwable -> {
            finishLoading(throwable);
            CrashReport.getInstance()
                .log(key_appId, String.valueOf(appId));
            CrashReport.getInstance()
                .log(key_packageName, String.valueOf(packageName));
            CrashReport.getInstance()
                .log(key_uname, uname);
          });
    } else {
      Logger.d(TAG, "loading app info using app package name");
      subscription = appRepository.getApp(packageName, refresh, sponsored, storeName)
          .map(getApp -> getApp)
          .flatMap(getApp -> manageOrganicAds(getApp))
          .observeOn(AndroidSchedulers.mainThread())
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
          .subscribe(getApp -> {
            setupAppView(getApp);
          }, throwable -> {
            finishLoading(throwable);
          });
    }
  }

  @Override public void onResume() {
    super.onResume();

    // restore download bar status
    // TODO: 04/08/16 restore download bar status
  }

  @Override public void onPause() {
    super.onPause();

    // save download bar status
    // TODO: 04/08/16 save download bar status
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putBoolean(Keys.SUGGESTED_SHOWING, suggestedShowing);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), originTag, null);
  }

  private boolean hasDescription(GetAppMeta.Media media) {
    return !TextUtils.isEmpty(media.getDescription());
  }

  public void buyApp(GetAppMeta.App app) {
    billingAnalytics.sendPaymentViewShowEvent();
    startActivityForResult(
        BillingActivity.getIntent(getActivity(), app.getId(), BuildConfig.APPLICATION_ID),
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
        installApp.putExtra(AppBoughtReceiver.APP_ID, appId);
        installApp.putExtra(AppBoughtReceiver.APP_PATH, purchase.getApkPath());
        fragmentActivity.sendBroadcast(installApp);
      } catch (Throwable throwable) {
        if (throwable instanceof BillingException) {
          ShowMessage.asSnack(header.badge, R.string.user_cancelled);
        } else {
          ShowMessage.asSnack(header.badge, R.string.unknown_error);
        }
      }
    } else {
      super.onActivityResult(requestCode, resultCode, intent);
    }
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    this.menu = menu;
    inflater.inflate(R.menu.menu_appview_fragment, menu);
    if (searchBuilder != null && searchBuilder.isValid()) {
      final FragmentActivity activity = getActivity();
      // from getActivity() "May return null if the fragment is associated with a Context instead."
      final Context context = getContext();
      if (activity != null) {
        searchBuilder.attachSearch(activity, menu.findItem(R.id.action_search));
        issuesAnalytics.attachSearchSuccess(false);
        return;
      } else if (context != null) {
        searchBuilder.attachSearch(context, menu.findItem(R.id.action_search));
        issuesAnalytics.attachSearchSuccess(true);
        return;
      } else {
        issuesAnalytics.attachSearchFailed(true);
        Logger.e(TAG, new IllegalStateException(
            "Unable to attach search to this fragment due to null parent"));
      }
    } else {
      issuesAnalytics.attachSearchFailed(false);
      Logger.e(TAG, new IllegalStateException(
          "Unable to attach search to this fragment due to invalid search builder"));
    }

    menu.removeItem(R.id.action_search);

    uninstallMenuItem = menu.findItem(R.id.menu_uninstall);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();
    if (i == R.id.menu_share) {

      final boolean appRatingExists = app != null
          && app.getStats() != null
          && app.getStats()
          .getRating() != null;

      final float averageRating = appRatingExists ? app.getStats()
          .getRating()
          .getAvg() : 0f;

      final boolean appHasStore = app != null && app.getStore() != null;

      final Long storeId = appHasStore ? app.getStore()
          .getId() : null;

      shareAppHelper.shareApp(appName, packageName, wUrl, (app == null ? null : app.getIcon()),
          averageRating, SpotAndShareAnalytics.SPOT_AND_SHARE_START_CLICK_ORIGIN_APPVIEW, storeId);

      appViewAnalytics.sendAppShareEvent();
      return true;
    } else if (i == R.id.menu_schedule) {
      appViewAnalytics.sendScheduleDownloadEvent();
      final Scheduled scheduled = createScheduled(app, appAction);

      ScheduledAccessor scheduledAccessor = AccessorFactory.getAccessorFor(
          ((AptoideApplication) getContext().getApplicationContext()
              .getApplicationContext()).getDatabase(), Scheduled.class);
      scheduledAccessor.insert(scheduled);

      String str = this.getString(R.string.added_to_scheduled);
      ShowMessage.asSnack(this.getView(), str);
      return true;
    } else if (i == R.id.menu_uninstall && unInstallAction != null) {
      unInstallAction.call();
      return true;
    } else if (i == R.id.menu_remote_install) {
      appViewAnalytics.sendRemoteInstallEvent();
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

    return super.onOptionsItemSelected(item);
  }

  private Scheduled createScheduled(GetAppMeta.App app, AppAction appAction) {

    String mainObbName = null;
    String mainObbPath = null;
    String mainObbMd5 = null;

    String patchObbName = null;
    String patchObbPath = null;
    String patchObbMd5 = null;

    Obb obb = app.getObb();
    if (obb != null) {
      Obb.ObbItem obbMain = obb.getMain();
      Obb.ObbItem obbPatch = obb.getPatch();

      if (obbMain != null) {
        mainObbName = obbMain.getFilename();
        mainObbPath = obbMain.getPath();
        mainObbMd5 = obbMain.getMd5sum();
      }

      if (obbPatch != null) {
        patchObbName = obbPatch.getFilename();
        patchObbPath = obbPatch.getPath();
        patchObbMd5 = obbPatch.getMd5sum();
      }
    }

    return new Scheduled(app.getName(), app.getFile()
        .getVername(), app.getIcon(), app.getFile()
        .getPath(), app.getFile()
        .getMd5sum(), app.getFile()
        .getVercode(), app.getPackageName(), app.getStore()
        .getName(), app.getFile()
        .getPathAlt(), mainObbName, mainObbPath, mainObbMd5, patchObbName, patchObbPath,
        patchObbMd5, false, appAction.name());
  }

  private Observable<GetApp> manageOrganicAds(GetApp getApp) {
    String packageName = getApp.getNodes()
        .getMeta()
        .getData()
        .getPackageName();
    String storeName = getApp.getNodes()
        .getMeta()
        .getData()
        .getStore()
        .getName();

    if (searchAdResult == null) {
      return adsRepository.getAdsFromAppView(packageName, storeName)
          .map(SearchAdResult::new)
          .doOnNext(ad -> {
            searchAdResult = ad;
            handleAdsLogic(searchAdResult);
          })
          .map(ad -> getApp)
          .onErrorReturn(throwable -> getApp);
    } else {
      handleAdsLogic(searchAdResult);
      return Observable.just(getApp);
    }
  }

  private void storeMinimalAdd(SearchAdResult searchAdResult) {
    storedMinimalAdAccessor.insert(adMapper.map(searchAdResult, null));
  }

  @NonNull private Observable<GetApp> setKeywords(GetApp getApp) {
    keywords = getApp.getNodes()
        .getMeta()
        .getData()
        .getMedia()
        .getKeywords();

    return Observable.just(getApp);
  }

  private void setupAppView(GetApp getApp) {
    app = getApp.getNodes()
        .getMeta()
        .getData();

    List<Group> groupsList = getApp.getNodes()
        .getGroups()
        .getDataList()
        .getList();

    if (groupsList.size() > 0) {
      final Group group = groupsList.get(0);
    }

    updateLocalVars(app);
    if (storeTheme == null) {
      storeTheme = getApp.getNodes()
          .getMeta()
          .getData()
          .getStore()
          .getAppearance()
          .getTheme();
    }

    // useful data for the syncAuthorization updates menu option
    installAction(packageName, app.getFile()
        .getVercode()).observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(appAction -> {
          AppViewFragment.this.appAction = appAction;
          MenuItem item = menu.findItem(R.id.menu_schedule);
          if (item != null) {
            showHideOptionsMenu(item, appAction != AppAction.OPEN);
          }
          if (appAction != AppAction.INSTALL) {
            setUnInstallMenuOptionVisible(() -> new PermissionManager().requestDownloadAccess(
                (PermissionService) getContext())
                .flatMap(success -> installManager.uninstall(packageName, app.getFile()
                    .getVername())
                    .toObservable())
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(aVoid -> {
                }, throwable -> throwable.printStackTrace()));
          } else {
            setUnInstallMenuOptionVisible(null);
          }
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        });

    header.setup(getApp);
    clearDisplayables().addDisplayables(setupDisplayables(getApp), true);
    setupObservables(getApp);
    showHideOptionsMenu(true);
    setupShare(getApp);
    if (openType == OpenType.OPEN_WITH_INSTALL_POPUP) {
      openType = null;
      GenericDialogs.createGenericOkCancelMessage(getContext(), marketName,
          getContext().getString(R.string.installapp_alrt, appName))
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
          .subscribe(new SimpleSubscriber<GenericDialogs.EResponse>() {
            @Override public void onNext(GenericDialogs.EResponse eResponse) {
              super.onNext(eResponse);
              switch (eResponse) {
                case YES:
                  installDisplayable.startInstallationProcess();
                  break;
                default:
                  break;
              }
            }
          });
    }
    if (isSuggestedShowing()) {
      showSuggestedApps();
    }
    finishLoading();
  }

  private void handleAdsLogic(SearchAdResult searchAdResult) {
    storeMinimalAdd(searchAdResult);
    AdNetworkUtils.knockCpc(adMapper.map(searchAdResult));
    AptoideUtils.ThreadU.runOnUiThread(
        () -> ReferrerUtils.extractReferrer(searchAdResult, ReferrerUtils.RETRIES, false,
            adsRepository, httpClient, converterFactory, qManager,
            getContext().getApplicationContext(),
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            new MinimalAdMapper()));
  }

  private void updateLocalVars(GetAppMeta.App app) {
    appId = app.getId();
    packageName = app.getPackageName();
    storeName = app.getStore()
        .getName();
    storeId = app.getStore()
        .getId();
    if (storeTheme == null) {
      storeTheme = app.getStore()
          .getAppearance()
          .getTheme();
    }
    md5 = app.getMd5();
    appName = app.getName();
  }

  public Observable<AppAction> installAction(String packageName, int versionCode) {
    return installManager.getInstall(md5, packageName, versionCode)
        .map(installationProgress -> installationProgress.getType())
        .map(installationType -> {
          AppAction action;
          switch (installationType) {
            case INSTALLED:
              action = AppAction.OPEN;
              break;
            case INSTALL:
              action = AppAction.INSTALL;
              break;
            case UPDATE:
              action = AppAction.UPDATE;
              break;
            case DOWNGRADE:
              action = AppAction.DOWNGRADE;
              break;
            default:
              action = AppAction.INSTALL;
          }
          return action;
        });
  }

  protected void showHideOptionsMenu(MenuItem item, boolean visible) {
    if (item != null) {
      item.setVisible(visible);
    }
  }

  @Override public void setUnInstallMenuOptionVisible(@Nullable Action0 unInstallAction) {
    this.unInstallAction = unInstallAction;
    showHideOptionsMenu(uninstallMenuItem, unInstallAction != null);
  }

  protected LinkedList<Displayable> setupDisplayables(GetApp getApp) {
    LinkedList<Displayable> displayables = new LinkedList<>();

    GetAppMeta.App app = getApp.getNodes()
        .getMeta()
        .getData();
    GetAppMeta.Media media = app.getMedia();

    final boolean shouldInstall = openType == OpenType.OPEN_AND_INSTALL;
    if (openType == OpenType.OPEN_AND_INSTALL) {
      openType = null;
    }
    installDisplayable =
        AppViewInstallDisplayable.newInstance(getApp, installManager, searchAdResult, shouldInstall,
            installedRepository, downloadFactory, timelineAnalytics, appViewAnalytics,
            installAppRelay, this,
            new DownloadCompleteAnalytics(Analytics.getInstance(), Answers.getInstance(),
                AppEventsLogger.newLogger(getContext().getApplicationContext())), navigationTracker,
            getEditorsBrickPosition());
    displayables.add(installDisplayable);
    displayables.add(new AppViewStoreDisplayable(getApp, appViewAnalytics, storeAnalytics));
    displayables.add(
        new AppViewRateAndCommentsDisplayable(getApp, storeCredentialsProvider, appViewAnalytics,
            installedRepository));

    // only show screen shots / video if the app has them
    if (isMediaAvailable(media)) {
      displayables.add(new AppViewScreenshotsDisplayable(app, appViewAnalytics));
    }
    displayables.add(new AppViewDescriptionDisplayable(getApp, appViewAnalytics));

    displayables.add(new AppViewFlagThisDisplayable(getApp, appViewAnalytics));
    displayables.add(new AppViewDeveloperDisplayable(getApp));

    return displayables;
  }

  private void setupObservables(GetApp getApp) {

    // ??

    final long storeId = getApp.getNodes()
        .getMeta()
        .getData()
        .getStore()
        .getId();

    // For stores subscription
    //DeprecatedDatabase.StoreQ.getAll(realm)
    //    .asObservable()
    //    .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
    //    .subscribe(stores -> {
    //      if (DeprecatedDatabase.StoreQ.get(storeId, realm) != null) {
    //        adapter.notifyDataSetChanged();
    //      }
    //    });

    final StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class);
    storeAccessor.getAll()
        .flatMapIterable(list -> list)
        .filter(store -> store != null && store.getStoreId() == storeId)
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(store -> {
          getAdapter().notifyDataSetChanged();
        });

    // ??

    // For install actions
    //DeprecatedDatabase.RollbackQ.getAll(realm)
    //    .asObservable()
    //    .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
    //    .subscribe(rollbacks -> {
    //      adapter.notifyDataSetChanged();
    //    });

    final RollbackAccessor rollbackAccessor = AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Rollback.class);
    rollbackAccessor.getAll()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(rollbacks -> {
          getAdapter().notifyDataSetChanged();
        });

    // TODO: 27-05-2016 neuro install actions, not present in v7
  }

  private void showHideOptionsMenu(boolean visible) {
    for (int i = 0; i < menu.size(); i++) {
      MenuItem item = menu.getItem(i);
      showHideOptionsMenu(item, visible);
    }
  }

  public void setupShare(GetApp app) {
    appName = app.getNodes()
        .getMeta()
        .getData()
        .getName();
    wUrl = app.getNodes()
        .getMeta()
        .getData()
        .getUrls()
        .getW();
  }

  private boolean isMediaAvailable(GetAppMeta.Media media) {
    if (media != null) {
      List<GetAppMeta.Media.Screenshot> screenshots = media.getScreenshots();
      List<GetAppMeta.Media.Video> videos = media.getVideos();
      boolean hasScreenShots = screenshots != null && screenshots.size() > 0;
      boolean hasVideos = videos != null && videos.size() > 0;
      return hasScreenShots || hasVideos;
    }
    return false;
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle("");
  }

  //
  // micro widget for header
  //

  @Override public void scroll(Position position) {
    RecyclerView rView = getRecyclerView();
    if (rView == null || getAdapter().getItemCount() == 0) {
      Logger.e(TAG, "Recycler view is null or there are no elements in the adapter");
      return;
    }

    if (position == Position.FIRST) {
      rView.scrollToPosition(0);
    } else if (position == Position.LAST) {
      rView.scrollToPosition(getAdapter().getItemCount());
    }
  }

  @Override public void itemAdded(int pos) {
    getLayoutManager().onItemsAdded(getRecyclerView(), pos, 1);
  }

  @Override public void itemRemoved(int pos) {
    getLayoutManager().onItemsRemoved(getRecyclerView(), pos, 1);
  }

  @Override public void itemChanged(int pos) {
    getLayoutManager().onItemsUpdated(getRecyclerView(), pos, 1);
  }

  public void showSuggestedApps() {
    appViewSimilarAppAnalytics.similarAppsIsShown();
    suggestedShowing = true;

    adsRepository.getAdsFromAppviewSuggested(packageName, keywords)
        .onErrorReturn(throwable -> Collections.emptyList())
        .zipWith(requestFactoryCdnWeb.newGetRecommendedRequest(6, packageName)
            .observe(), (minimalAds, listApps) -> new AppViewSuggestedAppsDisplayable(minimalAds,
            removeCurrentAppFromSuggested(listApps.getDataList()
                // TODO: 04/10/2017 trinkes make some default thing for StoreContext.home
                .getList()), appViewSimilarAppAnalytics, navigationTracker, StoreContext.home))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(appViewSuggestedAppsDisplayable -> {
          addDisplayableWithAnimation(1, appViewSuggestedAppsDisplayable);
        }, Throwable::printStackTrace);
  }

  private List<App> removeCurrentAppFromSuggested(List<App> list) {
    Iterator<App> iterator = list.iterator();
    while (iterator.hasNext()) {
      App next = iterator.next();
      if (next.getPackageName()
          .equals(packageName)) {
        iterator.remove();
      }
    }
    return list;
  }

  public String getMd5() {
    return appViewModel.getMd5();
  }

  public void setMd5(String md5) {
    this.appViewModel.setMd5(md5);
  }

  public String getUname() {
    return appViewModel.getUname();
  }

  public OpenType getOpenType() {
    return appViewModel.getOpenType();
  }

  public void setOpenType(OpenType openType) {
    this.appViewModel.setOpenType(openType);
  }

  public SearchAdResult getSearchAdResult() {
    return appViewModel.getSearchAdResult();
  }

  public void setSearchAdResult(SearchAdResult searchAdResult) {
    this.appViewModel.setSearchAdResult(searchAdResult);
  }

  public long getAppId() {
    return appViewModel.getAppId();
  }

  public void setAppId(long appId) {
    this.appViewModel.setAppId(appId);
  }

  public boolean isSponsored() {
    return appViewModel.isSponsored();
  }

  public String getStoreTheme() {
    return appViewModel.getStoreTheme();
  }

  public void setStoreTheme(String storeTheme) {
    this.appViewModel.setStoreTheme(storeTheme);
  }

  public String getStoreName() {
    return appViewModel.getStoreName();
  }

  public void setStoreName(String storeName) {
    this.appViewModel.setStoreName(storeName);
  }

  public GetAppMeta.App getApp() {
    return appViewModel.getApp();
  }

  public String getMarketName() {
    return appViewModel.getMarketName();
  }

  public String getEditorsBrickPosition() {
    return appViewModel.getEditorsBrickPosition();
  }

  protected enum BundleKeys {
    APP_ID, STORE_NAME, MINIMAL_AD, PACKAGE_NAME, SHOULD_INSTALL, MD5, UNAME,
  }

  public enum OpenType {
    /**
     * Only open the appview
     */
    OPEN_ONLY, /**
     * opens the appView and starts the installation
     */
    OPEN_AND_INSTALL, /**
     * open the appView and ask user if want to install the app
     */
    OPEN_WITH_INSTALL_POPUP
  }

  private final class AppViewHeader {

    private static final String BADGE_DIALOG_TAG = "badgeDialog";

    private final boolean animationsEnabled;
    private final AppBarLayout appBarLayout;
    private final CollapsingToolbarLayout collapsingToolbar;
    private final ImageView featuredGraphic;
    private final ImageView badge;
    private final TextView badgeText;
    private final ImageView appIcon;
    private final TextView fileSize;
    private final TextView downloadsCountInStore;
    private final TextView downloadsCount;

    AppViewHeader(@NonNull View view) {
      animationsEnabled = ManagerPreferences.getAnimationsEnabledStatus(
          ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());

      appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);
      collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
      appIcon = (ImageView) view.findViewById(R.id.app_icon);
      featuredGraphic = (ImageView) view.findViewById(R.id.featured_graphic);
      badge = (ImageView) view.findViewById(R.id.badge_img);
      badgeText = (TextView) view.findViewById(R.id.badge_text);
      fileSize = (TextView) view.findViewById(R.id.file_size);
      downloadsCountInStore = (TextView) view.findViewById(R.id.downloads_count_in_store);
      downloadsCount = (TextView) view.findViewById(R.id.downloads_count);
    }

    public AppBarLayout getAppBarLayout() {
      return appBarLayout;
    }

    public CollapsingToolbarLayout getCollapsingToolbar() {
      return collapsingToolbar;
    }

    public ImageView getFeaturedGraphic() {
      return featuredGraphic;
    }

    public ImageView getBadge() {
      return badge;
    }

    public TextView getBadgeText() {
      return badgeText;
    }

    public ImageView getAppIcon() {
      return appIcon;
    }

    public TextView getFileSize() {
      return fileSize;
    }

    public TextView getDownloadsCountInStore() {
      return downloadsCountInStore;
    }

    public TextView getDownloadsCount() {
      return downloadsCount;
    }

    // setup methods
    public void setup(@NonNull GetApp getApp) {

      GetAppMeta.App app = getApp.getNodes()
          .getMeta()
          .getData();

      String headerImageUrl = app.getGraphic();
      List<GetAppMeta.Media.Screenshot> screenshots = app.getMedia()
          .getScreenshots();

      final Context context = getContext();
      if (!TextUtils.isEmpty(headerImageUrl)) {
        ImageLoader.with(context)
            .load(app.getGraphic(), R.drawable.app_view_header_gradient, featuredGraphic);
      } else if (screenshots != null && screenshots.size() > 0 && !TextUtils.isEmpty(
          screenshots.get(0)
              .getUrl())) {
        ImageLoader.with(context)
            .load(screenshots.get(0)
                .getUrl(), R.drawable.app_view_header_gradient, featuredGraphic);
      }

      if (app.getIcon() != null) {
        ImageLoader.with(context)
            .load(getApp.getNodes()
                .getMeta()
                .getData()
                .getIcon(), appIcon);
      }

      collapsingToolbar.setTitle(app.getName());
      StoreTheme storeTheme = StoreTheme.get(AppViewFragment.this.storeTheme);
      collapsingToolbar.setBackgroundColor(
          ContextCompat.getColor(getActivity(), storeTheme.getPrimaryColor()));
      collapsingToolbar.setContentScrimColor(
          ContextCompat.getColor(getActivity(), storeTheme.getPrimaryColor()));
      ThemeUtils.setStatusBarThemeColor(getActivity(),
          StoreTheme.get(AppViewFragment.this.storeTheme));
      // un-comment the following lines to give app icon a fading effect when user expands / collapses the action bar
      /*
      appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {

				@Override
				public void onStateChanged(AppBarLayout appBarLayout, State state) {
					switch (state) {
						case EXPANDED:
							if (animationsEnabled) {
								appIcon.animate().alpha(1F).start();
							} else {
								appIcon.setVisibility(View.VISIBLE);
							}
							break;

						default:
						case IDLE:
						case COLLAPSED:
							if (animationsEnabled) {
								appIcon.animate().alpha(0F).start();
							} else {
								appIcon.setVisibility(View.INVISIBLE);
							}
							break;
					}
				}
			});
			*/

      fileSize.setText(AptoideUtils.StringU.formatBytes(app.getSize(), false));

      downloadsCountInStore.setText(AptoideUtils.StringU.withSuffix(app.getStats()
          .getDownloads()));
      downloadsCount.setText(AptoideUtils.StringU.withSuffix(app.getStats()
          .getPdownloads()));

      @DrawableRes int badgeResId = 0;
      @StringRes int badgeMessageId = 0;

      Malware.Rank rank = app.getFile()
          .getMalware()
          .getRank() == null ? Malware.Rank.UNKNOWN : app.getFile()
          .getMalware()
          .getRank();
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

      ImageLoader.with(context)
          .load(badgeResId, badge);
      badgeText.setText(badgeMessageId);

      if (getEditorsBrickPosition() != null) {
        appViewAnalytics.sendEditorsChoiceClickEvent(navigationTracker.getPreviousScreen(),
            getPackageName(), getEditorsBrickPosition());
      }
      appViewAnalytics.sendAppViewOpenedFromEvent(navigationTracker.getPreviousScreen(),
          navigationTracker.getCurrentScreen(), getPackageName(), app.getDeveloper()
              .getName(), app.getFile()
              .getMalware()
              .getRank()
              .name());
      final Malware malware = app.getFile()
          .getMalware();
      badge.setOnClickListener(v -> {
        appViewAnalytics.sendBadgeClickEvent();
        DialogBadgeV7.newInstance(malware, app.getName(), malware.getRank())
            .show(getFragmentManager(), BADGE_DIALOG_TAG);
      });
    }
  }

  private class Keys {
    public static final String SUGGESTED_SHOWING = "SUGGESTED_SHOWING";
  }
}

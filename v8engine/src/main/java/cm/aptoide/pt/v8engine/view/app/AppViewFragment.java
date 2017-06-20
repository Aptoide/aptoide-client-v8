/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.app;

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
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.database.AppAction;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.accessors.RollbackAccessor;
import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.accessors.StoredMinimalAdAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Malware;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.SimpleSubscriber;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.ads.AdsRepository;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.app.AppBoughtReceiver;
import cm.aptoide.pt.v8engine.app.AppRepository;
import cm.aptoide.pt.v8engine.app.AppViewAnalytics;
import cm.aptoide.pt.v8engine.billing.PaymentAnalytics;
import cm.aptoide.pt.v8engine.billing.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.billing.purchase.PaidAppPurchase;
import cm.aptoide.pt.v8engine.billing.view.PaymentActivity;
import cm.aptoide.pt.v8engine.billing.view.PurchaseIntentMapper;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.repository.InstalledRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.spotandshare.SpotAndShareAnalytics;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreTheme;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.util.referrer.ReferrerUtils;
import cm.aptoide.pt.v8engine.view.ThemeUtils;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewDescriptionDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewDeveloperDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewFlagThisDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewRateAndCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewScreenshotsDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewStoreDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewSuggestedAppsDisplayable;
import cm.aptoide.pt.v8engine.view.dialog.DialogBadgeV7;
import cm.aptoide.pt.v8engine.view.fragment.AptoideBaseFragment;
import cm.aptoide.pt.v8engine.view.install.remote.RemoteInstallDialog;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.share.ShareAppHelper;
import cm.aptoide.pt.v8engine.view.store.StoreFragment;
import com.facebook.appevents.AppEventsLogger;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import okhttp3.OkHttpClient;
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
  //
  // constants
  //
  private static final String TAG = AppViewFragment.class.getSimpleName();
  private static final String BAR_EXPANDED = "BAR_EXPANDED";
  private static final int PAY_APP_REQUEST_CODE = 12;

  private final String key_appId = "appId";
  private final String key_packageName = "packageName";
  private final String key_uname = "uname";
  private final String key_md5sum = "md5sum";
  //private static final String TAG = AppViewFragment.class.getName();
  //
  // vars
  //
  private AppViewHeader header;
  private long appId;
  @Partners @Getter private String packageName;
  private OpenType openType;
  private Scheduled scheduled;
  private String storeTheme;
  //
  // static fragment default new instance method
  //
  private MinimalAd minimalAd;
  // Stored to postpone ads logic
  private InstallManager installManager;
  private Action0 unInstallAction;
  private MenuItem uninstallMenuItem;
  private AppRepository appRepository;
  private Subscription subscription;
  private AdsRepository adsRepository;
  private boolean sponsored;
  private List<MinimalAd> suggestedAds;
  // buy app vars
  private String storeName;
  private float priceValue;
  private String currency;
  private double taxRate;
  private AppViewInstallDisplayable installDisplayable;
  private String md5;
  private String uname;
  private PermissionManager permissionManager;
  private Menu menu;
  @Partners @Getter private String appName;
  @Partners @Getter private String wUrl;
  private GetAppMeta.App app;
  private AppAction appAction = AppAction.OPEN;
  private InstalledRepository installedRepository;
  private GetApp getApp;
  private AptoideAccountManager accountManager;
  private StoreCredentialsProvider storeCredentialsProvider;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private SocialRepository socialRepository;
  private AccountNavigator accountNavigator;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private StoredMinimalAdAccessor storedMinimalAdAccessor;
  private PaymentAnalytics paymentAnalytics;
  private SpotAndShareAnalytics spotAndShareAnalytics;
  private PurchaseIntentMapper purchaseIntentMapper;
  private ShareAppHelper shareAppHelper;
  private QManager qManager;
  private TimelineAnalytics timelineAnalytics;
  private AppViewAnalytics appViewAnalytics;

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

  public static AppViewFragment newInstance(long appId, String packageName, OpenType openType) {
    Bundle bundle = new Bundle();
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

  public static AppViewFragment newInstance(MinimalAd minimalAd) {
    Bundle bundle = new Bundle();
    bundle.putLong(BundleKeys.APP_ID.name(), minimalAd.getAppId());
    bundle.putString(BundleKeys.PACKAGE_NAME.name(), minimalAd.getPackageName());
    bundle.putParcelable(BundleKeys.MINIMAL_AD.name(), minimalAd);

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

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    qManager = ((V8Engine) getContext().getApplicationContext()).getQManager();
    purchaseIntentMapper =
        ((V8Engine) getContext().getApplicationContext()).getPurchaseIntentMapper();
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    accountNavigator =
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator());
    permissionManager = new PermissionManager();
    installManager = ((V8Engine) getContext().getApplicationContext()).getInstallManager(
        InstallerFactory.ROLLBACK);
    bodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    paymentAnalytics = ((V8Engine) getContext().getApplicationContext()).getPaymentAnalytics();
    final TokenInvalidator tokenInvalidator =
        ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator();
    timelineAnalytics = new TimelineAnalytics(Analytics.getInstance(),
        AppEventsLogger.newLogger(getContext().getApplicationContext()), bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, V8Engine.getConfiguration()
        .getAppId(),
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
    socialRepository =
        new SocialRepository(accountManager, bodyInterceptor, converterFactory, httpClient,
            timelineAnalytics, tokenInvalidator,
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
    appRepository = RepositoryFactory.getAppRepository(getContext(),
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
    httpClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    adsRepository = ((V8Engine) getContext().getApplicationContext()).getAdsRepository();
    installedRepository = RepositoryFactory.getInstalledRepository();
    storeCredentialsProvider = new StoreCredentialsProviderImpl();
    storedMinimalAdAccessor = AccessorFactory.getAccessorFor(StoredMinimalAd.class);
    spotAndShareAnalytics = new SpotAndShareAnalytics(Analytics.getInstance());
    paymentAnalytics = ((V8Engine) getContext().getApplicationContext()).getPaymentAnalytics();
    shareAppHelper =
        new ShareAppHelper(installedRepository, accountManager, accountNavigator, getActivity(),
            spotAndShareAnalytics, timelineAnalytics,
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
    appViewAnalytics = new AppViewAnalytics(Analytics.getInstance(),
        AppEventsLogger.newLogger(getContext().getApplicationContext()));
  }

  @Partners @Override public void loadExtras(Bundle args) {
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
    minimalAd = args.getParcelable(BundleKeys.MINIMAL_AD.name());
    storeName = args.getString(BundleKeys.STORE_NAME.name());
    sponsored = minimalAd != null;
    storeTheme = args.getString(StoreFragment.BundleCons.STORE_THEME);
  }

  @Override public int getContentViewId() {
    return VIEW_ID;
  }

  @Partners @Override public void bindViews(View view) {
    super.bindViews(view);
    header = new AppViewHeader(view);
    setHasOptionsMenu(true);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    header = null;
    if (storeTheme != null) {
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreTheme.get(V8Engine.getConfiguration()
          .getDefaultTheme()));
      ThemeUtils.setAptoideTheme(getActivity());
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);

    if (subscription != null) {
      subscription.unsubscribe();
    }

    if (appId >= 0) {
      Logger.d(TAG, "loading app info using app ID");
      subscription = appRepository.getApp(appId, refresh, sponsored, storeName, packageName)
          .map(getApp -> this.getApp = getApp)
          .flatMap(getApp -> manageOrganicAds(getApp))
          .flatMap(getApp -> manageSuggestedAds(getApp).onErrorReturn(throwable -> getApp))
          .observeOn(AndroidSchedulers.mainThread())
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
          .subscribe(getApp -> {
            setupAppView(getApp);
          }, throwable -> finishLoading(throwable));
    } else if (!TextUtils.isEmpty(md5)) {
      subscription = appRepository.getAppFromMd5(md5, refresh, sponsored)
          .map(getApp -> this.getApp = getApp)
          .flatMap(getApp -> manageOrganicAds(getApp))
          .flatMap(getApp -> manageSuggestedAds(getApp).onErrorReturn(throwable -> getApp))
          .observeOn(AndroidSchedulers.mainThread())
          .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
          .subscribe(getApp -> {
            setupAppView(getApp);
          }, throwable -> {
            finishLoading(throwable);
          });
    } else if (!TextUtils.isEmpty(uname)) {
      subscription = appRepository.getAppFromUname(uname, refresh, sponsored)
          .map(getApp -> this.getApp = getApp)
          .flatMap(getApp -> manageOrganicAds(getApp))
          .flatMap(getApp -> manageSuggestedAds(getApp).onErrorReturn(throwable -> getApp))
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
          .map(getApp -> this.getApp = getApp)
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

  private boolean hasDescription(GetAppMeta.Media media) {
    return !TextUtils.isEmpty(media.getDescription());
  }

  public void buyApp(GetAppMeta.App app) {
    paymentAnalytics.sendPaidAppBuyButtonPressedEvent(app.getPay()
        .getPrice(), app.getPay()
        .getCurrency());
    startActivityForResult(PaymentActivity.getIntent(getActivity(), app.getId(), app.getStore()
        .getName(), sponsored), PAY_APP_REQUEST_CODE);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PAY_APP_REQUEST_CODE) {
      try {
        final PaidAppPurchase purchase =
            (PaidAppPurchase) purchaseIntentMapper.map(data, resultCode);

        FragmentActivity fragmentActivity = getActivity();
        Intent installApp = new Intent(AppBoughtReceiver.APP_BOUGHT);
        installApp.putExtra(AppBoughtReceiver.APP_ID, appId);
        installApp.putExtra(AppBoughtReceiver.APP_PATH, purchase.getApkPath());
        fragmentActivity.sendBroadcast(installApp);
      } catch (Throwable throwable) {
        if (throwable instanceof PaymentCancellationException) {
          Logger.i(TAG, "The user canceled.");
          ShowMessage.asSnack(header.badge, R.string.user_cancelled);
        } else {
          Logger.i(TAG,
              "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
          ShowMessage.asSnack(header.badge, R.string.unknown_error);
        }
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    this.menu = menu;
    inflater.inflate(R.menu.menu_appview_fragment, menu);
    SearchUtils.setupGlobalSearchView(menu, this);
    uninstallMenuItem = menu.findItem(R.id.menu_uninstall);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();

    if (i == R.id.menu_share) {
      shareAppHelper.shareApp(appName, packageName, wUrl, (app == null ? null : app.getIcon()),
          (app != null
              && app.getStats() != null
              && app.getStats()
              .getRating() != null ? app.getStats()
              .getRating()
              .getAvg() : 0f), SpotAndShareAnalytics.SPOT_AND_SHARE_START_CLICK_ORIGIN_APPVIEW);
      appViewAnalytics.sendAppShareEvent();
      return true;
    } else if (i == R.id.menu_schedule) {
      appViewAnalytics.sendScheduleDownloadEvent();
      scheduled = Scheduled.from(app, appAction);

      ScheduledAccessor scheduledAccessor = AccessorFactory.getAccessorFor(Scheduled.class);
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

    if (minimalAd == null) {
      return adsRepository.getAdsFromAppView(packageName, storeName)
          .doOnNext(ad -> {
            minimalAd = ad;
            handleAdsLogic(minimalAd);
          })
          .map(ad -> getApp)
          .onErrorReturn(throwable -> getApp);
    } else {
      handleAdsLogic(minimalAd);
      return Observable.just(getApp);
    }
  }

  private void storeMinimalAdd(MinimalAd minimalAd) {
    storedMinimalAdAccessor.insert(StoredMinimalAd.from(minimalAd, null));
  }

  @NonNull private Observable<GetApp> manageSuggestedAds(GetApp getApp1) {
    List<String> keywords = getApp1.getNodes()
        .getMeta()
        .getData()
        .getMedia()
        .getKeywords();
    String packageName = getApp1.getNodes()
        .getMeta()
        .getData()
        .getPackageName();

    return adsRepository.getAdsFromAppviewSuggested(packageName, keywords)
        .map(minimalAds -> {
          suggestedAds = minimalAds;
          return getApp1;
        });
  }

  private void setupAppView(GetApp getApp) {
    app = getApp.getNodes()
        .getMeta()
        .getData();
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
    installAction().observeOn(AndroidSchedulers.mainThread())
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
                .flatMap(success -> installManager.uninstall(getContext(), packageName,
                    app.getFile()
                        .getVername()))
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
      GenericDialogs.createGenericOkCancelMessage(getContext(), Application.getConfiguration()
          .getMarketName(), getContext().getString(R.string.installapp_alrt, appName))
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
    finishLoading();
  }

  private void handleAdsLogic(MinimalAd minimalAd) {
    storeMinimalAdd(minimalAd);
    DataproviderUtils.AdNetworksUtils.knockCpc(minimalAd);
    AptoideUtils.ThreadU.runOnUiThread(
        () -> ReferrerUtils.extractReferrer(minimalAd, ReferrerUtils.RETRIES, false, adsRepository,
            httpClient, converterFactory, qManager, getContext().getApplicationContext(),
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences()));
  }

  private void updateLocalVars(GetAppMeta.App app) {
    appId = app.getId();
    packageName = app.getPackageName();
    storeName = app.getStore()
        .getName();
    storeTheme = app.getStore()
        .getAppearance()
        .getTheme();
    md5 = app.getMd5();
    appName = app.getName();
  }

  public Observable<AppAction> installAction() {
    InstalledAccessor installedAccessor = AccessorFactory.getAccessorFor(Installed.class);
    return installedAccessor.getAsList(packageName)
        .map(installedList -> {
          if (installedList != null && installedList.size() > 0) {
            Installed installed = installedList.get(0);
            if (app.getFile()
                .getVercode() == installed.getVersionCode()) {
              //current installed version
              return AppAction.OPEN;
            } else if (app.getFile()
                .getVercode() > installed.getVersionCode()) {
              //update
              return AppAction.UPDATE;
            } else {
              //downgrade
              return AppAction.DOWNGRADE;
            }
          } else {
            //app not installed
            return AppAction.INSTALL;
          }
        });
  }

  @Partners protected void showHideOptionsMenu(MenuItem item, boolean visible) {
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
        AppViewInstallDisplayable.newInstance(getApp, installManager, minimalAd, shouldInstall,
            installedRepository, timelineAnalytics, appViewAnalytics);
    displayables.add(installDisplayable);
    displayables.add(new AppViewStoreDisplayable(getApp, appViewAnalytics));
    displayables.add(
        new AppViewRateAndCommentsDisplayable(getApp, storeCredentialsProvider, appViewAnalytics,
            installedRepository));

    // only show screen shots / video if the app has them
    if (isMediaAvailable(media)) {
      displayables.add(new AppViewScreenshotsDisplayable(app, appViewAnalytics));
    }
    displayables.add(new AppViewDescriptionDisplayable(getApp, appViewAnalytics));

    displayables.add(new AppViewFlagThisDisplayable(getApp, appViewAnalytics));
    if (suggestedAds != null) {
      displayables.add(new AppViewSuggestedAppsDisplayable(suggestedAds, appViewAnalytics));
    }
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

    final StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
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

    final RollbackAccessor rollbackAccessor = AccessorFactory.getAccessorFor(Rollback.class);
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

  @Partners protected enum BundleKeys {
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

    // views
    @Getter private final AppBarLayout appBarLayout;

    @Getter private final CollapsingToolbarLayout collapsingToolbar;

    @Getter private final ImageView featuredGraphic;

    @Getter private final ImageView badge;

    @Getter private final TextView badgeText;

    @Getter private final ImageView appIcon;

    @Getter private final TextView fileSize;

    @Getter private final TextView downloadsCountInStore;

    @Getter private final TextView downloadsCount;

    // ctor
    public AppViewHeader(@NonNull View view) {
      animationsEnabled = ManagerPreferences.getAnimationsEnabledStatus(
          ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());

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

      Analytics.AppViewViewedFrom.appViewOpenFrom(app.getPackageName(), app.getDeveloper()
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
}

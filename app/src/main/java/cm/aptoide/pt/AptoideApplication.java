package cm.aptoide.pt;

import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AccountFactory;
import cm.aptoide.accountmanager.AccountPersistence;
import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.AccountServiceV3;
import cm.aptoide.pt.account.AccountSettingsBodyInterceptorV7;
import cm.aptoide.pt.account.AndroidAccountDataMigration;
import cm.aptoide.pt.account.AndroidAccountManagerPersistence;
import cm.aptoide.pt.account.AndroidAccountProvider;
import cm.aptoide.pt.account.DatabaseStoreDataPersist;
import cm.aptoide.pt.account.FacebookLoginResult;
import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.ads.PackageRepositoryVersionCodeProvider;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.TrackerFilter;
import cm.aptoide.pt.billing.Billing;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.BillingPool;
import cm.aptoide.pt.billing.external.ExternalBillingSerializer;
import cm.aptoide.pt.billing.payment.Adyen;
import cm.aptoide.pt.billing.purchase.PurchaseFactory;
import cm.aptoide.pt.billing.view.PaymentThrowableCodeMapper;
import cm.aptoide.pt.billing.view.PurchaseBundleMapper;
import cm.aptoide.pt.crashreports.ConsoleLogger;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.crashreports.CrashlyticsCrashLogger;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Notification;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.dataprovider.cache.L2Cache;
import cm.aptoide.pt.dataprovider.cache.POSTCacheInterceptor;
import cm.aptoide.pt.dataprovider.cache.POSTCacheKeyAlgorithm;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.AdsApplicationVersionCodeProvider;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.deprecated.SQLiteDatabaseHelper;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.file.CacheHelper;
import cm.aptoide.pt.file.FileManager;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.install.RootInstallNotificationEventReceiver;
import cm.aptoide.pt.install.installer.RootInstallErrorNotificationFactory;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.leak.LeakTool;
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import cm.aptoide.pt.networking.BodyInterceptorV3;
import cm.aptoide.pt.networking.BodyInterceptorV7;
import cm.aptoide.pt.networking.Cdn;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.networking.MultipartBodyInterceptor;
import cm.aptoide.pt.networking.NoAuthenticationBodyInterceptorV3;
import cm.aptoide.pt.networking.NoOpTokenInvalidator;
import cm.aptoide.pt.networking.RefreshTokenInvalidator;
import cm.aptoide.pt.notification.NotificationCenter;
import cm.aptoide.pt.notification.NotificationIdsMapper;
import cm.aptoide.pt.notification.NotificationPolicyFactory;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.notification.NotificationService;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.notification.NotificationsCleaner;
import cm.aptoide.pt.notification.PnpV1NotificationService;
import cm.aptoide.pt.notification.SystemNotificationShower;
import cm.aptoide.pt.notification.sync.NotificationSyncFactory;
import cm.aptoide.pt.notification.sync.NotificationSyncManager;
import cm.aptoide.pt.preferences.AdultContent;
import cm.aptoide.pt.preferences.LocalPersistenceAdultContent;
import cm.aptoide.pt.preferences.PRNGFixes;
import cm.aptoide.pt.preferences.Preferences;
import cm.aptoide.pt.preferences.RemotePersistenceAdultContent;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.social.TimelineRepositoryFactory;
import cm.aptoide.pt.social.data.TimelinePostsRepository;
import cm.aptoide.pt.social.data.TimelineResponseCardMapper;
import cm.aptoide.pt.spotandshare.AccountGroupNameProvider;
import cm.aptoide.pt.spotandshare.ShareApps;
import cm.aptoide.pt.spotandshare.SpotAndShareAnalytics;
import cm.aptoide.pt.spotandshare.group.GroupNameProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.sync.SyncScheduler;
import cm.aptoide.pt.sync.SyncService;
import cm.aptoide.pt.sync.SyncStorage;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.SecurityUtils;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.view.ActivityProvider;
import cm.aptoide.pt.view.FragmentProvider;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.AppCenterRepository;
import cm.aptoide.pt.view.app.AppService;
import cm.aptoide.pt.view.entry.EntryActivity;
import cm.aptoide.pt.view.entry.EntryPointChooser;
import cm.aptoide.pt.view.recycler.DisplayableWidgetMapping;
import cm.aptoide.pt.view.share.NotLoggedInShareAnalytics;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import rx.Completable;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.preferences.managed.ManagedKeys.CAMPAIGN_SOCIAL_NOTIFICATIONS_PREFERENCE_VIEW_KEY;

public abstract class AptoideApplication extends Application {

  public static final String CACHE_FILE_NAME = "aptoide.wscache";
  private static final String TAG = AptoideApplication.class.getName();

  private static FragmentProvider fragmentProvider;
  private static ActivityProvider activityProvider;
  private static DisplayableWidgetMapping displayableWidgetMapping;
  private static ShareApps shareApps;
  private static boolean autoUpdateWasCalled = false;
  @Inject Database database;
  @Inject @Named("rollback") InstallManager rollbackInstallManager;
  @Inject AptoideDownloadManager downloadManager;
  @Inject CacheHelper cacheHelper;
  @Inject AppEventsLogger appEventsLogger;
  @Inject AptoideAccountManager accountManager;
  @Inject @Named("pool-v7") BodyInterceptor<BaseBody> bodyInterceptorPoolV7;
  private BodyInterceptor<BaseBody> bodyInterceptorWebV7;
  private BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3;
  @Inject Preferences preferences;
  private cm.aptoide.pt.preferences.SecurePreferences securePreferences;
  private SecureCoderDecoder secureCodeDecoder;
  private AdultContent adultContent;
  @Inject IdsRepository idsRepository;
  @Inject GoogleApiClient googleSignInClient;
  private LeakTool leakTool;
  private String aptoideMd5sum;
  @Inject @Named("default") OkHttpClient defaultClient;
  private OkHttpClient longTimeoutClient;
  private L2Cache httpClientCache;
  @Inject @Named("user-agent") Interceptor userAgentInterceptor;
  @Inject AndroidAccountProvider androidAccountProvider;
  private BillingAnalytics billingAnalytics;
  private ObjectMapper nonNullObjectMapper;
  private RequestBodyFactory requestBodyFactory;
  private ExternalBillingSerializer inAppBillingSerialzer;
  private PurchaseBundleMapper purchaseBundleMapper;
  private PaymentThrowableCodeMapper paymentThrowableCodeMapper;
  private MultipartBodyInterceptor multipartBodyInterceptor;
  private NotificationService pnpV1NotificationService;
  private NotificationCenter notificationCenter;
  private QManager qManager;
  private EntryPointChooser entryPointChooser;
  private NotificationSyncScheduler notificationSyncScheduler;
  @Inject RootAvailabilityManager rootAvailabilityManager;
  private RootInstallationRetryHandler rootInstallationRetryHandler;
  private RefreshTokenInvalidator tokenInvalidator;
  private FileManager fileManager;
  private StoreManager storeManager;
  private PackageRepository packageRepository;
  private AdsApplicationVersionCodeProvider applicationVersionCodeProvider;
  private AdsRepository adsRepository;
  private NotificationProvider notificationProvider;
  private SyncStorage syncStorage;
  private SyncScheduler syncScheduler;
  private TimelineRepositoryFactory timelineRepositoryFactory;
  @Inject AuthenticationPersistence authenticationPersistence;
  private BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody>
      noAuthorizationBodyInterceptorV3;
  private BehaviorRelay<Map<Integer, Result>> fragmentResultRelay;
  @Inject CallbackManager facebookCallbackManager;
  private Map<Integer, Result> fragmentResulMap;
  private PublishRelay<FacebookLoginResult> facebookLoginResultRelay;
  private NavigationTracker navigationTracker;
  private BillingPool billingPool;
  private NotLoggedInShareAnalytics notLoggedInShareAnalytics;
  @Inject AccountAnalytics accountAnalytics;
  private PageViewsAnalytics pageViewsAnalytics;
  private BodyInterceptor<BaseBody> accountSettingsBodyInterceptorPoolV7;
  private BodyInterceptor<BaseBody> accountSettingsBodyInterceptorWebV7;
  private Adyen adyen;
  private PurchaseFactory purchaseFactory;
  @Inject InstalledAccessor installedAccessor;
  @Inject Crashlytics crashlytics;
  @Inject @Named("retrofit-log") Interceptor retrofitLogInterceptor;
  @Inject AccountManager androidAccountManager;
  @Inject @Named("default") SharedPreferences defaultSharedPreferences;
  @Inject @Named("secure") SharedPreferences secureSharedPreferences;
  private ApplicationComponent applicationComponent;
  private AppCenter appCenter;

  public static FragmentProvider getFragmentProvider() {
    return fragmentProvider;
  }

  public static ActivityProvider getActivityProvider() {
    return activityProvider;
  }

  public static DisplayableWidgetMapping getDisplayableWidgetMapping() {
    return displayableWidgetMapping;
  }

  public static boolean isAutoUpdateWasCalled() {
    return autoUpdateWasCalled;
  }

  public static void setAutoUpdateWasCalled(boolean autoUpdateWasCalled) {
    AptoideApplication.autoUpdateWasCalled = autoUpdateWasCalled;
  }

  public static ShareApps getShareApps() {
    return shareApps;
  }

  public LeakTool getLeakTool() {
    if (leakTool == null) {
      leakTool = new LeakTool();
    }
    return leakTool;
  }

  @Override public void onCreate() {

    getApplicationComponent().inject(this);

    CrashReport.getInstance()
        .addLogger(new CrashlyticsCrashLogger(crashlytics))
        .addLogger(new ConsoleLogger());

    Logger.setDBG(ToolboxManager.isDebug(defaultSharedPreferences) || BuildConfig.DEBUG);

    try {
      PRNGFixes.apply();
    } catch (Exception e) {
      CrashReport.getInstance()
          .log(e);
    }

    //
    // call super
    //
    super.onCreate();

    //
    // execute custom Application onCreate code with time metric
    //

    long initialTimestamp = System.currentTimeMillis();

    getLeakTool().setup(this);

    //
    // hack to set the debug flag active in case of Debug
    //

    fragmentProvider = createFragmentProvider();
    activityProvider = createActivityProvider();
    displayableWidgetMapping = createDisplayableWidgetMapping();
    shareApps = new ShareApps(new SpotAndShareAnalytics(Analytics.getInstance()));

    //
    // do not erase this code. it is useful to figure out when someone forgot to attach an error handler when subscribing and the app
    // is crashing in Rx without a proper stack trace
    //
    //if (BuildConfig.DEBUG) {
    //  RxJavaPlugins.getInstance().registerObservableExecutionHook(new RxJavaStackTracer());
    //}

    //
    // async app initialization
    // beware! this code could be executed at the same time the first activity is
    // visible
    //
    /**
     * There's not test at the moment
     * TODO change this class in order to accept that there's no test
     * AN-1838
     */
    checkAppSecurity().andThen(generateAptoideUuid())
        .observeOn(Schedulers.computation())
        .andThen(prepareApp(AptoideApplication.this.getAccountManager()).onErrorComplete(err -> {
          // in case we have an error preparing the app, log that error and continue
          CrashReport.getInstance()
              .log(err);
          return true;
        }))
        .andThen(discoverAndSaveInstalledApps())
        .subscribe(() -> { /* do nothing */}, error -> CrashReport.getInstance()
            .log(error));

    //
    // app synchronous initialization
    //

    sendAppStartToAnalytics().doOnCompleted(
        () -> SecurePreferences.setFirstRun(false, secureSharedPreferences))
        .subscribe(() -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable));

    initializeFlurry(this, BuildConfig.FLURRY_KEY);

    clearFileCache();

    //
    // this will trigger the migration if needed
    //

    SQLiteDatabaseHelper dbHelper = new SQLiteDatabaseHelper(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    if (db.isOpen()) {
      db.close();
    }

    startNotificationCenter();
    getRootInstallationRetryHandler().start();
    AptoideApplicationAnalytics aptoideApplicationAnalytics = new AptoideApplicationAnalytics();
    accountManager.accountStatus()
        .map(account -> account.isLoggedIn())
        .distinctUntilChanged()
        .subscribe(isLoggedIn -> aptoideApplicationAnalytics.updateDimension(isLoggedIn));

    long totalExecutionTime = System.currentTimeMillis() - initialTimestamp;
    Logger.v(TAG, String.format("onCreate took %d millis.", totalExecutionTime));
  }

  public ApplicationComponent getApplicationComponent() {
    if (applicationComponent == null) {
      applicationComponent = DaggerApplicationComponent.builder()
          .applicationModule(
              new ApplicationModule(this, getImageCachePath(), getCachePath(), getAccountType(),
                  getPartnerId(), getMarketName(), getExtraId(), getAptoidePackage(),
                  getAptoideMd5sum(), getLoginPreferences()))
          .build();
    }
    return applicationComponent;
  }

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  public TokenInvalidator getTokenInvalidator() {
    if (tokenInvalidator == null) {
      tokenInvalidator =
          new RefreshTokenInvalidator(getNoAuthenticationBodyInterceptorV3(), getDefaultClient(),
              WebService.getDefaultConverter(), defaultSharedPreferences, getExtraId(),
              new NoOpTokenInvalidator(), authenticationPersistence);
    }
    return tokenInvalidator;
  }

  public BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> getNoAuthenticationBodyInterceptorV3() {
    if (noAuthorizationBodyInterceptorV3 == null) {
      noAuthorizationBodyInterceptorV3 =
          new NoAuthenticationBodyInterceptorV3(idsRepository, getAptoideMd5sum(),
              getAptoidePackage());
    }
    return noAuthorizationBodyInterceptorV3;
  }

  private void startNotificationCenter() {
    getPreferences().getBoolean(CAMPAIGN_SOCIAL_NOTIFICATIONS_PREFERENCE_VIEW_KEY, true)
        .first()
        .subscribe(enabled -> getNotificationSyncScheduler().setEnabled(enabled),
            throwable -> CrashReport.getInstance()
                .log(throwable));

    getNotificationCenter().setup();
  }

  public abstract String getCachePath();

  public abstract boolean hasMultiStoreSearch();

  public abstract String getDefaultStoreName();

  public abstract String getMarketName();

  public abstract String getFeedbackEmail();

  public abstract String getImageCachePath();

  public abstract String getAccountType();

  public abstract String getAutoUpdateUrl();

  public abstract String getPartnerId();

  public abstract String getExtraId();

  public abstract String getDefaultThemeName();

  public abstract boolean isCreateStoreUserPrivacyEnabled();

  public RootInstallationRetryHandler getRootInstallationRetryHandler() {
    if (rootInstallationRetryHandler == null) {

      Intent retryActionIntent = new Intent(this, RootInstallNotificationEventReceiver.class);
      retryActionIntent.setAction(RootInstallNotificationEventReceiver.ROOT_INSTALL_RETRY_ACTION);
      PendingIntent retryPendingIntent =
          PendingIntent.getBroadcast(this, 2, retryActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      NotificationCompat.Action action =
          new NotificationCompat.Action(R.drawable.ic_refresh_black_24dp,
              getString(R.string.generalscreen_short_root_install_timeout_error_action),
              retryPendingIntent);

      PendingIntent deleteAction = PendingIntent.getBroadcast(this, 3, retryActionIntent.setAction(
          RootInstallNotificationEventReceiver.ROOT_INSTALL_DISMISS_ACTION),
          PendingIntent.FLAG_UPDATE_CURRENT);

      final SystemNotificationShower systemNotificationShower = new SystemNotificationShower(this,
          (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE),
          new NotificationIdsMapper());
      int notificationId = 230498;
      rootInstallationRetryHandler =
          new RootInstallationRetryHandler(notificationId, systemNotificationShower,
              getRollbackInstallManager(), PublishRelay.create(), 0, this,
              new RootInstallErrorNotificationFactory(notificationId,
                  BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), action,
                  deleteAction));
    }
    return rootInstallationRetryHandler;
  }

  public NotificationCenter getNotificationCenter() {
    if (notificationCenter == null) {

      final SystemNotificationShower systemNotificationShower = new SystemNotificationShower(this,
          (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE),
          new NotificationIdsMapper());

      final NotificationAccessor notificationAccessor = AccessorFactory.getAccessorFor(
          ((AptoideApplication) this.getApplicationContext()).database, Notification.class);

      final NotificationProvider notificationProvider = getNotificationProvider();

      notificationCenter =
          new NotificationCenter(notificationProvider, getNotificationSyncScheduler(),
              systemNotificationShower, CrashReport.getInstance(),
              new NotificationPolicyFactory(notificationProvider),
              new NotificationsCleaner(notificationAccessor,
                  Calendar.getInstance(TimeZone.getTimeZone("UTC"))), getAccountManager());
    }
    return notificationCenter;
  }

  public NotificationProvider getNotificationProvider() {
    if (notificationProvider == null) {
      notificationProvider = new NotificationProvider(AccessorFactory.getAccessorFor(
          ((AptoideApplication) this.getApplicationContext()).database, Notification.class),
          Schedulers.io());
    }
    return notificationProvider;
  }

  public StoreManager getStoreManager() {
    if (storeManager == null) {
      storeManager =
          new StoreManager(accountManager, getDefaultClient(), WebService.getDefaultConverter(),
              getMultipartBodyInterceptor(), getBodyInterceptorV3(),
              getAccountSettingsBodyInterceptorPoolV7(), defaultSharedPreferences,
              getTokenInvalidator(), getRequestBodyFactory(), getNonNullObjectMapper());
    }
    return storeManager;
  }

  public NotificationSyncScheduler getNotificationSyncScheduler() {
    if (notificationSyncScheduler == null) {
      notificationSyncScheduler = new NotificationSyncManager(getSyncScheduler(), true,
          new NotificationSyncFactory(defaultSharedPreferences, getPnpV1NotificationService(),
              getNotificationProvider()));
    }
    return notificationSyncScheduler;
  }

  public SyncScheduler getSyncScheduler() {
    if (syncScheduler == null) {
      syncScheduler =
          new SyncScheduler(this, SyncService.class, (AlarmManager) getSystemService(ALARM_SERVICE),
              getSyncStorage());
    }
    return syncScheduler;
  }

  public SharedPreferences getDefaultSharedPreferences() {
    return defaultSharedPreferences;
  }

  public GroupNameProvider getGroupNameProvider() {
    return new AccountGroupNameProvider(getAccountManager(), Build.MANUFACTURER, Build.MODEL,
        Build.ID);
  }

  public NotificationService getPnpV1NotificationService() {
    if (pnpV1NotificationService == null) {
      pnpV1NotificationService =
          new PnpV1NotificationService(BuildConfig.APPLICATION_ID, getDefaultClient(),
              WebService.getDefaultConverter(), idsRepository, BuildConfig.VERSION_NAME,
              getExtraId(), defaultSharedPreferences, getResources(), authenticationPersistence,
              getAccountManager());
    }
    return pnpV1NotificationService;
  }

  public OkHttpClient getLongTimeoutClient() {
    if (longTimeoutClient == null) {
      final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
      okHttpClientBuilder.addInterceptor(getUserAgentInterceptor());
      okHttpClientBuilder.addInterceptor(retrofitLogInterceptor);
      okHttpClientBuilder.connectTimeout(2, TimeUnit.MINUTES);
      okHttpClientBuilder.readTimeout(2, TimeUnit.MINUTES);
      okHttpClientBuilder.writeTimeout(2, TimeUnit.MINUTES);

      if (ToolboxManager.isToolboxEnableRetrofitLogs(defaultSharedPreferences)) {
        okHttpClientBuilder.addInterceptor(retrofitLogInterceptor);
      }

      longTimeoutClient = okHttpClientBuilder.build();
    }
    return longTimeoutClient;
  }

  public OkHttpClient getDefaultClient() {
    if (defaultClient == null) {
      final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
      okHttpClientBuilder.readTimeout(45, TimeUnit.SECONDS);
      okHttpClientBuilder.writeTimeout(45, TimeUnit.SECONDS);

      final File cacheDirectory = new File("/");
      final int cacheMaxSize = 10 * 1024 * 1024;
      okHttpClientBuilder.cache(new Cache(cacheDirectory, cacheMaxSize)); // 10 MiB

      okHttpClientBuilder.addInterceptor(new POSTCacheInterceptor(getHttpClientCache()));
      okHttpClientBuilder.addInterceptor(getUserAgentInterceptor());

      if (ToolboxManager.isToolboxEnableRetrofitLogs(defaultSharedPreferences)) {
        okHttpClientBuilder.addInterceptor(retrofitLogInterceptor);
      }

      defaultClient = okHttpClientBuilder.build();
    }
    return defaultClient;
  }

  public Interceptor getUserAgentInterceptor() {
    return userAgentInterceptor;
  }

  public L2Cache getHttpClientCache() {
    if (httpClientCache == null) {
      httpClientCache =
          new L2Cache(new POSTCacheKeyAlgorithm(), new File(getCacheDir(), CACHE_FILE_NAME));
    }
    return httpClientCache;
  }

  public AptoideDownloadManager getDownloadManager() {
    return downloadManager;
  }

  public InstallManager getRollbackInstallManager() {
    return rollbackInstallManager;
  }

  public QManager getQManager() {
    if (qManager == null) {
      qManager = new QManager(defaultSharedPreferences, getResources(),
          ((ActivityManager) getSystemService(ACTIVITY_SERVICE)),
          ((WindowManager) getSystemService(WINDOW_SERVICE)));
    }
    return qManager;
  }

  public EntryPointChooser getEntryPointChooser() {
    if (entryPointChooser == null) {
      entryPointChooser = new EntryPointChooser(() -> getQManager().isSupportedExtensionsDefined());
    }
    return entryPointChooser;
  }

  public AptoideAccountManager getAccountManager() {
    if (accountManager == null) {

      FacebookSdk.sdkInitialize(this);

      final AccountFactory accountFactory = new AccountFactory();

      final AccountService accountService =
          new AccountServiceV3(accountFactory, getDefaultClient(), getLongTimeoutClient(),
              WebService.getDefaultConverter(), getNonNullObjectMapper(), defaultSharedPreferences,
              getExtraId(), getTokenInvalidator(), authenticationPersistence,
              getNoAuthenticationBodyInterceptorV3(), getMultipartBodyInterceptor(),
              getBodyInterceptorWebV7(), getBodyInterceptorPoolV7());

      final AndroidAccountDataMigration accountDataMigration = new AndroidAccountDataMigration(
          SecurePreferencesImplementation.getInstance(this, defaultSharedPreferences),
          defaultSharedPreferences, androidAccountManager,
          new SecureCoderDecoder.Builder(this, defaultSharedPreferences).create(),
          SQLiteDatabaseHelper.DATABASE_VERSION,
          getDatabasePath(SQLiteDatabaseHelper.DATABASE_NAME).getPath(), getAccountType(),
          BuildConfig.VERSION_NAME, Schedulers.io());

      final AccountPersistence accountPersistence =
          new AndroidAccountManagerPersistence(androidAccountManager, new DatabaseStoreDataPersist(
              AccessorFactory.getAccessorFor(
                  ((AptoideApplication) this.getApplicationContext()).database, Store.class),
              new DatabaseStoreDataPersist.DatabaseStoreMapper()), accountFactory,
              accountDataMigration, androidAccountProvider, authenticationPersistence,
              Schedulers.io());
    }
    return accountManager;
  }

  public AuthenticationPersistence getAuthenticationPersistence() {
    return authenticationPersistence;
  }

  public Preferences getPreferences() {
    if (preferences == null) {
    }
    return preferences;
  }

  public cm.aptoide.pt.preferences.SecurePreferences getSecurePreferences() {
    if (securePreferences == null) {
      securePreferences = new cm.aptoide.pt.preferences.SecurePreferences(defaultSharedPreferences,
          getSecureCoderDecoder());
    }
    return securePreferences;
  }

  public GoogleApiClient getGoogleSignInClient() {
    if (googleSignInClient == null) {
    }
    return googleSignInClient;
  }

  public SecureCoderDecoder getSecureCoderDecoder() {
    if (secureCodeDecoder == null) {
      secureCodeDecoder = new SecureCoderDecoder.Builder(this, defaultSharedPreferences).create();
    }
    return secureCodeDecoder;
  }

  public BillingAnalytics getBillingAnalytics() {
    if (billingAnalytics == null) {
      billingAnalytics =
          new BillingAnalytics(Analytics.getInstance(), appEventsLogger, getAptoidePackage());
    }
    return billingAnalytics;
  }

  public Billing getBilling(String merchantName) {
    return getBillingPool().get(merchantName);
  }

  public BillingPool getBillingPool() {
    if (billingPool == null) {
      billingPool =
          new BillingPool(defaultSharedPreferences, getBodyInterceptorV3(), getDefaultClient(),
              getAccountManager(), database, getResources(), getPackageRepository(),
              getTokenInvalidator(), getSyncScheduler(), getInAppBillingSerializer(),
              getBodyInterceptorPoolV7(), getAccountSettingsBodyInterceptorPoolV7(),
              new HashMap<>(), WebService.getDefaultConverter(), CrashReport.getInstance(),
              getAdyen(), getPurchaseFactory(), Build.VERSION_CODES.JELLY_BEAN,
              Build.VERSION_CODES.JELLY_BEAN);
    }
    return billingPool;
  }

  public Adyen getAdyen() {
    if (adyen == null) {
      adyen = new Adyen(this, Charset.forName("UTF-8"), Schedulers.io(), PublishRelay.create());
    }
    return adyen;
  }

  public BillingIdManager getIdResolver(String merchantName) {
    return getBillingPool().getIdResolver(merchantName);
  }

  public Database getDatabase() {
    return database;
  }

  public PackageRepository getPackageRepository() {
    if (packageRepository == null) {
      packageRepository = new PackageRepository(getPackageManager());
    }
    return packageRepository;
  }

  public PaymentThrowableCodeMapper getPaymentThrowableCodeMapper() {
    if (paymentThrowableCodeMapper == null) {
      paymentThrowableCodeMapper = new PaymentThrowableCodeMapper();
    }
    return paymentThrowableCodeMapper;
  }

  public PurchaseBundleMapper getPurchaseBundleMapper() {
    if (purchaseBundleMapper == null) {
      purchaseBundleMapper =
          new PurchaseBundleMapper(getPaymentThrowableCodeMapper(), getPurchaseFactory());
    }
    return purchaseBundleMapper;
  }

  public ExternalBillingSerializer getInAppBillingSerializer() {
    if (inAppBillingSerialzer == null) {
      inAppBillingSerialzer = new ExternalBillingSerializer();
    }
    return inAppBillingSerialzer;
  }

  public NetworkOperatorManager getNetworkOperatorManager() {
    return new NetworkOperatorManager(
        (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
  }

  private void clearFileCache() {
    getFileManager().purgeCache()
        .first()
        .toSingle()
        .subscribe(cleanedSize -> Logger.d(TAG,
            "cleaned size: " + AptoideUtils.StringU.formatBytes(cleanedSize, false)),
            err -> CrashReport.getInstance()
                .log(err));
  }

  public FileManager getFileManager() {
    if (fileManager == null) {
      fileManager = new FileManager(cacheHelper, new FileUtils(), new String[] {
          getApplicationContext().getCacheDir().getPath(), getCachePath()
      }, downloadManager, getHttpClientCache());
    }
    return fileManager;
  }

  private void initializeFlurry(Context context, String flurryKey) {
    new FlurryAgent.Builder().withLogEnabled(false)
        .build(context, flurryKey);
  }

  private Completable sendAppStartToAnalytics() {
    return Analytics.Lifecycle.Application.onCreate(this, WebService.getDefaultConverter(),
        getDefaultClient(), getAccountSettingsBodyInterceptorPoolV7(),
        SecurePreferencesImplementation.getInstance(getApplicationContext(),
            defaultSharedPreferences), getTokenInvalidator());
  }

  private Completable checkAppSecurity() {
    return Completable.fromAction(() -> {
      if (SecurityUtils.checkAppSignature(this) != SecurityUtils.VALID_APP_SIGNATURE) {
        Logger.w(TAG, "app signature is not valid!");
      }

      if (SecurityUtils.checkEmulator()) {
        Logger.w(TAG, "application is running on an emulator");
      }

      if (SecurityUtils.checkDebuggable(this)) {
        Logger.w(TAG, "application has debug flag active");
      }
    });
  }

  protected DisplayableWidgetMapping createDisplayableWidgetMapping() {
    return DisplayableWidgetMapping.getInstance();
  }

  private Completable generateAptoideUuid() {
    return Completable.fromAction(() -> idsRepository.getUniqueIdentifier())
        .subscribeOn(Schedulers.newThread());
  }

  private Completable prepareApp(AptoideAccountManager accountManager) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMapCompletable(account -> {
          if (SecurePreferences.isFirstRun(secureSharedPreferences)) {

            PreferenceManager.setDefaultValues(this, R.xml.settings, false);

            return setupFirstRun().andThen(rootAvailabilityManager.updateRootAvailability())
                .andThen(Completable.merge(accountManager.updateAccount(), createShortcut()));
          }

          return Completable.complete();
        });
  }

  // todo re-factor all this code to proper Rx
  private Completable setupFirstRun() {
    return Completable.defer(() -> {

      final StoreCredentialsProviderImpl storeCredentials = new StoreCredentialsProviderImpl(
          AccessorFactory.getAccessorFor(
              ((AptoideApplication) this.getApplicationContext()).database, Store.class));

      StoreUtilsProxy proxy =
          new StoreUtilsProxy(getAccountManager(), getAccountSettingsBodyInterceptorPoolV7(),
              storeCredentials, AccessorFactory.getAccessorFor(
              ((AptoideApplication) this.getApplicationContext()).database, Store.class),
              getDefaultClient(), WebService.getDefaultConverter(), getTokenInvalidator(),
              defaultSharedPreferences);

      BaseRequestWithStore.StoreCredentials defaultStoreCredentials =
          storeCredentials.get(getDefaultStoreName());

      return generateAptoideUuid().andThen(proxy.addDefaultStore(
          GetStoreMetaRequest.of(defaultStoreCredentials, getAccountSettingsBodyInterceptorPoolV7(),
              getDefaultClient(), WebService.getDefaultConverter(), getTokenInvalidator(),
              defaultSharedPreferences), getAccountManager(), defaultStoreCredentials)
          .andThen(refreshUpdates()))
          .doOnError(err -> CrashReport.getInstance()
              .log(err));
    });
  }

  /**
   * BaseBodyInterceptor for v7 ws calls with CDN = pool configuration
   */
  public BodyInterceptor<BaseBody> getBodyInterceptorPoolV7() {
    if (bodyInterceptorPoolV7 == null) {
    }
    return bodyInterceptorPoolV7;
  }

  /**
   * BaseBodyInterceptor for v7 ws calls with CDN = web configuration
   */
  public BodyInterceptor<BaseBody> getBodyInterceptorWebV7() {
    if (bodyInterceptorWebV7 == null) {
      bodyInterceptorWebV7 =
          new BodyInterceptorV7(idsRepository, authenticationPersistence, getAptoideMd5sum(),
              getAptoidePackage(), getQManager(), Cdn.WEB, defaultSharedPreferences, getResources(),
              BuildConfig.VERSION_CODE);
    }
    return bodyInterceptorWebV7;
  }

  public BodyInterceptor<BaseBody> getAccountSettingsBodyInterceptorPoolV7() {
    if (accountSettingsBodyInterceptorPoolV7 == null) {
      accountSettingsBodyInterceptorPoolV7 =
          new AccountSettingsBodyInterceptorV7(getBodyInterceptorPoolV7(), getLocalAdultContent());
    }
    return accountSettingsBodyInterceptorPoolV7;
  }

  public BodyInterceptor<BaseBody> getAccountSettingsBodyInterceptorWebV7() {
    if (accountSettingsBodyInterceptorWebV7 == null) {
      accountSettingsBodyInterceptorWebV7 =
          new AccountSettingsBodyInterceptorV7(getBodyInterceptorWebV7(), getLocalAdultContent());
    }
    return accountSettingsBodyInterceptorWebV7;
  }

  public BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> getBodyInterceptorV3() {
    if (bodyInterceptorV3 == null) {
      bodyInterceptorV3 =
          new BodyInterceptorV3(idsRepository, getAptoideMd5sum(), getAptoidePackage(),
              getQManager(), defaultSharedPreferences, BodyInterceptorV3.RESPONSE_MODE_JSON,
              Build.VERSION.SDK_INT, getNetworkOperatorManager(), authenticationPersistence);
    }
    return bodyInterceptorV3;
  }

  public BodyInterceptor<HashMapNotNull<String, RequestBody>> getMultipartBodyInterceptor() {
    if (multipartBodyInterceptor == null) {
      multipartBodyInterceptor =
          new MultipartBodyInterceptor(idsRepository, getRequestBodyFactory(),
              authenticationPersistence);
    }
    return multipartBodyInterceptor;
  }

  public RequestBodyFactory getRequestBodyFactory() {
    if (requestBodyFactory == null) {
      requestBodyFactory = new RequestBodyFactory();
    }
    return requestBodyFactory;
  }

  public ObjectMapper getNonNullObjectMapper() {
    if (nonNullObjectMapper == null) {
      nonNullObjectMapper = new ObjectMapper();
      nonNullObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    return nonNullObjectMapper;
  }

  public String getAptoideMd5sum() {
    if (aptoideMd5sum == null) {
      synchronized (this) {
        if (aptoideMd5sum == null) {
          aptoideMd5sum = calculateMd5Sum();
        }
      }
    }
    return aptoideMd5sum;
  }

  private String calculateMd5Sum() {
    try {
      return AptoideUtils.AlgorithmU.computeMd5(
          getPackageManager().getPackageInfo(getAptoidePackage(), 0));
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String getAptoidePackage() {
    return BuildConfig.APPLICATION_ID;
  }

  public AdultContent getAdultContent() {
    return new RemotePersistenceAdultContent(getLocalAdultContent(), getAccountManager());
  }

  private AdultContent getLocalAdultContent() {
    if (adultContent == null) {
      adultContent = new LocalPersistenceAdultContent(getPreferences(), getSecurePreferences());
    }
    return adultContent;
  }

  public Completable createShortcut() {
    return Completable.defer(() -> {
      createAppShortcut();
      return null;
    });
  }

  private Completable discoverAndSaveInstalledApps() {
    return Observable.fromCallable(() -> {
      // remove the current installed apps
      //AccessorFactory.getAccessorFor(Installed.class).removeAll();

      // get the installed apps
      List<PackageInfo> installedApps =
          AptoideUtils.SystemU.getAllInstalledApps(getPackageManager());
      Logger.v(TAG, "Found " + installedApps.size() + " user installed apps.");

      // Installed apps are inserted in database based on their firstInstallTime. Older comes first.
      Collections.sort(installedApps,
          (lhs, rhs) -> (int) ((lhs.firstInstallTime - rhs.firstInstallTime) / 1000));

      // return sorted installed apps
      return installedApps;
    })  // transform installation package into Installed table entry and save all the data
        .flatMapIterable(list -> list)
        .map(packageInfo -> new Installed(packageInfo, getPackageManager()))
        .toList()
        .flatMap(appsInstalled -> installedAccessor.getAll()
            .first()
            .map(installedFromDatabase -> combineLists(appsInstalled, installedFromDatabase,
                installed -> installed.setStatus(Installed.STATUS_UNINSTALLED))))
        .doOnNext(list -> {
          installedAccessor.removeAll();
          installedAccessor.insertAll(list);
        })
        .toCompletable();
  }

  public <T> List<T> combineLists(List<T> list1, List<T> list2, @Nullable Action1<T> transformer) {
    List<T> toReturn = new ArrayList<>(list1.size() + list2.size());
    toReturn.addAll(list1);
    for (T item : list2) {
      if (!toReturn.contains(item)) {
        if (transformer != null) {
          transformer.call(item);
        }
        toReturn.add(item);
      }
    }

    return toReturn;
  }

  private Completable refreshUpdates() {
    return RepositoryFactory.getUpdateRepository(this, defaultSharedPreferences)
        .sync(true);
  }

  private void createAppShortcut() {
    Intent shortcutIntent = new Intent(this, EntryActivity.class);
    shortcutIntent.setAction(Intent.ACTION_MAIN);
    Intent intent = new Intent();
    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
        Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
    intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
    getApplicationContext().sendBroadcast(intent);
  }

  public RootAvailabilityManager getRootAvailabilityManager() {
    return rootAvailabilityManager;
  }

  public AdsApplicationVersionCodeProvider getVersionCodeProvider() {
    if (applicationVersionCodeProvider == null) {
      applicationVersionCodeProvider =
          new PackageRepositoryVersionCodeProvider(getPackageRepository(), getPackageName());
    }
    return applicationVersionCodeProvider;
  }

  public AdsRepository getAdsRepository() {
    if (adsRepository == null) {
      adsRepository = new AdsRepository(idsRepository, accountManager, getDefaultClient(),
          WebService.getDefaultConverter(), qManager, defaultSharedPreferences,
          getApplicationContext(),
          (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE), getResources(),
          getVersionCodeProvider(), AdNetworkUtils::isGooglePlayServicesAvailable,
          this::getPartnerId, new MinimalAdMapper());
    }
    return adsRepository;
  }

  public SyncStorage getSyncStorage() {
    if (syncStorage == null) {
      syncStorage = new SyncStorage(new HashMap<>());
    }
    return syncStorage;
  }

  public TimelinePostsRepository getTimelineRepository(String action, Context context) {
    if (timelineRepositoryFactory == null) {
      timelineRepositoryFactory =
          new TimelineRepositoryFactory(new HashMap<>(), getAccountSettingsBodyInterceptorPoolV7(),
              getDefaultClient(), defaultSharedPreferences, getTokenInvalidator(),
              new LinksHandlerFactory(this), getPackageRepository(),
              WebService.getDefaultConverter(),
              new TimelineResponseCardMapper(accountManager, getMarketName()),
              RepositoryFactory.getUpdateRepository(context,
                  ((AptoideApplication) context.getApplicationContext()).defaultSharedPreferences));
    }
    return timelineRepositoryFactory.create(action);
  }

  public PageViewsAnalytics getPageViewsAnalytics() {
    if (pageViewsAnalytics == null) {
      pageViewsAnalytics =
          new PageViewsAnalytics(appEventsLogger, Analytics.getInstance(), getNavigationTracker());
    }
    return pageViewsAnalytics;
  }

  public BehaviorRelay<Map<Integer, Result>> getFragmentResultRelay() {
    if (fragmentResultRelay == null) {
      fragmentResultRelay = BehaviorRelay.create();
    }
    return fragmentResultRelay;
  }

  public CallbackManager getFacebookCallbackManager() {
    if (facebookCallbackManager == null) {
    }
    return facebookCallbackManager;
  }

  public Map<Integer, Result> getFragmentResulMap() {
    if (fragmentResulMap == null) {
      fragmentResulMap = new HashMap<>();
    }
    return fragmentResulMap;
  }

  public PublishRelay<FacebookLoginResult> getFacebookLoginResultRelay() {
    if (facebookLoginResultRelay == null) {
      facebookLoginResultRelay = PublishRelay.create();
    }
    return facebookLoginResultRelay;
  }

  public NavigationTracker getNavigationTracker() {
    if (navigationTracker == null) {
      navigationTracker = new NavigationTracker(new ArrayList<>(), new TrackerFilter());
    }
    return navigationTracker;
  }

  public abstract LoginPreferences getLoginPreferences();

  public abstract FragmentProvider createFragmentProvider();

  public abstract ActivityProvider createActivityProvider();

  public NotLoggedInShareAnalytics getNotLoggedInShareAnalytics() {
    if (notLoggedInShareAnalytics == null) {
      notLoggedInShareAnalytics =
          new NotLoggedInShareAnalytics(getAccountAnalytics(), appEventsLogger,
              Analytics.getInstance());
    }
    return notLoggedInShareAnalytics;
  }

  public AccountAnalytics getAccountAnalytics() {
    if (accountAnalytics == null) {
    }
    return accountAnalytics;
  }

  public AppCenter getAppCenter() {
    if (appCenter == null) {
      appCenter = new AppCenter(new AppCenterRepository(new AppService(
          new StoreCredentialsProviderImpl(
              AccessorFactory.getAccessorFor(getDatabase(), Store.class)),
          getBodyInterceptorPoolV7(), getDefaultClient(), WebService.getDefaultConverter(),
          getTokenInvalidator(), getDefaultSharedPreferences()), new HashMap<>()));
    }
    return appCenter;
  }

  public PurchaseFactory getPurchaseFactory() {
    if (purchaseFactory == null) {
      purchaseFactory = new PurchaseFactory();
    }
    return purchaseFactory;
  }

  public IdsRepository getIdsRepository() {
    return idsRepository;
  }
}


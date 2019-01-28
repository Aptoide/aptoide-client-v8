package cm.aptoide.pt;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import cm.aptoide.accountmanager.AdultContent;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.account.AccountSettingsBodyInterceptorV7;
import cm.aptoide.pt.account.AdultContentAnalytics;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.analytics.FirstLaunchAnalytics;
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
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Notification;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.cache.L2Cache;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.AdsApplicationVersionCodeProvider;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.PostReadRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.deprecated.SQLiteDatabaseHelper;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.file.CacheHelper;
import cm.aptoide.pt.file.FileManager;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.leak.LeakTool;
import cm.aptoide.pt.link.AptoideInstallParser;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.NotificationCenter;
import cm.aptoide.pt.notification.NotificationInfo;
import cm.aptoide.pt.notification.NotificationPolicyFactory;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.notification.NotificationsCleaner;
import cm.aptoide.pt.notification.SystemNotificationShower;
import cm.aptoide.pt.preferences.PRNGFixes;
import cm.aptoide.pt.preferences.Preferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.search.suggestions.SearchSuggestionManager;
import cm.aptoide.pt.search.suggestions.TrendingManager;
import cm.aptoide.pt.social.data.ReadPostsPersistence;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.sync.SyncScheduler;
import cm.aptoide.pt.sync.alarm.SyncStorage;
import cm.aptoide.pt.sync.rx.RxSyncScheduler;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.util.PreferencesXmlParser;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.SecurityUtils;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.view.ActivityModule;
import cm.aptoide.pt.view.ActivityProvider;
import cm.aptoide.pt.view.BaseActivity;
import cm.aptoide.pt.view.BaseFragment;
import cm.aptoide.pt.view.FragmentModule;
import cm.aptoide.pt.view.FragmentProvider;
import cm.aptoide.pt.view.entry.EntryActivity;
import cm.aptoide.pt.view.entry.EntryPointChooser;
import cm.aptoide.pt.view.recycler.DisplayableWidgetMapping;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import java.io.IOException;
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
import okhttp3.OkHttpClient;
import org.xmlpull.v1.XmlPullParserException;
import rx.Completable;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.preferences.managed.ManagedKeys.CAMPAIGN_SOCIAL_NOTIFICATIONS_PREFERENCE_VIEW_KEY;

public abstract class AptoideApplication extends Application {

  static final String CACHE_FILE_NAME = "aptoide.wscache";
  private static final String TAG = AptoideApplication.class.getName();

  private static FragmentProvider fragmentProvider;
  private static ActivityProvider activityProvider;
  private static DisplayableWidgetMapping displayableWidgetMapping;
  private static boolean autoUpdateWasCalled = false;
  @Inject Database database;
  @Inject AptoideDownloadManager aptoideDownloadManager;
  @Inject CacheHelper cacheHelper;
  @Inject AptoideAccountManager accountManager;
  @Inject Preferences preferences;
  @Inject @Named("secure") cm.aptoide.pt.preferences.SecurePreferences securePreferences;
  @Inject AdultContent adultContent;
  @Inject IdsRepository idsRepository;
  @Inject @Named("default") OkHttpClient defaultClient;
  @Inject RootAvailabilityManager rootAvailabilityManager;
  @Inject AuthenticationPersistence authenticationPersistence;
  @Inject Crashlytics crashlytics;
  @Inject SyncScheduler alarmSyncScheduler;
  @Inject @Named("pool-v7") BodyInterceptor<BaseBody> bodyInterceptorPoolV7;
  @Inject @Named("web-v7") BodyInterceptor<BaseBody> bodyInterceptorWebV7;
  @Inject @Named("defaultInterceptorV3") BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody>
      bodyInterceptorV3;
  @Inject L2Cache httpClientCache;
  @Inject QManager qManager;
  @Inject TokenInvalidator tokenInvalidator;
  @Inject PackageRepository packageRepository;
  @Inject AdsApplicationVersionCodeProvider applicationVersionCodeProvider;
  @Inject AdsRepository adsRepository;
  @Inject SyncStorage syncStorage;
  @Inject NavigationTracker navigationTracker;
  @Inject @Named("account-settings-pool-v7") BodyInterceptor<BaseBody>
      accountSettingsBodyInterceptorPoolV7;
  @Inject TrendingManager trendingManager;
  @Inject AdultContentAnalytics adultContentAnalytics;
  @Inject NotificationAnalytics notificationAnalytics;
  @Inject SearchSuggestionManager searchSuggestionManager;
  @Inject AnalyticsManager analyticsManager;
  @Inject FirstLaunchAnalytics firstLaunchAnalytics;
  @Inject InvalidRefreshTokenLogoutManager invalidRefreshTokenLogoutManager;
  @Inject RootInstallationRetryHandler rootInstallationRetryHandler;
  @Inject AptoideShortcutManager shortcutManager;
  @Inject SettingsManager settingsManager;
  @Inject InstallManager installManager;
  @Inject @Named("default-followed-stores") List<String> defaultFollowedStores;
  private LeakTool leakTool;
  private String aptoideMd5sum;
  private BillingAnalytics billingAnalytics;
  private ExternalBillingSerializer inAppBillingSerialzer;
  private PurchaseBundleMapper purchaseBundleMapper;
  private PaymentThrowableCodeMapper paymentThrowableCodeMapper;
  private NotificationCenter notificationCenter;
  private EntryPointChooser entryPointChooser;
  private FileManager fileManager;
  private NotificationProvider notificationProvider;
  private BehaviorRelay<Map<Integer, Result>> fragmentResultRelay;
  private Map<Integer, Result> fragmentResulMap;
  private BillingPool billingPool;
  private BodyInterceptor<BaseBody> accountSettingsBodyInterceptorWebV7;
  private Adyen adyen;
  private PurchaseFactory purchaseFactory;
  private ApplicationComponent applicationComponent;
  private ReadPostsPersistence readPostsPersistence;
  private PublishRelay<NotificationInfo> notificationsPublishRelay;
  private NotificationsCleaner notificationsCleaner;
  private TimelineAnalytics timelineAnalytics;

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
    Logger.setDBG(ToolboxManager.isDebug(getDefaultSharedPreferences()) || BuildConfig.DEBUG);

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

    sendAppStartToAnalytics().doOnCompleted(() -> SecurePreferences.setFirstRun(false,
        SecurePreferencesImplementation.getInstance(getApplicationContext(),
            getDefaultSharedPreferences())))
        .subscribe(() -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable));

    initializeMoPub(this, BuildConfig.MOPUB_BANNER_50_HOME_PLACEMENT_ID);

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
    startNotificationCleaner();
    rootInstallationRetryHandler.start();
    AptoideApplicationAnalytics aptoideApplicationAnalytics = new AptoideApplicationAnalytics();
    aptoideApplicationAnalytics.setPackageDimension(getPackageName());
    aptoideApplicationAnalytics.setVersionCodeDimension(getVersionCode());
    accountManager.accountStatus()
        .map(account -> account.isLoggedIn())
        .distinctUntilChanged()
        .subscribe(isLoggedIn -> aptoideApplicationAnalytics.updateDimension(isLoggedIn));

    dispatchPostReadEventInterval().subscribe(() -> {
    }, throwable -> CrashReport.getInstance()
        .log(throwable));

    long totalExecutionTime = System.currentTimeMillis() - initialTimestamp;
    Logger.getInstance()
        .v(TAG, String.format("onCreate took %d millis.", totalExecutionTime));
    analyticsManager.setup();
    invalidRefreshTokenLogoutManager.start();
    aptoideDownloadManager.start();
  }

  private void initializeMoPub(Context context, String moPubKey) {
    SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(moPubKey).build();
    MoPub.initializeSdk(context, sdkConfiguration, null);
  }

  public ApplicationComponent getApplicationComponent() {
    if (applicationComponent == null) {
      applicationComponent = DaggerApplicationComponent.builder()
          .applicationModule(new ApplicationModule(this, getAptoideMd5sum()))
          .flavourApplicationModule(new FlavourApplicationModule(this))
          .build();
    }
    return applicationComponent;
  }

  /**
   * <p>Needs to be here, to be mocked for tests. Should be on BaseActivity if there were no
   * tests</p>
   *
   * @return Returns a new Activity Module for the Activity Component
   */
  public ActivityModule getActivityModule(BaseActivity activity, Intent intent,
      NotificationSyncScheduler notificationSyncScheduler, View view, boolean firstCreated,
      String fileProviderAuthority) {

    return new ActivityModule(activity, intent, notificationSyncScheduler, view, firstCreated,
        fileProviderAuthority);
  }

  /**
   * Needs to be here, to be mocked for tests. Should be on BaseFragment if there were no tests
   *
   * @return Returns a new Fragment Module for the Fragment Component
   */
  public FragmentModule getFragmentModule(BaseFragment baseFragment, Bundle savedInstanceState,
      Bundle arguments, boolean createStoreUserPrivacyEnabled, String packageName) {
    return new FragmentModule(baseFragment, savedInstanceState, arguments,
        createStoreUserPrivacyEnabled, packageName);
  }

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  public TokenInvalidator getTokenInvalidator() {
    return tokenInvalidator;
  }

  private void startNotificationCenter() {
    getPreferences().getBoolean(CAMPAIGN_SOCIAL_NOTIFICATIONS_PREFERENCE_VIEW_KEY, true)
        .first()
        .subscribe(enabled -> getNotificationSyncScheduler().setEnabled(enabled),
            throwable -> CrashReport.getInstance()
                .log(throwable));

    getNotificationCenter().setup();
  }

  private void startNotificationCleaner() {
    getNotificationCleaner().setup();
  }

  private NotificationsCleaner getNotificationCleaner() {
    if (notificationsCleaner == null) {
      notificationsCleaner =
          new NotificationsCleaner(AccessorFactory.getAccessorFor(database, Notification.class),
              Calendar.getInstance(TimeZone.getTimeZone("UTC")), accountManager,
              getNotificationProvider(), CrashReport.getInstance());
    }
    return notificationsCleaner;
  }

  public abstract String getCachePath();

  public abstract String getFeedbackEmail();

  public abstract String getAccountType();

  public abstract String getPartnerId();

  public abstract String getExtraId();

  public abstract boolean isCreateStoreUserPrivacyEnabled();

  @NonNull protected abstract SystemNotificationShower getSystemNotificationShower();

  public PublishRelay<NotificationInfo> getNotificationsPublishRelay() {
    if (notificationsPublishRelay == null) {
      notificationsPublishRelay = PublishRelay.create();
    }
    return notificationsPublishRelay;
  }

  public NotificationCenter getNotificationCenter() {
    if (notificationCenter == null) {
      final NotificationProvider notificationProvider = getNotificationProvider();
      notificationCenter =
          new NotificationCenter(notificationProvider, getNotificationSyncScheduler(),
              new NotificationPolicyFactory(notificationProvider),
              new NotificationAnalytics(new AptoideInstallParser(), analyticsManager,
                  navigationTracker));
    }
    return notificationCenter;
  }

  public NotificationProvider getNotificationProvider() {
    if (notificationProvider == null) {
      notificationProvider =
          new NotificationProvider(AccessorFactory.getAccessorFor(database, Notification.class),
              Schedulers.io());
    }
    return notificationProvider;
  }

  public abstract NotificationSyncScheduler getNotificationSyncScheduler();

  public SharedPreferences getDefaultSharedPreferences() {
    return PreferenceManager.getDefaultSharedPreferences(this);
  }

  public OkHttpClient getDefaultClient() {
    return defaultClient;
  }

  public AptoideDownloadManager getDownloadManager() {
    return aptoideDownloadManager;
  }

  public InstallManager getInstallManager() {
    return installManager;
  }

  public QManager getQManager() {
    return qManager;
  }

  public EntryPointChooser getEntryPointChooser() {
    if (entryPointChooser == null) {
      entryPointChooser = new EntryPointChooser(() -> qManager.isSupportedExtensionsDefined());
    }
    return entryPointChooser;
  }

  public AptoideAccountManager getAccountManager() {
    return accountManager;
  }

  public AuthenticationPersistence getAuthenticationPersistence() {
    return authenticationPersistence;
  }

  public Preferences getPreferences() {
    return preferences;
  }

  public BillingAnalytics getBillingAnalytics() {
    if (billingAnalytics == null) {
      billingAnalytics =
          new BillingAnalytics(getAptoidePackage(), analyticsManager, navigationTracker);
    }
    return billingAnalytics;
  }

  public Billing getBilling(String merchantName) {
    return getBillingPool().get(merchantName);
  }

  public BillingPool getBillingPool() {
    if (billingPool == null) {
      billingPool = new BillingPool(getDefaultSharedPreferences(), bodyInterceptorV3, defaultClient,
          accountManager, database, getResources(), packageRepository, tokenInvalidator,
          new RxSyncScheduler(new HashMap<>(), CrashReport.getInstance()),
          getInAppBillingSerializer(), bodyInterceptorPoolV7, accountSettingsBodyInterceptorPoolV7,
          new HashMap<>(), WebService.getDefaultConverter(), CrashReport.getInstance(), getAdyen(),
          getPurchaseFactory(), Build.VERSION_CODES.JELLY_BEAN, Build.VERSION_CODES.JELLY_BEAN,
          getAuthenticationPersistence(), getPreferences());
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

  private void clearFileCache() {
    getFileManager().purgeCache()
        .subscribe(cleanedSize -> Logger.getInstance()
                .d(TAG, "cleaned size: " + AptoideUtils.StringU.formatBytes(cleanedSize, false)),
            err -> CrashReport.getInstance()
                .log(err));
  }

  public FileManager getFileManager() {
    if (fileManager == null) {
      fileManager = new FileManager(cacheHelper, new FileUtils(), new String[] {
          getApplicationContext().getCacheDir().getPath(), getCachePath()
      }, aptoideDownloadManager, httpClientCache);
    }
    return fileManager;
  }

  private void initializeFlurry(Context context, String flurryKey) {
    new FlurryAgent.Builder().withLogEnabled(false)
        .build(context, flurryKey);
  }

  private Completable sendAppStartToAnalytics() {
    return firstLaunchAnalytics.sendAppStart(this,
        SecurePreferencesImplementation.getInstance(getApplicationContext(),
            getDefaultSharedPreferences()), WebService.getDefaultConverter(), getDefaultClient(),
        getAccountSettingsBodyInterceptorPoolV7(), getTokenInvalidator());
  }

  private Completable checkAppSecurity() {
    return Completable.fromAction(() -> {
      if (SecurityUtils.checkAppSignature(this) != SecurityUtils.VALID_APP_SIGNATURE) {
        Logger.getInstance()
            .w(TAG, "app signature is not valid!");
      }

      if (SecurityUtils.checkEmulator()) {
        Logger.getInstance()
            .w(TAG, "application is running on an emulator");
      }

      if (SecurityUtils.checkDebuggable(this)) {
        Logger.getInstance()
            .w(TAG, "application has debug flag active");
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
          if (SecurePreferences.isFirstRun(
              SecurePreferencesImplementation.getInstance(getApplicationContext(),
                  getDefaultSharedPreferences()))) {

            setSharedPreferencesValues();

            return setupFirstRun().andThen(rootAvailabilityManager.updateRootAvailability())
                .andThen(Completable.merge(accountManager.updateAccount(), createShortcut()));
          }

          return Completable.complete();
        });
  }

  private void setSharedPreferencesValues() {
    PreferencesXmlParser preferencesXmlParser = new PreferencesXmlParser();

    XmlResourceParser parser = getResources().getXml(R.xml.settings);
    try {
      List<String[]> parsedPrefsList = preferencesXmlParser.parse(parser);
      for (String[] keyValue : parsedPrefsList) {
        getDefaultSharedPreferences().edit()
            .putBoolean(keyValue[0], Boolean.valueOf(keyValue[1]))
            .apply();
      }
    } catch (IOException | XmlPullParserException e) {
      e.printStackTrace();
    }
  }

  // todo re-factor all this code to proper Rx
  private Completable setupFirstRun() {
    return Completable.defer(() -> {

      final StoreCredentialsProviderImpl storeCredentials =
          new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(database, Store.class));

      StoreUtilsProxy proxy =
          new StoreUtilsProxy(accountManager, accountSettingsBodyInterceptorPoolV7,
              storeCredentials, AccessorFactory.getAccessorFor(database, Store.class),
              defaultClient, WebService.getDefaultConverter(), tokenInvalidator,
              getDefaultSharedPreferences());

      return generateAptoideUuid().andThen(
          setDefaultFollowedStores(storeCredentials, proxy).andThen(refreshUpdates())
              .doOnError(err -> CrashReport.getInstance()
                  .log(err)));
    });
  }

  private Completable setDefaultFollowedStores(StoreCredentialsProviderImpl storeCredentials,
      StoreUtilsProxy proxy) {

    return Observable.from(defaultFollowedStores)
        .flatMapCompletable(followedStoreName -> {
          BaseRequestWithStore.StoreCredentials defaultStoreCredentials =
              storeCredentials.get(followedStoreName);

          return proxy.addDefaultStore(
              GetStoreMetaRequest.of(defaultStoreCredentials, accountSettingsBodyInterceptorPoolV7,
                  defaultClient, WebService.getDefaultConverter(), tokenInvalidator,
                  getDefaultSharedPreferences()), accountManager, defaultStoreCredentials);
        })
        .toCompletable();
  }

  /**
   * BaseBodyInterceptor for v7 ws calls with CDN = pool configuration
   */
  public BodyInterceptor<BaseBody> getBodyInterceptorPoolV7() {
    return bodyInterceptorPoolV7;
  }

  public BodyInterceptor<BaseBody> getAccountSettingsBodyInterceptorPoolV7() {
    return accountSettingsBodyInterceptorPoolV7;
  }

  public BodyInterceptor<BaseBody> getAccountSettingsBodyInterceptorWebV7() {
    if (accountSettingsBodyInterceptorWebV7 == null) {
      accountSettingsBodyInterceptorWebV7 =
          new AccountSettingsBodyInterceptorV7(bodyInterceptorWebV7, adultContent);
    }
    return accountSettingsBodyInterceptorWebV7;
  }

  public BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> getBodyInterceptorV3() {
    return bodyInterceptorV3;
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

  protected String getAptoidePackage() {
    return BuildConfig.APPLICATION_ID;
  }

  public Completable createShortcut() {
    return Completable.defer(() -> {
      if (shortcutManager.shouldCreateShortcut()) {
        createAppShortcut();
      }
      return null;
    });
  }

  private Completable discoverAndSaveInstalledApps() {
    InstalledAccessor installedAccessor = AccessorFactory.getAccessorFor(database, Installed.class);
    return Observable.fromCallable(() -> {
      // remove the current installed apps
      //AccessorFactory.getAccessorFor(Installed.class).removeAll();

      // get the installed apps
      List<PackageInfo> installedApps =
          AptoideUtils.SystemU.getAllInstalledApps(getPackageManager());
      Logger.getInstance()
          .v(TAG, "Found " + installedApps.size() + " user installed apps.");

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
    return RepositoryFactory.getUpdateRepository(this, getDefaultSharedPreferences())
        .sync(true, false);
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
    return applicationVersionCodeProvider;
  }

  public AdsRepository getAdsRepository() {
    return adsRepository;
  }

  public SyncStorage getSyncStorage() {
    return syncStorage;
  }

  public BehaviorRelay<Map<Integer, Result>> getFragmentResultRelay() {
    if (fragmentResultRelay == null) {
      fragmentResultRelay = BehaviorRelay.create();
    }
    return fragmentResultRelay;
  }

  @SuppressLint("UseSparseArrays") public Map<Integer, Result> getFragmentResulMap() {
    if (fragmentResulMap == null) {
      fragmentResulMap = new HashMap<>();
    }
    return fragmentResulMap;
  }

  public NavigationTracker getNavigationTracker() {
    return navigationTracker;
  }

  public abstract FragmentProvider createFragmentProvider();

  public abstract ActivityProvider createActivityProvider();

  public PurchaseFactory getPurchaseFactory() {
    if (purchaseFactory == null) {
      purchaseFactory = new PurchaseFactory();
    }
    return purchaseFactory;
  }

  public ReadPostsPersistence getReadPostsPersistence() {
    if (readPostsPersistence == null) {
      readPostsPersistence = new ReadPostsPersistence(new ArrayList<>());
    }
    return readPostsPersistence;
  }

  public String getVersionCode() {
    String version = "NaN";
    try {
      PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
      version = String.valueOf(pInfo.versionCode);
    } catch (PackageManager.NameNotFoundException e) {

    }
    return version;
  }

  private Completable dispatchPostReadEventInterval() {
    return Observable.interval(10, TimeUnit.SECONDS)
        .switchMap(__ -> getReadPostsPersistence().getPosts(10)
            .toObservable()
            .filter(postReads -> !postReads.isEmpty())
            .flatMap(
                postsRead -> PostReadRequest.of(postsRead, bodyInterceptorPoolV7, defaultClient,
                    WebService.getDefaultConverter(), tokenInvalidator,
                    getDefaultSharedPreferences())
                    .observe()
                    .flatMapCompletable(___ -> getReadPostsPersistence().removePosts(postsRead)))
            .repeatWhen(completed -> completed.takeWhile(
                ____ -> !getReadPostsPersistence().isPostsEmpty())))
        .toCompletable();
  }

  public SyncScheduler getAlarmSyncScheduler() {
    return alarmSyncScheduler;
  }

  public TrendingManager getTrendingManager() {
    return trendingManager;
  }

  public NotificationAnalytics getNotificationAnalytics() {
    return notificationAnalytics;
  }

  public IdsRepository getIdsRepository() {
    return idsRepository;
  }

  public SearchSuggestionManager getSearchSuggestionManager() {
    return searchSuggestionManager;
  }

  public TimelineAnalytics getTimelineAnalytics() {
    if (timelineAnalytics == null) {
      timelineAnalytics = new TimelineAnalytics(getNavigationTracker(), analyticsManager);
    }
    return timelineAnalytics;
  }

  public AnalyticsManager getAnalyticsManager() {
    return analyticsManager;
  }

  public AdultContentAnalytics getAdultContentAnalytics() {
    return adultContentAnalytics;
  }

  public SettingsManager getSettingsManager() {
    return settingsManager;
  }
}


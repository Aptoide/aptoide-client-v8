package cm.aptoide.pt;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.work.WorkManager;
import cm.aptoide.accountmanager.AdultContent;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.account.AdultContentAnalytics;
import cm.aptoide.pt.account.MatureBodyInterceptorV7;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.AdsUserPropertyManager;
import cm.aptoide.pt.analytics.FirstLaunchAnalytics;
import cm.aptoide.pt.crashreports.ConsoleLogger;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.RoomNotificationPersistence;
import cm.aptoide.pt.database.room.AptoideDatabase;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.cache.L2Cache;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.AdsApplicationVersionCodeProvider;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.download.OemidProvider;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.file.CacheHelper;
import cm.aptoide.pt.file.FileManager;
import cm.aptoide.pt.install.AptoideInstalledAppsRepository;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.InstalledBroadcastReceiver;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.leak.LeakTool;
import cm.aptoide.pt.link.AptoideInstallParser;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.networking.Pnp1AuthorizationInterceptor;
import cm.aptoide.pt.notification.AptoideWorkerFactory;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.NotificationCenter;
import cm.aptoide.pt.notification.NotificationInfo;
import cm.aptoide.pt.notification.NotificationPolicyFactory;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.notification.NotificationService;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.notification.NotificationsCleaner;
import cm.aptoide.pt.notification.ReadyToInstallNotificationManager;
import cm.aptoide.pt.notification.SystemNotificationShower;
import cm.aptoide.pt.notification.sync.NotificationSyncFactory;
import cm.aptoide.pt.notification.sync.NotificationSyncManager;
import cm.aptoide.pt.preferences.AptoideMd5Manager;
import cm.aptoide.pt.preferences.PRNGFixes;
import cm.aptoide.pt.preferences.Preferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.search.suggestions.SearchSuggestionManager;
import cm.aptoide.pt.search.suggestions.TrendingManager;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.sync.SyncScheduler;
import cm.aptoide.pt.sync.alarm.SyncStorage;
import cm.aptoide.pt.themes.NewFeature;
import cm.aptoide.pt.themes.NewFeatureManager;
import cm.aptoide.pt.themes.ThemeAnalytics;
import cm.aptoide.pt.updates.UpdateRepository;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.view.ActivityModule;
import cm.aptoide.pt.view.ActivityProvider;
import cm.aptoide.pt.view.BaseActivity;
import cm.aptoide.pt.view.BaseFragment;
import cm.aptoide.pt.view.FragmentModule;
import cm.aptoide.pt.view.FragmentProvider;
import cm.aptoide.pt.view.configuration.implementation.VanillaActivityProvider;
import cm.aptoide.pt.view.configuration.implementation.VanillaFragmentProvider;
import cm.aptoide.pt.view.recycler.DisplayableWidgetMapping;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryPerformance;
import com.indicative.client.android.Indicative;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import io.rakam.api.Rakam;
import io.rakam.api.RakamClient;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import okhttp3.OkHttpClient;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.preferences.managed.ManagedKeys.CAMPAIGN_SOCIAL_NOTIFICATIONS_PREFERENCE_VIEW_KEY;

public abstract class AptoideApplication extends Application {

  static final String CACHE_FILE_NAME = "aptoide.wscache";
  private static final String TAG = AptoideApplication.class.getName();
  private static FragmentProvider fragmentProvider;
  private static ActivityProvider activityProvider;
  private static DisplayableWidgetMapping displayableWidgetMapping;
  @Inject AptoideDatabase aptoideDatabase;
  @Inject RoomNotificationPersistence notificationPersistence;
  @Inject AptoideInstalledAppsRepository aptoideInstalledAppsRepository;
  @Inject @Named("base-rakam-host") String rakamBaseHost;
  @Inject AptoideDownloadManager aptoideDownloadManager;
  @Inject UpdateRepository updateRepository;
  @Inject CacheHelper cacheHelper;
  @Inject AptoideAccountManager accountManager;
  @Inject Preferences preferences;
  @Inject @Named("secure") cm.aptoide.pt.preferences.SecurePreferences securePreferences;
  @Inject AdultContent adultContent;
  @Inject IdsRepository idsRepository;
  @Inject @Named("default") OkHttpClient defaultClient;
  @Inject RootAvailabilityManager rootAvailabilityManager;
  @Inject AuthenticationPersistence authenticationPersistence;
  @Inject SyncScheduler alarmSyncScheduler;
  @Inject @Named("mature-pool-v7") BodyInterceptor<BaseBody> bodyInterceptorPoolV7;
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
  @Inject NewFeature newFeature;
  @Inject NewFeatureManager newFeatureManager;
  @Inject ReadyToInstallNotificationManager readyToInstallNotificationManager;
  @Inject ThemeAnalytics themeAnalytics;
  @Inject @Named("mature-pool-v7") BodyInterceptor<BaseBody> accountSettingsBodyInterceptorPoolV7;
  @Inject StoreCredentialsProvider storeCredentials;
  @Inject StoreUtilsProxy storeUtilsProxy;
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
  @Inject AdsUserPropertyManager adsUserPropertyManager;
  @Inject OemidProvider oemidProvider;
  @Inject AptoideMd5Manager aptoideMd5Manager;
  @Inject AptoideWorkerFactory aptoideWorkerFactory;
  @Inject LaunchManager launchManager;
  @Inject AppInBackgroundTracker appInBackgroundTracker;
  @Inject AppCoinsManager appCoinsManager;
  @Inject FileManager fileManager;
  private LeakTool leakTool;
  private NotificationCenter notificationCenter;
  private NotificationProvider notificationProvider;
  private BehaviorRelay<Map<Integer, Result>> fragmentResultRelay;
  private Map<Integer, Result> fragmentResultMap;
  private BodyInterceptor<BaseBody> accountSettingsBodyInterceptorWebV7;
  private ApplicationComponent applicationComponent;
  private PublishRelay<NotificationInfo> notificationsPublishRelay;
  private NotificationsCleaner notificationsCleaner;
  private NotificationSyncScheduler notificationSyncScheduler;
  private AptoideApplicationAnalytics aptoideApplicationAnalytics;

  private InstalledBroadcastReceiver packageChangeReceiver;


  public static FragmentProvider getFragmentProvider() {
    return fragmentProvider;
  }

  public static ActivityProvider getActivityProvider() {
    return activityProvider;
  }

  public static DisplayableWidgetMapping getDisplayableWidgetMapping() {
    return displayableWidgetMapping;
  }

  public LeakTool getLeakTool() {
    if (leakTool == null) {
      leakTool = new LeakTool();
    }
    return leakTool;
  }

  @Override public void onCreate() {
    getApplicationComponent().inject(this);
    packageChangeReceiver = new InstalledBroadcastReceiver();
    packageChangeReceiver.register(this);

    appInBackgroundTracker.initialize();
    CrashReport.getInstance()
        .addLogger(new ConsoleLogger());
    Logger.setDBG(ToolboxManager.isDebug(getDefaultSharedPreferences()) || BuildConfig.DEBUG);

    Single.fromCallable(() -> aptoideMd5Manager.calculateMd5Sum())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(__ -> {
        }, error -> CrashReport.getInstance()
            .log(error));

    try {
      PRNGFixes.apply();
    } catch (Exception e) {
      CrashReport.getInstance()
          .log(e);
    }

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
    analyticsManager.setup();
    UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
    aptoideApplicationAnalytics = new AptoideApplicationAnalytics(analyticsManager);

    androidx.work.Configuration configuration =
        new androidx.work.Configuration.Builder().setWorkerFactory(aptoideWorkerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build();
    WorkManager.initialize(this, configuration);

    FacebookSdk.sdkInitialize(this);
    AppEventsLogger.activateApp(this);
    AppEventsLogger.newLogger(this);

    initializeFlurry(this, BuildConfig.FLURRY_KEY);

    generateAptoideUuid().andThen(
            Completable.mergeDelayError(initializeRakamSdk(), initializeSentry(),
                initializeIndicative()))
        .doOnError(throwable -> CrashReport.getInstance()
            .log(throwable))
        .onErrorComplete()
        .andThen(Completable.mergeDelayError(setUpInitialAdsUserProperty(),
            handleAdsUserPropertyToggle(), sendAptoideApplicationStartAnalytics(
                uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION),
            aptoideInstalledAppsRepository.syncWithDevice()
                .subscribeOn(Schedulers.computation())))
        .doOnError(throwable -> CrashReport.getInstance()
            .log(throwable))
        .onErrorComplete()
        .andThen(setUpFirstRunAnalytics())
        .andThen(launchManager.launch()
            .subscribeOn(Schedulers.computation()))
        .subscribe(() -> { /* do nothing */}, error -> CrashReport.getInstance()
            .log(error));

    clearFileCache();

    startNotificationCenter();
    startNotificationCleaner();

    rootAvailabilityManager.isRootAvailable()
        .doOnSuccess(isRootAvailable -> {
          if (isRootAvailable) {
            rootInstallationRetryHandler.start();
          }
        })
        .subscribe(__ -> {
        }, throwable -> throwable.printStackTrace());

    accountManager.accountStatus()
        .map(account -> account.isLoggedIn())
        .distinctUntilChanged()
        .subscribe(isLoggedIn -> aptoideApplicationAnalytics.updateDimension(isLoggedIn));

    long totalExecutionTime = System.currentTimeMillis() - initialTimestamp;
    Logger.getInstance()
        .v(TAG, String.format("onCreate took %d millis.", totalExecutionTime));
    invalidRefreshTokenLogoutManager.start();

    installManager.start();
  }

  private Completable initializeIndicative() {
    return Completable.fromAction(() -> {
      Indicative.launch(getApplicationContext(), BuildConfig.INDICATIVE_KEY);
      Indicative.setUniqueID(idsRepository.getAndroidId());
      Indicative.addProperties(getIndicativeProperties());
    });
  }

  private Map<String, Object> getIndicativeProperties() {
    HashMap<String, Object> properties = new HashMap<>();
    properties.put("device_os_api_level", Build.VERSION.SDK_INT);
    properties.put("device_aptoide_vc", BuildConfig.VERSION_CODE);
    return properties;
  }

  private Completable handleAdsUserPropertyToggle() {
    return Completable.fromAction(() -> adsUserPropertyManager.start());
  }

  private Completable setUpInitialAdsUserProperty() {
    return idsRepository.getUniqueIdentifier()
        .flatMapCompletable(id -> adsUserPropertyManager.setUp(id))
        .doOnCompleted(() -> {
          Rakam.getInstance()
              .enableForegroundTracking(this);
        });
  }

  private Completable setUpFirstRunAnalytics() {
    return sendAppStartToAnalytics();
  }

  private Completable initializeSentry() {
    if (BuildConfig.SENTRY_DSN_KEY.equals("0")) {
      return Completable.complete();
    }
    return Completable.fromAction(
        () -> Sentry.init(BuildConfig.SENTRY_DSN_KEY, new AndroidSentryClientFactory(this)));
  }

  private void initializeRakam() {
    RakamClient instance = Rakam.getInstance();

    try {
      instance.initialize(this, new URL(rakamBaseHost), BuildConfig.RAKAM_API_KEY);
    } catch (MalformedURLException e) {
      Logger.getInstance()
          .e(TAG, "error: ", e);
    }
    instance.setDeviceId(idsRepository.getAndroidId());
    instance.trackSessionEvents(true);
    instance.setLogLevel(Log.VERBOSE);
    instance.setEventUploadPeriodMillis(1);
  }

  public ApplicationComponent getApplicationComponent() {
    if (applicationComponent == null) {
      applicationComponent = DaggerApplicationComponent.builder()
          .applicationModule(new ApplicationModule(this))
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
      notificationsCleaner = new NotificationsCleaner(notificationPersistence,
          Calendar.getInstance(TimeZone.getTimeZone("UTC")), accountManager,
          getNotificationProvider(), CrashReport.getInstance());
    }
    return notificationsCleaner;
  }

  public String getFeedbackEmail() {
    return "support@aptoide.com";
  }

  public String getAccountType() {
    return BuildConfig.APPLICATION_ID;
  }

  public String getExtraId() {
    return null;
  }

  public boolean isCreateStoreUserPrivacyEnabled() {
    return true;
  }

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
              new NotificationPolicyFactory(notificationProvider, aptoideInstalledAppsRepository),
              new NotificationAnalytics(new AptoideInstallParser(), analyticsManager,
                  navigationTracker));
    }
    return notificationCenter;
  }

  public NotificationProvider getNotificationProvider() {
    if (notificationProvider == null) {
      notificationProvider = new NotificationProvider(
          new RoomNotificationPersistence(aptoideDatabase.notificationDao()), Schedulers.io());
    }
    return notificationProvider;
  }

  public NotificationSyncScheduler getNotificationSyncScheduler() {
    if (notificationSyncScheduler == null) {
      notificationSyncScheduler = new NotificationSyncManager(getAlarmSyncScheduler(), true,
          new NotificationSyncFactory(new NotificationService(BuildConfig.APPLICATION_ID,
              new OkHttpClient.Builder().readTimeout(45, TimeUnit.SECONDS)
                  .writeTimeout(45, TimeUnit.SECONDS)
                  .addInterceptor(new Pnp1AuthorizationInterceptor(getAuthenticationPersistence(),
                      getTokenInvalidator()))
                  .build(), WebService.getDefaultConverter(), getIdsRepository(),
              BuildConfig.VERSION_NAME, getExtraId(), getDefaultSharedPreferences(), getResources(),
              getAccountManager()), getNotificationProvider()));
    }
    return notificationSyncScheduler;
  }

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

  public AptoideAccountManager getAccountManager() {
    return accountManager;
  }

  public AuthenticationPersistence getAuthenticationPersistence() {
    return authenticationPersistence;
  }

  public Preferences getPreferences() {
    return preferences;
  }

  public PackageRepository getPackageRepository() {
    return packageRepository;
  }

  private void clearFileCache() {
    fileManager.purgeCache()
        .subscribe(cleanedSize -> Logger.getInstance()
                .d(TAG, "cleaned size: " + AptoideUtils.StringU.formatBytes(cleanedSize, false)),
            err -> CrashReport.getInstance()
                .log(err));
  }

  private void initializeFlurry(Context context, String flurryKey) {
    new FlurryAgent.Builder().withLogEnabled(false)
        .withCaptureUncaughtExceptions(true)
        .withIncludeBackgroundSessionsInMetrics(true)
        .withPerformanceMetrics(FlurryPerformance.ALL)
        .build(context, flurryKey);
  }

  private Completable sendAptoideApplicationStartAnalytics(boolean isTv) {
    return Completable.fromAction(() -> {
      aptoideApplicationAnalytics.setPackageDimension(getPackageName());
      aptoideApplicationAnalytics.setVersionCodeDimension(getVersionCode());
      aptoideApplicationAnalytics.sendIsTvEvent(isTv);
    });
  }

  private Completable sendAppStartToAnalytics() {
    return firstLaunchAnalytics.sendAppStart(this,
        SecurePreferencesImplementation.getInstance(getApplicationContext(),
            getDefaultSharedPreferences()), idsRepository);
  }

  protected DisplayableWidgetMapping createDisplayableWidgetMapping() {
    return DisplayableWidgetMapping.getInstance();
  }

  private Completable generateAptoideUuid() {
    return Completable.fromAction(() -> idsRepository.getUniqueIdentifier());
  }

  private Completable initializeRakamSdk() {
    return Completable.fromAction(() -> initializeRakam())
        .subscribeOn(Schedulers.newThread());
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
          new MatureBodyInterceptorV7(bodyInterceptorWebV7, adultContent);
    }
    return accountSettingsBodyInterceptorWebV7;
  }

  public BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> getBodyInterceptorV3() {
    return bodyInterceptorV3;
  }

  protected String getAptoidePackage() {
    return BuildConfig.APPLICATION_ID;
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

  @SuppressLint("UseSparseArrays") public Map<Integer, Result> getFragmentResultMap() {
    if (fragmentResultMap == null) {
      fragmentResultMap = new HashMap<>();
    }
    return fragmentResultMap;
  }

  public NavigationTracker getNavigationTracker() {
    return navigationTracker;
  }

  public NewFeatureManager getNewFeatureManager() {
    return newFeatureManager;
  }

  public ReadyToInstallNotificationManager getReadyToInstallNotificationManager() {
    return readyToInstallNotificationManager;
  }

  public NewFeature getNewFeature() {
    return newFeature;
  }

  public ThemeAnalytics getThemeAnalytics() {
    return themeAnalytics;
  }

  public FragmentProvider createFragmentProvider() {
    return new VanillaFragmentProvider();
  }

  public ActivityProvider createActivityProvider() {
    return new VanillaActivityProvider();
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

  public String getPartnerId() {
    return oemidProvider.getOemid();
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

  public AnalyticsManager getAnalyticsManager() {
    return analyticsManager;
  }

  public AdultContentAnalytics getAdultContentAnalytics() {
    return adultContentAnalytics;
  }

  public SettingsManager getSettingsManager() {
    return settingsManager;
  }

  public StoreCredentialsProvider getStoreCredentials() {
    return storeCredentials;
  }

  public AppCoinsManager getAppCoinsManager() {
    return this.appCoinsManager;
  }
}


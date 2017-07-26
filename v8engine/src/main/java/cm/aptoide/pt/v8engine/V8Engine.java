/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.v8engine;

import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.AlarmManager;
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
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AccountDataPersist;
import cm.aptoide.accountmanager.AccountFactory;
import cm.aptoide.accountmanager.AccountManagerService;
import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Notification;
import cm.aptoide.pt.database.realm.PaymentAuthorization;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
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
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.PRNGFixes;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.root.RootValueSaver;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.SecurityUtils;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.v8engine.abtesting.ABTestManager;
import cm.aptoide.pt.v8engine.account.AndroidAccountDataMigration;
import cm.aptoide.pt.v8engine.account.AndroidAccountManagerDataPersist;
import cm.aptoide.pt.v8engine.account.AndroidAccountProvider;
import cm.aptoide.pt.v8engine.account.BaseBodyAccountManagerInterceptorFactory;
import cm.aptoide.pt.v8engine.account.DatabaseStoreDataPersist;
import cm.aptoide.pt.v8engine.account.LogAccountAnalytics;
import cm.aptoide.pt.v8engine.account.NoOpTokenInvalidator;
import cm.aptoide.pt.v8engine.account.NoTokenBodyInterceptor;
import cm.aptoide.pt.v8engine.account.RefreshTokenInvalidatorFactory;
import cm.aptoide.pt.v8engine.account.SocialAccountFactory;
import cm.aptoide.pt.v8engine.ads.AdsRepository;
import cm.aptoide.pt.v8engine.ads.MinimalAdMapper;
import cm.aptoide.pt.v8engine.ads.PackageRepositoryVersionCodeProvider;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.billing.AccountPayer;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.PaymentAnalytics;
import cm.aptoide.pt.v8engine.billing.inapp.InAppBillingSerializer;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationFactory;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.InAppBillingProductRepository;
import cm.aptoide.pt.v8engine.billing.repository.InAppBillingRepository;
import cm.aptoide.pt.v8engine.billing.repository.InAppTransactionRepository;
import cm.aptoide.pt.v8engine.billing.repository.PaidAppProductRepository;
import cm.aptoide.pt.v8engine.billing.repository.PaidAppTransactionRepository;
import cm.aptoide.pt.v8engine.billing.repository.PaymentMethodMapper;
import cm.aptoide.pt.v8engine.billing.repository.PaymentRepositoryFactory;
import cm.aptoide.pt.v8engine.billing.repository.ProductFactory;
import cm.aptoide.pt.v8engine.billing.repository.ProductRepositoryFactory;
import cm.aptoide.pt.v8engine.billing.repository.PurchaseFactory;
import cm.aptoide.pt.v8engine.billing.repository.TransactionFactory;
import cm.aptoide.pt.v8engine.billing.repository.sync.PaymentSyncScheduler;
import cm.aptoide.pt.v8engine.billing.repository.sync.ProductBundleMapper;
import cm.aptoide.pt.v8engine.billing.view.PaymentThrowableCodeMapper;
import cm.aptoide.pt.v8engine.billing.view.PurchaseIntentMapper;
import cm.aptoide.pt.v8engine.crashreports.ConsoleLogger;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.crashreports.CrashlyticsCrashLogger;
import cm.aptoide.pt.v8engine.deprecated.SQLiteDatabaseHelper;
import cm.aptoide.pt.v8engine.download.DownloadAnalytics;
import cm.aptoide.pt.v8engine.download.DownloadMirrorEventInterceptor;
import cm.aptoide.pt.v8engine.download.PaidAppsDownloadInterceptor;
import cm.aptoide.pt.v8engine.filemanager.CacheHelper;
import cm.aptoide.pt.v8engine.filemanager.FileManager;
import cm.aptoide.pt.v8engine.install.InstallFabricEvents;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.install.RootInstallNotificationEventReceiver;
import cm.aptoide.pt.v8engine.install.installer.RootInstallErrorNotificationFactory;
import cm.aptoide.pt.v8engine.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.v8engine.leak.LeakTool;
import cm.aptoide.pt.v8engine.networking.BaseBodyInterceptorV3;
import cm.aptoide.pt.v8engine.networking.BaseBodyInterceptorV7;
import cm.aptoide.pt.v8engine.networking.BasicAuthenticator;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import cm.aptoide.pt.v8engine.networking.MultipartBodyInterceptor;
import cm.aptoide.pt.v8engine.networking.RefreshTokenInvalidator;
import cm.aptoide.pt.v8engine.networking.UserAgentInterceptor;
import cm.aptoide.pt.v8engine.notification.NotificationCenter;
import cm.aptoide.pt.v8engine.notification.NotificationHandler;
import cm.aptoide.pt.v8engine.notification.NotificationIdsMapper;
import cm.aptoide.pt.v8engine.notification.NotificationNetworkService;
import cm.aptoide.pt.v8engine.notification.NotificationPolicyFactory;
import cm.aptoide.pt.v8engine.notification.NotificationProvider;
import cm.aptoide.pt.v8engine.notification.NotificationSyncScheduler;
import cm.aptoide.pt.v8engine.notification.NotificationSyncService;
import cm.aptoide.pt.v8engine.notification.NotificationsCleaner;
import cm.aptoide.pt.v8engine.notification.SystemNotificationShower;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import cm.aptoide.pt.v8engine.preferences.Preferences;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.spotandshare.AccountGroupNameProvider;
import cm.aptoide.pt.v8engine.spotandshare.ShareApps;
import cm.aptoide.pt.v8engine.spotandshare.SpotAndShareAnalytics;
import cm.aptoide.pt.v8engine.spotandshare.group.GroupNameProvider;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.account.store.StoreManager;
import cm.aptoide.pt.v8engine.view.configuration.ActivityProvider;
import cm.aptoide.pt.v8engine.view.configuration.FragmentProvider;
import cm.aptoide.pt.v8engine.view.configuration.implementation.ActivityProviderImpl;
import cm.aptoide.pt.v8engine.view.configuration.implementation.FragmentProviderImpl;
import cm.aptoide.pt.v8engine.view.entry.EntryActivity;
import cm.aptoide.pt.v8engine.view.entry.EntryPointChooser;
import cm.aptoide.pt.v8engine.view.recycler.DisplayableWidgetMapping;
import cn.dreamtobe.filedownloader.OkHttp3Connection;
import com.crashlytics.android.answers.Answers;
import com.facebook.appevents.AppEventsLogger;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.jakewharton.rxrelay.PublishRelay;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.seatgeek.sixpack.SixpackBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.preferences.managed.ManagedKeys.CAMPAIGN_SOCIAL_NOTIFICATIONS_PREFERENCE_VIEW_KEY;
import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class V8Engine extends Application {

  private static final String CACHE_FILE_NAME = "aptoide.wscache";
  private static final String TAG = V8Engine.class.getName();

  @Getter private static FragmentProvider fragmentProvider;
  @Getter private static ActivityProvider activityProvider;
  @Getter private static DisplayableWidgetMapping displayableWidgetMapping;
  @Setter @Getter private static boolean autoUpdateWasCalled = false;

  @Getter @Setter private static ShareApps shareApps;
  private AptoideAccountManager accountManager;
  private BodyInterceptor<BaseBody> baseBodyInterceptorV7;
  private BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> baseBodyInterceptorV3;
  private Preferences preferences;
  private cm.aptoide.pt.v8engine.preferences.SecurePreferences securePreferences;
  private SecureCoderDecoder secureCodeDecoder;
  private AdultContent adultContent;
  private IdsRepository idsRepository;
  private GoogleApiClient googleSignInClient;
  private LeakTool leakTool;
  private String aptoideMd5sum;
  private AptoideDownloadManager downloadManager;
  private SparseArray<InstallManager> installManagers;
  private OkHttpClient defaultClient;
  private OkHttpClient longTimeoutClient;
  private L2Cache httpClientCache;
  private UserAgentInterceptor userAgentInterceptor;
  private AccountFactory accountFactory;
  private AndroidAccountProvider androidAccountProvider;
  private PaymentAnalytics paymentAnalytics;
  private ObjectMapper nonNullObjectMapper;
  private RequestBodyFactory requestBodyFactory;
  private PaymentSyncScheduler paymentSyncScheduler;
  private InAppBillingRepository inAppBillingRepository;
  private Payer accountPayer;
  private InAppBillingSerializer inAppBillingSerialzer;
  private AuthorizationFactory authorizationFactory;
  private Billing billing;
  private PurchaseIntentMapper purchaseIntentMapper;
  private PaymentThrowableCodeMapper paymentThrowableCodeMapper;
  private MultipartBodyInterceptor multipartBodyInterceptor;
  private NotificationHandler notificationHandler;
  private NotificationCenter notificationCenter;
  private QManager qManager;
  private EntryPointChooser entryPointChooser;
  private NotificationSyncScheduler notificationSyncScheduler;
  private RootAvailabilityManager rootAvailabilityManager;
  private RootInstallationRetryHandler rootInstallationRetryHandler;
  private RefreshTokenInvalidator tokenInvalidator;
  private FileManager fileManager;
  private CacheHelper cacheHelper;
  private StoreManager storeManager;
  private PackageRepository packageRepository;
  private AdsApplicationVersionCodeProvider applicationVersionCodeProvider;
  private AdsRepository adsRepository;
  private ABTestManager abTestManager;
  private Authenticator webServiceAuthenticator;

  /**
   * call after this instance onCreate()
   */
  protected void activateLogger() {
    Logger.setDBG(true);
  }

  public LeakTool getLeakTool() {
    if (leakTool == null) {
      leakTool = new LeakTool();
    }
    return leakTool;
  }

  @Partners @Override public void onCreate() {
    //
    // apply security fixes
    //
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

    Logger.setDBG(ToolboxManager.isDebug(getDefaultSharedPreferences()) || BuildConfig.DEBUG);

    Database.initialize(this);

    //
    // async app initialization
    // beware! this code could be executed at the same time the first activity is
    // visible
    //
    checkAppSecurity().andThen(generateAptoideUuid())
        .observeOn(Schedulers.computation())
        .andThen(initAbTestManager())
        .andThen(prepareApp(V8Engine.this.getAccountManager()).onErrorComplete(err -> {
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

    long totalExecutionTime = System.currentTimeMillis() - initialTimestamp;
    Logger.v(TAG, String.format("onCreate took %d millis.", totalExecutionTime));
  }

  public TokenInvalidator getTokenInvalidator() {
    if (tokenInvalidator == null) {
      tokenInvalidator = new RefreshTokenInvalidator(getAccountManager());
    }
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
              getInstallManager(InstallerFactory.ROLLBACK), PublishRelay.create(), 0, this,
              new RootInstallErrorNotificationFactory(notificationId,
                  BitmapFactory.decodeResource(getResources(), getConfiguration().getIcon()),
                  action, deleteAction));
    }
    return rootInstallationRetryHandler;
  }

  public NotificationNetworkService getNotificationNetworkService() {
    return getNotificationHandler();
  }

  public NotificationCenter getNotificationCenter() {
    if (notificationCenter == null) {

      final SystemNotificationShower systemNotificationShower = new SystemNotificationShower(this,
          (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE),
          new NotificationIdsMapper());

      final NotificationAccessor notificationAccessor =
          AccessorFactory.getAccessorFor(Notification.class);

      final NotificationProvider notificationProvider =
          new NotificationProvider(notificationAccessor, Schedulers.io());

      notificationCenter = new NotificationCenter(getNotificationHandler(), notificationProvider,
          getNotificationSyncScheduler(), systemNotificationShower, CrashReport.getInstance(),
          new NotificationPolicyFactory(notificationProvider),
          new NotificationsCleaner(notificationAccessor), getAccountManager());
    }
    return notificationCenter;
  }

  public StoreManager getStoreManager() {
    if (storeManager == null) {
      storeManager =
          new StoreManager(accountManager, getDefaultClient(), WebService.getDefaultConverter(),
              getMultipartBodyInterceptor(), getBaseBodyInterceptorV3(), getBaseBodyInterceptorV7(),
              getDefaultSharedPreferences(), getTokenInvalidator(), getRequestBodyFactory(),
              getNonNullObjectMapper());
    }
    return storeManager;
  }

  @NonNull public NotificationSyncScheduler getNotificationSyncScheduler() {
    if (notificationSyncScheduler == null) {

      long pushNotificationSocialPeriodicity = DateUtils.MINUTE_IN_MILLIS * 10;
      if (ToolboxManager.getPushNotificationPullingInterval(getDefaultSharedPreferences()) > 0) {
        pushNotificationSocialPeriodicity =
            ToolboxManager.getPushNotificationPullingInterval(getDefaultSharedPreferences());
      }

      final List<NotificationSyncScheduler.Schedule> scheduleList = Arrays.asList(
          new NotificationSyncScheduler.Schedule(
              NotificationSyncService.NOTIFICATIONS_CAMPAIGN_ACTION, AlarmManager.INTERVAL_DAY),
          new NotificationSyncScheduler.Schedule(
              NotificationSyncService.NOTIFICATIONS_SOCIAL_ACTION,
              pushNotificationSocialPeriodicity));

      notificationSyncScheduler =
          new NotificationSyncScheduler(this, (AlarmManager) getSystemService(ALARM_SERVICE),
              NotificationSyncService.class, scheduleList, true);
    }
    return notificationSyncScheduler;
  }

  public SharedPreferences getDefaultSharedPreferences() {
    return PreferenceManager.getDefaultSharedPreferences(this);
  }

  public GroupNameProvider getGroupNameProvider() {
    return new AccountGroupNameProvider(getAccountManager(), Build.MANUFACTURER, Build.MODEL,
        Build.ID);
  }

  public NotificationHandler getNotificationHandler() {
    if (notificationHandler == null) {
      notificationHandler =
          new NotificationHandler(getConfiguration().getAppId(), getDefaultClient(),
              WebService.getDefaultConverter(), getIdsRepository(),
              getConfiguration().getVersionName(), getAccountManager(),
              getConfiguration().getExtraId(), PublishRelay.create(), getDefaultSharedPreferences(),
              getResources());
    }
    return notificationHandler;
  }

  public OkHttpClient getLongTimeoutClient() {
    if (longTimeoutClient == null) {
      final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
      okHttpClientBuilder.addInterceptor(getUserAgentInterceptor());
      okHttpClientBuilder.addInterceptor(getToolboxRetrofitLogsInterceptor());
      okHttpClientBuilder.connectTimeout(2, TimeUnit.MINUTES);
      okHttpClientBuilder.readTimeout(2, TimeUnit.MINUTES);
      okHttpClientBuilder.writeTimeout(2, TimeUnit.MINUTES);

      if (ToolboxManager.isToolboxEnableRetrofitLogs(getDefaultSharedPreferences())) {
        okHttpClientBuilder.addInterceptor(getToolboxRetrofitLogsInterceptor());
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

      if (ToolboxManager.isToolboxEnableRetrofitLogs(getDefaultSharedPreferences())) {
        okHttpClientBuilder.addInterceptor(getToolboxRetrofitLogsInterceptor());
      }

      defaultClient = okHttpClientBuilder.build();
    }
    return defaultClient;
  }

  public Interceptor getUserAgentInterceptor() {
    if (userAgentInterceptor == null) {
      userAgentInterceptor =
          new UserAgentInterceptor(getAndroidAccountProvider(), getIdsRepository(),
              getConfiguration().getPartnerId(), new DisplayMetrics(),
              AptoideUtils.SystemU.TERMINAL_INFO, AptoideUtils.Core.getDefaultVername(this));
    }
    return userAgentInterceptor;
  }

  private Interceptor getToolboxRetrofitLogsInterceptor() {
    return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
  }

  public L2Cache getHttpClientCache() {
    if (httpClientCache == null) {
      httpClientCache =
          new L2Cache(new POSTCacheKeyAlgorithm(), new File(getCacheDir(), CACHE_FILE_NAME));
    }
    return httpClientCache;
  }

  public AptoideDownloadManager getDownloadManager() {
    if (downloadManager == null) {

      final String apkPath = getConfiguration().getCachePath() + "apks/";
      final String obbPath = getConfiguration().getCachePath() + "obb/";
      final OkHttpClient.Builder httpClientBuilder =
          new OkHttpClient.Builder().addInterceptor(getUserAgentInterceptor())
              .addInterceptor(new PaidAppsDownloadInterceptor(getAccountManager()))
              .addInterceptor(new DownloadMirrorEventInterceptor(Analytics.getInstance()))
              .connectTimeout(20, TimeUnit.SECONDS)
              .writeTimeout(20, TimeUnit.SECONDS)
              .readTimeout(20, TimeUnit.SECONDS);

      FileUtils.createDir(apkPath);
      FileUtils.createDir(obbPath);

      FileDownloader.init(this, new DownloadMgrInitialParams.InitCustomMaker().connectionCreator(
          new OkHttp3Connection.Creator(httpClientBuilder)));

      downloadManager = new AptoideDownloadManager(AccessorFactory.getAccessorFor(Download.class),
          getCacheHelper(), new FileUtils(action -> Analytics.File.moveFile(action)),
          new DownloadAnalytics(Analytics.getInstance()), FileDownloader.getImpl(),
          getConfiguration().getCachePath(), apkPath, obbPath);
    }
    return downloadManager;
  }

  public InstallManager getInstallManager(int installerType) {

    if (installManagers == null) {
      installManagers = new SparseArray<>();
    }

    InstallManager installManager = installManagers.get(installerType);
    if (installManager == null) {
      installManager = new InstallManager(getApplicationContext(), getDownloadManager(),
          new InstallerFactory(new MinimalAdMapper(),
              new InstallFabricEvents(Analytics.getInstance(), Answers.getInstance())).create(this,
              installerType), getRootAvailabilityManager(), getDefaultSharedPreferences(),
          SecurePreferencesImplementation.getInstance(getApplicationContext(),
              getDefaultSharedPreferences()));
      installManagers.put(installerType, installManager);
    }

    return installManager;
  }

  public QManager getQManager() {
    if (qManager == null) {
      qManager = new QManager(getDefaultSharedPreferences(), getResources(),
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

      final AccountManagerService accountManagerService = new AccountManagerService(
          new BaseBodyAccountManagerInterceptorFactory(getIdsRepository(), getPreferences(),
              getSecurePreferences(), getAptoideMd5sum(), getAptoidePackage(), getQManager(),
              getDefaultSharedPreferences(), getResources(), getPackageName(),
              Build.VERSION.SDK_INT, getPackageRepository()), getAccountFactory(),
          getDefaultClient(), getLongTimeoutClient(), WebService.getDefaultConverter(),
          getNonNullObjectMapper(), new RefreshTokenInvalidatorFactory(),
          getDefaultSharedPreferences());

      final AndroidAccountDataMigration accountDataMigration = new AndroidAccountDataMigration(
          SecurePreferencesImplementation.getInstance(this, getDefaultSharedPreferences()),
          getDefaultSharedPreferences(), AccountManager.get(this),
          new SecureCoderDecoder.Builder(this, getDefaultSharedPreferences()).create(),
          SQLiteDatabaseHelper.DATABASE_VERSION,
          getDatabasePath(SQLiteDatabaseHelper.DATABASE_NAME).getPath(),
          getConfiguration().getAccountType());

      final AccountDataPersist accountDataPersist =
          new AndroidAccountManagerDataPersist(AccountManager.get(this),
              new DatabaseStoreDataPersist(AccessorFactory.getAccessorFor(Store.class),
                  new DatabaseStoreDataPersist.DatabaseStoreMapper()), getAccountFactory(),
              accountDataMigration, getAndroidAccountProvider(), Schedulers.io());

      accountManager = new AptoideAccountManager.Builder().setAccountDataPersist(accountDataPersist)
          .setAccountAnalytics(new LogAccountAnalytics())
          .setAccountManagerService(accountManagerService)
          .build();
    }
    return accountManager;
  }

  public AccountFactory getAccountFactory() {
    if (accountFactory == null) {
      accountFactory = new AccountFactory(new SocialAccountFactory(this, getGoogleSignInClient()),
          new AccountService(new NoTokenBodyInterceptor(getIdsRepository(), getAptoideMd5sum(),
              getAptoidePackage()), getDefaultClient(), WebService.getDefaultConverter(),
              new NoOpTokenInvalidator(), getDefaultSharedPreferences()));
    }
    return accountFactory;
  }

  public AndroidAccountProvider getAndroidAccountProvider() {
    if (androidAccountProvider == null) {
      androidAccountProvider =
          new AndroidAccountProvider(AccountManager.get(this), getConfiguration().getAccountType(),
              Schedulers.io());
    }
    return androidAccountProvider;
  }

  public IdsRepository getIdsRepository() {
    if (idsRepository == null) {
      idsRepository = new IdsRepository(
          SecurePreferencesImplementation.getInstance(getApplicationContext(),
              getDefaultSharedPreferences()), this,
          Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
    }
    return idsRepository;
  }

  public Preferences getPreferences() {
    if (preferences == null) {
      preferences = new Preferences(getDefaultSharedPreferences());
    }
    return preferences;
  }

  public ABTestManager getABTestManager() {
    if (abTestManager == null) {
      abTestManager = new ABTestManager(new SixpackBuilder(),
          new OkHttpClient.Builder().authenticator(
              new BasicAuthenticator(BuildConfig.SIXPACK_USER, BuildConfig.SIXPACK_PASSWORD))
              .build(), BuildConfig.SIXPACK_URL);
    }
    return abTestManager;
  }

  public cm.aptoide.pt.v8engine.preferences.SecurePreferences getSecurePreferences() {
    if (securePreferences == null) {
      securePreferences =
          new cm.aptoide.pt.v8engine.preferences.SecurePreferences(getDefaultSharedPreferences(),
              getSecureCoderDecoder());
    }
    return securePreferences;
  }

  public GoogleApiClient getGoogleSignInClient() {
    if (googleSignInClient == null) {
      googleSignInClient = new GoogleApiClient.Builder(this).addApi(GOOGLE_SIGN_IN_API,
          new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
              .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
              .requestScopes(new Scope(Scopes.PROFILE))
              .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
              .build())
          .build();
    }
    return googleSignInClient;
  }

  public SecureCoderDecoder getSecureCoderDecoder() {
    if (secureCodeDecoder == null) {
      secureCodeDecoder =
          new SecureCoderDecoder.Builder(this, getDefaultSharedPreferences()).create();
    }
    return secureCodeDecoder;
  }

  public PaymentAnalytics getPaymentAnalytics() {
    if (paymentAnalytics == null) {
      paymentAnalytics =
          new PaymentAnalytics(Analytics.getInstance(), AppEventsLogger.newLogger(this),
              getAptoidePackage());
    }
    return paymentAnalytics;
  }

  public PaymentSyncScheduler getPaymentSyncScheduler() {
    if (paymentSyncScheduler == null) {
      paymentSyncScheduler =
          new PaymentSyncScheduler(new ProductBundleMapper(), getAndroidAccountProvider(),
              getConfiguration().getContentAuthority());
    }
    return paymentSyncScheduler;
  }

  public Billing getBilling() {

    if (billing == null) {

      final TransactionFactory confirmationFactory = new TransactionFactory();

      final PaymentRepositoryFactory paymentRepositoryFactory = new PaymentRepositoryFactory(
          new InAppTransactionRepository(getNetworkOperatorManager(),
              AccessorFactory.getAccessorFor(PaymentConfirmation.class), getPaymentSyncScheduler(),
              confirmationFactory, getBaseBodyInterceptorV3(), getDefaultClient(),
              WebService.getDefaultConverter(), getAccountPayer(), getTokenInvalidator(),
              getDefaultSharedPreferences()),
          new PaidAppTransactionRepository(getNetworkOperatorManager(),
              AccessorFactory.getAccessorFor(PaymentConfirmation.class), getPaymentSyncScheduler(),
              confirmationFactory, getBaseBodyInterceptorV3(), WebService.getDefaultConverter(),
              getDefaultClient(), getAccountPayer(), getTokenInvalidator(),
              getDefaultSharedPreferences()));

      final ProductFactory productFactory = new ProductFactory();

      final PurchaseFactory purchaseFactory =
          new PurchaseFactory(getInAppBillingSerializer(), getInAppBillingRepository());

      final PaymentMethodMapper paymentMethodMapper =
          new PaymentMethodMapper(paymentRepositoryFactory, new AuthorizationRepository(
              AccessorFactory.getAccessorFor(PaymentAuthorization.class), getPaymentSyncScheduler(),
              getAuthorizationFactory(), getBaseBodyInterceptorV3(), getDefaultClient(),
              WebService.getDefaultConverter(), getAccountPayer(), getTokenInvalidator(),
              getDefaultSharedPreferences()), getAuthorizationFactory(), getAccountPayer());

      final ProductRepositoryFactory productRepositoryFactory = new ProductRepositoryFactory(
          new PaidAppProductRepository(purchaseFactory, paymentMethodMapper,
              getNetworkOperatorManager(), getBaseBodyInterceptorV3(), getDefaultClient(),
              WebService.getDefaultConverter(), productFactory, getTokenInvalidator(),
              getDefaultSharedPreferences(), getResources()),
          new InAppBillingProductRepository(purchaseFactory, paymentMethodMapper, productFactory,
              getBaseBodyInterceptorV3(), getDefaultClient(), WebService.getDefaultConverter(),
              getNetworkOperatorManager(), getTokenInvalidator(), getDefaultSharedPreferences(),
              getPackageRepository()));

      billing = new Billing(productRepositoryFactory, paymentRepositoryFactory,
          getInAppBillingRepository());
    }
    return billing;
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

  public PurchaseIntentMapper getPurchaseIntentMapper() {
    if (purchaseIntentMapper == null) {
      purchaseIntentMapper = new PurchaseIntentMapper(getPaymentThrowableCodeMapper());
    }
    return purchaseIntentMapper;
  }

  public InAppBillingSerializer getInAppBillingSerializer() {
    if (inAppBillingSerialzer == null) {
      inAppBillingSerialzer = new InAppBillingSerializer();
    }
    return inAppBillingSerialzer;
  }

  public AuthorizationFactory getAuthorizationFactory() {
    if (authorizationFactory == null) {
      authorizationFactory = new AuthorizationFactory();
    }
    return authorizationFactory;
  }

  public Payer getAccountPayer() {
    if (accountPayer == null) {
      accountPayer = new AccountPayer(getAccountManager());
    }
    return accountPayer;
  }

  public InAppBillingRepository getInAppBillingRepository() {
    if (inAppBillingRepository == null) {
      inAppBillingRepository =
          new InAppBillingRepository(AccessorFactory.getAccessorFor(PaymentConfirmation.class),
              getBaseBodyInterceptorV3(), getDefaultClient(), WebService.getDefaultConverter(),
              getTokenInvalidator(), getDefaultSharedPreferences());
    }
    return inAppBillingRepository;
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
      fileManager = new FileManager(getCacheHelper(), new FileUtils(), new String[] {
          getApplicationContext().getCacheDir().getPath(), getConfiguration().getCachePath()
      }, getDownloadManager(), getHttpClientCache());
    }
    return fileManager;
  }

  private CacheHelper getCacheHelper() {
    if (cacheHelper == null) {
      List<CacheHelper.FolderToManage> folders = new LinkedList<>();

      final String cachePath = getConfiguration().getCachePath();

      long month = DateUtils.DAY_IN_MILLIS * 30;
      folders.add(new CacheHelper.FolderToManage(new File(cachePath), month));
      folders.add(new CacheHelper.FolderToManage(new File(cachePath + "icons/"), 1024 * 1024));
      folders.add(new CacheHelper.FolderToManage(
          new File(getApplicationContext().getCacheDir() + "image_manager_disk_cache/"), month));
      cacheHelper =
          new CacheHelper(ManagerPreferences.getCacheLimit(getDefaultSharedPreferences()), folders,
              new FileUtils());
    }
    return cacheHelper;
  }

  private void initializeFlurry(Context context, String flurryKey) {
    new FlurryAgent.Builder().withLogEnabled(false)
        .build(context, flurryKey);
  }

  private Completable sendAppStartToAnalytics() {
    return Analytics.Lifecycle.Application.onCreate(this, WebService.getDefaultConverter(),
        getDefaultClient(), getBaseBodyInterceptorV7(),
        SecurePreferencesImplementation.getInstance(getApplicationContext(),
            getDefaultSharedPreferences()), getTokenInvalidator());
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

  @Partners protected FragmentProvider createFragmentProvider() {
    return new FragmentProviderImpl();
  }

  @Partners protected ActivityProvider createActivityProvider() {
    return new ActivityProviderImpl();
  }

  @Partners protected DisplayableWidgetMapping createDisplayableWidgetMapping() {
    return DisplayableWidgetMapping.getInstance();
  }

  private Completable generateAptoideUuid() {
    return Completable.fromAction(() -> getIdsRepository().getUniqueIdentifier())
        .subscribeOn(Schedulers.newThread());
  }

  private Completable initAbTestManager() {
    return Completable.defer(
        () -> getABTestManager().initialize(getIdsRepository().getUniqueIdentifier())
            .toCompletable());
  }

  private Completable prepareApp(AptoideAccountManager accountManager) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMapCompletable(account -> {
          if (SecurePreferences.isFirstRun(
              SecurePreferencesImplementation.getInstance(getApplicationContext(),
                  getDefaultSharedPreferences()))) {

            PreferenceManager.setDefaultValues(this, R.xml.settings, false);

            return setupFirstRun(accountManager).andThen(
                getRootAvailabilityManager().updateRootAvailability())
                .andThen(Completable.merge(accountManager.syncCurrentAccount(), createShortcut()));
          }

          return Completable.complete();
        });
  }

  // todo re-factor all this code to proper Rx
  private Completable setupFirstRun(final AptoideAccountManager accountManager) {
    return Completable.defer(() -> {

      final StoreCredentialsProviderImpl storeCredentials = new StoreCredentialsProviderImpl();

      StoreUtilsProxy proxy =
          new StoreUtilsProxy(getAccountManager(), getBaseBodyInterceptorV7(), storeCredentials,
              AccessorFactory.getAccessorFor(Store.class), getDefaultClient(),
              WebService.getDefaultConverter(), getTokenInvalidator(),
              getDefaultSharedPreferences());

      BaseRequestWithStore.StoreCredentials defaultStoreCredentials =
          storeCredentials.get(getConfiguration().getDefaultStore());

      return generateAptoideUuid().andThen(proxy.addDefaultStore(
          GetStoreMetaRequest.of(defaultStoreCredentials, getBaseBodyInterceptorV7(),
              getDefaultClient(), WebService.getDefaultConverter(), getTokenInvalidator(),
              getDefaultSharedPreferences()), getAccountManager(), defaultStoreCredentials)
          .andThen(refreshUpdates()))
          .doOnError(err -> CrashReport.getInstance()
              .log(err));
    });
  }

  public BodyInterceptor<BaseBody> getBaseBodyInterceptorV7() {
    if (baseBodyInterceptorV7 == null) {
      baseBodyInterceptorV7 = new BaseBodyInterceptorV7(getIdsRepository(), getAccountManager(),
          getAdultContent(getSecurePreferences()), getAptoideMd5sum(), getAptoidePackage(),
          getQManager(), "pool", getDefaultSharedPreferences(), getResources(), getPackageName(),
          getPackageRepository());
    }
    return baseBodyInterceptorV7;
  }

  public BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> getBaseBodyInterceptorV3() {
    if (baseBodyInterceptorV3 == null) {
      baseBodyInterceptorV3 =
          new BaseBodyInterceptorV3(getIdsRepository(), getAptoideMd5sum(), getAptoidePackage(),
              getAccountManager(), getQManager(), getDefaultSharedPreferences(),
              BaseBodyInterceptorV3.RESPONSE_MODE_JSON, Build.VERSION.SDK_INT);
    }
    return baseBodyInterceptorV3;
  }

  public BodyInterceptor<HashMapNotNull<String, RequestBody>> getMultipartBodyInterceptor() {
    if (multipartBodyInterceptor == null) {
      multipartBodyInterceptor =
          new MultipartBodyInterceptor(getIdsRepository(), getAccountManager(),
              getRequestBodyFactory());
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
    return getConfiguration().getAppId();
  }

  public AdultContent getAdultContent(
      cm.aptoide.pt.v8engine.preferences.SecurePreferences securePreferences) {
    if (adultContent == null) {
      adultContent = new AdultContent(getAccountManager(), getPreferences(), securePreferences);
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
    InstalledAccessor installedAccessor = AccessorFactory.getAccessorFor(Installed.class);
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
    return RepositoryFactory.getUpdateRepository(this, getDefaultSharedPreferences())
        .sync(true);
  }

  /**
   * Do {@link #createShortcut()} using a {@link Completable}
   */
  @Deprecated @Partners public void createShortCut() {
    createAppShortcut();
  }

  private void createAppShortcut() {
    Intent shortcutIntent = new Intent(this, EntryActivity.class);
    shortcutIntent.setAction(Intent.ACTION_MAIN);
    Intent intent = new Intent();
    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Aptoide");
    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
        Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
    intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
    getApplicationContext().sendBroadcast(intent);
  }

  @Partners protected void setupCrashReports(boolean isDisabled) {
    CrashReport.getInstance()
        .addLogger(new CrashlyticsCrashLogger(this, isDisabled))
        .addLogger(new ConsoleLogger());
  }

  //
  // Strict Mode
  //

  /**
   * do not erase this method. it should be called in internal and dev Application class
   * of Vanilla module
   */
  protected void setupStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
        .penaltyLog()
        .build());

    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedClosableObjects()
        .detectLeakedClosableObjects()
        .penaltyLog()
        .penaltyDeath()
        .build());
  }

  public RootAvailabilityManager getRootAvailabilityManager() {
    if (rootAvailabilityManager == null) {
      rootAvailabilityManager = new RootAvailabilityManager(new RootValueSaver() {
        final String IS_PHONE_ROOTED = "IS_PHONE_ROOTED";

        @Override public Single<Boolean> isPhoneRoot() {
          return getSecurePreferences().getBoolean(IS_PHONE_ROOTED, false)
              .first()
              .toSingle();
        }

        @Override public Completable save(boolean rootAvailable) {
          return getSecurePreferences().save(IS_PHONE_ROOTED, rootAvailable);
        }
      });
    }
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
      adsRepository = new AdsRepository(getIdsRepository(), accountManager, getDefaultClient(),
          WebService.getDefaultConverter(), qManager, getDefaultSharedPreferences(),
          getApplicationContext(),
          (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE), getResources(),
          getVersionCodeProvider(),
          (context) -> AdNetworkUtils.isGooglePlayServicesAvailable(context),
          () -> V8Engine.getConfiguration()
              .getPartnerId(), new MinimalAdMapper());
    }
    return adsRepository;
  }
}

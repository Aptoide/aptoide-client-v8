/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import cm.aptoide.accountmanager.AccountDataPersist;
import cm.aptoide.accountmanager.AccountFactory;
import cm.aptoide.accountmanager.AccountManagerService;
import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.cache.L2Cache;
import cm.aptoide.pt.networkclient.okhttp.cache.POSTCacheInterceptor;
import cm.aptoide.pt.networkclient.okhttp.cache.POSTCacheKeyAlgorithm;
import cm.aptoide.pt.preferences.PRNGFixes;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.spotandshareandroid.GroupNameProvider;
import cm.aptoide.pt.spotandshareandroid.ShareApps;
import cm.aptoide.pt.spotandshareandroid.SpotAndShareApplication;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.SecurityUtils;
import cm.aptoide.pt.v8engine.abtesting.ABTestManager;
import cm.aptoide.pt.v8engine.account.AccountEventsAnalytcs;
import cm.aptoide.pt.v8engine.account.AndroidAccountDataMigration;
import cm.aptoide.pt.v8engine.account.AndroidAccountManagerDataPersist;
import cm.aptoide.pt.v8engine.account.AndroidAccountProvider;
import cm.aptoide.pt.v8engine.account.BaseBodyInterceptorFactory;
import cm.aptoide.pt.v8engine.account.DatabaseStoreDataPersist;
import cm.aptoide.pt.v8engine.account.SocialAccountFactory;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.ConsoleLogger;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.crashreports.CrashlyticsCrashLogger;
import cm.aptoide.pt.v8engine.deprecated.SQLiteDatabaseHelper;
import cm.aptoide.pt.v8engine.download.DownloadAnalytics;
import cm.aptoide.pt.v8engine.download.DownloadMirrorEventInterceptor;
import cm.aptoide.pt.v8engine.download.PaidAppsDownloadInterceptor;
import cm.aptoide.pt.v8engine.filemanager.CacheHelper;
import cm.aptoide.pt.v8engine.filemanager.FileManager;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.leak.LeakTool;
import cm.aptoide.pt.v8engine.networking.BaseBodyInterceptorV3;
import cm.aptoide.pt.v8engine.networking.BaseBodyInterceptorV7;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import cm.aptoide.pt.v8engine.networking.UserAgentInterceptor;
import cm.aptoide.pt.v8engine.payment.PaymentAnalytics;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import cm.aptoide.pt.v8engine.preferences.Preferences;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.spotandshare.AccountGroupNameProvider;
import cm.aptoide.pt.v8engine.spotandshare.SpotAndShareAnalytics;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.MainActivity;
import cm.aptoide.pt.v8engine.view.configuration.ActivityProvider;
import cm.aptoide.pt.v8engine.view.configuration.FragmentProvider;
import cm.aptoide.pt.v8engine.view.configuration.implementation.ActivityProviderImpl;
import cm.aptoide.pt.v8engine.view.configuration.implementation.FragmentProviderImpl;
import cm.aptoide.pt.v8engine.view.recycler.DisplayableWidgetMapping;
import cn.dreamtobe.filedownloader.OkHttp3Connection;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class V8Engine extends SpotAndShareApplication {

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

    Logger.setDBG(ManagerPreferences.isDebug() || BuildConfig.DEBUG);

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

    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    sendAppStartToAnalytics(sharedPreferences);

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

    long totalExecutionTime = System.currentTimeMillis() - initialTimestamp;
    Logger.v(TAG, String.format("onCreate took %d millis.", totalExecutionTime));
  }

  @Override protected TokenInvalidator getTokenInvalidator() {
    return new TokenInvalidator() {
      @Override public Single<String> invalidateAccessToken() {
        final AptoideAccountManager accountManager = getAccountManager();
        return accountManager.refreshToken()
            .andThen(accountManager.accountStatus()
                .first()
                .toSingle())
            .map(account -> account.getAccessToken());
      }
    };
  }

  public GroupNameProvider getGroupNameProvider() {
    return new AccountGroupNameProvider(getAccountManager(), Build.MANUFACTURER, Build.MODEL,
        Build.ID);
  }

  public OkHttpClient getLongTimeoutClient() {
    if (longTimeoutClient == null) {
      final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
      clientBuilder.addInterceptor(getUserAgentInterceptor());
      clientBuilder.connectTimeout(2, TimeUnit.MINUTES);
      clientBuilder.readTimeout(2, TimeUnit.MINUTES);
      clientBuilder.writeTimeout(2, TimeUnit.MINUTES);
      longTimeoutClient = clientBuilder.build();
    }
    return longTimeoutClient;
  }

  public OkHttpClient getDefaultClient() {
    if (defaultClient == null) {
      final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
      clientBuilder.readTimeout(45, TimeUnit.SECONDS);
      clientBuilder.writeTimeout(45, TimeUnit.SECONDS);

      final File cacheDirectory = new File("/");
      final int cacheMaxSize = 10 * 1024 * 1024;
      clientBuilder.cache(new Cache(cacheDirectory, cacheMaxSize)); // 10 MiB

      clientBuilder.addInterceptor(new POSTCacheInterceptor(getHttpClientCache()));
      clientBuilder.addInterceptor(getUserAgentInterceptor());

      defaultClient = clientBuilder.build();
    }
    return defaultClient;
  }

  public Interceptor getUserAgentInterceptor() {
    if (userAgentInterceptor == null) {
      userAgentInterceptor =
          new UserAgentInterceptor(getAndroidAccountProvider(), getIdsRepository(),
              getConfiguration().getPartnerId(), new DisplayMetrics(),
              AptoideUtils.SystemU.TERMINAL_INFO, AptoideUtils.Core.getDefaultVername());
    }
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
          CacheHelper.build(), new FileUtils(action -> Analytics.File.moveFile(action)),
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
      installManager = new InstallManager(getDownloadManager(),
          new InstallerFactory().create(this, installerType));
      installManagers.put(installerType, installManager);
    }

    return installManager;
  }

  public AptoideAccountManager getAccountManager() {
    if (accountManager == null) {

      final AccountManagerService accountManagerService = new AccountManagerService(
          new BaseBodyInterceptorFactory(getIdsRepository(), getPreferences(),
              getSecurePreferences(), getAptoideMd5sum(), getAptoidePackage()), getAccountFactory(),
          getDefaultClient(), getLongTimeoutClient(), WebService.getDefaultConverter());

      final AndroidAccountDataMigration accountDataMigration =
          new AndroidAccountDataMigration(SecurePreferencesImplementation.getInstance(this),
              PreferenceManager.getDefaultSharedPreferences(this), AccountManager.get(this),
              new SecureCoderDecoder.Builder(this).create(), SQLiteDatabaseHelper.DATABASE_VERSION,
              getDatabasePath(SQLiteDatabaseHelper.DATABASE_NAME).getPath(),
              getConfiguration().getAccountType());

      final AccountDataPersist accountDataPersist =
          new AndroidAccountManagerDataPersist(AccountManager.get(this),
              new DatabaseStoreDataPersist(AccessorFactory.getAccessorFor(Store.class),
                  new DatabaseStoreDataPersist.DatabaseStoreMapper()), getAccountFactory(),
              accountDataMigration, getAndroidAccountProvider(), Schedulers.io());

      accountManager =
          new AptoideAccountManager.Builder().setAccountAnalytics(new AccountEventsAnalytcs())
              .setAccountDataPersist(accountDataPersist)
              .setAccountManagerService(accountManagerService)
              .build();
    }
    return accountManager;
  }

  public AccountFactory getAccountFactory() {
    if (accountFactory == null) {
      accountFactory = new AccountFactory(new SocialAccountFactory(this, getGoogleSignInClient()),
          new AccountService(getBaseBodyInterceptorV3(), getDefaultClient(),
              WebService.getDefaultConverter()));
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
      idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), this,
          Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
    }
    return idsRepository;
  }

  public Preferences getPreferences() {
    if (preferences == null) {
      preferences = new Preferences(PreferenceManager.getDefaultSharedPreferences(this));
    }
    return preferences;
  }

  public cm.aptoide.pt.v8engine.preferences.SecurePreferences getSecurePreferences() {
    if (securePreferences == null) {
      securePreferences = new cm.aptoide.pt.v8engine.preferences.SecurePreferences(
          PreferenceManager.getDefaultSharedPreferences(this), getSecureCoderDecoder());
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
      secureCodeDecoder = new SecureCoderDecoder.Builder(this).create();
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

  private void clearFileCache() {
    FileManager.build(getDownloadManager(), getHttpClientCache())
        .purgeCache()
        .first()
        .toSingle()
        .subscribe(cleanedSize -> Logger.d(TAG,
            "cleaned size: " + AptoideUtils.StringU.formatBytes(cleanedSize, false)),
            err -> CrashReport.getInstance()
                .log(err));
  }

  private void initializeFlurry(Context context, String flurryKey) {
    new FlurryAgent.Builder().withLogEnabled(false)
        .build(context, flurryKey);
  }

  private void sendAppStartToAnalytics(SharedPreferences sPref) {
    Analytics.LocalyticsSessionControl.firstSession(sPref);
    Analytics.Lifecycle.Application.onCreate(this);
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
    return Completable.defer(() -> ABTestManager.getInstance()
        .initialize(getIdsRepository().getUniqueIdentifier())
        .toCompletable());
  }

  private Completable prepareApp(AptoideAccountManager accountManager) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMapCompletable(account -> {
          if (SecurePreferences.isFirstRun()) {

            PreferenceManager.setDefaultValues(this, R.xml.settings, false);

            return setupFirstRun(accountManager).andThen(
                Completable.merge(accountManager.syncCurrentAccount(), createShortcut()));
          }

          return Completable.complete();
        });
  }

  // todo re-factor all this code to proper Rx
  private Completable setupFirstRun(final AptoideAccountManager accountManager) {
    return Completable.defer(() -> {
      SecurePreferences.setFirstRun(false);

      final StoreCredentialsProviderImpl storeCredentials = new StoreCredentialsProviderImpl();

      StoreUtilsProxy proxy =
          new StoreUtilsProxy(getAccountManager(), getBaseBodyInterceptorV7(), storeCredentials,
              AccessorFactory.getAccessorFor(Store.class), getDefaultClient(),
              WebService.getDefaultConverter());

      BaseRequestWithStore.StoreCredentials defaultStoreCredentials =
          storeCredentials.get(getConfiguration().getDefaultStore());

      return generateAptoideUuid().andThen(proxy.addDefaultStore(
          GetStoreMetaRequest.of(defaultStoreCredentials, getBaseBodyInterceptorV7(),
              getDefaultClient(), WebService.getDefaultConverter()), getAccountManager(),
          defaultStoreCredentials)
          .andThen(refreshUpdates()))
          .doOnError(err -> CrashReport.getInstance()
              .log(err));
    });
  }

  public BodyInterceptor<BaseBody> getBaseBodyInterceptorV7() {
    if (baseBodyInterceptorV7 == null) {
      baseBodyInterceptorV7 = new BaseBodyInterceptorV7(getIdsRepository(), getAccountManager(),
          getAdultContent(getSecurePreferences()), getAptoideMd5sum(), getAptoidePackage());
    }
    return baseBodyInterceptorV7;
  }

  public BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> getBaseBodyInterceptorV3() {
    if (baseBodyInterceptorV3 == null) {
      baseBodyInterceptorV3 =
          new BaseBodyInterceptorV3(getAptoideMd5sum(), getAptoidePackage(), getIdsRepository());
    }
    return baseBodyInterceptorV3;
  }

  private String getAptoideMd5sum() {
    if (aptoideMd5sum == null) {
      synchronized (this) {
        if (aptoideMd5sum == null) {
          aptoideMd5sum = caculateMd5Sum();
        }
      }
    }
    return aptoideMd5sum;
  }

  private String caculateMd5Sum() {
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
    return Observable.fromCallable(() -> {
      // remove the current installed apps
      AccessorFactory.getAccessorFor(Installed.class)
          .removeAll();

      // get the installed apps
      List<PackageInfo> installedApps = AptoideUtils.SystemU.getAllInstalledApps();
      Logger.v(TAG, "Found " + installedApps.size() + " user installed apps.");

      // Installed apps are inserted in database based on their firstInstallTime. Older comes first.
      Collections.sort(installedApps,
          (lhs, rhs) -> (int) ((lhs.firstInstallTime - rhs.firstInstallTime) / 1000));

      // return sorted installed apps
      return installedApps;
    })  // transform installation package into Installed table entry and save all the data
        .flatMapIterable(list -> list)
        .map(packageInfo -> new Installed(packageInfo))
        .toList()
        .doOnNext(list -> {
          AccessorFactory.getAccessorFor(Installed.class)
              .insertAll(list);
        })
        .toCompletable();
  }

  private Completable refreshUpdates() {
    return RepositoryFactory.getUpdateRepository(DataProvider.getContext())
        .sync(true);
  }

  /**
   * Use {@link #createShortcut()} using a {@link Completable}
   */
  @Deprecated @Partners public void createShortCut() {
    createAppShortcut();
  }

  private void createAppShortcut() {
    Intent shortcutIntent = new Intent(this, MainActivity.class);
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
}

package cm.aptoide.pt;

import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AccountFactory;
import cm.aptoide.accountmanager.AccountPersistence;
import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.AccountServiceV3;
import cm.aptoide.pt.account.AccountSettingsBodyInterceptorV7;
import cm.aptoide.pt.account.AdultContentAnalytics;
import cm.aptoide.pt.account.AndroidAccountDataMigration;
import cm.aptoide.pt.account.AndroidAccountManagerPersistence;
import cm.aptoide.pt.account.AndroidAccountProvider;
import cm.aptoide.pt.account.DatabaseStoreDataPersist;
import cm.aptoide.pt.account.FacebookSignUpAdapter;
import cm.aptoide.pt.account.GoogleSignUpAdapter;
import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.account.MatureContentPersistence;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.addressbook.AddressBookAnalytics;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.ads.PackageRepositoryVersionCodeProvider;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.AnalyticsDataSaver;
import cm.aptoide.pt.analytics.FirstLaunchAnalytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.TrackerFilter;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.analytics.analytics.AptoideBiAnalytics;
import cm.aptoide.pt.analytics.analytics.AptoideBiEventLogger;
import cm.aptoide.pt.analytics.analytics.EventLogger;
import cm.aptoide.pt.analytics.analytics.FabricEventLogger;
import cm.aptoide.pt.analytics.analytics.FacebookEventLogger;
import cm.aptoide.pt.analytics.analytics.FlurryEventLogger;
import cm.aptoide.pt.analytics.analytics.KnockEventLogger;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.AppViewSimilarAppAnalytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.accessors.RealmToRealmDatabaseMigration;
import cm.aptoide.pt.database.accessors.RollbackAccessor;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.dataprovider.cache.L2Cache;
import cm.aptoide.pt.dataprovider.cache.POSTCacheInterceptor;
import cm.aptoide.pt.dataprovider.cache.POSTCacheKeyAlgorithm;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.AdsApplicationVersionCodeProvider;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.deprecated.SQLiteDatabaseHelper;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadCompleteAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.DownloadInstallationProvider;
import cm.aptoide.pt.download.DownloadMirrorEventInterceptor;
import cm.aptoide.pt.download.PaidAppsDownloadInterceptor;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.file.CacheHelper;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallFabricEvents;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.install.Installer;
import cm.aptoide.pt.install.InstallerAnalytics;
import cm.aptoide.pt.install.InstallerFactory;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.install.RootInstallNotificationEventReceiver;
import cm.aptoide.pt.install.installer.DefaultInstaller;
import cm.aptoide.pt.install.installer.InstallationProvider;
import cm.aptoide.pt.install.installer.RollbackInstaller;
import cm.aptoide.pt.install.installer.RootInstallErrorNotificationFactory;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.install.rollback.RollbackFactory;
import cm.aptoide.pt.install.rollback.RollbackRepository;
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
import cm.aptoide.pt.networking.UserAgentInterceptor;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.preferences.AdultContent;
import cm.aptoide.pt.preferences.LocalPersistenceAdultContent;
import cm.aptoide.pt.preferences.Preferences;
import cm.aptoide.pt.preferences.SecurePreferences;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.repository.DownloadRepository;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.root.RootValueSaver;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.suggestions.SearchSuggestionManager;
import cm.aptoide.pt.search.suggestions.SearchSuggestionRemoteRepository;
import cm.aptoide.pt.search.suggestions.SearchSuggestionService;
import cm.aptoide.pt.search.suggestions.TrendingManager;
import cm.aptoide.pt.search.suggestions.TrendingService;
import cm.aptoide.pt.social.data.CardType;
import cm.aptoide.pt.spotandshare.SpotAndShareAnalytics;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.sync.SyncScheduler;
import cm.aptoide.pt.sync.alarm.AlarmSyncScheduler;
import cm.aptoide.pt.sync.alarm.AlarmSyncService;
import cm.aptoide.pt.sync.alarm.SyncStorage;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.timeline.post.PostAnalytics;
import cm.aptoide.pt.updates.UpdatesAnalytics;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.view.share.NotLoggedInShareAnalytics;
import cn.dreamtobe.filedownloader.OkHttp3Connection;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import dagger.Module;
import dagger.Provides;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;

import static android.content.Context.ALARM_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

@Module public class ApplicationModule {

  private final AptoideApplication application;
  private final String imageCachePath;
  private final String cachePath;
  private final String accountType;
  private final String partnerId;
  private final String marketName;
  private final String extraId;
  private final String aptoidePackage;
  private final String aptoideMd5sum;
  private final LoginPreferences loginPreferences;

  public ApplicationModule(AptoideApplication application, String imageCachePath, String cachePath,
      String accountType, String partnerId, String marketName, String extraId,
      String aptoidePackage, String aptoideMd5sum, LoginPreferences loginPreferences) {
    this.application = application;
    this.imageCachePath = imageCachePath;
    this.cachePath = cachePath;
    this.accountType = accountType;
    this.partnerId = partnerId;
    this.marketName = marketName;
    this.extraId = extraId;
    this.aptoidePackage = aptoidePackage;
    this.aptoideMd5sum = aptoideMd5sum;
    this.loginPreferences = loginPreferences;
  }

  @Singleton @Provides InstallerAnalytics provideInstallerAnalytics(Answers answers,
      AppEventsLogger appEventsLogger, AnalyticsManager analyticsManager) {
    return new InstallFabricEvents(analyticsManager);
  }

  @Singleton @Provides AptoideDownloadManager provideAptoideDownloadManager(DownloadAccessor downloadAccessor,
      @Named("user-agent") Interceptor userAgentInterceptor, CacheHelper cacheHelper,
      AuthenticationPersistence authenticationPersistence,AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    final String apkPath = cachePath + "apks/";
    final String obbPath = cachePath + "obb/";
    final OkHttpClient.Builder httpClientBuilder =
        new OkHttpClient.Builder().addInterceptor(userAgentInterceptor)
            .addInterceptor(new PaidAppsDownloadInterceptor(authenticationPersistence))
            .addInterceptor(new DownloadMirrorEventInterceptor(Analytics.getInstance()))
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS);

    FileUtils.createDir(apkPath);
    FileUtils.createDir(obbPath);
    FileDownloader.init(application,
        new DownloadMgrInitialParams.InitCustomMaker().connectionCreator(
            new OkHttp3Connection.Creator(httpClientBuilder)));

    return new AptoideDownloadManager(downloadAccessor, cacheHelper,
        new FileUtils(action -> Analytics.File.moveFile(action)),
        new DownloadAnalytics(Analytics.getInstance(),
            new DownloadCompleteAnalytics(analyticsManager, navigationTracker)),
        FileDownloader.getImpl(), cachePath, apkPath, obbPath);
  }

  @Singleton @Provides @Named("default") Installer provideDefaultInstaller(
      InstallationProvider installationProvider,
      @Named("default") SharedPreferences sharedPreferences,
      InstalledRepository installedRepository, RootAvailabilityManager rootAvailabilityManager,
      InstallerAnalytics installerAnalytics) {
    return new DefaultInstaller(application.getPackageManager(), installationProvider,
        new FileUtils(), Analytics.getInstance(),
        ToolboxManager.isDebug(sharedPreferences) || BuildConfig.DEBUG, installedRepository, 180000,
        rootAvailabilityManager, sharedPreferences, installerAnalytics);
  }

  @Singleton @Provides @Named("rollback") Installer provideRollbackInstaller(
      @Named("default") Installer defaultInstaller, RollbackRepository rollbackRepository,
      InstallationProvider installationProvider) {
    return new RollbackInstaller(defaultInstaller, rollbackRepository,
        new RollbackFactory(imageCachePath), installationProvider);
  }

  @Singleton @Provides RollbackRepository provideRollbackRepository(
      RollbackAccessor rollbackAccessor) {
    return new RollbackRepository(rollbackAccessor);
  }

  @Singleton @Provides RollbackAccessor provideRollbackAccessor(Database database) {
    return new RollbackAccessor(database);
  }

  @Singleton @Provides InstallationProvider provideInstallationProvider(
      AptoideDownloadManager downloadManager, DownloadAccessor downloadAccessor,
      InstalledRepository installedRepository, Database database) {
    return new DownloadInstallationProvider(downloadManager, downloadAccessor, installedRepository,
        new MinimalAdMapper(), AccessorFactory.getAccessorFor(database, StoredMinimalAd.class));
  }

  @Singleton @Provides CacheHelper provideCacheHelper(
      @Named("default") SharedPreferences defaultSharedPreferences) {
    final List<CacheHelper.FolderToManage> folders = new LinkedList<>();

    long month = DateUtils.DAY_IN_MILLIS;
    folders.add(new CacheHelper.FolderToManage(new File(cachePath), month));
    folders.add(new CacheHelper.FolderToManage(new File(cachePath + "icons/"), month));
    folders.add(new CacheHelper.FolderToManage(
        new File(application.getCacheDir() + "image_manager_disk_cache/"), month));
    return new CacheHelper(ManagerPreferences.getCacheLimit(defaultSharedPreferences), folders,
        new FileUtils());
  }

  @Singleton @Provides AppEventsLogger provideAppEventsLogger() {
    return AppEventsLogger.newLogger(application);
  }

  @Singleton @Provides DownloadRepository provideDownloadRepository(Database database) {
    return new DownloadRepository(new DownloadAccessor(database));
  }

  @Singleton @Provides Answers provideAnswers(Fabric fabric) {
    return fabric.getKit(Answers.class);
  }

  @Singleton @Provides Crashlytics provideCrashlytics(Fabric fabric) {
    return fabric.getKit(Crashlytics.class);
  }

  @Singleton @Provides TwitterCore provideTwitter(Fabric fabric) {
    return fabric.getKit(TwitterCore.class);
  }

  @Singleton @Provides TwitterAuthClient provideTwitterAuthClient() {
    return new TwitterAuthClient();
  }

  @Singleton @Provides Fabric provideFabric() {
    return Fabric.with(application, new Answers(), new Crashlytics.Builder().core(
        new CrashlyticsCore.Builder().disabled(BuildConfig.CRASH_REPORTS_DISABLED)
            .build())
        .build(), new TwitterCore(
        new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET)));
  }

  @Singleton @Provides InstalledRepository provideInstalledRepository(
      InstalledAccessor installedAccessor) {
    return new InstalledRepository(installedAccessor);
  }

  @Singleton @Provides DownloadFactory provideDownloadFactory() {
    return new DownloadFactory(marketName);
  }

  @Singleton @Provides InstalledAccessor provideInstalledAccessor(Database database) {
    return new InstalledAccessor(database);
  }

  @Singleton @Provides DownloadAccessor provideDownloadAccessor(Database database) {
    return new DownloadAccessor(database);
  }

  @Singleton @Provides @Named("user-agent") Interceptor provideUserAgentInterceptor(
      AndroidAccountProvider androidAccountProvider, IdsRepository idsRepository) {
    return new UserAgentInterceptor(androidAccountProvider, idsRepository, partnerId,
        new DisplayMetrics(), AptoideUtils.SystemU.TERMINAL_INFO,
        AptoideUtils.Core.getDefaultVername(application));
  }

  @Singleton @Provides @Named("retrofit-log") Interceptor provideRetrofitLogInterceptor() {
    return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
  }

  @Singleton @Provides IdsRepository provideIdsRepository(
      @Named("default") SharedPreferences defaultSharedPreferences,
      ContentResolver contentResolver) {
    return new IdsRepository(
        SecurePreferencesImplementation.getInstance(application.getApplicationContext(),
            defaultSharedPreferences), application,
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID));
  }

  @Singleton @Provides ContentResolver provideContentResolver() {
    return application.getContentResolver();
  }

  @Singleton @Provides AndroidAccountProvider provideAndroidAccountProvider(
      AccountManager accountManager) {
    return new AndroidAccountProvider(accountManager, accountType, Schedulers.io());
  }

  @Singleton @Provides PermissionManager providePermissionManager() {
    return new PermissionManager();
  }

  @Singleton @Provides AccountManager provideAccountManager() {
    return AccountManager.get(application);
  }

  @Singleton @Provides RootAvailabilityManager provideRootAvailabilityManager(
      @Named("secure") SecurePreferences securePreferences) {
    return new RootAvailabilityManager(new RootValueSaver() {
      final String IS_PHONE_ROOTED = "IS_PHONE_ROOTED";

      @Override public Single<Boolean> isPhoneRoot() {
        return securePreferences.getBoolean(IS_PHONE_ROOTED, false)
            .first()
            .toSingle();
      }

      @Override public Completable save(boolean rootAvailable) {
        return securePreferences.save(IS_PHONE_ROOTED, rootAvailable);
      }
    });
  }

  @Singleton @Provides Map<Integer, Result> provideFragmentNavigatorMap() {
    return new HashMap<>();
  }

  @Singleton @Provides BehaviorRelay<Map<Integer, Result>> provideFragmentNavigatorRelay() {
    return BehaviorRelay.create();
  }

  @Singleton @Provides Resources provideResources() {
    return application.getResources();
  }

  @Singleton @Provides AuthenticationPersistence provideAuthenticationPersistence(
      AndroidAccountProvider androidAccountProvider) {
    return new AuthenticationPersistence(androidAccountProvider,
        ((AccountManager) application.getSystemService(Context.ACCOUNT_SERVICE)));
  }

  @Singleton @Provides @Named("default") SharedPreferences providesDefaultSharedPerefences() {
    return PreferenceManager.getDefaultSharedPreferences(application);
  }

  @Singleton @Provides @Named("secure") SecurePreferences providesSecurePerefences(
      @Named("default") SharedPreferences defaultSharedPreferences,
      SecureCoderDecoder secureCoderDecoder) {
    return new SecurePreferences(defaultSharedPreferences, secureCoderDecoder);
  }

  @Singleton @Provides @Named("secureShared") SharedPreferences providesSecureSharedPreferences(
      @Named("default") SharedPreferences defaultSharedPreferences) {
    return SecurePreferencesImplementation.getInstance(getApplicationContext(),
        defaultSharedPreferences);
  }

  @Singleton @Provides RootInstallationRetryHandler provideRootInstallationRetryHandler() {

    Intent retryActionIntent = new Intent(application, RootInstallNotificationEventReceiver.class);
    retryActionIntent.setAction(RootInstallNotificationEventReceiver.ROOT_INSTALL_RETRY_ACTION);

    PendingIntent retryPendingIntent = PendingIntent.getBroadcast(application, 2, retryActionIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Action action =
        new NotificationCompat.Action(R.drawable.ic_refresh_black_24dp,
            application.getString(R.string.generalscreen_short_root_install_timeout_error_action),
            retryPendingIntent);

    PendingIntent deleteAction = PendingIntent.getBroadcast(application, 3,
        retryActionIntent.setAction(
            RootInstallNotificationEventReceiver.ROOT_INSTALL_DISMISS_ACTION),
        PendingIntent.FLAG_UPDATE_CURRENT);

    int notificationId = 230498;
    return new RootInstallationRetryHandler(notificationId,
        application.getSystemNotificationShower(),
        application.getInstallManager(InstallerFactory.ROLLBACK), PublishRelay.create(), 0,
        application, new RootInstallErrorNotificationFactory(notificationId,
        BitmapFactory.decodeResource(application.getResources(), R.mipmap.ic_launcher), action,
        deleteAction));
  }

  @Singleton @Provides GoogleApiClient provideGoogleApiClient() {
    return new GoogleApiClient.Builder(application).addApi(GOOGLE_SIGN_IN_API,
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
            .requestScopes(new Scope(Scopes.PROFILE))
            .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
            .build())
        .build();
  }

  @Singleton @Provides AptoideAccountManager provideAptoideAccountManager(AdultContent adultContent,
      StoreAccessor storeAccessor, @Named("default") OkHttpClient httpClient,
      @Named("long-timeout") OkHttpClient longTimeoutHttpClient, AccountManager accountManager,
      @Named("default") SharedPreferences defaultSharedPreferences,
      AuthenticationPersistence authenticationPersistence, TokenInvalidator tokenInvalidator,
      @Named("pool-v7")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("web-v7")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorWebV7,
      @Named("multipart") MultipartBodyInterceptor multipartBodyInterceptor,
      AndroidAccountProvider androidAccountProvider, GoogleApiClient googleApiClient,
      @Named("no-authentication-v3") BodyInterceptor<BaseBody> noAuthenticationBodyInterceptorV3,
      ObjectMapper objectMapper, Converter.Factory converterFactory, StoreManager storeManager) {
    FacebookSdk.sdkInitialize(application);
    final AccountFactory accountFactory = new AccountFactory();

    final AccountService accountService =
        new AccountServiceV3(accountFactory, httpClient, longTimeoutHttpClient, converterFactory,
            objectMapper, defaultSharedPreferences, extraId, tokenInvalidator,
            authenticationPersistence, noAuthenticationBodyInterceptorV3, multipartBodyInterceptor,
            bodyInterceptorWebV7, bodyInterceptorPoolV7);

    final AndroidAccountDataMigration accountDataMigration = new AndroidAccountDataMigration(
        SecurePreferencesImplementation.getInstance(application, defaultSharedPreferences),
        defaultSharedPreferences, AccountManager.get(application),
        new SecureCoderDecoder.Builder(application, defaultSharedPreferences).create(),
        SQLiteDatabaseHelper.DATABASE_VERSION,
        application.getDatabasePath(SQLiteDatabaseHelper.DATABASE_NAME)
            .getPath(), application.getAccountType(), BuildConfig.VERSION_NAME, Schedulers.io());

    final AccountPersistence accountPersistence =
        new AndroidAccountManagerPersistence(accountManager,
            new DatabaseStoreDataPersist(storeAccessor,
                new DatabaseStoreDataPersist.DatabaseStoreMapper()), accountFactory,
            accountDataMigration, androidAccountProvider, authenticationPersistence,
            Schedulers.io());

    return new AptoideAccountManager.Builder().setAccountPersistence(
        new MatureContentPersistence(accountPersistence, adultContent))
        .setAccountService(accountService)
        .registerSignUpAdapter(GoogleSignUpAdapter.TYPE,
            new GoogleSignUpAdapter(googleApiClient, loginPreferences))
        .registerSignUpAdapter(FacebookSignUpAdapter.TYPE,
            new FacebookSignUpAdapter(Arrays.asList("email"), LoginManager.getInstance(),
                loginPreferences))
        .setStoreManager(storeManager)
        .build();
  }

  @Singleton @Provides @Named("default") OkHttpClient provideOkHttpClient(L2Cache httpClientCache,
      @Named("user-agent") Interceptor userAgentInterceptor,
      @Named("default") SharedPreferences sharedPreferences,
      @Named("retrofit-log") Interceptor retrofitLogInterceptor) {
    final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    okHttpClientBuilder.readTimeout(45, TimeUnit.SECONDS);
    okHttpClientBuilder.writeTimeout(45, TimeUnit.SECONDS);

    final Cache cache = new Cache(application.getCacheDir(), 10 * 1024 * 1024);
    try {
      // For billing to handle stale data properly the cache should only be stored in memory.
      // In order to make sure it happens we clean up all data persisted in disk when client
      // is first created. It only affects API calls with GET verb.
      cache.evictAll();
    } catch (IOException ignored) {
    }
    okHttpClientBuilder.cache(cache); // 10 MiB
    okHttpClientBuilder.addInterceptor(new POSTCacheInterceptor(httpClientCache));
    okHttpClientBuilder.addInterceptor(userAgentInterceptor);

    if (ToolboxManager.isToolboxEnableRetrofitLogs(sharedPreferences)) {
      okHttpClientBuilder.addInterceptor(retrofitLogInterceptor);
    }

    return okHttpClientBuilder.build();
  }

  @Singleton @Provides @Named("long-timeout") OkHttpClient provideLongTimeoutOkHttpClient(
      @Named("user-agent") Interceptor userAgentInterceptor,
      @Named("default") SharedPreferences sharedPreferences,
      @Named("retrofit-log") Interceptor retrofitLogInterceptor) {
    final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    okHttpClientBuilder.addInterceptor(userAgentInterceptor);
    okHttpClientBuilder.addInterceptor(retrofitLogInterceptor);
    okHttpClientBuilder.connectTimeout(2, TimeUnit.MINUTES);
    okHttpClientBuilder.readTimeout(2, TimeUnit.MINUTES);
    okHttpClientBuilder.writeTimeout(2, TimeUnit.MINUTES);

    if (ToolboxManager.isToolboxEnableRetrofitLogs(sharedPreferences)) {
      okHttpClientBuilder.addInterceptor(retrofitLogInterceptor);
    }

    return okHttpClientBuilder.build();
  }

  @Singleton @Provides @Named("web-socket") OkHttpClient provideWebSocketOkHttpClient(
      @Named("user-agent") Interceptor userAgentInterceptor,
      @Named("default") SharedPreferences sharedPreferences,
      @Named("retrofit-log") Interceptor retrofitLogInterceptor) {
    final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    okHttpClientBuilder.addInterceptor(userAgentInterceptor);
    okHttpClientBuilder.addInterceptor(retrofitLogInterceptor);
    okHttpClientBuilder.connectTimeout(2, TimeUnit.MINUTES);
    okHttpClientBuilder.readTimeout(1, TimeUnit.MINUTES);
    okHttpClientBuilder.writeTimeout(1, TimeUnit.MINUTES);
    okHttpClientBuilder.pingInterval(10, TimeUnit.SECONDS);

    if (ToolboxManager.isToolboxEnableRetrofitLogs(sharedPreferences)) {
      okHttpClientBuilder.addInterceptor(retrofitLogInterceptor);
    }

    return okHttpClientBuilder.build();
  }

  @Singleton @Provides public ObjectMapper provideNonNullObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper;
  }

  @Singleton @Provides L2Cache provideL2Cache() {
    return new L2Cache(new POSTCacheKeyAlgorithm(),
        new File(application.getCacheDir(), AptoideApplication.CACHE_FILE_NAME));
  }

  @Singleton @Provides NotificationAccessor provideNotificationAccessor(Database database) {
    return new NotificationAccessor(database);
  }

  @Singleton @Provides SyncScheduler provideSyncScheduler(SyncStorage syncStorage) {
    return new AlarmSyncScheduler(application, AlarmSyncService.class,
        (AlarmManager) application.getSystemService(ALARM_SERVICE), syncStorage);
  }

  @Singleton @Provides SyncStorage provideSyncStorage() {
    return new SyncStorage(new HashMap<>());
  }

  @Singleton @Provides StoreUtilsProxy provideStoreUtilsProxy(AptoideAccountManager accountManager,
      StoreAccessor storeAccessor, @Named("default") OkHttpClient httpClient,
      @Named("default") SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator,
      @Named("account-settings-pool-v7")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptor) {
    return new StoreUtilsProxy(accountManager, bodyInterceptor,
        new StoreCredentialsProviderImpl(storeAccessor), storeAccessor, httpClient,
        WebService.getDefaultConverter(), tokenInvalidator, sharedPreferences);
  }

  @Singleton @Provides TokenInvalidator provideTokenInvalidator(
      @Named("default") OkHttpClient httpClient,
      @Named("default") SharedPreferences sharedPreferences,
      AuthenticationPersistence authenticationPersistence,
      @Named("no-authentication-v3") BodyInterceptor<BaseBody> bodyInterceptor) {
    return new RefreshTokenInvalidator(bodyInterceptor, httpClient,
        WebService.getDefaultConverter(), sharedPreferences, extraId, new NoOpTokenInvalidator(),
        authenticationPersistence);
  }

  @Singleton @Provides @Named("no-authentication-v3")
  BodyInterceptor<BaseBody> provideNoAuthenticationBodyInterceptorV3(IdsRepository idsRepository) {
    return new NoAuthenticationBodyInterceptorV3(idsRepository, aptoideMd5sum, aptoidePackage);
  }

  @Singleton @Provides @Named("account-settings-pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> provideAccountSettingsBodyInterceptorPoolV7(
      @Named("pool-v7") BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptor,
      AdultContent adultContent) {
    return new AccountSettingsBodyInterceptorV7(bodyInterceptor, adultContent);
  }

  @Singleton @Provides @Named("pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> provideBodyInterceptorPoolV7(
      AuthenticationPersistence authenticationPersistence, IdsRepository idsRepository,
      @Named("default") SharedPreferences sharedPreferences, Resources resources,
      QManager qManager) {
    return new BodyInterceptorV7(idsRepository, authenticationPersistence, aptoideMd5sum,
        aptoidePackage, qManager, Cdn.POOL, sharedPreferences, resources, BuildConfig.VERSION_CODE);
  }

  @Singleton @Provides @Named("multipart") MultipartBodyInterceptor provideMultipartBodyInterceptor(
      IdsRepository idsRepository, AuthenticationPersistence authenticationPersistence,
      RequestBodyFactory requestBodyFactory) {
    return new MultipartBodyInterceptor(idsRepository, requestBodyFactory,
        authenticationPersistence);
  }

  @Singleton @Provides RequestBodyFactory provideRequestBodyFactory() {
    return new RequestBodyFactory();
  }

  @Singleton @Provides @Named("web-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> provideBodyInterceptorWebV7(
      AuthenticationPersistence authenticationPersistence, IdsRepository idsRepository,
      @Named("default") SharedPreferences sharedPreferences, Resources resources,
      QManager qManager) {
    return new BodyInterceptorV7(idsRepository, authenticationPersistence, aptoideMd5sum,
        aptoidePackage, qManager, Cdn.WEB, sharedPreferences, resources, BuildConfig.VERSION_CODE);
  }

  @Singleton @Provides QManager provideQManager(
      @Named("default") SharedPreferences sharedPreferences, Resources resources) {
    return new QManager(sharedPreferences, resources,
        ((ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE)),
        ((WindowManager) application.getSystemService(Context.WINDOW_SERVICE)));
  }

  @Singleton @Provides AdultContent provideLocalAdultContent(Preferences preferences,
      @Named("secure") SecurePreferences securePreferences) {
    return new LocalPersistenceAdultContent(preferences, securePreferences);
  }

  @Singleton @Provides Preferences provideDefaultPreferences(
      @Named("default") SharedPreferences sharedPreferences) {
    return new Preferences(sharedPreferences);
  }

  @Singleton @Provides StoreAccessor provideStoreAccessor(Database database) {
    return new StoreAccessor(database);
  }

  @Singleton @Provides SecureCoderDecoder provideSecureCoderDecoder(
      @Named("default") SharedPreferences sharedPreferences) {
    return new SecureCoderDecoder.Builder(application, sharedPreferences).create();
  }

  @Singleton @Provides StoreRepository provideStoreRepository(StoreAccessor storeAccessor) {
    return new StoreRepository(storeAccessor);
  }

  @Singleton @Provides PageViewsAnalytics providePageViewsAnalytics(AppEventsLogger appEventsLogger,
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    return new PageViewsAnalytics(analyticsManager,
        navigationTracker);
  }

  @Singleton @Provides NavigationTracker provideNavigationTracker() {
    return new NavigationTracker(new ArrayList<>(), new TrackerFilter());
  }

  @Singleton @Provides Database provideDatabase() {
    Realm.init(application);
    final RealmConfiguration realmConfiguration =
        new RealmConfiguration.Builder().name(BuildConfig.REALM_FILE_NAME)
            .schemaVersion(BuildConfig.REALM_SCHEMA_VERSION)
            .migration(new RealmToRealmDatabaseMigration())
            .build();
    Realm.setDefaultConfiguration(realmConfiguration);
    return new Database();
  }

  @Singleton @Provides CallbackManager provideCallbackManager() {
    return new CallbackManagerImpl();
  }

  @Singleton @Provides AccountAnalytics provideAccountAnalytics(NavigationTracker navigationTracker, AnalyticsManager analyticsManager) {
    return new AccountAnalytics(navigationTracker, CrashReport.getInstance(),
        analyticsManager);
  }

  @Singleton @Provides AdultContentAnalytics provideAdultContentAnalytics(AnalyticsManager analyticsManager, NavigationTracker navigationTracker){
      return new AdultContentAnalytics(analyticsManager,navigationTracker);
  }

  @Singleton @Provides StoreManager provideStoreManager(@Named("default") OkHttpClient okHttpClient,
      @Named("multipart") MultipartBodyInterceptor multipartBodyInterceptor,
      @Named("defaulInterceptorV3")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3,
      @Named("account-settings-pool-v7")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> accountSettingsBodyInterceptorPoolV7,
      @Named("default") SharedPreferences defaultSharedPreferences,
      TokenInvalidator tokenInvalidator, RequestBodyFactory requestBodyFactory,
      ObjectMapper nonNullObjectMapper) {
    return new StoreManager(okHttpClient, WebService.getDefaultConverter(),
        multipartBodyInterceptor, bodyInterceptorV3, accountSettingsBodyInterceptorPoolV7,
        defaultSharedPreferences, tokenInvalidator, requestBodyFactory, nonNullObjectMapper);
  }

  @Singleton @Provides AdsRepository provideAdsRepository(IdsRepository idsRepository,
      AptoideAccountManager accountManager, @Named("default") OkHttpClient okHttpClient,
      QManager qManager, @Named("default") SharedPreferences defaultSharedPreferences,
      AdsApplicationVersionCodeProvider adsApplicationVersionCodeProvider) {
    return new AdsRepository(idsRepository, accountManager, okHttpClient,
        WebService.getDefaultConverter(), qManager, defaultSharedPreferences,
        application.getApplicationContext(),
        (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE),
        application.getResources(), adsApplicationVersionCodeProvider,
        AdNetworkUtils::isGooglePlayServicesAvailable, application::getPartnerId,
        new MinimalAdMapper());
  }

  @Singleton @Provides AdsApplicationVersionCodeProvider providesAdsApplicationVersionCodeProvider(
      PackageRepository packageRepository) {
    return new PackageRepositoryVersionCodeProvider(packageRepository,
        application.getPackageName());
  }

  @Singleton @Provides PackageRepository providesPackageRepository() {
    return new PackageRepository(application.getPackageManager());
  }

  @Singleton @Provides @Named("defaulInterceptorV3")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> providesBodyInterceptorV3(
      IdsRepository idsRepository, QManager qManager,
      @Named("default") SharedPreferences defaultSharedPreferences,
      NetworkOperatorManager networkOperatorManager,
      AuthenticationPersistence authenticationPersistence) {
    return new BodyInterceptorV3(idsRepository, aptoideMd5sum, aptoidePackage, qManager,
        defaultSharedPreferences, BodyInterceptorV3.RESPONSE_MODE_JSON, Build.VERSION.SDK_INT,
        networkOperatorManager, authenticationPersistence);
  }

  @Singleton @Provides NetworkOperatorManager providesNetworkOperatorManager() {
    return new NetworkOperatorManager(
        (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE));
  }

  @Singleton @Provides TrendingManager providesTrendingManager(TrendingService trendingService) {
    return new TrendingManager(trendingService);
  }

  @Singleton @Provides Converter.Factory providesConverterFactory() {
    return WebService.getDefaultConverter();
  }

  @Singleton @Provides StoreCredentialsProvider providesStoreCredentialsProvider(
      StoreAccessor storeAccessor) {
    return new StoreCredentialsProviderImpl(storeAccessor);
  }

  @Singleton @Provides TrendingService providesTrendingService(
      StoreCredentialsProvider storeCredentialsProvider,
      @Named("default") SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator,
      Converter.Factory converterFactory, @Named("default") OkHttpClient httpClient,
      @Named("account-settings-pool-v7")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptor) {
    return new TrendingService(storeCredentialsProvider, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Singleton @Provides @Named("ws-prod-base-url") String providesBaseWebServiceTestsUrl() {
    return "http://"
        + cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_SEARCH_HOST
        + "/api/7/";
  }

  @Singleton @Provides @Named("rx") CallAdapter.Factory providesCallAdapterFactory() {
    return RxJavaCallAdapterFactory.create();
  }

  @Singleton @Provides SearchSuggestionManager providesSearchSuggestionManager(
      SearchSuggestionRemoteRepository remoteRepository) {
    return new SearchSuggestionManager(new SearchSuggestionService(remoteRepository),
        Schedulers.io());
  }

  @Singleton @Provides Retrofit providesDefaultRetrofit(@Named("ws-prod-base-url") String baseUrl,
      @Named("default") OkHttpClient httpClient, Converter.Factory converterFactory,
      @Named("rx") CallAdapter.Factory rxCallAdapterFactory) {
    return new Retrofit.Builder().baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(converterFactory)
        .addCallAdapterFactory(rxCallAdapterFactory)
        .build();
  }

  @Singleton @Provides SearchSuggestionRemoteRepository providesSearchSuggestionRemoteRepository(
      Retrofit retrofit) {
    return retrofit.create(SearchSuggestionRemoteRepository.class);
  }

  @Singleton @Provides @Named("Aptoide") EventLogger providesAptoideEventLogger() {
    return new AptoideBiEventLogger(new AptoideBiAnalytics());
  }

  @Singleton @Provides @Named("Facebook") EventLogger providesFacebookEventLogger() {
    return new FacebookEventLogger();
  }

  @Singleton @Provides @Named("Flurry") EventLogger providesFlurryEventLogger() {
    return new FlurryEventLogger();
  }

  @Singleton @Provides @Named("Fabric") EventLogger providesFabricEventLogger() {
    return new FabricEventLogger();
  }

  @Singleton @Provides @Named("Knock") EventLogger providesKnockEventLogger() {
    return new KnockEventLogger();
  }

  @Singleton @Provides AnalyticsManager providesAnalyticsManager(
      @Named("Aptoide") EventLogger aptoideBiEventLogger,
      @Named("Facebook") EventLogger facebookEventLogger,
      @Named("Fabric") EventLogger fabricEventLogger, @Named("Knock") EventLogger knockEventLogger,
      @Named("Flurry") EventLogger flurryEventLogger) {
    List<String> flurryEvents = Arrays.asList(InstallAnalytics.APPLICATION_INSTALL,DownloadCompleteAnalytics.PARTIAL_EVENT_NAME,
        DownloadCompleteAnalytics.EVENT_NAME, AppViewAnalytics.HOME_PAGE_EDITORS_CHOICE_FLURRY,AppViewAnalytics.APP_VIEW_OPEN_FROM, StoreAnalytics.STORES_TAB_OPEN,
        StoreAnalytics.STORES_TAB_INTERACT, StoreAnalytics.STORES_OPEN,StoreAnalytics.STORES_INTERACT, AccountAnalytics.SIGN_UP_EVENT_NAME,AccountAnalytics.LOGIN_EVENT_NAME,
        FirstLaunchAnalytics.FIRST_LAUNCH,AccountAnalytics.LOGIN_SIGN_UP_START_SCREEN, AccountAnalytics.CREATE_USER_PROFILE, AccountAnalytics.PROFILE_SETTINGS,
        AdultContentAnalytics.ADULT_CONTENT);
    for(CardType cardType : CardType.values()){
      flurryEvents.add(cardType.name() + "_" + TimelineAnalytics.APPS_TIMELINE_EVENT);
    }
    return new AnalyticsManager.Builder().addLogger(aptoideBiEventLogger,
        Arrays.asList(PostAnalytics.OPEN_EVENT_NAME,PostAnalytics.POST,AppViewAnalytics.OPEN_APP_VIEW,NotificationAnalytics.NOTIFICATION_EVENT_NAME,
            TimelineAnalytics.OPEN_APP,TimelineAnalytics.UPDATE_APP, TimelineAnalytics.OPEN_STORE, TimelineAnalytics.OPEN_ARTICLE,TimelineAnalytics.LIKE,
            TimelineAnalytics.OPEN_BLOG, TimelineAnalytics.OPEN_VIDEO, TimelineAnalytics.OPEN_CHANNEL, TimelineAnalytics.OPEN_STORE_PROFILE, TimelineAnalytics.COMMENT,
            TimelineAnalytics.SHARE, TimelineAnalytics.SHARE_SEND, TimelineAnalytics.COMMENT_SEND,TimelineAnalytics.FAB,TimelineAnalytics.SCROLLING_EVENT,TimelineAnalytics.OPEN_TIMELINE_EVENT,
            AccountAnalytics.APTOIDE_EVENT_NAME))
        .addLogger(facebookEventLogger, Arrays.asList(PostAnalytics.OPEN_EVENT_NAME,PostAnalytics.NEW_POST_EVENT_NAME,PostAnalytics.POST_COMPLETE,
            InstallAnalytics.APPLICATION_INSTALL, InstallAnalytics.NOTIFICATION_APPLICATION_INSTALL, InstallAnalytics.EDITORS_APPLICATION_INSTALL,
            AddressBookAnalytics.FOLLOW_FRIENDS_CHOOSE_NETWORK, AddressBookAnalytics.FOLLOW_FRIENDS_HOW_TO,
            AddressBookAnalytics.FOLLOW_FRIENDS_APTOIDE_ACCESS, AddressBookAnalytics.FOLLOW_FRIENDS_NEW_CONNECTIONS,
            AddressBookAnalytics.FOLLOW_FRIENDS_SET_MY_PHONENUMBER, DownloadCompleteAnalytics.PARTIAL_EVENT_NAME,
            DownloadCompleteAnalytics.NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME, DownloadCompleteAnalytics.EVENT_NAME,
            SearchAnalytics.SEARCH,SearchAnalytics.NO_RESULTS,SearchAnalytics.APP_CLICK,SearchAnalytics.SEARCH_START,
            AppViewAnalytics.EDITORS_CHOICE_CLICKS,AppViewAnalytics.APP_VIEW_OPEN_FROM, AppViewAnalytics.APP_VIEW_INTERACT, NotificationAnalytics.NOTIFICATION_RECEIVED,
            NotificationAnalytics.NOTIFICATION_PRESSED,NotificationAnalytics.NOTIFICATION_RECEIVED,
            SpotAndShareAnalytics.EVENT_NAME_SPOT_SHARE,SpotAndShareAnalytics.EVENT_NAME_SPOT_SHARE_JOIN,SpotAndShareAnalytics.EVENT_NAME_SPOT_SHARE_CREATE,
            SpotAndShareAnalytics.EVENT_NAME_SPOT_SHARE_SEND_APP, SpotAndShareAnalytics.EVENT_NAME_SPOT_SHARE_RECEIVE_APP,
            TimelineAnalytics.SOCIAL_CARD_PREVIEW, TimelineAnalytics.CARD_ACTION, TimelineAnalytics.TIMELINE_OPENED,TimelineAnalytics.FOLLOW_FRIENDS,
            StoreAnalytics.STORES_TAB_OPEN, StoreAnalytics.STORES_TAB_INTERACT, StoreAnalytics.STORES_OPEN, StoreAnalytics.STORES_INTERACT,AccountAnalytics.SIGN_UP_EVENT_NAME,
            AccountAnalytics.LOGIN_EVENT_NAME, UpdatesAnalytics.UPDATE_EVENT, PageViewsAnalytics.PAGE_VIEW_EVENT, DrawerAnalytics.DRAWER_OPEN_EVENT,
            DrawerAnalytics.DRAWER_INTERACT_EVENT, FirstLaunchAnalytics.FIRST_LAUNCH, InstallFabricEvents.ROOT_V2_COMPLETE, InstallFabricEvents.ROOT_V2_START,
            AppViewSimilarAppAnalytics.APP_VIEW_SIMILAR_APP_SLIDE_IN,AppViewSimilarAppAnalytics.SIMILAR_APP_INTERACT,
            NotLoggedInShareAnalytics.POP_UP_SHARE_TIMELINE, AccountAnalytics.LOGIN_SIGN_UP_START_SCREEN, AccountAnalytics.CREATE_USER_PROFILE, AccountAnalytics.PROFILE_SETTINGS,
            AccountAnalytics.ENTRY))
        .addLogger(fabricEventLogger, Arrays.asList(DownloadCompleteAnalytics.EVENT_NAME, SpotAndShareAnalytics.EVENT_NAME_SPOT_SHARE_PERMISSIONS,
            InstallFabricEvents.ROOT_V2_COMPLETE, InstallFabricEvents.ROOT_V2_START))
        .addLogger(knockEventLogger, Arrays.asList(NotificationAnalytics.NOTIFICATION_TOUCH))
        .addLogger(flurryEventLogger, flurryEvents)
        .addDataSaver(new AnalyticsDataSaver())
        .build();
  }
}

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
import android.os.Environment;
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
import cm.aptoide.accountmanager.AdultContent;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsLogger;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.EventLogger;
import cm.aptoide.analytics.SessionLogger;
import cm.aptoide.analytics.implementation.AptoideBiAnalytics;
import cm.aptoide.analytics.implementation.AptoideBiEventService;
import cm.aptoide.analytics.implementation.EventsPersistence;
import cm.aptoide.analytics.implementation.PageViewsAnalytics;
import cm.aptoide.analytics.implementation.loggers.AptoideBiEventLogger;
import cm.aptoide.analytics.implementation.loggers.FabricEventLogger;
import cm.aptoide.analytics.implementation.loggers.FacebookEventLogger;
import cm.aptoide.analytics.implementation.loggers.FlurryEventLogger;
import cm.aptoide.analytics.implementation.loggers.HttpKnockEventLogger;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.analytics.implementation.network.RetrofitAptoideBiService;
import cm.aptoide.analytics.implementation.persistence.SharedPreferencesSessionPersistence;
import cm.aptoide.analytics.implementation.utils.AnalyticsEventParametersNormalizer;
import cm.aptoide.pt.abtesting.ABTestCenterRepository;
import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.abtesting.ABTestService;
import cm.aptoide.pt.abtesting.AbTestCacheValidator;
import cm.aptoide.pt.abtesting.AbTestSearchRepository;
import cm.aptoide.pt.abtesting.ExperimentModel;
import cm.aptoide.pt.abtesting.RealmExperimentMapper;
import cm.aptoide.pt.abtesting.RealmExperimentPersistence;
import cm.aptoide.pt.abtesting.SearchAbTestService;
import cm.aptoide.pt.abtesting.SearchExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubBannerAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubNativeAdExperiment;
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
import cm.aptoide.pt.account.view.user.NewsletterManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.addressbook.AddressBookAnalytics;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.ads.MoPubAnalytics;
import cm.aptoide.pt.ads.PackageRepositoryVersionCodeProvider;
import cm.aptoide.pt.analytics.FirstLaunchAnalytics;
import cm.aptoide.pt.analytics.TrackerFilter;
import cm.aptoide.pt.analytics.analytics.AnalyticsBodyInterceptorV7;
import cm.aptoide.pt.analytics.analytics.RealmEventMapper;
import cm.aptoide.pt.analytics.analytics.RealmEventPersistence;
import cm.aptoide.pt.app.AdsManager;
import cm.aptoide.pt.app.AppCoinsManager;
import cm.aptoide.pt.app.AppCoinsService;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.CampaignAnalytics;
import cm.aptoide.pt.app.DownloadStateParser;
import cm.aptoide.pt.app.ReviewsManager;
import cm.aptoide.pt.app.ReviewsRepository;
import cm.aptoide.pt.app.ReviewsService;
import cm.aptoide.pt.app.view.donations.DonationsAnalytics;
import cm.aptoide.pt.app.view.donations.DonationsService;
import cm.aptoide.pt.app.view.donations.WalletService;
import cm.aptoide.pt.appview.PreferencesManager;
import cm.aptoide.pt.appview.PreferencesPersister;
import cm.aptoide.pt.autoupdate.AutoUpdateService;
import cm.aptoide.pt.billing.BillingAnalytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.crashreports.CrashlyticsCrashLogger;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.accessors.InstallationAccessor;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.accessors.RealmToRealmDatabaseMigration;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Store;
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
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.deprecated.SQLiteDatabaseHelper;
import cm.aptoide.pt.download.AppValidationAnalytics;
import cm.aptoide.pt.download.AppValidator;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadApkPathsProvider;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.DownloadInstallationProvider;
import cm.aptoide.pt.download.DownloadMirrorEventInterceptor;
import cm.aptoide.pt.download.FileDownloadManagerProvider;
import cm.aptoide.pt.download.Md5Comparator;
import cm.aptoide.pt.download.OemidProvider;
import cm.aptoide.pt.download.PaidAppsDownloadInterceptor;
import cm.aptoide.pt.downloadmanager.AppDownloaderProvider;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadAppFileMapper;
import cm.aptoide.pt.downloadmanager.DownloadAppMapper;
import cm.aptoide.pt.downloadmanager.DownloadStatusMapper;
import cm.aptoide.pt.downloadmanager.DownloadsRepository;
import cm.aptoide.pt.downloadmanager.FileDownloaderProvider;
import cm.aptoide.pt.downloadmanager.RetryFileDownloadManagerProvider;
import cm.aptoide.pt.downloadmanager.RetryFileDownloaderProvider;
import cm.aptoide.pt.editorial.EditorialAnalytics;
import cm.aptoide.pt.editorial.EditorialService;
import cm.aptoide.pt.file.CacheHelper;
import cm.aptoide.pt.home.AdMapper;
import cm.aptoide.pt.home.BannerRepository;
import cm.aptoide.pt.home.BottomNavigationAnalytics;
import cm.aptoide.pt.home.BundleDataSource;
import cm.aptoide.pt.home.BundlesRepository;
import cm.aptoide.pt.home.BundlesResponseMapper;
import cm.aptoide.pt.home.HomeAnalytics;
import cm.aptoide.pt.home.RemoteBundleDataSource;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.impressions.ImpressionManager;
import cm.aptoide.pt.impressions.ImpressionService;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallFabricEvents;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.install.Installer;
import cm.aptoide.pt.install.InstallerAnalytics;
import cm.aptoide.pt.install.InstallerFactory;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.install.RootInstallNotificationEventReceiver;
import cm.aptoide.pt.install.installer.DefaultInstaller;
import cm.aptoide.pt.install.installer.InstallationProvider;
import cm.aptoide.pt.install.installer.RootInstallErrorNotificationFactory;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.link.AptoideInstallParser;
import cm.aptoide.pt.logger.AnalyticsLogcatLogger;
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
import cm.aptoide.pt.networking.UserAgentInterceptorV8;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.preferences.Preferences;
import cm.aptoide.pt.preferences.SecurePreferences;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.promotions.CaptchaService;
import cm.aptoide.pt.promotions.PromotionViewAppMapper;
import cm.aptoide.pt.promotions.PromotionsAnalytics;
import cm.aptoide.pt.promotions.PromotionsManager;
import cm.aptoide.pt.promotions.PromotionsPreferencesManager;
import cm.aptoide.pt.promotions.PromotionsService;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.repository.request.RewardAppCoinsAppsRepository;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.root.RootValueSaver;
import cm.aptoide.pt.search.SearchHostProvider;
import cm.aptoide.pt.search.SearchManager;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.suggestions.SearchSuggestionManager;
import cm.aptoide.pt.search.suggestions.SearchSuggestionRemoteRepository;
import cm.aptoide.pt.search.suggestions.SearchSuggestionService;
import cm.aptoide.pt.search.suggestions.TrendingManager;
import cm.aptoide.pt.search.suggestions.TrendingService;
import cm.aptoide.pt.social.data.ReadPostsPersistence;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.sync.SyncScheduler;
import cm.aptoide.pt.sync.alarm.AlarmSyncScheduler;
import cm.aptoide.pt.sync.alarm.AlarmSyncService;
import cm.aptoide.pt.sync.alarm.SyncStorage;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.updates.UpdateRepository;
import cm.aptoide.pt.updates.UpdatesAnalytics;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.AppCenterRepository;
import cm.aptoide.pt.view.app.AppService;
import cm.aptoide.pt.view.settings.SupportEmailProvider;
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
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetClient;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
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
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static android.content.Context.ALARM_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

@Module public class ApplicationModule {

  private static final String DONATIONS_URL = "https://api.blockchainds.com/";

  private final AptoideApplication application;
  private final String aptoideMd5sum;

  public ApplicationModule(AptoideApplication application, String aptoideMd5sum) {
    this.application = application;
    this.aptoideMd5sum = aptoideMd5sum;
  }

  @Singleton @Provides InstallManager providesInstallManager(
      AptoideDownloadManager aptoideDownloadManager, InstallerAnalytics installerAnalytics,
      RootAvailabilityManager rootAvailabilityManager,
      @Named("default") SharedPreferences defaultSharedPreferences,
      @Named("secureShared") SharedPreferences secureSharedPreferences,
      DownloadsRepository downloadsRepository, InstalledRepository installedRepository,
      @Named("cachePath") String cachePath, @Named("apkPath") String apkPath,
      @Named("obbPath") String obbPath, DownloadAnalytics downloadAnalytics) {

    return new InstallManager(application, aptoideDownloadManager,
        new InstallerFactory(new MinimalAdMapper(), installerAnalytics).create(application),
        rootAvailabilityManager, defaultSharedPreferences, secureSharedPreferences,
        downloadsRepository, installedRepository, cachePath, apkPath, obbPath, new FileUtils());
  }

  @Singleton @Provides InstallerAnalytics providesInstallerAnalytics(
      AnalyticsManager analyticsManager, InstallAnalytics installAnalytics,
      @Named("default") SharedPreferences sharedPreferences,
      RootAvailabilityManager rootAvailabilityManager) {
    return new InstallFabricEvents(analyticsManager, installAnalytics, sharedPreferences,
        rootAvailabilityManager);
  }

  @Singleton @Provides DownloadAnalytics providesDownloadAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker,
      ConnectivityManager connectivityManager, TelephonyManager providesSystemService) {
    return new DownloadAnalytics(connectivityManager, providesSystemService, navigationTracker,
        analyticsManager);
  }

  @Singleton @Provides UpdatesAnalytics providesUpdatesAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    return new UpdatesAnalytics(analyticsManager, navigationTracker);
  }

  @Singleton @Provides CampaignAnalytics providesCampaignAnalytics(
      AnalyticsManager analyticsManager) {
    return new CampaignAnalytics(new HashMap<>(), analyticsManager);
  }

  @Singleton @Provides TelephonyManager providesTelephonyManager() {
    return (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);
  }

  @Singleton @Provides ConnectivityManager providesConnectivityManager() {
    return (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  @Singleton @Provides InstallAnalytics provideInstallAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker, ConnectivityManager connectivityManager,
      TelephonyManager telephonyManager) {
    return new InstallAnalytics(CrashReport.getInstance(), analyticsManager, navigationTracker,
        new HashMap<>(), connectivityManager, telephonyManager);
  }

  @Singleton @Provides @Named("aptoidePackage") String provideAptoidePackage() {
    return BuildConfig.APPLICATION_ID;
  }

  @Singleton @Provides @Named("apkPath") String provideApkPath(
      @Named("cachePath") String cachePath) {
    return cachePath + "apks/";
  }

  @Singleton @Provides @Named("obbPath") String provideObbPath(
      @Named("cachePath") String cachePath) {
    return cachePath + "obb/";
  }

  @Singleton @Provides AptoideDownloadManager provideAptoideDownloadManager(
      DownloadsRepository downloadsRepository, DownloadStatusMapper downloadStatusMapper,
      @Named("cachePath") String cachePath, DownloadAppMapper downloadAppMapper,
      AppDownloaderProvider appDownloaderProvider, @Named("apkPath") String apkPath,
      @Named("obbPath") String obbPath, DownloadAnalytics downloadAnalytics) {
    FileUtils.createDir(apkPath);
    FileUtils.createDir(obbPath);
    return new AptoideDownloadManager(downloadsRepository, downloadStatusMapper, cachePath,
        downloadAppMapper, appDownloaderProvider, downloadAnalytics);
  }

  @Provides @Singleton DownloadAppFileMapper providesDownloadAppFileMapper() {
    return new DownloadAppFileMapper();
  }

  @Singleton @Provides DownloadAppMapper providesDownloadAppMapper(
      DownloadAppFileMapper downloadAppFileMapper) {
    return new DownloadAppMapper(downloadAppFileMapper);
  }

  @Provides @Singleton FileDownloaderProvider providesFileDownloaderProvider(
      @Named("cachePath") String cachePath, @Named("user-agent") Interceptor userAgentInterceptor,
      AuthenticationPersistence authenticationPersistence, DownloadAnalytics downloadAnalytics,
      InstallAnalytics installAnalytics, Md5Comparator md5Comparator) {

    final OkHttpClient.Builder httpClientBuilder =
        new OkHttpClient.Builder().addInterceptor(userAgentInterceptor)
            .addInterceptor(new PaidAppsDownloadInterceptor(authenticationPersistence))
            .addInterceptor(new DownloadMirrorEventInterceptor(downloadAnalytics, installAnalytics))
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS);
    FileDownloader.init(application,
        new DownloadMgrInitialParams.InitCustomMaker().connectionCreator(
            new OkHttp3Connection.Creator(httpClientBuilder)));

    return new FileDownloadManagerProvider(cachePath, FileDownloader.getImpl(), md5Comparator);
  }

  @Singleton @Provides Md5Comparator providesMd5Comparator(@Named("cachePath") String cachePath) {
    return new Md5Comparator(cachePath);
  }

  @Singleton @Provides AppDownloaderProvider providesAppDownloaderProvider(
      RetryFileDownloaderProvider fileDownloaderProvider, DownloadAnalytics downloadAnalytics) {
    return new AppDownloaderProvider(fileDownloaderProvider, downloadAnalytics);
  }

  @Singleton @Provides RetryFileDownloaderProvider providesRetryFileDownloaderProvider(
      FileDownloaderProvider fileDownloaderProvider) {
    return new RetryFileDownloadManagerProvider(fileDownloaderProvider);
  }

  @Singleton @Provides DownloadsRepository provideDownloadsRepository(
      DownloadAccessor downloadAccessor) {
    return new DownloadsRepository(downloadAccessor);
  }

  @Singleton @Provides DownloadStatusMapper downloadStatusMapper() {
    return new DownloadStatusMapper();
  }

  @Singleton @Provides @Named("default") Installer provideDefaultInstaller(
      InstallationProvider installationProvider,
      @Named("default") SharedPreferences sharedPreferences,
      InstalledRepository installedRepository, RootAvailabilityManager rootAvailabilityManager,
      InstallerAnalytics installerAnalytics) {
    return new DefaultInstaller(application.getPackageManager(), installationProvider,
        new FileUtils(), ToolboxManager.isDebug(sharedPreferences) || BuildConfig.DEBUG,
        installedRepository, 180000, rootAvailabilityManager, sharedPreferences,
        installerAnalytics);
  }

  @Singleton @Provides InstallationProvider provideInstallationProvider(
      AptoideDownloadManager downloadManager, DownloadAccessor downloadAccessor,
      InstalledRepository installedRepository, Database database) {
    return new DownloadInstallationProvider(downloadManager, downloadAccessor, installedRepository,
        new MinimalAdMapper(), AccessorFactory.getAccessorFor(database, StoredMinimalAd.class));
  }

  @Singleton @Provides CacheHelper provideCacheHelper(
      @Named("default") SharedPreferences defaultSharedPreferences,
      @Named("cachePath") String cachePath) {
    final List<CacheHelper.FolderToManage> folders = new LinkedList<>();

    long day = DateUtils.DAY_IN_MILLIS;
    folders.add(new CacheHelper.FolderToManage(new File(cachePath), day));
    folders.add(new CacheHelper.FolderToManage(new File(cachePath + "icons/"), day));
    folders.add(new CacheHelper.FolderToManage(
        new File(application.getCacheDir() + "image_manager_disk_cache/"), day));
    return new CacheHelper(ManagerPreferences.getCacheLimit(defaultSharedPreferences), folders,
        new FileUtils());
  }

  @Singleton @Provides AppEventsLogger provideAppEventsLogger() {
    return AppEventsLogger.newLogger(application);
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

  @Singleton @Provides OemidProvider providesOemidProvider() {
    return new OemidProvider();
  }

  @Singleton @Provides DownloadApkPathsProvider downloadApkPathsProvider(
      OemidProvider oemidProvider) {
    return new DownloadApkPathsProvider(oemidProvider);
  }

  @Singleton @Provides AppValidationAnalytics providesAppValidationAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    return new AppValidationAnalytics(analyticsManager, navigationTracker);
  }

  @Singleton @Provides AppValidator providesAppValidator(
      AppValidationAnalytics appValidationAnalytics) {
    return new AppValidator(appValidationAnalytics);
  }

  @Singleton @Provides DownloadFactory provideDownloadFactory(
      @Named("marketName") String marketName, DownloadApkPathsProvider downloadApkPathsProvider,
      @Named("cachePath") String cachePath, AppValidator appValidator) {
    return new DownloadFactory(marketName, downloadApkPathsProvider, cachePath, appValidator);
  }

  @Singleton @Provides InstalledAccessor provideInstalledAccessor(Database database,
      InstallationAccessor installationAccessor) {
    return new InstalledAccessor(database, installationAccessor);
  }

  @Singleton @Provides InstallationAccessor providesInstallationAccessor(Database database) {
    return new InstallationAccessor(database);
  }

  @Singleton @Provides DownloadAccessor provideDownloadAccessor(Database database) {
    return new DownloadAccessor(database);
  }

  @Singleton @Provides @Named("user-agent") Interceptor provideUserAgentInterceptor(
      AndroidAccountProvider androidAccountProvider, IdsRepository idsRepository,
      @Named("partnerID") String partnerId) {
    return new UserAgentInterceptor(androidAccountProvider, idsRepository, partnerId,
        new DisplayMetrics(), AptoideUtils.SystemU.TERMINAL_INFO,
        AptoideUtils.Core.getDefaultVername(application));
  }

  @Singleton @Provides @Named("user-agent-v8") Interceptor provideUserAgentInterceptorV8(
      IdsRepository idsRepository, @Named("aptoidePackage") String aptoidePackage) {
    return new UserAgentInterceptorV8(idsRepository, AptoideUtils.SystemU.getRelease(),
        Build.VERSION.SDK_INT, AptoideUtils.SystemU.getModel(), AptoideUtils.SystemU.getProduct(),
        System.getProperty("os.arch"), new DisplayMetrics(),
        AptoideUtils.Core.getDefaultVername(application)
            .replace("aptoide-", ""), aptoidePackage, aptoideMd5sum, BuildConfig.VERSION_CODE);
  }

  @Singleton @Provides @Named("retrofit-log") Interceptor provideRetrofitLogInterceptor() {
    return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
  }

  @Singleton @Provides IdsRepository provideIdsRepository(
      @Named("default") SharedPreferences defaultSharedPreferences,
      ContentResolver contentResolver) {
    Context applicationContext = application.getApplicationContext();
    return new IdsRepository(
        SecurePreferencesImplementation.getInstance(applicationContext, defaultSharedPreferences),
        applicationContext, Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID));
  }

  @Singleton @Provides ContentResolver provideContentResolver() {
    return application.getContentResolver();
  }

  @Singleton @Provides AndroidAccountProvider provideAndroidAccountProvider(
      AccountManager accountManager, @Named("accountType") String accountType) {
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

  @Singleton @Provides @Named("aptoide-theme") String providesAptoideTheme() {
    return BuildConfig.APTOIDE_THEME;
  }

  @Singleton @Provides RootInstallationRetryHandler provideRootInstallationRetryHandler(
      InstallManager installManager) {

    Intent retryActionIntent = new Intent(application, RootInstallNotificationEventReceiver.class);
    retryActionIntent.setAction(RootInstallNotificationEventReceiver.ROOT_INSTALL_RETRY_ACTION);

    PendingIntent retryPendingIntent = PendingIntent.getBroadcast(application, 2, retryActionIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Action action =
        new NotificationCompat.Action(R.drawable.ic_refresh_action_black,
            application.getString(R.string.generalscreen_short_root_install_timeout_error_action),
            retryPendingIntent);

    PendingIntent deleteAction = PendingIntent.getBroadcast(application, 3,
        retryActionIntent.setAction(
            RootInstallNotificationEventReceiver.ROOT_INSTALL_DISMISS_ACTION),
        PendingIntent.FLAG_UPDATE_CURRENT);

    int notificationId = 230498;
    return new RootInstallationRetryHandler(notificationId,
        application.getSystemNotificationShower(), installManager, PublishRelay.create(), 0,
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
      StoreAccessor storeAccessor, AccountManager accountManager,
      @Named("default") SharedPreferences defaultSharedPreferences,
      AuthenticationPersistence authenticationPersistence,
      AndroidAccountProvider androidAccountProvider, GoogleApiClient googleApiClient,
      StoreManager storeManager, AccountService accountService, AccountFactory accountFactory,
      LoginPreferences loginPreferences) {
    FacebookSdk.sdkInitialize(application);

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
        .setAdultService(adultContent)
        .registerSignUpAdapter(GoogleSignUpAdapter.TYPE,
            new GoogleSignUpAdapter(googleApiClient, loginPreferences))
        .registerSignUpAdapter(FacebookSignUpAdapter.TYPE,
            new FacebookSignUpAdapter(Arrays.asList("email"), LoginManager.getInstance(),
                loginPreferences))
        .setStoreManager(storeManager)
        .build();
  }

  @Singleton @Provides AccountFactory provideAccountFactory() {
    return new AccountFactory();
  }

  @Singleton @Provides AccountService provideAccountService(
      @Named("default") OkHttpClient httpClient,
      @Named("long-timeout") OkHttpClient longTimeoutHttpClient,
      @Named("default") SharedPreferences defaultSharedPreferences,
      AuthenticationPersistence authenticationPersistence, TokenInvalidator tokenInvalidator,
      @Named("pool-v7")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("web-v7")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorWebV7,
      @Named("multipart") MultipartBodyInterceptor multipartBodyInterceptor,
      @Named("no-authentication-v3") BodyInterceptor<BaseBody> noAuthenticationBodyInterceptorV3,
      @Named("defaultInterceptorV3")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3,
      @Named("default") ObjectMapper objectMapper, Converter.Factory converterFactory,
      @Named("extraID") String extraId, AccountFactory accountFactory) {
    return new AccountServiceV3(accountFactory, httpClient, longTimeoutHttpClient, converterFactory,
        objectMapper, defaultSharedPreferences, extraId, tokenInvalidator,
        authenticationPersistence, noAuthenticationBodyInterceptorV3, bodyInterceptorV3,
        multipartBodyInterceptor, bodyInterceptorWebV7, bodyInterceptorPoolV7);
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

  @Singleton @Provides @Named("v8") OkHttpClient provideV8OkHttpClient(L2Cache httpClientCache,
      @Named("user-agent-v8") Interceptor userAgentInterceptorV8,
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
    okHttpClientBuilder.addInterceptor(userAgentInterceptorV8);

    if (ToolboxManager.isToolboxEnableRetrofitLogs(sharedPreferences)) {
      okHttpClientBuilder.addInterceptor(retrofitLogInterceptor);
    }

    return okHttpClientBuilder.build();
  }

  @Singleton @Provides @Named("default") ObjectMapper provideNonNullObjectMapper() {
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
      @Named("no-authentication-v3") BodyInterceptor<BaseBody> bodyInterceptor,
      @Named("extraID") String extraId) {
    return new RefreshTokenInvalidator(bodyInterceptor, httpClient,
        WebService.getDefaultConverter(), sharedPreferences, extraId, new NoOpTokenInvalidator(),
        authenticationPersistence, PublishSubject.create());
  }

  @Singleton @Provides InvalidRefreshTokenLogoutManager provideInvalidRefreshTokenLogoutManager(
      AptoideAccountManager aptoideAccountManager, TokenInvalidator refreshTokenInvalidator) {
    return new InvalidRefreshTokenLogoutManager(aptoideAccountManager,
        ((RefreshTokenInvalidator) refreshTokenInvalidator));
  }

  @Singleton @Provides @Named("no-authentication-v3")
  BodyInterceptor<BaseBody> provideNoAuthenticationBodyInterceptorV3(IdsRepository idsRepository,
      @Named("aptoidePackage") String aptoidePackage) {
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
      @Named("default") SharedPreferences sharedPreferences, Resources resources, QManager qManager,
      @Named("aptoidePackage") String aptoidePackage) {
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

  /**
   * BaseBodyInterceptor for v7 ws calls with CDN = web configuration
   */
  @Singleton @Provides @Named("web-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> provideBodyInterceptorWebV7(
      AuthenticationPersistence authenticationPersistence, IdsRepository idsRepository,
      @Named("default") SharedPreferences sharedPreferences, Resources resources, QManager qManager,
      @Named("aptoidePackage") String aptoidePackage) {
    return new BodyInterceptorV7(idsRepository, authenticationPersistence, aptoideMd5sum,
        aptoidePackage, qManager, Cdn.WEB, sharedPreferences, resources, BuildConfig.VERSION_CODE);
  }

  @Singleton @Provides @Named("analytics-interceptor")
  AnalyticsBodyInterceptorV7 provideAnalyticsBodyInterceptorV7(
      AuthenticationPersistence authenticationPersistence, IdsRepository idsRepository,
      @Named("default") SharedPreferences sharedPreferences, Resources resources, QManager qManager,
      @Named("aptoidePackage") String aptoidePackage) {
    return new AnalyticsBodyInterceptorV7(idsRepository, authenticationPersistence, aptoideMd5sum,
        aptoidePackage, resources, BuildConfig.VERSION_CODE, qManager, sharedPreferences);
  }

  @Singleton @Provides QManager provideQManager(
      @Named("default") SharedPreferences sharedPreferences, Resources resources,
      WindowManager windowManager) {
    return new QManager(sharedPreferences, resources,
        ((ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE)), windowManager);
  }

  @Singleton @Provides WindowManager provideWindowManager() {
    return ((WindowManager) application.getSystemService(Context.WINDOW_SERVICE));
  }

  @Singleton @Provides Preferences provideDefaultPreferences(
      @Named("default") SharedPreferences sharedPreferences) {
    return new Preferences(sharedPreferences);
  }

  @Singleton @Provides StoreAccessor provideStoreAccessor(Database database) {
    return new StoreAccessor(database);
  }

  @Singleton @Provides UpdateAccessor providesUpdateAccessor(Database database) {
    return new UpdateAccessor(database);
  }

  @Singleton @Provides SecureCoderDecoder provideSecureCoderDecoder(
      @Named("default") SharedPreferences sharedPreferences) {
    return new SecureCoderDecoder.Builder(application, sharedPreferences).create();
  }

  @Singleton @Provides StoreRepository provideStoreRepository(StoreAccessor storeAccessor) {
    return new StoreRepository(storeAccessor);
  }

  @Singleton @Provides PageViewsAnalytics providePageViewsAnalytics(
      AnalyticsManager analyticsManager) {
    return new PageViewsAnalytics(analyticsManager);
  }

  @Singleton @Provides NotificationAnalytics provideNotificationAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    return new NotificationAnalytics(new AptoideInstallParser(), analyticsManager,
        navigationTracker);
  }

  @Singleton @Provides SearchAnalytics providesSearchAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    return new SearchAnalytics(analyticsManager, navigationTracker);
  }

  @Singleton @Provides AnalyticsLogger providesAnalyticsDebugLogger() {
    return new AnalyticsLogcatLogger();
  }

  @Singleton @Provides NavigationTracker provideNavigationTracker(
      PageViewsAnalytics pageViewsAnalytics, AnalyticsLogger logger) {
    return new NavigationTracker(new ArrayList<>(), new TrackerFilter(), pageViewsAnalytics,
        logger);
  }

  @Singleton @Provides Database provideDatabase() {
    Realm.init(application);
    final RealmConfiguration realmConfiguration =
        new RealmConfiguration.Builder().name(BuildConfig.REALM_FILE_NAME)
            .schemaVersion(BuildConfig.REALM_SCHEMA_VERSION)
            .migration(new RealmToRealmDatabaseMigration(application.getApplicationContext()))
            .build();
    Realm.setDefaultConfiguration(realmConfiguration);
    return new Database();
  }

  @Singleton @Provides CallbackManager provideCallbackManager() {
    return new CallbackManagerImpl();
  }

  @Singleton @Provides AccountAnalytics provideAccountAnalytics(NavigationTracker navigationTracker,
      AnalyticsManager analyticsManager) {
    return new AccountAnalytics(navigationTracker, CrashReport.getInstance(), analyticsManager);
  }

  @Singleton @Provides AdultContentAnalytics provideAdultContentAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    return new AdultContentAnalytics(analyticsManager, navigationTracker);
  }

  @Singleton @Provides DeepLinkAnalytics provideDeepLinkAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    return new DeepLinkAnalytics(analyticsManager, navigationTracker);
  }

  @Singleton @Provides StoreManager provideStoreManager(@Named("default") OkHttpClient okHttpClient,
      @Named("multipart") MultipartBodyInterceptor multipartBodyInterceptor,
      @Named("defaultInterceptorV3")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3,
      @Named("account-settings-pool-v7")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> accountSettingsBodyInterceptorPoolV7,
      @Named("default") SharedPreferences defaultSharedPreferences,
      TokenInvalidator tokenInvalidator, RequestBodyFactory requestBodyFactory,
      @Named("default") ObjectMapper nonNullObjectMapper, StoreRepository storeRepository) {
    return new StoreManager(okHttpClient, WebService.getDefaultConverter(),
        multipartBodyInterceptor, bodyInterceptorV3, accountSettingsBodyInterceptorPoolV7,
        defaultSharedPreferences, tokenInvalidator, requestBodyFactory, nonNullObjectMapper,
        storeRepository);
  }

  @Singleton @Provides AdsRepository provideAdsRepository(IdsRepository idsRepository,
      AptoideAccountManager accountManager, @Named("default") OkHttpClient okHttpClient,
      QManager qManager, @Named("default") SharedPreferences defaultSharedPreferences,
      AdsApplicationVersionCodeProvider adsApplicationVersionCodeProvider,
      ConnectivityManager connectivityManager) {
    return new AdsRepository(idsRepository, accountManager, okHttpClient,
        WebService.getDefaultConverter(), qManager, defaultSharedPreferences,
        application.getApplicationContext(), connectivityManager, application.getResources(),
        adsApplicationVersionCodeProvider, AdNetworkUtils::isGooglePlayServicesAvailable,
        application::getPartnerId, new MinimalAdMapper());
  }

  @Singleton @Provides RewardAppCoinsAppsRepository providesRewardAppCoinsAppsRepository(
      @Named("default") OkHttpClient okHttpClient, @Named("pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> baseBodyBodyInterceptor,
      TokenInvalidator tokenInvalidator, @Named("default") SharedPreferences sharedPreferences,
      InstallManager installManager) {
    return new RewardAppCoinsAppsRepository(okHttpClient, WebService.getDefaultConverter(),
        baseBodyBodyInterceptor, tokenInvalidator, sharedPreferences, installManager);
  }

  @Singleton @Provides AdsApplicationVersionCodeProvider providesAdsApplicationVersionCodeProvider(
      PackageRepository packageRepository) {
    return new PackageRepositoryVersionCodeProvider(packageRepository,
        application.getPackageName());
  }

  @Singleton @Provides PackageRepository providesPackageRepository() {
    return new PackageRepository(application.getPackageManager());
  }

  @Singleton @Provides @Named("defaultInterceptorV3")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> providesBodyInterceptorV3(
      IdsRepository idsRepository, QManager qManager,
      @Named("default") SharedPreferences defaultSharedPreferences,
      NetworkOperatorManager networkOperatorManager,
      AuthenticationPersistence authenticationPersistence,
      @Named("aptoidePackage") String aptoidePackage) {
    return new BodyInterceptorV3(idsRepository, aptoideMd5sum, aptoidePackage, qManager,
        defaultSharedPreferences, BodyInterceptorV3.RESPONSE_MODE_JSON, Build.VERSION.SDK_INT,
        networkOperatorManager, authenticationPersistence);
  }

  @Singleton @Provides NetworkOperatorManager providesNetworkOperatorManager(
      TelephonyManager telephonyManager) {
    return new NetworkOperatorManager(telephonyManager);
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

  @Singleton @Provides @Named("ws-prod-suggestions-base-url") String provideSearchBaseUrl(
      @Named("default") SharedPreferences sharedPreferences) {
    return new SearchHostProvider(ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences),
        cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_SCHEME,
        cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_SEARCH_HOST,
        cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_SEARCH_SSL_HOST).getSearchHost();
  }

  @Singleton @Provides @Named("rx") CallAdapter.Factory providesCallAdapterFactory() {
    return RxJavaCallAdapterFactory.create();
  }

  @Singleton @Provides SearchManager providesSearchManager(@Named("pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> baseBodyBodyInterceptor,
      @Named("default") SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator,
      @Named("default") OkHttpClient okHttpClient, Converter.Factory converterFactory,
      Database database, AdsRepository adsRepository, AptoideAccountManager accountManager,
      MoPubBannerAdExperiment moPubBannerAdExperiment,
      MoPubNativeAdExperiment moPubNativeAdExperiment, SearchExperiment searchExperiment) {
    return new SearchManager(sharedPreferences, tokenInvalidator, baseBodyBodyInterceptor,
        okHttpClient, converterFactory, StoreUtils.getSubscribedStoresAuthMap(
        AccessorFactory.getAccessorFor(database, Store.class)), adsRepository, database,
        accountManager, moPubBannerAdExperiment, moPubNativeAdExperiment, searchExperiment);
  }

  @Singleton @Provides SearchSuggestionManager providesSearchSuggestionManager(
      SearchSuggestionRemoteRepository remoteRepository) {
    return new SearchSuggestionManager(new SearchSuggestionService(remoteRepository),
        Schedulers.io());
  }

  @Singleton @Provides Retrofit providesSearchSuggestionsRetrofit(
      @Named("ws-prod-suggestions-base-url") String baseUrl,
      @Named("default") OkHttpClient httpClient, Converter.Factory converterFactory,
      @Named("rx") CallAdapter.Factory rxCallAdapterFactory) {
    return new Retrofit.Builder().baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(converterFactory)
        .addCallAdapterFactory(rxCallAdapterFactory)
        .build();
  }

  @Singleton @Provides @Named("base-host") String providesBaseHost(
      @Named("default") SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_V7_HOST
        + "/api/7/";
  }

  @Singleton @Provides @Named("base-secondary-host") String providesBaseSecondaryHost(
      @Named("default") SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_READ_V7_HOST
        + "/api/7/";
  }

  @Singleton @Provides @Named("retrofit-v7") Retrofit providesV7Retrofit(
      @Named("base-host") String baseHost, @Named("default") OkHttpClient httpClient,
      Converter.Factory converterFactory, @Named("rx") CallAdapter.Factory rxCallAdapterFactory) {
    return new Retrofit.Builder().baseUrl(baseHost)
        .client(httpClient)
        .addCallAdapterFactory(rxCallAdapterFactory)
        .addConverterFactory(converterFactory)
        .build();
  }

  @Singleton @Provides @Named("ab-testing-base-host") String providesABTestingBaseHost(
      @Named("default") SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_AB_TESTING_HOST
        + "/api/v1/";
  }

  @Singleton @Provides @Named("apichain-bds-base-host") String providesApichainBdsBaseHost(
      @Named("default") SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_APICHAIN_BDS_HOST;
  }

  @Singleton @Provides @Named("retrofit-AB") Retrofit providesABRetrofit(
      @Named("ab-testing-base-host") String baseHost, @Named("default") OkHttpClient httpClient,
      Converter.Factory converterFactory, @Named("rx") CallAdapter.Factory rxCallAdapterFactory) {
    return new Retrofit.Builder().baseUrl(baseHost)
        .client(httpClient)
        .addCallAdapterFactory(rxCallAdapterFactory)
        .addConverterFactory(converterFactory)
        .build();
  }

  @Singleton @Provides @Named("retrofit-donations") Retrofit providesDonationsRetrofit(
      @Named("v8") OkHttpClient httpClient, Converter.Factory converterFactory,
      @Named("rx") CallAdapter.Factory rxCallAdapterFactory) {
    return new Retrofit.Builder().baseUrl(DONATIONS_URL)
        .client(httpClient)
        .addCallAdapterFactory(rxCallAdapterFactory)
        .addConverterFactory(converterFactory)
        .build();
  }

  @Singleton @Provides @Named("retrofit-apichain-bds") Retrofit providesApiChainBDSRetrofit(
      @Named("v8") OkHttpClient httpClient, Converter.Factory converterFactory,
      @Named("rx") CallAdapter.Factory rxCallAdapterFactory,
      @Named("apichain-bds-base-host") String baseHost) {
    return new Retrofit.Builder().baseUrl(baseHost)
        .client(httpClient)
        .addCallAdapterFactory(rxCallAdapterFactory)
        .addConverterFactory(converterFactory)
        .build();
  }

  @Singleton @Provides @Named("retrofit-v7-secondary") Retrofit providesV7SecondaryRetrofit(
      @Named("default") OkHttpClient httpClient, @Named("base-secondary-host") String baseHost,
      Converter.Factory converterFactory, @Named("rx") CallAdapter.Factory rxCallAdapterFactory) {
    return new Retrofit.Builder().baseUrl(baseHost)
        .client(httpClient)
        .addCallAdapterFactory(rxCallAdapterFactory)
        .addConverterFactory(converterFactory)
        .build();
  }

  @Singleton @Provides SearchSuggestionRemoteRepository providesSearchSuggestionRemoteRepository(
      Retrofit retrofit) {
    return retrofit.create(SearchSuggestionRemoteRepository.class);
  }

  @Singleton @Provides RetrofitAptoideBiService.ServiceV7 providesAptoideBiService(
      @Named("retrofit-v7") Retrofit retrofit) {
    return retrofit.create(RetrofitAptoideBiService.ServiceV7.class);
  }

  @Singleton @Provides AutoUpdateService.Service providesAutoUpdateService(
      @Named("retrofit-auto-update") Retrofit retrofit) {
    return retrofit.create(AutoUpdateService.Service.class);
  }

  @Singleton @Provides SearchAbTestService.Service providesSearchAbTestRetrofit(
      @Named("retrofit-auto-update") Retrofit retrofit) {
    return retrofit.create(SearchAbTestService.Service.class);
  }

  @Singleton @Provides SearchAbTestService providesSearchAbTestService(
      SearchAbTestService.Service service) {
    return new SearchAbTestService(service);
  }

  @Singleton @Provides ABTestService.ServiceV7 providesABTestServiceV7(
      @Named("retrofit-AB") Retrofit retrofit) {
    return retrofit.create(ABTestService.ServiceV7.class);
  }

  @Singleton @Provides DonationsService.ServiceV8 providesDonationsServiceV8(
      @Named("retrofit-donations") Retrofit retrofit) {
    return retrofit.create(DonationsService.ServiceV8.class);
  }

  @Singleton @Provides CaptchaService.ServiceInterface providesCaptchaServiceInterface(
      @Named("retrofit-apichain-bds") Retrofit retrofit) {
    return retrofit.create(CaptchaService.ServiceInterface.class);
  }

  @Singleton @Provides PromotionsService providesPromotionsService(@Named("pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      Converter.Factory converterFactory, @Named("default") SharedPreferences sharedPreferences) {
    return new PromotionsService(bodyInterceptorPoolV7, okHttpClient, tokenInvalidator,
        converterFactory, sharedPreferences);
  }

  @Singleton @Provides CaptchaService providesCaptchaService(
      CaptchaService.ServiceInterface service, IdsRepository idsRepository) {
    return new CaptchaService(service, idsRepository);
  }

  @Singleton @Provides WalletService.ServiceV7 providesWalletServiceV8(
      @Named("retrofit-v7-secondary") Retrofit retrofit) {
    return retrofit.create(WalletService.ServiceV7.class);
  }

  @Singleton @Provides CrashReport providesCrashReports() {
    return CrashReport.getInstance();
  }

  @Singleton @Provides RealmEventMapper providesRealmEventMapper(
      @Named("default") ObjectMapper objectMapper) {
    return new RealmEventMapper(objectMapper);
  }

  @Singleton @Provides EventsPersistence providesEventsPersistence(Database database,
      RealmEventMapper mapper) {
    return new RealmEventPersistence(database, mapper);
  }

  @Singleton @Provides AptoideBiEventService providesRetrofitAptoideBiService(
      RetrofitAptoideBiService.ServiceV7 serviceV7,
      @Named("analytics-interceptor") AnalyticsBodyInterceptorV7 bodyInterceptor) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return new RetrofitAptoideBiService(dateFormat, serviceV7, bodyInterceptor);
  }

  @Singleton @Provides FirstLaunchAnalytics providesFirstLaunchAnalytics(
      AnalyticsManager analyticsManager, AnalyticsLogger logger, SafetyNetClient safetyNetClient) {
    return new FirstLaunchAnalytics(analyticsManager, logger, safetyNetClient,
        application.getPackageName());
  }

  @Singleton @Provides SafetyNetClient providesSafetyNetClient() {
    return SafetyNet.getClient(application);
  }

  @Singleton @Provides @Named("aptoideLogger") EventLogger providesAptoideEventLogger(
      @Named("aptoide") AptoideBiEventLogger aptoideBiEventLogger) {
    return aptoideBiEventLogger;
  }

  @Singleton @Provides @Named("aptoideSession") SessionLogger providesAptoideSessionLogger(
      @Named("aptoide") AptoideBiEventLogger aptoideBiEventLogger) {
    return aptoideBiEventLogger;
  }

  @Singleton @Provides @Named("facebook") EventLogger providesFacebookEventLogger(
      AppEventsLogger facebook, AnalyticsLogger logger) {
    return new FacebookEventLogger(facebook, logger);
  }

  @Singleton @Provides @Named("flurry") FlurryEventLogger providesFlurryLogger(
      AnalyticsLogger logger) {
    return new FlurryEventLogger(application, logger);
  }

  @Singleton @Provides @Named("flurryLogger") EventLogger providesFlurryEventLogger(
      @Named("flurry") FlurryEventLogger eventLogger) {
    return eventLogger;
  }

  @Singleton @Provides @Named("flurrySession") SessionLogger providesFlurrySessionLogger(
      @Named("flurry") FlurryEventLogger eventLogger) {
    return eventLogger;
  }

  @Singleton @Provides @Named("aptoide") AptoideBiEventLogger providesAptoideBILogger(
      EventsPersistence persistence, AptoideBiEventService service, Crashlytics crashlytics,
      @Named("default") SharedPreferences preferences, AnalyticsLogger debugLogger) {
    return new AptoideBiEventLogger(
        new AptoideBiAnalytics(persistence, new SharedPreferencesSessionPersistence(preferences),
            service, new CompositeSubscription(), Schedulers.computation(),
            BuildConfig.ANALYTICS_EVENTS_INITIAL_DELAY_IN_MILLIS,
            BuildConfig.ANALYTICS_EVENTS_TIME_INTERVAL_IN_MILLIS,
            new CrashlyticsCrashLogger(crashlytics), debugLogger),
        BuildConfig.ANALYTICS_SESSION_INTERVAL_IN_MILLIS);
  }

  @Singleton @Provides @Named("fabric") EventLogger providesFabricEventLogger(Answers fabric,
      AnalyticsLogger logger) {
    return new FabricEventLogger(fabric, logger);
  }

  @Singleton @Provides HttpKnockEventLogger providesknockEventLogger(
      @Named("default") OkHttpClient client) {
    return new HttpKnockEventLogger(client);
  }

  @Singleton @Provides @Named("aptoideEvents") Collection<String> provideAptoideEvents() {
    return Arrays.asList(AppViewAnalytics.OPEN_APP_VIEW,
        NotificationAnalytics.NOTIFICATION_EVENT_NAME, TimelineAnalytics.OPEN_APP,
        TimelineAnalytics.UPDATE_APP, TimelineAnalytics.OPEN_STORE, TimelineAnalytics.OPEN_ARTICLE,
        TimelineAnalytics.LIKE, TimelineAnalytics.OPEN_BLOG, TimelineAnalytics.OPEN_VIDEO,
        TimelineAnalytics.OPEN_CHANNEL, TimelineAnalytics.OPEN_STORE_PROFILE,
        TimelineAnalytics.COMMENT, TimelineAnalytics.SHARE, TimelineAnalytics.SHARE_SEND,
        TimelineAnalytics.COMMENT_SEND, TimelineAnalytics.FAB, TimelineAnalytics.SCROLLING_EVENT,
        TimelineAnalytics.OPEN_TIMELINE_EVENT, AccountAnalytics.APTOIDE_EVENT_NAME,
        DownloadAnalytics.DOWNLOAD_EVENT_NAME, InstallAnalytics.INSTALL_EVENT_NAME,
        PromotionsAnalytics.VALENTINE_MIGRATOR);
  }

  @Singleton @Provides @Named("fabricEvents") Collection<String> provideFabricEvents() {
    return Arrays.asList(DownloadAnalytics.DOWNLOAD_COMPLETE_EVENT,
        InstallFabricEvents.ROOT_V2_COMPLETE, InstallFabricEvents.ROOT_V2_START,
        InstallFabricEvents.IS_INSTALLATION_TYPE_EVENT_NAME,
        AppValidationAnalytics.INVALID_DOWNLOAD_PATH_EVENT);
  }

  @Singleton @Provides AnalyticsManager providesAnalyticsManager(
      @Named("aptoideLogger") EventLogger aptoideBiEventLogger,
      @Named("facebook") EventLogger facebookEventLogger,
      @Named("fabric") EventLogger fabricEventLogger,
      @Named("flurryLogger") EventLogger flurryEventLogger, HttpKnockEventLogger knockEventLogger,
      @Named("aptoideEvents") Collection<String> aptoideEvents,
      @Named("facebookEvents") Collection<String> facebookEvents,
      @Named("fabricEvents") Collection<String> fabricEvents,
      @Named("flurryEvents") Collection<String> flurryEvents,
      @Named("flurrySession") SessionLogger flurrySessionLogger,
      @Named("aptoideSession") SessionLogger aptoideSessionLogger,
      @Named("normalizer") AnalyticsEventParametersNormalizer analyticsNormalizer,
      AnalyticsLogger logger) {

    return new AnalyticsManager.Builder().addLogger(aptoideBiEventLogger, aptoideEvents)
        .addLogger(facebookEventLogger, facebookEvents)
        .addLogger(fabricEventLogger, fabricEvents)
        .addLogger(flurryEventLogger, flurryEvents)
        .addSessionLogger(flurrySessionLogger)
        .addSessionLogger(aptoideSessionLogger)
        .setKnockLogger(knockEventLogger)
        .setAnalyticsNormalizer(analyticsNormalizer)
        .setDebugLogger(logger)
        .build();
  }

  @Singleton @Provides @Named("normalizer")
  AnalyticsEventParametersNormalizer providesAnalyticsNormalizer() {
    return new AnalyticsEventParametersNormalizer();
  }

  @Singleton @Provides AppShortcutsAnalytics providesAppShortcutsAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    return new AppShortcutsAnalytics(analyticsManager, navigationTracker);
  }

  @Singleton @Provides ReadPostsPersistence providesReadPostsPersistence() {
    return new ReadPostsPersistence(new ArrayList<>());
  }

  @Singleton @Provides TimelineAnalytics providesTimelineAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    return new TimelineAnalytics(navigationTracker, analyticsManager);
  }

  @Singleton @Provides StoreAnalytics providesStoreAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    return new StoreAnalytics(analyticsManager, navigationTracker);
  }

  @Singleton @Provides AppService providesAppService(
      StoreCredentialsProvider storeCredentialsProvider, @Named("pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("defaultInterceptorV3")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3,
      @Named("default") OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences) {

    return new AppService(storeCredentialsProvider, bodyInterceptorPoolV7, bodyInterceptorV3,
        okHttpClient, WebService.getDefaultConverter(), tokenInvalidator, sharedPreferences,
        application.getResources());
  }

  @Singleton @Provides AppCenterRepository providesAppCenterRepository(AppService appService) {
    return new AppCenterRepository(appService, new HashMap<>());
  }

  @Singleton @Provides AppCenter providesAppCenter(AppCenterRepository appCenterRepository) {
    return new AppCenter(appCenterRepository);
  }

  @Singleton @Provides AppCoinsManager providesAppCoinsManager(AppCoinsService appCoinsService,
      DonationsService donationsService) {
    return new AppCoinsManager(appCoinsService, donationsService);
  }

  @Singleton @Provides AppCoinsService providesAppCoinsService(@Named("pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences, Converter.Factory converterFactory) {
    return new AppCoinsService(okHttpClient, tokenInvalidator, sharedPreferences,
        bodyInterceptorPoolV7, converterFactory);
  }

  @Named("remote") @Singleton @Provides BundleDataSource providesRemoteBundleDataSource(
      @Named("pool-v7")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, Converter.Factory converter,
      BundlesResponseMapper mapper, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences, AptoideAccountManager accountManager,
      PackageRepository packageRepository, Database database, IdsRepository idsRepository,
      QManager qManager, Resources resources, WindowManager windowManager,
      ConnectivityManager connectivityManager,
      AdsApplicationVersionCodeProvider adsApplicationVersionCodeProvider) {
    return new RemoteBundleDataSource(5, new HashMap<>(), bodyInterceptorPoolV7, okHttpClient,
        converter, mapper, tokenInvalidator, sharedPreferences, new WSWidgetsUtils(),
        new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(database, Store.class)),
        idsRepository.getUniqueIdentifier(),
        AdNetworkUtils.isGooglePlayServicesAvailable(getApplicationContext()),
        ((AptoideApplication) getApplicationContext()).getPartnerId(), accountManager,
        qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)), resources,
        windowManager, connectivityManager, adsApplicationVersionCodeProvider, packageRepository,
        10, 10);
  }

  @Singleton @Provides BundlesRepository providesBundleRepository(
      @Named("remote") BundleDataSource remoteBundleDataSource) {
    return new BundlesRepository(remoteBundleDataSource, new HashMap<>(), new HashMap<>(), 5);
  }

  @Singleton @Provides BannerRepository providesBannerRepository() {
    return new BannerRepository();
  }

  @Singleton @Provides AdMapper providesAdMapper() {
    return new AdMapper();
  }

  @Singleton @Provides BundlesResponseMapper providesBundlesMapper(
      @Named("marketName") String marketName, InstallManager installManager,
      PackageRepository packageRepository) {
    return new BundlesResponseMapper(marketName, installManager, packageRepository);
  }

  @Singleton @Provides UpdatesManager providesUpdatesManager(UpdateRepository updateRepository) {
    return new UpdatesManager(updateRepository);
  }

  @Singleton @Provides UpdateRepository providesUpdateRepository(UpdateAccessor updateAccessor,
      StoreAccessor storeAccessor, IdsRepository idsRepository, @Named("pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, @Named("default") SharedPreferences sharedPreferences) {
    return new UpdateRepository(updateAccessor, storeAccessor, idsRepository, bodyInterceptorPoolV7,
        okHttpClient, converterFactory, tokenInvalidator, sharedPreferences,
        application.getPackageManager());
  }

  @Singleton @Provides NotLoggedInShareAnalytics providesNotLoggedInShareAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker,
      AccountAnalytics accountAnalytics) {
    return new NotLoggedInShareAnalytics(analyticsManager, navigationTracker, accountAnalytics);
  }

  @Singleton @Provides AppViewAnalytics providesAppViewAnalytics(
      DownloadAnalytics downloadAnalytics, AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker, TimelineAnalytics timelineAnalytics,
      NotLoggedInShareAnalytics notLoggedInShareAnalytics, BillingAnalytics billingAnalytics,
      StoreAnalytics storeAnalytics) {
    return new AppViewAnalytics(downloadAnalytics, analyticsManager, navigationTracker,
        timelineAnalytics, notLoggedInShareAnalytics, billingAnalytics, storeAnalytics);
  }

  @Singleton @Provides PreferencesPersister providesUserPreferencesPersister(
      @Named("default") SharedPreferences sharedPreferences) {
    return new PreferencesPersister(sharedPreferences);
  }

  @Singleton @Provides PreferencesManager providesPreferencesManager(
      PreferencesPersister preferencesPersister) {
    return new PreferencesManager(preferencesPersister);
  }

  @Singleton @Provides ReviewsManager providesReviewsManager(ReviewsRepository reviewsRepository) {
    return new ReviewsManager(reviewsRepository);
  }

  @Singleton @Provides ReviewsRepository providesReviewsRepository(ReviewsService reviewsService) {
    return new ReviewsRepository(reviewsService);
  }

  @Singleton @Provides ReviewsService providesReviewsService(
      StoreCredentialsProvider storeCredentialsProvider, @Named("pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences) {
    return new ReviewsService(storeCredentialsProvider, bodyInterceptorPoolV7, okHttpClient,
        WebService.getDefaultConverter(), tokenInvalidator, sharedPreferences);
  }

  @Singleton @Provides AdsManager providesAdsManager(AdsRepository adsRepository) {
    return new AdsManager(adsRepository, AccessorFactory.getAccessorFor(
        ((AptoideApplication) application.getApplicationContext()).getDatabase(),
        StoredMinimalAd.class), new MinimalAdMapper());
  }

  @Singleton @Provides BillingAnalytics providesBillingAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    return new BillingAnalytics(BuildConfig.APPLICATION_ID, analyticsManager, navigationTracker);
  }

  @Singleton @Provides ABTestService providesABTestService(ABTestService.ServiceV7 serviceV7,
      IdsRepository idsRepository) {
    return new ABTestService(serviceV7, idsRepository.getUniqueIdentifier());
  }

  @Singleton @Provides RealmExperimentPersistence providesRealmExperimentPersistence(
      Database database) {
    return new RealmExperimentPersistence(database, new RealmExperimentMapper());
  }

  @Singleton @Provides @Named("ab-test-local-cache")
  HashMap<String, ExperimentModel> providesAbTestLocalCache() {
    return new HashMap<>();
  }

  @Singleton @Provides AbTestCacheValidator providesAbTestCacheValidator(
      @Named("ab-test-local-cache") HashMap<String, ExperimentModel> localCache) {
    return new AbTestCacheValidator(localCache);
  }

  @Singleton @Provides ABTestCenterRepository providesABTestCenterRepository(
      ABTestService abTestService, RealmExperimentPersistence persistence,
      @Named("ab-test-local-cache") HashMap<String, ExperimentModel> localCache,
      AbTestCacheValidator cacheValidator) {
    return new ABTestCenterRepository(abTestService, localCache, persistence, cacheValidator);
  }

  @Singleton @Provides AbTestSearchRepository providesAbTestSearchRepository(
      ABTestService abTestService, RealmExperimentPersistence persistence,
      SearchAbTestService searchAbTestService,
      @Named("ab-test-local-cache") HashMap<String, ExperimentModel> localCache,
      AbTestCacheValidator abTestCacheValidator) {
    return new AbTestSearchRepository(abTestService, localCache, persistence, searchAbTestService,
        abTestCacheValidator);
  }

  @Singleton @Provides @Named("ab-test") ABTestManager providesABTestManager(
      ABTestCenterRepository abTestCenterRepository) {
    return new ABTestManager(abTestCenterRepository);
  }

  @Singleton @Provides @Named("search-ab-test") ABTestManager providesSearchABTestManager(
      AbTestSearchRepository abTestCenterRepository) {
    return new ABTestManager(abTestCenterRepository);
  }

  @Singleton @Provides PromotionsManager providePromotionsManager(InstallManager installManager,
      PromotionViewAppMapper promotionViewAppMapper, DownloadFactory downloadFactory,
      DownloadStateParser downloadStateParser, PromotionsAnalytics promotionsAnalytics,
      NotificationAnalytics notificationAnalytics, InstallAnalytics installAnalytics,
      PreferencesManager preferencesManager, PromotionsService promotionsService,
      InstalledRepository installedRepository, @Named("homePromotionsId") String promotionId) {
    return new PromotionsManager(promotionViewAppMapper, installManager, downloadFactory,
        downloadStateParser, promotionsAnalytics, notificationAnalytics, installAnalytics,
        preferencesManager, application.getApplicationContext()
        .getPackageManager(), promotionsService, installedRepository, promotionId);
  }

  @Singleton @Provides PromotionViewAppMapper providesPromotionViewAppMapper(
      DownloadStateParser downloadStateParser) {
    return new PromotionViewAppMapper(downloadStateParser);
  }

  @Singleton @Provides ImpressionManager providesImpressionManager(
      ImpressionService impressionService) {
    return new ImpressionManager(impressionService);
  }

  @Singleton @Provides DownloadStateParser providesDownloadStateParser() {
    return new DownloadStateParser();
  }

  @Singleton @Provides ImpressionService providesImpressionService(@Named("pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences, Converter.Factory converterFactory) {
    return new ImpressionService(bodyInterceptorPoolV7, okHttpClient, tokenInvalidator,
        sharedPreferences, converterFactory);
  }

  @Singleton @Provides EditorialService providesEditorialService(@Named("pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, @Named("default") SharedPreferences sharedPreferences) {
    return new EditorialService(bodyInterceptorPoolV7, okHttpClient, tokenInvalidator,
        converterFactory, sharedPreferences);
  }

  @Singleton @Provides EditorialAnalytics providesEditorialAnalytics(
      DownloadAnalytics downloadAnalytics, AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    return new EditorialAnalytics(downloadAnalytics, analyticsManager, navigationTracker);
  }

  @Singleton @Provides DonationsService providesDonationsService(
      DonationsService.ServiceV8 service) {
    return new DonationsService(service, Schedulers.io());
  }

  @Singleton @Provides WalletService providesWalletService(WalletService.ServiceV7 service) {
    return new WalletService(service, Schedulers.io());
  }

  @Singleton @Provides LoginPreferences provideLoginPreferences() {
    return new LoginPreferences(application, GoogleApiAvailability.getInstance());
  }

  @Singleton @Provides @Named("defaultStoreName") String provideStoreName() {
    return "apps";
  }

  @Singleton @Provides @Named("extraID") String provideExtraID() {
    return "";
  }

  @Singleton @Provides @Named("marketName") String provideMarketName() {
    return BuildConfig.MARKET_NAME;
  }

  @Singleton @Provides @Named("homePromotionsId") String provideHomePromotionsId() {
    return BuildConfig.HOME_PROMOTION_ID;
  }

  @Singleton @Provides @Named("accountType") String provideAccountType() {
    return BuildConfig.APPLICATION_ID;
  }

  @Singleton @Provides @Named("cachePath") String provideCachePath() {
    return Environment.getExternalStorageDirectory()
        .getAbsolutePath() + "/.aptoide/";
  }

  @Singleton @Provides @Named("imageCachePath") String provideImageCachePatch(
      @Named("cachePath") String cachePath) {
    return cachePath + "icons/";
  }

  @Singleton @Provides @Named("default-followed-stores")
  List<String> provideDefaultFollowedStores() {
    return Arrays.asList("apps", "bds-store");
  }

  @Singleton @Provides AptoideApplicationAnalytics provideAptoideApplicationAnalytics() {
    return new AptoideApplicationAnalytics();
  }

  @Singleton @Provides MoPubAnalytics providesMoPubAnalytics() {
    return new MoPubAnalytics();
  }

  @Singleton @Provides @Named("flurryEvents") Collection<String> provideFlurryEvents() {
    List<String> flurryEvents = new LinkedList<>(Arrays.asList(InstallAnalytics.APPLICATION_INSTALL,
        DownloadAnalytics.EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME,
        DownloadAnalytics.DOWNLOAD_COMPLETE_EVENT, AppViewAnalytics.HOME_PAGE_EDITORS_CHOICE_FLURRY,
        AppViewAnalytics.APP_VIEW_OPEN_FROM, StoreAnalytics.STORES_TAB_INTERACT,
        StoreAnalytics.STORES_OPEN, StoreAnalytics.STORES_INTERACT,
        AccountAnalytics.SIGN_UP_EVENT_NAME, AccountAnalytics.LOGIN_EVENT_NAME,
        FirstLaunchAnalytics.FIRST_LAUNCH, AccountAnalytics.LOGIN_SIGN_UP_START_SCREEN,
        AccountAnalytics.CREATE_USER_PROFILE, AccountAnalytics.CREATE_YOUR_STORE,
        AccountAnalytics.PROFILE_SETTINGS, AdultContentAnalytics.ADULT_CONTENT,
        DeepLinkAnalytics.APP_LAUNCH, DeepLinkAnalytics.FACEBOOK_APP_LAUNCH,
        AppViewAnalytics.CLICK_INSTALL));
    return flurryEvents;
  }

  @Singleton @Provides @Named("facebookEvents") Collection<String> provideFacebookEvents() {
    return Arrays.asList(InstallAnalytics.APPLICATION_INSTALL,
        InstallAnalytics.NOTIFICATION_APPLICATION_INSTALL,
        InstallAnalytics.EDITORS_APPLICATION_INSTALL,
        AddressBookAnalytics.FOLLOW_FRIENDS_CHOOSE_NETWORK,
        AddressBookAnalytics.FOLLOW_FRIENDS_HOW_TO,
        AddressBookAnalytics.FOLLOW_FRIENDS_APTOIDE_ACCESS,
        AddressBookAnalytics.FOLLOW_FRIENDS_NEW_CONNECTIONS,
        AddressBookAnalytics.FOLLOW_FRIENDS_SET_MY_PHONENUMBER,
        DownloadAnalytics.EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME,
        DownloadAnalytics.NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME,
        DownloadAnalytics.DOWNLOAD_COMPLETE_EVENT, SearchAnalytics.SEARCH,
        SearchAnalytics.NO_RESULTS, SearchAnalytics.APP_CLICK, SearchAnalytics.SEARCH_START,
        SearchAnalytics.AB_SEARCH_ACTION, SearchAnalytics.AB_SEARCH_IMPRESSION,
        AppViewAnalytics.EDITORS_CHOICE_CLICKS, AppViewAnalytics.APP_VIEW_OPEN_FROM,
        AppViewAnalytics.APP_VIEW_INTERACT, AppViewAnalytics.DONATIONS_IMPRESSION,
        NotificationAnalytics.NOTIFICATION_RECEIVED, NotificationAnalytics.NOTIFICATION_IMPRESSION,
        NotificationAnalytics.NOTIFICATION_PRESSED, NotificationAnalytics.NOTIFICATION_RECEIVED,
        TimelineAnalytics.SOCIAL_CARD_PREVIEW, TimelineAnalytics.CARD_ACTION,
        TimelineAnalytics.TIMELINE_OPENED, StoreAnalytics.STORES_TAB_INTERACT,
        StoreAnalytics.STORES_OPEN, StoreAnalytics.STORES_INTERACT,
        AccountAnalytics.SIGN_UP_EVENT_NAME, AccountAnalytics.LOGIN_EVENT_NAME,
        AccountAnalytics.FOLLOW_FRIENDS, UpdatesAnalytics.UPDATE_EVENT,
        PageViewsAnalytics.PAGE_VIEW_EVENT, FirstLaunchAnalytics.FIRST_LAUNCH,
        FirstLaunchAnalytics.PLAY_PROTECT_EVENT, InstallFabricEvents.ROOT_V2_COMPLETE,
        InstallFabricEvents.ROOT_V2_START, AppViewAnalytics.SIMILAR_APP_INTERACT,
        NotLoggedInShareAnalytics.POP_UP_SHARE_TIMELINE,
        AccountAnalytics.LOGIN_SIGN_UP_START_SCREEN, AccountAnalytics.CREATE_USER_PROFILE,
        AccountAnalytics.PROFILE_SETTINGS, AccountAnalytics.ENTRY,
        DeepLinkAnalytics.FACEBOOK_APP_LAUNCH, AppViewAnalytics.CLICK_INSTALL,
        BillingAnalytics.PAYMENT_AUTH, BillingAnalytics.PAYMENT_LOGIN,
        BillingAnalytics.PAYMENT_POPUP, AppShortcutsAnalytics.APPS_SHORTCUTS,
        AccountAnalytics.CREATE_YOUR_STORE, DeepLinkAnalytics.FACEBOOK_APP_LAUNCH,
        AppViewAnalytics.CLICK_INSTALL, BillingAnalytics.PAYMENT_AUTH,
        BillingAnalytics.PAYMENT_LOGIN, BillingAnalytics.PAYMENT_POPUP, HomeAnalytics.HOME_INTERACT,
        HomeAnalytics.CURATION_CARD_CLICK, HomeAnalytics.CURATION_CARD_IMPRESSION,
        HomeAnalytics.HOME_CHIP_CLICK, TimelineAnalytics.MESSAGE_IMPRESSION,
        TimelineAnalytics.MESSAGE_INTERACT, AccountAnalytics.PROMOTE_APTOIDE_EVENT_NAME,
        BottomNavigationAnalytics.BOTTOM_NAVIGATION_INTERACT,
        NotLoggedInShareAnalytics.MESSAGE_IMPRESSION, NotLoggedInShareAnalytics.MESSAGE_INTERACT,
        DownloadAnalytics.DOWNLOAD_INTERACT, DonationsAnalytics.DONATIONS_INTERACT,
        EditorialAnalytics.CURATION_CARD_INSTALL, PromotionsAnalytics.PROMOTION_DIALOG,
        PromotionsAnalytics.PROMOTIONS_INTERACT, PromotionsAnalytics.VALENTINE_MIGRATOR);
  }

  @Singleton @Provides AptoideShortcutManager providesShortcutManager() {
    return new AptoideShortcutManager();
  }

  @Singleton @Provides SettingsManager providesSettingsManager() {
    return new SettingsManager();
  }

  @Singleton @Provides LoginSignupManager providesLoginSignupManager() {
    return new LoginSignupManager();
  }

  @Singleton @Provides MyAccountManager providesMyAccountManager() {
    return new MyAccountManager();
  }

  @Singleton @Provides PromotionsPreferencesManager providesPromotionsPreferencesManager(
      PreferencesPersister persister) {
    return new PromotionsPreferencesManager(persister);
  }

  @Singleton @Provides PromotionsAnalytics providesPromotionsAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker,
      DownloadAnalytics downloadAnalytics) {
    return new PromotionsAnalytics(analyticsManager, navigationTracker, downloadAnalytics);
  }

  @Singleton @Provides @Named("retrofit-auto-update") Retrofit providesAutoUpdateRetrofit(
      @Named("default") OkHttpClient httpClient, @Named("auto-update-base-host") String baseHost,
      Converter.Factory converterFactory, @Named("rx") CallAdapter.Factory rxCallAdapterFactory) {
    return new Retrofit.Builder().baseUrl(baseHost)
        .client(httpClient)
        .addCallAdapterFactory(rxCallAdapterFactory)
        .addConverterFactory(converterFactory)
        .build();
  }

  @Singleton @Provides @Named("auto-update-base-host") String providesAutoUpdateBaseHost() {
    return "http://imgs.aptoide.com/";
  }

  @Singleton @Provides SupportEmailProvider providesSupportEmailProvider(
      @Named("support-email") String supportEmail) {
    return new SupportEmailProvider(supportEmail, application.getString(R.string.aptoide_email));
  }

  @Singleton @Provides NewsletterManager providesNewsletterManager() {
    return new NewsletterManager();
  }

  @Named("rating-one-decimal-format") @Singleton @Provides DecimalFormat providesDecimalFormat() {
    return new DecimalFormat("0.0");
  }

  @Singleton @Provides SearchExperiment providesSearchExperiment(
      @Named("search-ab-test") ABTestManager abTestManager) {
    return new SearchExperiment(abTestManager);
  }
}

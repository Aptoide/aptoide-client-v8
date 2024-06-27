package cm.aptoide.pt;

import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.UiModeManager;
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
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;
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
import cm.aptoide.analytics.implementation.loggers.FacebookEventLogger;
import cm.aptoide.analytics.implementation.loggers.FlurryEventLogger;
import cm.aptoide.analytics.implementation.loggers.HttpKnockEventLogger;
import cm.aptoide.analytics.implementation.loggers.IndicativeEventLogger;
import cm.aptoide.analytics.implementation.loggers.RakamEventLogger;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.analytics.implementation.network.RetrofitAptoideBiService;
import cm.aptoide.analytics.implementation.persistence.SharedPreferencesSessionPersistence;
import cm.aptoide.analytics.implementation.utils.AnalyticsEventParametersNormalizer;
import cm.aptoide.analytics.implementation.utils.MapToJsonMapper;
import cm.aptoide.pt.aab.DynamicSplitsManager;
import cm.aptoide.pt.aab.DynamicSplitsMapper;
import cm.aptoide.pt.aab.DynamicSplitsRemoteService;
import cm.aptoide.pt.aab.DynamicSplitsService;
import cm.aptoide.pt.aab.SplitsMapper;
import cm.aptoide.pt.abtesting.ABTestCenterRepository;
import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.abtesting.ABTestService;
import cm.aptoide.pt.abtesting.ABTestServiceProvider;
import cm.aptoide.pt.abtesting.AbTestCacheValidator;
import cm.aptoide.pt.abtesting.ExperimentModel;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.AccountServiceV3;
import cm.aptoide.pt.account.AdultContentAnalytics;
import cm.aptoide.pt.account.AgentPersistence;
import cm.aptoide.pt.account.AndroidAccountManagerPersistence;
import cm.aptoide.pt.account.AndroidAccountProvider;
import cm.aptoide.pt.account.DatabaseStoreDataPersist;
import cm.aptoide.pt.account.FacebookSignUpAdapter;
import cm.aptoide.pt.account.GoogleSignUpAdapter;
import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.account.MatureBodyInterceptorV7;
import cm.aptoide.pt.account.MatureContentPersistence;
import cm.aptoide.pt.account.OAuthModeProvider;
import cm.aptoide.pt.account.view.ImageInfoProvider;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.account.view.user.NewsletterManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.ads.MoPubAnalytics;
import cm.aptoide.pt.ads.PackageRepositoryVersionCodeProvider;
import cm.aptoide.pt.ads.WalletAdsOfferCardManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.analytics.FirstLaunchAnalytics;
import cm.aptoide.pt.analytics.TrackerFilter;
import cm.aptoide.pt.analytics.analytics.AnalyticsBodyInterceptorV7;
import cm.aptoide.pt.apkfy.ApkfyManager;
import cm.aptoide.pt.apkfy.ApkfyService;
import cm.aptoide.pt.apkfy.AptoideApkfyService;
import cm.aptoide.pt.app.AdsManager;
import cm.aptoide.pt.app.AppCoinsAdvertisingManager;
import cm.aptoide.pt.app.AppCoinsService;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.CampaignAnalytics;
import cm.aptoide.pt.app.DownloadStateParser;
import cm.aptoide.pt.app.ReviewsManager;
import cm.aptoide.pt.app.ReviewsRepository;
import cm.aptoide.pt.app.ReviewsService;
import cm.aptoide.pt.app.appsflyer.AppsFlyerManager;
import cm.aptoide.pt.app.appsflyer.AppsFlyerRepository;
import cm.aptoide.pt.app.appsflyer.AppsFlyerService;
import cm.aptoide.pt.app.aptoideinstall.AptoideInstallManager;
import cm.aptoide.pt.app.aptoideinstall.AptoideInstallRepository;
import cm.aptoide.pt.app.migration.AppcMigrationManager;
import cm.aptoide.pt.app.migration.AppcMigrationPersistence;
import cm.aptoide.pt.app.migration.AppcMigrationRepository;
import cm.aptoide.pt.appview.PreferencesPersister;
import cm.aptoide.pt.autoupdate.Service;
import cm.aptoide.pt.blacklist.BlacklistManager;
import cm.aptoide.pt.blacklist.BlacklistPersistence;
import cm.aptoide.pt.blacklist.BlacklistUnitMapper;
import cm.aptoide.pt.blacklist.Blacklister;
import cm.aptoide.pt.bonus.BonusAppcRemoteService;
import cm.aptoide.pt.bonus.BonusAppcService;
import cm.aptoide.pt.bottomNavigation.BottomNavigationAnalytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.RoomAppComingSoonPersistence;
import cm.aptoide.pt.database.RoomAppcMigrationPersistence;
import cm.aptoide.pt.database.RoomAptoideInstallPersistence;
import cm.aptoide.pt.database.RoomDownloadPersistence;
import cm.aptoide.pt.database.RoomEventMapper;
import cm.aptoide.pt.database.RoomEventPersistence;
import cm.aptoide.pt.database.RoomExperimentMapper;
import cm.aptoide.pt.database.RoomExperimentPersistence;
import cm.aptoide.pt.database.RoomInstallationMapper;
import cm.aptoide.pt.database.RoomInstallationPersistence;
import cm.aptoide.pt.database.RoomInstalledPersistence;
import cm.aptoide.pt.database.RoomNotificationPersistence;
import cm.aptoide.pt.database.RoomStorePersistence;
import cm.aptoide.pt.database.RoomStoredMinimalAdPersistence;
import cm.aptoide.pt.database.RoomUpdatePersistence;
import cm.aptoide.pt.database.room.AptoideDatabase;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.aab.AppBundlesVisibilityManager;
import cm.aptoide.pt.dataprovider.aab.AppBundlesVisibilitySettingsProvider;
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
import cm.aptoide.pt.download.SplitAnalyticsMapper;
import cm.aptoide.pt.download.SplitTypeSubFileTypeMapper;
import cm.aptoide.pt.download.view.DownloadStatusManager;
import cm.aptoide.pt.downloadmanager.AppDownloaderProvider;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadAppFileMapper;
import cm.aptoide.pt.downloadmanager.DownloadAppMapper;
import cm.aptoide.pt.downloadmanager.DownloadPersistence;
import cm.aptoide.pt.downloadmanager.DownloadStatusMapper;
import cm.aptoide.pt.downloadmanager.DownloadsRepository;
import cm.aptoide.pt.downloadmanager.FileDownloaderProvider;
import cm.aptoide.pt.downloadmanager.RetryFileDownloadManagerProvider;
import cm.aptoide.pt.downloadmanager.RetryFileDownloaderProvider;
import cm.aptoide.pt.editorial.CaptionBackgroundPainter;
import cm.aptoide.pt.editorial.EditorialAnalytics;
import cm.aptoide.pt.editorial.EditorialService;
import cm.aptoide.pt.editorialList.EditorialListAnalytics;
import cm.aptoide.pt.file.CacheHelper;
import cm.aptoide.pt.file.FileManager;
import cm.aptoide.pt.home.AppComingSoonRegistrationManager;
import cm.aptoide.pt.home.AppComingSoonRegistrationPersistence;
import cm.aptoide.pt.home.ChipManager;
import cm.aptoide.pt.home.EskillsPreferencesManager;
import cm.aptoide.pt.home.HomeAnalytics;
import cm.aptoide.pt.home.apps.AppMapper;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.home.bundles.BundleDataSource;
import cm.aptoide.pt.home.bundles.BundlesRepository;
import cm.aptoide.pt.home.bundles.BundlesResponseMapper;
import cm.aptoide.pt.home.bundles.RemoteBundleDataSource;
import cm.aptoide.pt.home.bundles.ads.AdMapper;
import cm.aptoide.pt.home.more.eskills.EskillsAnalytics;
import cm.aptoide.pt.install.AppInstallerStatusReceiver;
import cm.aptoide.pt.install.AptoideInstallPersistence;
import cm.aptoide.pt.install.AptoideInstalledAppsRepository;
import cm.aptoide.pt.install.FilePathProvider;
import cm.aptoide.pt.install.ForegroundManager;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallAppSizeValidator;
import cm.aptoide.pt.install.InstallEvents;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.Installer;
import cm.aptoide.pt.install.InstallerAnalytics;
import cm.aptoide.pt.install.PackageInstallerManager;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.install.RootInstallNotificationEventReceiver;
import cm.aptoide.pt.install.RootInstallerProvider;
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
import cm.aptoide.pt.notification.AptoideWorkerFactory;
import cm.aptoide.pt.notification.ComingSoonNotificationManager;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.NotificationIdsMapper;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.notification.ReadyToInstallNotificationManager;
import cm.aptoide.pt.notification.RoomLocalNotificationSyncMapper;
import cm.aptoide.pt.notification.RoomLocalNotificationSyncPersistence;
import cm.aptoide.pt.notification.UpdatesNotificationManager;
import cm.aptoide.pt.notification.sync.LocalNotificationSyncManager;
import cm.aptoide.pt.packageinstaller.AppInstaller;
import cm.aptoide.pt.preferences.AptoideMd5Manager;
import cm.aptoide.pt.preferences.Preferences;
import cm.aptoide.pt.preferences.SecurePreferences;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.promotions.PromotionViewAppMapper;
import cm.aptoide.pt.promotions.PromotionsAnalytics;
import cm.aptoide.pt.promotions.PromotionsManager;
import cm.aptoide.pt.promotions.PromotionsPreferencesManager;
import cm.aptoide.pt.promotions.PromotionsService;
import cm.aptoide.pt.reactions.ReactionsManager;
import cm.aptoide.pt.reactions.network.ReactionsRemoteService;
import cm.aptoide.pt.reactions.network.ReactionsService;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.root.RootValueSaver;
import cm.aptoide.pt.search.SearchHostProvider;
import cm.aptoide.pt.search.SearchRepository;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.suggestions.SearchSuggestionManager;
import cm.aptoide.pt.search.suggestions.SearchSuggestionRemoteRepository;
import cm.aptoide.pt.search.suggestions.SearchSuggestionService;
import cm.aptoide.pt.search.suggestions.TrendingManager;
import cm.aptoide.pt.search.suggestions.TrendingService;
import cm.aptoide.pt.socialmedia.SocialMediaAnalytics;
import cm.aptoide.pt.store.RoomStoreRepository;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StorePersistence;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.sync.SyncScheduler;
import cm.aptoide.pt.sync.alarm.AlarmSyncScheduler;
import cm.aptoide.pt.sync.alarm.SyncStorage;
import cm.aptoide.pt.themes.NewFeature;
import cm.aptoide.pt.themes.NewFeatureManager;
import cm.aptoide.pt.themes.ThemeAnalytics;
import cm.aptoide.pt.updates.UpdateMapper;
import cm.aptoide.pt.updates.UpdatePersistence;
import cm.aptoide.pt.updates.UpdateRepository;
import cm.aptoide.pt.updates.UpdatesAnalytics;
import cm.aptoide.pt.util.MarketResourceFormatter;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.AppCenterRepository;
import cm.aptoide.pt.view.app.AppService;
import cm.aptoide.pt.view.settings.SupportEmailProvider;
import cm.aptoide.pt.wallet.WalletAppProvider;
import cn.dreamtobe.filedownloader.OkHttp3Connection;
import com.aptoide.authentication.AptoideAuthentication;
import com.aptoide.authentication.network.RemoteAuthenticationService;
import com.aptoide.authenticationrx.AptoideAuthenticationRx;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetClient;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

import static android.content.Context.UI_MODE_SERVICE;
import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

@Module public class ApplicationModule {

  private final AptoideApplication application;

  public ApplicationModule(AptoideApplication application) {
    this.application = application;
  }

  @Singleton @Provides AppInBackgroundTracker providesAppInBackgroundTracker() {
    return new AppInBackgroundTracker();
  }

  @Singleton @Provides ReadyToInstallNotificationManager providesReadyToInstallNotificationManager(
      InstallManager installManager) {
    return new ReadyToInstallNotificationManager(installManager, new NotificationIdsMapper());
  }

  @Singleton @Provides LaunchManager providesLaunchManager(FirstLaunchManager firstLaunchManager,
      UpdateLaunchManager updateLaunchManager,
      @Named("secureShared") SharedPreferences secureSharedPreferences) {
    return new LaunchManager(firstLaunchManager, updateLaunchManager, secureSharedPreferences);
  }

  @Singleton @Provides FirstLaunchManager providesFirstLaunchManager(
      @Named("default") SharedPreferences defaultSharedPreferences, IdsRepository idsRepository,
      FollowedStoresManager followedStoresManager, RootAvailabilityManager rootAvailabilityManager,
      AptoideAccountManager aptoideAccountManager, AptoideShortcutManager shortcutManager) {
    return new FirstLaunchManager(defaultSharedPreferences, idsRepository, followedStoresManager,
        rootAvailabilityManager, aptoideAccountManager, shortcutManager, application);
  }

  @Singleton @Provides UpdateLaunchManager providesUpdateLaunchManager(
      FollowedStoresManager followedStoresManager) {
    return new UpdateLaunchManager(followedStoresManager);
  }

  @Singleton @Provides FollowedStoresManager providesFollowedStoresManager(
      StoreCredentialsProvider storeCredentialsProvider,
      @Named("default-followed-stores") List<String> defaultFollowedStores,
      StoreUtilsProxy storeUtilsProxy, @Named("mature-pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> accountSettingsBodyInterceptorPoolV7,
      AptoideAccountManager aptoideAccountManager, @Named("default") OkHttpClient httpClient,
      TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences defaultSharedPreferences) {
    return new FollowedStoresManager(storeCredentialsProvider, defaultFollowedStores,
        storeUtilsProxy, accountSettingsBodyInterceptorPoolV7, aptoideAccountManager, httpClient,
        tokenInvalidator, defaultSharedPreferences);
  }

  @Singleton @Provides InstallManager providesInstallManager(
      AptoideDownloadManager aptoideDownloadManager, @Named("default") Installer defaultInstaller,
      RootAvailabilityManager rootAvailabilityManager,
      @Named("default") SharedPreferences defaultSharedPreferences,
      @Named("secureShared") SharedPreferences secureSharedPreferences,
      DownloadsRepository downloadsRepository,
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository,
      PackageInstallerManager packageInstallerManager, ForegroundManager foregroundManager,
      AptoideInstallManager aptoideInstallManager, InstallAppSizeValidator installAppSizeValidator,
      FileManager fileManager) {
    return new InstallManager(application, aptoideDownloadManager, defaultInstaller,
        rootAvailabilityManager, defaultSharedPreferences, secureSharedPreferences,
        downloadsRepository, aptoideInstalledAppsRepository, packageInstallerManager,
        foregroundManager, aptoideInstallManager, installAppSizeValidator, fileManager);
  }

  @Singleton @Provides InstallAppSizeValidator providesInstallAppSizeValidator(
      FilePathProvider filePathProvider) {
    return new InstallAppSizeValidator(filePathProvider);
  }

  @Singleton @Provides FilePathProvider filePathManager(@Named("cachePath") String cachePath,
      @Named("apkPath") String apkPath, @Named("obbPath") String obbPath) {
    return new FilePathProvider(apkPath, obbPath, cachePath);
  }

  @Singleton @Provides ForegroundManager providesForegroundManager() {
    return new ForegroundManager(application.getApplicationContext());
  }

  @Singleton @Provides RootInstallerProvider providesRootInstallerProvider(
      InstallerAnalytics installerAnalytics) {
    return new RootInstallerProvider(installerAnalytics, application.getApplicationContext()
        .getPackageName());
  }

  @Singleton @Provides InstallerAnalytics providesInstallerAnalytics(
      AnalyticsManager analyticsManager, InstallAnalytics installAnalytics,
      @Named("default") SharedPreferences sharedPreferences,
      RootAvailabilityManager rootAvailabilityManager, NavigationTracker navigationTracker) {
    return new InstallEvents(analyticsManager, installAnalytics, sharedPreferences,
        rootAvailabilityManager, navigationTracker);
  }

  @Singleton @Provides DownloadAnalytics providesDownloadAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker,
      ConnectivityManager connectivityManager, TelephonyManager providesSystemService) {
    return new DownloadAnalytics(connectivityManager, providesSystemService, navigationTracker,
        analyticsManager);
  }

  @Singleton @Provides UpdatesAnalytics providesUpdatesAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker, InstallAnalytics installAnalytics) {
    return new UpdatesAnalytics(analyticsManager, navigationTracker, installAnalytics);
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
      @Named("obbPath") String obbPath, DownloadAnalytics downloadAnalytics,
      FilePathProvider filePathProvider) {
    FileUtils.createDir(apkPath);
    FileUtils.createDir(obbPath);
    return new AptoideDownloadManager(downloadsRepository, downloadStatusMapper, cachePath,
        downloadAppMapper, appDownloaderProvider, downloadAnalytics, new FileUtils(),
        filePathProvider);
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
      DownloadPersistence downloadPersistence) {
    return new DownloadsRepository(downloadPersistence);
  }

  @Singleton @Provides DownloadStatusMapper downloadStatusMapper() {
    return new DownloadStatusMapper();
  }

  @Singleton @Provides @Named("default") Installer provideDefaultInstaller(
      InstallationProvider installationProvider,
      @Named("default") SharedPreferences sharedPreferences,
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository,
      RootAvailabilityManager rootAvailabilityManager, InstallerAnalytics installerAnalytics,
      AppInstaller appInstaller, AppInstallerStatusReceiver appInstallerStatusReceiver,
      RootInstallerProvider rootInstallerProvider) {
    return new DefaultInstaller(application.getPackageManager(), installationProvider, appInstaller,
        new FileUtils(), ToolboxManager.isDebug(sharedPreferences) || BuildConfig.DEBUG,
        aptoideInstalledAppsRepository, BuildConfig.ROOT_TIMEOUT, rootAvailabilityManager,
        sharedPreferences, installerAnalytics, getInstallingStateTimeout(),
        appInstallerStatusReceiver, rootInstallerProvider, application);
  }

  private int getInstallingStateTimeout() {
    return Build.VERSION.SDK_INT >= 21
        ? BuildConfig.INSTALLING_STATE_INSTALLER_TIMEOUT_IN_MILLIS_21_PLUS
        : BuildConfig.INSTALLING_STATE_INSTALLER_TIMEOUT_IN_MILLIS_21_MINUS;
  }

  @Singleton @Provides InstallationProvider provideInstallationProvider(
      AptoideDownloadManager downloadManager, DownloadPersistence downloadPersistence,
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository,
      RoomStoredMinimalAdPersistence roomStoredMinimalAdPersistence) {
    return new DownloadInstallationProvider(downloadManager, downloadPersistence,
        aptoideInstalledAppsRepository, new MinimalAdMapper(), roomStoredMinimalAdPersistence);
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

  @Singleton @Provides AptoideInstalledAppsRepository provideInstalledRepository(
      RoomInstalledPersistence roomInstalledPersistence, FileUtils fileUtils) {
    return new AptoideInstalledAppsRepository(roomInstalledPersistence,
        application.getPackageManager(), fileUtils);
  }

  @Singleton @Provides FileUtils provideFileUtils() {
    return new FileUtils();
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
      @Named("cachePath") String cachePath, AppValidator appValidator,
      SplitTypeSubFileTypeMapper splitTypeSubFileTypeMapper) {
    return new DownloadFactory(marketName, downloadApkPathsProvider, cachePath, appValidator,
        splitTypeSubFileTypeMapper);
  }

  @Singleton @Provides SplitTypeSubFileTypeMapper provideSplitTypeSubFileTypeMapper() {
    return new SplitTypeSubFileTypeMapper();
  }

  @Singleton @Provides SplitAnalyticsMapper splitAnalyticsMapper() {
    return new SplitAnalyticsMapper();
  }

  @Singleton @Provides RoomInstalledPersistence provideRoomInstalledPersistence(
      AptoideDatabase database, RoomInstallationPersistence roomInstallationPersistence,
      RoomInstallationMapper roomInstallationMapper) {
    return new RoomInstalledPersistence(database.installedDao(), roomInstallationPersistence,
        roomInstallationMapper);
  }

  @Singleton @Provides RoomInstallationMapper providesRoomInstallationMapper() {
    return new RoomInstallationMapper();
  }

  @Singleton @Provides RoomInstallationPersistence providesInstallationAccessor(
      AptoideDatabase database) {
    return new RoomInstallationPersistence(database.installationDao());
  }

  @Singleton @Provides DownloadPersistence provideDownloadPersistence(AptoideDatabase database) {
    return new RoomDownloadPersistence(database.downloadDAO());
  }

  @Singleton @Provides @Named("user-agent") Interceptor provideUserAgentInterceptor(
      AndroidAccountProvider androidAccountProvider, IdsRepository idsRepository,
      @Named("partnerID") String partnerId) {
    return new UserAgentInterceptor(idsRepository, partnerId, new DisplayMetrics(),
        AptoideUtils.SystemU.TERMINAL_INFO, AptoideUtils.Core.getDefaultVername(application));
  }

  @Singleton @Provides @Named("user-agent-v8") Interceptor provideUserAgentInterceptorV8(
      IdsRepository idsRepository, @Named("aptoidePackage") String aptoidePackage,
      AuthenticationPersistence authenticationPersistence, AptoideMd5Manager aptoideMd5Manager) {
    return new UserAgentInterceptorV8(idsRepository, AptoideUtils.SystemU.getRelease(),
        Build.VERSION.SDK_INT, AptoideUtils.SystemU.getModel(), AptoideUtils.SystemU.getProduct(),
        System.getProperty("os.arch"), new DisplayMetrics(),
        AptoideUtils.Core.getDefaultVername(application)
            .replace("aptoide-", ""), aptoidePackage, aptoideMd5Manager, BuildConfig.VERSION_CODE,
        authenticationPersistence);
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
    return SecurePreferencesImplementation.getInstance(application.getApplicationContext(),
        defaultSharedPreferences);
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
                .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
                .build())
        .build();
  }

  @Singleton @Provides @Named("facebookLoginPermissions")
  List<String> providesFacebookLoginPermissions() {
    return Collections.singletonList("email");
  }

  @Singleton @Provides AptoideAccountManager provideAptoideAccountManager(AdultContent adultContent,
      GoogleApiClient googleApiClient, StoreManager storeManager, AccountService accountService,
      LoginPreferences loginPreferences, AccountPersistence accountPersistence,
      @Named("facebookLoginPermissions") List<String> facebookPermissions) {
    FacebookSdk.sdkInitialize(application);

    return new AptoideAccountManager.Builder().setAccountPersistence(
            new MatureContentPersistence(accountPersistence, adultContent))
        .setAccountService(accountService)
        .setAdultService(adultContent)
        .registerSignUpAdapter(GoogleSignUpAdapter.TYPE,
            new GoogleSignUpAdapter(googleApiClient, loginPreferences))
        .registerSignUpAdapter(FacebookSignUpAdapter.TYPE,
            new FacebookSignUpAdapter(facebookPermissions, LoginManager.getInstance(),
                loginPreferences))
        .setStoreManager(storeManager)
        .build();
  }

  @Singleton @Provides AccountPersistence providesAccountPersistence(AccountManager accountManager,
      DatabaseStoreDataPersist databaseStoreDataPersist, AccountFactory accountFactory,
      AndroidAccountProvider androidAccountProvider,
      AuthenticationPersistence authenticationPersistence) {
    return new AndroidAccountManagerPersistence(accountManager, databaseStoreDataPersist,
        accountFactory, androidAccountProvider, authenticationPersistence, Schedulers.io());
  }

  @Singleton @Provides DatabaseStoreDataPersist providesDatabaseStoreDataPersist(
      RoomStoreRepository storeRepository) {
    return new DatabaseStoreDataPersist(new DatabaseStoreDataPersist.DatabaseStoreMapper(),
        storeRepository);
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
      @Named("extraID") String extraId, AccountFactory accountFactory,
      OAuthModeProvider oAuthModeProvider, AptoideAuthenticationRx aptoideAuthentication) {
    return new AccountServiceV3(accountFactory, httpClient, longTimeoutHttpClient, converterFactory,
        objectMapper, defaultSharedPreferences, extraId, tokenInvalidator,
        authenticationPersistence, noAuthenticationBodyInterceptorV3, bodyInterceptorV3,
        multipartBodyInterceptor, bodyInterceptorWebV7, bodyInterceptorPoolV7, oAuthModeProvider,
        aptoideAuthentication);
  }

  @Singleton @Provides OAuthModeProvider provideOAuthModeProvider() {
    return new OAuthModeProvider();
  }

  @Singleton @Provides @Named("default") OkHttpClient provideOkHttpClient(
      @Named("default") OkHttpClient.Builder okHttpClientBuilder,
      @Named("user-agent") Interceptor userAgentInterceptor) {
    okHttpClientBuilder.addInterceptor(userAgentInterceptor);
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

  @Singleton @Provides @Named("default") OkHttpClient.Builder providesOkHttpBuilder(
      L2Cache httpClientCache, @Named("default") SharedPreferences sharedPreferences,
      @Named("retrofit-log") Interceptor retrofitLogInterceptor) {
    final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    okHttpClientBuilder.readTimeout(45, TimeUnit.SECONDS);
    okHttpClientBuilder.writeTimeout(45, TimeUnit.SECONDS);
    final Cache cache = new Cache(application.getCacheDir(), 10 * 1024 * 1024);
    okHttpClientBuilder.cache(cache); // 10 MiB
    okHttpClientBuilder.addInterceptor(new POSTCacheInterceptor(httpClientCache));

    if (ToolboxManager.isToolboxEnableRetrofitLogs(sharedPreferences)) {
      okHttpClientBuilder.addInterceptor(retrofitLogInterceptor);
    }

    return okHttpClientBuilder;
  }

  @Singleton @Provides @Named("v8") OkHttpClient provideV8OkHttpClient(
      @Named("default") OkHttpClient.Builder okHttpClientBuilder,
      @Named("user-agent-v8") Interceptor userAgentInterceptorV8) {
    okHttpClientBuilder.addInterceptor(userAgentInterceptorV8);
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

  @Singleton @Provides RoomNotificationPersistence provideRoomNotificationPersistence(
      AptoideDatabase database) {
    return new RoomNotificationPersistence(database.notificationDao());
  }

  @Singleton @Provides SyncScheduler provideSyncScheduler(SyncStorage syncStorage) {
    return new AlarmSyncScheduler(application, syncStorage);
  }

  @Singleton @Provides SyncStorage provideSyncStorage(
      RoomLocalNotificationSyncPersistence persistence) {
    return new SyncStorage(new HashMap<>(), persistence);
  }

  @Singleton @Provides StoreUtilsProxy provideStoreUtilsProxy(AptoideAccountManager accountManager,
      RoomStoreRepository storeRepository, @Named("default") OkHttpClient httpClient,
      @Named("default") SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator,
      @Named("mature-pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptor,
      StoreCredentialsProvider storeCredentialsProvider) {
    return new StoreUtilsProxy(accountManager, bodyInterceptor, storeCredentialsProvider,
        storeRepository, httpClient, WebService.getDefaultConverter(), tokenInvalidator,
        sharedPreferences);
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
      @Named("aptoidePackage") String aptoidePackage, AptoideMd5Manager aptoideMd5Manager) {
    return new NoAuthenticationBodyInterceptorV3(idsRepository, aptoideMd5Manager, aptoidePackage);
  }

  @Singleton @Provides @Named("mature-pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> provideAccountSettingsBodyInterceptorPoolV7(
      @Named("pool-v7") BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptor,
      AdultContent adultContent) {
    return new MatureBodyInterceptorV7(bodyInterceptor, adultContent);
  }

  @Singleton @Provides @Named("pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> provideBodyInterceptorPoolV7(
      AuthenticationPersistence authenticationPersistence, IdsRepository idsRepository,
      @Named("default") SharedPreferences sharedPreferences, Resources resources, QManager qManager,
      @Named("aptoidePackage") String aptoidePackage, AptoideMd5Manager aptoideMd5Manager) {
    return new BodyInterceptorV7(idsRepository, authenticationPersistence, aptoideMd5Manager,
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
      @Named("aptoidePackage") String aptoidePackage, AptoideMd5Manager aptoideMd5Manager) {
    return new BodyInterceptorV7(idsRepository, authenticationPersistence, aptoideMd5Manager,
        aptoidePackage, qManager, Cdn.WEB, sharedPreferences, resources, BuildConfig.VERSION_CODE);
  }

  @Singleton @Provides @Named("analytics-interceptor")
  AnalyticsBodyInterceptorV7 provideAnalyticsBodyInterceptorV7(
      AuthenticationPersistence authenticationPersistence, IdsRepository idsRepository,
      @Named("default") SharedPreferences sharedPreferences, Resources resources, QManager qManager,
      @Named("aptoidePackage") String aptoidePackage, AptoideMd5Manager aptoideMd5Manager) {
    return new AnalyticsBodyInterceptorV7(idsRepository, authenticationPersistence,
        aptoideMd5Manager, aptoidePackage, resources, BuildConfig.VERSION_CODE, qManager,
        sharedPreferences);
  }

  @Singleton @Provides QManager provideQManager(Resources resources, WindowManager windowManager) {
    return new QManager(resources,
        ((ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE)), windowManager,
        (UiModeManager) application.getSystemService(UI_MODE_SERVICE));
  }

  @Singleton @Provides WindowManager provideWindowManager() {
    return ((WindowManager) application.getSystemService(Context.WINDOW_SERVICE));
  }

  @Singleton @Provides Preferences provideDefaultPreferences(
      @Named("default") SharedPreferences sharedPreferences) {
    return new Preferences(sharedPreferences);
  }

  @Singleton @Provides UpdatePersistence providesUpdatePersistence(AptoideDatabase database) {
    return new RoomUpdatePersistence(database.updateDao());
  }

  @Singleton @Provides SecureCoderDecoder provideSecureCoderDecoder(
      @Named("default") SharedPreferences sharedPreferences) {
    return new SecureCoderDecoder.Builder(application, sharedPreferences).create();
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

  @Singleton @Provides AptoideDatabase providesAptoideDataBase(
      RoomMigrationProvider roomMigrationProvider) {
    return Room.databaseBuilder(application.getApplicationContext(), AptoideDatabase.class,
            BuildConfig.ROOM_DATABASE_NAME)
        .fallbackToDestructiveMigrationFrom(getSQLiteIntArrayVersions())
        .addMigrations(roomMigrationProvider.getMigrations())
        .build();
  }

  @Singleton @Provides RoomMigrationProvider providesRoomMigrationProvider() {
    return new RoomMigrationProvider();
  }

  private int[] getSQLiteIntArrayVersions() {
    int minSQLiteVersion = 0;
    int maxSQLiteVersion = 60;
    int count = maxSQLiteVersion - minSQLiteVersion + 1;
    int[] SQLiteVersions = new int[count];
    for (int i = minSQLiteVersion; i <= maxSQLiteVersion; i++) {
      SQLiteVersions[i - minSQLiteVersion] = i;
    }
    return SQLiteVersions;
  }

  @Singleton @Provides RoomEventPersistence providesRoomEventPersistence(
      AptoideDatabase aptoideDatabase, RoomEventMapper roomEventMapper) {
    return new RoomEventPersistence(aptoideDatabase.eventDAO(), roomEventMapper);
  }

  @Singleton @Provides RoomEventMapper providesRoomEventMapper(
      @Named("default") ObjectMapper objectMapper) {
    return new RoomEventMapper(objectMapper);
  }

  @Singleton @Provides EventsPersistence providesEventsPersistence(AptoideDatabase aptoideDatabase,
      RoomEventMapper mapper) {
    return new RoomEventPersistence(aptoideDatabase.eventDAO(), mapper);
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
      @Named("mature-pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> accountSettingsBodyInterceptorPoolV7,
      @Named("default") SharedPreferences defaultSharedPreferences,
      TokenInvalidator tokenInvalidator, RequestBodyFactory requestBodyFactory,
      @Named("default") ObjectMapper nonNullObjectMapper, RoomStoreRepository storeRepository) {
    return new StoreManager(okHttpClient, WebService.getDefaultConverter(),
        multipartBodyInterceptor, bodyInterceptorV3, accountSettingsBodyInterceptorPoolV7,
        defaultSharedPreferences, tokenInvalidator, requestBodyFactory, nonNullObjectMapper,
        storeRepository);
  }

  @Singleton @Provides AdsRepository provideAdsRepository(IdsRepository idsRepository,
      AptoideAccountManager accountManager, @Named("default") OkHttpClient okHttpClient,
      QManager qManager, @Named("default") SharedPreferences defaultSharedPreferences,
      AdsApplicationVersionCodeProvider adsApplicationVersionCodeProvider,
      ConnectivityManager connectivityManager, OemidProvider oemidProvider) {
    return new AdsRepository(idsRepository, accountManager, okHttpClient,
        WebService.getDefaultConverter(), qManager, defaultSharedPreferences,
        application.getApplicationContext(), connectivityManager, application.getResources(),
        adsApplicationVersionCodeProvider, AdNetworkUtils::isGooglePlayServicesAvailable,
        oemidProvider.getOemid(), new MinimalAdMapper());
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
      @Named("aptoidePackage") String aptoidePackage, AptoideMd5Manager aptoideMd5Manager) {
    return new BodyInterceptorV3(idsRepository, aptoideMd5Manager, aptoidePackage, qManager,
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
      RoomStoreRepository storeRepository) {
    return new StoreCredentialsProviderImpl(storeRepository);
  }

  @Singleton @Provides TrendingService providesTrendingService(
      StoreCredentialsProvider storeCredentialsProvider,
      @Named("default") SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator,
      Converter.Factory converterFactory, @Named("default") OkHttpClient httpClient,
      @Named("mature-pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptor,
      AppBundlesVisibilityManager appBundlesVisibilityManager) {
    return new TrendingService(storeCredentialsProvider, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences, appBundlesVisibilityManager);
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

  @Singleton @Provides DownloadStatusManager providesDownloadStatusManager(
      InstallManager installManager, AppcMigrationManager appcMigrationManager) {
    return new DownloadStatusManager(installManager, appcMigrationManager);
  }

  @Singleton @Provides SearchSuggestionManager providesSearchSuggestionManager(
      SearchSuggestionRemoteRepository remoteRepository) {
    return new SearchSuggestionManager(new SearchSuggestionService(remoteRepository),
        Schedulers.io());
  }

  @Singleton @Provides MoPubAdsManager providesMoPubAdsManager(
      WalletAdsOfferManager walletAdsOfferManager) {
    return new MoPubAdsManager(walletAdsOfferManager);
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

  @Singleton @Provides AppsFlyerManager providesAppsFlyerManager(
      AppsFlyerRepository appsFlyerRepository) {
    return new AppsFlyerManager(appsFlyerRepository);
  }

  @Singleton @Provides AppsFlyerRepository providesAppsFlyerRepository(
      AppsFlyerService appsFlyerService) {
    return new AppsFlyerRepository(appsFlyerService);
  }

  @Singleton @Provides AppsFlyerService providesAppsFlyerService(
      @Named("apps-flyer-retrofit") Retrofit retrofit) {
    return retrofit.create(AppsFlyerService.class);
  }

  @Singleton @Provides @Named("apps-flyer-retrofit") Retrofit providesAppsFlyerRetrofit(
      @Named("appsflyer-host") String appsFlyerHost, @Named("default") OkHttpClient httpClient,
      Converter.Factory converterFactory, @Named("rx") CallAdapter.Factory rxCallAdapterFactory) {

    return new Retrofit.Builder().baseUrl(appsFlyerHost)
        .client(httpClient)
        .addConverterFactory(converterFactory)
        .addCallAdapterFactory(rxCallAdapterFactory)
        .build();
  }

  @Singleton @Provides @Named("appsflyer-host") String providesAppsFlyerBaseUrl() {
    return "https://impression.appsflyer.com";
  }

  @Singleton @Provides @Named("reactions-host") String providesReactionsHost() {
    return cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
        + "://"
        + cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_REACTIONS_HOST
        + "/";
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

  @Singleton @Provides @Named("base-webservices-host") String providesBaseWebservicesHost(
      @Named("default") SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_HOST
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

  @Singleton @Provides @Named("ab-test-service-provider")
  ABTestServiceProvider providesABTestServiceProvider(@Named("default") OkHttpClient httpClient,
      Converter.Factory converterFactory, @Named("rx") CallAdapter.Factory rxCallAdapterFactory,
      @Named("default") SharedPreferences sharedPreferences) {
    return new ABTestServiceProvider(httpClient, converterFactory, rxCallAdapterFactory,
        sharedPreferences);
  }

  @Singleton @Provides @Named("retrofit-load-top-reactions")
  Retrofit providesLoadTopReactionsRetrofit(@Named("reactions-host") String baseHost,
      @Named("v8") OkHttpClient httpClient, Converter.Factory converterFactory,
      @Named("rx") CallAdapter.Factory rxCallAdapterFactory) {
    return new Retrofit.Builder().baseUrl(baseHost)
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

  @Singleton @Provides BonusAppcRemoteService.ServiceApi providesBonusAppcServiceApi(
      @Named("retrofit-apichain-bds") Retrofit retrofit) {
    return retrofit.create(BonusAppcRemoteService.ServiceApi.class);
  }

  @Singleton @Provides BonusAppcService providesBonusAppcService(
      BonusAppcRemoteService.ServiceApi serviceApi) {
    return new BonusAppcRemoteService(serviceApi);
  }

  @Singleton @Provides SearchSuggestionRemoteRepository providesSearchSuggestionRemoteRepository(
      Retrofit retrofit) {
    return retrofit.create(SearchSuggestionRemoteRepository.class);
  }

  @Singleton @Provides RetrofitAptoideBiService.ServiceV7 providesAptoideBiService(
      @Named("retrofit-v7") Retrofit retrofit) {
    return retrofit.create(RetrofitAptoideBiService.ServiceV7.class);
  }

  @Singleton @Provides Service providesAutoUpdateService(@Named("retrofit-v7") Retrofit retrofit) {
    return retrofit.create(Service.class);
  }

  @Singleton @Provides ABTestService.ABTestingService providesABTestServiceV7(
      @Named("retrofit-AB") Retrofit retrofit) {
    return retrofit.create(ABTestService.ABTestingService.class);
  }

  @Singleton @Provides ReactionsRemoteService.ServiceV8 providesReactionsServiceV8(
      @Named("retrofit-load-top-reactions") Retrofit retrofit) {
    return retrofit.create(ReactionsRemoteService.ServiceV8.class);
  }

  @Singleton @Provides ReactionsService providesReactionsService(
      ReactionsRemoteService.ServiceV8 reactionServiceV8) {
    return new ReactionsRemoteService(reactionServiceV8, Schedulers.io());
  }

  @Singleton @Provides PromotionsService providesPromotionsService(@Named("mature-pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      Converter.Factory converterFactory, @Named("default") SharedPreferences sharedPreferences,
      SplitsMapper splitsMapper, AppBundlesVisibilityManager appBundlesVisibilityManager) {
    return new PromotionsService(bodyInterceptorPoolV7, okHttpClient, tokenInvalidator,
        converterFactory, sharedPreferences, splitsMapper, appBundlesVisibilityManager);
  }

  @Singleton @Provides CrashReport providesCrashReports() {
    return CrashReport.getInstance();
  }

  @Singleton @Provides AptoideBiEventService providesRetrofitAptoideBiService(
      RetrofitAptoideBiService.ServiceV7 serviceV7,
      @Named("analytics-interceptor") AnalyticsBodyInterceptorV7 bodyInterceptor) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return new RetrofitAptoideBiService(dateFormat, serviceV7, bodyInterceptor);
  }

  @Singleton @Provides FirstLaunchAnalytics providesFirstLaunchAnalytics(
      AnalyticsManager analyticsManager, AnalyticsLogger logger, SafetyNetClient safetyNetClient,
      GmsStatusValueProvider gmsStatusValueProvider) {
    return new FirstLaunchAnalytics(analyticsManager, logger, safetyNetClient,
        application.getPackageName(), gmsStatusValueProvider);
  }

  @Singleton @Provides GmsStatusValueProvider providesGmsStatusValueProvider() {
    return new GmsStatusValueProvider(application.getApplicationContext());
  }

  @Singleton @Provides SafetyNetClient providesSafetyNetClient() {
    return SafetyNet.getClient(application);
  }

  @Singleton @Provides MapToJsonMapper providesMapToJsonMapper() {
    return new MapToJsonMapper();
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

  @Singleton @Provides @Named("rakamEventLogger") EventLogger providesRakamEventLogger(
      AnalyticsLogger logger, MapToJsonMapper mapToJsonMapper) {
    return new RakamEventLogger(logger, mapToJsonMapper);
  }

  @Singleton @Provides @Named("indicativeEventLogger") EventLogger providesIndicativeEventLogger(
      AnalyticsLogger logger) {
    return new IndicativeEventLogger(logger);
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
      EventsPersistence persistence, AptoideBiEventService service,
      @Named("default") SharedPreferences preferences, AnalyticsLogger debugLogger) {
    return new AptoideBiEventLogger(
        new AptoideBiAnalytics(persistence, new SharedPreferencesSessionPersistence(preferences),
            service, new CompositeSubscription(), Schedulers.computation(),
            BuildConfig.ANALYTICS_EVENTS_INITIAL_DELAY_IN_MILLIS,
            BuildConfig.ANALYTICS_EVENTS_TIME_INTERVAL_IN_MILLIS, CrashReport.getInstance(),
            debugLogger), BuildConfig.ANALYTICS_SESSION_INTERVAL_IN_MILLIS);
  }

  @Singleton @Provides HttpKnockEventLogger providesknockEventLogger(
      @Named("default") OkHttpClient client) {
    return new HttpKnockEventLogger(client);
  }

  @Singleton @Provides @Named("aptoideEvents") Collection<String> provideAptoideEvents() {
    return Arrays.asList(FirstLaunchAnalytics.FIRST_LAUNCH_BI,
        FirstLaunchAnalytics.PLAY_PROTECT_EVENT, AppViewAnalytics.OPEN_APP_VIEW,
        NotificationAnalytics.NOTIFICATION_EVENT_NAME, AccountAnalytics.APTOIDE_EVENT_NAME,
        DownloadAnalytics.DOWNLOAD_EVENT_NAME, InstallAnalytics.INSTALL_EVENT_NAME,
        PromotionsAnalytics.VALENTINE_MIGRATOR);
  }

  @Singleton @Provides AnalyticsManager providesAnalyticsManager(
      @Named("aptoideLogger") EventLogger aptoideBiEventLogger,
      @Named("facebook") EventLogger facebookEventLogger,
      @Named("flurryLogger") EventLogger flurryEventLogger, HttpKnockEventLogger knockEventLogger,
      @Named("aptoideEvents") Collection<String> aptoideEvents,
      @Named("facebookEvents") Collection<String> facebookEvents,
      @Named("flurryEvents") Collection<String> flurryEvents,
      @Named("flurrySession") SessionLogger flurrySessionLogger,
      @Named("aptoideSession") SessionLogger aptoideSessionLogger,
      @Named("normalizer") AnalyticsEventParametersNormalizer analyticsNormalizer,
      @Named("rakamEventLogger") EventLogger rakamEventLogger,
      @Named("rakamEvents") Collection<String> rakamEvents,
      @Named("indicativeEventLogger") EventLogger indicativeEventLogger,
      @Named("indicativeEvents") Collection<String> indicativeEvents, AnalyticsLogger logger) {

    return new AnalyticsManager.Builder().addLogger(aptoideBiEventLogger, aptoideEvents)
        .addLogger(facebookEventLogger, facebookEvents)
        .addLogger(flurryEventLogger, flurryEvents)
        .addLogger(rakamEventLogger, rakamEvents)
        .addLogger(indicativeEventLogger, indicativeEvents)
        .addSessionLogger(flurrySessionLogger)
        .addSessionLogger(aptoideSessionLogger)
        .setKnockLogger(knockEventLogger)
        .setAnalyticsNormalizer(analyticsNormalizer)
        .setDebugLogger(logger)
        .build();
  }

  @Singleton @Provides @Named("rakamEvents") Collection<String> providesRakamEvents() {
    return Arrays.asList(InstallAnalytics.CLICK_ON_INSTALL, DownloadAnalytics.RAKAM_DOWNLOAD_EVENT,
        InstallAnalytics.RAKAM_INSTALL_EVENT, SearchAnalytics.SEARCH,
        SearchAnalytics.SEARCH_RESULT_CLICK, FirstLaunchAnalytics.FIRST_LAUNCH_RAKAM,
        HomeAnalytics.VANILLA_PROMOTIONAL_CARDS);
  }

  @Singleton @Provides @Named("indicativeEvents") Collection<String> providesIndicativeEvents() {
    return Arrays.asList(InstallAnalytics.CLICK_ON_INSTALL, DownloadAnalytics.RAKAM_DOWNLOAD_EVENT,
        InstallAnalytics.RAKAM_INSTALL_EVENT, SearchAnalytics.SEARCH,
        SearchAnalytics.SEARCH_RESULT_CLICK, FirstLaunchAnalytics.FIRST_LAUNCH_RAKAM,
        HomeAnalytics.VANILLA_PROMOTIONAL_CARDS, EskillsAnalytics.ESKILLS_PROMOTIONAL_CARD,
        EskillsAnalytics.ESKILLS_PROMOTIONAL_PAGE, EskillsAnalytics.ESKILLS_APP_CLICK);
  }

  @Singleton @Provides @Named("normalizer")
  AnalyticsEventParametersNormalizer providesAnalyticsNormalizer() {
    return new AnalyticsEventParametersNormalizer();
  }

  @Singleton @Provides AppShortcutsAnalytics providesAppShortcutsAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    return new AppShortcutsAnalytics(analyticsManager, navigationTracker);
  }

  @Singleton @Provides StoreAnalytics providesStoreAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    return new StoreAnalytics(analyticsManager, navigationTracker);
  }

  @Singleton @Provides AppService providesAppService(
      StoreCredentialsProvider storeCredentialsProvider, @Named("mature-pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences, SplitsMapper splitsMapper,
      AppBundlesVisibilityManager appBundlesVisibilityManager) {

    return new AppService(storeCredentialsProvider, bodyInterceptorPoolV7, okHttpClient,
        WebService.getDefaultConverter(), tokenInvalidator, sharedPreferences, splitsMapper,
        appBundlesVisibilityManager);
  }

  @Singleton @Provides AppBundlesVisibilityManager providesAppBundlesVisibilityManager(
      AppBundlesVisibilitySettingsProvider AppBundlesVisibilitySettingsProvider) {
    return new AppBundlesVisibilityManager(AptoideUtils.isMIUIwithAABFix(),
        AptoideUtils.isDeviceMIUI(), AppBundlesVisibilitySettingsProvider);
  }

  @Singleton @Provides
  AppBundlesVisibilitySettingsProvider providesAppBundlesVisibilitySettingsProvider(
      @Named("default") SharedPreferences sharedPreferences) {
    return new AppBundlesVisibilitySettingsProvider(sharedPreferences);
  }

  @Singleton @Provides AppCenterRepository providesAppCenterRepository(AppService appService) {
    return new AppCenterRepository(appService, new HashMap<>());
  }

  @Singleton @Provides AppCenter providesAppCenter(AppCenterRepository appCenterRepository) {
    return new AppCenter(appCenterRepository);
  }

  @Singleton @Provides AppCoinsAdvertisingManager providesAppCoinsAdvertisingManager(
      AppCoinsService appCoinsService) {
    return new AppCoinsAdvertisingManager(appCoinsService);
  }

  @Singleton @Provides AppCoinsManager providesAppCoinsManager(
      BonusAppcService bonusAppcService) {
    return new AppCoinsManager(bonusAppcService);
  }

  @Singleton @Provides AppCoinsService providesAppCoinsService(@Named("mature-pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences, Converter.Factory converterFactory,
      AppBundlesVisibilityManager appBundlesVisibilityManager) {
    return new AppCoinsService(okHttpClient, tokenInvalidator, sharedPreferences,
        bodyInterceptorPoolV7, converterFactory, appBundlesVisibilityManager);
  }

  @Named("remote") @Singleton @Provides BundleDataSource providesRemoteBundleDataSource(
      @Named("mature-pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, Converter.Factory converter,
      BundlesResponseMapper mapper, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences, AptoideAccountManager accountManager,
      PackageRepository packageRepository, IdsRepository idsRepository, QManager qManager,
      Resources resources, WindowManager windowManager, ConnectivityManager connectivityManager,
      AdsApplicationVersionCodeProvider adsApplicationVersionCodeProvider,
      OemidProvider oemidProvider, AppBundlesVisibilityManager appBundlesVisibilityManager,
      StoreCredentialsProvider storeCredentialsProvider, AppCoinsManager appCoinsManager) {
    return new RemoteBundleDataSource(5, new HashMap<>(), bodyInterceptorPoolV7, okHttpClient,
        converter, mapper, tokenInvalidator, sharedPreferences, new WSWidgetsUtils(),
        storeCredentialsProvider, idsRepository,
        AdNetworkUtils.isGooglePlayServicesAvailable(application.getApplicationContext()),
        oemidProvider.getOemid(), accountManager,
        qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)), resources,
        windowManager, connectivityManager, adsApplicationVersionCodeProvider, packageRepository,
        10, 10, appBundlesVisibilityManager, appCoinsManager);
  }

  @Singleton @Provides StorePersistence providesStorePersistence(AptoideDatabase aptoideDatabase) {
    return new RoomStorePersistence(aptoideDatabase.storeDao());
  }

  @Singleton @Provides RoomStoreRepository providesRoomStoreRepository(
      StorePersistence storePersistence) {
    return new RoomStoreRepository(storePersistence);
  }

  @Singleton @Provides BundlesRepository providesBundleRepository(
      @Named("remote") BundleDataSource remoteBundleDataSource) {
    return new BundlesRepository(remoteBundleDataSource, new HashMap<>(), new HashMap<>(), 5);
  }

  @Singleton @Provides AdMapper providesAdMapper() {
    return new AdMapper();
  }

  @Singleton @Provides Blacklister providesBlacklister(BlacklistPersistence blacklistPersistence) {
    return new Blacklister(blacklistPersistence);
  }

  @Singleton @Provides BlacklistPersistence providesBlacklistPersistence(
      @Named("default") SharedPreferences sharedPreferences) {
    return new BlacklistPersistence(sharedPreferences);
  }

  @Singleton @Provides BlacklistUnitMapper providesBundleToBlacklistUnitMapper() {
    return new BlacklistUnitMapper();
  }

  @Singleton @Provides BlacklistManager providesBlacklistManager(Blacklister blacklister,
      BlacklistUnitMapper blacklistUnitMapper) {
    return new BlacklistManager(blacklister, blacklistUnitMapper);
  }

  @Singleton @Provides AppComingSoonRegistrationManager providesAppComingSoonPreferencesManager(
      AppComingSoonRegistrationPersistence appComingSoonRegistrationPersistence) {
    return new AppComingSoonRegistrationManager(appComingSoonRegistrationPersistence);
  }

  @Singleton @Provides
  AppComingSoonRegistrationPersistence providesAppComingSoonRegistrationPersistence(
      AptoideDatabase database) {
    return new RoomAppComingSoonPersistence(database.appComingSoonRegistrationDAO());
  }

  @Singleton @Provides BundlesResponseMapper providesBundlesMapper(InstallManager installManager,
      WalletAdsOfferCardManager walletAdsOfferCardManager, BlacklistManager blacklistManager,
      DownloadStateParser downloadStateParser,
      AppComingSoonRegistrationManager appComingSoonRegistrationManager) {
    return new BundlesResponseMapper(installManager, walletAdsOfferCardManager, blacklistManager,
        downloadStateParser, appComingSoonRegistrationManager);
  }

  @Singleton @Provides UpdatesManager providesUpdatesManager(UpdateRepository updateRepository) {
    return new UpdatesManager(updateRepository);
  }

  @Singleton @Provides UpdateRepository providesUpdateRepository(
      UpdatePersistence updatePersistence, RoomStoreRepository storeRepository,
      IdsRepository idsRepository, @Named("mature-pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, @Named("default") SharedPreferences sharedPreferences,
      AppBundlesVisibilityManager appBundlesVisibilityManager, UpdateMapper updateMapper,
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository) {
    return new UpdateRepository(updatePersistence, storeRepository, idsRepository,
        bodyInterceptorPoolV7, okHttpClient, converterFactory, tokenInvalidator, sharedPreferences,
        appBundlesVisibilityManager, updateMapper, aptoideInstalledAppsRepository);
  }

  @Singleton @Provides UpdateMapper providesUpdateMapper() {
    return new UpdateMapper();
  }

  @Singleton @Provides AppViewAnalytics providesAppViewAnalytics(
      DownloadAnalytics downloadAnalytics, AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker, StoreAnalytics storeAnalytics,
      InstallAnalytics installAnalytics) {
    return new AppViewAnalytics(downloadAnalytics, analyticsManager, navigationTracker,
        storeAnalytics, installAnalytics);
  }

  @Singleton @Provides PreferencesPersister providesUserPreferencesPersister(
      @Named("default") SharedPreferences sharedPreferences) {
    return new PreferencesPersister(sharedPreferences);
  }

  @Singleton @Provides ReviewsManager providesReviewsManager(ReviewsRepository reviewsRepository) {
    return new ReviewsManager(reviewsRepository);
  }

  @Singleton @Provides ReviewsRepository providesReviewsRepository(ReviewsService reviewsService) {
    return new ReviewsRepository(reviewsService);
  }

  @Singleton @Provides ReviewsService providesReviewsService(
      StoreCredentialsProvider storeCredentialsProvider, @Named("mature-pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences) {
    return new ReviewsService(storeCredentialsProvider, bodyInterceptorPoolV7, okHttpClient,
        WebService.getDefaultConverter(), tokenInvalidator, sharedPreferences);
  }

  @Singleton @Provides AdsManager providesAdsManager(AdsRepository adsRepository,
      RoomStoredMinimalAdPersistence roomStoredMinimalAdPersistence) {
    return new AdsManager(adsRepository, roomStoredMinimalAdPersistence, new MinimalAdMapper());
  }

  @Singleton @Provides ABTestService providesABTestService(
      @Named("ab-test-service-provider") ABTestServiceProvider abTestServiceProvider,
      IdsRepository idsRepository) {
    return new ABTestService(abTestServiceProvider, idsRepository, Schedulers.io());
  }

  @Singleton @Provides RoomExperimentPersistence providesRoomExperimentPersistence(
      AptoideDatabase database, RoomExperimentMapper mapper) {
    return new RoomExperimentPersistence(database.experimentDAO(), mapper);
  }

  @Singleton @Provides RoomExperimentMapper providesRoomExperimentMapper() {
    return new RoomExperimentMapper();
  }

  @Singleton @Provides RoomStoredMinimalAdPersistence providesRoomStoreMinimalAdPersistence(
      AptoideDatabase database) {
    return new RoomStoredMinimalAdPersistence(database.storeMinimalAdDAO());
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
      ABTestService abTestService, RoomExperimentPersistence persistence,
      @Named("ab-test-local-cache") HashMap<String, ExperimentModel> localCache,
      AbTestCacheValidator cacheValidator) {
    return new ABTestCenterRepository(abTestService, localCache, persistence, cacheValidator);
  }

  @Singleton @Provides @Named("ab-test") ABTestManager providesABTestManager(
      ABTestCenterRepository abTestCenterRepository) {
    return new ABTestManager(abTestCenterRepository);
  }

  @Singleton @Provides PromotionsManager providePromotionsManager(InstallManager installManager,
      PromotionViewAppMapper promotionViewAppMapper, DownloadFactory downloadFactory,
      DownloadStateParser downloadStateParser, PromotionsAnalytics promotionsAnalytics,
      NotificationAnalytics notificationAnalytics, InstallAnalytics installAnalytics,
      PromotionsService promotionsService,
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository,
      MoPubAdsManager moPubAdsManager, WalletAppProvider walletAppProvider,
      DynamicSplitsManager dynamicSplitsManager, SplitAnalyticsMapper splitAnalyticsMapper) {
    return new PromotionsManager(promotionViewAppMapper, installManager, downloadFactory,
        downloadStateParser, promotionsAnalytics, notificationAnalytics, installAnalytics,
        application.getApplicationContext()
            .getPackageManager(), promotionsService, aptoideInstalledAppsRepository,
        moPubAdsManager, walletAppProvider, dynamicSplitsManager, splitAnalyticsMapper);
  }

  @Singleton @Provides WalletAppProvider providesWalletAppProvider(AppCenter appCenter,
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository, InstallManager installManager,
      DownloadStateParser downloadStateParser) {
    return new WalletAppProvider(appCenter, aptoideInstalledAppsRepository, installManager,
        downloadStateParser);
  }

  @Singleton @Provides PromotionViewAppMapper providesPromotionViewAppMapper(
      DownloadStateParser downloadStateParser) {
    return new PromotionViewAppMapper(downloadStateParser);
  }

  @Singleton @Provides DownloadStateParser providesDownloadStateParser() {
    return new DownloadStateParser();
  }

  @Singleton @Provides EditorialService providesEditorialService(@Named("mature-pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, @Named("default") SharedPreferences sharedPreferences,
      SplitsMapper splitsMapper) {
    return new EditorialService(bodyInterceptorPoolV7, okHttpClient, tokenInvalidator,
        converterFactory, sharedPreferences, splitsMapper);
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

  @Singleton @Provides MarketResourceFormatter provideMarketResourceFormatter(
      @Named("marketName") String marketName) {
    return new MarketResourceFormatter(marketName);
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
    return Arrays.asList("apps", "catappult");
  }

  @Singleton @Provides AptoideApplicationAnalytics provideAptoideApplicationAnalytics(
      AnalyticsManager analyticsManager) {
    return new AptoideApplicationAnalytics(analyticsManager);
  }

  @Singleton @Provides MoPubAnalytics providesMoPubAnalytics() {
    return new MoPubAnalytics();
  }

  @Singleton @Provides UserFeedbackAnalytics providesUserFeedbackAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    return new UserFeedbackAnalytics(analyticsManager, navigationTracker);
  }

  @Singleton @Provides SocialMediaAnalytics providesSocialMediaAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    return new SocialMediaAnalytics(analyticsManager, navigationTracker);
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
        AppViewAnalytics.CLICK_INSTALL, InstallAnalytics.NOTIFICATION_APPLICATION_INSTALL,
        InstallAnalytics.EDITORS_APPLICATION_INSTALL,
        DownloadAnalytics.NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME, SearchAnalytics.SEARCH,
        SearchAnalytics.NO_RESULTS, SearchAnalytics.APP_CLICK, SearchAnalytics.SEARCH_START,
        SearchAnalytics.AB_SEARCH_ACTION, SearchAnalytics.AB_SEARCH_IMPRESSION,
        AppViewAnalytics.EDITORS_CHOICE_CLICKS, AppViewAnalytics.APP_VIEW_INTERACT,
        NotificationAnalytics.NOTIFICATION_RECEIVED,
        NotificationAnalytics.NOTIFICATION_IMPRESSION, NotificationAnalytics.NOTIFICATION_PRESSED,
        UpdatesAnalytics.UPDATE_EVENT, PageViewsAnalytics.PAGE_VIEW_EVENT,
        FirstLaunchAnalytics.PLAY_PROTECT_EVENT, InstallEvents.ROOT_V2_COMPLETE,
        InstallEvents.ROOT_V2_START, AppViewAnalytics.SIMILAR_APP_INTERACT, AccountAnalytics.ENTRY,
        AppShortcutsAnalytics.APPS_SHORTCUTS, HomeAnalytics.HOME_INTERACT,
        HomeAnalytics.CURATION_CARD_CLICK, HomeAnalytics.CURATION_CARD_IMPRESSION,
        HomeAnalytics.HOME_CHIP_INTERACT, AccountAnalytics.PROMOTE_APTOIDE_EVENT_NAME,
        EditorialListAnalytics.EDITORIAL_BN_CURATION_CARD_CLICK,
        EditorialListAnalytics.EDITORIAL_BN_CURATION_CARD_IMPRESSION,
        BottomNavigationAnalytics.BOTTOM_NAVIGATION_INTERACT, DownloadAnalytics.DOWNLOAD_INTERACT,
        EditorialAnalytics.CURATION_CARD_INSTALL,
        EditorialAnalytics.EDITORIAL_BN_CURATION_CARD_INSTALL, EditorialAnalytics.REACTION_INTERACT,
        PromotionsAnalytics.PROMOTION_DIALOG, PromotionsAnalytics.PROMOTIONS_INTERACT,
        PromotionsAnalytics.VALENTINE_MIGRATOR, AppViewAnalytics.ADS_BLOCK_BY_OFFER,
        AppViewAnalytics.APPC_SIMILAR_APP_INTERACT, AppViewAnalytics.BONUS_MIGRATION_APPVIEW,
        AppViewAnalytics.BONUS_GAME_WALLET_OFFER_19, DeepLinkAnalytics.APPCOINS_WALLET_DEEPLINK,
        InstallEvents.MIUI_INSTALLATION_ABOVE_20_EVENT_NAME,
        AptoideApplicationAnalytics.IS_ANDROID_TV, ThemeAnalytics.DARK_THEME_INTERACT_EVENT,
        UserFeedbackAnalytics.USER_FEEDBACK_EVENT_NAME,
        InstallEvents.IS_INSTALLATION_TYPE_EVENT_NAME,
        AppValidationAnalytics.INVALID_DOWNLOAD_PATH_EVENT,
        SocialMediaAnalytics.PROMOTE_SOCIAL_MEDIA_EVENT_NAME,
        HomeAnalytics.VANILLA_PROMOTIONAL_CARDS));
    return flurryEvents;
  }

  @Singleton @Provides @Named("facebookEvents") Collection<String> provideFacebookEvents() {
    return Arrays.asList(InstallAnalytics.APPLICATION_INSTALL,
        InstallAnalytics.NOTIFICATION_APPLICATION_INSTALL,
        InstallAnalytics.EDITORS_APPLICATION_INSTALL,
        DownloadAnalytics.EDITORS_CHOICE_DOWNLOAD_COMPLETE_EVENT_NAME,
        DownloadAnalytics.NOTIFICATION_DOWNLOAD_COMPLETE_EVENT_NAME,
        DownloadAnalytics.DOWNLOAD_COMPLETE_EVENT, SearchAnalytics.SEARCH,
        SearchAnalytics.NO_RESULTS, SearchAnalytics.APP_CLICK, SearchAnalytics.SEARCH_START,
        SearchAnalytics.AB_SEARCH_ACTION, SearchAnalytics.AB_SEARCH_IMPRESSION,
        AppViewAnalytics.EDITORS_CHOICE_CLICKS, AppViewAnalytics.APP_VIEW_OPEN_FROM,
        AppViewAnalytics.APP_VIEW_INTERACT,
        NotificationAnalytics.NOTIFICATION_RECEIVED, NotificationAnalytics.NOTIFICATION_IMPRESSION,
        NotificationAnalytics.NOTIFICATION_PRESSED, StoreAnalytics.STORES_TAB_INTERACT,
        StoreAnalytics.STORES_OPEN, StoreAnalytics.STORES_INTERACT,
        AccountAnalytics.SIGN_UP_EVENT_NAME, AccountAnalytics.LOGIN_EVENT_NAME,
        UpdatesAnalytics.UPDATE_EVENT, PageViewsAnalytics.PAGE_VIEW_EVENT,
        FirstLaunchAnalytics.FIRST_LAUNCH, FirstLaunchAnalytics.PLAY_PROTECT_EVENT,
        InstallEvents.ROOT_V2_COMPLETE, InstallEvents.ROOT_V2_START,
        AppViewAnalytics.SIMILAR_APP_INTERACT, AccountAnalytics.LOGIN_SIGN_UP_START_SCREEN,
        AccountAnalytics.CREATE_USER_PROFILE, AccountAnalytics.PROFILE_SETTINGS,
        AccountAnalytics.ENTRY, DeepLinkAnalytics.FACEBOOK_APP_LAUNCH,
        AppViewAnalytics.CLICK_INSTALL, AppShortcutsAnalytics.APPS_SHORTCUTS,
        AccountAnalytics.CREATE_YOUR_STORE, HomeAnalytics.HOME_INTERACT,
        HomeAnalytics.CURATION_CARD_CLICK, HomeAnalytics.CURATION_CARD_IMPRESSION,
        HomeAnalytics.HOME_CHIP_INTERACT, AccountAnalytics.PROMOTE_APTOIDE_EVENT_NAME,
        EditorialListAnalytics.EDITORIAL_BN_CURATION_CARD_CLICK,
        EditorialListAnalytics.EDITORIAL_BN_CURATION_CARD_IMPRESSION,
        BottomNavigationAnalytics.BOTTOM_NAVIGATION_INTERACT, DownloadAnalytics.DOWNLOAD_INTERACT,
        EditorialAnalytics.CURATION_CARD_INSTALL,
        EditorialAnalytics.EDITORIAL_BN_CURATION_CARD_INSTALL, EditorialAnalytics.REACTION_INTERACT,
        PromotionsAnalytics.PROMOTION_DIALOG, PromotionsAnalytics.PROMOTIONS_INTERACT,
        PromotionsAnalytics.VALENTINE_MIGRATOR, AppViewAnalytics.ADS_BLOCK_BY_OFFER,
        AppViewAnalytics.APPC_SIMILAR_APP_INTERACT, AppViewAnalytics.BONUS_MIGRATION_APPVIEW,
        AppViewAnalytics.BONUS_GAME_WALLET_OFFER_19, DeepLinkAnalytics.APPCOINS_WALLET_DEEPLINK,
        InstallEvents.MIUI_INSTALLATION_ABOVE_20_EVENT_NAME,
        AptoideApplicationAnalytics.IS_ANDROID_TV, ThemeAnalytics.DARK_THEME_INTERACT_EVENT,
        UserFeedbackAnalytics.USER_FEEDBACK_EVENT_NAME,
        InstallEvents.IS_INSTALLATION_TYPE_EVENT_NAME,
        AppValidationAnalytics.INVALID_DOWNLOAD_PATH_EVENT,
        SocialMediaAnalytics.PROMOTE_SOCIAL_MEDIA_EVENT_NAME,
        HomeAnalytics.VANILLA_PROMOTIONAL_CARDS);
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

  @Singleton @Provides EskillsPreferencesManager providesEskillPreferencesManager(
      PreferencesPersister persister) {
    return new EskillsPreferencesManager(persister);
  }

  @Singleton @Provides PromotionsAnalytics providesPromotionsAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker,
      DownloadAnalytics downloadAnalytics, InstallAnalytics installAnalytics) {
    return new PromotionsAnalytics(analyticsManager, navigationTracker, downloadAnalytics,
        installAnalytics);
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

  @Singleton @Provides ReactionsManager providesReactionsManager(
      ReactionsService reactionsService) {
    return new ReactionsManager(reactionsService, new HashMap<>());
  }

  @Singleton @Provides AppInstaller providesAppInstaller(
      AppInstallerStatusReceiver appInstallerStatusReceiver) {
    return new AppInstaller(application.getApplicationContext(),
        (installStatus) -> appInstallerStatusReceiver.onStatusReceived(installStatus));
  }

  @Singleton @Provides AppInstallerStatusReceiver providesAppInstallerStatusReceiver() {
    return new AppInstallerStatusReceiver(PublishSubject.create());
  }

  @Singleton @Provides PackageInstallerManager providesPackageInstallerManager() {
    return new PackageInstallerManager(AptoideUtils.isDeviceMIUI(),
        AptoideUtils.isMIUIwithAABFix());
  }

  @Singleton @Provides NotificationProvider provideNotificationProvider(
      RoomNotificationPersistence notificationPersistence) {
    return new NotificationProvider(notificationPersistence, Schedulers.io());
  }

  @Singleton @Provides
  RoomLocalNotificationSyncPersistence providesRoomLocalNotificationSyncPersistence(
      AptoideDatabase database, NotificationProvider provider) {
    return new RoomLocalNotificationSyncPersistence(new RoomLocalNotificationSyncMapper(), provider,
        database.localNotificationSyncDao());
  }

  @Singleton @Provides LocalNotificationSyncManager providesLocalNotificationSyncManager(
      SyncScheduler syncScheduler, NotificationProvider provider) {
    return new LocalNotificationSyncManager(syncScheduler, true, provider);
  }

  @Singleton @Provides ChipManager providesChipManager() {
    return new ChipManager();
  }

  @Singleton @Provides AppcMigrationManager providesAppcMigrationManager(
      AptoideInstalledAppsRepository repository, AppcMigrationRepository appcMigrationRepository) {
    return new AppcMigrationManager(repository, appcMigrationRepository);
  }

  @Singleton @Provides AppcMigrationRepository providesAppcMigrationService(
      AppcMigrationPersistence appcMigrationPersistence) {
    return new AppcMigrationRepository(appcMigrationPersistence);
  }

  @Singleton @Provides AppcMigrationPersistence providesAppcMigrationAccessor(
      AptoideDatabase database) {
    return new RoomAppcMigrationPersistence(database.migratedAppDAO());
  }

  @Singleton @Provides CaptionBackgroundPainter providesCaptionBackgroundPainter() {
    return new CaptionBackgroundPainter(application.getApplicationContext()
        .getResources());
  }

  @Singleton @Provides AptoideMd5Manager providesAptoideMd5Manager(
      PreferencesPersister preferencesPersister) {
    return new AptoideMd5Manager(preferencesPersister, application.getPackageManager(),
        application.getPackageName(), BuildConfig.VERSION_CODE);
  }

  @Singleton @Provides SplitsMapper providesSplitsMapper() {
    return new SplitsMapper();
  }

  @Singleton @Provides @Named("base-rakam-host") String providesBaseRakamHost(
      @Named("default") SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_RAKAM_HOST;
  }

  @Singleton @Provides ImageInfoProvider providesImageInfoProvider() {
    return new ImageInfoProvider(application.getContentResolver());
  }

  @Singleton @Provides ThemeAnalytics providesThemeAnalytics(AnalyticsManager analyticsManager) {
    return new ThemeAnalytics(analyticsManager);
  }

  @Singleton @Provides NewFeature providesNewFeature() {
    return new NewFeature("dark_theme",
        application.getString(R.string.dark_theme_notification_title),
        application.getString(R.string.dark_theme_notification_body), "turn_it_on",
        R.string.dark_theme_notification_button);
  }

  @Singleton @Provides NewFeatureManager providesNewFeatureManager(
      @Named("default") SharedPreferences sharedPreferences, NewFeature newFeature,
      LocalNotificationSyncManager localNotificationSyncManager) {
    return new NewFeatureManager(sharedPreferences, localNotificationSyncManager, newFeature);
  }

  @Singleton @Provides AptoideInstallManager providesAptoideInstallManager(
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository,
      AptoideInstallRepository aptoideInstallRepository) {
    return new AptoideInstallManager(aptoideInstalledAppsRepository, aptoideInstallRepository);
  }

  @Singleton @Provides AptoideInstallRepository providesAptoideInstallRepository(
      AptoideInstallPersistence aptoideInstallPersistence) {
    return new AptoideInstallRepository(aptoideInstallPersistence);
  }

  @Singleton @Provides AptoideInstallPersistence providesAptoideInstallPersistence(
      AptoideDatabase database) {
    return new RoomAptoideInstallPersistence(database.aptoideInstallDao());
  }

  @Singleton @Provides AptoideWorkerFactory providesUpdatesNotificationWorkerFactory(
      UpdateRepository updateRepository, @Named("default") SharedPreferences sharedPreferences,
      AptoideInstallManager aptoideInstallManager, SyncScheduler syncScheduler,
      SyncStorage syncStorage, CrashReport crashReport, AppCenter appCenter) {
    return new AptoideWorkerFactory(updateRepository, sharedPreferences, aptoideInstallManager,
        new AppMapper(), syncScheduler, syncStorage, crashReport, appCenter);
  }

  @Singleton @Provides ComingSoonNotificationManager providesComingSoonNotificationManager(
      AppComingSoonRegistrationManager appComingSoonRegistrationManager) {
    return new ComingSoonNotificationManager(application.getApplicationContext(),
        appComingSoonRegistrationManager);
  }

  @Singleton @Provides UpdatesNotificationManager providesUpdatesNotificationManager() {
    return new UpdatesNotificationManager(application.getApplicationContext());
  }

  @Singleton @Provides AptoideAuthenticationRx providesAptoideAuthentication(
      @Named("base-webservices-host") String authenticationBaseHost,
      @Named("default") OkHttpClient okHttpClient) {
    return new AptoideAuthenticationRx(new AptoideAuthentication(
        new RemoteAuthenticationService(authenticationBaseHost, okHttpClient)));
  }

  @Singleton @Provides AgentPersistence providesAgentPersistence(
      @Named("secureShared") SharedPreferences secureSharedPreferences) {
    return new AgentPersistence(secureSharedPreferences);
  }

  @Singleton @Provides SearchRepository providesSearchRepository(
      RoomStoreRepository roomStoreRepository, @Named("mature-pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> baseBodyBodyInterceptor,
      @Named("default") SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator,
      @Named("default") OkHttpClient okHttpClient, Converter.Factory converterFactory,
      AppBundlesVisibilityManager appBundlesVisibilityManager, OemidProvider oemidProvider) {
    return new SearchRepository(roomStoreRepository, baseBodyBodyInterceptor, okHttpClient,
        converterFactory, tokenInvalidator, sharedPreferences, appBundlesVisibilityManager,
        oemidProvider);
  }

  @Singleton @Provides FileManager providesFileManager(CacheHelper cacheHelper,
      @Named("cachePath") String cachePath, AptoideDownloadManager aptoideDownloadManager,
      L2Cache httpClientCache) {
    return new FileManager(cacheHelper, new FileUtils(), new String[] {
        application.getCacheDir().getPath(), cachePath
    }, aptoideDownloadManager, httpClientCache);
  }

  @Singleton @Provides DynamicSplitsService providesDynamicSplitsService(
      DynamicSplitsRemoteService.DynamicSplitsApi dynamicSplitsApi,
      DynamicSplitsMapper dynamicSplitsMapper) {
    return new DynamicSplitsRemoteService(dynamicSplitsApi, dynamicSplitsMapper);
  }

  @Singleton @Provides DynamicSplitsMapper providesDynamicSplitsMapper() {
    return new DynamicSplitsMapper();
  }

  @Singleton @Provides DynamicSplitsManager providesDynamicSplitsManager(
      DynamicSplitsService dynamicSplitsService) {
    return new DynamicSplitsManager(dynamicSplitsService);
  }

  @Singleton @Provides DynamicSplitsRemoteService.DynamicSplitsApi providesDynamicSplitsApi(
      @Named("retrofit-v7") Retrofit retrofit) {
    return retrofit.create(DynamicSplitsRemoteService.DynamicSplitsApi.class);
  }

  @Singleton @Provides ApkfyManager provideApkfyManager(ApkfyService apkfyService) {
    return new ApkfyManager(apkfyService);
  }

  @Singleton @Provides ApkfyService provideApkfyService(AptoideApkfyService.ServiceApi serviceApi) {
    return new AptoideApkfyService(serviceApi);
  }

  @Singleton @Provides @Named("retrofit-aptoide-mmp") Retrofit providesAptoideMmpRetrofit(
      @Named("aptoide-mmp-base-host") String baseHost, @Named("default") OkHttpClient httpClient,
      Converter.Factory converterFactory, @Named("rx") CallAdapter.Factory rxCallAdapterFactory) {
    return new Retrofit.Builder().baseUrl(baseHost)
        .client(httpClient)
        .addCallAdapterFactory(rxCallAdapterFactory)
        .addConverterFactory(converterFactory)
        .build();
  }

  @Singleton @Provides AptoideApkfyService.ServiceApi providesApkfyServiceApi(
      @Named("retrofit-aptoide-mmp") Retrofit retrofit) {
    return retrofit.create(AptoideApkfyService.ServiceApi.class);
  }

  @Singleton @Provides @Named("aptoide-mmp-base-host") String provideAptoideMmpBaseHost() {
    return "https://" + BuildConfig.APTOIDE_WEB_SERVICES_MMP_HOST + "/api/v1/";
  }
}

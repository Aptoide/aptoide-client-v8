package cm.aptoide.pt;

import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountException;
import cm.aptoide.accountmanager.AccountFactory;
import cm.aptoide.accountmanager.AccountPersistence;
import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.Store;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.AccountSettingsBodyInterceptorV7;
import cm.aptoide.pt.account.AndroidAccountProvider;
import cm.aptoide.pt.account.FacebookSignUpAdapter;
import cm.aptoide.pt.account.GoogleSignUpAdapter;
import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.DownloadCompleteAnalytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.TrackerFilter;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.accessors.RealmToRealmDatabaseMigration;
import cm.aptoide.pt.database.accessors.RollbackAccessor;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.cache.L2Cache;
import cm.aptoide.pt.dataprovider.cache.POSTCacheInterceptor;
import cm.aptoide.pt.dataprovider.cache.POSTCacheKeyAlgorithm;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.DownloadInstallationProvider;
import cm.aptoide.pt.download.DownloadMirrorEventInterceptor;
import cm.aptoide.pt.download.PaidAppsDownloadInterceptor;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.file.CacheHelper;
import cm.aptoide.pt.install.InstallFabricEvents;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.install.Installer;
import cm.aptoide.pt.install.InstallerAnalytics;
import cm.aptoide.pt.install.RootInstallNotificationEventReceiver;
import cm.aptoide.pt.install.installer.DefaultInstaller;
import cm.aptoide.pt.install.installer.InstallationProvider;
import cm.aptoide.pt.install.installer.RollbackInstaller;
import cm.aptoide.pt.install.installer.RootInstallErrorNotificationFactory;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.install.rollback.RollbackFactory;
import cm.aptoide.pt.install.rollback.RollbackRepository;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import cm.aptoide.pt.networking.BodyInterceptorV7;
import cm.aptoide.pt.networking.Cdn;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.networking.MultipartBodyInterceptor;
import cm.aptoide.pt.networking.NoAuthenticationBodyInterceptorV3;
import cm.aptoide.pt.networking.NoOpTokenInvalidator;
import cm.aptoide.pt.networking.RefreshTokenInvalidator;
import cm.aptoide.pt.networking.UserAgentInterceptor;
import cm.aptoide.pt.notification.NotificationIdsMapper;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.notification.NotificationService;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.notification.PnpV1NotificationService;
import cm.aptoide.pt.notification.SystemNotificationShower;
import cm.aptoide.pt.notification.sync.NotificationSyncFactory;
import cm.aptoide.pt.notification.sync.NotificationSyncManager;
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
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.sync.SyncScheduler;
import cm.aptoide.pt.sync.SyncService;
import cm.aptoide.pt.sync.SyncStorage;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.view.ApplicationTestScope;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

/**
 * Created by jose_messejana on 09-11-2017.
 */

@Module public class ApplicationModuleTest{

  private final Application application;
  private final String imageCachePath;
  private final String cachePath;
  private final String accountType;
  private final String partnerId;
  private final String marketName;
  private final String extraId;
  private final String aptoidePackage;
  private final String aptoideMd5sum;
  private final LoginPreferences loginPreferences;

  public ApplicationModuleTest(Application application, String imageCachePath, String cachePath,
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


  @Named("default") @ApplicationTestScope @Provides InstallManager provideDefaultInstallManager(
      AptoideDownloadManager downloadManager, DownloadRepository downloadRepository,
      InstalledRepository installedRepository,
      @Named("default") SharedPreferences defaultSharedPreferences,
      @Named("secure") SharedPreferences secureSharedPreferences,
      RootAvailabilityManager rootAvailabilityManager,
      @Named("default") Installer defaultInstaller) {
    return new InstallManager(application, downloadManager, defaultInstaller,
        rootAvailabilityManager, defaultSharedPreferences, secureSharedPreferences,
        downloadRepository, installedRepository);
  }

  @ApplicationTestScope @Provides InstallerAnalytics provideInstallerAnalytics(Answers answers,
      AppEventsLogger appEventsLogger) {
    return new InstallFabricEvents(Analytics.getInstance(), answers, appEventsLogger);
  }

  @Named("rollback") @ApplicationTestScope @Provides InstallManager provideRollbackInstallManager(
      AptoideDownloadManager downloadManager, DownloadRepository downloadRepository,
      InstalledRepository installedRepository,
      @Named("default") SharedPreferences defaultSharedPreferences,
      @Named("secure") SharedPreferences secureSharedPreferences,
      RootAvailabilityManager rootAvailabilityManager,
      @Named("rollback") Installer rollbackInstaller) {
    return new InstallManager(application, downloadManager, rollbackInstaller,
        rootAvailabilityManager, defaultSharedPreferences, secureSharedPreferences,
        downloadRepository, installedRepository);
  }

  @ApplicationTestScope @Provides AptoideDownloadManager provideAptoideDownloadManager(
      AppEventsLogger appEventsLogger, DownloadAccessor downloadAccessor,
      @Named("user-agent") Interceptor userAgentInterceptor, CacheHelper cacheHelper,
      AuthenticationPersistence authenticationPersistence, Answers answers) {
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
            new DownloadCompleteAnalytics(Analytics.getInstance(), answers, appEventsLogger)),
        FileDownloader.getImpl(), cachePath, apkPath, obbPath);
  }

  @ApplicationTestScope @Provides @Named("default") Installer provideDefaultInstaller(
      InstallationProvider installationProvider,
      @Named("default") SharedPreferences sharedPreferences,
      InstalledRepository installedRepository, RootAvailabilityManager rootAvailabilityManager,
      InstallerAnalytics installerAnalytics) {
    return new DefaultInstaller(application.getPackageManager(), installationProvider,
        new FileUtils(), Analytics.getInstance(),
        ToolboxManager.isDebug(sharedPreferences) || BuildConfig.DEBUG, installedRepository, 180000,
        rootAvailabilityManager, sharedPreferences, installerAnalytics);
  }

  @ApplicationTestScope @Provides @Named("rollback") Installer provideRollbackInstaller(
      @Named("default") Installer defaultInstaller, RollbackRepository rollbackRepository,
      InstallationProvider installationProvider) {
    return new RollbackInstaller(defaultInstaller, rollbackRepository,
        new RollbackFactory(imageCachePath), installationProvider);
  }

  @ApplicationTestScope @Provides RollbackRepository provideRollbackRepository(
      RollbackAccessor rollbackAcessor) {
    return new RollbackRepository(rollbackAcessor);
  }

  @ApplicationTestScope @Provides RollbackAccessor provideRollbackAccessor(Database database) {
    return new RollbackAccessor(database);
  }

  @ApplicationTestScope @Provides InstallationProvider provideInstallationProvider(
      AptoideDownloadManager downloadManager, DownloadAccessor downloadAccessor,
      InstalledRepository installedRepository, Database database) {
    return new DownloadInstallationProvider(downloadManager, downloadAccessor, installedRepository,
        new MinimalAdMapper(), AccessorFactory.getAccessorFor(database, StoredMinimalAd.class));
  }

  @ApplicationTestScope @Provides CacheHelper provideCacheHelper(
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

  @ApplicationTestScope @Provides AppEventsLogger provideAppEventsLogger() {
    return AppEventsLogger.newLogger(application);
  }

  @ApplicationTestScope @Provides DownloadRepository provideDownloadRepository(Database database) {
    return new DownloadRepository(new DownloadAccessor(database));
  }

  @ApplicationTestScope @Provides Answers provideAnswers(Fabric fabric) {
    return fabric.getKit(Answers.class);
  }

  @ApplicationTestScope @Provides Crashlytics provideCrashlytics(Fabric fabric) {
    return fabric.getKit(Crashlytics.class);
  }

  @ApplicationTestScope @Provides TwitterCore provideTwitter(Fabric fabric) {
    return fabric.getKit(TwitterCore.class);
  }

  @ApplicationTestScope @Provides TwitterAuthClient provideTwitterAuthClient(Fabric fabric) {
    return new TwitterAuthClient();
  }

  @ApplicationTestScope @Provides Fabric provideFabric() {
    return Fabric.with(application, new Answers(), new Crashlytics.Builder().core(
        new CrashlyticsCore.Builder().disabled(BuildConfig.CRASH_REPORTS_DISABLED)
            .build())
        .build(), new TwitterCore(
        new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET)));
  }

  @ApplicationTestScope @Provides InstalledRepository provideInstalledRepository(Database database,
      InstalledAccessor installedAccessor) {
    return new InstalledRepository(installedAccessor);
  }

  @ApplicationTestScope @Provides DownloadFactory provideDownloadFactory() {
    return new DownloadFactory(marketName);
  }

  @ApplicationTestScope @Provides InstalledAccessor provideInstalledAccessor(Database database) {
    return new InstalledAccessor(database);
  }

  @ApplicationTestScope @Provides DownloadAccessor provideDownloadAccessor(Database database) {
    return new DownloadAccessor(database);
  }

  @ApplicationTestScope @Provides @Named("user-agent") Interceptor provideUserAgentInterceptor(
      AndroidAccountProvider androidAccountProvider, IdsRepository idsRepository) {
    return new UserAgentInterceptor(androidAccountProvider, idsRepository, partnerId,
        new DisplayMetrics(), AptoideUtils.SystemU.TERMINAL_INFO,
        AptoideUtils.Core.getDefaultVername(application));
  }

  @ApplicationTestScope @Provides @Named("retrofit-log") Interceptor provideRetrofitLogInterceptor() {
    return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
  }

  @ApplicationTestScope @Provides IdsRepository provideIdsRepository(
      @Named("secure") SharedPreferences secureSharedPreferences, ContentResolver contentResolver) {
    return new IdsRepository(secureSharedPreferences, application,
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID));
  }

  @ApplicationTestScope @Provides ContentResolver provideContentResolver() {
    return application.getContentResolver();
  }

  @ApplicationTestScope @Provides AndroidAccountProvider provideAndroidAccountProvider(
      AccountManager accountManager) {
    return new AndroidAccountProvider(accountManager, accountType, Schedulers.io());
  }

  @ApplicationTestScope @Provides PermissionManager providePermissionManager() {
    return new PermissionManager();
  }

  @ApplicationTestScope @Provides AccountManager provideAccountManager() {
    return AccountManager.get(application);
  }

  @ApplicationTestScope @Provides RootAvailabilityManager provideRootAvailabilityManager(
      @Named("secure") SharedPreferences secureSharedPreferences) {
    return new RootAvailabilityManager(new RootValueSaver() {
      final String IS_PHONE_ROOTED = "IS_PHONE_ROOTED";

      @Override public Single<Boolean> isPhoneRoot() {
        return Single.fromCallable(
            () -> secureSharedPreferences.getBoolean(IS_PHONE_ROOTED, false));
      }

      @Override public Completable save(boolean rootAvailable) {
        return Completable.fromAction(() -> secureSharedPreferences.edit()
            .putBoolean(IS_PHONE_ROOTED, rootAvailable)
            .commit());
      }
    });
  }

  @ApplicationTestScope @Provides Map<Integer, Result> provideFragmentNavigatorMap() {
    return new HashMap<>();
  }

  @ApplicationTestScope @Provides BehaviorRelay<Map<Integer, Result>> provideFragmentNavigatorRelay() {
    return BehaviorRelay.create();
  }

  @ApplicationTestScope @Provides Resources provideResources() {
    return application.getResources();
  }

  @ApplicationTestScope @Provides AuthenticationPersistence provideAuthenticationPersistence(
      AndroidAccountProvider androidAccountProvider) {
    return new AuthenticationPersistence(androidAccountProvider,
        ((AccountManager) application.getSystemService(Context.ACCOUNT_SERVICE)));
  }

  @ApplicationTestScope @Provides @Named("default") SharedPreferences getDefaultSharedPerefences() {
    return PreferenceManager.getDefaultSharedPreferences(application);
  }

  @ApplicationTestScope @Provides @Named("secure") SharedPreferences getSecureSharedPerefences(
      @Named("default") SharedPreferences defaultSharedPreferences) {
    return new SecurePreferencesImplementation(application, defaultSharedPreferences);
  }

  @ApplicationTestScope @Provides RootInstallationRetryHandler provideRootInstallationRetryHandler(
      @Named("rollback") InstallManager rollbackInstallManager) {
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

    final SystemNotificationShower systemNotificationShower =
        new SystemNotificationShower(application,
            (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE),
            new NotificationIdsMapper());
    int notificationId = 230498;
    return new RootInstallationRetryHandler(notificationId, systemNotificationShower,
        rollbackInstallManager, PublishRelay.create(), 0, application,
        new RootInstallErrorNotificationFactory(notificationId,
            BitmapFactory.decodeResource(application.getResources(), R.mipmap.ic_launcher), action,
            deleteAction));
  }

  @ApplicationTestScope @Provides NotificationSyncScheduler provideNotificationSyncScheduler(
      SyncScheduler syncScheduler, @Named("default") SharedPreferences sharedPreferences,
      NotificationProvider notificationProvider, NotificationService notificationService) {
    return new NotificationSyncManager(syncScheduler, true,
        new NotificationSyncFactory(sharedPreferences, notificationService, notificationProvider));
  }

  @ApplicationTestScope @Provides NotificationService providePnp1NotificationService(
      @Named("default") OkHttpClient defaultClient, IdsRepository idsRepository,
      @Named("default") SharedPreferences defaultSharedPreferences, Resources resources,
      AptoideAccountManager aptoideAccountManager,
      AuthenticationPersistence authenticationPersistence) {
    return new PnpV1NotificationService(BuildConfig.APPLICATION_ID, defaultClient,
        WebService.getDefaultConverter(), idsRepository, BuildConfig.VERSION_NAME, extraId,
        defaultSharedPreferences, resources, authenticationPersistence, aptoideAccountManager);
  }

  @ApplicationTestScope @Provides GoogleApiClient provideGoogleApiClient() {
    return new GoogleApiClient.Builder(application).addApi(GOOGLE_SIGN_IN_API,
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
            .requestScopes(new Scope(Scopes.PROFILE))
            .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
            .build())
        .build();
  }

  @ApplicationTestScope @Provides AptoideAccountManager provideAptoideAccountManager(AdultContent adultContent,
      StoreAccessor storeAccessor, @Named("default") OkHttpClient httpClient,
      @Named("long-timeout") OkHttpClient longTimeoutHttpClient, AccountManager accountManager,
      @Named("secure") SharedPreferences secureSharedPreferences,
      @Named("default") SharedPreferences defaultSharedPreferences,
      SecureCoderDecoder secureCoderDecoder, AuthenticationPersistence authenticationPersistence,
      RefreshTokenInvalidator tokenInvalidator, @Named("pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("web-v7")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorWebV7,
      @Named("multipart")
          MultipartBodyInterceptor multipartBodyInterceptor,
      AndroidAccountProvider androidAccountProvider, GoogleApiClient googleApiClient,
      @Named("no-authentication-v3") BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> noAuthenticationBodyInterceptorV3,
      ObjectMapper objectMapper) {
    FacebookSdk.sdkInitialize(application);
    final AccountFactory accountFactory = new AccountFactory();

      Account account = new Account() {
        @Override public List<Store> getSubscribedStores() {
          return new ArrayList<>();
        }

        @Override public String getId() {
          return "1";
        }

        @Override public String getNickname() {
          return "D01";
        }

        @Override public String getAvatar() {
          return "avatar";
        }

        @Override public boolean isAdultContentEnabled() {
          return true;
        }

        @Override public Access getAccess() {
          return Access.PRIVATE;
        }

        @Override public boolean isAccessConfirmed() {
          return true;
        }

        @Override public boolean isLoggedIn() {
          if (TestType.types.equals(TestType.TestTypes.LOGGEDIN)) return true;
          else return false;
        }

        @Override public String getEmail() {
          return "jose.messejana@aptoide.com";
        }

        @Override public Store getStore() {
          return null;
        }

        @Override public boolean hasStore() {
          return false;
        }

        @Override public boolean isPublicUser() {
          return false;
        }

        @Override public boolean isPublicStore() {
          return false;
        }
      };
      final AccountService accountService = new AccountService() {
        @Override public Single<Account> getAccount(String email, String password) {
          if (TestType.types.equals(TestType.TestTypes.SIGNSIGNUPTESTS)) {
            Logger.d("TAG123", "REGULAR");
            return Single.just(account);
          } else if (TestType.types.equals(TestType.TestTypes.SIGNINWRONG)) {
            Logger.d("TAG123", "SIGNINWRONG");
            return Single.error(new AccountException("invalid_grant"));
          }
          return Single.just(account);
        }

        @Override public Single<Account> createAccount(String email, String metadata, String name,
            String type) {
          return Single.just(account);
        }

        @Override public Single<Account> createAccount(String email, String password) {
          if (TestType.types.equals(TestType.TestTypes.SIGNSIGNUPTESTS)) {
            Logger.d("TAG123", "REGULAR");
            return Single.just(account);
          } else if (TestType.types.equals(TestType.TestTypes.USEDEMAIL)) {
            Logger.d("TAG123", "USEDEMAIL");
            List<ErrorResponse> list = new ArrayList<>();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.code = "WOP-9";
            list.add(errorResponse);
            return Single.error(new AccountException(list));
          } else if (TestType.types.equals(TestType.TestTypes.INVALIDEMAIL)) {
            Logger.d("TAG123", "INVALIDEMAIL");
            return Single.error(new AccountException("IARG_106"));
          }
          return Single.just(account);
        }

        @Override public Single<Account> getAccount() {
          return Single.just(account);
        }

        @Override public Completable updateAccount(String nickname, String avatarPath) {
          return Completable.complete();
        }

        @Override public Completable updateAccount(String accessLevel) {
          return Completable.complete();
        }

        @Override public Completable updateAccountUsername(String username) {
          return Completable.complete();
        }

        @Override public Completable unsubscribeStore(String storeName, String storeUserName, String storePassword) {
          return Completable.complete();
        }

        @Override public Completable subscribeStore(String storeName, String storeUserName, String storePassword) {
          return Completable.complete();
        }

        @Override public Completable updateAccount(boolean adultContentEnabled) {
          return Completable.complete();
        }

        @Override public Completable removeAccount() {
          return Completable.complete();
        }
      };
      final AccountPersistence accountPersistence = new AccountPersistence() {
        @Override public Completable saveAccount(Account account) {
          return Completable.complete();
        }

        @Override public Single<Account> getAccount() {
          return Single.just(account);
        }

        @Override public Completable removeAccount() {
          return Completable.complete();
        }
      };

      return new AptoideAccountManager.Builder().setAccountPersistence(accountPersistence)
          .setAccountService(accountService)
          .registerSignUpAdapter(GoogleSignUpAdapter.TYPE,
              new GoogleSignUpAdapter(googleApiClient, loginPreferences))
          .registerSignUpAdapter(FacebookSignUpAdapter.TYPE,
              new FacebookSignUpAdapter(Arrays.asList("email"), LoginManager.getInstance(),
                  loginPreferences))
          .build();
  }

  @ApplicationTestScope @Provides @Named("default") OkHttpClient provideOkHttpClient(
      @Named("user-agent") Interceptor userAgentInterceptor, L2Cache cache,
      @Named("default") SharedPreferences sharedPreferences,
      @Named("retrofit-log") Interceptor retrofitLogInterceptor) {
    final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    okHttpClientBuilder.readTimeout(45, TimeUnit.SECONDS);
    okHttpClientBuilder.writeTimeout(45, TimeUnit.SECONDS);

    final File cacheDirectory = new File("/");
    final int cacheMaxSize = 10 * 1024 * 1024;
    okHttpClientBuilder.cache(new Cache(cacheDirectory, cacheMaxSize)); // 10 MiB

    okHttpClientBuilder.addInterceptor(new POSTCacheInterceptor(cache));
    okHttpClientBuilder.addInterceptor(userAgentInterceptor);

    if (ToolboxManager.isToolboxEnableRetrofitLogs(sharedPreferences)) {
      okHttpClientBuilder.addInterceptor(retrofitLogInterceptor);
    }

    return okHttpClientBuilder.build();
  }

  @ApplicationTestScope @Provides @Named("long-timeout") OkHttpClient provideLongTimeoutOkHttpClient(
      @Named("user-agent") Interceptor userAgentInterceptor,
      @Named("default") SharedPreferences sharedPreferences,
      @Named("retrofit-log") Interceptor retrofitLogInterceptor) {
    final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    okHttpClientBuilder.addInterceptor(userAgentInterceptor);
    okHttpClientBuilder.connectTimeout(2, TimeUnit.MINUTES);
    okHttpClientBuilder.readTimeout(2, TimeUnit.MINUTES);
    okHttpClientBuilder.writeTimeout(2, TimeUnit.MINUTES);

    if (ToolboxManager.isToolboxEnableRetrofitLogs(sharedPreferences)) {
      okHttpClientBuilder.addInterceptor(retrofitLogInterceptor);
    }

    return okHttpClientBuilder.build();
  }

  @ApplicationTestScope @Provides public ObjectMapper provideNonNullObjectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper;
  }

  @ApplicationTestScope @Provides L2Cache provideL2Cache() {
    return new L2Cache(new POSTCacheKeyAlgorithm(),
        new File(application.getCacheDir(), AptoideApplication.CACHE_FILE_NAME));
  }

  @ApplicationTestScope @Provides NotificationProvider provideNotificationProvider(
      NotificationAccessor notificationAccessor) {
    return new NotificationProvider(notificationAccessor, Schedulers.io());
  }

  @ApplicationTestScope @Provides NotificationAccessor provideNotificationAccessor(Database database) {
    return new NotificationAccessor(database);
  }

  @ApplicationTestScope @Provides SyncScheduler provideSyncScheduler(SyncStorage syncStorage) {
    return new SyncScheduler(application, SyncService.class,
        (AlarmManager) application.getSystemService(Context.ALARM_SERVICE), syncStorage);
  }

  @ApplicationTestScope @Provides SyncStorage provideSyncStorage() {
    return new SyncStorage(new HashMap<>());
  }

  @ApplicationTestScope @Provides StoreUtilsProxy provideStoreUtilsProxy(AptoideAccountManager accountManager,
      StoreAccessor storeAccessor, @Named("default") OkHttpClient httpClient,
      @Named("default") SharedPreferences sharedPreferences, RefreshTokenInvalidator tokenInvalidator,
      @Named("account-settings-pool-v7")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptor) {
    return new StoreUtilsProxy(accountManager, bodyInterceptor,
        new StoreCredentialsProviderImpl(storeAccessor), storeAccessor, httpClient,
        WebService.getDefaultConverter(), tokenInvalidator, sharedPreferences);
  }

  @ApplicationTestScope @Provides RefreshTokenInvalidator provideTokenInvalidator(
      @Named("default") OkHttpClient httpClient,
      @Named("default") SharedPreferences sharedPreferences,
      AuthenticationPersistence authenticationPersistence,
      @Named("no-authentication-v3") BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptor) {
    return new RefreshTokenInvalidator(bodyInterceptor, httpClient,
        WebService.getDefaultConverter(), sharedPreferences, extraId, new NoOpTokenInvalidator(),
        authenticationPersistence);
  }

  @ApplicationTestScope @Provides @Named("no-authentication-v3")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> provideNoAuthenticationBodyInterceptorV3(IdsRepository idsRepository) {
    return new NoAuthenticationBodyInterceptorV3(idsRepository, aptoideMd5sum, aptoidePackage);
  }

  @ApplicationTestScope @Provides @Named("account-settings-pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> provideAccountSettingsBodyInterceptorPoolV7(
      @Named("pool-v7") BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptor,
      AdultContent adultContent) {
    if(TestType.types.equals(TestType.TestTypes.MATURE)) {
      return new AccountSettingsBodyInterceptorV7(bodyInterceptor, adultContent) {
        @Override public Single<BaseBody> intercept(BaseBody body) {
          return Single.zip(bodyInterceptor.intercept(body), adultContent.enabled()
              .first()
              .toSingle(), (bodyV7, adultContentEnabled) -> {
            bodyV7.setMature(adultContentEnabled);
            if (bodyV7.isMature() == adultContentEnabled){
              return bodyV7;
            }
            return null;
          });
        }
      };
    }
    else{
        return new AccountSettingsBodyInterceptorV7(bodyInterceptor, adultContent);
      }
  }

  @ApplicationTestScope @Provides @Named("pool-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> provideBodyInterceptorPoolV7(
      AuthenticationPersistence authenticationPersistence, IdsRepository idsRepository,
      @Named("default") SharedPreferences sharedPreferences, Resources resources,
      QManager qManager) {
    return new BodyInterceptorV7(idsRepository, authenticationPersistence, aptoideMd5sum,
        aptoidePackage, qManager, Cdn.POOL, sharedPreferences, resources, BuildConfig.VERSION_CODE);
  }

  @ApplicationTestScope @Provides @Named("multipart")
  MultipartBodyInterceptor provideMultipartBodyInterceptor(
      IdsRepository idsRepository, AuthenticationPersistence authenticationPersistence,
      RequestBodyFactory requestBodyFactory) {
    return new MultipartBodyInterceptor(idsRepository, requestBodyFactory,
        authenticationPersistence);
  }

  @ApplicationTestScope @Provides RequestBodyFactory provideRequestBodyFactory() {
    return new RequestBodyFactory();
  }

  @ApplicationTestScope @Provides @Named("web-v7")
  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> provideBodyInterceptorWebV7(
      AuthenticationPersistence authenticationPersistence, IdsRepository idsRepository,
      @Named("default") SharedPreferences sharedPreferences, Resources resources,
      QManager qManager) {
    return new BodyInterceptorV7(idsRepository, authenticationPersistence, aptoideMd5sum,
        aptoidePackage, qManager, Cdn.WEB, sharedPreferences, resources, BuildConfig.VERSION_CODE);
  }

  @ApplicationTestScope @Provides QManager provideQManager(
      @Named("default") SharedPreferences sharedPreferences, Resources resources) {
    return new QManager(sharedPreferences, resources,
        ((ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE)),
        ((WindowManager) application.getSystemService(Context.WINDOW_SERVICE)));
  }

  @ApplicationTestScope @Provides AdultContent provideLocalAdultContent(Preferences preferences,
      SecurePreferences securePreferences) {
    return new LocalPersistenceAdultContent(preferences, securePreferences);
  }

  @ApplicationTestScope @Provides Preferences provideDefaultPreferences(
      @Named("default") SharedPreferences sharedPreferences) {
    return new Preferences(sharedPreferences);
  }

  @ApplicationTestScope @Provides SecurePreferences provideSecurePreferences(
      @Named("default") SharedPreferences sharedPreferences,
      SecureCoderDecoder secureCoderDecoder) {
    return new SecurePreferences(sharedPreferences, secureCoderDecoder);
  }

  @ApplicationTestScope @Provides StoreAccessor provideStoreAccessor(Database database) {
    return new StoreAccessor(database);
  }

  @ApplicationTestScope @Provides SecureCoderDecoder provideSecureCoderDecoder(
      @Named("default") SharedPreferences sharedPreferences) {
    return new SecureCoderDecoder.Builder(application, sharedPreferences).create();
  }

  @ApplicationTestScope @Provides StoreRepository provideStoreRepository(StoreAccessor storeAccessor) {
    return new StoreRepository(storeAccessor);
  }

  @ApplicationTestScope @Provides PageViewsAnalytics providePageViewsAnalytics(AppEventsLogger appEventsLogger,
      NavigationTracker navigationTracker) {
    return new PageViewsAnalytics(appEventsLogger, Analytics.getInstance(), navigationTracker);
  }

  @ApplicationTestScope @Provides NavigationTracker provideNavigationTracker() {
    return new NavigationTracker(new ArrayList<>(), new TrackerFilter());
  }

  @ApplicationTestScope @Provides Database provideDatabase() {
    Realm.init(application);
    final RealmConfiguration realmConfiguration =
        new RealmConfiguration.Builder().name(BuildConfig.REALM_FILE_NAME)
            .schemaVersion(BuildConfig.REALM_SCHEMA_VERSION)
            .migration(new RealmToRealmDatabaseMigration())
            .build();
    Realm.setDefaultConfiguration(realmConfiguration);
    return new Database();
  }

  @ApplicationTestScope @Provides CallbackManager provideCallbackManager(){
    return new CallbackManagerImpl();
  }

  @ApplicationTestScope @Provides AccountAnalytics provideAccountAnalytics(@Named("pool-v7") BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient defaulClient, RefreshTokenInvalidator tokenInvalidator, @Named ("default") SharedPreferences defaultSharedPreferences,
      AppEventsLogger appEventsLogger, NavigationTracker navigationTracker){
    return new AccountAnalytics(Analytics.getInstance(), bodyInterceptorPoolV7,
        defaulClient, WebService.getDefaultConverter(), tokenInvalidator,
        BuildConfig.APPLICATION_ID, defaultSharedPreferences, appEventsLogger,
        navigationTracker);
  }

  @ApplicationTestScope @Provides StoreManager provideStoreManager(AptoideAccountManager accountManager, @Named("default") OkHttpClient okHttpClient,
      @Named("multipart")MultipartBodyInterceptor multipartBodyInterceptor,
      @Named("no-authentication-v3") BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3,
      @Named("account-settings-pool-v7") BodyInterceptor<BaseBody> accountSettingsBodyInterceptorPoolV7, @Named("default") SharedPreferences defaultSharedPreferences,
      RefreshTokenInvalidator tokenInvalidator, RequestBodyFactory requestBodyFactory, ObjectMapper nonNullObjectMapper
      ){
      final StoreManager storeManager = new StoreManager(accountManager, okHttpClient, WebService.getDefaultConverter(),
        multipartBodyInterceptor, bodyInterceptorV3,
        accountSettingsBodyInterceptorPoolV7, defaultSharedPreferences,
        tokenInvalidator, requestBodyFactory, nonNullObjectMapper){
        @Override
        protected Completable createStore(String a, String b, String c, boolean d, String e){
          return Completable.complete();
        }
      };
      return storeManager;
  }
}

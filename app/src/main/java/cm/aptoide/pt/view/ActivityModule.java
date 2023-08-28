package cm.aptoide.pt.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.AppShortcutsAnalytics;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.CatappultNavigator;
import cm.aptoide.pt.DeepLinkAnalytics;
import cm.aptoide.pt.DeepLinkIntentReceiver;
import cm.aptoide.pt.R;
import cm.aptoide.pt.UserFeedbackAnalytics;
import cm.aptoide.pt.aab.DynamicSplitsManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.AgentPersistence;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.account.view.ImagePickerNavigator;
import cm.aptoide.pt.account.view.PhotoFileGenerator;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.account.view.store.ManageStoreNavigator;
import cm.aptoide.pt.account.view.user.ManageUserNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.DownloadStateParser;
import cm.aptoide.pt.app.view.AppViewNavigator;
import cm.aptoide.pt.autoupdate.AutoUpdateManager;
import cm.aptoide.pt.autoupdate.AutoUpdateRepository;
import cm.aptoide.pt.autoupdate.AutoUpdateService;
import cm.aptoide.pt.autoupdate.Service;
import cm.aptoide.pt.bottomNavigation.BottomNavigationAnalytics;
import cm.aptoide.pt.bottomNavigation.BottomNavigationMapper;
import cm.aptoide.pt.bottomNavigation.BottomNavigationNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.aab.AppBundlesVisibilityManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.SplitAnalyticsMapper;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.home.more.apps.ListAppsMoreRepository;
import cm.aptoide.pt.install.AppInstallerStatusReceiver;
import cm.aptoide.pt.install.AptoideInstalledAppsRepository;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallCompletedNotifier;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.navigator.ExternalNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.FragmentResultNavigator;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.notification.ContentPuller;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.notification.ReadyToInstallNotificationManager;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.permission.AccountPermissionProvider;
import cm.aptoide.pt.permission.PermissionProvider;
import cm.aptoide.pt.presenter.MainPresenter;
import cm.aptoide.pt.presenter.MainView;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.promotions.ClaimPromotionsNavigator;
import cm.aptoide.pt.promotions.PromotionsNavigator;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.socialmedia.SocialMediaNavigator;
import cm.aptoide.pt.store.RoomStoreRepository;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.themes.NewFeature;
import cm.aptoide.pt.themes.ThemeAnalytics;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.util.ApkFyManager;
import cm.aptoide.pt.util.MarketResourceFormatter;
import cm.aptoide.pt.view.app.ListStoreAppsNavigator;
import cm.aptoide.pt.view.dialog.DialogUtils;
import cm.aptoide.pt.view.settings.MyAccountNavigator;
import cm.aptoide.pt.wallet.WalletAppProvider;
import cm.aptoide.pt.wallet.WalletInstallAnalytics;
import cm.aptoide.pt.wallet.WalletInstallConfiguration;
import cm.aptoide.pt.wallet.WalletInstallManager;
import cm.aptoide.pt.wallet.WalletInstallNavigator;
import cm.aptoide.pt.wallet.WalletInstallPresenter;
import cm.aptoide.pt.wallet.WalletInstallView;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import dagger.Module;
import dagger.Provides;
import java.util.Map;
import javax.inject.Named;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.WINDOW_SERVICE;

@Module public class ActivityModule {

  private final AppCompatActivity activity;
  private final Intent intent;
  private final NotificationSyncScheduler notificationSyncScheduler;
  private final View view;
  private final String fileProviderAuthority;
  private final boolean firstCreated;

  public ActivityModule(AppCompatActivity activity, Intent intent,
      NotificationSyncScheduler notificationSyncScheduler, View view, boolean firstCreated,
      String fileProviderAuthority) {
    this.activity = activity;
    this.intent = intent;
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.view = view;
    this.firstCreated = firstCreated;
    this.fileProviderAuthority = fileProviderAuthority;
  }

  @ActivityScope @Provides ApkFyManager provideApkFy(
      @Named("secureShared") SharedPreferences securePreferences) {
    return new ApkFyManager(activity, intent, securePreferences);
  }

  @ActivityScope @Provides AutoUpdateService providesAutoUpdateService(Service service,
      @Named("package-name") String packageName,
      @Named("client-sdk-version") int clientSdkVersion) {
    return new AutoUpdateService(service, packageName, clientSdkVersion);
  }

  @ActivityScope @Provides AutoUpdateRepository providesAutoUpdateRepository(
      AutoUpdateService autoUpdateService) {
    return new AutoUpdateRepository(autoUpdateService);
  }

  @ActivityScope @Provides @Named("main-fragment-navigator")
  FragmentNavigator provideMainFragmentNavigator(Map<Integer, Result> fragmentResultMap,
      BehaviorRelay<Map<Integer, Result>> fragmentResultRelay, FragmentManager fragmentManager) {
    return new FragmentResultNavigator(fragmentManager, R.id.fragment_placeholder,
        android.R.anim.fade_in, android.R.anim.fade_out, fragmentResultMap, fragmentResultRelay);
  }

  @ActivityScope @Provides FragmentManager provideFragmentManager() {
    return activity.getSupportFragmentManager();
  }

  @ActivityScope @Provides SearchNavigator providesSearchNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      AppNavigator appNavigator) {
    return new SearchNavigator(fragmentNavigator, appNavigator);
  }

  @ActivityScope @Provides DeepLinkManager provideDeepLinkManager(StoreUtilsProxy storeUtilsProxy,
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      BottomNavigationNavigator bottomNavigationNavigator, SearchNavigator searchNavigator,
      @Named("default") SharedPreferences sharedPreferences,
      RoomStoreRepository roomStoreRepository, NavigationTracker navigationTracker,
      SearchAnalytics searchAnalytics, DeepLinkAnalytics deepLinkAnalytics,
      AppShortcutsAnalytics appShortcutsAnalytics, AptoideAccountManager accountManager,
      StoreAnalytics storeAnalytics, AdsRepository adsRepository, AppNavigator appNavigator,
      InstallManager installManager, NewFeature newFeature, ThemeManager themeManager,
      ThemeAnalytics themeAnalytics,
      ReadyToInstallNotificationManager readyToInstallNotificationManager) {
    return new DeepLinkManager(storeUtilsProxy, fragmentNavigator, bottomNavigationNavigator,
        searchNavigator, (DeepLinkManager.DeepLinkView) activity, sharedPreferences,
        roomStoreRepository, navigationTracker, searchAnalytics, appShortcutsAnalytics,
        accountManager, deepLinkAnalytics, storeAnalytics, adsRepository, appNavigator,
        installManager, newFeature, themeManager, themeAnalytics,
        readyToInstallNotificationManager);
  }

  @ActivityScope @Provides Presenter provideMainPresenter(
      RootInstallationRetryHandler rootInstallationRetryHandler, ApkFyManager apkFyManager,
      InstallManager installManager, @Named("default") SharedPreferences sharedPreferences,
      @Named("secureShared") SharedPreferences secureSharedPreferences,
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      DeepLinkManager deepLinkManager, BottomNavigationNavigator bottomNavigationNavigator,
      UpdatesManager updatesManager, AutoUpdateManager autoUpdateManager,
      RootAvailabilityManager rootAvailabilityManager,
      BottomNavigationMapper bottomNavigationMapper, AptoideAccountManager accountManager,
      AccountNavigator accountNavigator, AgentPersistence agentPersistence) {
    return new MainPresenter((MainView) view, installManager, rootInstallationRetryHandler,
        CrashReport.getInstance(), apkFyManager, new ContentPuller(activity),
        notificationSyncScheduler,
        new InstallCompletedNotifier(PublishRelay.create(), installManager,
            CrashReport.getInstance()), sharedPreferences, secureSharedPreferences,
        fragmentNavigator, deepLinkManager, firstCreated, (AptoideBottomNavigator) activity,
        AndroidSchedulers.mainThread(), Schedulers.io(), bottomNavigationNavigator, updatesManager,
        autoUpdateManager, (PermissionService) activity, rootAvailabilityManager,
        bottomNavigationMapper, accountManager, accountNavigator, agentPersistence);
  }

  @ActivityScope @Provides AccountNavigator provideAccountNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      AptoideAccountManager accountManager, CallbackManager callbackManager,
      GoogleApiClient googleApiClient, AccountAnalytics accountAnalytics,
      BottomNavigationNavigator bottomNavigationNavigator, ThemeManager themeManager) {
    return new AccountNavigator(bottomNavigationNavigator, fragmentNavigator, accountManager,
        ((ActivityNavigator) activity), LoginManager.getInstance(), callbackManager,
        googleApiClient, PublishRelay.create(), "http://m.aptoide.com/account/password-recovery",
        accountAnalytics, themeManager);
  }

  @ActivityScope @Provides ScreenOrientationManager provideScreenOrientationManager() {
    return new ScreenOrientationManager(activity,
        (WindowManager) activity.getSystemService(WINDOW_SERVICE));
  }

  @ActivityScope @Provides AccountPermissionProvider provideAccountPermissionProvider() {
    return new AccountPermissionProvider(((PermissionProvider) activity));
  }

  @ActivityScope @Provides PhotoFileGenerator providePhotoFileGenerator() {
    return new PhotoFileGenerator(activity,
        activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileProviderAuthority);
  }

  @ActivityScope @Provides UriToPathResolver provideUriToPathResolver() {
    return new UriToPathResolver(activity.getContentResolver());
  }

  @ActivityScope @Provides ImagePickerNavigator provideImagePickerNavigator() {
    return new ImagePickerNavigator((ActivityNavigator) activity);
  }

  @ActivityScope @Provides ManageStoreNavigator provideManageStoreNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      BottomNavigationNavigator bottomNavigationNavigator) {
    return new ManageStoreNavigator(fragmentNavigator, bottomNavigationNavigator);
  }

  @ActivityScope @Provides ManageUserNavigator provideManageUserNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      BottomNavigationNavigator bottomNavigationNavigator) {
    return new ManageUserNavigator(fragmentNavigator, bottomNavigationNavigator);
  }

  @ActivityScope @Provides ListStoreAppsNavigator provideListStoreAppsNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      AppNavigator appNavigator) {
    return new ListStoreAppsNavigator(fragmentNavigator, appNavigator);
  }

  @ActivityScope @Provides MyAccountNavigator provideMyAccountNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      AccountNavigator accountNavigator, AppNavigator appNavigator, ThemeManager themeManager,
      SocialMediaNavigator socialMediaNavigator) {
    return new MyAccountNavigator(fragmentNavigator, accountNavigator, appNavigator, themeManager,
        socialMediaNavigator);
  }

  @ActivityScope @Provides BottomNavigationMapper provideBottomNavigationMapper() {
    return new BottomNavigationMapper();
  }

  @ActivityScope @Provides BottomNavigationNavigator provideBottomNavigationNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      BottomNavigationAnalytics bottomNavigationAnalytics, SearchAnalytics searchAnalytics,
      ThemeManager themeManager) {
    return new BottomNavigationNavigator(fragmentNavigator, bottomNavigationAnalytics,
        searchAnalytics, themeManager);
  }

  @ActivityScope @Provides BottomNavigationAnalytics providesBottomNavigationAnalytics(
      AnalyticsManager manager, NavigationTracker tracker) {
    return new BottomNavigationAnalytics(manager, tracker);
  }

  @ActivityScope @Provides AppViewNavigator providesAppViewNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      AppNavigator appNavigator, CatappultNavigator catappultNavigator) {
    return new AppViewNavigator(fragmentNavigator, (ActivityNavigator) activity, appNavigator,
        catappultNavigator);
  }

  @ActivityScope @Provides DialogUtils providesDialogUtils(AptoideAccountManager accountManager,
      AccountNavigator accountNavigator,
      @Named("mature-pool-v7") BodyInterceptor<BaseBody> bodyInterceptor,
      @Named("default") OkHttpClient httpClient, Converter.Factory converterFactory,
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository,
      TokenInvalidator tokenInvalidator, @Named("default") SharedPreferences sharedPreferences,
      MarketResourceFormatter marketResourceFormatter, ThemeManager themeManager,
      UserFeedbackAnalytics userFeedbackAnalaytics) {
    return new DialogUtils(accountManager, accountNavigator, bodyInterceptor, httpClient,
        converterFactory, aptoideInstalledAppsRepository, tokenInvalidator, sharedPreferences,
        marketResourceFormatter, themeManager, userFeedbackAnalaytics);
  }

  @ActivityScope @Provides AppNavigator providesAppNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator) {
    return new AppNavigator(fragmentNavigator);
  }

  @ActivityScope @Provides AppCoinsInfoNavigator providesAppCoinsInfoNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      SocialMediaNavigator socialMediaNavigator, CatappultNavigator catappultNavigator) {
    return new AppCoinsInfoNavigator(fragmentNavigator, socialMediaNavigator, catappultNavigator);
  }

  @ActivityScope @Provides ExternalNavigator providesExternalNavigator(ThemeManager themeManager) {
    return new ExternalNavigator(activity.getApplicationContext(), themeManager);
  }

  @ActivityScope @Provides CatappultNavigator providesCatappultNavigator(
      ExternalNavigator externalNavigator) {
    return new CatappultNavigator(externalNavigator);
  }

  @ActivityScope @Provides @Named("screenHeight") float providesScreenHeight(Resources resources) {
    return resources.getDisplayMetrics().heightPixels;
  }

  @ActivityScope @Provides @Named("screenWidth") float providesScreenWidth(Resources resources) {
    return resources.getDisplayMetrics().widthPixels;
  }

  @ActivityScope @Provides AutoUpdateManager provideAutoUpdateManager(
      DownloadFactory downloadFactory, PermissionManager permissionManager,
      InstallManager installManager, DownloadAnalytics downloadAnalytics,
      @Named("local-version-code") int localVersionCode,
      AutoUpdateRepository autoUpdateRepository) {
    return new AutoUpdateManager(downloadFactory, permissionManager, installManager,
        downloadAnalytics, localVersionCode, autoUpdateRepository, Build.VERSION.SDK_INT,
        ((AptoideApplication) activity.getApplication()).getDefaultSharedPreferences());
  }

  @ActivityScope @Provides @Named("package-name") String providePackageName() {
    return activity.getPackageName();
  }

  @ActivityScope @Provides @Named("client-sdk-version") int provideClientSdkVersion() {
    return Build.VERSION.SDK_INT;
  }

  @ActivityScope @Provides @Named("local-version-code") int provideLocalVersionCode(
      @Named("package-name") String packageName) {
    try {
      return activity.getPackageManager()
          .getPackageInfo(packageName, 0).versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      return -1;
    }
  }

  @ActivityScope @Provides ClaimPromotionsNavigator providesClaimPromotionsNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      AppNavigator appNavigator) {
    return new ClaimPromotionsNavigator(fragmentNavigator, (ActivityResultNavigator) activity,
        appNavigator);
  }

  @ActivityScope @Provides PromotionsNavigator providesPromotionsNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      AppNavigator appNavigator) {
    return new PromotionsNavigator(fragmentNavigator, appNavigator);
  }

  @ActivityScope @Provides WalletInstallPresenter providesWalletInstallPresenter(
      WalletInstallConfiguration configuration, WalletInstallNavigator walletInstallNavigator,
      WalletInstallManager walletInstallManager, WalletInstallAnalytics walletInstallAnalytics,
      MoPubAdsManager moPubAdsManager) {
    return new WalletInstallPresenter((WalletInstallView) view, walletInstallManager,
        walletInstallNavigator, new PermissionManager(), ((PermissionService) activity),
        AndroidSchedulers.mainThread(), Schedulers.io(), configuration, walletInstallAnalytics,
        moPubAdsManager);
  }

  @ActivityScope @Provides WalletInstallNavigator providesWalletInstallNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator) {
    return new WalletInstallNavigator(fragmentNavigator);
  }

  @ActivityScope @Provides WalletInstallManager providesWalletInstallManager(
      InstallManager installManager, DownloadFactory downloadFactory,
      DownloadStateParser downloadStateParser, MoPubAdsManager moPubAdsManager,
      WalletInstallAnalytics walletInstallAnalytics,
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository,
      WalletAppProvider walletAppProvider, AppInstallerStatusReceiver appInstallerStatusReceiver,
      DynamicSplitsManager dynamicSplitsManager) {
    return new WalletInstallManager(activity.getPackageManager(), installManager, downloadFactory,
        downloadStateParser, moPubAdsManager, walletInstallAnalytics,
        aptoideInstalledAppsRepository, walletAppProvider, appInstallerStatusReceiver,
        dynamicSplitsManager);
  }

  @ActivityScope @Provides WalletInstallAnalytics providesWalletInstallAnalytics(
      DownloadAnalytics downloadAnalytics, NotificationAnalytics notificationAnalytics,
      InstallAnalytics installAnalytics, DownloadStateParser downloadStateParser,
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker,
      SplitAnalyticsMapper splitAnalyticsMapper) {
    return new WalletInstallAnalytics(downloadAnalytics, notificationAnalytics, installAnalytics,
        downloadStateParser, analyticsManager, navigationTracker, splitAnalyticsMapper);
  }

  @ActivityScope @Provides WalletInstallConfiguration providesWalletInstallConfiguration() {
    return new WalletInstallConfiguration(
        intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.PACKAGE_NAME_KEY),
        intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.WALLET_PACKAGE_NAME_KEY));
  }

  @ActivityScope @Provides ListAppsMoreRepository providesListAppsMoreRepository(
      StoreCredentialsProvider storeCredentialsProvider,
      @Named("default") OkHttpClient okHttpClient, @Named("mature-pool-v7")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> baseBodyBodyInterceptor,
      TokenInvalidator tokenInvalidator, @Named("default") SharedPreferences sharedPreferences,
      Converter.Factory converterFactory, AppBundlesVisibilityManager appBundlesVisibilityManager) {
    return new ListAppsMoreRepository(storeCredentialsProvider, baseBodyBodyInterceptor,
        okHttpClient, converterFactory, tokenInvalidator, sharedPreferences,
        activity.getResources(), activity.getWindowManager(), appBundlesVisibilityManager);
  }

  @ActivityScope @Provides ThemeManager providesThemeManager() {
    return new ThemeManager(activity,
        ((AptoideApplication) activity.getApplicationContext()).getDefaultSharedPreferences());
  }

  @ActivityScope @Provides SocialMediaNavigator providesSocialMediaNavigator(
      ExternalNavigator externalNavigator) {
    return new SocialMediaNavigator(externalNavigator);
  }
}

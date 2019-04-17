package cm.aptoide.pt.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.AppShortcutsAnalytics;
import cm.aptoide.pt.DeepLinkAnalytics;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.account.view.ImagePickerNavigator;
import cm.aptoide.pt.account.view.PhotoFileGenerator;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.account.view.store.ManageStoreNavigator;
import cm.aptoide.pt.account.view.user.ManageUserNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.AppViewNavigator;
import cm.aptoide.pt.app.view.donations.DonationsAnalytics;
import cm.aptoide.pt.autoupdate.AutoUpdateManager;
import cm.aptoide.pt.autoupdate.AutoUpdateRepository;
import cm.aptoide.pt.autoupdate.AutoUpdateService;
import cm.aptoide.pt.bottomNavigation.BottomNavigationAnalytics;
import cm.aptoide.pt.bottomNavigation.BottomNavigationMapper;
import cm.aptoide.pt.bottomNavigation.BottomNavigationNavigator;
import cm.aptoide.pt.autoupdate.Service;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.editorial.EditorialNavigator;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.install.InstallCompletedNotifier;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.FragmentResultNavigator;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.notification.ContentPuller;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.permission.AccountPermissionProvider;
import cm.aptoide.pt.permission.PermissionProvider;
import cm.aptoide.pt.presenter.MainPresenter;
import cm.aptoide.pt.presenter.MainView;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.promotions.CaptchaService;
import cm.aptoide.pt.promotions.ClaimPromotionsManager;
import cm.aptoide.pt.promotions.ClaimPromotionsNavigator;
import cm.aptoide.pt.promotions.PromotionsManager;
import cm.aptoide.pt.promotions.PromotionsNavigator;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.splashscreen.SplashScreenManager;
import cm.aptoide.pt.splashscreen.SplashScreenNavigator;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.util.ApkFy;
import cm.aptoide.pt.util.MarketResourceFormatter;
import cm.aptoide.pt.view.app.ListStoreAppsNavigator;
import cm.aptoide.pt.view.dialog.DialogUtils;
import cm.aptoide.pt.view.settings.MyAccountNavigator;
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
  private boolean firstCreated;

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

  @ActivityScope @Provides ApkFy provideApkFy(
      @Named("secureShared") SharedPreferences securePreferences) {
    return new ApkFy(activity, intent, securePreferences);
  }

  @ActivityScope @Provides AutoUpdateService providesRetrofitAptoideBiService(Service service,
      @Named("package-name") String packageName,
      @Named("auto-update-store-name") String storeName) {
    return new AutoUpdateService(service, packageName, storeName);
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

  @ActivityScope @Provides DeepLinkManager provideDeepLinkManager(
      NotificationAnalytics notificationAnalytics, StoreUtilsProxy storeUtilsProxy,
      StoreRepository storeRepository,
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      BottomNavigationNavigator bottomNavigationNavigator, SearchNavigator searchNavigator,
      @Named("default") SharedPreferences sharedPreferences, StoreAccessor storeAccessor,
      NavigationTracker navigationTracker, SearchAnalytics searchAnalytics,
      DeepLinkAnalytics deepLinkAnalytics, AppShortcutsAnalytics appShortcutsAnalytics,
      AptoideAccountManager accountManager, StoreAnalytics storeAnalytics,
      AdsRepository adsRepository, AppNavigator appNavigator,
      @Named("aptoide-theme") String theme) {
    return new DeepLinkManager(storeUtilsProxy, storeRepository, fragmentNavigator,
        bottomNavigationNavigator, searchNavigator, (DeepLinkManager.DeepLinkMessages) activity,
        sharedPreferences, storeAccessor, theme, notificationAnalytics, navigationTracker,
        searchAnalytics, appShortcutsAnalytics, accountManager, deepLinkAnalytics, storeAnalytics,
        adsRepository, appNavigator);
  }

  @ActivityScope @Provides Presenter provideMainPresenter(
      RootInstallationRetryHandler rootInstallationRetryHandler, ApkFy apkFy,
      InstallManager installManager, @Named("default") SharedPreferences sharedPreferences,
      @Named("secureShared") SharedPreferences secureSharedPreferences,
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      DeepLinkManager deepLinkManager, BottomNavigationNavigator bottomNavigationNavigator,
      UpdatesManager updatesManager, AutoUpdateManager autoUpdateManager,
      SplashScreenManager splashScreenManager, SplashScreenNavigator splashScreenNavigator) {
    return new MainPresenter((MainView) view, installManager, rootInstallationRetryHandler,
        CrashReport.getInstance(), apkFy, new ContentPuller(activity), notificationSyncScheduler,
        new InstallCompletedNotifier(PublishRelay.create(), installManager,
            CrashReport.getInstance()), sharedPreferences, secureSharedPreferences,
        fragmentNavigator, deepLinkManager, firstCreated, (AptoideBottomNavigator) activity,
        AndroidSchedulers.mainThread(), Schedulers.io(), bottomNavigationNavigator, updatesManager,
        autoUpdateManager, splashScreenManager, splashScreenNavigator);
  }

  @ActivityScope @Provides SplashScreenManager provideSplashScreenManager() {
    return new SplashScreenManager();
  }

  @ActivityScope @Provides SplashScreenNavigator provideSplashScreenNavigator(
      BottomNavigationNavigator bottomNavigationNavigator,
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator) {
    return new SplashScreenNavigator(bottomNavigationNavigator, fragmentNavigator);
  }

  @ActivityScope @Provides AccountNavigator provideAccountNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      AptoideAccountManager accountManager, CallbackManager callbackManager,
      GoogleApiClient googleApiClient, AccountAnalytics accountAnalytics,
      BottomNavigationNavigator bottomNavigationNavigator, @Named("aptoide-theme") String theme) {
    return new AccountNavigator(bottomNavigationNavigator, fragmentNavigator, accountManager,
        ((ActivityNavigator) activity), LoginManager.getInstance(), callbackManager,
        googleApiClient, PublishRelay.create(), "http://m.aptoide.com/account/password-recovery",
        accountAnalytics, theme);
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
      AccountNavigator accountNavigator, AppNavigator appNavigator) {
    return new MyAccountNavigator(fragmentNavigator, accountNavigator, appNavigator);
  }

  @ActivityScope @Provides BottomNavigationMapper provideBottomNavigationMapper() {
    return new BottomNavigationMapper();
  }

  @ActivityScope @Provides BottomNavigationNavigator provideBottomNavigationNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      BottomNavigationAnalytics bottomNavigationAnalytics, SearchAnalytics searchAnalytics,
      @Named("aptoide-theme") String theme) {
    return new BottomNavigationNavigator(fragmentNavigator, bottomNavigationAnalytics,
        searchAnalytics, theme);
  }

  @ActivityScope @Provides BottomNavigationAnalytics providesBottomNavigationAnalytics(
      AnalyticsManager manager, NavigationTracker tracker) {
    return new BottomNavigationAnalytics(manager, tracker);
  }

  @ActivityScope @Provides AppViewNavigator providesAppViewNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      AppNavigator appNavigator) {
    return new AppViewNavigator(fragmentNavigator, (ActivityNavigator) activity, appNavigator);
  }

  @ActivityScope @Provides DialogUtils providesDialogUtils(AptoideAccountManager accountManager,
      AccountNavigator accountNavigator,
      @Named("pool-v7") BodyInterceptor<BaseBody> bodyInterceptor,
      @Named("default") OkHttpClient httpClient, Converter.Factory converterFactory,
      InstalledRepository installedRepository, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences, Resources resources,
      @Named("marketName") String marketName, MarketResourceFormatter marketResourceFormatter) {
    return new DialogUtils(accountManager, accountNavigator, bodyInterceptor, httpClient,
        converterFactory, installedRepository, tokenInvalidator, sharedPreferences, resources,
        marketName, marketResourceFormatter);
  }

  @ActivityScope @Provides AppNavigator providesAppNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator) {
    return new AppNavigator(fragmentNavigator);
  }

  @ActivityScope @Provides AppCoinsInfoNavigator providesAppCoinsInfoNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator,
      @Named("aptoide-theme") String theme) {
    return new AppCoinsInfoNavigator(((ActivityNavigator) activity), fragmentNavigator, theme);
  }

  @ActivityScope @Provides EditorialNavigator providesEditorialNavigator(
      AppNavigator appNavigator) {
    return new EditorialNavigator((ActivityNavigator) activity, appNavigator);
  }

  @ActivityScope @Provides @Named("screenHeight") float providesScreenHeight(Resources resources) {
    return resources.getDisplayMetrics().heightPixels;
  }

  @ActivityScope @Provides @Named("screenWidth") float providesScreenWidth(Resources resources) {
    return resources.getDisplayMetrics().widthPixels;
  }

  @ActivityScope @Provides DonationsAnalytics providesDonationsAnalytics(
      AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    return new DonationsAnalytics(analyticsManager, navigationTracker);
  }

  @ActivityScope @Provides AutoUpdateManager provideAutoUpdateManager(
      DownloadFactory downloadFactory, PermissionManager permissionManager,
      InstallManager installManager, DownloadAnalytics downloadAnalytics,
      @Named("local-version-code") int localVersionCode,
      AutoUpdateRepository autoUpdateRepository) {
    return new AutoUpdateManager(downloadFactory, permissionManager, installManager,
        downloadAnalytics, localVersionCode, autoUpdateRepository, Build.VERSION.SDK_INT);
  }

  @ActivityScope @Provides @Named("package-name") String providePackageName() {
    return activity.getPackageName();
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

  @ActivityScope @Provides ClaimPromotionsManager providesClaimPromotionsManager(
      CaptchaService captchaService, PromotionsManager promotionsManager) {
    return new ClaimPromotionsManager(promotionsManager, captchaService);
  }

  @ActivityScope @Provides ClaimPromotionsNavigator providesClaimPromotionsNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator) {
    return new ClaimPromotionsNavigator(fragmentNavigator, (ActivityResultNavigator) activity);
  }

  @ActivityScope @Provides PromotionsNavigator providesPromotionsNavigator(
      @Named("main-fragment-navigator") FragmentNavigator fragmentNavigator) {
    return new PromotionsNavigator(fragmentNavigator);
  }
}

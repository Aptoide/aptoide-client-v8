package cm.aptoide.pt.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.AppShortcutsAnalytics;
import cm.aptoide.pt.AptoideApplication;
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
import cm.aptoide.pt.ads.IronSourceAdRepository;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.AppViewNavigator;
import cm.aptoide.pt.app.view.EditorialNavigator;
import cm.aptoide.pt.app.view.donations.DonationsAnalytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.home.BottomNavigationAnalytics;
import cm.aptoide.pt.home.BottomNavigationMapper;
import cm.aptoide.pt.home.BottomNavigationNavigator;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.install.AutoUpdate;
import cm.aptoide.pt.install.InstallCompletedNotifier;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.navigator.ActivityNavigator;
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
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.util.ApkFy;
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

import static android.content.Context.WINDOW_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

@Module public class ActivityModule {

  private final AppCompatActivity activity;
  private final Intent intent;
  private final NotificationSyncScheduler notificationSyncScheduler;
  private final String marketName;
  private final String autoUpdateUrl;
  private final View view;
  private final String defaultTheme;
  private final String defaultStoreName;
  private final String fileProviderAuthority;
  private boolean firstCreated;

  public ActivityModule(AppCompatActivity activity, Intent intent,
      NotificationSyncScheduler notificationSyncScheduler, String marketName, String autoUpdateUrl,
      View view, String defaultTheme, String defaultStoreName, boolean firstCreated,
      String fileProviderAuthority) {
    this.activity = activity;
    this.intent = intent;
    this.notificationSyncScheduler = notificationSyncScheduler;
    this.marketName = marketName;
    this.autoUpdateUrl = autoUpdateUrl;
    this.view = view;
    this.firstCreated = firstCreated;
    this.defaultTheme = defaultTheme;
    this.defaultStoreName = defaultStoreName;
    this.fileProviderAuthority = fileProviderAuthority;
  }

  @ActivityScope @Provides ApkFy provideApkFy(
      @Named("secureShared") SharedPreferences securePreferences) {
    return new ApkFy(activity, intent, securePreferences);
  }

  @ActivityScope @Provides AutoUpdate provideAutoUpdate(DownloadFactory downloadFactory,
      PermissionManager permissionManager, Resources resources,
      DownloadAnalytics downloadAnalytics) {
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    return new AutoUpdate((ActivityView) activity, downloadFactory, permissionManager,
        application.getInstallManager(), resources, autoUpdateUrl, R.mipmap.ic_launcher, false,
        marketName, downloadAnalytics);
  }

  @ActivityScope @Provides FragmentNavigator provideFragmentNavigator(
      Map<Integer, Result> fragmentResultMap,
      BehaviorRelay<Map<Integer, Result>> fragmentResultRelay, FragmentManager fragmentManager) {
    return new FragmentResultNavigator(fragmentManager, R.id.fragment_placeholder,
        android.R.anim.fade_in, android.R.anim.fade_out, fragmentResultMap, fragmentResultRelay);
  }

  @ActivityScope @Provides FragmentManager provideFragmentManager() {
    return activity.getSupportFragmentManager();
  }

  @ActivityScope @Provides SearchNavigator providesSearchNavigator(
      FragmentNavigator fragmentNavigator, AppNavigator appNavigator) {
    return new SearchNavigator(fragmentNavigator, defaultStoreName, appNavigator);
  }

  @ActivityScope @Provides DeepLinkManager provideDeepLinkManager(
      NotificationAnalytics notificationAnalytics, StoreUtilsProxy storeUtilsProxy,
      StoreRepository storeRepository, FragmentNavigator fragmentNavigator,
      BottomNavigationNavigator bottomNavigationNavigator, SearchNavigator searchNavigator,
      @Named("default") SharedPreferences sharedPreferences, StoreAccessor storeAccessor,
      NavigationTracker navigationTracker, SearchAnalytics searchAnalytics,
      DeepLinkAnalytics deepLinkAnalytics, AppShortcutsAnalytics appShortcutsAnalytics,
      AptoideAccountManager accountManager, StoreAnalytics storeAnalytics,
      AdsRepository adsRepository, AppNavigator appNavigator) {
    return new DeepLinkManager(storeUtilsProxy, storeRepository, fragmentNavigator,
        bottomNavigationNavigator, searchNavigator, (DeepLinkManager.DeepLinkMessages) activity,
        sharedPreferences, storeAccessor, defaultTheme, notificationAnalytics, navigationTracker,
        searchAnalytics, appShortcutsAnalytics, accountManager, deepLinkAnalytics, storeAnalytics,
        adsRepository, appNavigator);
  }

  @ActivityScope @Provides Presenter provideMainPresenter(
      RootInstallationRetryHandler rootInstallationRetryHandler, ApkFy apkFy, AutoUpdate autoUpdate,
      @Named("default") SharedPreferences sharedPreferences,
      @Named("secureShared") SharedPreferences secureSharedPreferences,
      FragmentNavigator fragmentNavigator, DeepLinkManager deepLinkManager,
      BottomNavigationNavigator bottomNavigationNavigator, UpdatesManager updatesManager) {
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    InstallManager installManager = application.getInstallManager();
    return new MainPresenter((MainView) view, installManager, rootInstallationRetryHandler,
        CrashReport.getInstance(), apkFy, autoUpdate, new ContentPuller(activity),
        notificationSyncScheduler,
        new InstallCompletedNotifier(PublishRelay.create(), installManager,
            CrashReport.getInstance()), sharedPreferences, secureSharedPreferences,
        fragmentNavigator, deepLinkManager, firstCreated, (AptoideBottomNavigator) activity,
        AndroidSchedulers.mainThread(), bottomNavigationNavigator, updatesManager);
  }

  @ActivityScope @Provides AccountNavigator provideAccountNavigator(
      FragmentNavigator fragmentNavigator, AptoideAccountManager accountManager,
      CallbackManager callbackManager, GoogleApiClient googleApiClient,
      AccountAnalytics accountAnalytics, BottomNavigationNavigator bottomNavigationNavigator) {
    return new AccountNavigator(bottomNavigationNavigator, fragmentNavigator, accountManager,
        ((ActivityNavigator) activity), LoginManager.getInstance(), callbackManager,
        googleApiClient, PublishRelay.create(), "http://m.aptoide.com/account/password-recovery",
        accountAnalytics);
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
      FragmentNavigator fragmentNavigator, BottomNavigationNavigator bottomNavigationNavigator) {
    return new ManageStoreNavigator(fragmentNavigator, bottomNavigationNavigator);
  }

  @ActivityScope @Provides ManageUserNavigator provideManageUserNavigator(
      FragmentNavigator fragmentNavigator, BottomNavigationNavigator bottomNavigationNavigator) {
    return new ManageUserNavigator(fragmentNavigator, bottomNavigationNavigator);
  }

  @ActivityScope @Provides LinksHandlerFactory provideLinksHandlerFactory() {
    return new LinksHandlerFactory(activity);
  }

  @ActivityScope @Provides ListStoreAppsNavigator provideListStoreAppsNavigator(
      FragmentNavigator fragmentNavigator, AppNavigator appNavigator) {
    return new ListStoreAppsNavigator(fragmentNavigator, appNavigator);
  }

  @ActivityScope @Provides MyAccountNavigator provideMyAccountNavigator(
      FragmentNavigator fragmentNavigator, AccountNavigator accountNavigator,
      AppNavigator appNavigator) {
    return new MyAccountNavigator(fragmentNavigator, accountNavigator, appNavigator);
  }

  @ActivityScope @Provides BottomNavigationMapper provideBottomNavigationMapper() {
    return new BottomNavigationMapper();
  }

  @ActivityScope @Provides BottomNavigationNavigator provideBottomNavigationNavigator(
      FragmentNavigator fragmentNavigator, @Named("defaultStoreName") String defaultStoreName,
      BottomNavigationAnalytics bottomNavigationAnalytics, SearchAnalytics searchAnalytics) {
    return new BottomNavigationNavigator(fragmentNavigator, defaultStoreName,
        bottomNavigationAnalytics, searchAnalytics);
  }

  @ActivityScope @Provides BottomNavigationAnalytics providesBottomNavigationAnalytics(
      AnalyticsManager manager, NavigationTracker tracker) {
    return new BottomNavigationAnalytics(manager, tracker);
  }

  @ActivityScope @Provides AppViewNavigator providesAppViewNavigator(
      FragmentNavigator fragmentNavigator, AppNavigator appNavigator) {
    final AptoideApplication application = (AptoideApplication) getApplicationContext();

    return new AppViewNavigator(fragmentNavigator, (ActivityNavigator) activity,
        application.hasMultiStoreSearch(), application.getDefaultStoreName(), appNavigator);
  }

  @ActivityScope @Provides DialogUtils providesDialogUtils(AptoideAccountManager accountManager,
      AccountNavigator accountNavigator,
      @Named("pool-v7") BodyInterceptor<BaseBody> bodyInterceptor,
      @Named("default") OkHttpClient httpClient, Converter.Factory converterFactory,
      InstalledRepository installedRepository, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences, Resources resources) {
    return new DialogUtils(accountManager, accountNavigator, bodyInterceptor, httpClient,
        converterFactory, installedRepository, tokenInvalidator, sharedPreferences, resources);
  }

  @ActivityScope @Provides AppNavigator providesAppNavigator(FragmentNavigator fragmentNavigator) {
    return new AppNavigator(fragmentNavigator);
  }

  @ActivityScope @Provides AppCoinsInfoNavigator providesAppCoinsInfoNavigator(
      FragmentNavigator fragmentNavigator) {
    return new AppCoinsInfoNavigator(((ActivityNavigator) activity), fragmentNavigator);
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

  @ActivityScope @Provides IronSourceAdRepository providesIronSourceAdRepository() {
    return new IronSourceAdRepository(activity);
  }

  @ActivityScope @Provides ClaimPromotionsManager providesClaimPromotionsManager(
      CaptchaService captchaService, PromotionsManager promotionsManager) {
    return new ClaimPromotionsManager(promotionsManager, captchaService);
  }

  @ActivityScope @Provides ClaimPromotionsNavigator providesClaimPromotionsNavigator(
      FragmentNavigator fragmentNavigator) {
    return new ClaimPromotionsNavigator(fragmentNavigator);
  }

  @ActivityScope @Provides PromotionsNavigator providesPromotionsNavigator(
      FragmentNavigator fragmentNavigator) {
    return new PromotionsNavigator(fragmentNavigator);
  }
}

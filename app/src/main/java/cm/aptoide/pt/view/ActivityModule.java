package cm.aptoide.pt.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AppShortcutsAnalytics;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.DeepLinkAnalytics;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.account.view.ImagePickerNavigator;
import cm.aptoide.pt.account.view.MyAccountNavigator;
import cm.aptoide.pt.account.view.PhotoFileGenerator;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.account.view.store.ManageStoreNavigator;
import cm.aptoide.pt.account.view.user.ManageUserNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.install.AutoUpdate;
import cm.aptoide.pt.install.InstallCompletedNotifier;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.FragmentResultNavigator;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.notification.ContentPuller;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.notification.view.NotificationNavigator;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.permission.AccountPermissionProvider;
import cm.aptoide.pt.permission.PermissionProvider;
import cm.aptoide.pt.presenter.MainPresenter;
import cm.aptoide.pt.presenter.MainView;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.util.ApkFy;
import cm.aptoide.pt.view.app.ListStoreAppsNavigator;
import cm.aptoide.pt.view.settings.NewAccountNavigator;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import dagger.Module;
import dagger.Provides;
import java.util.Map;
import javax.inject.Named;

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
      PermissionManager permissionManager, Resources resources) {
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    return new AutoUpdate((ActivityView) activity, downloadFactory, permissionManager,
        application.getInstallManager(), resources, autoUpdateUrl, R.mipmap.ic_launcher, false,
        marketName);
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
      FragmentNavigator fragmentNavigator) {
    return new SearchNavigator(fragmentNavigator, defaultStoreName);
  }

  @ActivityScope @Provides DeepLinkManager provideDeepLinkManager(
      NotificationAnalytics notificationAnalytics, StoreUtilsProxy storeUtilsProxy,
      StoreRepository storeRepository, FragmentNavigator fragmentNavigator,
      @Named("default") SharedPreferences sharedPreferences, StoreAccessor storeAccessor,
      NavigationTracker navigationTracker, SearchNavigator searchNavigator,
      SearchAnalytics searchAnalytics, DeepLinkAnalytics deepLinkAnalytics,
      AppShortcutsAnalytics appShortcutsAnalytics, AptoideAccountManager accountManager,
      TimelineAnalytics timelineAnalytics, StoreAnalytics storeAnalytics,
      AdsRepository adsRepository) {
    return new DeepLinkManager(storeUtilsProxy, storeRepository, fragmentNavigator,
        (TabNavigator) activity, (DeepLinkManager.DeepLinkMessages) activity, sharedPreferences,
        storeAccessor, defaultTheme, notificationAnalytics, navigationTracker, searchNavigator,
        searchAnalytics, appShortcutsAnalytics, accountManager, deepLinkAnalytics,
        timelineAnalytics, storeAnalytics, adsRepository);
  }

  @ActivityScope @Provides Presenter provideMainPresenter(
      RootInstallationRetryHandler rootInstallationRetryHandler, ApkFy apkFy, AutoUpdate autoUpdate,
      @Named("default") SharedPreferences sharedPreferences,
      @Named("secureShared") SharedPreferences secureSharedPreferences,
      FragmentNavigator fragmentNavigator, DeepLinkManager deepLinkManager) {
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    InstallManager installManager = application.getInstallManager();
    return new MainPresenter((MainView) view, installManager, rootInstallationRetryHandler,
        CrashReport.getInstance(), apkFy, autoUpdate, new ContentPuller(activity),
        notificationSyncScheduler,
        new InstallCompletedNotifier(PublishRelay.create(), installManager,
            CrashReport.getInstance()), sharedPreferences, secureSharedPreferences,
        fragmentNavigator, deepLinkManager, defaultStoreName, defaultTheme, firstCreated,
        (AptoideBottomNavigator) activity);
  }

  @ActivityScope @Provides AccountNavigator provideAccountNavigator(
      FragmentNavigator fragmentNavigator, AptoideAccountManager accountManager,
      CallbackManager callbackManager, GoogleApiClient googleApiClient,
      AccountAnalytics accountAnalytics) {
    return new AccountNavigator(fragmentNavigator, accountManager, ((ActivityNavigator) activity),
        LoginManager.getInstance(), callbackManager, googleApiClient, PublishRelay.create(),
        defaultStoreName, defaultTheme, "http://m.aptoide.com/account/password-recovery",
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
      FragmentNavigator fragmentNavigator) {
    return new ManageStoreNavigator(fragmentNavigator, defaultStoreName, defaultTheme);
  }

  @ActivityScope @Provides ManageUserNavigator provideManageUserNavigator(
      FragmentNavigator fragmentNavigator) {
    return new ManageUserNavigator(fragmentNavigator, defaultStoreName, defaultTheme);
  }

  @ActivityScope @Provides MyAccountNavigator provideMyAccountNavigator(
      FragmentNavigator fragmentNavigator, AccountNavigator accountNavigator,
      NotificationNavigator notificationNavigator) {
    return new MyAccountNavigator(fragmentNavigator, accountNavigator, notificationNavigator);
  }

  @ActivityScope @Provides NotificationNavigator provideNotificationNavigator(
      LinksHandlerFactory linksHandlerFactory, FragmentNavigator fragmentNavigator) {
    return new NotificationNavigator((TabNavigator) activity, linksHandlerFactory,
        fragmentNavigator);
  }

  @ActivityScope @Provides LinksHandlerFactory provideLinksHandlerFactory() {
    return new LinksHandlerFactory(activity);
  }

  @ActivityScope @Provides ListStoreAppsNavigator provideListStoreAppsNavigator(
      FragmentNavigator fragmentNavigator) {
    return new ListStoreAppsNavigator(fragmentNavigator);
  }

  @ActivityScope @Provides NewAccountNavigator provideNewAccountNavigator(
      FragmentNavigator fragmentNavigator, MyAccountNavigator myAccountNavigator,
      AccountNavigator accountNavigator) {
    return new NewAccountNavigator(fragmentNavigator, myAccountNavigator, accountNavigator);
  }
}

package cm.aptoide.pt.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.PageViewsAnalytics;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.account.view.ImagePickerNavigator;
import cm.aptoide.pt.account.view.PhotoFileGenerator;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.account.view.store.ManageStoreNavigator;
import cm.aptoide.pt.account.view.user.ManageUserNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.AutoUpdate;
import cm.aptoide.pt.install.InstallCompletedNotifier;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.InstallerFactory;
import cm.aptoide.pt.install.installer.RootInstallationRetryHandler;
import cm.aptoide.pt.link.AptoideInstallParser;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.FragmentResultNavigator;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.navigator.TabNavigator;
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
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.util.ApkFy;
import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import dagger.Module;
import dagger.Provides;
import java.util.Map;
import javax.inject.Named;
import okhttp3.OkHttpClient;

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
  private boolean firstCreated;
  private final String fileProviderAuthority;

  public ActivityModule(AppCompatActivity activity, Intent intent, NotificationSyncScheduler notificationSyncScheduler, String marketName,
      String autoUpdateUrl, View view, String defaultTheme, String defaultStoreName,
      boolean firstCreated, String fileProviderAuthority) {
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
        application.getInstallManager(InstallerFactory.ROLLBACK), resources, autoUpdateUrl, R.mipmap.ic_launcher, false, marketName);
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

  @ActivityScope @Provides DeepLinkManager provideDeepLinkManager(StoreUtilsProxy storeUtilsProxy,
      StoreRepository storeRepository, FragmentNavigator fragmentNavigator,
      @Named("default") SharedPreferences sharedPreferences, StoreAccessor storeAccessor,
      @Named("default") OkHttpClient httpClient, @Named("pool-v7")
      BodyInterceptor<BaseBody> bodyInterceptorV7,
      NavigationTracker navigationTracker, PageViewsAnalytics pageViewsAnalytics, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences defaultSharedPreferences) {
    NotificationAnalytics notificationAnalytics = new NotificationAnalytics(Analytics.getInstance(),
        AppEventsLogger.newLogger(activity.getApplicationContext()),bodyInterceptorV7,httpClient, WebService.getDefaultConverter(), tokenInvalidator,
        cm.aptoide.pt.dataprovider.BuildConfig.APPLICATION_ID,defaultSharedPreferences,new AptoideInstallParser());
    return new DeepLinkManager(storeUtilsProxy, storeRepository, fragmentNavigator,
        (TabNavigator) activity, (DeepLinkManager.DeepLinkMessages) activity, sharedPreferences,
        storeAccessor, defaultTheme, defaultStoreName, navigationTracker, pageViewsAnalytics, notificationAnalytics);
  }

  @ActivityScope @Provides Presenter provideMainPresenter(RootInstallationRetryHandler rootInstallationRetryHandler,
      ApkFy apkFy, AutoUpdate autoUpdate,
      @Named("default") SharedPreferences sharedPreferences,
      @Named("secureShared") SharedPreferences secureSharedPreferences,
      FragmentNavigator fragmentNavigator, DeepLinkManager deepLinkManager) {
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    InstallManager installManager = application.getInstallManager(InstallerFactory.ROLLBACK);
    return new MainPresenter((MainView) view, installManager, rootInstallationRetryHandler,
        CrashReport.getInstance(), apkFy, autoUpdate, new ContentPuller(activity),
        notificationSyncScheduler,
        new InstallCompletedNotifier(PublishRelay.create(), installManager,
            CrashReport.getInstance()), sharedPreferences, secureSharedPreferences,
        fragmentNavigator, deepLinkManager, defaultStoreName, defaultTheme, firstCreated);
  }

  @ActivityScope @Provides AccountNavigator provideAccountNavigator(FragmentNavigator fragmentNavigator, AptoideAccountManager accountManager,
      CallbackManager callbackManager, GoogleApiClient googleApiClient){
    return new AccountNavigator(fragmentNavigator, accountManager,
        ((ActivityNavigator) activity), LoginManager.getInstance(), callbackManager, googleApiClient,
        PublishRelay.create(),defaultStoreName, defaultTheme, "http://m.aptoide.com/account/password-recovery");
  }

  @ActivityScope @Provides ScreenOrientationManager provideScreenOrientationManager(){
    return new ScreenOrientationManager(activity, (WindowManager) activity.getSystemService(WINDOW_SERVICE));
  }

  @ActivityScope @Provides AccountPermissionProvider provideAccountPermissionProvider(){
    return new AccountPermissionProvider(((PermissionProvider) activity));
  }

  @ActivityScope @Provides PhotoFileGenerator providePhotoFileGenerator(){
    return new PhotoFileGenerator(activity,
        activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileProviderAuthority);
  }

  @ActivityScope @Provides UriToPathResolver provideUriToPathResolver(){
    return new UriToPathResolver(activity.getContentResolver());
  }

  @ActivityScope @Provides ImagePickerNavigator provideImagePickerNavigator(){
    return new ImagePickerNavigator((ActivityNavigator) activity);
  }

  @ActivityScope @Provides ManageStoreNavigator provideManageStoreNavigator(FragmentNavigator fragmentNavigator){
    return new ManageStoreNavigator(fragmentNavigator, defaultStoreName, defaultTheme);
  }

  @ActivityScope @Provides ManageUserNavigator provideManageUserNavigator(FragmentNavigator fragmentNavigator){
    return new ManageUserNavigator(fragmentNavigator, defaultStoreName, defaultTheme);
  }

}

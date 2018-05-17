package cm.aptoide.pt.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.ErrorsMapper;
import cm.aptoide.pt.account.view.AccountErrorMapper;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.account.view.ImagePickerNavigator;
import cm.aptoide.pt.account.view.ImagePickerPresenter;
import cm.aptoide.pt.account.view.ImagePickerView;
import cm.aptoide.pt.account.view.ImageValidator;
import cm.aptoide.pt.account.view.PhotoFileGenerator;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.account.view.store.ManageStoreErrorMapper;
import cm.aptoide.pt.account.view.store.ManageStoreNavigator;
import cm.aptoide.pt.account.view.store.ManageStorePresenter;
import cm.aptoide.pt.account.view.store.ManageStoreView;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.account.view.user.CreateUserErrorMapper;
import cm.aptoide.pt.account.view.user.ManageUserNavigator;
import cm.aptoide.pt.account.view.user.ManageUserPresenter;
import cm.aptoide.pt.account.view.user.ManageUserView;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.app.AdsManager;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.AppViewManager;
import cm.aptoide.pt.app.DownloadStateParser;
import cm.aptoide.pt.app.FlagManager;
import cm.aptoide.pt.app.FlagService;
import cm.aptoide.pt.app.ReviewsManager;
import cm.aptoide.pt.app.ReviewsRepository;
import cm.aptoide.pt.app.ReviewsService;
import cm.aptoide.pt.app.view.AppViewNavigator;
import cm.aptoide.pt.app.view.AppViewPresenter;
import cm.aptoide.pt.app.view.AppViewView;
import cm.aptoide.pt.app.view.NewAppViewFragment;
import cm.aptoide.pt.appview.PreferencesManager;
import cm.aptoide.pt.appview.UserPreferencesPersister;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.home.AdMapper;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.home.BottomNavigationMapper;
import cm.aptoide.pt.home.BundlesRepository;
import cm.aptoide.pt.home.GetRewardAppCoinsAppsNavigator;
import cm.aptoide.pt.home.Home;
import cm.aptoide.pt.home.HomeAnalytics;
import cm.aptoide.pt.home.HomeNavigator;
import cm.aptoide.pt.home.HomePresenter;
import cm.aptoide.pt.home.HomeView;
import cm.aptoide.pt.home.apps.AppsNavigator;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.permission.AccountPermissionProvider;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsPresenter;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsView;
import cm.aptoide.pt.search.SearchManager;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.suggestions.SearchSuggestionManager;
import cm.aptoide.pt.search.suggestions.TrendingManager;
import cm.aptoide.pt.search.view.SearchResultPresenter;
import cm.aptoide.pt.search.view.SearchResultView;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.store.view.my.MyStoresNavigator;
import cm.aptoide.pt.store.view.my.MyStoresPresenter;
import cm.aptoide.pt.store.view.my.MyStoresView;
import cm.aptoide.pt.view.app.AppCenter;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import javax.inject.Named;
import okhttp3.OkHttpClient;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module public class FragmentModule {

  private final Fragment fragment;
  private final Bundle savedInstance;
  private final Bundle arguments;
  private final boolean isCreateStoreUserPrivacyEnabled;
  private final String packageName;

  public FragmentModule(Fragment fragment, Bundle savedInstance, Bundle arguments,
      boolean isCreateStoreUserPrivacyEnabled, String packageName) {
    this.fragment = fragment;
    this.savedInstance = savedInstance;
    this.arguments = arguments;
    this.isCreateStoreUserPrivacyEnabled = isCreateStoreUserPrivacyEnabled;
    this.packageName = packageName;
  }

  @FragmentScope @Provides LoginSignUpCredentialsPresenter provideLoginSignUpPresenter(
      AptoideAccountManager accountManager, AccountNavigator accountNavigator,
      AccountErrorMapper errorMapper, AccountAnalytics accountAnalytics) {
    return new LoginSignUpCredentialsPresenter((LoginSignUpCredentialsView) fragment,
        accountManager, CrashReport.getInstance(),
        arguments.getBoolean("dismiss_to_navigate_to_main_view"),
        arguments.getBoolean("clean_back_stack"), accountNavigator,
        Arrays.asList("email", "user_friends"), Arrays.asList("email"), errorMapper,
        accountAnalytics);
  }

  @FragmentScope @Provides ImagePickerPresenter provideImagePickerPresenter(
      AccountPermissionProvider accountPermissionProvider, PhotoFileGenerator photoFileGenerator,
      ImageValidator imageValidator, UriToPathResolver uriToPathResolver,
      ImagePickerNavigator imagePickerNavigator) {
    return new ImagePickerPresenter((ImagePickerView) fragment, CrashReport.getInstance(),
        accountPermissionProvider, photoFileGenerator, imageValidator,
        AndroidSchedulers.mainThread(), uriToPathResolver, imagePickerNavigator,
        fragment.getActivity()
            .getContentResolver(), ImageLoader.with(fragment.getContext()));
  }

  @FragmentScope @Provides ManageStorePresenter provideManageStorePresenter(
      UriToPathResolver uriToPathResolver, ManageStoreNavigator manageStoreNavigator,
      ManageStoreErrorMapper manageStoreErrorMapper, AptoideAccountManager accountManager,
      AccountAnalytics accountAnalytics) {
    return new ManageStorePresenter((ManageStoreView) fragment, CrashReport.getInstance(),
        uriToPathResolver, packageName, manageStoreNavigator,
        arguments.getBoolean("go_to_home", true), manageStoreErrorMapper, accountManager,
        arguments.getInt(FragmentNavigator.REQUEST_CODE_EXTRA), accountAnalytics);
  }

  @FragmentScope @Provides ManageUserPresenter provideManageUserPresenter(
      AptoideAccountManager accountManager, CreateUserErrorMapper errorMapper,
      ManageUserNavigator manageUserNavigator, UriToPathResolver uriToPathResolver,
      AccountAnalytics accountAnalytics) {
    return new ManageUserPresenter((ManageUserView) fragment, CrashReport.getInstance(),
        accountManager, errorMapper, manageUserNavigator, arguments.getBoolean("is_edit", false),
        uriToPathResolver, isCreateStoreUserPrivacyEnabled, savedInstance == null,
        accountAnalytics);
  }

  @FragmentScope @Provides ImageValidator provideImageValidator() {
    return new ImageValidator(ImageLoader.with(fragment.getContext()), Schedulers.computation());
  }

  @FragmentScope @Provides CreateUserErrorMapper provideCreateUserErrorMapper(
      AccountErrorMapper accountErrorMapper) {
    return new CreateUserErrorMapper(fragment.getContext(), accountErrorMapper,
        fragment.getResources());
  }

  @FragmentScope @Provides AccountErrorMapper provideAccountErrorMapper() {
    return new AccountErrorMapper(fragment.getContext(), new ErrorsMapper());
  }

  @FragmentScope @Provides ManageStoreErrorMapper provideManageStoreErrorMapper() {
    return new ManageStoreErrorMapper(fragment.getResources(), new ErrorsMapper());
  }

  @FragmentScope @Provides SearchResultPresenter provideSearchResultPresenter(
      SearchAnalytics searchAnalytics, SearchNavigator searchNavigator, SearchManager searchManager,
      TrendingManager trendingManager, SearchSuggestionManager searchSuggestionManager,
      BottomNavigationMapper bottomNavigationMapper) {
    return new SearchResultPresenter((SearchResultView) fragment, searchAnalytics, searchNavigator,
        CrashReport.getInstance(), AndroidSchedulers.mainThread(), searchManager,
        ((AptoideApplication) fragment.getContext()
            .getApplicationContext()).getDefaultThemeName(), trendingManager,
        searchSuggestionManager, (AptoideBottomNavigator) fragment.getActivity(),
        bottomNavigationMapper);
  }

  @FragmentScope @Provides HomePresenter providesHomePresenter(Home home,
      HomeNavigator homeNavigator, AdMapper adMapper, AptoideAccountManager aptoideAccountManager,
      HomeAnalytics homeAnalytics) {
    return new HomePresenter((HomeView) fragment, home, AndroidSchedulers.mainThread(),
        CrashReport.getInstance(), homeNavigator, adMapper, aptoideAccountManager, homeAnalytics);
  }

  @FragmentScope @Provides HomeNavigator providesHomeNavigator(FragmentNavigator fragmentNavigator,
      BottomNavigationMapper bottomNavigationMapper) {
    return new HomeNavigator(fragmentNavigator, (AptoideBottomNavigator) fragment.getActivity(),
        bottomNavigationMapper);
  }

  @FragmentScope @Provides Home providesHome(BundlesRepository bundlesRepository) {
    return new Home(bundlesRepository);
  }

  @FragmentScope @Provides MyStoresPresenter providesMyStorePresenter(
      AptoideAccountManager aptoideAccountManager, MyStoresNavigator navigator) {
    return new MyStoresPresenter((MyStoresView) fragment, AndroidSchedulers.mainThread(),
        aptoideAccountManager, navigator);
  }

  @FragmentScope @Provides MyStoresNavigator providesMyStoreNavigator(
      FragmentNavigator fragmentNavigator, BottomNavigationMapper bottomNavigationMapper) {
    return new MyStoresNavigator(fragmentNavigator, (AptoideBottomNavigator) fragment.getActivity(),
        bottomNavigationMapper);
  }

  @FragmentScope @Provides HomeAnalytics providesHomeAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    return new HomeAnalytics(navigationTracker, analyticsManager);
  }

  @FragmentScope @Provides AppsNavigator providesAppsNavigator(FragmentNavigator fragmentNavigator,
      BottomNavigationMapper bottomNavigationMapper) {
    return new AppsNavigator(fragmentNavigator, (AptoideBottomNavigator) fragment.getActivity(),
        bottomNavigationMapper);
  }

  @FragmentScope @Provides GetRewardAppCoinsAppsNavigator providesGetRewardAppCoinsAppsNavigator(
      FragmentNavigator fragmentNavigator) {
    return new GetRewardAppCoinsAppsNavigator(fragmentNavigator);
  }

  @FragmentScope @Provides ReviewsManager providesReviewsManager(
      ReviewsRepository reviewsRepository) {
    return new ReviewsManager(reviewsRepository);
  }

  @FragmentScope @Provides ReviewsRepository providesReviewsRepository(
      ReviewsService reviewsService) {
    return new ReviewsRepository(reviewsService);
  }

  @FragmentScope @Provides ReviewsService providesReviewsService(
      StoreCredentialsProvider storeCredentialsProvider,
      @Named("pool-v7") BodyInterceptor<BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences) {
    return new ReviewsService(storeCredentialsProvider, bodyInterceptorPoolV7, okHttpClient,
        WebService.getDefaultConverter(), tokenInvalidator, sharedPreferences);
  }

  @FragmentScope @Provides AdsManager providesAdsManager(AdsRepository adsRepository) {
    return new AdsManager(adsRepository);
  }

  @FragmentScope @Provides FlagManager providesFlagManager(FlagService flagService) {
    return new FlagManager(flagService);
  }

  @FragmentScope @Provides FlagService providesFlagService(@Named("defaultInterceptorV3")
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3,
      @Named("default") OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences) {
    return new FlagService(bodyInterceptorV3, okHttpClient, tokenInvalidator, sharedPreferences);
  }

  @FragmentScope @Provides UserPreferencesPersister providesUserPreferencesPersister(
      @Named("default") SharedPreferences sharedPreferences) {
    return new UserPreferencesPersister(sharedPreferences);
  }

  @FragmentScope @Provides PreferencesManager providesPreferencesManager(
      UserPreferencesPersister userPreferencesPersister) {
    return new PreferencesManager(userPreferencesPersister);
  }

  @FragmentScope @Provides DownloadStateParser providesDownloadStateParser() {
    return new DownloadStateParser();
  }

  @FragmentScope @Provides AppViewManager providesAppViewManager(UpdatesManager updatesManager,
      InstallManager installManager, DownloadFactory downloadFactory, AppCenter appCenter,
      ReviewsManager reviewsManager, AdsManager adsManager, StoreManager storeManager,
      FlagManager flagManager, StoreUtilsProxy storeUtilsProxy,
      AptoideAccountManager aptoideAccountManager, PreferencesManager preferencesManager,
      DownloadStateParser downloadStateParser, AppViewAnalytics appViewAnalytics,
      NotificationAnalytics notificationAnalytics) {
    return new AppViewManager(updatesManager, installManager, downloadFactory, appCenter,
        reviewsManager, adsManager, storeManager, flagManager, storeUtilsProxy,
        aptoideAccountManager, preferencesManager, downloadStateParser, appViewAnalytics,
        notificationAnalytics);
  }

  @FragmentScope @Provides AppViewPresenter providesAppViewPresenter(
      AccountNavigator accountNavigator, AppViewAnalytics analytics,
      AppViewNavigator appViewNavigator, AppViewManager appViewManager,
      AptoideAccountManager accountManager, CrashReport crashReport) {
    return new AppViewPresenter((AppViewView) fragment, accountNavigator, analytics,
        appViewNavigator, appViewManager, accountManager, AndroidSchedulers.mainThread(),
        crashReport, arguments.getLong(NewAppViewFragment.BundleKeys.APP_ID.name(), -1),
        arguments.getString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), null),
        new PermissionManager(), ((PermissionService) fragment.getContext()));
  }
}

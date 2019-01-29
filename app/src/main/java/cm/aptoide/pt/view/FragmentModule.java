package cm.aptoide.pt.view;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.abtesting.experiments.MoPubBannerAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubInterstitialAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubNativeAdExperiment;
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
import cm.aptoide.pt.app.AdsManager;
import cm.aptoide.pt.app.AppCoinsManager;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.AppViewAnalytics;
import cm.aptoide.pt.app.AppViewManager;
import cm.aptoide.pt.app.CampaignAnalytics;
import cm.aptoide.pt.app.DownloadStateParser;
import cm.aptoide.pt.app.FlagManager;
import cm.aptoide.pt.app.FlagService;
import cm.aptoide.pt.app.ReviewsManager;
import cm.aptoide.pt.app.view.AppCoinsInfoView;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.app.view.AppViewFragment.BundleKeys;
import cm.aptoide.pt.app.view.AppViewNavigator;
import cm.aptoide.pt.app.view.AppViewPresenter;
import cm.aptoide.pt.app.view.AppViewView;
import cm.aptoide.pt.app.view.EditorialAnalytics;
import cm.aptoide.pt.app.view.EditorialManager;
import cm.aptoide.pt.app.view.EditorialNavigator;
import cm.aptoide.pt.app.view.EditorialPresenter;
import cm.aptoide.pt.app.view.EditorialRepository;
import cm.aptoide.pt.app.view.EditorialService;
import cm.aptoide.pt.app.view.EditorialView;
import cm.aptoide.pt.app.view.MoreBundleManager;
import cm.aptoide.pt.app.view.MoreBundlePresenter;
import cm.aptoide.pt.app.view.MoreBundleView;
import cm.aptoide.pt.appview.PreferencesManager;
import cm.aptoide.pt.billing.view.login.PaymentLoginPresenter;
import cm.aptoide.pt.billing.view.login.PaymentLoginView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.home.AdMapper;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.home.BannerRepository;
import cm.aptoide.pt.home.BottomNavigationMapper;
import cm.aptoide.pt.home.BundlesRepository;
import cm.aptoide.pt.home.Home;
import cm.aptoide.pt.home.HomeAnalytics;
import cm.aptoide.pt.home.HomeNavigator;
import cm.aptoide.pt.home.HomePresenter;
import cm.aptoide.pt.home.HomeView;
import cm.aptoide.pt.home.apps.AppsNavigator;
import cm.aptoide.pt.impressions.ImpressionManager;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.permission.AccountPermissionProvider;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsPresenter;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsView;
import cm.aptoide.pt.promotions.PromotionViewAppMapper;
import cm.aptoide.pt.promotions.PromotionsAnalytics;
import cm.aptoide.pt.promotions.PromotionsManager;
import cm.aptoide.pt.promotions.PromotionsNavigator;
import cm.aptoide.pt.promotions.PromotionsPreferencesManager;
import cm.aptoide.pt.promotions.PromotionsPresenter;
import cm.aptoide.pt.promotions.PromotionsView;
import cm.aptoide.pt.search.SearchManager;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.suggestions.SearchSuggestionManager;
import cm.aptoide.pt.search.suggestions.TrendingManager;
import cm.aptoide.pt.search.view.SearchResultPresenter;
import cm.aptoide.pt.search.view.SearchResultView;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment.BundleCons;
import cm.aptoide.pt.store.view.my.MyStoresNavigator;
import cm.aptoide.pt.store.view.my.MyStoresPresenter;
import cm.aptoide.pt.store.view.my.MyStoresView;
import cm.aptoide.pt.timeline.SocialRepository;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.wizard.WizardPresenter;
import cm.aptoide.pt.view.wizard.WizardView;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import javax.inject.Named;
import okhttp3.OkHttpClient;
import org.parceler.Parcels;
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
        CrashReport.getInstance(), AndroidSchedulers.mainThread(), searchManager, trendingManager,
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
      BottomNavigationMapper bottomNavigationMapper, AppNavigator appNavigator,
      @Named("aptoide-theme") String theme) {
    return new HomeNavigator(fragmentNavigator, (AptoideBottomNavigator) fragment.getActivity(),
        bottomNavigationMapper, appNavigator, ((ActivityNavigator) fragment.getActivity()), theme);
  }

  @FragmentScope @Provides Home providesHome(BundlesRepository bundlesRepository,
      ImpressionManager impressionManager, AdsManager adsManager,
      PromotionsManager promotionsManager,
      PromotionsPreferencesManager promotionsPreferencesManager,
      MoPubBannerAdExperiment bannerAdExperiment, BannerRepository bannerRepository,
      MoPubNativeAdExperiment nativeAdExperiment) {
    return new Home(bundlesRepository, impressionManager, promotionsManager, bannerAdExperiment,
        nativeAdExperiment, bannerRepository, promotionsPreferencesManager);
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
      BottomNavigationMapper bottomNavigationMapper, AppNavigator appNavigator) {
    return new AppsNavigator(fragmentNavigator, (AptoideBottomNavigator) fragment.getActivity(),
        bottomNavigationMapper, appNavigator);
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

  @FragmentScope @Provides SocialRepository providesSocialRepository(
      @Named("pool-v7") BodyInterceptor<BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences) {
    return new SocialRepository(bodyInterceptorPoolV7, WebService.getDefaultConverter(),
        okHttpClient, tokenInvalidator, sharedPreferences);
  }

  @FragmentScope @Provides AppViewManager providesAppViewManager(InstallManager installManager,
      DownloadFactory downloadFactory, AppCenter appCenter, ReviewsManager reviewsManager,
      AdsManager adsManager, StoreManager storeManager, FlagManager flagManager,
      StoreUtilsProxy storeUtilsProxy, AptoideAccountManager aptoideAccountManager,
      AppViewConfiguration appViewConfiguration, PreferencesManager preferencesManager,
      DownloadStateParser downloadStateParser, AppViewAnalytics appViewAnalytics,
      NotificationAnalytics notificationAnalytics, InstallAnalytics installAnalytics,
      Resources resources, WindowManager windowManager, SocialRepository socialRepository,
      @Named("marketName") String marketName, AppCoinsManager appCoinsManager,
      MoPubInterstitialAdExperiment moPubInterstitialAdExperiment,
      MoPubBannerAdExperiment moPubBannerAdExperiment,
      MoPubNativeAdExperiment moPubNativeAdExperiment) {
    return new AppViewManager(installManager, downloadFactory, appCenter, reviewsManager,
        adsManager, storeManager, flagManager, storeUtilsProxy, aptoideAccountManager,
        appViewConfiguration, preferencesManager, downloadStateParser, appViewAnalytics,
        notificationAnalytics, installAnalytics,
        (Type.APPS_GROUP.getPerLineCount(resources, windowManager) * 6), socialRepository,
        marketName, appCoinsManager, moPubInterstitialAdExperiment, moPubBannerAdExperiment,
        moPubNativeAdExperiment);
  }

  @FragmentScope @Provides AppViewPresenter providesAppViewPresenter(
      AccountNavigator accountNavigator, AppViewAnalytics analytics,
      CampaignAnalytics campaignAnalytics, AppViewNavigator appViewNavigator,
      AppViewManager appViewManager, AptoideAccountManager accountManager,
      CrashReport crashReport) {
    return new AppViewPresenter((AppViewView) fragment, accountNavigator, analytics,
        campaignAnalytics, appViewNavigator, appViewManager, accountManager,
        AndroidSchedulers.mainThread(), crashReport, new PermissionManager(),
        ((PermissionService) fragment.getContext()));
  }

  @FragmentScope @Provides AppViewConfiguration providesAppViewConfiguration() {
    return new AppViewConfiguration(arguments.getLong(BundleKeys.APP_ID.name(), -1),
        arguments.getString(BundleKeys.PACKAGE_NAME.name(), null),
        arguments.getString(BundleKeys.STORE_NAME.name(), null),
        arguments.getString(BundleKeys.STORE_THEME.name(), ""),
        Parcels.unwrap(arguments.getParcelable(BundleKeys.MINIMAL_AD.name())),
        ((AppViewFragment.OpenType) arguments.getSerializable(BundleKeys.SHOULD_INSTALL.name())),
        arguments.getString(BundleKeys.MD5.name(), ""),
        arguments.getString(BundleKeys.UNAME.name(), ""),
        arguments.getDouble(BundleKeys.APPC.name(), -1),
        arguments.getString(BundleKeys.EDITORS_CHOICE_POSITION.name(), ""),
        arguments.getString(BundleKeys.ORIGIN_TAG.name(), ""),
        arguments.getString(BundleKeys.DOWNLOAD_CONVERSION_URL.name(), ""));
  }

  @FragmentScope @Provides MoreBundlePresenter providesGetStoreWidgetsPresenter(
      MoreBundleManager moreBundleManager, CrashReport crashReport, HomeNavigator homeNavigator,
      AdMapper adMapper, BundleEvent bundleEvent, HomeAnalytics homeAnalytics) {
    return new MoreBundlePresenter((MoreBundleView) fragment, moreBundleManager,
        AndroidSchedulers.mainThread(), crashReport, homeNavigator, adMapper, bundleEvent,
        homeAnalytics);
  }

  @FragmentScope @Provides MoreBundleManager providesGetStoreManager(
      BundlesRepository bundlesRepository) {
    return new MoreBundleManager(bundlesRepository);
  }

  @FragmentScope @Provides BundleEvent providesBundleEvent() {
    return new BundleEvent(arguments.getString(BundleCons.TITLE),
        arguments.getString(BundleCons.ACTION));
  }

  @FragmentScope @Provides WizardPresenter providesWizardPresenter(
      AptoideAccountManager aptoideAccountManager, CrashReport crashReport,
      AccountAnalytics accountAnalytics) {
    return new WizardPresenter((WizardView) fragment, aptoideAccountManager, crashReport,
        accountAnalytics);
  }

  @FragmentScope @Provides PaymentLoginPresenter providesPaymentLoginPresenter(
      AccountNavigator accountNavigator, AptoideAccountManager accountManager,
      CrashReport crashReport, AccountErrorMapper accountErrorMapper,
      ScreenOrientationManager screenOrientationManager, AccountAnalytics accountAnalytics) {
    return new PaymentLoginPresenter((PaymentLoginView) fragment,
        arguments.getInt(FragmentNavigator.REQUEST_CODE_EXTRA),
        Arrays.asList("email", "user_friends"), accountNavigator, Arrays.asList("email"),
        accountManager, crashReport, accountErrorMapper, AndroidSchedulers.mainThread(),
        screenOrientationManager, accountAnalytics);
  }

  @FragmentScope @Provides AppCoinsInfoPresenter providesAppCoinsInfoPresenter(
      AppCoinsInfoNavigator appCoinsInfoNavigator, InstallManager installManager,
      CrashReport crashReport) {
    return new AppCoinsInfoPresenter((AppCoinsInfoView) fragment, appCoinsInfoNavigator,
        installManager, crashReport, AppCoinsInfoNavigator.APPC_WALLET_PACKAGE_NAME,
        AndroidSchedulers.mainThread());
  }

  @FragmentScope @Provides EditorialRepository providesEditorialRepository(
      EditorialService editorialService) {
    return new EditorialRepository(editorialService);
  }

  @FragmentScope @Provides EditorialManager providesEditorialManager(
      EditorialRepository editorialRepository, InstallManager installManager,
      PreferencesManager preferencesManager, DownloadFactory downloadFactory,
      DownloadStateParser downloadStateParser, NotificationAnalytics notificationAnalytics,
      InstallAnalytics installAnalytics, EditorialAnalytics editorialAnalytics) {
    return new EditorialManager(editorialRepository, arguments.getString("cardId", ""),
        installManager, preferencesManager, downloadFactory, downloadStateParser,
        notificationAnalytics, installAnalytics, editorialAnalytics);
  }

  @FragmentScope @Provides EditorialPresenter providesEditorialPresenter(
      EditorialManager editorialManager, CrashReport crashReport,
      EditorialAnalytics editorialAnalytics, EditorialNavigator editorialNavigator) {
    return new EditorialPresenter((EditorialView) fragment, editorialManager,
        AndroidSchedulers.mainThread(), crashReport, new PermissionManager(),
        ((PermissionService) fragment.getContext()), editorialAnalytics, editorialNavigator);
  }

  @FragmentScope @Provides PromotionsPresenter providesPromotionsPresenter(
      PromotionsManager promotionsManager, PromotionsAnalytics promotionsAnalytics,
      PromotionsNavigator promotionsNavigator) {
    return new PromotionsPresenter((PromotionsView) fragment, promotionsManager,
        new PermissionManager(), ((PermissionService) fragment.getContext()),
        AndroidSchedulers.mainThread(), promotionsAnalytics, promotionsNavigator);
  }

  @FragmentScope @Provides PromotionViewAppMapper providesPromotionViewAppMapper(
      DownloadStateParser downloadStateParser) {
    return new PromotionViewAppMapper(downloadStateParser);
  }
}

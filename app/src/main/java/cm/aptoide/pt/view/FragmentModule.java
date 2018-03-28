package cm.aptoide.pt.view;

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
import cm.aptoide.pt.account.view.user.CreateUserErrorMapper;
import cm.aptoide.pt.account.view.user.ManageUserNavigator;
import cm.aptoide.pt.account.view.user.ManageUserPresenter;
import cm.aptoide.pt.account.view.user.ManageUserView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.AdMapper;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.home.BottomNavigationMapper;
import cm.aptoide.pt.home.BundlesRepository;
import cm.aptoide.pt.home.Home;
import cm.aptoide.pt.home.HomeAnalytics;
import cm.aptoide.pt.home.HomeNavigator;
import cm.aptoide.pt.home.HomePresenter;
import cm.aptoide.pt.home.HomeView;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
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
import cm.aptoide.pt.store.view.my.MyStoresNavigator;
import cm.aptoide.pt.store.view.my.MyStoresPresenter;
import cm.aptoide.pt.store.view.my.MyStoresView;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
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
            .getApplicationContext()).hasMultiStoreSearch(),
        ((AptoideApplication) fragment.getContext()
            .getApplicationContext()).getDefaultStoreName(),
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

  @FragmentScope @Provides BottomNavigationMapper providesBottomNavigationMapper() {
    return new BottomNavigationMapper();
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
}

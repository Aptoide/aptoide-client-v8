package cm.aptoide.pt.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
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
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.home.AdMapper;
import cm.aptoide.pt.home.BundleDataSource;
import cm.aptoide.pt.home.BundlesRepository;
import cm.aptoide.pt.home.BundlesResponseMapper;
import cm.aptoide.pt.home.Home;
import cm.aptoide.pt.home.HomeNavigator;
import cm.aptoide.pt.home.HomePresenter;
import cm.aptoide.pt.home.HomeView;
import cm.aptoide.pt.home.RemoteBundleDataSource;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.permission.AccountPermissionProvider;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsPresenter;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsView;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import javax.inject.Named;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

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

  @Provides @FragmentScope LoginSignUpCredentialsPresenter provideLoginSignUpPresenter(
      AptoideAccountManager accountManager, AccountNavigator accountNavigator,
      AccountErrorMapper errorMapper, AccountAnalytics accountAnalytics) {
    return new LoginSignUpCredentialsPresenter((LoginSignUpCredentialsView) fragment,
        accountManager, CrashReport.getInstance(),
        arguments.getBoolean("dismiss_to_navigate_to_main_view"),
        arguments.getBoolean("clean_back_stack"), accountNavigator,
        Arrays.asList("email", "user_friends"), Arrays.asList("email"), errorMapper,
        accountAnalytics);
  }

  @Provides @FragmentScope ImagePickerPresenter provideImagePickerPresenter(
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

  @FragmentScope @Provides HomePresenter providesHomePresenter(Home home,
      HomeNavigator homeNavigator, AdMapper adMapper) {
    return new HomePresenter((HomeView) fragment, home, AndroidSchedulers.mainThread(),
        CrashReport.getInstance(), homeNavigator, adMapper);
  }

  @FragmentScope @Provides HomeNavigator providesHomeNavigator(
      FragmentNavigator fragmentNavigator) {
    return new HomeNavigator(fragmentNavigator);
  }

  @Named("remote") @FragmentScope @Provides BundleDataSource providesRemoteBundleDataSource(
      @Named("pool-v7") BodyInterceptor<BaseBody> bodyInterceptorPoolV7,
      @Named("default") OkHttpClient okHttpClient, Converter.Factory converter,
      BundlesResponseMapper mapper, TokenInvalidator tokenInvalidator,
      @Named("default") SharedPreferences sharedPreferences, AptoideAccountManager accountManager) {
    return new RemoteBundleDataSource(5, Integer.MAX_VALUE, bodyInterceptorPoolV7, okHttpClient,
        converter, mapper, tokenInvalidator, sharedPreferences, new WSWidgetsUtils(),
        new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
            ((AptoideApplication) getApplicationContext().getApplicationContext()).getDatabase(),
            Store.class)).fromUrl(""),
        ((AptoideApplication) getApplicationContext()).getIdsRepository()
            .getUniqueIdentifier(),
        AdNetworkUtils.isGooglePlayServicesAvailable(getApplicationContext()),
        ((AptoideApplication) getApplicationContext()).getPartnerId(), accountManager,
        ((AptoideApplication) getApplicationContext()).getQManager()
            .getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)),
        getApplicationContext().getResources(),
        (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE),
        (ConnectivityManager) getApplicationContext().getSystemService(
            Context.CONNECTIVITY_SERVICE),
        ((AptoideApplication) getApplicationContext()).getVersionCodeProvider());
  }

  @FragmentScope @Provides BundlesRepository providesBundleRepository(
      @Named("remote") BundleDataSource remoteBundleDataSource,
      @Named("local") BundleDataSource localBundleDataSource) {
    return new BundlesRepository(remoteBundleDataSource, localBundleDataSource);
  }

  @FragmentScope @Provides Home providesHome(BundlesRepository bundlesRepository) {
    return new Home(bundlesRepository);
  }
}

package cm.aptoide.pt.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.accountmanager.AptoideAccountManager;
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
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.permission.AccountPermissionProvider;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsPresenter;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsView;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module public class FragmentModule {

  private final Fragment fragment;
  private final Bundle savedInstance;
  private final boolean dismissToNavigateToMainView;
  private final boolean navigateToHome;
  private final boolean goToHome;
  private final boolean isEditProfile;
  private final boolean isCreateStoreUserPrivacyEnabled;
  private final String packageName;


  public FragmentModule(Fragment fragment, Bundle savedInstance, boolean dismissToNavigateToMainView, boolean navigateToHome, boolean goToHome,
      boolean isEditProfile, boolean isCreateStoreUserPrivacyEnabled, String packageName) {
    this.fragment = fragment;
    this.savedInstance = savedInstance;
    this.dismissToNavigateToMainView = dismissToNavigateToMainView;
    this.navigateToHome = navigateToHome;
    this.goToHome = goToHome;
    this.isEditProfile = isEditProfile;
    this.isCreateStoreUserPrivacyEnabled = isCreateStoreUserPrivacyEnabled;
    this.packageName = packageName;
  }

  @Provides @FragmentScope LoginSignUpCredentialsPresenter provideLoginSignUpPresenter(AptoideAccountManager accountManager, AccountNavigator accountNavigator,
      AccountErrorMapper errorMapper, AccountAnalytics accountAnalytics){
    return new LoginSignUpCredentialsPresenter((LoginSignUpCredentialsView) fragment, accountManager, CrashReport.getInstance(),
        dismissToNavigateToMainView, navigateToHome, accountNavigator,
        Arrays.asList("email", "user_friends"), Arrays.asList("email"), errorMapper,
        accountAnalytics);
  }

  @Provides @FragmentScope ImagePickerPresenter provideImagePickerPresenter(AccountPermissionProvider accountPermissionProvider,
      PhotoFileGenerator photoFileGenerator, ImageValidator imageValidator, UriToPathResolver uriToPathResolver, ImagePickerNavigator imagePickerNavigator){
    return new ImagePickerPresenter((ImagePickerView) fragment, CrashReport.getInstance(), accountPermissionProvider, photoFileGenerator,
        imageValidator, AndroidSchedulers.mainThread(), uriToPathResolver, imagePickerNavigator,
        fragment.getActivity().getContentResolver(), ImageLoader.with(fragment.getContext()));
  }

  @FragmentScope @Provides ManageStorePresenter provideManageStorePresenter(StoreManager storeManager, UriToPathResolver uriToPathResolver,
      ManageStoreNavigator manageStoreNavigator, ManageStoreErrorMapper manageStoreErrorMapper){
    return new ManageStorePresenter((ManageStoreView) fragment, CrashReport.getInstance(), storeManager, uriToPathResolver,
        packageName, manageStoreNavigator, goToHome, manageStoreErrorMapper);
  }

  @FragmentScope @Provides ManageUserPresenter provideManageUserPresenter(AptoideAccountManager accountManager, CreateUserErrorMapper errorMapper,
      ManageUserNavigator manageUserNavigator, UriToPathResolver uriToPathResolver){
    return new ManageUserPresenter((ManageUserView) fragment, CrashReport.getInstance(), accountManager, errorMapper, manageUserNavigator,
        isEditProfile, uriToPathResolver, isCreateStoreUserPrivacyEnabled, savedInstance);
  }

  @FragmentScope @Provides ImageValidator provideImageValidator(){
    return new ImageValidator(ImageLoader.with(fragment.getContext()), Schedulers.computation());
  }

  @FragmentScope @Provides CreateUserErrorMapper provideCreateUserErrorMapper(AccountErrorMapper accountErrorMapper){
    return new CreateUserErrorMapper(fragment.getContext(), accountErrorMapper, fragment.getResources());
  }

  @FragmentScope @Provides AccountErrorMapper provideAccountErrorMapper(){
    return new AccountErrorMapper(fragment.getContext(), new ErrorsMapper());
  }

  @FragmentScope @Provides ManageStoreErrorMapper provideManageStoreErrorMapper(){
    return new ManageStoreErrorMapper(fragment.getResources(), new ErrorsMapper());
  }
}

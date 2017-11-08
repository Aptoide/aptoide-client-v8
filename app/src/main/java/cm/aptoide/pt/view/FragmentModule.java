package cm.aptoide.pt.view;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountErrorMapper;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsPresenter;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsView;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;

@Module public class FragmentModule {

  private final BaseFragment fragment;
  private final boolean dismissToNavigateToMainView;
  private final boolean navigateToHome;

  public FragmentModule(BaseFragment fragment, boolean dismissToNavigateToMainView,
      boolean navigateToHome) {
    this.fragment = fragment;
    this.dismissToNavigateToMainView = dismissToNavigateToMainView;
    this.navigateToHome = navigateToHome;
  }

  @Provides @FragmentScope LoginSignUpCredentialsPresenter provideLoginSignUpPresenter(AptoideAccountManager accountManager, AccountNavigator accountNavigator,
      AccountErrorMapper errorMapper, AccountAnalytics accountAnalytics){
    return new LoginSignUpCredentialsPresenter((LoginSignUpCredentialsView) fragment, accountManager, CrashReport.getInstance(),
        dismissToNavigateToMainView, navigateToHome, accountNavigator,
        Arrays.asList("email", "user_friends"), Arrays.asList("email"), errorMapper,
        accountAnalytics);
  }

}

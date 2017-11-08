package cm.aptoide.pt.view;

import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.account.view.LoginSignUpPresenter;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsPresenter;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;

@Module public class FragmentModule {

  @Provides @FragmentScope LoginSignUpPresenter provideLoginSignUpPresenter(){
    return new LoginSignUpCredentialsPresenter(this, accountManager, crashReport,
        dismissToNavigateToMainView, navigateToHome, accountNavigator,
        Arrays.asList("email", "user_friends"), Arrays.asList("email"), errorMapper,
        ((AptoideApplication) getContext().getApplicationContext()).getAccountAnalytics());
  }
}

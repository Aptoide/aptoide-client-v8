package cm.aptoide.pt.presenter;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.account.view.LoginSignUpCredentialsConfiguration;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import java.util.Collection;

public class LoginSignupCredentialsFlavorPresenter extends LoginSignUpCredentialsPresenter {

  private final LoginSignUpCredentialsView view;
  private final ThrowableToStringMapper errorMapper;
  private final CrashReport crashReport;
  private final AccountNavigator accountNavigator;

  public LoginSignupCredentialsFlavorPresenter(LoginSignUpCredentialsView view,
      AptoideAccountManager accountManager, CrashReport crashReport,
      LoginSignUpCredentialsConfiguration configuration, AccountNavigator accountNavigator,
      Collection<String> permissions, ThrowableToStringMapper errorMapper,
      AccountAnalytics accountAnalytics) {
    super(view, accountManager, crashReport, configuration, accountNavigator, permissions,
        errorMapper, accountAnalytics);
    this.view = view;
    this.errorMapper = errorMapper;
    this.crashReport = crashReport;
    this.accountNavigator = accountNavigator;
  }

  @Override public void present() {
    super.present();
    handleConnectWithEmailClick();
  }

  private void handleConnectWithEmailClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.showAptoideLoginAreaClick()
            .doOnNext(___ -> view.showAptoideLoginArea())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError(errorMapper.map(err));
          crashReport.log(err);
        });
  }

  @Override public boolean handle() {
    return view.tryCloseLoginBottomSheet(true);
  }
}

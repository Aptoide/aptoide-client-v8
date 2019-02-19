package cm.aptoide.pt.presenter;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.AptoideCredentials;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import java.util.Collection;
import rx.Observable;

public class LoginSignupCredentialsFlavorPresenter extends LoginSignUpCredentialsPresenter {

  private final LoginSignUpCredentialsView view;
  private final ThrowableToStringMapper errorMapper;
  private final CrashReport crashReport;

  public LoginSignupCredentialsFlavorPresenter(LoginSignUpCredentialsView view,
      AptoideAccountManager accountManager, CrashReport crashReport,
      boolean dismissToNavigateToMainView, boolean navigateToHome,
      AccountNavigator accountNavigator, Collection<String> permissions,
      Collection<String> requiredPermissions, ThrowableToStringMapper errorMapper,
      AccountAnalytics accountAnalytics) {
    super(view, accountManager, crashReport, dismissToNavigateToMainView, navigateToHome,
        accountNavigator, permissions, requiredPermissions, errorMapper, accountAnalytics);
    this.view = view;
    this.errorMapper = errorMapper;
    this.crashReport = crashReport;
  }

  @Override public void present() {

    super.present();

    handleCobrandText();
    showAptoideSignUpEvent();
    handleAptoideShowSignUpEvent();
    hideTCandPP();
  }

  protected Observable<AptoideCredentials> getAptoideSignUpEvent() {
    return view.aptoideSignUpEvent();
  }

  private void hideTCandPP() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.hideTCandPP())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleAptoideShowSignUpEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> showAptoideSignUpEvent())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError(errorMapper.map(err));
          crashReport.log(err);
        });
  }

  private Observable<Boolean> showAptoideSignUpEvent() {
    return view.showAptoideSignUpAreaClick()
        .doOnNext(__ -> view.showAptoideSignUpArea());
  }

  private void handleCobrandText() {
    view.getLifecycleEvent()
        .doOnNext(__ -> view.setCobrandText())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          crashReport.log(err);
        });
  }

  @Override public boolean handle() {
    return view.tryCloseLoginBottomSheet(false);
  }
}

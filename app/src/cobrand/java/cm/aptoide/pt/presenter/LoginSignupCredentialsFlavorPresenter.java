package cm.aptoide.pt.presenter;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import java.util.Collection;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class LoginSignupCredentialsFlavorPresenter extends LoginSignUpCredentialsPresenter {

  private final LoginSignUpCredentialsView view;
  private final AccountAnalytics accountAnalytics;
  private final AptoideAccountManager accountManager;
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
    this.accountAnalytics = accountAnalytics;
    this.accountManager = accountManager;
    this.errorMapper = errorMapper;
    this.crashReport = crashReport;
  }

  @Override public void present() {

    super.present();

    handleCobrandText();
    showAptoideSignUpEvent();
    handleAptoideShowSignUpEvent();
    handleAptoideSignUpEvent();
    hideTCandPP();
  }

  private void handleAptoideSignUpEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.aptoideSignUpEvent()
            .doOnNext(click -> {
              view.hideKeyboard();
              view.showLoading();
              lockScreenRotation();
              accountAnalytics.sendAptoideSignUpButtonPressed();
            })
            .flatMapCompletable(
                credentials -> accountManager.signUp(AptoideAccountManager.APTOIDE_SIGN_UP_TYPE,
                    credentials)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnCompleted(() -> {
                      accountAnalytics.loginSuccess();
                      navigateToCreateProfile();
                      unlockScreenRotation();
                      view.hideLoading();
                    })
                    .doOnError(throwable -> {
                      accountAnalytics.sendSignUpErrorEvent(AccountAnalytics.LoginMethod.APTOIDE,
                          throwable);
                      view.showError(errorMapper.map(throwable));
                      crashReport.log(throwable);
                      unlockScreenRotation();
                      view.hideLoading();
                    }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
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

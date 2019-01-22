package cm.aptoide.pt.billing.view.login;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.AptoideCredentials;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.FacebookSignUpAdapter;
import cm.aptoide.pt.account.FacebookSignUpException;
import cm.aptoide.pt.account.GoogleSignUpAdapter;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import java.util.Collection;
import rx.Observable;
import rx.Scheduler;

public class PaymentLoginPresenter implements Presenter {

  private static final int RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE = 2;
  private final PaymentLoginView view;
  private final AccountNavigator accountNavigator;
  private final int requestCode;
  private final Collection<String> permissions;
  private final Collection<String> requiredPermissions;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final ThrowableToStringMapper errorMapper;
  private final Scheduler viewScheduler;
  private final ScreenOrientationManager orientationManager;
  private final AccountAnalytics accountAnalytics;

  public PaymentLoginPresenter(PaymentLoginView view, int requestCode,
      Collection<String> permissions, AccountNavigator accountNavigator,
      Collection<String> requiredPermissions, AptoideAccountManager accountManager,
      CrashReport crashReport, ThrowableToStringMapper errorMapper, Scheduler viewScheduler,
      ScreenOrientationManager orientationManager, AccountAnalytics accountAnalytics) {
    this.view = view;
    this.accountNavigator = accountNavigator;
    this.requestCode = requestCode;
    this.permissions = permissions;
    this.requiredPermissions = requiredPermissions;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.errorMapper = errorMapper;
    this.viewScheduler = viewScheduler;
    this.orientationManager = orientationManager;
    this.accountAnalytics = accountAnalytics;
  }

  @Override public void present() {

    onViewCreatedCheckLoginStatus();

    handleClickOnTermsAndConditions();

    handleClickOnPrivacyPolicy();

    handleBackButtonAndUpNavigationEvent();

    handleFacebookSignUpResult();

    handleFacebookSignUpEvent();

    handleGrantFacebookRequiredPermissionsEvent();

    handleGoogleSignUpResult();

    handleGoogleSignUpEvent();

    handleRecoverPasswordEvent();

    handleAptoideLoginEvent();

    handleAptoideSignUpEvent();
  }

  private void handleClickOnTermsAndConditions() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.termsAndConditionsClickEvent())
        .doOnNext(__ -> accountNavigator.navigateToTermsAndConditions())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnPrivacyPolicy() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.privacyPolicyClickEvent())
        .doOnNext(__ -> accountNavigator.navigateToPrivacyPolicy())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleGrantFacebookRequiredPermissionsEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.grantFacebookRequiredPermissionsEvent())
        .doOnNext(__ -> view.showLoading())
        .doOnNext(__ -> accountNavigator.navigateToFacebookSignUpForResult(requiredPermissions))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void onViewCreatedCheckLoginStatus() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> accountManager.accountStatus())
        .filter(account -> account.isLoggedIn())
        .doOnNext(account -> accountAnalytics.loginSuccess())
        .observeOn(viewScheduler)
        .doOnNext(__ -> accountNavigator.popViewWithResult(requestCode, true))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleAptoideSignUpEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(event -> view.aptoideSignUpEvent()
            .doOnNext(credentials -> showNotCheckedMessage(credentials.isChecked()))
            .filter(AptoideCredentials::isChecked)
            .doOnNext(__ -> {
              view.showLoading();
              orientationManager.lock();
              accountAnalytics.sendAptoideSignUpButtonPressed();
            })
            .flatMapCompletable(
                result -> accountManager.signUp(AptoideAccountManager.APTOIDE_SIGN_UP_TYPE, result)
                    .observeOn(viewScheduler)
                    .doOnTerminate(() -> {
                      view.hideLoading();
                      orientationManager.unlock();
                    })
                    .doOnError(throwable -> {
                      accountAnalytics.sendSignUpErrorEvent(AccountAnalytics.LoginMethod.APTOIDE,
                          throwable);
                      view.showError(errorMapper.map(throwable));
                      crashReport.log(throwable);
                    }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleAptoideLoginEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(event -> view.aptoideLoginEvent()
            .doOnNext(__ -> {
              view.showLoading();
              orientationManager.lock();
              accountAnalytics.sendAptoideLoginButtonPressed();
            })
            .flatMapCompletable(result -> accountManager.login(result)
                .observeOn(viewScheduler)
                .doOnTerminate(() -> {
                  view.hideLoading();
                  orientationManager.unlock();
                })
                .doOnError(throwable -> {
                  accountAnalytics.sendLoginErrorEvent(AccountAnalytics.LoginMethod.APTOIDE,
                      throwable);
                  view.showError(errorMapper.map(throwable));
                  crashReport.log(throwable);
                }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleFacebookSignUpResult() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> accountNavigator.facebookSignUpResults()
            .flatMapCompletable(result -> accountManager.signUp(FacebookSignUpAdapter.TYPE, result)
                .observeOn(viewScheduler)
                .doOnTerminate(() -> view.hideLoading())
                .doOnError(throwable -> {

                  if (throwable instanceof FacebookSignUpException
                      && ((FacebookSignUpException) throwable).getCode()
                      == FacebookSignUpException.MISSING_REQUIRED_PERMISSIONS) {
                    view.showFacebookPermissionsRequiredError();
                  } else {
                    view.showError(errorMapper.map(throwable));
                    crashReport.log(throwable);
                  }
                  accountAnalytics.sendLoginErrorEvent(AccountAnalytics.LoginMethod.FACEBOOK,
                      throwable);
                }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  public void handleFacebookSignUpEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.facebookSignUpEvent())
        .doOnNext(__ -> view.showLoading())
        .doOnNext(click -> accountAnalytics.sendFacebookLoginButtonPressed())
        .doOnNext(__ -> accountNavigator.navigateToFacebookSignUpForResult(permissions))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleGoogleSignUpEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.googleSignUpEvent())
        .doOnNext(event -> view.showLoading())
        .doOnNext(event -> accountAnalytics.sendGoogleLoginButtonPressed())
        .flatMapSingle(event -> accountNavigator.navigateToGoogleSignUpForResult(
            RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE))
        .observeOn(viewScheduler)
        .doOnNext(connectionResult -> {
          if (!connectionResult.isSuccess()) {
            view.showConnectionError(connectionResult);
            view.hideLoading();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError(errorMapper.map(err));
          crashReport.log(err);
        });
  }

  private void handleGoogleSignUpResult() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> accountNavigator.googleSignUpResults(RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE)
            .flatMapCompletable(result -> accountManager.signUp(GoogleSignUpAdapter.TYPE, result)
                .observeOn(viewScheduler)
                .doOnTerminate(() -> view.hideLoading())
                .doOnError(throwable -> {
                  view.showError(errorMapper.map(throwable));
                  crashReport.log(throwable);
                  accountAnalytics.sendLoginErrorEvent(AccountAnalytics.LoginMethod.GOOGLE,
                      throwable);
                }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleBackButtonAndUpNavigationEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.backButtonEvent(), view.upNavigationEvent()))
        .doOnNext(__ -> accountNavigator.popViewWithResult(requestCode, false))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleRecoverPasswordEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.recoverPasswordEvent())
        .doOnNext(__ -> accountNavigator.navigateToRecoverPasswordView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void showNotCheckedMessage(boolean checked) {
    if (!checked) {
      view.showTermsConditionError();
    }
  }
}

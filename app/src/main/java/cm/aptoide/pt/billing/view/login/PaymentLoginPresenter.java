package cm.aptoide.pt.billing.view.login;

import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.FacebookSignUpAdapter;
import cm.aptoide.pt.account.FacebookSignUpException;
import cm.aptoide.pt.account.GoogleSignUpAdapter;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import cm.aptoide.pt.view.orientation.ScreenOrientationManager;
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

  public PaymentLoginPresenter(PaymentLoginView view, int requestCode,
      Collection<String> permissions, AccountNavigator accountNavigator,
      Collection<String> requiredPermissions, AptoideAccountManager accountManager,
      CrashReport crashReport, ThrowableToStringMapper errorMapper, Scheduler viewScheduler,
      ScreenOrientationManager orientationManager) {
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
  }

  @Override public void present() {

    onViewCreatedCheckLoginStatus();

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

  private void handleGrantFacebookRequiredPermissionsEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.grantFacebookRequiredPermissionsEvent())
        .doOnNext(__ -> view.showLoading())
        .doOnNext(__ -> accountNavigator.navigateToFacebookSignUpForResult(requiredPermissions))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void onViewCreatedCheckLoginStatus() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> accountManager.accountStatus())
        .filter(account -> account.isLoggedIn())
        .observeOn(viewScheduler)
        .doOnNext(__ -> accountNavigator.popViewWithResult(requestCode, true))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleAptoideSignUpEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(event -> view.aptoideSignUpEvent()
            .doOnNext(__ -> {
              view.showLoading();
              orientationManager.lock();
            })
            .flatMapCompletable(
                result -> accountManager.signUp(AptoideAccountManager.APTOIDE_SIGN_UP_TYPE, result)
                    .observeOn(viewScheduler)
                    .doOnCompleted(() -> sendAptoideSignUpSuccessEvent())
                    .doOnTerminate(() -> {
                      view.hideLoading();
                      orientationManager.unlock();
                    })
                    .doOnError(throwable -> {
                      sendAptoideSignUpFailEvent();
                      view.showError(errorMapper.map(throwable));
                      crashReport.log(throwable);
                    }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleAptoideLoginEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(event -> view.aptoideLoginEvent()
            .doOnNext(__ -> {
              view.showLoading();
              orientationManager.lock();
            })
            .flatMapCompletable(result -> accountManager.login(result)
                .observeOn(viewScheduler)
                .doOnCompleted(() -> sendAptoideLoginSuccessEvent())
                .doOnTerminate(() -> {
                  view.hideLoading();
                  orientationManager.unlock();
                })
                .doOnError(throwable -> {
                  sendAptoideLoginFailEvent();
                  view.showError(errorMapper.map(throwable));
                  crashReport.log(throwable);
                }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleFacebookSignUpResult() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> accountNavigator.facebookSignUpResults()
            .flatMapCompletable(result -> accountManager.signUp(FacebookSignUpAdapter.TYPE, result)
                .observeOn(viewScheduler)
                .doOnCompleted(() -> sendFacebookSignUpSuccessEvent())
                .doOnTerminate(() -> view.hideLoading())
                .doOnError(throwable -> {
                  sendFacebookSignUpErrorEvent(throwable);

                  if (throwable instanceof FacebookSignUpException
                      && ((FacebookSignUpException) throwable).getCode()
                      == FacebookSignUpException.MISSING_REQUIRED_PERMISSIONS) {
                    view.showFacebookPermissionsRequiredError();
                  } else {
                    view.showError(errorMapper.map(throwable));
                    crashReport.log(throwable);
                  }
                }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  public void handleFacebookSignUpEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.facebookSignUpEvent())
        .doOnNext(__ -> view.showLoading())
        .doOnNext(__ -> accountNavigator.navigateToFacebookSignUpForResult(permissions))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleGoogleSignUpEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.googleSignUpEvent())
        .doOnNext(event -> view.showLoading())
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
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> accountNavigator.googleSignUpResults(RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE)
            .flatMapCompletable(result -> accountManager.signUp(GoogleSignUpAdapter.TYPE, result)
                .observeOn(viewScheduler)
                .doOnCompleted(() -> sendGoogleSignUpSuccessEvent())
                .doOnTerminate(() -> view.hideLoading())
                .doOnError(throwable -> {
                  view.showError(errorMapper.map(throwable));
                  crashReport.log(throwable);
                  sendGoogleSignUpFailEvent();
                }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleBackButtonAndUpNavigationEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.backButtonEvent(), view.upNavigationEvent()))
        .doOnNext(__ -> accountNavigator.popViewWithResult(requestCode, false))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleRecoverPasswordEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.recoverPasswordEvent())
        .doOnNext(__ -> accountNavigator.navigateToRecoverPasswordView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void sendFacebookSignUpErrorEvent(Throwable throwable) {
    if (throwable instanceof FacebookSignUpException) {
      switch (((FacebookSignUpException) throwable).getCode()) {
        case FacebookSignUpException.MISSING_REQUIRED_PERMISSIONS:
          Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
              Analytics.Account.SignUpLoginStatus.FAILED,
              Analytics.Account.LoginStatusDetail.PERMISSIONS_DENIED);
          break;
        case FacebookSignUpException.USER_CANCELLED:
          Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
              Analytics.Account.SignUpLoginStatus.FAILED,
              Analytics.Account.LoginStatusDetail.CANCEL);
          break;
        case FacebookSignUpException.ERROR:
          Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
              Analytics.Account.SignUpLoginStatus.FAILED,
              Analytics.Account.LoginStatusDetail.SDK_ERROR);
          break;
      }
    }
  }

  private void sendAptoideLoginSuccessEvent() {
    Analytics.Account.loginStatus(Analytics.Account.LoginMethod.APTOIDE,
        Analytics.Account.SignUpLoginStatus.SUCCESS, Analytics.Account.LoginStatusDetail.SUCCESS);
  }

  private void sendFacebookSignUpSuccessEvent() {
    Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
        Analytics.Account.SignUpLoginStatus.SUCCESS, Analytics.Account.LoginStatusDetail.SUCCESS);
  }

  private void sendGoogleSignUpSuccessEvent() {
    Analytics.Account.loginStatus(Analytics.Account.LoginMethod.GOOGLE,
        Analytics.Account.SignUpLoginStatus.SUCCESS, Analytics.Account.LoginStatusDetail.SUCCESS);
  }

  private void sendGoogleSignUpFailEvent() {
    Analytics.Account.loginStatus(Analytics.Account.LoginMethod.GOOGLE,
        Analytics.Account.SignUpLoginStatus.FAILED, Analytics.Account.LoginStatusDetail.SDK_ERROR);
  }

  private void sendAptoideLoginFailEvent() {
    Analytics.Account.loginStatus(Analytics.Account.LoginMethod.APTOIDE,
        Analytics.Account.SignUpLoginStatus.FAILED,
        Analytics.Account.LoginStatusDetail.GENERAL_ERROR);
  }

  private void sendAptoideSignUpSuccessEvent() {
    Analytics.Account.signInSuccessAptoide(Analytics.Account.SignUpLoginStatus.SUCCESS);
  }

  private void sendAptoideSignUpFailEvent() {
    Analytics.Account.signInSuccessAptoide(Analytics.Account.SignUpLoginStatus.FAILED);
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}

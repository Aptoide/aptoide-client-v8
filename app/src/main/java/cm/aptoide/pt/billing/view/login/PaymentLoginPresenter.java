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
import java.util.Collection;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class PaymentLoginPresenter implements Presenter {

  private static final int RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE = 2;
  private final PaymentLoginView view;
  private final AccountNavigator accountNavigator;
  private final int requestCode;
  private final Collection<String> permissions;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final ThrowableToStringMapper errorMapper;

  public PaymentLoginPresenter(PaymentLoginView view, int requestCode,
      Collection<String> permissions, AccountNavigator accountNavigator,
      AptoideAccountManager accountManager, CrashReport crashReport,
      ThrowableToStringMapper errorMapper) {
    this.view = view;
    this.accountNavigator = accountNavigator;
    this.requestCode = requestCode;
    this.permissions = permissions;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.errorMapper = errorMapper;
  }

  @Override public void present() {

    handleBackButtonAndUpNavigationEvent();

    handleFacebookSignUpEvent();

    handleFacebookSignUpResult();

    handleGoogleSignUpEvent();

    handleGoogleSignUpResult();

    handleRecoverPasswordEvent();
  }

  private void handleFacebookSignUpResult() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> accountNavigator.facebookSignUpResults()
            .flatMapCompletable(result -> accountManager.signUp(FacebookSignUpAdapter.TYPE, result)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                  sendFacebookSignUpSuccessEvent();
                  accountNavigator.popViewWithResult(requestCode, true);
                })
                .doOnTerminate(() -> view.hideLoading())
                .doOnError(throwable -> {
                  sendFacebookSignUpErrorEvent(throwable);

                  if (throwable instanceof FacebookSignUpException
                      && ((FacebookSignUpException) throwable).getCode()
                      == FacebookSignUpException.MISSING_REQUIRED_PERMISSIONS) {
                    view.showFacebookPermissionsRequiredError(throwable);
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
        .observeOn(AndroidSchedulers.mainThread())
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
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                  sendGoogleSignUpSuccessEvent();
                  accountNavigator.popViewWithResult(requestCode, true);
                })
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

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}

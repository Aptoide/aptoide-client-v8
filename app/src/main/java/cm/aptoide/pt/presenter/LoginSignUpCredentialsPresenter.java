/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.presenter;

import android.os.Bundle;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.view.BackButton;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 06/02/17.
 */

public class LoginSignUpCredentialsPresenter implements Presenter, BackButton.ClickHandler {

  private static final String TAG = LoginSignUpCredentialsPresenter.class.getName();

  private final LoginSignUpCredentialsView view;
  private final AptoideAccountManager accountManager;
  private final LoginPreferences loginAvailability;
  private final CrashReport crashReport;

  public LoginSignUpCredentialsPresenter(LoginSignUpCredentialsView view,
      AptoideAccountManager accountManager, LoginPreferences loginAvailability,
      CrashReport crashReport) {
    this.view = view;
    this.accountManager = accountManager;
    this.loginAvailability = loginAvailability;
    this.crashReport = crashReport;
  }

  @Override public void present() {
    handleClickOnFacebookLogin();
    handleClickOnGoogleLogin();
    handleAptoideShowLoginClick();
    handleAptoideLoginClick();
    handleAptoideShowSignUpClick();
    handleAptoideSignUpClick();
    handleAccountStatusChangeWhileShowingView();
    handleForgotPasswordClick();
    handleTogglePasswordVisibility();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private void handleTogglePasswordVisibility() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(resumed -> togglePasswordVisibility())
        .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleForgotPasswordClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(resumed -> forgotPasswordSelection())
        .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleAptoideLoginClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> aptoideLoginClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleAptoideSignUpClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> aptoideSignUpClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleAptoideShowLoginClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> aptoideShowLoginClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError(err);
          crashReport.log(err);
        });
  }

  private void handleAptoideShowSignUpClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> aptoideShowSignUpClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError(err);
          crashReport.log(err);
        });
  }

  private void handleClickOnGoogleLogin() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> showOrHideGoogleLogin())
        .flatMap(__ -> view.googleLoginClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError(err);
          crashReport.log(err);
        });
  }

  private void handleClickOnFacebookLogin() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> showOrHideFacebookLogin())
        .flatMap(__ -> view.facebookLoginClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError(err);
          crashReport.log(err);
        });
  }

  private void handleAccountStatusChangeWhileShowingView() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMapSingle(__ -> accountManager.accountStatus()
            .first()
            .toSingle())
        .doOnNext(account -> {
          if (account.isLoggedIn()) {
            view.navigateBack();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  private Observable<Void> aptoideLoginClick() {
    return view.aptoideLoginClick()
        .doOnNext(__ -> {
          view.hideKeyboard();
          view.showLoading();
          lockScreenRotation();
        }).<Void>flatMapCompletable(
            credentials -> accountManager.login(Account.Type.APTOIDE, credentials.getUsername(),
                credentials.getPassword(), null)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                  Logger.d(TAG, "aptoide login successful");
                  unlockScreenRotation();
                  Analytics.Account.loginStatus(Analytics.Account.LoginMethod.APTOIDE,
                      Analytics.Account.SignUpLoginStatus.SUCCESS,
                      Analytics.Account.LoginStatusDetail.SUCCESS);
                  view.navigateToMainView();
                  view.hideLoading();
                })
                .doOnError(throwable -> {
                  view.showError(throwable);
                  view.hideLoading();
                  crashReport.log(throwable);
                  unlockScreenRotation();
                  Analytics.Account.loginStatus(Analytics.Account.LoginMethod.APTOIDE,
                      Analytics.Account.SignUpLoginStatus.FAILED,
                      Analytics.Account.LoginStatusDetail.GENERAL_ERROR);
                })).retry()
        .map(__ -> null);
  }

  private Observable<Void> aptoideSignUpClick() {
    return view.aptoideSignUpClick()
        .doOnNext(__ -> {
          view.hideKeyboard();
          view.showLoading();
          lockScreenRotation();
        })
        .flatMapCompletable(credentials -> accountManager.signUp(credentials.getUsername(),
            credentials.getPassword())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> {
              Analytics.Account.signInSuccessAptoide(Analytics.Account.SignUpLoginStatus.SUCCESS);
              view.navigateToCreateProfile();
              unlockScreenRotation();
              view.hideLoading();
            })
            .doOnError(throwable -> {
              Analytics.Account.signInSuccessAptoide(Analytics.Account.SignUpLoginStatus.FAILED);
              view.showError(throwable);
              crashReport.log(throwable);
              unlockScreenRotation();
              view.hideLoading();
            }))
        .retry()
        .map(__ -> null);
  }

  private Observable<Void> aptoideShowLoginClick() {
    return view.showAptoideLoginAreaClick()
        .doOnNext(__ -> view.showAptoideLoginArea());
  }

  private Observable<Void> aptoideShowSignUpClick() {
    return view.showAptoideSignUpAreaClick()
        .doOnNext(__ -> view.showAptoideSignUpArea());
  }

  private Observable<Void> forgotPasswordSelection() {
    return view.forgotPasswordClick()
        .doOnNext(selection -> view.showForgotPasswordView());
  }

  private Observable<Void> togglePasswordVisibility() {
    return view.showHidePasswordClick()
        .doOnNext(__ -> {
          if (view.isPasswordVisible()) {
            view.hidePassword();
          } else {
            view.showPassword();
          }
        });
  }

  private void showOrHideFacebookLogin() {
    if (loginAvailability.isFacebookLoginEnabled()) {
      view.showFacebookLogin();
    } else {
      view.hideFacebookLogin();
    }
  }

  private void showOrHideGoogleLogin() {
    if (loginAvailability.isGoogleLoginEnabled()) {
      view.showGoogleLogin();
    } else {
      view.hideGoogleLogin();
    }
  }

  @Override public boolean handle() {
    return view.tryCloseLoginBottomSheet();
  }

  private void lockScreenRotation() {
    view.lockScreenRotation();
  }

  private void unlockScreenRotation() {
    view.unlockScreenRotation();
  }
}

/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.presenter;

import android.content.Context;
import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.FacebookAccountException;
import cm.aptoide.pt.account.FacebookSignUpAdapter;
import cm.aptoide.pt.account.GoogleSignUpAdapter;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.view.BackButton;
import cm.aptoide.pt.view.account.user.ManageUserFragment;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class LoginSignUpCredentialsPresenter implements Presenter, BackButton.ClickHandler {

  private static final String TAG = LoginSignUpCredentialsPresenter.class.getName();

  private final LoginSignUpCredentialsView view;
  private final AptoideAccountManager accountManager;
  private final FragmentNavigator fragmentNavigator;
  private final CrashReport crashReport;
  private final boolean navigateToHome;
  private boolean dismissToNavigateToMainView;

  public LoginSignUpCredentialsPresenter(LoginSignUpCredentialsView view,
      AptoideAccountManager accountManager, FragmentNavigator fragmentNavigator,
      CrashReport crashReport, boolean dismissToNavigateToMainView, boolean navigateToHome) {
    this.view = view;
    this.accountManager = accountManager;
    this.fragmentNavigator = fragmentNavigator;
    this.crashReport = crashReport;
    this.dismissToNavigateToMainView = dismissToNavigateToMainView;
    this.navigateToHome = navigateToHome;
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
        .flatMap(__ -> googleLoginClick())
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
        .doOnNext(__ -> {
          Context appContext = view.getApplicationContext();
          FacebookSdk.sdkInitialize(appContext);
        })
        .doOnNext(__ -> showOrHideFacebookLogin())
        .flatMap(__ -> facebookLoginClick())
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
            navigateBack();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  private Observable<GoogleSignInResult> googleLoginClick() {
    return view.googleLoginClick()
        .doOnNext(selected -> view.showLoading()).<Void>flatMapCompletable(
            result -> accountManager.signUp(GoogleSignUpAdapter.TYPE, result)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                  Logger.d(TAG, "google login successful");
                  Analytics.Account.loginStatus(Analytics.Account.LoginMethod.GOOGLE,
                      Analytics.Account.SignUpLoginStatus.SUCCESS,
                      Analytics.Account.LoginStatusDetail.SUCCESS);
                  navigateToMainView();
                })
                .doOnTerminate(() -> view.hideLoading())
                .doOnError(throwable -> {
                  view.showError(throwable);
                  crashReport.log(throwable);
                  Analytics.Account.loginStatus(Analytics.Account.LoginMethod.GOOGLE,
                      Analytics.Account.SignUpLoginStatus.FAILED,
                      Analytics.Account.LoginStatusDetail.SDK_ERROR);
                })).retry();
  }

  private Observable<LoginResult> facebookLoginClick() {
    return view.facebookLoginClick()
        .doOnNext(selected -> view.showLoading()).<Void>flatMapCompletable(result -> {
          return accountManager.signUp(FacebookSignUpAdapter.TYPE, result)
              .observeOn(AndroidSchedulers.mainThread())
              .doOnCompleted(() -> {
                Logger.d(TAG, "facebook login successful");
                Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
                    Analytics.Account.SignUpLoginStatus.SUCCESS,
                    Analytics.Account.LoginStatusDetail.SUCCESS);
                navigateToMainView();
              })
              .doOnTerminate(() -> view.hideLoading())
              .doOnError(throwable -> {

                if (throwable instanceof FacebookAccountException) {
                  switch (((FacebookAccountException) throwable).getCode()) {
                    case FacebookAccountException.FACEBOOK_DENIED_CREDENTIALS:
                      view.showPermissionsRequiredMessage();
                      break;
                    case FacebookAccountException.FACEBOOK_API_INVALID_RESPONSE:
                      view.showFacebookLoginError();
                      break;
                    default:
                  }
                } else {
                  crashReport.log(throwable);
                  view.showError(throwable);
                }
              });
        }).retry();
  }

  private Observable<Void> aptoideLoginClick() {
    return view.aptoideLoginClick()
        .doOnNext(__ -> {
          view.hideKeyboard();
          view.showLoading();
          lockScreenRotation();
        }).<Void>flatMapCompletable(credentials -> accountManager.login(credentials)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> {
              Logger.d(TAG, "aptoide login successful");
              unlockScreenRotation();
              Analytics.Account.loginStatus(Analytics.Account.LoginMethod.APTOIDE,
                  Analytics.Account.SignUpLoginStatus.SUCCESS,
                  Analytics.Account.LoginStatusDetail.SUCCESS);
              navigateToMainView();
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
        .flatMapCompletable(credentials -> accountManager.signUp(AptoideAccountManager.APTOIDE_SIGN_UP_TYPE, credentials)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> {
              Analytics.Account.signInSuccessAptoide(Analytics.Account.SignUpLoginStatus.SUCCESS);
              navigateToCreateProfile();
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
    if (accountManager.isSignUpEnabled(FacebookSignUpAdapter.TYPE)) {
      view.showFacebookLogin();
    } else {
      view.hideFacebookLogin();
    }
  }

  private void showOrHideGoogleLogin() {
    if (accountManager.isSignUpEnabled(GoogleSignUpAdapter.TYPE)) {
      view.showGoogleLogin();
    } else {
      view.hideGoogleLogin();
    }
  }

  private void navigateToMainView() {
    if (dismissToNavigateToMainView) {
      view.dismiss();
    } else if (navigateToHome) {
      navigateToMainViewCleaningBackStack();
    } else {
      navigateBack();
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

  private void navigateToCreateProfile() {
    fragmentNavigator.cleanBackStack();
    fragmentNavigator.navigateTo(ManageUserFragment.newInstanceToCreate());
  }

  private void navigateToMainViewCleaningBackStack() {
    fragmentNavigator.navigateToHomeCleaningBackStack();
  }

  private void navigateBack() {
    fragmentNavigator.popBackStack();
  }
}

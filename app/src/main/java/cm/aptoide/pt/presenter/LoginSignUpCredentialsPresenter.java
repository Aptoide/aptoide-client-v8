/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.presenter;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.FacebookSignUpAdapter;
import cm.aptoide.pt.account.FacebookSignUpException;
import cm.aptoide.pt.account.GoogleSignUpAdapter;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.BackButton;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import java.util.Collection;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class LoginSignUpCredentialsPresenter implements Presenter, BackButton.ClickHandler {

  private static final int RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE = 2;
  private final LoginSignUpCredentialsView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final boolean navigateToHome;
  private final AccountNavigator accountNavigator;
  private final Collection<String> permissions;
  private final Collection<String> requiredPermissions;
  private final ThrowableToStringMapper errorMapper;
  private final AccountAnalytics accountAnalytics;
  private boolean dismissToNavigateToMainView;

  public LoginSignUpCredentialsPresenter(LoginSignUpCredentialsView view,
      AptoideAccountManager accountManager, CrashReport crashReport,
      boolean dismissToNavigateToMainView, boolean navigateToHome,
      AccountNavigator accountNavigator, Collection<String> permissions,
      Collection<String> requiredPermissions, ThrowableToStringMapper errorMapper,
      AccountAnalytics accountAnalytics) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.dismissToNavigateToMainView = dismissToNavigateToMainView;
    this.navigateToHome = navigateToHome;
    this.accountNavigator = accountNavigator;
    this.permissions = permissions;
    this.requiredPermissions = requiredPermissions;
    this.errorMapper = errorMapper;
    this.accountAnalytics = accountAnalytics;
  }

  @Override public void present() {

    handleAptoideLoginEvent();

    handleGoogleSignUpEvent();
    handleGoogleSignUpResult();

    handleFacebookSignUpResult();
    handleFacebookSignUpEvent();
    handleFacebookSignUpWithRequiredPermissionsEvent();

    handleAptoideShowLoginEvent();
    handleAptoideShowSignUpEvent();
    handleAptoideSignUpEvent();
    handleAccountStatusChangeWhileShowingView();
    handleForgotPasswordClick();
    handleTogglePasswordVisibility();
  }

  private void handleTogglePasswordVisibility() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> togglePasswordVisibility())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleForgotPasswordClick() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(resumed -> forgotPasswordSelection())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleAptoideLoginEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.aptoideLoginEvent()
            .doOnNext(click -> {
              view.hideKeyboard();
              view.showLoading();
              lockScreenRotation();
              accountAnalytics.sendAptoideLoginButtonPressed();
            }).<Void>flatMapCompletable(credentials -> accountManager.login(credentials)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                  unlockScreenRotation();
                  accountAnalytics.loginSuccess();
                  navigateToMainView();
                  view.hideLoading();
                })
                .doOnError(throwable -> {
                  view.showError(errorMapper.map(throwable));
                  view.hideLoading();
                  crashReport.log(throwable);
                  unlockScreenRotation();
                  accountAnalytics.sendLoginErrorEvent(AccountAnalytics.LoginMethod.APTOIDE,
                      throwable);
                })).retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleAptoideSignUpEvent() {
    view.getLifecycle()
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

  private void handleAptoideShowLoginEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> aptoideShowLoginClick())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError(errorMapper.map(err));
          crashReport.log(err);
        });
  }

  private void handleAptoideShowSignUpEvent() {
    view.getLifecycle()
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

  private void handleGoogleSignUpEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> showOrHideGoogleSignUp())
        .flatMap(__ -> view.googleSignUpEvent())
        .doOnNext(event -> {
          view.showLoading();
          accountAnalytics.sendGoogleLoginButtonPressed();
        })
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
                  accountAnalytics.loginSuccess();
                  navigateToMainView();
                })
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

  private void handleFacebookSignUpEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> showOrHideFacebookSignUp())
        .flatMap(__ -> view.facebookSignUpEvent())
        .doOnNext(event -> {
          view.showLoading();
          accountAnalytics.sendFacebookLoginButtonPressed();
          accountNavigator.navigateToFacebookSignUpForResult(permissions);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError(errorMapper.map(err));
          crashReport.log(err);
        });
  }

  private void handleFacebookSignUpWithRequiredPermissionsEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.facebookSignUpWithRequiredPermissionsInEvent())
        .doOnNext(event -> {
          view.showLoading();
          accountNavigator.navigateToFacebookSignUpForResult(requiredPermissions);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError(errorMapper.map(err));
          crashReport.log(err);
        });
  }

  private void handleFacebookSignUpResult() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> accountNavigator.facebookSignUpResults()
            .flatMapCompletable(result -> accountManager.signUp(FacebookSignUpAdapter.TYPE, result)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                  accountAnalytics.loginSuccess();
                  navigateToMainView();
                })
                .doOnTerminate(() -> view.hideLoading())
                .doOnError(throwable -> {
                  if (throwable instanceof FacebookSignUpException
                      && ((FacebookSignUpException) throwable).getCode()
                      == FacebookSignUpException.MISSING_REQUIRED_PERMISSIONS) {
                    view.showFacebookPermissionsRequiredError(throwable);
                  }
                  accountAnalytics.sendLoginErrorEvent(AccountAnalytics.LoginMethod.FACEBOOK,
                      throwable);
                  crashReport.log(throwable);
                  view.showError(errorMapper.map(throwable));
                }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private Observable<Void> aptoideShowLoginClick() {
    return view.showAptoideLoginAreaClick()
        .doOnNext(__ -> view.showAptoideLoginArea());
  }

  private Observable<Void> showAptoideSignUpEvent() {
    return view.showAptoideSignUpAreaClick()
        .doOnNext(__ -> view.showAptoideSignUpArea());
  }

  private Observable<Void> forgotPasswordSelection() {
    return view.forgotPasswordClick()
        .doOnNext(selection -> accountNavigator.navigateToRecoverPasswordView());
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

  private void showOrHideFacebookSignUp() {
    if (accountManager.isSignUpEnabled(FacebookSignUpAdapter.TYPE)) {
      view.showFacebookLogin();
    } else {
      view.hideFacebookLogin();
    }
  }

  private void showOrHideGoogleSignUp() {
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
    accountNavigator.navigateToCreateProfileView();
  }

  private void navigateToMainViewCleaningBackStack() {
    accountNavigator.navigateToHomeView();
  }

  private void navigateBack() {
    accountNavigator.popView();
  }
}

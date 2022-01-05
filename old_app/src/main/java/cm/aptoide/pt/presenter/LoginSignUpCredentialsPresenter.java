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
import cm.aptoide.pt.account.view.LoginSignUpCredentialsConfiguration;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.BackButton;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import java.util.Collection;
import rx.android.schedulers.AndroidSchedulers;

public abstract class LoginSignUpCredentialsPresenter
    implements Presenter, BackButton.ClickHandler {

  private static final int RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE = 2;
  private final LoginSignUpCredentialsView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final LoginSignUpCredentialsConfiguration configuration;
  private final AccountNavigator accountNavigator;
  private final Collection<String> permissions;
  private final ThrowableToStringMapper errorMapper;
  private final AccountAnalytics accountAnalytics;

  public LoginSignUpCredentialsPresenter(LoginSignUpCredentialsView view,
      AptoideAccountManager accountManager, CrashReport crashReport,
      LoginSignUpCredentialsConfiguration configuration, AccountNavigator accountNavigator,
      Collection<String> permissions, ThrowableToStringMapper errorMapper,
      AccountAnalytics accountAnalytics) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.configuration = configuration;
    this.accountNavigator = accountNavigator;
    this.permissions = permissions;
    this.errorMapper = errorMapper;
    this.accountAnalytics = accountAnalytics;
  }

  @Override public void present() {
    handleOpenOptions();
    handleGoogleSignUpEvent();
    handleGoogleSignUpResult();

    handleFacebookSignUpResult();
    handleFacebookSignUpEvent();
    handleFacebookSignUpWithRequiredPermissionsEvent();

    handleAccountStatusChangeWhileShowingView();
  }

  private void handleOpenOptions() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> {
          if (configuration.getHasMagicLinkError()) {
            view.showAptoideLoginArea();
            view.showMagicLinkError(configuration.getMagicLinkErrorMessage());
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  private void handleAccountStatusChangeWhileShowingView() {
    view.getLifecycleEvent()
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

  void showNotCheckedMessage(boolean checked) {
    if (!checked) {
      view.showTermsConditionError();
    }
  }

  private void handleGoogleSignUpEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> showOrHideGoogleSignUp())
        .flatMap(__ -> view.googleSignUpEvent()
            .doOnNext(this::showNotCheckedMessage)
            .filter(event -> event)
            .retry())
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
    view.getLifecycleEvent()
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
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> showOrHideFacebookSignUp())
        .flatMap(__ -> view.facebookSignUpEvent()
            .doOnNext(this::showNotCheckedMessage)
            .filter(event -> event)
            .retry())
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
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.facebookSignUpWithRequiredPermissionsInEvent())
        .doOnNext(event -> {
          view.showLoading();
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

  private void handleFacebookSignUpResult() {
    view.getLifecycleEvent()
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
    if (configuration.getDismissToNavigateToMainView()) {
      view.dismiss();
    } else if (configuration.getCleanBackStack()) {
      navigateToMainViewCleaningBackStack();
    } else {
      navigateBack();
    }
  }

  void lockScreenRotation() {
    view.lockScreenRotation();
  }

  void unlockScreenRotation() {
    view.unlockScreenRotation();
  }

  void navigateToCreateProfile() {
    accountNavigator.navigateToCreateProfileView();
  }

  private void navigateToMainViewCleaningBackStack() {
    accountNavigator.navigateToHomeView();
  }

  private void navigateBack() {
    accountNavigator.popView();
  }
}

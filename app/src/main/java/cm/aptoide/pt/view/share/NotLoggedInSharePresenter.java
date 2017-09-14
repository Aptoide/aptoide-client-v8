package cm.aptoide.pt.view.share;

import android.content.SharedPreferences;
import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.FacebookSignUpAdapter;
import cm.aptoide.pt.account.FacebookSignUpException;
import cm.aptoide.pt.account.GoogleSignUpAdapter;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.account.view.AccountNavigator;
import java.util.Collection;
import rx.android.schedulers.AndroidSchedulers;

public class NotLoggedInSharePresenter implements Presenter {

  private static final int RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE = 5;
  private final NotLoggedInShareView view;
  private final SharedPreferences sharedPreferences;
  private final CrashReport crashReport;
  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;
  private final Collection<String> permissions;
  private final Collection<String> requiredPermissions;
  private final int requestCode;

  public NotLoggedInSharePresenter(NotLoggedInShareView view, SharedPreferences sharedPreferences,
      CrashReport crashReport, AptoideAccountManager accountManager,
      AccountNavigator accountNavigator, Collection<String> permissions,
      Collection<String> requiredPermissions, int requestCode) {
    this.view = view;
    this.sharedPreferences = sharedPreferences;
    this.crashReport = crashReport;
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.permissions = permissions;
    this.requiredPermissions = requiredPermissions;
    this.requestCode = requestCode;
  }

  @Override public void present() {

    handleGoogleSignInEvent();
    handleGoogleSignInResult();

    handleFacebookSignInEvent();
    handleFacebookSignInResult();
    handleFacebookSignInWithRequiredPermissionsEvent();

    handleCloseEvent();

  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void handleCloseEvent() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.closeEvent())
        .doOnNext(__ -> accountNavigator.popNotLoggedInViewWithResult(requestCode, false))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleGoogleSignInEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> showOrHideGoogleLogin())
        .flatMap(__ -> view.googleSignUpEvent())
        .doOnNext(event -> {
          view.showLoading();
        })
        .flatMapSingle(event -> accountNavigator.navigateToGoogleSignUpForResult(
            RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE))
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
          view.showError(err);
          crashReport.log(err);
        });
  }

  private void handleGoogleSignInResult() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> accountNavigator.googleSignUpResults(RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE)
            .flatMapCompletable(result -> accountManager.signUp(GoogleSignUpAdapter.TYPE, result)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                  Analytics.Account.loginStatus(Analytics.Account.LoginMethod.GOOGLE,
                      Analytics.Account.SignUpLoginStatus.SUCCESS,
                      Analytics.Account.LoginStatusDetail.SUCCESS);
                  accountNavigator.popNotLoggedInViewWithResult(requestCode, true);
                })
                .doOnTerminate(() -> view.hideLoading())
                .doOnError(throwable -> {
                  view.showError(throwable);
                  crashReport.log(throwable);

                  Analytics.Account.loginStatus(Analytics.Account.LoginMethod.GOOGLE,
                      Analytics.Account.SignUpLoginStatus.FAILED,
                      Analytics.Account.LoginStatusDetail.SDK_ERROR);
                }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleFacebookSignInEvent() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> showOrHideFacebookLogin())
        .flatMap(__ -> view.facebookSignUpEvent())
        .doOnNext(event -> {
          view.showLoading();
          accountNavigator.navigateToFacebookSignUpForResult(permissions);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError(err);
          crashReport.log(err);
        });
  }

  private void handleFacebookSignInResult() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> accountNavigator.facebookSignUpResults()
            .flatMapCompletable(result -> accountManager.signUp(FacebookSignUpAdapter.TYPE, result)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                  Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
                      Analytics.Account.SignUpLoginStatus.SUCCESS,
                      Analytics.Account.LoginStatusDetail.SUCCESS);
                  accountNavigator.popNotLoggedInViewWithResult(requestCode, true);
                })
                .doOnTerminate(() -> view.hideLoading())
                .doOnError(throwable -> {
                  sendFacebookErrorAnalyics(throwable);

                  if (throwable instanceof FacebookSignUpException
                      && ((FacebookSignUpException) throwable).getCode()
                      == FacebookSignUpException.MISSING_REQUIRED_PERMISSIONS) {
                    view.showFacebookPermissionsRequiredError(throwable);
                  } else {
                    crashReport.log(throwable);
                    view.showError(throwable);
                  }
                }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void sendFacebookErrorAnalyics(Throwable throwable) {
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

  private void handleFacebookSignInWithRequiredPermissionsEvent() {
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
          view.showError(err);
          crashReport.log(err);
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
}

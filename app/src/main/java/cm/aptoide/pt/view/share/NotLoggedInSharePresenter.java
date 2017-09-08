package cm.aptoide.pt.view.share;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.FacebookAccountException;
import cm.aptoide.pt.account.FacebookSignUpAdapter;
import cm.aptoide.pt.account.GoogleSignUpAdapter;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class NotLoggedInSharePresenter implements Presenter {

  private final NotLoggedInShareView view;
  private final SharedPreferences sharedPreferences;
  private final CrashReport crashReport;
  private final AptoideAccountManager accountManager;

  public NotLoggedInSharePresenter(NotLoggedInShareView view, SharedPreferences sharedPreferences,
      CrashReport crashReport, AptoideAccountManager accountManager) {
    this.view = view;
    this.sharedPreferences = sharedPreferences;
    this.crashReport = crashReport;
    this.accountManager = accountManager;
  }

  @Override public void present() {

    handleClickOnFacebookLogin();
    handleClickOnGoogleLogin();

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.closeClick())
        .doOnNext(__ -> view.closeFragment())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));

  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private Observable<GoogleSignInResult> googleLoginClick() {
    return view.googleLoginClick()
        .doOnNext(selected -> view.showLoading()).<Void>flatMapCompletable(
            result -> accountManager.signUp(GoogleSignUpAdapter.TYPE, result)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                  Analytics.Account.loginStatus(Analytics.Account.LoginMethod.GOOGLE,
                      Analytics.Account.SignUpLoginStatus.SUCCESS,
                      Analytics.Account.LoginStatusDetail.SUCCESS);
                  view.navigateToMainView();
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
                Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
                    Analytics.Account.SignUpLoginStatus.SUCCESS,
                    Analytics.Account.LoginStatusDetail.SUCCESS);
                view.navigateToMainView();
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

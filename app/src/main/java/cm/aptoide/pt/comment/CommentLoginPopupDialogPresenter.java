package cm.aptoide.pt.comment;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.FacebookSignUpAdapter;
import cm.aptoide.pt.account.FacebookSignUpException;
import cm.aptoide.pt.account.GoogleSignUpAdapter;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import cm.aptoide.pt.view.share.NotLoggedInShareAnalytics;
import java.util.Collection;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tiagopedrinho on 22/11/2018.
 */

public class CommentLoginPopupDialogPresenter implements Presenter {

  private static final int RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE = 5;
  private final CommentLoginPopupDialogView view;
  private final CrashReport crashReport;
  private final CompositeSubscription subscriptions;
  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;
  private final Collection<String> permissions;
  private final Collection<String> requiredPermissions;
  private final int requestCode;
  private final ThrowableToStringMapper errorMapper;
  private final NotLoggedInShareAnalytics analytics;

  public CommentLoginPopupDialogPresenter(CommentLoginPopupDialogView view,
      CompositeSubscription subscriptions, CrashReport crashReport,
      AptoideAccountManager accountManager, AccountNavigator accountNavigator,
      Collection<String> permissions, Collection<String> requiredPermissions, int requestCode,
      ThrowableToStringMapper errorMapper, NotLoggedInShareAnalytics analytics) {
    this.view = view;
    this.subscriptions = subscriptions;
    this.crashReport = crashReport;
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.permissions = permissions;
    this.requiredPermissions = requiredPermissions;
    this.requestCode = requestCode;
    this.errorMapper = errorMapper;
    this.analytics = analytics;
  }

  @Override public void present() {
    handleGoogleSignInEvent();
    handleGoogleSignInResult();
    handleFacebookSignInEvent();
    handleFacebookSignInResult();
    handleFacebookSignInWithRequiredPermissionsEvent();
  }

  private void handleGoogleSignInEvent() {
    subscriptions.add(Observable.fromCallable(() -> {
      showOrHideGoogleLogin();
      return null;
    })
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
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError();
          crashReport.log(err);
        }));
  }

  private void handleGoogleSignInResult() {
    subscriptions.add(accountNavigator.googleSignUpResults(RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE)
        .flatMapCompletable(result -> accountManager.signUp(GoogleSignUpAdapter.TYPE, result)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> {
              analytics.loginSuccess();
              //analytics.sendGoogleLoginResultEvent(packageName, "success");
              accountNavigator.popViewWithResult(requestCode, true);
            })
            .doOnTerminate(() -> view.hideLoading())
            .doOnError(throwable -> {
              view.showError();
              crashReport.log(throwable);
              //analytics.sendGoogleLoginResultEvent(packageName, "fail");
              analytics.sendSignUpErrorEvent(AccountAnalytics.LoginMethod.GOOGLE, throwable);
            }))
        .retry()
        .subscribe());
  }

  private void handleFacebookSignInEvent() {
    subscriptions.add(Observable.fromCallable(() -> {
      showOrHideFacebookLogin();
      return null;
    })
        .flatMap(__ -> view.facebookSignUpEvent())
        .doOnNext(event -> {
          view.showLoading();
          accountNavigator.navigateToFacebookSignUpForResult(permissions);
        })
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError();
          crashReport.log(err);
        }));
  }

  private void handleFacebookSignInResult() {
    subscriptions.add(accountNavigator.facebookSignUpResults()
        .flatMapCompletable(result -> accountManager.signUp(FacebookSignUpAdapter.TYPE, result)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> {
              analytics.loginSuccess();
              //analytics.sendFacebookLoginButtonPressed(packageName, "success");
              accountNavigator.popViewWithResult(requestCode, true);
            })
            .doOnTerminate(() -> view.hideLoading())
            .doOnError(throwable -> {
              //analytics.sendFacebookLoginButtonPressed(packageName, "fail");
              if (throwable instanceof FacebookSignUpException
                  && ((FacebookSignUpException) throwable).getCode()
                  == FacebookSignUpException.MISSING_REQUIRED_PERMISSIONS) {
                view.showFacebookPermissionsRequiredError(throwable);
              } else {
                crashReport.log(throwable);
                view.showError();
              }
              analytics.sendSignUpErrorEvent(AccountAnalytics.LoginMethod.FACEBOOK, throwable);
            }))
        .retry()
        .subscribe());
  }

  private void handleFacebookSignInWithRequiredPermissionsEvent() {
    subscriptions.add(view.facebookSignUpWithRequiredPermissionsInEvent()
        .doOnNext(event -> {
          view.showLoading();
          accountNavigator.navigateToFacebookSignUpForResult(requiredPermissions);
        })
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError();
          //crashReport.log(err);
        }));
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

  void dispose() {
    subscriptions.clear();
  }

}

/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.content.Context;
import android.os.Bundle;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.account.LoginPreferences;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.BackButton;
import cm.aptoide.pt.v8engine.view.account.user.ManageUserFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import java.util.Collection;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by marcelobenites on 06/02/17.
 */

public class LoginSignUpCredentialsPresenter implements Presenter, BackButton.ClickHandler {

  private static final String TAG = LoginSignUpCredentialsPresenter.class.getName();

  private final LoginSignUpCredentialsView view;
  private final AptoideAccountManager accountManager;
  private final Collection<String> facebookRequiredPermissions;
  private final LoginPreferences loginAvailability;
  private final FragmentNavigator fragmentNavigator;
  private final CrashReport crashReport;
  private final boolean navigateToHome;
  private boolean dismissToNavigateToMainView;

  public LoginSignUpCredentialsPresenter(LoginSignUpCredentialsView view,
      AptoideAccountManager accountManager, Collection<String> facebookRequiredPermissions,
      LoginPreferences loginAvailability, FragmentNavigator fragmentNavigator,
      CrashReport crashReport, boolean dismissToNavigateToMainView, boolean navigateToHome) {
    this.view = view;
    this.accountManager = accountManager;
    this.facebookRequiredPermissions = facebookRequiredPermissions;
    this.loginAvailability = loginAvailability;
    this.fragmentNavigator = fragmentNavigator;
    this.crashReport = crashReport;
    this.dismissToNavigateToMainView = dismissToNavigateToMainView;
    this.navigateToHome = navigateToHome;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> {
          Context appContext = view.getApplicationContext();
          FacebookSdk.sdkInitialize(appContext);
        })
        .doOnNext(__ -> showOrHideLogin())
        .flatMap(
            __ -> Observable.merge(googleLoginClick(), facebookLoginClick(), aptoideLoginClick(),
                aptoideSignUpClick(), aptoideShowLoginClick(), aptoideShowSignUpClick()))
        .retry()
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(resumed -> Observable.merge(forgotPasswordSelection(), togglePasswordVisibility()))
        .retry()
        .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));

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

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private void showOrHideLogin() {
    showOrHideFacebookLogin();
    showOrHideGoogleLogin();
  }

  private Observable<Void> googleLoginClick() {
    return view.googleLoginClick()
        .doOnNext(selected -> view.showLoading()).<Void>flatMap(
            credentials -> accountManager.login(Account.Type.GOOGLE, credentials.getEmail(),
                credentials.getToken(), credentials.getDisplayName())
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
                })
                .toObservable()).retry();
  }

  private Observable<Void> facebookLoginClick() {
    return view.facebookLoginClick()
        .doOnNext(selected -> view.showLoading()).<Void>flatMap(credentials -> {
          if (declinedRequiredPermissions(credentials.getDeniedPermissions())) {
            view.hideLoading();
            view.showPermissionsRequiredMessage();
            Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
                Analytics.Account.SignUpLoginStatus.FAILED,
                Analytics.Account.LoginStatusDetail.PERMISSIONS_DENIED);
            return Observable.empty();
          }

          return getFacebookUsername(credentials.getToken()).flatMapCompletable(
              username -> accountManager.login(Account.Type.FACEBOOK, username,
                  credentials.getToken()
                      .getToken(), null)
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
                    crashReport.log(throwable);
                    view.showError(throwable);
                  }))
              .toObservable();
        }).retry();
  }

  private Observable<Void> aptoideLoginClick() {
    return view.aptoideLoginClick().<Void>flatMap(credentials -> {
      view.hideKeyboard();
      view.showLoading();
      lockScreenRotation();
      return accountManager.login(Account.Type.APTOIDE, credentials.getUsername(),
          credentials.getPassword(), null)
          .observeOn(AndroidSchedulers.mainThread())
          .doOnCompleted(() -> {
            Logger.d(TAG, "aptoide login successful");
            unlockScreenRotation();
            Analytics.Account.loginStatus(Analytics.Account.LoginMethod.APTOIDE,
                Analytics.Account.SignUpLoginStatus.SUCCESS,
                Analytics.Account.LoginStatusDetail.SUCCESS);
            navigateToMainView();
          })
          .doOnTerminate(() -> view.hideLoading())
          .doOnError(throwable -> {
            view.showError(throwable);
            crashReport.log(throwable);
            unlockScreenRotation();
            Analytics.Account.loginStatus(Analytics.Account.LoginMethod.APTOIDE,
                Analytics.Account.SignUpLoginStatus.FAILED,
                Analytics.Account.LoginStatusDetail.GENERAL_ERROR);
          })
          .toObservable();
    }).retry();
  }

  private Observable<Void> aptoideSignUpClick() {
    return view.aptoideSignUpClick().<Void>flatMap(credentials -> {
      view.hideKeyboard();
      view.showLoading();
      lockScreenRotation();

      final Completable signUp =
          accountManager.signUp(credentials.getUsername(), credentials.getPassword());

      return signUp.observeOn(AndroidSchedulers.mainThread())
          .doOnCompleted(() -> {
            Logger.d(TAG, "aptoide sign up successful");
            Analytics.Account.signInSuccessAptoide(Analytics.Account.SignUpLoginStatus.SUCCESS);
            navigateToCreateProfile();
            unlockScreenRotation();
          })
          .doOnTerminate(() -> view.hideLoading())
          .doOnError(throwable -> {
            Analytics.Account.signInSuccessAptoide(Analytics.Account.SignUpLoginStatus.FAILED);
            view.showError(throwable);
            crashReport.log(throwable);
            unlockScreenRotation();
          })
          .toObservable();
    }).retry();
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

  private void navigateToMainView() {
    if (dismissToNavigateToMainView) {
      view.dismiss();
    } else if (navigateToHome) {
      navigateToMainViewCleaningBackStack();
    } else {
      navigateBack();
    }
  }

  private boolean declinedRequiredPermissions(Set<String> declinedPermissions) {
    return declinedPermissions.containsAll(facebookRequiredPermissions);
  }

  private Single<String> getFacebookUsername(AccessToken accessToken) {
    return Single.create(singleSubscriber -> {
      final GraphRequest request =
          GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override public void onCompleted(JSONObject object, GraphResponse response) {
              if (!singleSubscriber.isUnsubscribed()) {
                if (response.getError() == null) {
                  String email = null;
                  try {
                    email =
                        object.has("email") ? object.getString("email") : object.getString("id");
                  } catch (JSONException e) {
                    singleSubscriber.onError(e);
                  }
                  singleSubscriber.onSuccess(email);
                } else {
                  singleSubscriber.onError(response.getError()
                      .getException());
                }
              }
            }
          });
      singleSubscriber.add(Subscriptions.create(() -> request.setCallback(null)));
      request.executeAsync();
    });
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
    fragmentNavigator.navigateToWithoutBackSave(ManageUserFragment.newInstanceToCreate());
  }

  private void navigateToMainViewCleaningBackStack() {
    fragmentNavigator.navigateToHomeCleaningBackStack();
  }

  private void navigateBack() {
    fragmentNavigator.popBackStack();
  }
}

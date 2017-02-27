/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import android.text.TextUtils;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.view.LoginSignUpView;
import cm.aptoide.pt.v8engine.view.View;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import java.util.Collection;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by marcelobenites on 06/02/17.
 */

public class LoginSignUpCredentialsPresenter implements Presenter {

  private final LoginSignUpView view;
  private final AptoideAccountManager accountManager;
  private final Collection<String> facebookRequiredPermissions;
  private boolean dimissToNavigateToMainView;

  public LoginSignUpCredentialsPresenter(LoginSignUpView view, AptoideAccountManager accountManager,
      Collection<String> facebookRequiredPermissions, boolean dimissToNavigateToMainView) {
    this.view = view;
    this.accountManager = accountManager;
    this.facebookRequiredPermissions = facebookRequiredPermissions;
    this.dimissToNavigateToMainView = dimissToNavigateToMainView;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> showOrHideLogins())
        .flatMap(resumed -> Observable.merge(googleLoginClick(), facebookLoginClick(),
            aptoideLoginClick(), aptoideSignUpClick(), aptoideShowLoginClick(),
            aptoideShowSignUpClick()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(resumed -> Observable.merge(forgotPasswordSelection(), showHidePassword())
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void showOrHideLogins() {
    showOrHideFacebookLogin();
    showOrHideGoogleLogin();
  }

  private Observable<Void> googleLoginClick() {
    return view.googleLoginClick().doOnNext(selected -> view.showLoading()).<Void>flatMap(
        credentials -> accountManager.login(Account.Type.GOOGLE, credentials.getEmail(),
            credentials.getToken(), credentials.getDisplayName())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> navigateToMainView())
            .doOnTerminate(() -> view.hideLoading())
            .doOnError(throwable -> view.showError(throwable))
            .toObservable()).retry();
  }

  private void navigateToMainView() {
    if (dimissToNavigateToMainView) {
      view.dismiss();
    } else {
      view.navigateToMainView();
    }
  }

  private Observable<Void> facebookLoginClick() {
    return view.facebookLoginClick().doOnNext(selected -> view.showLoading()).<Void>flatMap(
        credentials -> {
          if (declinedRequiredPermissions(credentials.getDeniedPermissions())) {
            view.hideLoading();
            view.showPermissionsRequiredMessage();
            return Observable.empty();
          }

          return getFacebookUsername(credentials.getToken()).flatMapCompletable(
              username -> accountManager.login(Account.Type.FACEBOOK, username,
                  credentials.getToken().getToken(), null)
                  .observeOn(AndroidSchedulers.mainThread())
                  .doOnCompleted(() -> navigateToMainView())
                  .doOnTerminate(() -> view.hideLoading())
                  .doOnError(throwable -> view.showError(throwable))).toObservable();
        }).retry();
  }

  private Observable<Void> aptoideLoginClick() {
    return view.aptoideLoginClick().doOnNext(selected -> {
      view.hideKeyboard();
      view.showLoading();
    }).<Void>flatMap(
        credentials -> {
          if (TextUtils.isEmpty(credentials.getPassword()) || TextUtils.isEmpty(
              credentials.getUsername())) {
            view.showCheckAptoideCredentialsMessage();
            return Observable.empty();
          }
          return accountManager.login(Account.Type.APTOIDE, credentials.getUsername(),
              credentials.getPassword(), null)
              .observeOn(AndroidSchedulers.mainThread())
              .doOnCompleted(() -> navigateToMainView())
              .doOnTerminate(() -> view.hideLoading())
              .doOnError(throwable -> view.showError(throwable))
              .toObservable();
        }).retry();
  }

  private Observable<Void> aptoideSignUpClick() {
    return view.aptoideSignUpClick().doOnNext(__ -> {
      view.hideKeyboard();
      view.showLoading();
    }).<Void>flatMap(
        credentials -> {
          if (TextUtils.isEmpty(credentials.getPassword()) || TextUtils.isEmpty(
              credentials.getUsername())) {
            view.showCheckAptoideCredentialsMessage();
            return Observable.empty();
          }
          return accountManager.createAccount(credentials.getUsername(), credentials.getPassword())
              .observeOn(AndroidSchedulers.mainThread())
              .doOnCompleted(() -> view.navigateToCreateProfile())
              .doOnTerminate(() -> view.hideLoading())
              .doOnError(throwable -> view.showError(throwable))
              .toObservable();
        }).retry();
  }

  private Observable<Void> aptoideShowLoginClick() {
    return view.showAptoideLoginAreaClick().doOnNext(__ -> view.showAptoideLoginArea());
  }

  private Observable<Void> aptoideShowSignUpClick() {
    return view.showAptoideSignUpAreaClick().doOnNext(__ -> view.showAptoideSignUpArea());
  }

  private Observable<Void> forgotPasswordSelection() {
    return view.forgotPasswordClick().doOnNext(selection -> view.navigateToForgotPasswordView());
  }

  private Observable<Void> showHidePassword() {
    return view.showHidePasswordClick().doOnNext(__ -> {
      if (view.isPasswordVisible()) {
        view.hidePassword();
      } else {
        view.showPassword();
      }
    });
  }

  private void showOrHideFacebookLogin() {
    if (accountManager.isFacebookLoginEnabled()) {
      view.showFacebookLogin();
    } else {
      view.hideFacebookLogin();
    }
  }

  private void showOrHideGoogleLogin() {
    if (accountManager.isGoogleLoginEnabled()) {
      view.showGoogleLogin();
    } else {
      view.hideGoogleLogin();
    }
  }

  private boolean declinedRequiredPermissions(Set<String> declinedPermissions) {
    return declinedPermissions.containsAll(facebookRequiredPermissions);
  }

  private Single<String> getFacebookUsername(AccessToken accessToken) {
    return Single.create(new Single.OnSubscribe<String>() {
      @Override public void call(SingleSubscriber<? super String> singleSubscriber) {
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
                    singleSubscriber.onError(response.getError().getException());
                  }
                }
              }
            });
        singleSubscriber.add(Subscriptions.create(() -> request.setCallback(null)));
        request.executeAsync();
      }
    });
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }
}

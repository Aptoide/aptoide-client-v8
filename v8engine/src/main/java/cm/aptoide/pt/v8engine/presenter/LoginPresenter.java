/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.pt.v8engine.view.LoginView;
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

public class LoginPresenter implements Presenter {

  private final LoginView view;
  private final AptoideAccountManager accountManager;
  private final Collection<String> facebookRequiredPermissions;

  public LoginPresenter(LoginView view, AptoideAccountManager accountManager,
      Collection<String> facebookRequiredPermissions) {
    this.view = view;
    this.accountManager = accountManager;
    this.facebookRequiredPermissions = facebookRequiredPermissions;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> showOrHideLogins())
        .flatMap(resumed -> Observable.merge(googleLoginSelection(), facebookLoginSelection(),
            aptoideLoginClick()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(resumed -> Observable.merge(forgotPasswordSelection(), successMessageShown(),
            showSignUpClick(), showLoginClick(), showHidePassword())
            .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private Observable<Void> showHidePassword() {
    return view.showHidePasswordClick().doOnNext(__ -> {
      // TODO: 13/2/2017 sithengineer missing view model to support this...
    });
  }

  private Observable<Void> showSignUpClick() {
    return view.showSignUpClick().doOnNext(selected -> view.showSignUpArea());
  }

  private Observable<Void> showLoginClick() {
    return view.showAptoideLoginClick().doOnNext(selected -> view.showLoginArea());
  }

  private Observable<Void> successMessageShown() {
    return view.successMessageShown().doOnNext(selection -> view.navigateToMainView());
  }

  private Observable<Void> forgotPasswordSelection() {
    return view.forgotPasswordClick().doOnNext(selection -> view.navigateToForgotPasswordView());
  }

  private void showOrHideLogins() {
    showOrHideFacebookLogin();
    showOrHideGoogleLogin();
  }

  private void showOrHideFacebookLogin() {
    if (accountManager.isFacebookLoginEnabled()) {
      view.showFacebookLogin();
    } else {
      view.hideFacebookLogin();
    }
  }

  private Observable<Void> googleLoginSelection() {
    return view.googleLoginClick().doOnNext(selected -> view.showLoading()).<Void>flatMap(
        credentials -> accountManager.login(LoginMode.GOOGLE, credentials.getEmail(),
            credentials.getToken(), credentials.getDisplayName())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> view.showSuccessMessage())
            .doOnTerminate(() -> view.hideLoading())
            .doOnError(throwable -> view.showError(throwable))
            .toObservable()).retry();
  }

  private Observable<Void> facebookLoginSelection() {
    return view.facebookLoginClick().doOnNext(selected -> view.showLoading()).<Void>flatMap(
        credentials -> {
          if (declinedRequiredPermissions(credentials.getDeniedPermissions())) {
            view.hideLoading();
            view.showPermissionsRequiredMessage();
            return Observable.empty();
          }

          return getFacebookUsername(credentials.getToken()).flatMapCompletable(
              username -> accountManager.login(LoginMode.FACEBOOK, username,
                  credentials.getToken().getToken(), null)
                  .observeOn(AndroidSchedulers.mainThread())
                  .doOnCompleted(() -> view.showSuccessMessage())
                  .doOnTerminate(() -> view.hideLoading())
                  .doOnError(throwable -> view.showError(throwable))).toObservable();
        }).retry();
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

  private boolean declinedRequiredPermissions(Set<String> declinedPermissions) {
    return declinedPermissions.containsAll(facebookRequiredPermissions);
  }

  private void showOrHideGoogleLogin() {
    if (accountManager.isGoogleLoginEnabled()) {
      view.showGoogleLogin();
    } else {
      view.hideGoogleLogin();
    }
  }

  private Observable<Void> aptoideLoginClick() {
    return view.aptoideLoginClick().doOnNext(selected -> view.showLoading()).<Void>flatMap(
        credentials -> {
          if (TextUtils.isEmpty(credentials.getPassword()) || TextUtils.isEmpty(
              credentials.getUsername())) {
            view.showCheckAptoideCredentialsMessage();
            return Observable.empty();
          }
          return accountManager.login(LoginMode.APTOIDE, credentials.getUsername(),
              credentials.getPassword(), null)
              .observeOn(AndroidSchedulers.mainThread())
              .doOnCompleted(() -> view.showSuccessMessage())
              .doOnTerminate(() -> view.hideLoading())
              .doOnError(throwable -> view.showError(throwable))
              .toObservable();
        }).retry();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}

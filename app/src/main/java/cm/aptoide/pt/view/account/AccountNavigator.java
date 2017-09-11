/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.view.account;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.FacebookLoginResult;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.view.navigator.ActivityNavigator;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Collection;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class AccountNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final AptoideAccountManager accountManager;
  private final ActivityNavigator activityNavigator;
  private final LoginManager facebookLoginManager;
  private final CallbackManager callbackManager;
  private final GoogleApiClient client;
  private PublishRelay<FacebookLoginResult> facebookLoginSubject;

  public AccountNavigator(FragmentNavigator fragmentNavigator, AptoideAccountManager accountManager,
      ActivityNavigator activityNavigator, LoginManager facebookLoginManager,
      CallbackManager callbackManager, GoogleApiClient client,
      PublishRelay<FacebookLoginResult> facebookLoginSubject) {
    this.fragmentNavigator = fragmentNavigator;
    this.accountManager = accountManager;
    this.activityNavigator = activityNavigator;
    this.facebookLoginManager = facebookLoginManager;
    this.callbackManager = callbackManager;
    this.client = client;
    this.facebookLoginSubject = facebookLoginSubject;
  }

  public void navigateToAccountView(Analytics.Account.AccountOrigins accountOrigins) {
    if (accountManager.isLoggedIn()) {
      fragmentNavigator.navigateTo(MyAccountFragment.newInstance());
    } else {
      Analytics.Account.enterAccountScreen(accountOrigins);
      fragmentNavigator.navigateTo(LoginSignUpFragment.newInstance(false, false, false));
    }
  }

  public Single<ConnectionResult> navigateToGoogleSignInForResult(int requestCode) {
    return Single.fromCallable(() -> {
      final ConnectionResult connectionResult = client.blockingConnect();
      if (connectionResult.isSuccess()) {
        activityNavigator.navigateForResult(Auth.GoogleSignInApi.getSignInIntent(client),
            requestCode);
      }
      return connectionResult;
    })
        .subscribeOn(Schedulers.io());
  }

  public Observable<GoogleSignInResult> googleSignInResults(int requestCode) {
    return activityNavigator.results(requestCode)
        .map(result -> Auth.GoogleSignInApi.getSignInResultFromIntent(result.getData()))
        .doOnNext(result -> client.disconnect());
  }

  public void navigateToFacebookSignInForResult(Collection<String> permissions) {
    facebookLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        facebookLoginSubject.call(
            new FacebookLoginResult(loginResult, FacebookLoginResult.STATE_SUCCESS, null));
      }

      @Override public void onCancel() {
        facebookLoginSubject.call(
            new FacebookLoginResult(null, FacebookLoginResult.STATE_CANCELLED, null));
      }

      @Override public void onError(FacebookException error) {
        facebookLoginSubject.call(
            new FacebookLoginResult(null, FacebookLoginResult.STATE_ERROR, error));
      }
    });
    facebookLoginManager.logInWithReadPermissions(activityNavigator.getActivity(), permissions);
  }

  public Observable<FacebookLoginResult> facebookSignInResults() {
    return Observable.combineLatest(activityNavigator.results()
            .filter(result -> callbackManager.onActivityResult(result.getRequestCode(),
                result.getResultCode(), result.getData())), facebookLoginSubject,
        (result, loginResult) -> loginResult);
  }
}

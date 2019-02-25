/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.account.view;

import android.app.Activity;
import android.net.Uri;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.FacebookLoginResult;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.store.ManageStoreViewModel;
import cm.aptoide.pt.account.view.user.ManageUserFragment;
import cm.aptoide.pt.account.view.user.ProfileStepTwoFragment;
import cm.aptoide.pt.bottomNavigation.BottomNavigationNavigator;
import cm.aptoide.pt.link.CustomTabsHelper;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.view.settings.MyAccountFragment;
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

  private final BottomNavigationNavigator bottomNavigationNavigator;
  private final FragmentNavigator fragmentNavigator;
  private final AptoideAccountManager accountManager;
  private final ActivityNavigator activityNavigator;
  private final LoginManager facebookLoginManager;
  private final CallbackManager callbackManager;
  private final GoogleApiClient client;
  private final PublishRelay<FacebookLoginResult> facebookLoginSubject;
  private final String recoverPasswordUrl;
  private final AccountAnalytics accountAnalytics;
  private final String theme;

  public AccountNavigator(BottomNavigationNavigator bottomNavigationNavigator,
      FragmentNavigator fragmentNavigator, AptoideAccountManager accountManager,
      ActivityNavigator activityNavigator, LoginManager facebookLoginManager,
      CallbackManager callbackManager, GoogleApiClient client,
      PublishRelay<FacebookLoginResult> facebookLoginSubject, String recoverPasswordUrl,
      AccountAnalytics accountAnalytics, String theme) {
    this.bottomNavigationNavigator = bottomNavigationNavigator;
    this.fragmentNavigator = fragmentNavigator;
    this.accountManager = accountManager;
    this.activityNavigator = activityNavigator;
    this.facebookLoginManager = facebookLoginManager;
    this.callbackManager = callbackManager;
    this.client = client;
    this.facebookLoginSubject = facebookLoginSubject;
    this.recoverPasswordUrl = recoverPasswordUrl;
    this.accountAnalytics = accountAnalytics;
    this.theme = theme;
  }

  public void navigateToRecoverPasswordView() {
    activityNavigator.navigateTo(Uri.parse(recoverPasswordUrl));
  }

  public void navigateToAccountView(AccountAnalytics.AccountOrigins accountOrigins) {
    if (accountManager.isLoggedIn()) {
      fragmentNavigator.navigateTo(MyAccountFragment.newInstance(), true);
    } else {
      accountAnalytics.enterAccountScreen(accountOrigins);
      fragmentNavigator.navigateTo(LoginSignUpFragment.newInstance(false, false, false, false),
          true);
    }
  }

  public Single<ConnectionResult> navigateToGoogleSignUpForResult(int requestCode) {
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

  public Observable<GoogleSignInResult> googleSignUpResults(int requestCode) {
    return activityNavigator.results(requestCode)
        .map(result -> Auth.GoogleSignInApi.getSignInResultFromIntent(result.getData()))
        .doOnNext(result -> client.disconnect());
  }

  public void navigateToFacebookSignUpForResult(Collection<String> permissions) {
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

  public Observable<FacebookLoginResult> facebookSignUpResults() {
    return Observable.combineLatest(activityNavigator.results()
            .filter(result -> callbackManager.onActivityResult(result.getRequestCode(),
                result.getResultCode(), result.getData())), facebookLoginSubject,
        (result, loginResult) -> loginResult);
  }

  public void popViewWithResult(int requestCode, boolean success) {
    fragmentNavigator.popWithResult(
        new Result(requestCode, (success ? Activity.RESULT_OK : Activity.RESULT_CANCELED), null));
  }

  public void navigateToHomeView() {
    bottomNavigationNavigator.navigateToHome();
  }

  public void popView() {
    fragmentNavigator.popBackStack();
  }

  public void navigateToCreateProfileView() {
    fragmentNavigator.navigateToCleaningBackStack(ManageUserFragment.newInstanceToCreate(), true);
  }

  public void navigateToProfileStepTwoView() {
    fragmentNavigator.navigateToCleaningBackStack(ProfileStepTwoFragment.newInstance(), true);
  }

  public void navigateToCreateStoreView() {
    fragmentNavigator.navigateToCleaningBackStack(
        ManageStoreFragment.newInstance(new ManageStoreViewModel(), true), true);
  }

  public void navigateToTermsAndConditions() {
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab(activityNavigator.getActivity()
            .getString(R.string.all_url_terms_conditions), activityNavigator.getActivity(), theme);
  }

  public void navigateToPrivacyPolicy() {
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab(activityNavigator.getActivity()
            .getString(R.string.all_url_privacy_policy), activityNavigator.getActivity(), theme);
  }
}

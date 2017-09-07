/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 08/02/2017.
 */

package cm.aptoide.pt.view.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.presenter.SocialLoginView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.Arrays;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 08/02/17.
 */

public abstract class SocialLoginFragment extends GooglePlayServicesFragment
    implements SocialLoginView {

  private static final int RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE = 2;
  protected LoginManager facebookLoginManager;
  protected List<String> facebookRequestedPermissions;
  protected PublishRelay<FacebookAccountViewModel> facebookLoginSubject;
  private PublishRelay<GoogleAccountViewModel> googleLoginSubject;
  private GoogleApiClient client;
  private AccountErrorMapper errorMapper;
  private CallbackManager callbackManager;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    errorMapper = new AccountErrorMapper(getContext());
    facebookLoginManager = LoginManager.getInstance();
    facebookRequestedPermissions = Arrays.asList("email", "user_friends");
    callbackManager = CallbackManager.Factory.create();
  }

  @Override public void onResume() {
    super.onResume();
    getGoogleButton().setOnClickListener(v -> {
      googleLoginClicked();
      connect();
      startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(client),
          RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE);
    });
  }

  @Override public void onPause() {
    super.onPause();
    getGoogleButton().setOnClickListener(null);
  }

  public void googleLoginClicked() {
    // does nothing
  }

  protected abstract Button getGoogleButton();

  protected abstract Button getFacebookButton();

  @Override public void showGoogleLogin() {
    getGoogleButton().setVisibility(View.VISIBLE);
  }

  @Override public void hideGoogleLogin() {
    getGoogleButton().setVisibility(View.GONE);
  }

  public void showError(Throwable throwable) {
    Snackbar.make(getRootView(), errorMapper.map(throwable), Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public Observable<GoogleAccountViewModel> googleLoginClick() {
    return googleLoginSubject;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE) {
      final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      final GoogleSignInAccount account = result.getSignInAccount();
      if (result.isSuccess() && account != null) {
        googleLoginSubject.call(
            new GoogleAccountViewModel(account.getDisplayName(), account.getServerAuthCode(),
                account.getEmail()));
      } else {
        showGoogleLoginError();
      }
    } else {
      callbackManager.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    googleLoginSubject = PublishRelay.create();
    client = ((AptoideApplication) getContext().getApplicationContext()).getGoogleSignInClient();
    bindViews(view);
  }

  @Override protected void connect() {
    client.connect();
  }

  protected void bindViews(View view) {
    RxView.clicks(getFacebookButton())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> facebookLoginManager.logInWithReadPermissions(SocialLoginFragment.this,
            facebookRequestedPermissions));
  }

  private View getRootView() {
    return getActivity().findViewById(android.R.id.content);
  }

  protected void showFacebookLoginError(@StringRes int errorRes) {
    Snackbar.make(getRootView(), errorRes, Snackbar.LENGTH_LONG)
        .show();
  }

  protected void showGoogleLoginError() {
    Snackbar.make(getRootView(), R.string.google_login_cancelled, Snackbar.LENGTH_LONG)
        .show();
  }

  protected void registerFacebookCallback() {
    facebookLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        facebookLoginSubject.call(new FacebookAccountViewModel(loginResult.getAccessToken(),
            loginResult.getRecentlyDeniedPermissions()));
      }

      @Override public void onCancel() {
        showFacebookLoginError(R.string.facebook_login_cancelled);
        Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
            Analytics.Account.SignUpLoginStatus.FAILED, Analytics.Account.LoginStatusDetail.CANCEL);
      }

      @Override public void onError(FacebookException error) {
        Analytics.Account.loginStatus(Analytics.Account.LoginMethod.FACEBOOK,
            Analytics.Account.SignUpLoginStatus.FAILED,
            Analytics.Account.LoginStatusDetail.SDK_ERROR);
        showFacebookLoginError(R.string.error_occured);
      }
    });
  }
}

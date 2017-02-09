/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.AptoideWsV3Exception;
import cm.aptoide.accountmanager.ws.ErrorsMapper;
import cm.aptoide.accountmanager.ws.responses.GenericResponseV3;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.presenter.LoginPresenter;
import cm.aptoide.pt.v8engine.view.LoginView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Arrays;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 06/02/17.
 */
public class LoginActivity extends GoogleLoginActivity implements LoginView {

  private ProgressDialog progressDialog;
  private View content;
  private SignInButton googleLoginButton;
  private LoginButton facebookLoginButton;

  private CallbackManager callbackManager;
  private LoginManager facebookLoginManager;

  private PublishRelay<FacebookAccountViewModel> facebookLoginSubject;
  private AlertDialog facebookEmailRequiredDialog;
  private List<String> facebookRequiredPermissions;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_activity_layout);

    facebookRequiredPermissions = Arrays.asList("email");
    content = findViewById(android.R.id.content);
    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(this);

    googleLoginButton =
        (SignInButton) findViewById(cm.aptoide.accountmanager.R.id.g_sign_in_button);

    facebookLoginButton = (LoginButton) findViewById(cm.aptoide.accountmanager.R.id.fb_login_button);
    callbackManager = CallbackManager.Factory.create();
    facebookLoginManager = LoginManager.getInstance();
    facebookLoginSubject = PublishRelay.create();
    facebookEmailRequiredDialog = new AlertDialog.Builder(this)
        .setMessage(cm.aptoide.accountmanager.R.string.facebook_email_permission_regected_message)
        .setPositiveButton(cm.aptoide.accountmanager.R.string.facebook_grant_permission_button, (dialog, which) -> {
          facebookLoginManager.logInWithReadPermissions(LoginActivity.this,
              facebookRequiredPermissions);
        })
        .setNegativeButton(android.R.string.cancel, null)
        .create();

    final AptoideAccountManager accountManager = AptoideAccountManager.getInstance(
        this,
        Application.getConfiguration(),
        new SecureCoderDecoder.Builder(this).create(),
        AccountManager.get(this),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), this));

    attachPresenter(
        new LoginPresenter(this, accountManager, facebookRequiredPermissions),
        savedInstanceState);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @Override protected void onDestroy() {
    super.onDestroy();

  }

  @Override protected SignInButton getGoogleButton() {
    return googleLoginButton;
  }

  @Override protected void showGoogleLoginError() {
    ShowMessage.asSnack(content, cm.aptoide.accountmanager.R.string.unknown_error);
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public void showError(Throwable throwable) {
    final String message;
    if (throwable instanceof AptoideWsV3Exception) {
      final GenericResponseV3 oAuth = ((AptoideWsV3Exception) throwable).getBaseResponse();
      message = getString(ErrorsMapper.getWebServiceErrorMessageFromCode(oAuth.getError()));
    } else {
      message = getString(cm.aptoide.accountmanager.R.string.unknown_error);
    }
    ShowMessage.asSnack(content, message);
  }

  @Override public void showFacebookLogin() {
    FacebookSdk.sdkInitialize(getApplicationContext());
    facebookLoginButton.setReadPermissions(facebookRequiredPermissions);
    facebookLoginButton.setVisibility(View.VISIBLE);
    facebookLoginManager
        .registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
          @Override public void onSuccess(LoginResult loginResult) {
            facebookLoginSubject.call(new FacebookAccountViewModel(loginResult.getAccessToken(), loginResult.getRecentlyDeniedPermissions(), loginResult.getRecentlyGrantedPermissions()));
          }

          @Override public void onCancel() {
            showFacebookLoginError(cm.aptoide.accountmanager.R.string.unknown_error);
          }

          @Override public void onError(FacebookException error) {
            showFacebookLoginError(cm.aptoide.accountmanager.R.string.error_occured);
          }
        });
  }

  @Override public void hideFacebookLogin() {
    facebookLoginButton.setVisibility(View.GONE);
  }

  @Override public Observable<FacebookAccountViewModel> facebookLoginSelection() {
    return facebookLoginSubject;
  }

  @Override public void showPermissionsRequiredMessage() {
    facebookEmailRequiredDialog.show();
  }

  private void showFacebookLoginError(@StringRes int errorRes) {
    ShowMessage.asSnack(content, errorRes);
  }
}
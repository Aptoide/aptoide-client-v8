/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.AptoideWsV3Exception;
import cm.aptoide.accountmanager.ws.ErrorsMapper;
import cm.aptoide.accountmanager.ws.responses.GenericResponseV3;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
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
import com.jakewharton.rxbinding.support.design.widget.RxSnackbar;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Arrays;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 06/02/17.
 */
public class LoginActivity extends GoogleLoginActivity implements LoginView {

  public static String SKIP_BUTTON = "skip_button";
  private ProgressDialog progressDialog;
  private View content;
  private SignInButton googleLoginButton;
  private LoginButton facebookLoginButton;

  private CallbackManager callbackManager;
  private LoginManager facebookLoginManager;

  private PublishRelay<FacebookAccountViewModel> facebookLoginSubject;
  private PublishRelay<Void> skipSubject;
  private AlertDialog facebookEmailRequiredDialog;
  private List<String> facebookRequestedPermissions;
  private Button aptoideLoginButton;
  private EditText aptoideEmailEditText;
  private EditText aptoidePasswordEditText;
  private Button hideShowAptoidePasswordButton;
  private TextView forgotPasswordButton;
  private TextView orMessage;
  private boolean skipButton;
  private Snackbar successSnackbar;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_activity_layout);

    facebookRequestedPermissions = Arrays.asList("email", "user_friends");
    content = findViewById(android.R.id.content);
    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(this);
    successSnackbar = Snackbar.make(content, cm.aptoide.accountmanager.R.string.login_successful,
        Snackbar.LENGTH_SHORT);

    skipButton = getIntent().getBooleanExtra(SKIP_BUTTON, false);
    skipSubject = PublishRelay.create();
    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    getSupportActionBar().setHomeButtonEnabled(true);
    if (!skipButton) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    getSupportActionBar().setDisplayShowTitleEnabled(true);
    getSupportActionBar().setTitle(cm.aptoide.accountmanager.R.string.login);

    orMessage = (TextView) findViewById(cm.aptoide.accountmanager.R.id.or_message);
    forgotPasswordButton = (TextView) findViewById(cm.aptoide.accountmanager.R.id.forgot_password);

    googleLoginButton =
        (SignInButton) findViewById(cm.aptoide.accountmanager.R.id.g_sign_in_button);

    aptoideLoginButton = (Button) findViewById(cm.aptoide.accountmanager.R.id.button_login);
    aptoideEmailEditText = (EditText) findViewById(cm.aptoide.accountmanager.R.id.username);
    aptoidePasswordEditText = (EditText) findViewById(cm.aptoide.accountmanager.R.id.password);
    hideShowAptoidePasswordButton =
        (Button) findViewById(cm.aptoide.accountmanager.R.id.btn_show_hide_pass);

    facebookLoginButton =
        (LoginButton) findViewById(cm.aptoide.accountmanager.R.id.fb_login_button);
    callbackManager = CallbackManager.Factory.create();
    facebookLoginManager = LoginManager.getInstance();
    facebookLoginSubject = PublishRelay.create();
    facebookEmailRequiredDialog = new AlertDialog.Builder(this).setMessage(
        cm.aptoide.accountmanager.R.string.facebook_email_permission_regected_message)
        .setPositiveButton(cm.aptoide.accountmanager.R.string.facebook_grant_permission_button,
            (dialog, which) -> {
              facebookLoginManager.logInWithReadPermissions(LoginActivity.this,
                  Arrays.asList("email"));
            })
        .setNegativeButton(android.R.string.cancel, null)
        .create();

    final AptoideAccountManager accountManager =
        ((V8Engine)getApplicationContext()).getAccountManager();

    attachPresenter(new LoginPresenter(this, accountManager, facebookRequestedPermissions),
        savedInstanceState);
  }

  @Override protected void onResume() {
    super.onResume();
    RxView.clicks(hideShowAptoidePasswordButton).doOnNext(click -> {
      final int cursorPosition = aptoidePasswordEditText.getSelectionStart();
      final boolean passwordShown = aptoidePasswordEditText.getTransformationMethod() == null;
      hideShowAptoidePasswordButton.setBackgroundResource(
          passwordShown ? cm.aptoide.accountmanager.R.drawable.icon_closed_eye
              : cm.aptoide.accountmanager.R.drawable.icon_open_eye);
      aptoidePasswordEditText.setTransformationMethod(
          passwordShown ? new PasswordTransformationMethod() : null);
      aptoidePasswordEditText.setSelection(cursorPosition);
    }).compose(bindUntilEvent(LifecycleEvent.PAUSE)).subscribe();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    if (skipButton) {
      menu.add(0, 0, 0, cm.aptoide.accountmanager.R.string.wizard_skip)
          .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
      return true;
    } else {
      return super.onCreateOptionsMenu(menu);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();
    if (i == android.R.id.home || i == cm.aptoide.accountmanager.R.id.home || i == 0) {
      skipSubject.call(null);
      return true;
    }
    return super.onOptionsItemSelected(item);
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
    facebookLoginButton.setReadPermissions(facebookRequestedPermissions);
    facebookLoginButton.setVisibility(View.VISIBLE);
    facebookLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        facebookLoginSubject.call(new FacebookAccountViewModel(loginResult.getAccessToken(),
            loginResult.getRecentlyDeniedPermissions()));
      }

      @Override public void onCancel() {
        showFacebookLoginError(cm.aptoide.accountmanager.R.string.unknown_error);
      }

      @Override public void onError(FacebookException error) {
        showFacebookLoginError(cm.aptoide.accountmanager.R.string.error_occured);
      }
    });
    showOrMessage();
  }

  @Override public void showGoogleLogin() {
    super.showGoogleLogin();
    showOrMessage();
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

  @Override public void navigateToMainView() {
    finish();
  }

  @Override public Observable<AptoideAccountViewModel> aptoideLoginSelection() {
    return RxView.clicks(aptoideLoginButton)
        .map(click -> new AptoideAccountViewModel(aptoideEmailEditText.getText().toString(),
            aptoidePasswordEditText.getText().toString()));
  }

  @Override public void showCheckAptoideCredentialsMessage() {
    ShowMessage.asSnack(content, cm.aptoide.accountmanager.R.string.fields_cannot_empty);
  }

  @Override public void navigateToForgotPasswordView() {
    startActivity(new Intent(Intent.ACTION_VIEW,
        Uri.parse("http://m.aptoide.com/account/password-recovery")));
  }

  @Override public Observable<Void> forgotPasswordSelection() {
    return RxView.clicks(forgotPasswordButton);
  }

  @Override public Observable<Void> skipSelection() {
    return skipSubject;
  }

  @Override public Observable<Void> successMessageShown() {
    return RxSnackbar.dismisses(successSnackbar).map(dismiss -> null);
  }

  @Override public void showSuccessMessage() {
    successSnackbar.show();
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

  private void showOrMessage() {
    orMessage.setVisibility(View.VISIBLE);
  }

  private void showFacebookLoginError(@StringRes int errorRes) {
    ShowMessage.asSnack(content, errorRes);
  }
}
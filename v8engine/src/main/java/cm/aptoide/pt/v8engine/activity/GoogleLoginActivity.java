/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.BuildConfig;
import cm.aptoide.accountmanager.ws.AptoideWsV3Exception;
import cm.aptoide.accountmanager.ws.ErrorsMapper;
import cm.aptoide.accountmanager.ws.responses.GenericResponseV3;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.gms.GooglePlayServicesConnection;
import cm.aptoide.pt.v8engine.presenter.GoogleLoginPresenter;
import cm.aptoide.pt.v8engine.view.GoogleLoginView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

/**
 * Created by marcelobenites on 06/02/17.
 */
public class GoogleLoginActivity extends BaseActivity implements GoogleLoginView {

  private static final int RESOLVE_CONNECTION_ERROR_REQUEST_CODE = 1;
  private static final int RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE = 2;
  private GoogleApiAvailability apiAvailability;
  private Dialog errorDialog;
  private GoogleApiClient client;
  private SignInButton googleLoginButton;

  private PublishRelay<CredentialsViewModel> googleLoginSubject;
  private ProgressDialog progressDialog;
  private View content;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_activity_layout);

    content = findViewById(android.R.id.content);
    googleLoginButton =
        (SignInButton) findViewById(cm.aptoide.accountmanager.R.id.g_sign_in_button);
    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(this);

    googleLoginSubject = PublishRelay.create();
    apiAvailability = GoogleApiAvailability.getInstance();
    final GoogleSignInOptions options =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
            .requestScopes(new Scope(Scopes.PROFILE))
            .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
            .build();
    client = new GoogleApiClient.Builder(this)
        .addApi(GOOGLE_SIGN_IN_API, options).build();
    final AptoideAccountManager accountManager = new AptoideAccountManager(
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), this), this,
        Application.getConfiguration(), AccountManager.get(this),
        new SecureCoderDecoder.Builder(this).create());

    final GooglePlayServicesConnection connection =
        new GooglePlayServicesConnection(this, apiAvailability, client);
    attachPresenter(
        new GoogleLoginPresenter(this, connection, Application.getConfiguration(), accountManager),
        savedInstanceState);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (errorDialog != null) {
      errorDialog.dismiss();
    }
  }

  @Override public Observable<Void> googleCredentialsSelection() {
    return RxView.clicks(googleLoginButton);
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

  @Override public void showGoogleCredentialsSelector() {
    googleLoginButton.setVisibility(View.VISIBLE);
  }

  @Override public void hideGoogleCredentialsSelector() {
    googleLoginButton.setVisibility(View.GONE);
  }

  @Override public void navigateToGoogleCredentialsView() {
    startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(client),
        RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE) {
      final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      final GoogleSignInAccount account = result.getSignInAccount();
      if (result.isSuccess() && account != null) {
        googleLoginSubject.call(
            new CredentialsViewModel(account.getDisplayName(), account.getServerAuthCode(),
                account.getEmail()));
      }
    }
  }

  @Override public Observable<CredentialsViewModel> googleLoginSelection() {
    return googleLoginSubject;
  }

  @Override public void showResolution(int errorCode) {
    final PendingIntent errorResolutionPendingIntent =
        apiAvailability.getErrorResolutionPendingIntent(this, errorCode,
            RESOLVE_CONNECTION_ERROR_REQUEST_CODE);
    try {
      errorResolutionPendingIntent.send(this, RESOLVE_CONNECTION_ERROR_REQUEST_CODE, null);
    } catch (PendingIntent.CanceledException e) {
      CrashReport.getInstance().log(e);
    }
  }

  @Override public void showConnectionErrorMessage(int errorCode) {
    if (errorDialog != null && errorDialog.isShowing()) {
      return;
    }

    errorDialog =
        apiAvailability.getErrorDialog(this, errorCode, RESOLVE_CONNECTION_ERROR_REQUEST_CODE);

    errorDialog.show();
  }
}
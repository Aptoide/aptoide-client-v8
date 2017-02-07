/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.app.Dialog;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import cm.aptoide.accountmanager.BuildConfig;
import cm.aptoide.accountmanager.GoogleLoginUtils;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.gms.GooglePlayServicesConnection;
import cm.aptoide.pt.v8engine.presenter.GoogleLoginPresenter;
import cm.aptoide.pt.v8engine.view.GooglePlayServicesView;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

/**
 * Created by marcelobenites on 06/02/17.
 */
public class GooglePlayServicesActivity extends BaseActivity
    implements GooglePlayServicesView {

  public static final int RESOLVE_CONNECTION_ERROR_REQUEST_CODE = 1;
  private GoogleApiAvailability apiAvailability;
  private Dialog errorDialog;
  private GoogleApiClient client;
  private SignInButton googleLoginButton;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_activity_layout);

    googleLoginButton = (SignInButton) findViewById(cm.aptoide.accountmanager.R.id.g_sign_in_button);

    apiAvailability = GoogleApiAvailability.getInstance();
    final GoogleSignInOptions options =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
            .requestScopes(new Scope(Scopes.PROFILE))
            .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
            .build();
    client = new GoogleApiClient.Builder(this)
        .addApi(GOOGLE_SIGN_IN_API, options)
        .build();

    final GooglePlayServicesConnection connection =
        new GooglePlayServicesConnection(this, apiAvailability, client);
    attachPresenter(new GoogleLoginPresenter(this, connection), savedInstanceState);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (errorDialog != null) {
      errorDialog.dismiss();
    }
  }

  @Override public Observable<Void> onGoogleLoginSelection() {
    return RxView.clicks(googleLoginButton);
  }

  @Override public void showGoogleLogin() {
    googleLoginButton.setVisibility(View.VISIBLE);
  }

  @Override public void hideGoogleLogin() {
    googleLoginButton.setVisibility(View.GONE);
  }

  @Override public void showSuccess() {
    Toast.makeText(this, "SUCCESS", Toast.LENGTH_LONG).show();
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
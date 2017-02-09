/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 08/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cm.aptoide.accountmanager.BuildConfig;
import cm.aptoide.pt.v8engine.view.GoogleLoginView;
import cm.aptoide.pt.v8engine.view.LoginView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

/**
 * Created by marcelobenites on 08/02/17.
 */

public abstract class GoogleLoginActivity extends GooglePlayServicesActivity implements GoogleLoginView {

  private static final int RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE = 2;
  private GoogleApiClient client;
  private PublishRelay<LoginView.GoogleAccountViewModel> googleLoginSubject;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    googleLoginSubject = PublishRelay.create();

    final GoogleSignInOptions options =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
            .requestScopes(new Scope(Scopes.PROFILE))
            .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
            .build();
    client = getClientBuilder()
        .addApi(GOOGLE_SIGN_IN_API, options)
        .build();
  }

  protected abstract SignInButton getGoogleButton();

  @Override protected void onResume() {
    super.onResume();
    getGoogleButton().setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        client.connect();
        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(client),
            RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE);
      }
    });
  }

  @Override protected void onPause() {
    super.onPause();
    getGoogleButton().setOnClickListener(null);
  }

  @Override protected GoogleApiClient getClient() {
    return client;
  }

  @Override public void showGoogleLogin() {
    getGoogleButton().setVisibility(View.VISIBLE);
  }

  @Override public void hideGoogleLogin() {
    getGoogleButton().setVisibility(View.GONE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE) {
      final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      final GoogleSignInAccount account = result.getSignInAccount();
      if (result.isSuccess() && account != null) {
        googleLoginSubject.call(
            new LoginView.GoogleAccountViewModel(account.getDisplayName(), account.getServerAuthCode(),
                account.getEmail()));
      }
    }
  }

  @Override public Observable<LoginView.GoogleAccountViewModel> googleLoginSelection() {
    return googleLoginSubject;
  }
}
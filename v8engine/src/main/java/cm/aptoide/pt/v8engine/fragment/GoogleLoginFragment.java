/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 08/02/2017.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import cm.aptoide.accountmanager.BuildConfig;
import cm.aptoide.pt.v8engine.view.GoogleLoginView;
import cm.aptoide.pt.v8engine.viewModel.GoogleAccountViewModel;
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

public abstract class GoogleLoginFragment extends GooglePlayServicesFragment
    implements GoogleLoginView {

  private static final int RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE = 2;
  private PublishRelay<GoogleAccountViewModel> googleLoginSubject;
  private GoogleApiClient client;

  @Override public void onResume() {
    super.onResume();
    getGoogleButton().setOnClickListener(v -> {
      connect();
      startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(client),
          RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE);
    });
  }

  protected abstract SignInButton getGoogleButton();

  @Override protected void connect() {
    client.connect();
  }

  @Override public void onPause() {
    super.onPause();
    getGoogleButton().setOnClickListener(null);
  }

  @Override public void showGoogleLogin() {
    getGoogleButton().setVisibility(View.VISIBLE);
  }

  @Override public void hideGoogleLogin() {
    getGoogleButton().setVisibility(View.GONE);
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
    }
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    googleLoginSubject = PublishRelay.create();

    final GoogleSignInOptions options =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
            .requestScopes(new Scope(Scopes.PROFILE))
            .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
            .build();

    client = getClientBuilder().addApi(GOOGLE_SIGN_IN_API, options).build();
  }

  protected abstract void showGoogleLoginError();
}

/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 08/02/2017.
 */

package cm.aptoide.pt.v8engine.view.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.presenter.GoogleLoginView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

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

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    googleLoginSubject = PublishRelay.create();
    client = ((V8Engine) getContext().getApplicationContext()).getGoogleSignInClient();
  }

  @Override protected void connect() {
    client.connect();
  }

  protected abstract void showGoogleLoginError();
}

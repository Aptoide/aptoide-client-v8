/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 08/02/2017.
 */

package cm.aptoide.pt.view.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.presenter.GoogleLoginView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

public abstract class GoogleLoginFragment extends GooglePlayServicesFragment
    implements GoogleLoginView {

  private static final int RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE = 2;
  private PublishRelay<GoogleSignInResult> googleLoginSubject;
  private GoogleApiClient client;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    client = ((AptoideApplication) getContext().getApplicationContext()).getGoogleSignInClient();
    googleLoginSubject = PublishRelay.create();
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

  @Override public void showGoogleLogin() {
    getGoogleButton().setVisibility(View.VISIBLE);
  }

  @Override public void hideGoogleLogin() {
    getGoogleButton().setVisibility(View.GONE);
  }

  @Override public Observable<GoogleSignInResult> googleLoginClick() {
    return googleLoginSubject;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RESOLVE_GOOGLE_CREDENTIALS_REQUEST_CODE) {
      googleLoginSubject.call(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
    }
  }

  @Override protected void connect() {
    client.connect();
  }

}

/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 07/02/2017.
 */

package cm.aptoide.pt.v8engine.view;

import rx.Observable;

/**
 * Created by marcelobenites on 07/02/17.
 */

public interface GoogleLoginView extends GooglePlayServicesView {

  void showLoading();

  void hideLoading();

  void showGoogleCredentialsSelector();

  void hideGoogleCredentialsSelector();

  void navigateToGoogleCredentialsView();

  Observable<Void> googleCredentialsSelection();

  Observable<CredentialsViewModel> googleLoginSelection();

  void showError(Throwable throwable);

  class CredentialsViewModel {

    private final String name;
    private final String token;
    private final String email;

    public CredentialsViewModel(String name, String token, String email) {
      this.name = name;
      this.token = token;
      this.email = email;
    }

    public String getName() {
      return name;
    }

    public String getToken() {
      return token;
    }

    public String getEmail() {
      return email;
    }
  }

}

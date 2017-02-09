/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 07/02/2017.
 */

package cm.aptoide.pt.v8engine.view;

import rx.Observable;

/**
 * Created by marcelobenites on 07/02/17.
 */

public interface LoginView extends GoogleLoginView {

  void showLoading();

  void hideLoading();

  void showError(Throwable throwable);

  class AccountViewModel {

    private final String username;
    private final String password;

    public AccountViewModel(String password, String username) {
      this.password = password;
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public String getUsername() {
      return username;
    }
  }

  class GoogleAccountViewModel extends AccountViewModel {

    private final String displayName;

    public GoogleAccountViewModel(String displayName, String password, String username) {
      super(password, username);
      this.displayName = displayName;
    }

    public String getDisplayName() {
      return displayName;
    }
  }

}

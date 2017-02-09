/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 07/02/2017.
 */

package cm.aptoide.pt.v8engine.view;

import com.facebook.AccessToken;
import java.util.Set;
import rx.Observable;

/**
 * Created by marcelobenites on 07/02/17.
 */

public interface LoginView extends GoogleLoginView {

  void showLoading();

  void hideLoading();

  void showError(Throwable throwable);

  void showFacebookLogin();

  void showPermissionsRequiredMessage();

  void hideFacebookLogin();

  Observable<FacebookAccountViewModel> facebookLoginSelection();

  class GoogleAccountViewModel {

    private final String displayName;
    private final String token;
    private final String email;

    public GoogleAccountViewModel(String displayName, String token, String email) {
      this.displayName = displayName;
      this.token = token;
      this.email = email;
    }

    public String getDisplayName() {
      return displayName;
    }

    public String getToken() {
      return token;
    }

    public String getEmail() {
      return email;
    }
  }

  class FacebookAccountViewModel {

    private final AccessToken token;
    private final Set<String> deniedPermissions;
    private final Set<String> grantedPermissions;

    public FacebookAccountViewModel(AccessToken token, Set<String> deniedPermissions, Set<String> grantedPermissions) {
      this.token = token;
      this.deniedPermissions = deniedPermissions;
      this.grantedPermissions = grantedPermissions;
    }

    public AccessToken getToken() {
      return token;
    }

    public Set<String> getDeniedPermissions() {
      return deniedPermissions;
    }

    public Set<String> getGrantedPermissions() {
      return grantedPermissions;
    }
  }

}

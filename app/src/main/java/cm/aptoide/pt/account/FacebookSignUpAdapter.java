package cm.aptoide.pt.account;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.SignUpAdapter;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;

public class FacebookSignUpAdapter implements SignUpAdapter<FacebookLoginResult> {

  public static final String TYPE = "FACEBOOK";
  private final List<String> facebookRequiredPermissions;
  private final LoginManager loginManager;
  private final LoginPreferences loginPreferences;

  public FacebookSignUpAdapter(List<String> facebookRequiredPermissions, LoginManager loginManager,
      LoginPreferences loginPreferences) {
    this.facebookRequiredPermissions = facebookRequiredPermissions;
    this.loginManager = loginManager;
    this.loginPreferences = loginPreferences;
  }

  @Override public Single<Account> signUp(FacebookLoginResult result, AccountService service) {

    if (!isEnabled()) {
      return Single.error(new IllegalStateException("Facebook sign up is not enabled"));
    }

    if (result.getState() == FacebookLoginResult.STATE_CANCELLED) {
      return Single.error(
          new FacebookSignUpException(FacebookSignUpException.USER_CANCELLED, "USER_CANCELLED"));
    }

    if (result.getState() == FacebookLoginResult.STATE_ERROR) {
      return Single.defer(() -> Single.error(
          new FacebookSignUpException(FacebookSignUpException.ERROR, result.getError()
              .getMessage())));
    }

    if (!result.getResult()
        .getAccessToken()
        .getPermissions()
        .containsAll(facebookRequiredPermissions)) {
      return Single.error(new FacebookSignUpException(FacebookSignUpException.
          MISSING_REQUIRED_PERMISSIONS, "MISSING_REQUIRED_PERMISSIONS"));
    }

    return getFacebookEmail(result.getResult()
        .getAccessToken()).flatMap(email -> service.createAccount(email, result.getResult()
        .getAccessToken()
        .getToken(), null, TYPE));
  }

  @Override public Completable logout() {
    return Completable.fromAction(() -> {
      loginManager.logOut();
    });
  }

  @Override public boolean isEnabled() {
    return loginPreferences.isFacebookLoginEnabled();
  }

  private Single<String> getFacebookEmail(AccessToken accessToken) {
    return Single.defer(() -> {
      try {
        final GraphResponse response = GraphRequest.newMeRequest(accessToken, null)
            .executeAndWait();
        final JSONObject object = response.getJSONObject();
        if (response.getError() == null && object != null) {
          try {
            return Single.just(
                object.has("email") ? object.getString("email") : object.getString("id"));
          } catch (JSONException ignored) {
            return Single.error(
                new FacebookSignUpException(FacebookSignUpException.ERROR, "Error parsing email"));
          }
        } else {
          return Single.error(new FacebookSignUpException(FacebookSignUpException.ERROR,
              "Unknown error(maybe network error when getting user data)"));
        }
      } catch (RuntimeException exception) {
        return Single.error(
            new FacebookSignUpException(FacebookSignUpException.ERROR, exception.getMessage()));
      }
    })
        .subscribeOn(Schedulers.io());
  }
}

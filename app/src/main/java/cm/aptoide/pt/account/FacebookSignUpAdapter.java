package cm.aptoide.pt.account;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.SignUpAdapter;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;

public class FacebookSignUpAdapter implements SignUpAdapter<LoginResult> {

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

  @Override public Single<Account> signUp(LoginResult result, AccountService service) {

    if (!isEnabled()) {
      return Single.error(new IllegalStateException("Facebook sign up is not enabled"));
    }

    if (declinedRequiredPermissions(result.getRecentlyDeniedPermissions())) {
      return Single.error(
          new FacebookAccountException(FacebookAccountException.FACEBOOK_DENIED_CREDENTIALS));
    }

    return getFacebookEmail(result.getAccessToken()).flatMap(email -> service.createAccount(email,
        result.getAccessToken()
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

  private boolean declinedRequiredPermissions(Set<String> declinedPermissions) {
    return declinedPermissions.containsAll(facebookRequiredPermissions);
  }

  private Single<String> getFacebookEmail(AccessToken accessToken) {
    return Single.defer(() -> {
      final GraphResponse response = GraphRequest.newMeRequest(accessToken, null)
          .executeAndWait();
      final JSONObject object = response.getJSONObject();
      if (response.getError() == null && object != null) {
        try {
          return Single.just(
              object.has("email") ? object.getString("email") : object.getString("id"));
        } catch (JSONException ignored) {
          return Single.error(
              new FacebookAccountException(FacebookAccountException.FACEBOOK_API_INVALID_RESPONSE));
        }
      } else {
        return Single.error(
            new FacebookAccountException(FacebookAccountException.FACEBOOK_API_INVALID_RESPONSE));
      }
    })
        .subscribeOn(Schedulers.io());
  }
}

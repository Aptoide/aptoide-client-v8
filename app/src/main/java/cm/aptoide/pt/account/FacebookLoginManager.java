package cm.aptoide.pt.account;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;

public class FacebookLoginManager {

  private final AptoideAccountManager accountManager;
  private final List<String> facebookRequiredPermissions;

  public FacebookLoginManager(AptoideAccountManager accountManager,
      List<String> facebookRequiredPermissions) {
    this.accountManager = accountManager;
    this.facebookRequiredPermissions = facebookRequiredPermissions;
  }

  public Completable login(LoginResult result) {
    if (declinedRequiredPermissions(result.getRecentlyDeniedPermissions())) {
      return Completable.error(
          new FacebookAccountException(FacebookAccountException.FACEBOOK_DENIED_CREDENTIALS));
    }

    return getFacebookUsername(result.getAccessToken()).flatMapCompletable(
        username -> accountManager.login(Account.Type.FACEBOOK, username, result.getAccessToken()
            .getToken(), null));
  }

  private boolean declinedRequiredPermissions(Set<String> declinedPermissions) {
    return declinedPermissions.containsAll(facebookRequiredPermissions);
  }

  private Single<String> getFacebookUsername(AccessToken accessToken) {
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
    }).subscribeOn(Schedulers.io());
  }
}

package cm.aptoide.pt.v8engine.view.account;

import com.facebook.AccessToken;
import java.util.Set;

public class FacebookAccountViewModel {

  private final AccessToken token;
  private final Set<String> deniedPermissions;

  public FacebookAccountViewModel(AccessToken token, Set<String> deniedPermissions) {
    this.token = token;
    this.deniedPermissions = deniedPermissions;
  }

  public AccessToken getToken() {
    return token;
  }

  public Set<String> getDeniedPermissions() {
    return deniedPermissions;
  }
}

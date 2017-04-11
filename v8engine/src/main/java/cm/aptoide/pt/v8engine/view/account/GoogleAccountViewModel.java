package cm.aptoide.pt.v8engine.view.account;

public class GoogleAccountViewModel {

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

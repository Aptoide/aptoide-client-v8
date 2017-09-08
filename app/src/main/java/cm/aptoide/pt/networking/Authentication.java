package cm.aptoide.pt.networking;

public class Authentication {

  private final String email;
  private final String refreshToken;
  private final String accessToken;
  private final String password;
  private final String type;

  public Authentication(String email, String refreshToken, String accessToken, String password,
      String type) {
    this.email = email;
    this.refreshToken = refreshToken;
    this.accessToken = accessToken;
    this.password = password;
    this.type = type;
  }

  public String getEmail() {
    return email;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getPassword() {
    return password;
  }

  public String getType() {
    return type;
  }

  public boolean isAuthenticated() {
    return !isEmpty(email) && !isEmpty(accessToken) && !isEmpty(refreshToken) && !isEmpty(password);
  }

  private boolean isEmpty(String string) {
    return string == null
        || string.trim()
        .length() == 0;
  }
}

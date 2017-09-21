package cm.aptoide.accountmanager;

public class AptoideCredentials {

  private final String email;
  private final String password;

  public AptoideCredentials(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}
package cm.aptoide.accountmanager;

public class AptoideCredentials {

  private final String email;
  private final String password;
  private final boolean isChecked;

  public AptoideCredentials(String email, String password, boolean isChecked) {
    this.email = email;
    this.password = password;
    this.isChecked = isChecked;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public boolean isChecked() {
    return isChecked;
  }
}
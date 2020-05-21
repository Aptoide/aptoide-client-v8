package cm.aptoide.accountmanager;

public class AptoideCredentials {

  private final String email;
  private final String code;
  private final boolean isChecked;

  public AptoideCredentials(String email, String code, boolean isChecked) {
    this.email = email;
    this.code = code;
    this.isChecked = isChecked;
  }

  public String getEmail() {
    return email;
  }

  public String getCode() {
    return code;
  }

  public boolean isChecked() {
    return isChecked;
  }
}
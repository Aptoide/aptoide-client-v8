package cm.aptoide.accountmanager;

public class AptoideCredentials {

  private final String email;
  private final String code;
  private final boolean isChecked;
  private final String agent;
  private final String state;

  public AptoideCredentials(String email, String code, boolean isChecked, String agent,
      String state) {
    this.email = email;
    this.code = code;
    this.isChecked = isChecked;
    this.agent = agent;
    this.state = state;
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

  public String getAgent() {
    return agent;
  }

  public String getState() {
    return state;
  }
}
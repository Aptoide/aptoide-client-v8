package cm.aptoide.pt.v8engine.view.account;

public class AptoideAccountViewModel {

  private final String username;
  private final String password;

  public AptoideAccountViewModel(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}


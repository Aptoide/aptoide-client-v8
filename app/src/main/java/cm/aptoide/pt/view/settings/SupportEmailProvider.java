package cm.aptoide.pt.view.settings;

public class SupportEmailProvider {
  private final String email;
  private final String aptoideEmail;

  public SupportEmailProvider(String email, String aptoideEmail) {
    this.email = email;
    this.aptoideEmail = aptoideEmail;
  }

  public boolean isAptoideSupport() {
    return aptoideEmail.equals(email);
  }
}

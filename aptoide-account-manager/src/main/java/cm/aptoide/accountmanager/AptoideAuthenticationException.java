package cm.aptoide.accountmanager;

public class AptoideAuthenticationException extends IllegalStateException {
  public AptoideAuthenticationException() {
    super("User not authenticated");
  }
}

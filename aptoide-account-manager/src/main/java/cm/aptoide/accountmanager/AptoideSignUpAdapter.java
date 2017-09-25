package cm.aptoide.accountmanager;

import java.net.SocketTimeoutException;
import rx.Completable;
import rx.Single;

public class AptoideSignUpAdapter implements SignUpAdapter<AptoideCredentials> {

  private final CredentialsValidator credentialsValidator;
  private final AccountAnalytics accountAnalytics;

  public AptoideSignUpAdapter(CredentialsValidator credentialsValidator,
      AccountAnalytics accountAnalytics) {
    this.credentialsValidator = credentialsValidator;
    this.accountAnalytics = accountAnalytics;
  }

  @Override public Single<Account> signUp(AptoideCredentials credentials, AccountService service) {
    return credentialsValidator.validate(credentials, true)
        .andThen(service.createAccount(credentials.getEmail(), credentials.getPassword()))
        .doOnSuccess(account -> accountAnalytics.signUp())
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof SocketTimeoutException) {
            return service.getAccount(credentials.getEmail(), credentials.getPassword());
          }
          return Single.error(throwable);
        });
  }

  @Override public Completable logout() {
    return Completable.complete();
  }

  @Override public boolean isEnabled() {
    return true;
  }
}

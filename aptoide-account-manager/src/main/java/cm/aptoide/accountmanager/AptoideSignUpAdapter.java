package cm.aptoide.accountmanager;

import java.net.SocketTimeoutException;
import rx.Completable;
import rx.Single;

public class AptoideSignUpAdapter implements SignUpAdapter<AptoideCredentials> {

  private final CredentialsValidator credentialsValidator;

  public AptoideSignUpAdapter(CredentialsValidator credentialsValidator) {
    this.credentialsValidator = credentialsValidator;
  }

  @Override public Single<Account> signUp(AptoideCredentials credentials, AccountService service) {
    return credentialsValidator.validate(credentials)
        .andThen(service.createAccount(credentials.getEmail(), credentials.getCode()))
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof SocketTimeoutException) {
            return service.getAccount(credentials.getEmail(), credentials.getCode());
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

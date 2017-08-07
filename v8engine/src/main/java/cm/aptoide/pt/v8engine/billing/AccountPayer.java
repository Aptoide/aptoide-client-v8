package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.accountmanager.AptoideAccountManager;
import rx.Observable;
import rx.Single;

public class AccountPayer implements Payer {

  private final AptoideAccountManager accountManager;

  public AccountPayer(AptoideAccountManager accountManager) {
    this.accountManager = accountManager;
  }

  @Override public Single<String> getId() {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> {
          if (account.isLoggedIn()) {
            return Single.just(account.getEmail());
          }
          return Single.error(
              new IllegalStateException("User not logged in can not obtain payer id"));
        });
  }

  @Override public Observable<Boolean> isAuthenticated() {
    return accountManager.accountStatus()
        .map(account -> account.isLoggedIn());
  }
}

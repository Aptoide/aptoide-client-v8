package cm.aptoide.pt.billing.customer;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.billing.Customer;
import rx.Observable;
import rx.Single;

public class AccountCustomer implements Customer {

  private final AptoideAccountManager accountManager;

  public AccountCustomer(AptoideAccountManager accountManager) {
    this.accountManager = accountManager;
  }

  @Override public Single<String> getId() {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> {
          if (account.isLoggedIn()) {
            return Single.just(account.getId());
          }
          return Single.error(
              new IllegalStateException("User not logged in can not obtain customer id"));
        });
  }

  @Override public Observable<Boolean> isAuthenticated() {
    return accountManager.accountStatus()
        .map(account -> account.isLoggedIn());
  }
}

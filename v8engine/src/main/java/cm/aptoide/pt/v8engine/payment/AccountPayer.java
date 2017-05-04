package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.accountmanager.AptoideAccountManager;
import rx.Single;

/**
 * Created by marcelobenites on 04/05/17.
 */

public class AccountPayer implements Payer {

  private final AptoideAccountManager accountManager;

  public AccountPayer(AptoideAccountManager accountManager) {
    this.accountManager = accountManager;
  }

  @Override public Single<String> getId() {
    return accountManager.accountStatus().first().toSingle().flatMap(account -> {
      if (account.isLoggedIn()) {
        return Single.just(account.getEmail());
      }
      return Single.error(new IllegalStateException("User not logged in can not obtain payer id"));
    });
  }
}

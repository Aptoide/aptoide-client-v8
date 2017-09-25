package cm.aptoide.accountmanager;

import rx.Completable;
import rx.Single;

public interface AccountPersistence {

  Completable saveAccount(Account account);

  Single<Account> getAccount();

  Completable removeAccount();
}

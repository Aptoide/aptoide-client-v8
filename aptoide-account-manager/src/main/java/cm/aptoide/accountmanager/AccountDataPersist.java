package cm.aptoide.accountmanager;

import rx.Completable;
import rx.Single;

public interface AccountDataPersist {

  Completable saveAccount(Account account);

  Single<Account> getAccount();

  Completable removeAccount();
}

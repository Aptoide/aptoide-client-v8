package cm.aptoide.accountmanager;

import rx.Completable;
import rx.Single;

public interface SignUpAdapter<T> {

  Single<Account> signUp(T data, AccountService service);

  Completable logout();

  boolean isEnabled();
}
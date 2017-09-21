package cm.aptoide.accountmanager;

import rx.Completable;
import rx.Single;

public interface AccountService {

  Single<Account> getAccount(String email, String password);

  Single<Account> createAccount(String email, String metadata, String name, String type);

  Single<Account> createAccount(String email, String password);

  Single<Account> getAccount();

  Completable updateAccount(String nickname, String avatarPath);

  Completable updateAccount(String accessLevel);

  Completable updateAccountUsername(String username);

  Completable unsubscribeStore(String storeName, String storeUserName, String storePassword);

  Completable subscribeStore(String storeName, String storeUserName, String storePassword);

  Completable updateAccount(boolean adultContentEnabled);

  Completable removeAccount();
}

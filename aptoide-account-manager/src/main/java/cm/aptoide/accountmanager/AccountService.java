package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.model.v7.GetUserMeta;
import rx.Completable;
import rx.Observable;
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

  Completable subscribeUser(long userId);

  Completable unsubscribeUser(long userId);

  Completable updateAccount(boolean adultContentEnabled);

  Completable removeAccount();

  Observable<GetUserMeta> getUserInfo(long userId);
}

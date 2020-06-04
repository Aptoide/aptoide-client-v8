package cm.aptoide.accountmanager;

import com.aptoide.authentication.model.CodeAuth;
import rx.Completable;
import rx.Single;

public interface AccountService {

  Single<Account> getAccount(String email, String code, String state, String agent);

  Single<Account> createAccount(String email, String metadata, String type);

  Single<Account> createAccount(String email, String password);

  Completable updateTermsAndConditions();

  Single<Account> getAccount(String email);

  Completable updateAccount(String nickname, String avatarPath);

  Completable updateAccount(String accessLevel);

  Completable updateAccountUsername(String username);

  Completable unsubscribeStore(String storeName, String storeUserName, String storePassword);

  Completable subscribeStore(String storeName, String storeUserName, String storePassword);

  Completable updateAccount(boolean adultContentEnabled);

  Completable removeAccount();

  Single<CodeAuth> sendMagicLink(String email);
}

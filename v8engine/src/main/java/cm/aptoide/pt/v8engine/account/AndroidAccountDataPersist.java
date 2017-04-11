package cm.aptoide.pt.v8engine.account;

import android.accounts.AccountManager;
import android.os.Build;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountDataPersist;
import cm.aptoide.accountmanager.AccountFactory;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import java.util.concurrent.Executors;
import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Persists {@link Account} data in using {@link AccountManager}.
 */
public class AndroidAccountDataPersist implements AccountDataPersist {

  private static final String ACCOUNT_ACCESS_LEVEL = "access";
  private static final String ACCOUNT_ACCESS_CONFIRMED = "access_confirmed";
  private static final String ACCOUNT_ADULT_CONTENT_ENABLED =
      "aptoide_account_manager_mature_switch";
  private static final String ACCOUNT_TYPE = "aptoide_account_manager_login_mode";
  private static final String ACCOUNT_ACCESS_TOKEN = "access_token";
  private static final String ACCOUNT_REFRESH_TOKEN = "refresh_token";
  private static final String ACCOUNT_NICKNAME = "username";
  private static final String ACCOUNT_AVATAR_URL = "useravatar";
  private static final String ACCOUNT_STORE_NAME = "userRepo";
  private static final String ACCOUNT_ID = "userId";
  private static final String ACCOUNT_STORE_AVATAR_URL = "storeAvatar";

  private final String accountType;
  private final AccountManager androidAccountManager;
  private final DatabaseStoreDataPersist storePersist;
  private final AccountFactory accountFactory;
  private final AndroidAccountDataMigration accountDataMigration;

  private Account accountCache;

  public AndroidAccountDataPersist(String accountType, AccountManager androidAccountManager,
      DatabaseStoreDataPersist storePersist, AccountFactory accountFactory,
      AndroidAccountDataMigration accountDataMigration) {
    this.accountType = accountType;
    this.androidAccountManager = androidAccountManager;
    this.storePersist = storePersist;
    this.accountFactory = accountFactory;
    this.accountDataMigration = accountDataMigration;
  }

  @Override public Completable saveAccount(Account account) {
    return Completable.defer(() -> {
      final android.accounts.Account[] androidAccounts =
          androidAccountManager.getAccountsByType(accountType);

      final android.accounts.Account androidAccount;
      if (androidAccounts.length == 0) {
        androidAccount = new android.accounts.Account(account.getEmail(), accountType);
        try {
          androidAccountManager.addAccountExplicitly(androidAccount, account.getPassword(), null);
        } catch (SecurityException e) {
          return Completable.error(e);
        }
      } else {
        androidAccount = androidAccounts[0];
      }

      androidAccountManager.setUserData(androidAccount, ACCOUNT_ID, account.getId());
      androidAccountManager.setUserData(androidAccount, ACCOUNT_NICKNAME, account.getNickname());
      androidAccountManager.setUserData(androidAccount, ACCOUNT_AVATAR_URL, account.getAvatar());
      androidAccountManager.setUserData(androidAccount, ACCOUNT_REFRESH_TOKEN,
          account.getRefreshToken());
      androidAccountManager.setUserData(androidAccount, ACCOUNT_ACCESS_TOKEN,
          account.getAccessToken());
      androidAccountManager.setUserData(androidAccount, ACCOUNT_TYPE, account.getType().name());
      androidAccountManager.setUserData(androidAccount, ACCOUNT_STORE_NAME, account.getStoreName());
      androidAccountManager.setUserData(androidAccount, ACCOUNT_STORE_AVATAR_URL,
          account.getStoreAvatar());
      androidAccountManager.setUserData(androidAccount, ACCOUNT_ADULT_CONTENT_ENABLED,
          String.valueOf(account.isAdultContentEnabled()));
      androidAccountManager.setUserData(androidAccount, ACCOUNT_ACCESS_LEVEL,
          account.getAccess().name());
      androidAccountManager.setUserData(androidAccount, ACCOUNT_ACCESS_CONFIRMED,
          String.valueOf(account.isAccessConfirmed()));

      return storePersist.persist(account.getSubscribedStores()).doOnCompleted(() -> {
        accountCache = account;
      });
    }).subscribeOn(Schedulers.io());
  }

  @Override public Single<Account> getAccount() {
    if (accountCache != null) {
      return Single.just(accountCache);
    }
    return accountDataMigration.migrate().andThen(getAndroidAccount().flatMap(androidAccount -> {

      final String access = androidAccountManager.getUserData(androidAccount,
          AndroidAccountDataPersist.ACCOUNT_ACCESS_LEVEL);

      return storePersist.get()
          .doOnError(err -> CrashReport.getInstance().log(err))
          .map(stores -> accountFactory.createAccount(access, stores,
              androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.ACCOUNT_ID), androidAccount.name,
              androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.ACCOUNT_NICKNAME),
              androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.ACCOUNT_AVATAR_URL),
              androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.ACCOUNT_REFRESH_TOKEN),
              androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.ACCOUNT_ACCESS_TOKEN),
              androidAccountManager.getPassword(androidAccount), Account.Type.valueOf(
                  androidAccountManager.getUserData(androidAccount,
                      AndroidAccountDataPersist.ACCOUNT_TYPE)),
              androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.ACCOUNT_STORE_NAME),
              androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.ACCOUNT_STORE_AVATAR_URL), Boolean.valueOf(
                  androidAccountManager.getUserData(androidAccount,
                      AndroidAccountDataPersist.ACCOUNT_ADULT_CONTENT_ENABLED)), Boolean.valueOf(
                  androidAccountManager.getUserData(androidAccount,
                      AndroidAccountDataPersist.ACCOUNT_ACCESS_CONFIRMED))));
    }));
  }

  @Override public Completable removeAccount() {
    return getAndroidAccount().doOnSuccess(androidAccount -> {
      if (Build.VERSION.SDK_INT >= 22) {
        androidAccountManager.removeAccountExplicitly(androidAccount);
      } else {
        androidAccountManager.removeAccount(androidAccount, null, null);
      }
      accountCache = null;
    }).toCompletable();
  }

  private Single<android.accounts.Account> getAndroidAccount() {
    return Single.defer(() -> {
      final android.accounts.Account[] accounts =
          androidAccountManager.getAccountsByType(accountType);

      if (accounts.length == 0) {
        return Single.error(new IllegalStateException("No account found."));
      }
      return Single.just(accounts[0]);
    }).observeOn(Schedulers.from(Executors.newSingleThreadExecutor()));
  }
}

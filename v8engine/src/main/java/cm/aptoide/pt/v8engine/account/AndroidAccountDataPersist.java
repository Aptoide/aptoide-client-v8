package cm.aptoide.pt.v8engine.account;

import android.accounts.AccountManager;
import android.os.Build;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountDataPersist;
import cm.aptoide.accountmanager.AccountFactory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import java.util.concurrent.Executors;
import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;

public class AndroidAccountDataPersist implements AccountDataPersist {

  private static final String ACCESS = "access";
  private static final String ACCESS_CONFIRMED = "access_confirmed";
  private static final String MATURE_SWITCH = "aptoide_account_manager_mature_switch";
  private static final String LOGIN_MODE = "aptoide_account_manager_login_mode";
  private static final String ACCESS_TOKEN = "access_token";
  private static final String REFRESH_TOKEN = "refresh_token";
  private static final String USER_NICK_NAME = "username";
  private static final String QUEUE_NAME = "queueName";
  private static final String USER_AVATAR = "useravatar";
  private static final String USER_REPO = "userRepo";
  private static final String USER_ID = "userId";
  private static final String REPO_AVATAR = "storeAvatar";

  private final String accountType;
  private final AccountManager androidAccountManager;
  private final DatabaseStoreDataPersist storePersist;
  private final AccountFactory accountFactory;

  private Account accountCache;

  public AndroidAccountDataPersist(String accountType, AccountManager androidAccountManager,
      DatabaseStoreDataPersist storePersist, AccountFactory accountFactory) {
    this.accountType = accountType;
    this.androidAccountManager = androidAccountManager;
    this.storePersist = storePersist;
    this.accountFactory = accountFactory;
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

      androidAccountManager.setUserData(androidAccount, USER_ID, account.getId());
      androidAccountManager.setUserData(androidAccount, USER_NICK_NAME, account.getNickname());
      androidAccountManager.setUserData(androidAccount, USER_AVATAR, account.getAvatar());
      androidAccountManager.setUserData(androidAccount, REFRESH_TOKEN, account.getRefreshToken());
      androidAccountManager.setUserData(androidAccount, ACCESS_TOKEN, account.getToken());
      androidAccountManager.setUserData(androidAccount, LOGIN_MODE, account.getType().name());
      androidAccountManager.setUserData(androidAccount, USER_REPO, account.getStore());
      androidAccountManager.setUserData(androidAccount, REPO_AVATAR, account.getStoreAvatar());
      androidAccountManager.setUserData(androidAccount, MATURE_SWITCH,
          String.valueOf(account.isAdultContentEnabled()));
      androidAccountManager.setUserData(androidAccount, ACCESS, account.getAccess().name());
      androidAccountManager.setUserData(androidAccount, ACCESS_CONFIRMED,
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
    return getAndroidAccount().flatMap(androidAccount -> {

      final String access =
          androidAccountManager.getUserData(androidAccount, AndroidAccountDataPersist.ACCESS);

      return storePersist.get()
          .doOnError(err -> CrashReport.getInstance().log(err))
          .map(stores -> accountFactory.createAccount(access, stores,
              androidAccountManager.getUserData(androidAccount, AndroidAccountDataPersist.USER_ID),
              androidAccount.name, androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.USER_NICK_NAME),
              androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.USER_AVATAR),
              androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.REFRESH_TOKEN),
              androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.ACCESS_TOKEN),
              androidAccountManager.getPassword(androidAccount), Account.Type.valueOf(
                  androidAccountManager.getUserData(androidAccount,
                      AndroidAccountDataPersist.LOGIN_MODE)),
              androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.USER_REPO),
              androidAccountManager.getUserData(androidAccount,
                  AndroidAccountDataPersist.REPO_AVATAR), Boolean.valueOf(
                  androidAccountManager.getUserData(androidAccount,
                      AndroidAccountDataPersist.MATURE_SWITCH)), Boolean.valueOf(
                  androidAccountManager.getUserData(androidAccount,
                      AndroidAccountDataPersist.ACCESS_CONFIRMED))));
    });
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
        Single.error(new IllegalStateException("No account found."));
      }
      return Single.just(accounts[0]);
    }).observeOn(Schedulers.from(Executors.newSingleThreadExecutor()));
  }
}

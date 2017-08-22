package cm.aptoide.pt.v8engine.account;

import android.accounts.AccountManager;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountDataPersist;
import cm.aptoide.accountmanager.AccountFactory;
import cm.aptoide.accountmanager.Store;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import rx.Completable;
import rx.Scheduler;
import rx.Single;

/**
 * Persists {@link Account} data in using {@link AccountManager}.
 */
public class AndroidAccountManagerDataPersist implements AccountDataPersist {

  public static final String ACCOUNT_ACCESS_LEVEL = "access";
  public static final String ACCOUNT_ACCESS_CONFIRMED = "access_confirmed";
  public static final String ACCOUNT_ADULT_CONTENT_ENABLED =
      "aptoide_account_manager_mature_switch";
  public static final String ACCOUNT_TYPE = "aptoide_account_manager_login_mode";
  public static final String ACCOUNT_ACCESS_TOKEN = "access_token";
  public static final String ACCOUNT_REFRESH_TOKEN = "refresh_token";
  public static final String ACCOUNT_NICKNAME = "username";
  public static final String ACCOUNT_AVATAR_URL = "useravatar";
  public static final String ACCOUNT_STORE_NAME = "userRepo";
  public static final String ACCOUNT_ID = "userId";
  public static final String ACCOUNT_STORE_AVATAR_URL = "storeAvatar";
  private static final String ACCOUNT_STORE_DOWNLOAD_COUNT = "account_store_download_count";
  private static final String ACCOUNT_STORE_ID = "account_store_id";
  private static final String ACCOUNT_STORE_THEME = "account_store_theme";
  private static final String ACCOUNT_STORE_USERNAME = "account_store_username";
  private static final String ACCOUNT_STORE_PASSWORD = "account_store_password";

  private final AccountManager androidAccountManager;
  private final DatabaseStoreDataPersist storePersist;
  private final AccountFactory accountFactory;
  private final AndroidAccountDataMigration accountDataMigration;
  private final AndroidAccountProvider androidAccountProvider;
  private final Scheduler scheduler;

  private Account accountCache;

  public AndroidAccountManagerDataPersist(AccountManager androidAccountManager,
      DatabaseStoreDataPersist storePersist, AccountFactory accountFactory,
      AndroidAccountDataMigration accountDataMigration,
      AndroidAccountProvider androidAccountProvider, Scheduler scheduler) {
    this.androidAccountManager = androidAccountManager;
    this.storePersist = storePersist;
    this.accountFactory = accountFactory;
    this.accountDataMigration = accountDataMigration;
    this.androidAccountProvider = androidAccountProvider;
    this.scheduler = scheduler;
  }

  @Override public Completable saveAccount(Account account) {
    return androidAccountProvider.getAndroidAccount()
        .onErrorResumeNext(
            throwable -> androidAccountProvider.createAndroidAccount(account.getEmail(),
                account.getPassword()))
        .flatMapCompletable(androidAccount -> {

          androidAccountManager.setUserData(androidAccount, ACCOUNT_ID, account.getId());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_NICKNAME,
              account.getNickname());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_AVATAR_URL,
              account.getAvatar());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_REFRESH_TOKEN,
              account.getRefreshToken());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_ACCESS_TOKEN,
              account.getAccessToken());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_TYPE, account.getType()
              .name());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_ADULT_CONTENT_ENABLED,
              String.valueOf(account.isAdultContentEnabled()));
          androidAccountManager.setUserData(androidAccount, ACCOUNT_ACCESS_LEVEL,
              account.getAccess()
                  .name());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_ACCESS_CONFIRMED,
              String.valueOf(account.isAccessConfirmed()));

          androidAccountManager.setUserData(androidAccount, ACCOUNT_STORE_NAME, account.getStore()
              .getName());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_STORE_AVATAR_URL,
              account.getStore()
                  .getAvatar());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_STORE_DOWNLOAD_COUNT,
              String.valueOf(account.getStore()
                  .getDownloadCount()));
          androidAccountManager.setUserData(androidAccount, ACCOUNT_STORE_ID, String.valueOf(
              account.getStore()
                  .getId()));
          androidAccountManager.setUserData(androidAccount, ACCOUNT_STORE_THEME, account.getStore()
              .getTheme());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_STORE_USERNAME,
              account.getStore()
                  .getUsername());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_STORE_PASSWORD,
              account.getStore()
                  .getPassword());

          return storePersist.persist(account.getSubscribedStores())
              .doOnCompleted(() -> {
                accountCache = account;
              });
        })
        .subscribeOn(scheduler);
  }

  @Override public Single<Account> getAccount() {
    if (accountCache != null) {
      return Single.just(accountCache);
    }
    return accountDataMigration.migrate()
        .andThen(androidAccountProvider.getAndroidAccount()
            .flatMap(androidAccount -> {

              final String access =
                  androidAccountManager.getUserData(androidAccount, ACCOUNT_ACCESS_LEVEL);

              return storePersist.get()
                  .doOnError(err -> CrashReport.getInstance()
                      .log(err))
                  .map(stores -> accountFactory.createAccount(access, stores,
                      androidAccountManager.getUserData(androidAccount, ACCOUNT_ID),
                      androidAccount.name,
                      androidAccountManager.getUserData(androidAccount, ACCOUNT_NICKNAME),
                      androidAccountManager.getUserData(androidAccount, ACCOUNT_AVATAR_URL),
                      androidAccountManager.getUserData(androidAccount, ACCOUNT_REFRESH_TOKEN),
                      androidAccountManager.getUserData(androidAccount, ACCOUNT_ACCESS_TOKEN),
                      androidAccountManager.getPassword(androidAccount), Account.Type.valueOf(
                          androidAccountManager.getUserData(androidAccount, ACCOUNT_TYPE)),
                      createStore(androidAccountManager, androidAccount), Boolean.valueOf(
                          androidAccountManager.getUserData(androidAccount,
                              ACCOUNT_ADULT_CONTENT_ENABLED)), Boolean.valueOf(
                          androidAccountManager.getUserData(androidAccount,
                              ACCOUNT_ACCESS_CONFIRMED))));
            }));
  }

  @Override public Completable removeAccount() {
    return androidAccountProvider.removeAndroidAccount()
        .doOnCompleted(() -> accountCache = null);
  }

  private Store createStore(AccountManager androidAccountManager,
      android.accounts.Account account) {
    return new Store(
        Long.valueOf(androidAccountManager.getUserData(account, ACCOUNT_STORE_DOWNLOAD_COUNT)),
        androidAccountManager.getUserData(account, ACCOUNT_STORE_AVATAR_URL),
        Long.valueOf(androidAccountManager.getUserData(account, ACCOUNT_STORE_ID)),
        androidAccountManager.getUserData(account, ACCOUNT_STORE_NAME),
        androidAccountManager.getUserData(account, ACCOUNT_STORE_THEME),
        androidAccountManager.getUserData(account, ACCOUNT_STORE_USERNAME),
        androidAccountManager.getUserData(account, ACCOUNT_STORE_PASSWORD), true);
  }
}

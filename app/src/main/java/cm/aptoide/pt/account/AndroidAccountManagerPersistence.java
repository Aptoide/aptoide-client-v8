package cm.aptoide.pt.account;

import android.accounts.AccountManager;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountFactory;
import cm.aptoide.accountmanager.AccountPersistence;
import cm.aptoide.accountmanager.Store;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import java.util.Date;
import rx.Completable;
import rx.Scheduler;
import rx.Single;

/**
 * Persists {@link Account} data in using {@link AccountManager}.
 */
public class AndroidAccountManagerPersistence implements AccountPersistence {

  public static final String ACCOUNT_ACCESS_LEVEL = "access";
  public static final String ACCOUNT_ACCESS_CONFIRMED = "access_confirmed";
  public static final String ACCOUNT_ADULT_CONTENT_ENABLED =
      "aptoide_account_manager_mature_switch";
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
  private static final String ACCOUNT_PRIVACY_POLICY = "account_privacy_policy";
  private static final String ACCOUNT_TERMS_AND_CONDITIONS = "account_terms_and_conditions";
  private static final String ACCOUNT_BIRTH_DATE = "account_birth_date";

  private final AccountManager androidAccountManager;
  private final DatabaseStoreDataPersist storePersist;
  private final AccountFactory accountFactory;
  private final AndroidAccountDataMigration accountDataMigration;
  private final AndroidAccountProvider androidAccountProvider;
  private final AuthenticationPersistence authenticationPersistence;
  private final Scheduler scheduler;

  private Account accountCache;

  public AndroidAccountManagerPersistence(AccountManager androidAccountManager,
      DatabaseStoreDataPersist storePersist, AccountFactory accountFactory,
      AndroidAccountDataMigration accountDataMigration,
      AndroidAccountProvider androidAccountProvider,
      AuthenticationPersistence authenticationPersistence, Scheduler scheduler) {
    this.androidAccountManager = androidAccountManager;
    this.storePersist = storePersist;
    this.accountFactory = accountFactory;
    this.accountDataMigration = accountDataMigration;
    this.androidAccountProvider = androidAccountProvider;
    this.authenticationPersistence = authenticationPersistence;
    this.scheduler = scheduler;
  }

  @Override public Completable saveAccount(Account account) {
    return androidAccountProvider.getAndroidAccount()
        .flatMapCompletable(androidAccount -> {

          androidAccountManager.setUserData(androidAccount, ACCOUNT_ID, account.getId());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_NICKNAME,
              account.getNickname());
          androidAccountManager.setUserData(androidAccount, ACCOUNT_AVATAR_URL,
              account.getAvatar());
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
          androidAccountManager.setUserData(androidAccount, ACCOUNT_TERMS_AND_CONDITIONS,
              String.valueOf(account.acceptedTermsAndConditions()));
          androidAccountManager.setUserData(androidAccount, ACCOUNT_PRIVACY_POLICY,
              String.valueOf(account.acceptedPrivacyPolicy()));
          androidAccountManager.setUserData(androidAccount, ACCOUNT_BIRTH_DATE,
              account.getBirthDate()
                  .toLocaleString());

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
              final boolean terms =
                  androidAccountManager.getUserData(androidAccount, ACCOUNT_TERMS_AND_CONDITIONS)
                      != null ? Boolean.valueOf(androidAccountManager.getUserData(androidAccount,
                      ACCOUNT_TERMS_AND_CONDITIONS)) : false;
              final boolean privacy =
                  androidAccountManager.getUserData(androidAccount, ACCOUNT_PRIVACY_POLICY) != null
                      ? Boolean.valueOf(
                      androidAccountManager.getUserData(androidAccount, ACCOUNT_PRIVACY_POLICY))
                      : false;
              final Date birthdate =
                  androidAccountManager.getUserData(androidAccount, ACCOUNT_BIRTH_DATE) != null
                      ? new Date(
                      androidAccountManager.getUserData(androidAccount, ACCOUNT_BIRTH_DATE))
                      : new Date(1970, 1, 1);
              return storePersist.get()
                  .doOnError(err -> CrashReport.getInstance()
                      .log(err))
                  .flatMap(stores -> {

                    return authenticationPersistence.getAuthentication()
                        .flatMap(authentication -> {

                          if (authentication.isAuthenticated()) {

                            return Single.just(accountFactory.createAccount(access, stores,
                                androidAccountManager.getUserData(androidAccount, ACCOUNT_ID),
                                androidAccount.name,
                                androidAccountManager.getUserData(androidAccount, ACCOUNT_NICKNAME),
                                androidAccountManager.getUserData(androidAccount,
                                    ACCOUNT_AVATAR_URL), createStore(androidAccount),
                                Boolean.valueOf(androidAccountManager.getUserData(androidAccount,
                                    ACCOUNT_ADULT_CONTENT_ENABLED)), Boolean.valueOf(
                                    androidAccountManager.getUserData(androidAccount,
                                        ACCOUNT_ACCESS_CONFIRMED)), privacy, terms, birthdate));
                          }

                          return Single.error(
                              new IllegalStateException("Account not authenticated"));
                        });
                  });
            }));
  }

  @Override public Completable removeAccount() {
    return androidAccountProvider.removeAndroidAccount()
        .doOnCompleted(() -> accountCache = null);
  }

  private Store createStore(android.accounts.Account account) {
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

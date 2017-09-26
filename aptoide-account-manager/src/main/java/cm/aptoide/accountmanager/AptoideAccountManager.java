/*
 * Copyright (c) 2016.
 * Modified on 27/06/2016.
 */

package cm.aptoide.accountmanager;

import android.text.TextUtils;
import cm.aptoide.pt.crashreports.CrashReport;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.HashMap;
import java.util.Map;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class AptoideAccountManager {

  public static final String APTOIDE_SIGN_UP_TYPE = "APTOIDE";
  private final CredentialsValidator credentialsValidator;
  private final PublishRelay<Account> accountRelay;
  private final SignUpAdapterRegistry adapterRegistry;
  private final AccountPersistence accountPersistence;
  private final AccountService accountService;

  private AptoideAccountManager(AccountAnalytics accountAnalytics,
      CredentialsValidator credentialsValidator, AccountPersistence accountPersistence,
      AccountService accountService, PublishRelay<Account> accountRelay,
      SignUpAdapterRegistry adapterRegistry) {
    this.credentialsValidator = credentialsValidator;
    this.accountPersistence = accountPersistence;
    this.accountService = accountService;
    this.accountRelay = accountRelay;
    this.adapterRegistry = adapterRegistry;
  }

  public Observable<Account> accountStatus() {
    return Observable.merge(accountRelay, accountPersistence.getAccount()
        .onErrorReturn(throwable -> createLocalAccount())
        .toObservable());
  }

  private Single<Account> singleAccountStatus() {
    return accountStatus().first()
        .toSingle();
  }

  private Account createLocalAccount() {
    return new LocalAccount(Store.emptyStore());
  }

  /**
   * Use {@link Account#isLoggedIn()} instead.
   *
   * @return true if user is logged in, false otherwise.
   */
  @Deprecated public boolean isLoggedIn() {
    final Account account = getAccount();
    return account != null && account.isLoggedIn();
  }

  /**
   * Use {@link #accountStatus()} method instead.
   *
   * @return user Account
   */
  @Deprecated public Account getAccount() {
    return singleAccountStatus().onErrorReturn(throwable -> null)
        .toBlocking()
        .value();
  }

  public Completable logout() {
    return adapterRegistry.logoutAll()
        .andThen(singleAccountStatus().flatMapCompletable(account -> accountService.removeAccount())
            .andThen(accountPersistence.removeAccount())
            .doOnCompleted(() -> accountRelay.call(createLocalAccount())));
  }

  public Completable login(AptoideCredentials credentials) {
    return credentialsValidator.validate(credentials, false)
        .andThen(accountService.getAccount(credentials.getEmail(), credentials.getPassword()))
        .flatMapCompletable(account -> saveAccount(account));
  }

  public <T> Completable signUp(String type, T data) {
    return adapterRegistry.signUp(type, data)
        .flatMapCompletable(account -> saveAccount(account));
  }

  public boolean isSignUpEnabled(String type) {
    return adapterRegistry.isEnabled(type);
  }

  private Completable syncAccount() {
    return accountService.getAccount()
        .flatMapCompletable(account -> saveAccount(account));
  }

  private Completable saveAccount(Account account) {
    return accountPersistence.saveAccount(account)
        .doOnCompleted(() -> accountRelay.call(account));
  }

  public void unsubscribeStore(String storeName, String storeUserName, String storePassword) {
    accountService.unsubscribeStore(storeName, storeUserName, storePassword)
        .subscribe(() -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
  }

  public Completable subscribeStore(String storeName, String storeUserName, String storePassword) {
    return accountService.subscribeStore(storeName, storeUserName, storePassword);
  }

  /**
   * Use {@link Account#isAdultContentEnabled()} instead.
   *
   * @return true if adult content enabled, false otherwise.
   */
  @Deprecated public boolean isAccountMature() {
    final Account account = getAccount();
    return account != null && account.isAdultContentEnabled();
  }

  /**
   * Use {@link Account#isAccessConfirmed()} instead.
   *
   * @return true if user {@link Account.Access} level is confirmed, false otherwise.
   */
  @Deprecated public boolean isAccountAccessConfirmed() {
    final Account account = getAccount();
    return account != null && account.isAccessConfirmed();
  }

  public Completable updateAccount(boolean adultContentEnabled) {
    return singleAccountStatus().flatMapCompletable(
        account -> accountService.updateAccount(adultContentEnabled)
            .andThen(syncAccount()));
  }

  public Completable updateAccount(String username) {
    if (TextUtils.isEmpty(username)) {
      return Completable.error(
          new AccountValidationException(AccountValidationException.EMPTY_NAME));
    }
    return singleAccountStatus().flatMapCompletable(
        account -> accountService.updateAccountUsername(username)
            .andThen(syncAccount()));
  }

  public Completable updateAccount(Account.Access access) {
    return singleAccountStatus().flatMapCompletable(
        account -> accountService.updateAccount(access.name())
            .andThen(syncAccount()));
  }

  public Completable updateAccount(String username, String avatarPath) {
    return singleAccountStatus().flatMapCompletable(account -> {
      if (TextUtils.isEmpty(username) && TextUtils.isEmpty(avatarPath)) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.EMPTY_NAME_AND_AVATAR));
      } else if (TextUtils.isEmpty(username)) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.EMPTY_NAME));
      }
      return accountService.updateAccount(username, TextUtils.isEmpty(avatarPath) ? "" : avatarPath)
          .andThen(syncAccount());
    });
  }

  @Deprecated public Completable updateAccount() {
    return singleAccountStatus().flatMapCompletable(account -> syncAccount());
  }

  public static class Builder {

    private final Map<String, SignUpAdapter> adapters;
    private CredentialsValidator credentialsValidator;
    private AccountService accountService;
    private PublishRelay<Account> accountRelay;
    private AccountAnalytics accountAnalytics;
    private AccountPersistence accountPersistence;

    public Builder() {
      this.adapters = new HashMap<>();
    }

    public Builder setCredentialsValidator(CredentialsValidator credentialsValidator) {
      this.credentialsValidator = credentialsValidator;
      return this;
    }

    public Builder setAccountService(AccountService accountService) {
      this.accountService = accountService;
      return this;
    }

    public Builder setAccountRelay(PublishRelay<Account> accountRelay) {
      this.accountRelay = accountRelay;
      return this;
    }

    public Builder setAccountAnalytics(AccountAnalytics accountAnalytics) {
      this.accountAnalytics = accountAnalytics;
      return this;
    }

    public Builder setAccountPersistence(AccountPersistence accountPersistence) {
      this.accountPersistence = accountPersistence;
      return this;
    }

    public Builder registerSignUpAdapter(String type, SignUpAdapter signUpAdapter) {
      adapters.put(type, signUpAdapter);
      return this;
    }

    public AptoideAccountManager build() {

      if (accountAnalytics == null) {
        throw new IllegalArgumentException("AccountAnalytics is mandatory.");
      }

      if (accountPersistence == null) {
        throw new IllegalArgumentException("AccountDataPersist is mandatory.");
      }

      if (accountService == null) {
        throw new IllegalArgumentException("AccountManagerService is mandatory.");
      }

      if (credentialsValidator == null) {
        credentialsValidator = new CredentialsValidator();
      }

      if (accountRelay == null) {
        accountRelay = PublishRelay.create();
      }

      final SignUpAdapterRegistry adapterRegistry =
          new SignUpAdapterRegistry(adapters, accountService);

      adapterRegistry.register(APTOIDE_SIGN_UP_TYPE,
          new AptoideSignUpAdapter(credentialsValidator, accountAnalytics));

      return new AptoideAccountManager(accountAnalytics, credentialsValidator, accountPersistence,
          accountService, accountRelay, adapterRegistry);
    }
  }
}

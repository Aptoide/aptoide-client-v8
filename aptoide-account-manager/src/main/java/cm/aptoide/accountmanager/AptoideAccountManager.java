/*
 * Copyright (c) 2016.
 * Modified on 27/06/2016.
 */

package cm.aptoide.accountmanager;

import android.text.TextUtils;
import cm.aptoide.pt.crashreports.CrashReport;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.HashMap;
import java.util.List;
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
  private final StoreManager storeManager;
  private final AdultContent adultContent;

  private AptoideAccountManager(CredentialsValidator credentialsValidator,
      AccountPersistence accountPersistence, AccountService accountService,
      PublishRelay<Account> accountRelay, SignUpAdapterRegistry adapterRegistry,
      StoreManager storeManager, AdultContent adultContent) {
    this.credentialsValidator = credentialsValidator;
    this.accountPersistence = accountPersistence;
    this.accountService = accountService;
    this.accountRelay = accountRelay;
    this.adapterRegistry = adapterRegistry;
    this.storeManager = storeManager;
    this.adultContent = adultContent;
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

  /**
   * Updates the server account status with the latest adult content enabled option (true or false).
   * Does not sync the account locally since the server would return the old switch value.
   */
  public Completable updateAccount(boolean adultContentEnabled) {
    return singleAccountStatus().flatMapCompletable(
        account -> accountService.updateAccount(adultContentEnabled));
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

  public Completable changeBirthdayDate(String birthdate) {
    return accountService.changeBirthdate(birthdate)
        .andThen(syncAccount());
  }

  public Completable updateTermsAndConditions() {
    return accountService.updateTermsAndConditions()
        .andThen(accountStatus())
        .flatMapCompletable(account -> accountPersistence.saveAccount(
            new AptoideAccount(account.getId(), account.getEmail(), account.getNickname(),
                account.getAvatar(), account.getStore(), account.isAdultContentEnabled(),
                account.getAccess(), account.isAccessConfirmed(), account.getSubscribedStores(),
                true, true, account.getBirthDate())))
        .toCompletable();
  }

  public Completable changeSubscribeNewsletter(boolean isSubscribed) {
    if (isSubscribed) {
      return accountService.changeSubscribeNewsletter("1");
    } else {
      return accountService.changeSubscribeNewsletter("0");
    }
  }

  public Observable<Boolean> pinRequired() {
    return adultContent.pinRequired();
  }

  public Completable requirePin(int pin) {
    return adultContent.requirePin(pin);
  }

  public Completable removePin(int pin) {
    return adultContent.removePin(pin);
  }

  public Completable enable() {
    return accountStatus().first()
        .flatMapCompletable(account -> adultContent.enable(account.isLoggedIn()))
        .toCompletable();
  }

  public Completable disable() {
    return accountStatus().first()
        .flatMapCompletable(account -> adultContent.disable(account.isLoggedIn()))
        .toCompletable();
  }

  public Observable<Boolean> enabled() {
    return adultContent.enabled();
  }

  public Completable enable(int pin) {
    return adultContent.enable(pin);
  }

  @Deprecated public Completable updateAccount() {
    return singleAccountStatus().flatMapCompletable(account -> syncAccount());
  }

  public Completable createOrUpdate(String storeName, String storeDescription,
      String storeImagePath, boolean hasNewAvatar, String storeThemeName, boolean storeExists,
      List<SocialLink> storeLinksList,
      List<cm.aptoide.pt.dataprovider.model.v7.store.Store.SocialChannelType> storeDeleteLinksList) {
    return storeManager.createOrUpdate(storeName, storeDescription, storeImagePath, hasNewAvatar,
        storeThemeName, storeExists, storeLinksList, storeDeleteLinksList)
        .andThen(syncAccount());
  }

  public static class Builder {

    private final Map<String, SignUpAdapter> adapters;
    private CredentialsValidator credentialsValidator;
    private AccountService accountService;
    private PublishRelay<Account> accountRelay;
    private AccountPersistence accountPersistence;
    private StoreManager storeManager;
    private AdultContent adultContent;

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

    public Builder setAdultService(AdultContent adultContent) {
      this.adultContent = adultContent;
      return this;
    }

    public Builder setAccountRelay(PublishRelay<Account> accountRelay) {
      this.accountRelay = accountRelay;
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

    public Builder setStoreManager(StoreManager storeManager) {
      this.storeManager = storeManager;
      return this;
    }

    public AptoideAccountManager build() {

      if (accountPersistence == null) {
        throw new IllegalArgumentException("AccountDataPersist is mandatory.");
      }

      if (accountService == null) {
        throw new IllegalArgumentException("AccountManagerService is mandatory.");
      }

      if (storeManager == null) {
        throw new IllegalArgumentException("StoreManager is mandatory.");
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
          new AptoideSignUpAdapter(credentialsValidator));

      return new AptoideAccountManager(credentialsValidator, accountPersistence, accountService,
          accountRelay, adapterRegistry, storeManager, adultContent);
    }
  }
}

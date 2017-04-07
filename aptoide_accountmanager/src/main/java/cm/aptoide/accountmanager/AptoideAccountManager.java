/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/06/2016.
 */

package cm.aptoide.accountmanager;

import android.text.TextUtils;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import com.jakewharton.rxrelay.PublishRelay;
import java.net.SocketTimeoutException;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class AptoideAccountManager {

  public static final String IS_FACEBOOK_OR_GOOGLE = "facebook_google";

  private final AccountAnalytics accountAnalytics;
  private final CredentialsValidator credentialsValidator;
  private final PublishRelay<Account> accountRelay;
  private final AccountDataPersist dataPersist;
  private final AccountManagerService accountManagerService;

  private AptoideAccountManager(AccountAnalytics accountAnalytics,
      CredentialsValidator credentialsValidator, AccountDataPersist dataPersist,
      AccountManagerService accountManagerService, PublishRelay<Account> accountRelay) {
    this.credentialsValidator = credentialsValidator;
    this.accountAnalytics = accountAnalytics;
    this.dataPersist = dataPersist;
    this.accountManagerService = accountManagerService;
    this.accountRelay = accountRelay;
  }

  public Observable<Account> accountStatus() {
    return Observable.merge(accountRelay, dataPersist.getAccount().onErrorReturn(throwable -> {
      //CrashReport.getInstance().log(throwable);
      return createLocalAccount();
    }).toObservable());
  }

  private Single<Account> singleAccountStatus() {
    return accountStatus().first().toSingle();
  }

  private Account createLocalAccount() {
    return new LocalAccount();
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
    return singleAccountStatus().onErrorReturn(throwable -> null).toBlocking().value();
  }

  public Completable logout() {
    return singleAccountStatus().flatMapCompletable(
        account -> account.logout().andThen(removeAccount()));
  }

  public Completable removeAccount() {
    return dataPersist.removeAccount().doOnCompleted(() -> accountRelay.call(createLocalAccount()));
  }

  public Completable refreshToken() {
    return singleAccountStatus().flatMapCompletable(
        account -> account.refreshToken().andThen(saveAccount(account)));
  }

  private Completable saveAccount(Account account) {
    return dataPersist.saveAccount(account).doOnCompleted(() -> accountRelay.call(account));
  }

  public Completable signUp(String email, String password) {
    return credentialsValidator.validate(email, password, true)
        .andThen(accountManagerService.createAccount(email, password))
        .andThen(login(Account.Type.APTOIDE, email, password, null))
        .doOnCompleted(() -> accountAnalytics.signUp())
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof SocketTimeoutException) {
            return login(Account.Type.APTOIDE, email, password, null);
          }
          return Completable.error(throwable);
        });
  }

  public Completable login(Account.Type type, final String email, final String password,
      final String name) {
    return credentialsValidator.validate(email, password, false)
        .andThen(accountManagerService.login(type.name(), email, password, name))
        .flatMapCompletable(
            oAuth -> syncAccount(oAuth.getAccessToken(), oAuth.getRefreshToken(), password, type))
        .doOnCompleted(() -> accountAnalytics.login(email));
  }

  private Completable syncAccount(String accessToken, String refreshToken, String password,
      Account.Type type) {
    return accountManagerService.getAccount(accessToken, refreshToken, password, type.name(), this)
        .flatMapCompletable(account -> saveAccount(account));
  }

  public void unsubscribeStore(String storeName, String storeUserName, String storePassword) {
    accountManagerService.unsubscribeStore(storeName, storeUserName, storePassword, this)
        .subscribe(() -> {
        }, throwable -> CrashReport.getInstance().log(throwable));
  }

  public Completable subscribeStore(String storeName, String storeUserName, String storePassword) {
    return accountManagerService.subscribeStore(storeName, storeUserName, storePassword, this);
  }

  /**
   * Use {@link Account#getEmail()} instead.
   *
   * @return user e-mail.
   */
  @Deprecated public String getAccountEmail() {
    final Account account = getAccount();
    return account == null ? null : account.getEmail();
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
   * Use {@link Account#getAccess()} instead.
   *
   * @return user {@link Account.Access} level.
   */
  @Deprecated public Account.Access getAccountAccess() {
    return getAccount().getAccess();
  }

  public Completable syncCurrentAccount() {
    return singleAccountStatus().flatMapCompletable(
        account -> syncAccount(account.getAccessToken(), account.getRefreshToken(),
            account.getPassword(), account.getType()));
  }

  public Completable updateAccount(boolean adultContentEnabled) {
    return singleAccountStatus().flatMapCompletable(
        account -> accountManagerService.updateAccount(adultContentEnabled,
            account.getAccessToken())
            .andThen(syncAccount(account.getAccessToken(), account.getRefreshToken(),
                account.getPassword(), account.getType())));
  }

  public Completable updateAccount(Account.Access access) {
    return singleAccountStatus().flatMapCompletable(
        account -> accountManagerService.updateAccount(access.name(), this)
            .andThen(syncAccount(account.getAccessToken(), account.getRefreshToken(),
                account.getPassword(), account.getType())));
  }

  public Completable updateAccount(String nickname, String avatarPath) {
    return singleAccountStatus().flatMapCompletable(account -> {
      if (TextUtils.isEmpty(nickname) && TextUtils.isEmpty(avatarPath)) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.EMPTY_NAME_AND_AVATAR));
      } else if (TextUtils.isEmpty(nickname)) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.EMPTY_NAME));
      }
      return accountManagerService.updateAccount(account.getEmail(), nickname,
          account.getPassword(), TextUtils.isEmpty(avatarPath) ? "" : avatarPath,
          account.getAccessToken());
    });
  }

  /**
   * Use {@link Account#getAccessToken()} instead.
   *
   * @return account access token.
   */
  @Deprecated public String getAccessToken() {
    final Account account = getAccount();
    return account == null ? null : account.getAccessToken();
  }

  public static class Builder {

    private CredentialsValidator credentialsValidator;
    private AccountManagerService accountManagerService;
    private PublishRelay<Account> accountRelay;
    private AccountAnalytics accountAnalytics;
    private AccountDataPersist accountDataPersist;
    private AccountFactory accountFactory;

    private AptoideClientUUID aptoideClientUUID;
    private BasebBodyInterceptorFactory baseBodyInterceptorFactory;
    private ExternalAccountFactory externalAccountFactory;
    private AccountService accountService;

    public Builder setCredentialsValidator(CredentialsValidator credentialsValidator) {
      this.credentialsValidator = credentialsValidator;
      return this;
    }

    public Builder setAccountManagerService(AccountManagerService accountManagerService) {
      this.accountManagerService = accountManagerService;
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

    public Builder setAccountDataPersist(AccountDataPersist accountDataPersist) {
      this.accountDataPersist = accountDataPersist;
      return this;
    }

    public Builder setAptoideClientUUID(AptoideClientUUID aptoideClientUUID) {
      this.aptoideClientUUID = aptoideClientUUID;
      return this;
    }

    public Builder setBaseBodyInterceptorFactory(
        BasebBodyInterceptorFactory baseBodyInterceptorFactory) {
      this.baseBodyInterceptorFactory = baseBodyInterceptorFactory;
      return this;
    }

    public Builder setExternalAccountFactory(ExternalAccountFactory externalAccountFactory) {
      this.externalAccountFactory = externalAccountFactory;
      return this;
    }

    public Builder setAccountService(AccountService accountService) {
      this.accountService = accountService;
      return this;
    }

    public Builder setAccountFactory(AccountFactory accountFactory) {
      this.accountFactory = accountFactory;
      return this;
    }

    public AptoideAccountManager build() {

      if (accountAnalytics == null) {
        throw new IllegalArgumentException("AccountAnalytics is mandatory.");
      }

      if (accountDataPersist == null) {
        throw new IllegalArgumentException("AccountDataPersist is mandatory.");
      }

      if (accountManagerService == null) {

        if (aptoideClientUUID == null) {
          throw new IllegalArgumentException(
              "AptoideClientUUID is mandatory if AccountManagerService is not provided.");
        }

        if (baseBodyInterceptorFactory == null) {
          throw new IllegalArgumentException("BasebBodyInterceptorFactory is mandatory if "
              + "AccountManagerService is not provided.");
        }

        if (accountFactory == null) {

          if (externalAccountFactory == null) {
            throw new IllegalArgumentException(
                "ExternalAccountFactory is mandatory if AccountFactory is not provided.");
          }

          if (accountService == null) {
            this.accountService =
                new AccountService(aptoideClientUUID, baseBodyInterceptorFactory.createV3());
          }

          this.accountFactory =
              new AccountFactory(aptoideClientUUID, externalAccountFactory, accountService);
        }

        accountManagerService =
            new AccountManagerService(aptoideClientUUID, baseBodyInterceptorFactory,
                accountFactory);
      }

      if (credentialsValidator == null) {
        credentialsValidator = new CredentialsValidator();
      }

      if (accountRelay == null) {
        accountRelay = PublishRelay.create();
      }

      return new AptoideAccountManager(accountAnalytics, credentialsValidator, accountDataPersist,
          accountManagerService, accountRelay);
    }
  }
}

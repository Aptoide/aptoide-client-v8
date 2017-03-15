/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/06/2016.
 */

package cm.aptoide.accountmanager;

import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.ws.v3.ChangeUserSettingsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CheckUserCredentialsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CreateUserRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetUserRepoSubscriptionRequest;
import cm.aptoide.pt.dataprovider.ws.v3.OAuth2AuthenticationRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.dataprovider.ws.v7.ChangeStoreSubscriptionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v3.CheckUserCredentialsJson;
import cm.aptoide.pt.model.v3.Subscription;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.rxrelay.PublishRelay;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AptoideAccountManager {

  public static final String IS_FACEBOOK_OR_GOOGLE = "facebook_google";
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
  private final static String TAG = AptoideAccountManager.class.getSimpleName();

  private final String accountType;
  private final AptoideClientUUID aptoideClientUUID;
  private final Context applicationContext;
  private final AccountManager androidAccountManager;
  private final LoginAvailability loginAvailability;
  private final AccountRequestFactory requestFactory;
  private final Analytics analytics;
  private final StoreDataPersist storeDataPersist;
  private final AptoidePreferencesConfiguration configuration;
  private PublishRelay<Boolean> loginStatusRelay;
  private WeakReference<Account> weakRefAccount;

  public AptoideAccountManager(Context applicationContext,
      AptoidePreferencesConfiguration configuration, AccountManager androidAccountManager,
      AptoideClientUUID aptoideClientUUID, LoginAvailability loginAvailability, Analytics analytics,
      String accountType, AccountRequestFactory requestFactory, StoreDataPersist storeDataPersist) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.applicationContext = applicationContext;
    this.configuration = configuration;
    this.loginAvailability = loginAvailability;
    this.androidAccountManager = androidAccountManager;
    this.analytics = analytics;
    this.accountType = accountType;
    this.storeDataPersist = storeDataPersist;
    weakRefAccount = new WeakReference<>(null);
    loginStatusRelay = PublishRelay.create();
    this.requestFactory = requestFactory;
  }

  public Observable<Boolean> loginStatus() {
    return loginStatusRelay.startWith(isLoggedIn()).distinctUntilChanged();
  }

  /**
   * Use {@link Account#isLoggedIn()} instead.
   * @return true if user is logged in, false otherwise.
   */
  @Deprecated public boolean isLoggedIn() {
    final Account account = getAccount();
    return account != null && account.isLoggedIn();
  }

  /**
   * Use {@link #getAccountAsync()} method instead.
   *
   * @return user Account
   */
  @Deprecated public Account getAccount() {
    Account account = weakRefAccount.get();
    if (account != null) {
      return account;
    }

    return getAccountAsync().onErrorReturn(throwable -> null).toBlocking().value();
  }

  public Single<Account> getAccountAsync() {
    Account account = weakRefAccount.get();
    if (account != null) {
      return Single.just(account);
    }

    return getAndroidAccountAsync().flatMap(androidAccount -> {

      final Account.Access access =
          getAccessFrom(androidAccountManager.getUserData(androidAccount, ACCESS));

      return storeDataPersist.get()
          .doOnSuccess(stores -> {
            Logger.d("AptoideAccountManager", "nr stores= " + (stores != null ? stores.size() : 0));
          })
          .doOnError(err -> CrashReport.getInstance().log(err))
          .map(stores -> new Account(androidAccountManager.getUserData(androidAccount, USER_ID),
              androidAccount.name,
              androidAccountManager.getUserData(androidAccount, USER_NICK_NAME),
              androidAccountManager.getUserData(androidAccount, USER_AVATAR),
              androidAccountManager.getUserData(androidAccount, REFRESH_TOKEN),
              androidAccountManager.getUserData(androidAccount, ACCESS_TOKEN),
              androidAccountManager.getPassword(androidAccount),
              Account.Type.valueOf(androidAccountManager.getUserData(androidAccount, LOGIN_MODE)),
              androidAccountManager.getUserData(androidAccount, USER_REPO),
              androidAccountManager.getUserData(androidAccount, REPO_AVATAR),
              Boolean.valueOf(androidAccountManager.getUserData(androidAccount, MATURE_SWITCH)),
              access,
              Boolean.valueOf(androidAccountManager.getUserData(androidAccount, ACCESS_CONFIRMED)),
              stores));
    });
  }

  private Single<android.accounts.Account> getAndroidAccountAsync() {
    return Single.defer(() -> {
      try {
        return Single.just(getAndroidAccount());
      } catch (IllegalStateException e) {
        return Single.error(e);
      }
    }).observeOn(Schedulers.from(Executors.newSingleThreadExecutor()));
  }

  @NonNull private Account.Access getAccessFrom(String serverAccess) {
    return TextUtils.isEmpty(serverAccess) ? Account.Access.UNLISTED
        : Account.Access.valueOf(serverAccess.toUpperCase());
  }

  private android.accounts.Account getAndroidAccount() throws IllegalStateException {
    final android.accounts.Account[] accounts =
        androidAccountManager.getAccountsByType(accountType);

    if (accounts.length == 0) {
      throw new IllegalStateException("No account found.");
    }
    return accounts[0];
  }

  public void logout(GoogleApiClient client) {
    try {
      if (isFacebookLoginEnabled()) {
        FacebookSdk.sdkInitialize(applicationContext);
        LoginManager.getInstance().logOut();
      }
      if (isGoogleLoginEnabled()) {
        if (client != null && client.isConnected()) {
          Auth.GoogleSignInApi.signOut(client);
        }
        client.disconnect();
      }
    } catch (Exception e) {
      CrashReport.getInstance().log(e);
    }
    removeAccount();
    sendLoginEvent(false);
  }

  public boolean isFacebookLoginEnabled() {
    return loginAvailability.isFacebookLoginAvailable();
  }

  public boolean isGoogleLoginEnabled() {
    return loginAvailability.isGoogleLoginAvailable();
  }

  public void removeAccount() {
    getAndroidAccountAsync().doOnSuccess(androidAccount -> {
      if (Build.VERSION.SDK_INT >= 22) {
        androidAccountManager.removeAccountExplicitly(androidAccount);
      } else {
        androidAccountManager.removeAccount(androidAccount, null, null);
      }
      weakRefAccount = new WeakReference<>(null);
    }).onErrorReturn(throwable -> null).toBlocking().value();
  }

  /**
   * This method uses a 200 millis delay due to other bugs in the app (such as improper component
   * lifecycle in most fragments). After the proper component lifecycle is done, remove this delay.
   *
   * @param isLoggedIn boolean that indicates the current state of the user log in. This will
   * propagate to all listeners of {@link #loginStatus()}
   */
  private void sendLoginEvent(boolean isLoggedIn) {
    Observable.timer(650, TimeUnit.MILLISECONDS)
        .doOnNext(__ -> loginStatusRelay.call(isLoggedIn))
        .subscribe();

    //loginStatusRelay.call(isLoggedIn);
  }

  public Completable refreshAccountToken() {
    return getAccountAsync().flatMapCompletable(account -> refreshToken(account));
  }

  private Completable refreshToken(Account account) {
    return OAuth2AuthenticationRequest.of(account.getRefreshToken(),
        aptoideClientUUID.getUniqueIdentifier())
        .observe()
        .subscribeOn(Schedulers.io())
        .toSingle()
        .flatMapCompletable(oAuth -> {
          if (!oAuth.hasErrors()) {
            return saveAccount(
                new Account(account.getId(), account.getEmail(), account.getNickname(),
                    account.getAvatar(), account.getRefreshToken(), oAuth.getAccessToken(),
                    account.getPassword(), account.getType(), account.getStore(),
                    account.getStoreAvatar(), account.isAdultContentEnabled(), account.getAccess(),
                    account.isAccessConfirmed(), account.getSubscribedStores()));
          } else {
            return Completable.error(new AccountException(oAuth.getError()));
          }
        });
  }

  private Completable saveAccount(Account account) {
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

      weakRefAccount = new WeakReference<>(account);

      return Completable.complete();
    }).subscribeOn(Schedulers.io());
  }

  public Completable createAccount(String email, String password) {
    return validateCredentials(email, password, true).andThen(
        CreateUserRequest.of(email.toLowerCase(), password, aptoideClientUUID.getUniqueIdentifier())
            .observe(true)).toSingle().flatMapCompletable(response -> {
      if (response.hasErrors()) {
        return Completable.error(new AccountException(response.getErrors()));
      }
      return login(Account.Type.APTOIDE, email, password, null);
    }).doOnCompleted(() -> analytics.signUp()).onErrorResumeNext(throwable -> {
      if (throwable instanceof SocketTimeoutException) {
        return login(Account.Type.APTOIDE, email, password, null);
      }

      if (throwable instanceof AptoideWsV3Exception) {
        return Completable.error(
            new AccountException(((AptoideWsV3Exception) throwable).getBaseResponse().getError()));
      }

      return Completable.error(throwable);
    }).doOnCompleted(() -> analytics.login(email)).doOnCompleted(() -> sendLoginEvent(true));
  }

  private Completable validateCredentials(String email, String password, boolean validatePassword) {
    return Completable.defer(() -> {
      if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.EMPTY_EMAIL_AND_PASSWORD));
      } else if (TextUtils.isEmpty(password)) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.EMPTY_PASSWORD));
      } else if (TextUtils.isEmpty(email)) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.EMPTY_EMAIL));
      } else if (validatePassword && password.length() < 8 || !has1number1letter(password)) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.INVALID_PASSWORD));
      }
      return Completable.complete();
    });
  }

  public Completable login(Account.Type type, final String email, final String password,
      final String name) {
    return validateCredentials(email, password, false).andThen(
        OAuth2AuthenticationRequest.of(email, password, type.name(), name,
            aptoideClientUUID.getUniqueIdentifier())
            .observe()
            .toSingle()
            .flatMapCompletable(oAuth -> {
              if (!oAuth.hasErrors()) {
                return syncAccount(oAuth.getAccessToken(), oAuth.getRefreshToken(), password, type);
              } else {
                return Completable.error(new AccountException(oAuth.getError()));
              }
            })).onErrorResumeNext(throwable -> {
      if (throwable instanceof AptoideWsV3Exception) {
        return Completable.error(
            new AccountException(((AptoideWsV3Exception) throwable).getBaseResponse().getError()));
      }

      return Completable.error(throwable);
    }).doOnCompleted(() -> analytics.login(email)).doOnCompleted(() -> sendLoginEvent(true));
  }

  private boolean has1number1letter(String password) {
    boolean hasLetter = false;
    boolean hasNumber = false;

    for (char c : password.toCharArray()) {
      if (!hasLetter && Character.isLetter(c)) {
        if (hasNumber) return true;
        hasLetter = true;
      } else if (!hasNumber && Character.isDigit(c)) {
        if (hasLetter) return true;
        hasNumber = true;
      }
    }
    if (password.contains("!")
        || password.contains("@")
        || password.contains("#")
        || password.contains("$")
        || password.contains("#")
        || password.contains("*")) {
      hasNumber = true;
    }

    return hasNumber && hasLetter;
  }

  private Completable syncAccount(String accessToken, String refreshToken, String encryptedPassword,
      Account.Type type) {
    return Single.zip(getServerAccount(accessToken), getSubscribedStores(accessToken),
        (response, stores) -> mapServerAccountToAccount(response, refreshToken, accessToken,
            encryptedPassword, type, stores)).flatMapCompletable(account -> saveAccount(account));
  }

  private Single<CheckUserCredentialsJson> getServerAccount(String accessToken) {
    return CheckUserCredentialsRequest.of(accessToken).observe().toSingle().flatMap(response -> {
      if (response.getStatus().equals("OK")) {
        return Single.just(response);
      }
      return Single.error(new IllegalStateException("Failed to get user account"));
    });
  }

  private Single<List<Store>> getSubscribedStores(String accessToken) {
    return GetUserRepoSubscriptionRequest.of(accessToken)
        .observe()
        .observeOn(AndroidSchedulers.mainThread())
        .map(getUserRepoSubscription -> getUserRepoSubscription.getSubscription())
        .flatMapIterable(list -> list)
        .map(store -> mapToStore(store))
        .toList()
        .toSingle();
  }

  private Account mapServerAccountToAccount(CheckUserCredentialsJson serverUser,
      String refreshToken, String accessToken, String encryptedPassword, Account.Type accountType,
      List<Store> subscribedStores) {
    return new Account(String.valueOf(serverUser.getId()), serverUser.getEmail(),
        serverUser.getUsername(), serverUser.getAvatar(), refreshToken, accessToken,
        encryptedPassword, accountType, serverUser.getRepo(), serverUser.getRavatarHd(),
        serverUser.getSettings().getMatureswitch().equals("active"),
        getAccessFrom(serverUser.getAccess()), serverUser.isAccessConfirmed(), subscribedStores);
  }

  private Store mapToStore(Subscription subscription) {
    Store store = new Store(Long.parseLong(subscription.getDownloads()),
        subscription.getAvatarHd() != null ? subscription.getAvatarHd() : subscription.getAvatar(),
        subscription.getId().longValue(), subscription.getName(), subscription.getTheme(), null,
        null);
    return store;
  }

  /**
   * Use {@link Account#getToken()} instead.
   * @return account access token.
   */
  @Deprecated public String getAccessToken() {
    final Account account = getAccount();
    return account == null ? null : account.getToken();
  }

  public void unsubscribeStore(String storeName, String storeUserName, String storePassword) {
    changeSubscription(storeName, storeUserName, storePassword,
        ChangeStoreSubscriptionResponse.StoreSubscriptionState.UNSUBSCRIBED).subscribe(success -> {
    }, throwable -> CrashReport.getInstance().log(throwable));
  }

  private Observable<ChangeStoreSubscriptionResponse> changeSubscription(String storeName,
      String storeUserName, String sha1Password,
      ChangeStoreSubscriptionResponse.StoreSubscriptionState subscription) {
    return requestFactory.createChangeStoreSubscription(storeName, storeUserName, sha1Password,
        subscription, this).observe();
  }

  public Completable subscribeStore(String storeName, String storeUserName, String storePassword) {
    return changeSubscription(storeName, storeUserName, storePassword,
        ChangeStoreSubscriptionResponse.StoreSubscriptionState.SUBSCRIBED).toCompletable();
  }

  /**
   * Use {@link Account#getEmail()} instead.
   * @return user e-mail.
   */
  @Deprecated public String getUserEmail() {
    final Account account = getAccount();
    return account == null ? null : account.getEmail();
  }

  /**
   * Use {@link Account#isAdultContentEnabled()} instead.
   * @return true if adult content enabled, false otherwise.
   */
  @Deprecated public boolean isAccountMature() {
    final Account account = getAccount();
    return account == null ? false : account.isAdultContentEnabled();
  }

  /**
   * Use {@link Account#isAccessConfirmed()} instead.
   * @return true if user {@link Account.Access} level is confirmed, false otherwise.
   */
  @Deprecated public boolean isAccountAccessConfirmed() {
    final Account account = getAccount();
    return account == null ? false : account.isAccessConfirmed();
  }

  /**
   * Use {@link Account#getAccess()} instead.
   * @return user {@link Account.Access} level.
   */
  @Deprecated public Account.Access getAccountAccess() {
    return getAccount().getAccess();
  }

  public Completable syncCurrentAccount() {
    return getAccountAsync().flatMapCompletable(
        account -> syncAccount(account.getToken(), account.getRefreshToken(), account.getPassword(),
            account.getType()));
  }

  public Completable updateAccount(boolean adultContentEnabled) {
    return getAccountAsync().flatMapCompletable(account -> {
      return ChangeUserSettingsRequest.of(adultContentEnabled, account.getToken())
          .observe(true)
          .toSingle()
          .flatMapCompletable(response -> {
            if (response.getStatus().equals("OK")) {
              return syncAccount(account.getToken(), account.getRefreshToken(),
                  account.getPassword(), account.getType());
            } else {
              return Completable.error(new Exception(V3.getErrorMessage(response)));
            }
          });
    });
  }

  public Completable updateAccount(Account.Access access) {
    return getAccountAsync().flatMapCompletable(account -> {
      return requestFactory.createSetUser(access.name(), this)
          .observe(true)
          .toSingle()
          .flatMapCompletable(response -> {
            if (response.isOk()) {
              return syncAccount(account.getToken(), account.getRefreshToken(),
                  account.getPassword(), account.getType());
            } else {
              return Completable.error(new Exception(V7.getErrorMessage(response)));
            }
          });
    });
  }

  public Completable updateAccount(String nickname, String avatarPath) {
    return getAccountAsync().flatMapObservable(account -> {
      if (TextUtils.isEmpty(nickname)) {
        return Observable.error(
            new AccountValidationException(AccountValidationException.EMPTY_NAME));
      }
      return CreateUserRequest.of(account.getEmail(), nickname, account.getPassword(),
          (TextUtils.isEmpty(avatarPath) ? "" : avatarPath),
          aptoideClientUUID.getUniqueIdentifier(), getAccessToken()).observe(true);
    }).flatMap(response -> {
      if (!response.hasErrors()) {
        return Observable.just(response);
      } else {
        return Observable.error(new AccountException(response.getErrors()));
      }
    }).toCompletable();
  }
}

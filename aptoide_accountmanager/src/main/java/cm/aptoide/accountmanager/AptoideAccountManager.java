/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/06/2016.
 */

package cm.aptoide.accountmanager;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.ws.v3.ChangeUserSettingsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CheckUserCredentialsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CreateUserRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetUserRepoSubscriptionRequest;
import cm.aptoide.pt.dataprovider.ws.v3.OAuth2AuthenticationRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.model.v3.CheckUserCredentialsJson;
import cm.aptoide.pt.model.v3.Subscription;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.SetUserRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Executors;
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

  public static String LOGIN;
  public static String LOGIN_CANCELLED;
  public static String LOGOUT;

  private final String accountType;
  private final AptoideClientUUID aptoideClientUUID;
  private final Context applicationContext;
  private final AptoidePreferencesConfiguration configuration;
  private final AccountManager androidAccountManager;
  private final LoginAvailability loginAvailability;

  private final Analytics analytics;
  private FollowStoreService followStoreService;
  private final BodyInterceptor bodyInterceptor;

  public AptoideAccountManager(Context applicationContext,
      AptoidePreferencesConfiguration configuration, AccountManager androidAccountManager,
      AptoideClientUUID aptoideClientUUID, LoginAvailability loginAvailability, Analytics analytics,
      BodyInterceptor baseBodyInterceptor, String accountType,
      FollowStoreService followStoreService) {
    this.bodyInterceptor = baseBodyInterceptor;
    this.aptoideClientUUID = aptoideClientUUID;
    this.applicationContext = applicationContext;
    this.configuration = configuration;
    this.loginAvailability = loginAvailability;
    this.androidAccountManager = androidAccountManager;
    this.analytics = analytics;
    this.accountType = accountType;
    LOGIN = configuration.getAppId() + ".accountmanager.broadcast.login";
    LOGIN_CANCELLED = configuration.getAppId() + ".accountmanager.broadcast.LOGIN_CANCELLED";
    LOGOUT = configuration.getAppId() + ".accountmanager.broadcast.logout";
    this.followStoreService = followStoreService;
  }

  public boolean isLoggedIn() {
    final Account account = getAccount();
    return (account != null && !TextUtils.isEmpty(account.getEmail()) && !TextUtils.isEmpty(
        account.getToken()) && !TextUtils.isEmpty(account.getRefreshToken()) && !TextUtils.isEmpty(
        account.getPassword()));
  }

  public Account getAccount() {
    return getAccountAsync().onErrorReturn(throwable -> null).toBlocking().value();
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
    applicationContext.sendBroadcast(new Intent().setAction(LOGOUT));
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
    }).onErrorReturn(throwable -> null).toBlocking().value();
  }

  public void updateMatureSwitch(boolean matureSwitch) {
    getAccountAsync().flatMapCompletable(account -> saveAccount(
        new Account(account.getId(), account.getEmail(), account.getNickname(), account.getAvatar(),
            account.getRefreshToken(), account.getToken(), account.getPassword(), account.getType(),
            account.getStore(), account.getStoreAvatar(), matureSwitch, account.getAccess(),
            account.isAccessConfirmed()))).onErrorComplete().subscribe();
  }

  public Completable refreshAccountToken() {
    return getAccountAsync().flatMapCompletable(account -> refreshToken(account));
  }

  public Completable createAccount(String email, String password) {
    return validateCredentials(email, password).andThen(
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
      return Completable.error(throwable);
    }).doOnCompleted(() -> analytics.login(email)).doOnCompleted(() -> sendLoginBroadcast());
  }

  public void sendLoginBroadcast() {
    applicationContext.sendBroadcast(new Intent().setAction(LOGIN));
  }

  public Completable login(Account.Type type, final String email, final String password,
      final String name) {
    return OAuth2AuthenticationRequest.of(email, password, type.name(), name,
        aptoideClientUUID.getUniqueIdentifier()).observe().toSingle().flatMapCompletable(oAuth -> {
      if (!oAuth.hasErrors()) {
        return syncAccount(oAuth.getAccessToken(), oAuth.getRefreshToken(), password, type);
      } else {
        return Completable.error(new AccountException(oAuth.getError()));
      }
    }).doOnCompleted(() -> analytics.login(email)).doOnCompleted(() -> sendLoginBroadcast());
  }

  public void unsubscribeStore(String storeName,
      BaseRequestWithStore.StoreCredentials storeCredentials) {
    followStoreService.unFollowStore(storeName, storeCredentials.getUsername(),
        storeCredentials.getPasswordSha1(), bodyInterceptor).subscribe();
  }

  public Completable subscribeStore(String storeName, String storeUserName, String storePassword) {
    return followStoreService.followStore(storeName, storeUserName, storePassword, bodyInterceptor)
        .toCompletable();
  }

  public void sendLoginCancelledBroadcast() {
    applicationContext.sendBroadcast(new Intent().setAction(LOGIN_CANCELLED));
  }

  public Observable<List<Subscription>> getUserRepos() {
    return GetUserRepoSubscriptionRequest.of(getAccessToken())
        .observe()
        .observeOn(AndroidSchedulers.mainThread())
        .map(getUserRepoSubscription -> getUserRepoSubscription.getSubscription());
  }

  public String getAccessToken() {
    final Account account = getAccount();
    return account == null ? null : account.getToken();
  }

  public Account.Type getAccountType() {
    final Account account = getAccount();
    return account == null ? null : account.getType();
  }

  public String getUserEmail() {
    final Account account = getAccount();
    return account == null ? null : account.getEmail();
  }

  public boolean isAccountMature() {
    final Account account = getAccount();
    return account == null ? false : account.isMature();
  }

  public boolean isAccountAccessConfirmed() {
    final Account account = getAccount();
    return account == null ? false : account.isAccessConfirmed();
  }

  public Account.Access getAccountAccess() {
    final Account account = getAccount();
    return account == null ? null : account.getAccess();
  }

  public Completable syncCurrentAccount() {
    return getAccountAsync().flatMapCompletable(
        account -> syncAccount(account.getToken(), account.getRefreshToken(), account.getPassword(),
            account.getType()));
  }

  public Completable updateAccount(boolean mature) {
    return getAccountAsync().flatMapCompletable(account -> {
      return ChangeUserSettingsRequest.of(mature, getAccessToken())
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
      return SetUserRequest.of(access.name(), bodyInterceptor)
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

  private Account mapServerAccountToAccount(CheckUserCredentialsJson serverUser,
      String refreshToken, String accessToken, String encryptedPassword, Account.Type accountType) {
    return new Account(String.valueOf(serverUser.getId()), serverUser.getEmail(),
        serverUser.getUsername(), serverUser.getAvatar(), refreshToken, accessToken,
        encryptedPassword, accountType, serverUser.getRepo(), serverUser.getRavatarHd(),
        serverUser.getSettings().getMatureswitch().equals("active"),
        Account.Access.valueOf(serverUser.getAccess().toUpperCase()),
        serverUser.isAccessConfirmed());
  }

  private Completable validateCredentials(String email, String password) {
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
      } else if (password.length() < 8 || !has1number1letter(password)) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.INVALID_PASSWORD));
      }
      return Completable.complete();
    });
  }

  private Completable syncAccount(String accessToken, String refreshToken, String encryptedPassword,
      Account.Type type) {
    return CheckUserCredentialsRequest.of(accessToken)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.getStatus().equals("OK")) {
            return saveAccount(
                mapServerAccountToAccount(response, refreshToken, accessToken, encryptedPassword,
                    type));
          }
          return Completable.error(new IllegalStateException("Failed to refresh account"));
        });
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
                    account.getStoreAvatar(), account.isMature(), account.getAccess(),
                    account.isAccessConfirmed()));
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
          String.valueOf(account.isMature()));
      androidAccountManager.setUserData(androidAccount, ACCESS, account.getAccess().name());
      androidAccountManager.setUserData(androidAccount, ACCESS_CONFIRMED,
          String.valueOf(account.isAccessConfirmed()));
      return Completable.complete();
    }).subscribeOn(Schedulers.io());
  }

  private Single<Account> getAccountAsync() {
    return getAndroidAccountAsync().flatMap(androidAccount -> {
      final Account.Access access =
          androidAccountManager.getUserData(androidAccount, ACCESS) == null
              ? Account.Access.UNLISTED
              : Account.Access.valueOf(androidAccountManager.getUserData(androidAccount, ACCESS));

      return Single.just(new Account(androidAccountManager.getUserData(androidAccount, USER_ID),
          androidAccount.name, androidAccountManager.getUserData(androidAccount, USER_NICK_NAME),
          androidAccountManager.getUserData(androidAccount, USER_AVATAR),
          androidAccountManager.getUserData(androidAccount, REFRESH_TOKEN),
          androidAccountManager.getUserData(androidAccount, ACCESS_TOKEN),
          androidAccountManager.getPassword(androidAccount),
          Account.Type.valueOf(androidAccountManager.getUserData(androidAccount, LOGIN_MODE)),
          androidAccountManager.getUserData(androidAccount, USER_REPO),
          androidAccountManager.getUserData(androidAccount, REPO_AVATAR),
          Boolean.valueOf(androidAccountManager.getUserData(androidAccount, MATURE_SWITCH)), access,
          Boolean.valueOf(androidAccountManager.getUserData(androidAccount, ACCESS_CONFIRMED))));
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

  private android.accounts.Account getAndroidAccount() throws IllegalStateException {
    final android.accounts.Account[] accounts =
        androidAccountManager.getAccountsByType(accountType);

    if (accounts.length == 0) {
      throw new IllegalStateException("No account found.");
    }
    return accounts[0];
  }
}

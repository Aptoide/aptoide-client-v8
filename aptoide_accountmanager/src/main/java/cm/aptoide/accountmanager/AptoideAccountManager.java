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
import cm.aptoide.accountmanager.ws.ChangeUserRepoSubscriptionRequest;
import cm.aptoide.accountmanager.ws.CheckUserCredentialsRequest;
import cm.aptoide.accountmanager.ws.CreateUserRequest;
import cm.aptoide.accountmanager.ws.GetUserRepoSubscriptionRequest;
import cm.aptoide.accountmanager.ws.OAuth2AuthenticationRequest;
import cm.aptoide.accountmanager.ws.responses.CheckUserCredentialsJson;
import cm.aptoide.accountmanager.ws.responses.Subscription;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.preferences.managed.ManagedKeys;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AptoideAccountManager {

  public static final String IS_FACEBOOK_OR_GOOGLE = "facebook_google";
  private static final String MATURE_SWITCH = "aptoide_account_manager_mature_switch";
  private static final String LOGIN_MODE = "aptoide_account_manager_login_mode";
  private static final String ACCESS_TOKEN = "access_token";
  private static final String REFRESH_TOKEN = "refresh_token";
  private static final String USER_EMAIL = "usernameLogin";
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

  private final AptoideClientUUID aptoideClientUuid;
  private final Context applicationContext;
  private final AptoidePreferencesConfiguration configuration;
  private final AccountManager androidAccountManager;
  private final SecureCoderDecoder secureCoderDecoder;
  private final LoginAvailability loginAvailability;

  private final Analytics analytics;

  public AptoideAccountManager(Context applicationContext,
      AptoidePreferencesConfiguration configuration, SecureCoderDecoder secureCoderDecoder,
      AccountManager androidAccountManager, AptoideClientUUID aptoideClientUuid,
      LoginAvailability loginAvailability, Analytics analytics) {
    this.aptoideClientUuid = aptoideClientUuid;
    this.applicationContext = applicationContext;
    this.configuration = configuration;
    this.loginAvailability = loginAvailability;
    this.androidAccountManager = androidAccountManager;
    this.analytics = analytics;
    this.secureCoderDecoder = secureCoderDecoder;
    LOGIN = configuration.getAppId() + ".accountmanager.broadcast.login";
    LOGIN_CANCELLED = configuration.getAppId() + ".accountmanager.broadcast.LOGIN_CANCELLED";
    LOGOUT = configuration.getAppId() + ".accountmanager.broadcast.logout";
  }

  public boolean isLoggedIn() {
    return androidAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE).length != 0;
  }

  public void logout(GoogleApiClient client) {
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
    removeAccount();
    applicationContext.sendBroadcast(new Intent().setAction(LOGOUT));
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

  public Completable login(Account.Type type, final String email, final String password,
      final String name) {
    return OAuth2AuthenticationRequest.of(email, password, type, name,
        aptoideClientUuid.getUniqueIdentifier(), this)
        .observe()
        .toSingle()
        .flatMapCompletable(oAuth -> {
          if (!oAuth.hasErrors()) {
            return syncAccount(oAuth.getAccessToken(), oAuth.getRefreshToken(), password, type);
          } else {
            return Completable.error(new OAuthException(oAuth));
          }
        })
        .doOnCompleted(() -> analytics.login(email))
        .doOnCompleted(() -> sendLoginBroadcast());
  }

  private Account mapServerAccountToAccount(CheckUserCredentialsJson serverUser,
      String refreshToken, String accessToken, String encryptedPassword, Account.Type accountType) {
    return new Account(String.valueOf(serverUser.getId()), serverUser.getEmail(),
        serverUser.getUsername(), serverUser.getAvatar(), refreshToken, accessToken,
        encryptedPassword, accountType, serverUser.getRepo(), serverUser.getRavatarHd(),
        serverUser.getSettings().getMatureswitch().equals("active"), serverUser.getAccess(),
        serverUser.isAccessConfirmed());
  }

  public void updateMatureSwitch(boolean matureSwitch) {
    getAccountAsync().flatMapCompletable(account -> saveAccount(
        new Account(account.getId(), account.getEmail(), account.getNickname(), account.getAvatar(),
            account.getRefreshToken(), account.getToken(), account.getPassword(),
            account.getType(), account.getStore(), account.getStoreAvatar(), matureSwitch,
            account.getAccess(), account.isAccessConfirmed()))).onErrorComplete().subscribe();
  }

  public Completable refreshAccountToken() {
    return getAccountAsync().flatMapCompletable(account -> refreshToken(account));
  }

  private Completable refreshToken(Account account) {
    return OAuth2AuthenticationRequest.of(account.getRefreshToken(),
        aptoideClientUuid.getUniqueIdentifier(), this)
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
            return Completable.error(new OAuthException(oAuth));
          }
        });
  }

  public Completable createAccount(String email, String password) {
    return validateCredentials(email, password).andThen(
        CreateUserRequest.of(email.toLowerCase(), password, aptoideClientUuid.getUniqueIdentifier(),
            this).observe(true))
        .toSingle()
        .flatMapCompletable(oAuth -> {
          if (oAuth.hasErrors()) {
            return Completable.error(new OAuthException(oAuth));
          }
          return syncAccount(oAuth.getAccessToken(), oAuth.getRefreshToken(), password,
              Account.Type.APTOIDE);
        })
        .doOnCompleted(() -> analytics.login(email))
        .doOnCompleted(() -> sendLoginBroadcast())
        .onErrorResumeNext(throwable -> login(Account.Type.APTOIDE, email, password, null))
        .doOnCompleted(() -> analytics.signUp());
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

  public void unsubscribeStore(String storeName) {
    ChangeUserRepoSubscriptionRequest.of(storeName, false, this)
        .execute(genericResponseV3 -> Logger.d(TAG, "Successfully unsubscribed " + storeName),
            true);
  }

  public void subscribeStore(String storeName) {
    ChangeUserRepoSubscriptionRequest.of(storeName, true, this)
        .execute(genericResponseV3 -> Logger.d(TAG, "Successfully subscribed " + storeName), true);
  }

  public void sendLoginBroadcast() {
    applicationContext.sendBroadcast(new Intent().setAction(LOGIN));
  }

  public void sendLoginCancelledBroadcast() {
    applicationContext.sendBroadcast(new Intent().setAction(LOGIN_CANCELLED));
  }

  public Observable<List<Subscription>> getUserRepos() {
    return GetUserRepoSubscriptionRequest.of(this)
        .observe()
        .observeOn(AndroidSchedulers.mainThread())
        .map(getUserRepoSubscription -> getUserRepoSubscription.getSubscription());
  }

  private Completable saveAccount(Account account) {
    return Completable.defer(() -> {
      final android.accounts.Account[] androidAccounts =
          androidAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);

      final android.accounts.Account androidAccount;
      if (androidAccounts.length == 0) {
        androidAccount = new android.accounts.Account(account.getEmail(), Constants.ACCOUNT_TYPE);
        try {
          androidAccountManager.addAccountExplicitly(androidAccount,
              secureCoderDecoder.encrypt(account.getPassword()), null);
        } catch (SecurityException e) {
          return Completable.error(e);
        }
      } else {
        androidAccount = androidAccounts[0];
      }
      androidAccountManager.setUserData(androidAccount, REFRESH_TOKEN, account.getRefreshToken());
      androidAccountManager.setUserData(androidAccount, ACCESS_TOKEN, account.getToken());
      androidAccountManager.setUserData(androidAccount, LOGIN_MODE, account.getType().name());
      androidAccountManager.setUserData(androidAccount, USER_ID, account.getId());
      androidAccountManager.setUserData(androidAccount, USER_AVATAR, account.getAvatar());
      androidAccountManager.setUserData(androidAccount, USER_EMAIL, account.getEmail());
      androidAccountManager.setUserData(androidAccount, USER_NICK_NAME, account.getNickname());
      androidAccountManager.setUserData(androidAccount, USER_REPO, account.getStore());
      androidAccountManager.setUserData(androidAccount, REPO_AVATAR, account.getStoreAvatar());
      androidAccountManager.setUserData(androidAccount, ManagedKeys.ACCESS, account.getAccess());
      androidAccountManager.setUserData(androidAccount, ManagedKeys.ACCESS_CONFIRMED,
          String.valueOf(account.isAccessConfirmed()));
      return Completable.complete();
    }).subscribeOn(Schedulers.io());
  }

  private Single<Account> getAccountAsync() {
    return getAndroidAccountAsync().flatMap(androidAccount -> {
      String access = androidAccountManager.getUserData(androidAccount, ManagedKeys.ACCESS);

      return Single.just(new Account(androidAccountManager.getUserData(androidAccount, USER_ID),
          androidAccount.name, androidAccountManager.getUserData(androidAccount, USER_NICK_NAME),
          androidAccountManager.getUserData(androidAccount, USER_AVATAR),
          androidAccountManager.getUserData(androidAccount, REFRESH_TOKEN),
          androidAccountManager.getUserData(androidAccount, ACCESS_TOKEN),
          secureCoderDecoder.decrypt(androidAccountManager.getPassword(androidAccount)),
          Account.Type.valueOf(androidAccountManager.getUserData(androidAccount, LOGIN_MODE)),
          androidAccountManager.getUserData(androidAccount, USER_REPO),
          androidAccountManager.getUserData(androidAccount, REPO_AVATAR),
          Boolean.valueOf(androidAccountManager.getUserData(androidAccount, MATURE_SWITCH)),
          access == null ? "UNLISTED" : access, Boolean.valueOf(
          androidAccountManager.getUserData(androidAccount, ManagedKeys.ACCESS_CONFIRMED))));
    });
  }

  public Account getAccount() {
    return getAccountAsync().onErrorReturn(throwable -> null).toBlocking().value();
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
    return account == null ? false: account.isMature();
  }

  private Single<android.accounts.Account> getAndroidAccountAsync() {
    return Single.defer(() -> {
      final android.accounts.Account[] accounts =
          androidAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);

      if (accounts.length == 0) {
        return Single.error(new IllegalStateException("No account found."));
      }
      return Single.just(accounts[0]);
    });
  }

  public Completable syncCurrentAccount() {
    return getAccountAsync().flatMapCompletable(
        account -> syncAccount(account.getToken(), account.getRefreshToken(),
            account.getPassword(), account.getType()));
  }

  private Completable syncAccount(String accessToken, String refreshToken, String encryptedPassword,
      Account.Type type) {
    return CheckUserCredentialsRequest.of(this, accessToken)
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

  public boolean isGoogleLoginEnabled() {
    return loginAvailability.isGoogleLoginAvailable();
  }

  public boolean isFacebookLoginEnabled() {
    return loginAvailability.isFacebookLoginAvailable();
  }

  public Completable updateAccount(String nickname, String avatarPath) {
    return getAccountAsync().flatMapObservable(account -> {
      if (TextUtils.isEmpty(nickname)) {
        return Observable.error(new UserValidationException(UserValidationException.EMPTY_NAME));
      }
      return CreateUserRequest.of("true", account.getEmail(), nickname,
          account.getPassword(),
          (TextUtils.isEmpty(avatarPath) ? "" : avatarPath),
          aptoideClientUuid.getUniqueIdentifier(), this).observe();
    }).flatMap(response -> {
      if (!response.hasErrors()) {
        return Observable.just(response);
      } else {
        return Observable.error(new OAuthException(response));
      }
    }).toCompletable();
  }
}

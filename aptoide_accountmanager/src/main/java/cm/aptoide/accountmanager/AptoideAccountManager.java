/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/06/2016.
 */

package cm.aptoide.accountmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import cm.aptoide.accountmanager.ws.ChangeUserRepoSubscriptionRequest;
import cm.aptoide.accountmanager.ws.ChangeUserSettingsRequest;
import cm.aptoide.accountmanager.ws.CheckUserCredentialsRequest;
import cm.aptoide.accountmanager.ws.CreateUserRequest;
import cm.aptoide.accountmanager.ws.GetUserRepoSubscriptionRequest;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.accountmanager.ws.OAuth2AuthenticationRequest;
import cm.aptoide.accountmanager.ws.responses.CheckUserCredentialsJson;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.accountmanager.ws.responses.Subscription;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AptoideAccountManager {

  public static final String IS_FACEBOOK_OR_GOOGLE = "facebook_google";
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

  private Analytics analytics;
  private ILoginInterface callback;

  public AptoideAccountManager(Context applicationContext,
      AptoidePreferencesConfiguration configuration, SecureCoderDecoder secureCoderDecoder,
      AccountManager androidAccountManager, AptoideClientUUID aptoideClientUuid,
      LoginAvailability loginAvailability) {
    this.aptoideClientUuid = aptoideClientUuid;
    this.applicationContext = applicationContext;
    this.configuration = configuration;
    this.loginAvailability = loginAvailability;
    this.analytics = analytics;
    this.androidAccountManager = androidAccountManager;
    this.secureCoderDecoder = secureCoderDecoder;
    LOGIN = configuration.getAppId() + ".accountmanager.broadcast.login";
    LOGIN_CANCELLED = configuration.getAppId() + ".accountmanager.broadcast.LOGIN_CANCELLED";
    LOGOUT = configuration.getAppId() + ".accountmanager.broadcast.logout";
  }

  public Analytics getAnalytics() {
    return analytics;
  }

  public void setAnalytics(Analytics analytics) {
    this.analytics = analytics;
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
    removeLocalAccount();
    applicationContext.sendBroadcast(new Intent().setAction(LOGOUT));
  }

  public void removeLocalAccount() {
    Account[] accounts = androidAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
    for (Account account : accounts) {
      if (Build.VERSION.SDK_INT >= 22) {
        androidAccountManager.removeAccountExplicitly(account);
      } else {
        androidAccountManager.removeAccount(account, null, null);
      }
    }
    AccountManagerPreferences.removeUserEmail();
    AccountManagerPreferences.removeAccessToken();
    AccountManagerPreferences.removeRefreshToken();
    AccountManagerPreferences.removeMatureSwitch();
    AccountManagerPreferences.removeQueueName();
    AccountManagerPreferences.removeUserAvatar();
    AccountManagerPreferences.removeUserNickName();
    AccountManagerPreferences.removeUserRepo();
    AccountManagerPreferences.removeRepoAvatar();
  }

  public LoginMode getLoginMode() {
    return AccountManagerPreferences.getLoginMode();
  }

  public Completable login(LoginMode mode, final String username, final String password,
      final String name) {
    return getServerAccount(mode, username, password, name)
        .doOnSuccess(account -> analytics.login(account.getUsername()))
        .doOnSuccess(account -> sendLoginBroadcast())
        .flatMapCompletable(account -> saveAccount(account, password));
  }

  private Single<cm.aptoide.accountmanager.Account> getServerAccount(LoginMode mode,
      String username, String password, String name) {
    return OAuth2AuthenticationRequest.of(username, password, mode, name,
        aptoideClientUuid.getUniqueIdentifier(), this).observe().flatMap(oAuth -> {
      if (!oAuth.hasErrors()) {
        return Observable.just(
            new cm.aptoide.accountmanager.Account(username, oAuth.getRefreshToken(),
                oAuth.getAccessToken(), secureCoderDecoder.encrypt(password)));
      } else {
        return Observable.error(new OAuthException(oAuth));
      }
    }).toSingle();
  }

  private void saveServerUser(CheckUserCredentialsJson user) {

    AccountManagerPreferences.setUserId(user.getId());

    if (!TextUtils.isEmpty(user.getQueueName())) {
      //hasQueue = true;
      AccountManagerPreferences.setQueueName(user.getQueueName());
    }

    if (!TextUtils.isEmpty(user.getAvatar())) {
      AccountManagerPreferences.setUserAvatar(user.getAvatar());
    }

    if (!TextUtils.isEmpty(user.getRavatarHd())) {
      AccountManagerPreferences.setRepoAvatar(user.getRavatarHd());
    }

    if (!TextUtils.isEmpty(user.getRepo())) {
      AccountManagerPreferences.setUserRepo(user.getRepo());
    }

    if (!TextUtils.isEmpty(user.getUsername())) {
      AccountManagerPreferences.setUserNickName(user.getUsername());
    }

    if (user.getRepoDescription() != null && !TextUtils.isEmpty(
        user.getRepoDescription().getTheme())) {
      AccountManagerPreferences.setRepoTheme(user.getRepoDescription().getTheme());
    }

    if (!TextUtils.isEmpty(user.getAccess())) {
      ManagerPreferences.setUserAccess(user.getAccess());
    }

    if (!user.getAccessConfirmed().toString().isEmpty()) {
      ManagerPreferences.setUserAccessConfirmed(user.getAccessConfirmed());
    }

    if (user.getSettings() != null) {
      AccountManagerPreferences.setMatureSwitch(
          user.getSettings().getMatureswitch().equals("active"));
    }
  }

  public User getUser() {
    return new User(AccountManagerPreferences.getUserId(), AccountManagerPreferences.getUserNickName(),
        AccountManagerPreferences.getUserEmail(), AccountManagerPreferences.getQueueName(),
        AccountManagerPreferences.getUserAvatar(), AccountManagerPreferences.getUserRepo(),
        AccountManagerPreferences.getMatureSwitch(), AccountManagerPreferences.getRepoAvatar());
  }

  public void updateMatureSwitch(boolean matureSwitch) {
    AccountManagerPreferences.setMatureSwitch(matureSwitch);
    if (isLoggedIn()) {
      ChangeUserSettingsRequest.of(matureSwitch, this)
          .observe(true) // bypass cache since we are "writing" a value
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .doOnError(throwable -> {
            Logger.e(TAG, "Unable to update mature switch to " + Boolean.toString(matureSwitch));
            CrashReport.getInstance().log(throwable);
          })
          .subscribe();
    }
  }

  public Observable<String> invalidateAccessToken() {
    return Observable.fromCallable(() -> {
      return getRefreshToken();
    })
        .subscribeOn(Schedulers.io())
        .flatMap(s -> getNewAccessTokenFromRefreshToken(getRefreshToken()));
  }

  public @Nullable String getRefreshToken() {
    String refreshToken = AccountManagerPreferences.getRefreshToken();

    if (refreshToken == null || TextUtils.isEmpty(refreshToken)) {
      refreshToken = getUserStringFromAndroidAccountManager(SecureKeys.REFRESH_TOKEN);
      AccountManagerPreferences.setRefreshToken(refreshToken);
    }

    if (refreshToken == null || TextUtils.isEmpty(refreshToken)) {
      try {
        refreshToken = getRefreshTokenFromAccountManager(); // as it is done in V7
        AccountManagerPreferences.setRefreshToken(refreshToken);
      } catch (Exception e) {
        CrashReport.getInstance().log(e);
      }
    }

    return refreshToken;
  }

  private Observable<String> getNewAccessTokenFromRefreshToken(String refreshToken) {
    return OAuth2AuthenticationRequest.of(refreshToken, aptoideClientUuid.getUniqueIdentifier(), this)
        .observe()
        .observeOn(AndroidSchedulers.mainThread())
        .map(OAuth::getAccessToken)
        .subscribeOn(Schedulers.io())
        .doOnNext(accessToken -> {
          setAccessTokenOnLocalAccount(accessToken, null);
          AccountManagerPreferences.setAccessToken(accessToken);
        })
        .doOnError(throwable -> removeLocalAccount())
        .observeOn(AndroidSchedulers.mainThread());
  }

  private String getUserStringFromAndroidAccountManager(String key) {
    Account[] accountsByType = androidAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);

    return accountsByType.length > 0 ? androidAccountManager.getUserData(accountsByType[0], key)
        : null;
  }

  private @Nullable String getRefreshTokenFromAccountManager()
      throws AuthenticatorException, OperationCanceledException, IOException {
    Account[] accountsByType = androidAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
    String refreshToken = androidAccountManager.blockingGetAuthToken(accountsByType[0],
        Constants.AUTHTOKEN_TYPE_FULL_ACCESS, false);
    return refreshToken;
  }

  public void setAccessTokenOnLocalAccount(String accessToken, @Nullable Account userAccount) {
    if (userAccount == null) {
      Account[] accounts = androidAccountManager.getAccounts();
      for (final Account account : accounts) {
        if (TextUtils.equals(account.name, getUserEmail()) && TextUtils.equals(account.type,
            Constants.ACCOUNT_TYPE)) {
          userAccount = account;
          break;
        }
      }
    } else {
      androidAccountManager.setUserData(userAccount, SecureKeys.ACCESS_TOKEN, accessToken);
    }
  }

  /**
   * Get the userEmail of current logged user
   *
   * @return A string with the userEmail
   */
  public String getUserEmail() {
    String userName = AccountManagerPreferences.getUserEmail();
    if (userName == null || TextUtils.isEmpty(userName)) {
      Account[] accounts = androidAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
      if (accounts.length > 0) {
        userName = accounts[0].name;
        AccountManagerPreferences.setUserEmail(userName);
      }
    }
    return userName;
  }

  public Completable createAccount(String email, String password) {
    return validateAccountCredentials(email, password).andThen(
        CreateUserRequest.of(email.toLowerCase(), password,
            aptoideClientUuid.getUniqueIdentifier(), this).observe(true))
        .toSingle()
        .flatMap(oAuth -> {
          if (oAuth.hasErrors()) {
            return Single.error(new OAuthException(oAuth));
          }
          cm.aptoide.accountmanager.Account account =
              new cm.aptoide.accountmanager.Account(email, oAuth.getRefreshToken(),
                  oAuth.getAccessToken(), secureCoderDecoder.encrypt(password));
          AccountManagerPreferences.setAccessToken(account.getToken());
          setAccessTokenOnLocalAccount(account.getToken(), null);
          return Single.just(account);
        })
        .doOnSuccess(account -> analytics.signUp())
        .onErrorResumeNext(throwable -> getAccountOnTimeout(email, password, throwable, LoginMode.APTOIDE))
        .doOnSuccess(account -> analytics.login(account.getUsername()))
        .doOnSuccess(account -> sendLoginBroadcast())
        .flatMapCompletable(account -> saveAccount(account, password));
  }

  private Single<? extends cm.aptoide.accountmanager.Account> getAccountOnTimeout(String email,
      String password, Throwable throwable, LoginMode type) {
    if (throwable instanceof SocketTimeoutException) {
      return getServerAccount(type, email, password, null);
    }
    return Single.error(throwable);
  }

  private Completable validateAccountCredentials(String email, String password) {
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

  private Completable saveAccount(cm.aptoide.accountmanager.Account account, String password) {
    return Completable.defer(() -> {
      final Account androidAccount = new Account(account.getUsername(), Constants.ACCOUNT_TYPE);
      final String encryptPassword = secureCoderDecoder.encrypt(password);
      try {
        androidAccountManager.addAccountExplicitly(androidAccount, encryptPassword, null);
      } catch (SecurityException e) {
        return Completable.error(e);
      }
      androidAccountManager.setUserData(androidAccount, SecureKeys.REFRESH_TOKEN,
          account.getRefreshToken());
      AccountManagerPreferences.setAccessToken(account.getToken());
      AccountManagerPreferences.setRefreshToken(account.getRefreshToken());
      return syncUser().onErrorComplete();
    }).subscribeOn(Schedulers.io());
  }

  public Single<cm.aptoide.accountmanager.Account> getAccount() {
    return Single.defer(() -> {
      final Account[] accounts = androidAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);

      if (accounts.length == 0) {
        return Single.error(new IllegalStateException("Not logged in."));
      }

      return Single.just(new cm.aptoide.accountmanager.Account(accounts[0].name,
          AccountManagerPreferences.getRefreshToken(), AccountManagerPreferences.getAccessToken(),
          androidAccountManager.getPassword(accounts[0])));
    });
  }

  public Completable syncUser() {
    return CheckUserCredentialsRequest.of(this).observe().flatMap(response -> {
      if (response.getStatus().equals("OK")) {
        return Observable.just(response);
      }
      return Observable.error(new IllegalStateException("Failed to refresh account"));
    }).doOnNext(user -> saveServerUser(user)).toCompletable();
  }

  public String getAccessToken() {
    String accessToken = AccountManagerPreferences.getAccessToken();
    if (TextUtils.isEmpty(accessToken)) {
      accessToken = getUserStringFromAndroidAccountManager(SecureKeys.ACCESS_TOKEN);
      AccountManagerPreferences.setAccessToken(accessToken);
    }
    return accessToken;
  }

  public boolean isGoogleLoginEnabled() {
    return loginAvailability.isGoogleLoginAvailable();
  }

  public boolean isFacebookLoginEnabled() {
    return loginAvailability.isFacebookLoginAvailable();
  }

  public Completable createUser(String name, String avatarPath) {
    return getAccount()
        .flatMapObservable(account -> {
          if (TextUtils.isEmpty(name)) {
            return Observable.error(new UserValidationException(UserValidationException.EMPTY_NAME));
          }
          return Observable.just(account);
        })
        .flatMap(account -> CreateUserRequest.of("true", account.getUsername(), name,
            secureCoderDecoder.decrypt(account.getEncryptedPassword()), (TextUtils.isEmpty(avatarPath)? "" : avatarPath),
            aptoideClientUuid.getUniqueIdentifier(), this)
            .observe())
        .flatMap(response -> {
          if (!response.hasErrors()) {
            return Observable.just(response);
          } else {
            return Observable.error(new OAuthException(response));
          }
        })
        .toCompletable();
  }

  public interface ICreateStore {

    void onCreateSuccess(ProgressDialog progressDialog);

    void onCreateFail(@StringRes int reason);

    String getRepoName();

    String getRepoTheme();

    String getRepoAvatar();

    String getRepoDescription();
  }

  /**
   * This interface is used to interact with Account Manager. It informs outsiders if login was
   * made successfully or not and gives manager the user credentials
   */
  public interface ILoginInterface {

    /**
     * Called when logis is made successfully
     */
    void onLoginSuccess();

    /**
     * Called when the login fails
     */
    void onLoginFail(String reason);

    /**
     * Used to get user name inserted by user
     *
     * @return user name
     */
    String getIntroducedUserName();

    /**
     * Used to get password inserted by user
     *
     * @return password
     */
    String getIntroducedPassword();
  }
}

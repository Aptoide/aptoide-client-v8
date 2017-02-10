/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/06/2016.
 */

package cm.aptoide.accountmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.NetworkErrorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Button;
import cm.aptoide.accountmanager.util.UserCompleteData;
import cm.aptoide.accountmanager.ws.AptoideWsV3Exception;
import cm.aptoide.accountmanager.ws.ChangeUserRepoSubscriptionRequest;
import cm.aptoide.accountmanager.ws.ChangeUserSettingsRequest;
import cm.aptoide.accountmanager.ws.CheckUserCredentialsRequest;
import cm.aptoide.accountmanager.ws.CreateUserRequest;
import cm.aptoide.accountmanager.ws.ErrorsMapper;
import cm.aptoide.accountmanager.ws.GetUserRepoSubscriptionRequest;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.accountmanager.ws.OAuth2AuthenticationRequest;
import cm.aptoide.accountmanager.ws.responses.CheckUserCredentialsJson;
import cm.aptoide.accountmanager.ws.responses.GenericResponseV3;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.accountmanager.ws.responses.Subscription;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.utils.GenericDialogs;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;
import javax.security.auth.login.LoginException;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AptoideAccountManager {

  /**
   * This constant is used to send the broadcast when an account is removed
   */
  public static final String ACCOUNT_REMOVED_BROADCAST_KEY = "cm.aptoide.accountmanager" + "" +
      ".removedaccount.broadcast";

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

  private boolean userIsLoggedIn;
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
    this.userIsLoggedIn = isLoggedIn();
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
    userIsLoggedIn = false;
    applicationContext.sendBroadcast(new Intent().setAction(LOGOUT));
  }

  public void removeLocalAccount() {
    Account[] accounts = androidAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
    userIsLoggedIn = false;
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

  /**
   * make the request to the server for login using the user credentials
   *
   * @param mode login mode, ca be facebook, aptoide or google
   * @param userName user's username usually the email address
   * @param passwordOrToken user password or token given by google or facebook
   * @param nameForGoogle name given by google
   * @param context given context
   */
  public void login(LoginMode mode, final String userName, final String passwordOrToken,
      final String nameForGoogle, Context context) {
    ProgressDialog genericPleaseWaitDialog = null;
    if (context != null) {
      genericPleaseWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(context);
      if (!((Activity) context).isFinishing()) {
        genericPleaseWaitDialog.show();
      }
    }
    OAuth2AuthenticationRequest oAuth2AuthenticationRequest =
        OAuth2AuthenticationRequest.of(userName, passwordOrToken, mode, nameForGoogle,
            aptoideClientUuid.getUniqueIdentifier(), this);
    final ProgressDialog finalGenericPleaseWaitDialog = genericPleaseWaitDialog;
    oAuth2AuthenticationRequest.execute(oAuth -> {
      Logger.d(TAG, "onSuccess() called with: " + "oAuth = [" + oAuth + "]");

      if (!oAuth.hasErrors()) {
        AccountManagerPreferences.setAccessToken(oAuth.getAccessToken());

        createAccount(userName, passwordOrToken, null, oAuth.getRefresh_token(),
            oAuth.getAccessToken()).doOnCompleted(() -> {
          setAccessTokenOnLocalAccount(oAuth.getAccessToken(), null, SecureKeys.ACCESS_TOKEN);
          AccountManagerPreferences.setLoginMode(mode);
          onLoginSuccess(mode, "", "", "");
          if (finalGenericPleaseWaitDialog != null) {
            finalGenericPleaseWaitDialog.dismiss();
          }
          sendLoginBroadcast();
        }).subscribe();
      } else { // oAuth.hasErrors() = true

        if (finalGenericPleaseWaitDialog != null) {
          finalGenericPleaseWaitDialog.dismiss();
        }

        onLoginFail(applicationContext.getString(R.string.unknown_error));
      }
      Logger.e(TAG, "Error while adding the local account. Probably context was null");
    }, new ErrorRequestListener() {
      @Override public void onError(Throwable e) {
        try {
          if (e instanceof AptoideWsV3Exception) {
            GenericResponseV3 oAuth = ((AptoideWsV3Exception) e).getBaseResponse();
            onLoginFail(applicationContext.getString(
                ErrorsMapper.getWebServiceErrorMessageFromCode(oAuth.getError())));
          } else {
            onLoginFail(applicationContext.getString(R.string.unknown_error));
          }
        } finally {
          if (finalGenericPleaseWaitDialog != null) {
            finalGenericPleaseWaitDialog.dismiss();
          }
        }
      }
    }, true);
  }

  public Completable login(LoginMode mode, final String username, final String password,
      final String name) {
    return OAuth2AuthenticationRequest.of(username, password, mode, name,
        aptoideClientUuid.getUniqueIdentifier(), this).observe().flatMap(oAuth -> {
      if (!oAuth.hasErrors()) {
        AccountManagerPreferences.setAccessToken(oAuth.getAccessToken());
        return createAccount(username, password, null, oAuth.getRefresh_token(),
            oAuth.getAccessToken()).toObservable();
      } else {
        return Observable.error(new LoginException(oAuth.getError()));
      }
    }).toCompletable();
  }

  /**
   * Save user info on secured shared preferences
   *
   * @param user Object returned by webservice(CheckUserCredentialsRequest)
   * with the user info
   */
  private void saveUser(CheckUserCredentialsJson user) {

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

  /**
   * This method creates a new UserInfo object with all the user info
   *
   * @return User info class with all collected information about the user
   */
  public UserCompleteData getUserData() {
    UserCompleteData userCompleteData = new UserCompleteData();
    userCompleteData.setId(AccountManagerPreferences.getUserId());
    userCompleteData.setUserName(AccountManagerPreferences.getUserNickName());
    userCompleteData.setUserEmail(AccountManagerPreferences.getUserEmail());
    userCompleteData.setQueueName(AccountManagerPreferences.getQueueName());
    userCompleteData.setUserAvatar(AccountManagerPreferences.getUserAvatar());
    userCompleteData.setUserRepo(AccountManagerPreferences.getUserRepo());
    userCompleteData.setMatureSwitch(AccountManagerPreferences.getMatureSwitch());
    userCompleteData.setUserAvatarRepo(AccountManagerPreferences.getRepoAvatar());
    return userCompleteData;
  }

  /**
   * Get state of the mature switch
   *
   * @return return true if the switch is on, false if off
   */
  public boolean isMatureSwitchOn() {
    return AccountManagerPreferences.getMatureSwitch();
  }

  /**
   * Update the mature switch. If user is logged, it updates on aptoide's server too
   *
   * @param matureSwitch Switch state
   */
  @Partners public void updateMatureSwitch(boolean matureSwitch) {
    AccountManagerPreferences.setMatureSwitch(matureSwitch);
    if (userIsLoggedIn) {
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
          setAccessTokenOnLocalAccount(accessToken, null, SecureKeys.ACCESS_TOKEN);
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

  public void setAccessTokenOnLocalAccount(String accessToken, @Nullable Account userAccount,
      @NonNull String dataKey) {
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
      androidAccountManager.setUserData(userAccount, dataKey, accessToken);
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

  public void setupRegisterUser(IRegisterUser callback, Button signupButton, Context context) {
    final WeakReference callBackWeakReference = new WeakReference(callback);
    signupButton.setOnClickListener(v -> {
      IRegisterUser callback1 = (IRegisterUser) callBackWeakReference.get();
      if (callback1 != null) {
        ProgressDialog genericPleaseWaitDialog =
            GenericDialogs.createGenericPleaseWaitDialog(context);
        genericPleaseWaitDialog.show();
        registerUserUsingWebServices(callback1, genericPleaseWaitDialog, context);
      }
    });
  }

  void registerUserUsingWebServices(IRegisterUser callback, ProgressDialog genericPleaseWaitDialog,
      Context context) {
    String email = callback.getUserEmail();
    String password = callback.getUserPassword();
    if (validateUserCredentials(callback, email, password)) {
      CreateUserRequest.of(email, password, aptoideClientUuid.getUniqueIdentifier(), this)
          .execute(oAuth -> {
            if (oAuth.hasErrors()) {
              if (oAuth.getErrors() != null && oAuth.getErrors().size() > 0) {
                callback.onRegisterFail(
                    ErrorsMapper.getWebServiceErrorMessageFromCode(oAuth.getErrors().get(0).code));
                genericPleaseWaitDialog.dismiss();
              } else {
                callback.onRegisterFail(R.string.unknown_error);
                genericPleaseWaitDialog.dismiss();
              }
            } else {
              Bundle bundle = new Bundle();
              bundle.putString(AptoideLoginUtils.APTOIDE_LOGIN_USER_NAME_KEY, email);
              bundle.putString(AptoideLoginUtils.APTOIDE_LOGIN_PASSWORD_KEY, password);
              bundle.putString(AptoideLoginUtils.APTOIDE_LOGIN_REFRESH_TOKEN_KEY,
                  oAuth.getRefresh_token());
              bundle.putString(AptoideLoginUtils.APTOIDE_LOGIN_ACCESS_TOKEN_KEY,
                  oAuth.getAccessToken());
              callback.onRegisterSuccess(bundle);
              genericPleaseWaitDialog.dismiss();
            }
          }, e -> {
            if (e instanceof NetworkErrorException) {
              callback.onRegisterFail(R.string.unknown_error);
            }
            if (e instanceof SocketTimeoutException) {
              login(LoginMode.APTOIDE, email, password, null, context);
            }
            genericPleaseWaitDialog.dismiss();
            e.printStackTrace();
          }, true);
    } else {
      genericPleaseWaitDialog.dismiss();
    }
  }

  /**
   * Validate if user credentials are valid
   *
   * @return true if credentials are valid, false otherwise.
   */
  private boolean validateUserCredentials(IRegisterUser callback, String email, String password) {
    boolean toReturn = true;
    if (email.length() == 0 && password.length() == 0) {
      callback.onRegisterFail(R.string.no_email_and_pass_error_message);
      toReturn = false;
    } else if (password.length() == 0) {
      callback.onRegisterFail(R.string.no_pass_error_message);
      toReturn = false;
    } else if (email.length() == 0) {
      callback.onRegisterFail(R.string.no_email_error_message);
      toReturn = false;
    } else if (password.length() < 8 || !has1number1letter(password)) {
      callback.onRegisterFail(R.string.password_validation_text);
      toReturn = false;
    }

    return toReturn;
  }

  /**
   * Check if password has at least one letter and one number
   *
   * @param password String with password to check
   * @return True if has at least one number and one letter, false otherwise
   */
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

  public Completable createAccount(String userName, String userPassword,
      @Nullable String accountType, String refreshToken, String accessToken) {
    return Completable.defer(() -> {
      final Account account =
          new Account(userName, accountType != null ? accountType : Constants.ACCOUNT_TYPE);
      final String encryptPassword = secureCoderDecoder.encrypt(userPassword);
      try {
        androidAccountManager.addAccountExplicitly(account, encryptPassword, null);
      } catch (SecurityException e) {
        return Completable.error(e);
      }
      androidAccountManager.setUserData(account, SecureKeys.REFRESH_TOKEN, refreshToken);
      AccountManagerPreferences.setRefreshToken(refreshToken);
      return refreshAccount();
    });
  }

  public Completable refreshAccount() {
    return CheckUserCredentialsRequest.of(this).observe().flatMap(response -> {
      if (response.getStatus().equals("OK")) {
        return Observable.just(response);
      }
      return Observable.error(new LoginException("Failed to refresh account"));
    }).doOnNext(user -> saveUser(user)).toCompletable();
  }

  /**
   * Get the accessToken used to authenticate user on aptoide webservices
   *
   * @return A string with the token
   */
  @Partners @Nullable public String getAccessToken() {
    String accessToken = AccountManagerPreferences.getAccessToken();
    if (accessToken == null || TextUtils.isEmpty(accessToken)) {
      accessToken = getUserStringFromAndroidAccountManager(SecureKeys.ACCESS_TOKEN);
      AccountManagerPreferences.setAccessToken(accessToken);
    }
    return accessToken;
  }

  void onLoginFail(String reason) {
    if (callback != null) callback.onLoginFail(reason);
  }

  void onLoginSuccess(LoginMode loginType, String loginOrigin, String username, String password) {

    userIsLoggedIn = true;

    if (callback != null) {
      callback.onLoginSuccess();
    }
    if (analytics != null) {
      analytics.login(loginType.name());
    }
    /*if (loginOrigin.equals("signup")) {
      Intent intent = new Intent(applicationContext, CreateUserActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra(AptoideLoginUtils.APTOIDE_LOGIN_USER_NAME_KEY, username);
      intent.putExtra(AptoideLoginUtils.APTOIDE_LOGIN_PASSWORD_KEY, password);
      intent.putExtra(AptoideLoginUtils.APTOIDE_LOGIN_ACCESS_TOKEN_KEY, getAccessToken());
      applicationContext.startActivity(intent);
    }

    if ((loginType.equals(LoginMode.FACEBOOK) || loginType.equals(LoginMode.GOOGLE))
        && !ManagerPreferences.getUserAccessConfirmed()) {
      Intent socialIntent = new Intent(applicationContext, LoggedInActivity.class);
      socialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      socialIntent.putExtra(AptoideLoginUtils.IS_FACEBOOK_OR_GOOGLE, true);
      applicationContext.startActivity(socialIntent);
    }*/
  }

  void onLoginSuccess() {
    userIsLoggedIn = true;
    callback.onLoginSuccess();
  }

  public void sendRemoveLocalAccountBroadcaster(Context context) {
    Intent intent = new Intent();
    intent.setAction(ACCOUNT_REMOVED_BROADCAST_KEY);
    context.sendBroadcast(intent);
  }

  /**
   * get user name introduced in edit text by user
   *
   * @return The user name introduced by user
   */
  String getIntroducedUserName() {
    return callback == null ? null : callback.getIntroducedUserName();
  }

  /**
   * get password introduced in edit text by user
   *
   * @return The password introduced by user
   */
  String getIntroducedPassword() {
    return callback == null ? null : callback.getIntroducedPassword();
  }

  public boolean isGoogleLoginEnabled() {
    return loginAvailability.isGoogleLoginAvailable();
  }

  public boolean isFacebookLoginEnabled() {
    return loginAvailability.isFacebookLoginAvailable();
  }

  /*******************************************************/

  public interface IRegisterUser {

    void onRegisterSuccess(Bundle data);

    void onRegisterFail(@StringRes int reason);

    String getUserPassword();

    String getUserEmail();
  }

  public interface ICreateProfile {

    void onRegisterSuccess(ProgressDialog progressDialog);

    void onRegisterFail(@StringRes int reason);

    String getUserUsername();

    String getUserAvatar();
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

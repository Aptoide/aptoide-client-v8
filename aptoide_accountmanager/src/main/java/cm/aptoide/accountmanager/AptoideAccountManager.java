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
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
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
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.utils.GenericDialogs;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;
import javax.security.auth.login.LoginException;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 4/18/16. <li>{@link #openAccountManager(Context)}</li> <li>{@link
 * #openAccountManager(Context, boolean)}</li> <li>{@link #openAccountManager(Context,
 * Bundle)}</li>
 * <li>{@link #getUserEmail()}</li> <li>{@link #onActivityResult(Activity, int, int, Intent)}</li>
 * <li>{@link #getUserData()}</li> <li>{@link #updateMatureSwitch(boolean)}</li> <li>{@link
 * #invalidateAccessToken(Context)}</li> <li>{@link #invalidateAccessTokenSync(Context)}</li>
 * <li>{@link #ACCOUNT_REMOVED_BROADCAST_KEY}</li>
 */
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
  private static AptoideAccountManager instance;

  private final AptoideClientUUID aptoideClientUuid;
  private final Context applicationContext;
  private AptoidePreferencesConfiguration configuration;
  private Analytics analytics;
  private boolean userIsLoggedIn;

  private ILoginInterface callback;
  private AccountManager androidAccountManager;
  private SecureCoderDecoder secureCoderDecoder;

  public AptoideAccountManager(AptoideClientUUID aptoideClientUuid, Context applicationContext,
      AptoidePreferencesConfiguration configuration, AccountManager androidAccountManager,
      SecureCoderDecoder secureCoderDecoder) {
    this.aptoideClientUuid = aptoideClientUuid;
    this.applicationContext = applicationContext;
    this.configuration = configuration;
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

  public Observable<Void> login(Context context) {
    return Observable.fromCallable(() -> {
      if (isLoggedIn()) {
        return null;
      }
      IntentFilter loginFilter = new IntentFilter(AptoideAccountManager.LOGIN);
      loginFilter.addAction(AptoideAccountManager.LOGIN_CANCELLED);
      loginFilter.addAction(AptoideAccountManager.LOGOUT);
      return loginFilter;
    }).flatMap(intentFilter -> {
      if (intentFilter == null) {
        return Observable.just(null);
      }
      return Observable.create(new BroadcastRegisterOnSubscribe(context, intentFilter, null, null))
          .doOnSubscribe(() -> openAccountManager(context, false))
          .flatMap(intent -> {
            if (AptoideAccountManager.LOGIN.equals(intent.getAction())) {
              return Observable.just(null);
            } else if (AptoideAccountManager.LOGIN_CANCELLED.equals(intent.getAction())) {
              return Observable.error(new LoginException("User cancelled login."));
            } else if (AptoideAccountManager.LOGOUT.equals(intent.getAction())) {
              return Observable.error(new LoginException("User logged out."));
            }
            return Observable.empty();
          });
    });
  }

  public boolean isLoggedIn() {
    return androidAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE).length != 0;
  }

  /**
   * This method should be used to open login or account activity
   *
   * @param openMyAccount true if is expeted to open myAccountActivity after login
   */
  public void openAccountManager(Context context, boolean openMyAccount) {
    Bundle extras = new Bundle();
    extras.putBoolean(LoginActivity.OPEN_MY_ACCOUNT_ON_LOGIN_SUCCESS, openMyAccount);
    openAccountManager(context, extras);
  }

  /**
   * This method should be used to open login or account activity
   *
   * @param extras Extras to add on created intent (to login or register activity)
   */
  public void openAccountManager(Context context, @Nullable Bundle extras) {
    if (isLoggedIn()) {
      context.startActivity(new Intent(context, MyAccountActivity.class));
    } else {
      final Intent intent = new Intent(context, LoginActivity.class);
      if (extras != null) {
        intent.putExtras(extras);
      }
      //Intent intent = new Intent(applicationContext, CreateStoreActivity.class);
      context.startActivity(intent);
    }
  }

  /**
   * This method should be used to open login or account activity
   *
   * @param useSkip true if skip button should be displayed and back arrow hided
   * @param openMyAccount true if is expeted to open myAccountActivity after login
   */
  public void openAccountManager(Context context, boolean useSkip, boolean openMyAccount) {
    Bundle extras = new Bundle();
    extras.putBoolean(LoginActivity.SKIP_BUTTON, useSkip);
    extras.putBoolean(LoginActivity.OPEN_MY_ACCOUNT_ON_LOGIN_SUCCESS, openMyAccount);
    openAccountManager(context, extras);
  }

  public static AptoideAccountManager getInstance(Context context,
      AptoidePreferencesConfiguration configuration, SecureCoderDecoder secureCoderDecoder,
      AccountManager androidAccountManager, AptoideClientUUID aptoideClientUUID) {
    if (instance == null) {
      instance = new AptoideAccountManager(aptoideClientUUID, context.getApplicationContext(),
          configuration, androidAccountManager, secureCoderDecoder);
    }
    return instance;
  }

  public void setupLogout(FragmentActivity activity, Button logoutButton) {
    final WeakReference<FragmentActivity> activityRef = new WeakReference(activity);
    if (configuration.isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.FACEBOOK)) {
      FacebookSdk.sdkInitialize(applicationContext);
    }
    logoutButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        logout(activityRef);
      }
    });
  }

  public void logout(WeakReference<FragmentActivity> activityRef) {
    if (configuration.isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.FACEBOOK)) {
      FacebookLoginUtils.logout();
    }
    removeLocalAccount();
    userIsLoggedIn = false;
    if (activityRef != null) {
      Activity activity = activityRef.get();
      if (activity != null) {
        GoogleLoginUtils.logout((FragmentActivity) activity);
        openAccountManager(activity);
        activity.finish();
      }
    }
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

  /**
   * This method should be used to open login or account activity
   */
  public void openAccountManager(Context context) {
    openAccountManager(context, null);
  }

  public LoginMode getLoginMode() {
    return AccountManagerPreferences.getLoginMode();
  }

  /**
   * Handles the answer given by sign in. It receives the data and inform the Aptoide server
   *
   * @param requestCode Given on onActivityResult method
   * @param resultCode Given on onActivityResult method
   * @param data Given on onActivityResult method
   */
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    if (configuration.isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.GOOGLE)) {
      GoogleLoginUtils.onActivityResult(requestCode, data, this);
    }
    if (configuration.isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.FACEBOOK)) {
      FacebookLoginUtils.onActivityResult(requestCode, resultCode, data);
    }
    AptoideLoginUtils.onActivityResult(activity, requestCode, resultCode, data, this);
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
  public void login(LoginMode mode, final String userName,
      final String passwordOrToken, final String nameForGoogle, Context context) {
    ProgressDialog genericPleaseWaitDialog = null;
    if (context != null) {
      genericPleaseWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(context);
      if (!((Activity) context).isFinishing()) {
        genericPleaseWaitDialog.show();
      }
    }
    OAuth2AuthenticationRequest oAuth2AuthenticationRequest =
        OAuth2AuthenticationRequest.of(userName, passwordOrToken, mode, nameForGoogle,
            aptoideClientUuid.getAptoideClientUUID(), this);
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

  public Completable login(LoginMode mode, final String username,
      final String password, final String name) {
    return OAuth2AuthenticationRequest.of(username, password, mode, name,
        aptoideClientUuid.getAptoideClientUUID(), this).observe().flatMap(oAuth -> {
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
        AccountManagerPreferences.setRepoTheme(
            user.getRepoDescription().getTheme());
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
  public void updateMatureSwitch(boolean matureSwitch) {
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

  /**
   * Method used when the given AccessToken is invalid or has expired. The method will ask to
   * server for other accessToken
   *
   * @see AptoideAccountManager#invalidateAccessTokenSync(Context)
   */
  public Observable<String> invalidateAccessToken(@NonNull Context context) {
    return Observable.fromCallable(() -> {
      if (AptoideUtils.ThreadU.isUiThread()) {
        throw new IllegalThreadStateException("This method shouldn't be called on ui " + "thread.");
      }
      return getRefreshToken();
    })
        .subscribeOn(Schedulers.io())
        .flatMap(
            s -> getNewAccessTokenFromRefreshToken(getRefreshToken(), getOnErrorAction(context)));
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

  private Observable<String> getNewAccessTokenFromRefreshToken(String refreshToken,
      Action1<Throwable> action1) {
    return OAuth2AuthenticationRequest.of(refreshToken, aptoideClientUuid.getAptoideClientUUID(),
        this)
        .observe()
        .observeOn(AndroidSchedulers.mainThread())
        .map(OAuth::getAccessToken)
        .subscribeOn(Schedulers.io())
        .doOnNext(accessToken -> {
          setAccessTokenOnLocalAccount(accessToken, null, SecureKeys.ACCESS_TOKEN);
          AccountManagerPreferences.setAccessToken(accessToken);
        })
        .doOnError(action1)
        .observeOn(AndroidSchedulers.mainThread());
  }

  private Action1<Throwable> getOnErrorAction(Context context) {
    return new Action1<Throwable>() {
      @Override public void call(Throwable throwable) {
        removeLocalAccount();
        openAccountManager(context);
      }
    };
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

  /**
   * Method used when the given AccessToken is invalid or has expired. The method will ask to
   * server for other accessToken. This request is synchronous.
   *
   * @return The new Access token
   * @see AptoideAccountManager#invalidateAccessToken(Context)
   */
  public String invalidateAccessTokenSync(@NonNull Context context) {
    if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
      throw new IllegalThreadStateException("This method shouldn't be called on ui thread.");
    }
    String refreshToken = getRefreshToken();
    //final String[] stringToReturn = { "" };
    //stringToReturn[0] =
    //    getNewAccessTokenFromRefreshToken(refreshToken, getOnErrorAction(context)).toBlocking()
    //        .first();
    //return stringToReturn[0];
    return getNewAccessTokenFromRefreshToken(refreshToken, getOnErrorAction(context)).toBlocking()
        .first();
  }

  void setupRegisterUser(IRegisterUser callback, Button signupButton, Context context) {
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
      CreateUserRequest.of(email, password, aptoideClientUuid.getAptoideClientUUID(), this)
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

  void sendLoginBroadcast() {
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

  /**
   * Method responsible to setup all login modes
   *
   * @param callback Callback used to let outsiders know if the login was
   * successful or
   * not
   * @param activity Activity where the login is being made
   * @param facebookLoginButton facebook login button
   * @param loginButton Aptoide login button
   * @param registerButton Aptoide register button
   */
  public void setupLogins(ILoginInterface callback, FragmentActivity activity,
      LoginButton facebookLoginButton, Button loginButton, Button registerButton) {
    this.callback = callback;
    View googleSignInButton = activity.findViewById(R.id.g_sign_in_button);
    if (configuration.isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.GOOGLE)
        && GoogleLoginUtils.isGoogleEnabledOnCurrentDevice(activity)) {
      GoogleLoginUtils.setUpGoogle(activity, googleSignInButton);
    } else {
      googleSignInButton.setVisibility(View.GONE);
    }
    if (configuration.isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.FACEBOOK)) {
      FacebookLoginUtils.setupFacebook(activity, facebookLoginButton, this);
    } else {
      facebookLoginButton.setVisibility(View.GONE);
    }
    AptoideLoginUtils.setupAptoideLogin(activity, loginButton, registerButton, this);
  }

  public void removeLogins() {
    this.callback = null;
  }

  public Completable createAccount(String userName, String userPassword,
      @Nullable String accountType, String refreshToken, String accessToken) {
    return Completable.defer(() -> {
      final Account account = new Account(userName, accountType != null ? accountType: Constants.ACCOUNT_TYPE);
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
    return CheckUserCredentialsRequest.of(this).observe()
        .flatMap(response -> {
          if (response.getStatus().equals("OK")) {
            return Observable.just(response);
          }
          return Observable.error(new LoginException("Failed to refresh account"));
        })
        .doOnNext(user -> saveUser(user))
        .toCompletable();
  }

  /**
   * Get the accessToken used to authenticate user on aptoide webservices
   *
   * @return A string with the token
   */
  @Nullable public String getAccessToken() {
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
    if (loginOrigin.equals("signup")) {
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
    }
  }

  void onLoginSuccess() {
    userIsLoggedIn = true;
    callback.onLoginSuccess();
  }

  void sendRemoveLocalAccountBroadcaster(Context context) {
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

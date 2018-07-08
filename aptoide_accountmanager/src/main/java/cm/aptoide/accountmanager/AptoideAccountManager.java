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
import android.app.Application;
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
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.preferences.Application.getConfiguration;
import static cm.aptoide.pt.preferences.Application.getContext;

/**
 * Created by trinkes on 4/18/16. <li>{@link #openAccountManager(Context)}</li> <li>{@link
 * #openAccountManager(Context, boolean)}</li> <li>{@link #openAccountManager(Context, * Bundle)}</li>
 * <li>{@link #openAccountManager(Context, Bundle, boolean)}</li> <li>{@link
 * #getAccessToken()}</li>
 * <li>{@link #getUserEmail()}</li> <li>{@link #onActivityResult(Activity, int, int, Intent)}</li>
 * <li>{@link #getUserData()}</li> <li>{@link #updateMatureSwitch(boolean)}</li> <li>{@link
 * #invalidateAccessToken(Context)}</li> <li>{@link #invalidateAccessTokenSync(Context)}</li>
 * <li>{@link #ACCOUNT_REMOVED_BROADCAST_KEY}</li>
 */
public class AptoideAccountManager implements Application.ActivityLifecycleCallbacks {

  public static final String LOGIN =
      getConfiguration().getAppId() + ".accountmanager.broadcast.login";
  public static final String LOGIN_CANCELLED =
      getConfiguration().getAppId() + ".accountmanager.broadcast.LOGIN_CANCELLED";
  public static final String LOGOUT =
      getConfiguration().getAppId() + ".accountmanager.broadcast.logout";

  /**
   * This constant is used to send the broadcast when an account is removed
   */
  public static final String ACCOUNT_REMOVED_BROADCAST_KEY =
      "cm.aptoide.accountmanager" + "" + ".removedaccount.broadcast";

  private final static AptoideAccountManager instance = new AptoideAccountManager();
  private static final AptoideClientUUID aptoideClientUuid =
      new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
          DataProvider.getContext());
  private static String TAG = AptoideAccountManager.class.getSimpleName();
  /**
   * This variable indicates if the user is logged or not. It's used because in some cases the
   * account manager is not fast enough
   */
  private static boolean userIsLoggedIn = isLoggedIn();
  @Setter @Getter(AccessLevel.PACKAGE) private static Analytics analytics;
  /**
   * private variables
   */

  @Setter private static ILoginInterface mCallback;
  private WeakReference<Context> mContextWeakReference;

  public static Observable<Void> login(Context context) {
    return Observable.fromCallable(() -> {
      if (AptoideAccountManager.isLoggedIn()) {
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
          .doOnSubscribe(() -> AptoideAccountManager.openAccountManager(context, false))
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

  public static boolean isLoggedIn() {
    AccountManager manager = AccountManager.get(getContext());
    return manager.getAccountsByType(Constants.ACCOUNT_TYPE).length != 0;
  }

  /**
   * This method should be used to open login or account activity
   *
   * @param openMyAccount true if is expeted to open myAccountActivity after login
   */
  public static void openAccountManager(Context context, boolean openMyAccount) {
    Bundle extras = new Bundle();
    extras.putBoolean(LoginActivity.OPEN_MY_ACCOUNT_ON_LOGIN_SUCCESS, openMyAccount);
    openAccountManager(context, extras);
  }

  /**
   * This method should be used to open login or account activity
   *
   * @param extras Extras to add on created intent (to login or register activity)
   */
  public static void openAccountManager(Context context, @Nullable Bundle extras) {
    if (userIsLoggedIn) {
      context.startActivity(new Intent(context, MyAccountActivity.class));
    } else {
      final Intent intent = new Intent(context, LoginActivity.class);
      if (extras != null) {
        intent.putExtras(extras);
      }
      //Intent intent = new Intent(getContext(), CreateStoreActivity.class);
      context.startActivity(intent);
    }
  }

  /**
   * This method should be used to open login or account activity
   *
   * @param extras Extras to add on created intent (to login or register activity)
   * @param openMyAccount true if is expeted to open myAccountActivity after login
   */
  public static void openAccountManager(Context context, @Nullable Bundle extras,
      boolean openMyAccount) {
    if (extras == null) {
      extras = new Bundle();
    }
    extras.putBoolean(LoginActivity.OPEN_MY_ACCOUNT_ON_LOGIN_SUCCESS, openMyAccount);
    openAccountManager(context, extras);
  }

  /**
   * This method should be used to open login or account activity
   *
   * @param useSkip true if skip button should be displayed and back arrow hided
   * @param openMyAccount true if is expeted to open myAccountActivity after login
   */
  public static void openAccountManager(Context context, boolean useSkip, boolean openMyAccount) {
    Bundle extras = new Bundle();
    extras.putBoolean(LoginActivity.SKIP_BUTTON, useSkip);
    extras.putBoolean(LoginActivity.OPEN_MY_ACCOUNT_ON_LOGIN_SUCCESS, openMyAccount);
    openAccountManager(context, extras);
  }

  /**
   * This method should be used to login users for Mobile verification Login
   *
   * @param phoneNumber users inserted phonenumber
   * @param token users token sended by the Mobile provider
   * @param username users inserted username
   */
  @Partners public static void openAccountManager(ILoginInterface loginInterface, Context context,
      String phoneNumber, String token, String username) {
    setMCallback(loginInterface);
    switch (cm.aptoide.pt.preferences.Application.getConfiguration().getDefaultStore()) {
      case "aban-app-store":
        AptoideAccountManager.loginUserCredentials(LoginMode.ABAN, phoneNumber, token, username,
            context);
        break;
      case "dsc-market":
        AptoideAccountManager.loginUserCredentials(LoginMode.DSC, phoneNumber, token, username,
            context);
        break;
      default:
        throw new IllegalArgumentException(
            "Unknown Login Mode! Please check if you added to the LoginMode Enum");
    }
  }

  static AptoideAccountManager getInstance() {
    return instance;
  }

  static void setupLogout(FragmentActivity activity, Button logoutButton) {
    final WeakReference<FragmentActivity> activityRef = new WeakReference(activity);
    if (getConfiguration().isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.FACEBOOK)) {
      FacebookSdk.sdkInitialize(getContext());
    }
    logoutButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        logout(activityRef);
      }
    });
  }

  public static void logout(WeakReference<FragmentActivity> activityRef) {
    if (getConfiguration().isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.FACEBOOK)) {
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
    getContext().sendBroadcast(new Intent().setAction(LOGOUT));
  }

  public static void removeLocalAccount() {
    AccountManager manager = AccountManager.get(getContext());
    Account[] accounts = manager.getAccountsByType(Constants.ACCOUNT_TYPE);
    userIsLoggedIn = false;
    for (Account account : accounts) {
      if (Build.VERSION.SDK_INT >= 22) {
        manager.removeAccountExplicitly(account);
      } else {
        manager.removeAccount(account, null, null);
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
  public static void openAccountManager(Context context) {
    openAccountManager(context, null);
  }

  @Nullable public static LoginMode getLoginMode() {
    return AccountManagerPreferences.getLoginMode();
  }

  /**
   * Handles the answer given by sign in. It receives the data and inform the Aptoide server
   *
   * @param requestCode Given on onActivityResult method
   * @param resultCode Given on onActivityResult method
   * @param data Given on onActivityResult method
   */
  public static void onActivityResult(Activity activity, int requestCode, int resultCode,
      Intent data) {
    if (getConfiguration().isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.GOOGLE)) {
      GoogleLoginUtils.onActivityResult(requestCode, data);
    }
    if (getConfiguration().isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.FACEBOOK)) {
      FacebookLoginUtils.onActivityResult(requestCode, resultCode, data);
    }
    AptoideLoginUtils.onActivityResult(activity, requestCode, resultCode, data);
  }

  /**
   * make the request to the server for login using the user credentials
   *
   * @param mode login mode, ca be facebook, aptoide or google
   * @param userName user's username usually the email address
   * @param passwordOrToken user password or token given by google or facebook
   * @param nameForGoogle name given by google
   */
  static void loginUserCredentials(LoginMode mode, final String userName,
      final String passwordOrToken, final String nameForGoogle) {
    loginUserCredentials(mode, userName, passwordOrToken, nameForGoogle,
        getInstance().mContextWeakReference.get());
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
  static void loginUserCredentials(LoginMode mode, final String userName,
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
            aptoideClientUuid.getUniqueIdentifier());
    final ProgressDialog finalGenericPleaseWaitDialog = genericPleaseWaitDialog;
    oAuth2AuthenticationRequest.execute(oAuth -> {
      Logger.d(TAG, "onSuccess() called with: " + "oAuth = [" + oAuth + "]");

      if (!oAuth.hasErrors()) {
        AccountManagerPreferences.setAccessToken(oAuth.getAccessToken());

        getInstance().addLocalUserAccount(userName, passwordOrToken, null, oAuth.getRefresh_token(),
            oAuth.getAccessToken(), context).subscribe(isSuccess -> {
          if (isSuccess) {
            setAccessTokenOnLocalAccount(oAuth.getAccessToken(), null, SecureKeys.ACCESS_TOKEN);
            AccountManagerPreferences.setLoginMode(mode);
            getInstance().onLoginSuccess(mode, "", "", "");
            if (finalGenericPleaseWaitDialog != null) {
              finalGenericPleaseWaitDialog.dismiss();
            }
            sendLoginBroadcast();
          }
        }, throwable -> {
          removeLocalAccount();
          getInstance().onLoginFail(getContext().getString(R.string.unknown_error));
          CrashReport.getInstance().log(throwable);
          if (finalGenericPleaseWaitDialog != null) {
            finalGenericPleaseWaitDialog.dismiss();
          }
        });
      } else { // oAuth.hasErrors() = true

        if (finalGenericPleaseWaitDialog != null) {
          finalGenericPleaseWaitDialog.dismiss();
        }

        getInstance().onLoginFail(getContext().getString(R.string.unknown_error));
      }
      Logger.e(TAG, "Error while adding the local account. Probably context was null");
    }, new ErrorRequestListener() {
      @Override public void onError(Throwable e) {
        try {
          if (e instanceof AptoideWsV3Exception) {
            GenericResponseV3 oAuth = ((AptoideWsV3Exception) e).getBaseResponse();
            getInstance().onLoginFail(getContext().getString(
                ErrorsMapper.getWebServiceErrorMessageFromCode(oAuth.getError())));
          } else {
            getInstance().onLoginFail(getContext().getString(R.string.unknown_error));
          }
        } finally {
          if (finalGenericPleaseWaitDialog != null) {
            finalGenericPleaseWaitDialog.dismiss();
          }
        }
      }
    }, true);
  }

  /**
   * Save user info on secured shared preferences
   *
   * @param checkUserCredentialsJson Object returned by webservice(CheckUserCredentialsRequest)
   * with the user info
   */
  private static void saveUserInfo(CheckUserCredentialsJson checkUserCredentialsJson) {
    Logger.d(TAG, "saveUserInfo() called with: "
        + "checkUserCredentialsJson = ["
        + checkUserCredentialsJson
        + "]");

    if (checkUserCredentialsJson.getStatus().equals("OK")) {

      // TODO are these validations really needed?
      // why not just store the values, null or not?

      AccountManagerPreferences.setUserId(checkUserCredentialsJson.getId());

      if (!TextUtils.isEmpty(checkUserCredentialsJson.getQueueName())) {
        //hasQueue = true;
        AccountManagerPreferences.setQueueName(checkUserCredentialsJson.getQueueName());
      }

      if (!TextUtils.isEmpty(checkUserCredentialsJson.getAvatar())) {
        AccountManagerPreferences.setUserAvatar(checkUserCredentialsJson.getAvatar());
      }

      if (!TextUtils.isEmpty(checkUserCredentialsJson.getRavatarHd())) {
        AccountManagerPreferences.setRepoAvatar(checkUserCredentialsJson.getRavatarHd());
      }

      if (!TextUtils.isEmpty(checkUserCredentialsJson.getRepo())) {
        AccountManagerPreferences.setUserRepo(checkUserCredentialsJson.getRepo());
      }

      if (!TextUtils.isEmpty(checkUserCredentialsJson.getUsername())) {
        AccountManagerPreferences.setUserNickName(checkUserCredentialsJson.getUsername());
      }

      if (checkUserCredentialsJson.getRepoDescription() != null && !TextUtils.isEmpty(
          checkUserCredentialsJson.getRepoDescription().getTheme())) {
        AccountManagerPreferences.setRepoTheme(
            checkUserCredentialsJson.getRepoDescription().getTheme());
      }

      if (!TextUtils.isEmpty(checkUserCredentialsJson.getAccess())) {
        ManagerPreferences.setUserAccess(checkUserCredentialsJson.getAccess());
      }

      if (!checkUserCredentialsJson.getAccessConfirmed().toString().isEmpty()) {
        ManagerPreferences.setUserAccessConfirmed(checkUserCredentialsJson.getAccessConfirmed());
      }

      if (checkUserCredentialsJson.getSettings() != null) {
        AccountManagerPreferences.setMatureSwitch(
            checkUserCredentialsJson.getSettings().getMatureswitch().equals("active"));
      }
    }
  }

  /**
   * This method creates a new UserInfo object with all the user info
   *
   * @return User info class with all collected information about the user
   */
  public static UserCompleteData getUserData() {
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
  public static boolean isMatureSwitchOn() {
    return AccountManagerPreferences.getMatureSwitch();
  }

  /**
   * Update the mature switch. If user is logged, it updates on aptoide's server too
   *
   * @param matureSwitch Switch state
   */
  @Partners public static void updateMatureSwitch(boolean matureSwitch) {
    AccountManagerPreferences.setMatureSwitch(matureSwitch);
    if (userIsLoggedIn) {
      ChangeUserSettingsRequest.of(matureSwitch)
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
  public static Observable<String> invalidateAccessToken(@NonNull Context context) {
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

  public static @Nullable String getRefreshToken() {
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

  private static Observable<String> getNewAccessTokenFromRefreshToken(String refreshToken,
      Action1<Throwable> action1) {
    return OAuth2AuthenticationRequest.of(refreshToken, aptoideClientUuid.getUniqueIdentifier())
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

  private static Action1<Throwable> getOnErrorAction(Context context) {
    return new Action1<Throwable>() {
      @Override public void call(Throwable throwable) {
        removeLocalAccount();
        openAccountManager(context);
      }
    };
  }

  private static String getUserStringFromAndroidAccountManager(String key) {
    AccountManager manager = AccountManager.get(getContext());
    Account[] accountsByType = manager.getAccountsByType(Constants.ACCOUNT_TYPE);

    return accountsByType.length > 0 ? manager.getUserData(accountsByType[0], key) : null;
  }

  private static @Nullable String getRefreshTokenFromAccountManager()
      throws AuthenticatorException, OperationCanceledException, IOException {
    AccountManager manager = AccountManager.get(getContext());
    Account[] accountsByType = manager.getAccountsByType(Constants.ACCOUNT_TYPE);
    String refreshToken =
        manager.blockingGetAuthToken(accountsByType[0], Constants.AUTHTOKEN_TYPE_FULL_ACCESS,
            false);
    return refreshToken;
  }

  public static void setAccessTokenOnLocalAccount(String accessToken, @Nullable Account userAccount,
      @NonNull String dataKey) {
    AccountManager accountManager = AccountManager.get(getContext());

    if (userAccount == null) {
      Account[] accounts = accountManager.getAccounts();
      for (final Account account : accounts) {
        if (TextUtils.equals(account.name, AptoideAccountManager.getUserEmail())
            && TextUtils.equals(account.type, Constants.ACCOUNT_TYPE)) {
          userAccount = account;
          break;
        }
      }
    } else {
      accountManager.setUserData(userAccount, dataKey, accessToken);
    }
  }

  /**
   * Get the userEmail of current logged user
   *
   * @return A string with the userEmail
   */
  public static String getUserEmail() {
    String userName = AccountManagerPreferences.getUserEmail();
    if (userName == null || TextUtils.isEmpty(userName)) {
      AccountManager accountManager = AccountManager.get(getContext());
      Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
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
  public static String invalidateAccessTokenSync(@NonNull Context context) {
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

  static void setupRegisterUser(IRegisterUser callback, Button signupButton) {
    final WeakReference callBackWeakReference = new WeakReference(callback);
    signupButton.setOnClickListener(v -> {
      IRegisterUser callback1 = (IRegisterUser) callBackWeakReference.get();
      if (callback1 != null) {
        ProgressDialog genericPleaseWaitDialog =
            GenericDialogs.createGenericPleaseWaitDialog(v.getContext());
        genericPleaseWaitDialog.show();
        registerUserUsingWebServices(callback1, genericPleaseWaitDialog);
      }
    });
  }

  static void registerUserUsingWebServices(IRegisterUser callback,
      ProgressDialog genericPleaseWaitDialog) {
    String email = callback.getUserEmail();
    String password = callback.getUserPassword();
    if (validateUserCredentials(callback, email, password)) {
      CreateUserRequest.of(email, password, aptoideClientUuid.getUniqueIdentifier())
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
              AptoideAccountManager.loginUserCredentials(LoginMode.APTOIDE, email, password, null);
            }
            genericPleaseWaitDialog.dismiss();
            CrashReport.getInstance().log(e);
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
  private static boolean validateUserCredentials(IRegisterUser callback, String email,
      String password) {
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
   * Validate if user credentials are valid
   *
   * @return true if credentials are valid, false otherwise.
   */
  private static boolean validateUserCredentials(IRegisterUser callback, String username) {
    boolean toReturn = true;
    if (username.length() == 0) {
      callback.onRegisterFail(R.string.no_email_and_pass_error_message);
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
  private static boolean has1number1letter(String password) {
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

  public static void unsubscribeStore(String storeName) {
    ChangeUserRepoSubscriptionRequest.of(storeName, false)
        .execute(genericResponseV3 -> Logger.d(TAG, "Successfully unsubscribed " + storeName),
            true);
  }

  public static void subscribeStore(String storeName) {
    ChangeUserRepoSubscriptionRequest.of(storeName, true)
        .execute(genericResponseV3 -> Logger.d(TAG, "Successfully subscribed " + storeName), true);
  }

  static void sendLoginBroadcast() {
    getContext().sendBroadcast(new Intent().setAction(LOGIN));
  }

  public static void sendLoginCancelledBroadcast() {
    getContext().sendBroadcast(new Intent().setAction(LOGIN_CANCELLED));
  }

  public static Observable<List<Subscription>> getUserRepos() {
    return GetUserRepoSubscriptionRequest.of()
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
  protected void setupLogins(ILoginInterface callback, FragmentActivity activity,
      LoginButton facebookLoginButton, Button loginButton, Button registerButton) {
    mCallback = callback;
    this.mContextWeakReference = new WeakReference<>(activity);
    View googleSignInButton = activity.findViewById(R.id.g_sign_in_button);
    if (getConfiguration().isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.GOOGLE)
        && GoogleLoginUtils.isGoogleEnabledOnCurrentDevice(activity)) {
      GoogleLoginUtils.setUpGoogle(activity, googleSignInButton);
    } else {
      googleSignInButton.setVisibility(View.GONE);
    }
    if (getConfiguration().isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.FACEBOOK)) {
      FacebookLoginUtils.setupFacebook(activity, facebookLoginButton);
    } else {
      facebookLoginButton.setVisibility(View.GONE);
    }
    AptoideLoginUtils.setupAptoideLogin(activity, loginButton, registerButton);
    activity.getApplication().registerActivityLifecycleCallbacks(this);
  }

  /**
   * This method adds an new local account
   *
   * @param userName This will be used to identify the account
   * @param userPassword password to access the account
   * @param accountType account type
   * @param refreshToken Refresh token to be saved
   * @param accessToken AccessToken to be used on CheckUserCredentialsRequest
   * @return true if the account was added successfully, false otherwise
   */
  Observable<Boolean> addLocalUserAccount(String userName, String userPassword,
      @Nullable String accountType, String refreshToken, String accessToken) {
    return addLocalUserAccount(userName, userPassword, accountType, refreshToken, accessToken,
        mContextWeakReference.get());
  }

  /**
   * This method adds an new local account
   *
   * @param userName This will be used to identify the account
   * @param userPassword password to access the account
   * @param accountType account type
   * @param refreshToken Refresh token to be saved
   * @param accessToken AccessToken to be used on CheckUserCredentialsRequest
   * @param context given context
   * @return true if the account was added successfully, false otherwise
   */
  Observable<Boolean> addLocalUserAccount(String userName, String userPassword,
      @Nullable String accountType, String refreshToken, String accessToken, Context context) {
    if (context != null) {
      AccountManager accountManager = AccountManager.get(context);
      accountType = accountType != null ? accountType
          // TODO: 4/21/16 trinkes if needed, account type has to match with partners
          // version
          : Constants.ACCOUNT_TYPE;

      final Account account = new Account(userName, accountType);

      // Creating the account on the device and setting the auth token we got
      // (Not setting the auth token will cause another call to the server to authenticate
      // the user)
      SecureCoderDecoder secureCoderDecoder = new SecureCoderDecoder.Builder(context).create();
      String encryptPassword = secureCoderDecoder.encrypt(userPassword);
      try {
        accountManager.addAccountExplicitly(account, encryptPassword, null);
      } catch (SecurityException e) {
        CrashReport.getInstance().log(e);
      }
      accountManager.setUserData(account, SecureKeys.REFRESH_TOKEN, refreshToken);
      AccountManagerPreferences.setRefreshToken(refreshToken);

      return refreshAndSaveUserInfoData().doOnError(e -> {
        CrashReport.getInstance().log(e);
      }).map(userData -> true);
    }
    return Observable.just(false);
  }

  public static Observable<CheckUserCredentialsJson> refreshAndSaveUserInfoData() {
    return refreshUserInfoData().doOnNext(AptoideAccountManager::saveUserInfo);
  }

  public static Observable<CheckUserCredentialsJson> refreshUserInfoData() {
    return CheckUserCredentialsRequest.of(getAccessToken())
        .observe()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .doOnError(e -> {
          CrashReport.getInstance().log(e);
        });
  }

  /**
   * Get the accessToken used to authenticate user on aptoide webservices
   *
   * @return A string with the token
   */
  @Partners @Nullable public static String getAccessToken() {
    String accessToken = AccountManagerPreferences.getAccessToken();
    if (accessToken == null || TextUtils.isEmpty(accessToken)) {
      accessToken = getUserStringFromAndroidAccountManager(SecureKeys.ACCESS_TOKEN);
      AccountManagerPreferences.setAccessToken(accessToken);
    }
    return accessToken;
  }

  void onLoginFail(String reason) {
    if (mCallback != null) mCallback.onLoginFail(reason);
  }

  void onLoginSuccess(LoginMode loginType, String loginOrigin, String username, String password) {

    userIsLoggedIn = true;

    if (mCallback != null) {
      mCallback.onLoginSuccess();
    }
    if (analytics != null) {
      analytics.login(loginType.name());
    }
    if (loginOrigin.equals("signup")) {
      Intent intent = new Intent(getContext(), CreateUserActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra(AptoideLoginUtils.APTOIDE_LOGIN_USER_NAME_KEY, username);
      intent.putExtra(AptoideLoginUtils.APTOIDE_LOGIN_PASSWORD_KEY, password);
      intent.putExtra(AptoideLoginUtils.APTOIDE_LOGIN_ACCESS_TOKEN_KEY, getAccessToken());
      getContext().startActivity(intent);
    }

    if ((loginType.equals(LoginMode.FACEBOOK) || loginType.equals(LoginMode.GOOGLE))
        && !ManagerPreferences.getUserAccessConfirmed()) {
      Intent socialIntent = new Intent(getContext(), LoggedInActivity.class);
      socialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      socialIntent.putExtra(AptoideLoginUtils.IS_FACEBOOK_OR_GOOGLE, true);
      getContext().startActivity(socialIntent);
    }
  }

  void onLoginSuccess() {
    userIsLoggedIn = true;
    mCallback.onLoginSuccess();
  }

  void sendRemoveLocalAccountBroadcaster() {
    Intent intent = new Intent();
    intent.setAction(ACCOUNT_REMOVED_BROADCAST_KEY);
    getContext().sendBroadcast(intent);
  }

  /********************************************************
   * activity lifecycle
   */

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

  }

  @Override public void onActivityStarted(Activity activity) {

  }

  @Override public void onActivityResumed(Activity activity) {

  }

  @Override public void onActivityPaused(Activity activity) {

  }

  @Override public void onActivityStopped(Activity activity) {

  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

  }

  @Override public void onActivityDestroyed(Activity activity) {
    if (activity instanceof LoginActivity) {
      mCallback = null;
    }
  }

  /**
   * get user name introduced in edit text by user
   *
   * @return The user name introduced by user
   */
  String getIntroducedUserName() {
    return mCallback == null ? null : mCallback.getIntroducedUserName();
  }

  /**
   * get password introduced in edit text by user
   *
   * @return The password introduced by user
   */
  String getIntroducedPassword() {
    return mCallback == null ? null : mCallback.getIntroducedPassword();
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

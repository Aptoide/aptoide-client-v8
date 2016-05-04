package cm.aptoide.accountmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;

import cm.aptoide.accountmanager.ws.CreateUserRequest;
import cm.aptoide.accountmanager.ws.ErrorsMapper;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.accountmanager.ws.OAuth2AuthenticationRequest;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 4/18/16.
 */
public class AptoideAccountManager implements Application.ActivityLifecycleCallbacks {

	public static final String ACCOUNT_REMOVED_BROADCAST_KEY = "cm.aptoide.accountmanager" + "" +
			".removedaccount.broadcast";
	public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
	public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
	public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
	public final static String ARG_OPTIONS_BUNDLE = "BE";
	/**
	 * Auth token types
	 */
	public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Aptoide " +
			"account";
	public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Aptoide "
			+ "account";
	public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
	public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
	/**
	 * Account type id
	 */
	public static final String ACCOUNT_TYPE = "cm.aptoide.pt";
	private final static AptoideAccountManager instance = new AptoideAccountManager();
	private static String TAG = AptoideAccountManager.class.getSimpleName();
	/**
	 * private variables
	 */
	private ILoginInterface mCallback;
	private WeakReference<Context> mContextWeakReference;

	/**
	 * This method should be used to open login or account activity
	 *
	 * @param extras Extras to add on created intent (to login or register activity)
	 */
	public static void openAccountManager(Context context, @Nullable Bundle extras) {
		Log.d(TAG, "openAccountManager() called with: " + "context = [" + context + "], extras =" +
				" " +
				"[" + extras + "]");
		if (isLoggedIn(context)) {
			context.startActivity(new Intent(context, MyAccountActivity.class));
		} else {
			Intent intent = new Intent(context, LoginActivity.class);
			if (extras != null) {
				intent.putExtras(extras);
			}
			context.startActivity(intent);
		}
	}

	/**
	 * This method should be used to open login or account activity
	 */
	public static void openAccountManager(Context context) {
		openAccountManager(context, null);
	}

	public static boolean isLoggedIn(Context context) {
		AccountManager manager = android.accounts.AccountManager.get(context);
		return manager.getAccountsByType(Constants.ACCOUNT_TYPE).length != 0;
	}

	public static AptoideAccountManager getInstance() {
		return instance;
	}

	public static void onStop() {
		instance.mCallback = null;
	}

	public static void setupLogout(FragmentActivity activity, Button logoutButton) {
		final WeakReference<FragmentActivity> activityRef = new WeakReference(activity);
		FacebookSdk.sdkInitialize(cm.aptoide.pt.preferences.Application.getContext());
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logout(activityRef);
			}
		});
		GoogleLoginUtils.setupGoogleApiClient(activity);
	}

	private static void logout(WeakReference<FragmentActivity> activityRef) {
		FacebookLoginUtils.logout();
		GoogleLoginUtils.logout();
		getInstance().removeLocalAccount();
		Activity activity = activityRef.get();
		if (activity != null) {
			openAccountManager(activity);
			activity.finish();
		}
	}

	private static String getRefreshToken(Activity context) {
		String refreshToken = AccountManagerPreferences.getRefreshToken();
		if (refreshToken == null || TextUtils.isEmpty(refreshToken)) {
			AccountManager accountManager = AccountManager.get(cm.aptoide.pt.preferences
					.Application
					.getContext());
			Account[] accountsByType = accountManager.getAccountsByType(ACCOUNT_TYPE);
			//we only allow 1 aptoide account

			if (accountsByType.length > 0) {
				AccountManagerFuture<Bundle> authToken = accountManager.getAuthToken
						(accountsByType[0], AUTHTOKEN_TYPE_FULL_ACCESS, null, context, null, null);
				try {
					Bundle result = authToken.getResult();
					refreshToken = result.getString(AccountManager.KEY_AUTHTOKEN);
					AccountManagerPreferences.setRefreshToken(refreshToken);
				} catch (OperationCanceledException | IOException | AuthenticatorException e) {
					e.printStackTrace();
				}
			}
		}
		return refreshToken;
	}

	/**
	 * Get the accessToken used to authenticate user on aptoide webservices
	 *
	 * @return A string with the token
	 */
	public static String getAccessToken() {
		return AccountManagerPreferences.getAccessToken();
	}

	/**
	 * Get the userName of current logged user
	 *
	 * @return A string with the userName
	 */
	public static String getUserName() {
		String userName = AccountManagerPreferences.getUserName();
		if (userName == null || TextUtils.isEmpty(userName)) {
			AccountManager accountManager = AccountManager.get(cm.aptoide.pt.preferences
					.Application
					.getContext());
			Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
			if (accounts.length > 0) {
				userName = accounts[0].name;
				AccountManagerPreferences.setUserName(userName);
			}
		}
		return userName;
	}

	/**
	 * Handles the answer given by sign in. It receives the data and inform the Aptoide server
	 *
	 * @param requestCode Given on onActivityResult method
	 * @param resultCode  Given on onActivityResult method
	 * @param data        Given on onActivityResult method
	 * @return true if the login was successful, false otherwise
	 */
	protected static void onActivityResult(Activity activity, int requestCode, int resultCode,
										   Intent data) {
		GoogleLoginUtils.onActivityResult(requestCode, data);
		FacebookLoginUtils.onActivityResult(requestCode, resultCode, data);
		AptoideLoginUtils.onActivityResult(activity, requestCode, resultCode, data);
	}

	/**
	 * make the request to the server for login using the user credentials
	 *
	 * @param mode            login mode, ca be facebook, aptoide or google
	 * @param userName        user's username usually the email address
	 * @param passwordOrToken user password or token given by google or facebook
	 * @param nameForGoogle   name given by google
	 */
	static void loginUserCredentials(LoginMode mode, final String userName, final String
			passwordOrToken, final String nameForGoogle) {
		OAuth2AuthenticationRequest oAuth2AuthenticationRequest = OAuth2AuthenticationRequest.of
				(userName, passwordOrToken, mode, nameForGoogle);
		oAuth2AuthenticationRequest.execute(new SuccessRequestListener<OAuth>() {
			@Override
			public void onSuccess(OAuth oAuth) {
				Log.d(TAG, "onSuccess() called with: " + "oAuth = [" + oAuth + "]");
				boolean loginSuccessful = getInstance().addLocalUserAccount(userName,
						passwordOrToken, null, oAuth
						.getRefresh_token());
				if (loginSuccessful && !oAuth.hasErrors()) {
					AccountManagerPreferences.setAccessToken(oAuth.getAccessToken());
					getInstance().onLoginSuccess();
				} else {
					getInstance().onLoginFail(cm.aptoide.pt.preferences.Application.getContext()
							.getString(R.string.unknown_error));
					Log.e(TAG, "Error while adding the local account. Probably context was null");
				}
			}
		}, e -> getInstance().onLoginFail(e.getMessage()));
	}

	/**
	 * Method used when the given AccessToken is invalid or has expired. The method will ask to
	 * server for other accessToken
	 */
	public static Observable<String> invalidateAccessToken(@NonNull Activity context) {
		return Observable.fromCallable(() -> {
			if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
				throw new IllegalThreadStateException("This method shouldn't be called on ui " +
						"thread.");
			}
			return getRefreshToken(context);
		})
				.subscribeOn(Schedulers.io())
				.flatMap(AptoideAccountManager::getNewAccessTokenFromRefreshToken);
	}

	/**
	 * Method used when the given AccessToken is invalid or has expired. The method will ask to
	 * server for other accessToken
	 *
	 * @return The new Access token
	 */
	public static String invalidateAccessTokenSync(@NonNull Activity context) {
		if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
			throw new IllegalThreadStateException("This method shouldn't be called on ui thread.");
		}
		String refreshToken = getRefreshToken(context);
		final String[] stringToReturn = {""};
		getNewAccessTokenFromRefreshToken(refreshToken).toBlocking().subscribe((token) -> {
			stringToReturn[0] = token;
		});
		return stringToReturn[0];
	}

	private static Observable<String> getNewAccessTokenFromRefreshToken(String refreshToken) {
		Log.d(TAG, "invalidateAccessTokenSync: " + new Date().getTime());
		return OAuth2AuthenticationRequest.of(refreshToken)
				.observe()
				.map(OAuth::getAccessToken)
				.doOnNext(AccountManagerPreferences::setAccessToken);
	}

	public static void setupRegisterUser(IRegisterUser callback, Button signupButton) {
		final WeakReference callBackWeakReference = new WeakReference(callback);
		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IRegisterUser callback = (IRegisterUser) callBackWeakReference.get();
				if (callback != null) {
					RegisterUser(callback);
				}
			}
		});
	}

	public static void RegisterUser(IRegisterUser callback) {
		String email = callback.getUserEmail();
		String password = callback.getUserPassword();
		if (validateUserCredentials(callback, email, password)) {
			CreateUserRequest.of(email, password).execute(oAuth -> {
				if (oAuth.hasErrors()) {
					if (oAuth.getErrors() != null && oAuth.getErrors().size() > 0) {
						callback.onRegisterFail(ErrorsMapper.getErrorsMap()
								.get(oAuth.getErrors().get(0).code));
					} else {
						callback.onRegisterFail(R.string.unknown_error);
					}
				} else {
					Bundle bundle = new Bundle();
					bundle.putString(AptoideLoginUtils.APTOIDE_LOGIN_USER_NAME_KEY, email);
					bundle.putString(AptoideLoginUtils.APTOIDE_LOGIN_PASSWORD_KEY, password);
					bundle.putString(AptoideLoginUtils.APTOIDE_LOGIN_REFRESH_TOKEN_KEY, oAuth
							.getRefresh_token());
					bundle.putString(AptoideLoginUtils.APTOIDE_LOGIN_ACCESS_TOKEN_KEY, oAuth
							.getAccessToken());
					callback.onRegisterSuccess(bundle);
				}
			});
		}
	}

	/**
	 * Validate if user credentials are valid
	 *
	 * @return true if credentials are valid, false otherwise.
	 */
	private static boolean validateUserCredentials(IRegisterUser callback, String email, String
			password) {
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
		if (password.contains("!") || password.contains("@") || password.contains("#") || password
				.contains("$") || password
				.contains("#") || password.contains("*")) {
			hasNumber = true;
		}

		return hasNumber && hasLetter;
	}

	private void removeLocalAccount() {
		AccountManager manager = android.accounts.AccountManager.get(cm.aptoide.pt.preferences
				.Application
				.getContext());
		Account[] accounts = manager.getAccountsByType(Constants.ACCOUNT_TYPE);
		for (Account account : accounts) {
			if (Build.VERSION.SDK_INT >= 22) {
				manager.removeAccountExplicitly(account);
			} else {
				manager.removeAccount(account, null, null);
			}
		}
		AccountManagerPreferences.removeUserName();
		AccountManagerPreferences.removeAccessToken();
		AccountManagerPreferences.removeRefreshToken();
	}

	/**
	 * Method responsible to setup all login modes
	 *
	 * @param callback            Callback used to let outsiders know if the login was
	 *                               successful or
	 *
	 *                            not
	 * @param activity            Activity where the login is being made
	 * @param facebookLoginButton facebook login button
	 * @param loginButton         Aptoide login button
	 * @param registerButton      Aptoide register button
	 */
	protected void setupLogins(ILoginInterface callback, FragmentActivity activity, LoginButton
			facebookLoginButton, Button loginButton, Button registerButton) {
		this.mCallback = callback;
		this.mContextWeakReference = new WeakReference<>(activity.getApplicationContext());
		GoogleLoginUtils.setUpGoogle(activity);
		FacebookLoginUtils.setupFacebook(activity, facebookLoginButton);
		AptoideLoginUtils.setupAptoideLogin(activity, loginButton, registerButton);
		activity.getApplication().registerActivityLifecycleCallbacks(this);
	}

	/**
	 * this method adds an new local account
	 *
	 * @param userName     This will be used to identify the account
	 * @param userPassword password to access the account
	 * @param accountType  account type
	 * @param refreshToken Refresh token to be saved
	 * @return true of the account was added successfully, false otherwise
	 */
	boolean addLocalUserAccount(String userName, String userPassword, @Nullable String
			accountType, String refreshToken) {
		Context context = mContextWeakReference.get();
		boolean toReturn = false;
		if (context != null) {
			AccountManager accountManager = AccountManager.get(context);
			accountType = accountType != null ? accountType
					// TODO: 4/21/16 trinkes if needed, account type has to match with partners
					// version
					: ACCOUNT_TYPE;

			final Account account = new Account(userName, accountType);

			String authtokenType = AUTHTOKEN_TYPE_FULL_ACCESS;

			// Creating the account on the device and setting the auth token we got
			// (Not setting the auth token will cause another call to the server to authenticate
			// the user)
			accountManager.addAccountExplicitly(account, userPassword, null);
			accountManager.setAuthToken(account, authtokenType, refreshToken);
			AccountManagerPreferences.setRefreshToken(refreshToken);
			toReturn = true;
		}
		return toReturn;
	}

	public void onLoginFail(String reason) {
		mCallback.onLoginFail(reason);
	}

	public void onLoginSuccess() {
		mCallback.onLoginSuccess();
	}

	public void sendRemoveLocalAccountBroadcaster() {
		Intent intent = new Intent();
		intent.setAction(ACCOUNT_REMOVED_BROADCAST_KEY);
		cm.aptoide.pt.preferences.Application.getContext().sendBroadcast(intent);
	}

	/********************************************************
	 * activity lifecycle
	 */

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {

	}

	@Override
	public void onActivityPaused(Activity activity) {

	}

	@Override
	public void onActivityStopped(Activity activity) {

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		if (activity instanceof LoginActivity) {
			instance.mCallback = null;
		}
	}

	/**
	 * get user name introduced in edit text by user
	 *
	 * @return The user name introduced by user
	 */
	String getIntroducedUserName() {
		return mCallback.getIntroducedUserName();
	}

	/**
	 * get password introduced in edit text by user
	 *
	 * @return The password introduced by user
	 */
	String getIntroducedPassword() {
		return mCallback.getIntroducedPassword();
	}

	/*******************************************************/

	public interface IRegisterUser {

		void onRegisterSuccess(Bundle data);

		void onRegisterFail(@StringRes int reason);

		String getUserPassword();

		String getUserEmail();
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

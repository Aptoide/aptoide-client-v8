package cm.aptoide.accountmanager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.facebook.login.widget.LoginButton;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cm.aptoide.accountmanager.interfaces.IAptoideAccountRemoved;
import cm.aptoide.accountmanager.interfaces.IRemoveListener;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.accountmanager.ws.OAuth2AuthenticationRequest;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.NonNull;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 4/18/16.
 */
public class AptoideAccountManager implements GoogleApiClient.OnConnectionFailedListener, Application.ActivityLifecycleCallbacks, IRemoveListener {
    private static String TAG = AptoideAccountManager.class.getSimpleName();

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public final static String ARG_OPTIONS_BUNDLE = "BE";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Aptoide account";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Aptoide account";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";


    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "cm.aptoide.pt";

    /**
     * private variables
     */
    private ILoginInterface mCallback;
    private final static AptoideAccountManager instance = new AptoideAccountManager();
    private final static List<IAptoideAccountRemoved> removeAccountListnersList = new ArrayList<>();
    private WeakReference<Context> mContextWeakReference;

    /**
     * This method should be used to open login or account activity
     *
     * @param extras Extras to add on created intent (to login or register activity)
     */
    public static void openAccountManager(Context context, @Nullable Bundle extras) {
        if (isLoggedIn(context)) {
            Toast.makeText(context, "My account activity not implemented yet", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(context, "LoginActivity", Toast.LENGTH_SHORT).show();
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
        // TODO: 4/22/16 trinkes uncomment to return the real value
        AccountManager manager = android.accounts.AccountManager.get(context);
        return manager.getAccountsByType(Constants.ACCOUNT_TYPE).length != 0;
    }

    public static AptoideAccountManager getInstance() {
        return instance;
    }

    public static void onStop() {
        instance.mCallback = null;
    }

    /**
     * Method responsible to setup all login modes
     *
     * @param callback            Callback used to let outsiders know if the login was successful
     *                            or not
     * @param activity            Activity where the login is being made
     * @param facebookLoginButton facebook login button
     * @param loginButton         Aptoide login button
     * @param registerButton      Aptoide register button
     */
    protected void setupLogins(ILoginInterface callback, FragmentActivity activity, LoginButton facebookLoginButton, Button loginButton, Button registerButton) {
        this.mCallback = callback;
        this.mContextWeakReference = new WeakReference<>(activity.getApplicationContext());
        GoogleLoginUtils.setUpGoogle(activity, this);
        FacebookLoginUtils.setupFacebook(facebookLoginButton);
        AptoideLogin.setupAptoideLogin(loginButton, registerButton);
        activity.getApplication().registerActivityLifecycleCallbacks(this);
    }

    /**
     * Get the accessToken used to authenticate user on aptoide webservices
     *
     * @return A string with the token
     */
    public static String getAccessToken() {
        return SecurePreferences.getAccessToken();
    }

    /**
     * Get the userName of current logged user
     *
     * @return A string with the userName
     */
    public static String getUserName() {
        // TODO: 4/29/16 trinkes if null/empty, get it from account manager
        return SecurePreferences.getUserName();
    }

    /**
     * Handles the answer given by sign in. It receives the data and inform the Aptoide server
     *
     * @param requestCode Given on onActivityResult method
     * @param resultCode  Given on onActivityResult method
     * @param data        Given on onActivityResult method
     * @return true if the login was successful, false otherwise
     */
    protected static void onActivityResult(int requestCode, int resultCode, Intent data) {
        GoogleLoginUtils.onActivityResult(requestCode, data);
        FacebookLoginUtils.onActivityResult(requestCode, resultCode, data);
        AptoideLogin.onActivityResult(requestCode, resultCode, data);
    }

    static void loginUserCredentials(LoginMode mode, final String userName, final String passwordOrToken, final String nameForGoogle) {
        OAuth2AuthenticationRequest oAuth2AuthenticationRequest = OAuth2AuthenticationRequest.of(userName, passwordOrToken, mode, nameForGoogle);
        oAuth2AuthenticationRequest.execute(new SuccessRequestListener<OAuth>() {
            @Override
            public void onSuccess(OAuth oAuth) {
                Log.d(TAG, "onSuccess() called with: " + "oAuth = [" + oAuth + "]");
                boolean loginSuccessful = getInstance().addLocalUserAccount(userName, passwordOrToken, null, oAuth
                        .getRefresh_token());
                SecurePreferences.setAccessToken(oAuth.getAccessToken());
                SecurePreferences.setRefreshToken(oAuth.getRefresh_token());
                if (loginSuccessful) {
                    getInstance().onLoginSuccess();
                } else {
                    getInstance().onLoginFail(cm.aptoide.pt.preferences.Application.getContext()
                            .getString(R.string.unknown_error));
                    Log.e(TAG, "Error while adding the local account. Probably context was null");
                }
            }
        });
    }

    private boolean addLocalUserAccount(String accountName, String accountPassword, @Nullable String accountType, String authtoken) {
        Log.d(TAG, "addLocalUserAccount() called with: " + "accountName = [" + accountName + "], accountPassword = [" + accountPassword + "], accountType = [" + accountType + "], authtoken = [" + authtoken + "]");
        Context context = mContextWeakReference.get();
        boolean toReturn = false;
        if (context != null) {
            AccountManager accountManager = AccountManager.get(context);
            accountType = accountType != null
                    ? accountType
                    // TODO: 4/21/16 trinkes if needed, account type has to match with partners version
                    : ACCOUNT_TYPE;

            final Account account = new Account(accountName, accountType);

            String authtokenType = AUTHTOKEN_TYPE_FULL_ACCESS;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            accountManager.addAccountExplicitly(account, accountPassword, null);
            accountManager.setAuthToken(account, authtokenType, authtoken);
            toReturn = true;
        }
        return toReturn;
    }


    /**
     * Method used when the given AccessToken is invalid or has expired. The method will ask to
     * server for other accessToken
     */
    public static Observable<String> invalidateAccessToken(@NonNull Activity context) {
        return Observable.fromCallable(() -> {
            if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                throw new IllegalThreadStateException("This method shouldn't be called on ui thread.");
            }

            AccountManager accountManager = AccountManager.get(context);
            Account[] accountsByType = accountManager.getAccountsByType(ACCOUNT_TYPE);
            //we only allow 1 aptoide account

            if (accountsByType.length > 0) {
                AccountManagerFuture<Bundle> authToken = accountManager.getAuthToken(accountsByType[0], AUTHTOKEN_TYPE_FULL_ACCESS, null, context, null, null);
                try {
                    Bundle result = authToken.getResult();
                    return result.getString(AccountManager.KEY_AUTHTOKEN);
                } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                    e.printStackTrace();
                }

            }
            return null;
        })
                .subscribeOn(Schedulers.io())
                .flatMap(AptoideAccountManager::getNewAccessTokenFromRefreshToken);
    }

    /**
     * Method used when the given AccessToken is invalid or has expired. The method will ask to
     * server for other accessToken
     */
    public static String invalidateAccessTokenSync(@NonNull Activity context) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            throw new IllegalThreadStateException("This method shouldn't be called on ui thread.");
        }
        String refreshToken = SecurePreferences.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) {
            AccountManager accountManager = AccountManager.get(context);
            Account[] accountsByType = accountManager.getAccountsByType(ACCOUNT_TYPE);
            //we only allow 1 aptoide account

            if (accountsByType.length > 0) {
                AccountManagerFuture<Bundle> authToken = accountManager.getAuthToken(accountsByType[0], AUTHTOKEN_TYPE_FULL_ACCESS, null, context, null, null);
                try {
                    Bundle result = authToken.getResult();
                    refreshToken = result.getString(AccountManager.KEY_AUTHTOKEN);
                } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                    e.printStackTrace();
                }
            }
        }
        final String[] stringToReturn = {""};
        getNewAccessTokenFromRefreshToken(refreshToken).toBlocking()
                .subscribe((token) -> {
                    stringToReturn[0] = token;
                });
        return stringToReturn[0];
    }

    private static Observable<String> getNewAccessTokenFromRefreshToken(String refreshToken) {
        Log.d(TAG, "invalidateAccessTokenSync: " + new Date().getTime());
        return OAuth2AuthenticationRequest.of(refreshToken)
                .observe()
                .map(OAuth::getAccessToken)
                .doOnNext(SecurePreferences::setAccessToken);
    }

    public void onLoginFail(String reason) {
        mCallback.onLoginFail(reason);
    }

    public void onLoginSuccess() {
        mCallback.onLoginSuccess();
    }

    public void removeLocalAccount() {
        for (IAptoideAccountRemoved iAptoideAccountRemoved : removeAccountListnersList) {
            iAptoideAccountRemoved.removeAccount();
        }
    }

    /**
     * Register a callback to be invoked when an aptoide account is removed.
     * WARNING This Listener MUST be removed!!!!!!DON'T FORGET IT OR MEMORY LEAKS WILL HAPPEN!!!!!
     *
     * @param listener Listener to add.
     * @return WARNING This listener MUST be removed either by returned interface or using the
     * method removeAccountRemovedListener
     */
    public IRemoveListener addOnAccountRemovedListener(IAptoideAccountRemoved listener) {
        removeAccountListnersList.add(listener);
        return getInstance();
    }

    /**
     * Register a callback to be invoked when an aptoide account is removed.
     *
     * @param listener Listener to remove
     * @return true if removed successful false otherwise or if listener doesn't exists
     */
    @Override
    public boolean removeAccountRemovedListener(IAptoideAccountRemoved listener) {
        return removeAccountListnersList.remove(listener);
    }

    /**
     * Google sign in onConnectionFailed
     *
     * @param connectionResult Data about the login attempt
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO: 4/21/16 trinkes send reason
        onLoginFail("todo: get reason for google sign in");
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
    /*******************************************************/

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

}

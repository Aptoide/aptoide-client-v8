package cm.aptoide.accountmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cm.aptoide.accountmanager.interfaces.IAptoideAccountRemoved;
import cm.aptoide.accountmanager.interfaces.IRemoveListener;

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
     * @param context
     * @param extras  Extras to add on created intent (to login or register activity)
     */
    public static void openAccountManager(Context context, @Nullable Bundle extras) {
        if (isLoggedIn(context)) {
            Toast.makeText(context, "My account activity not implemented yet", Toast.LENGTH_SHORT).show();
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
     *
     * @param context
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
     * @param callback            Callback used to let outsiders know if the login was successful or not
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
        AptoideAccountManager.getInstance().setupAptoideLogin(loginButton, registerButton);
        activity.getApplication().registerActivityLifecycleCallbacks(this);
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
        GoogleLoginUtils.handleSignInResult(requestCode, data);
        FacebookLoginUtils.handleSignInResult(requestCode, resultCode, data);
    }


    private void setupAptoideLogin(Button loginButton, Button registerButton) {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mCallback.getUser();
                String password = mCallback.getPassword();

                if (username == null || password == null || (username.length() == 0 || password.length() == 0)) {
                    Toast.makeText(v.getContext(), R.string.fields_cannot_empty, Toast.LENGTH_LONG).show();
                    return;
                }

                submit(LoginMode.APTOIDE, username, password, null);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 4/22/16 trinkes use REQ_SIGNUP on startActivityForResult
                Snackbar.make(v, "Register acitivity not implemented yet", Snackbar.LENGTH_LONG).show();
//                Intent signup = new Intent(v.getContext(), signupClass);
//                ((Activity)v.getContext()).startActivityForResult(signup, REQ_SIGNUP);
            }
        });
    }

    public static void submit(LoginMode mode, final String userName, final String passwordOrToken, final String nameForGoogle) {
        // TODO: 4/20/16 trinkes implement login submission
        Log.e(TAG, "login submission to server not implemented yet");
        getInstance().addLocalUserAccount(userName, passwordOrToken, null, "token given by webservice");
        getInstance().onLoginSuccess();
    }

    private void addLocalUserAccount(String accountName, String accountPassword, String accountType, String authtoken) {
        Log.d("aptoide", TAG + "> finishLogin");
        Context context = mContextWeakReference.get();
        if (context != null) {
            AccountManager mAccountManager = AccountManager.get(context);
            accountType = accountType != null
                    ? accountType
                    // TODO: 4/21/16 trinkes if needed, account type has to match with partners version
                    : ACCOUNT_TYPE;

            final Account account = new Account(accountName, accountType);

            String authtokenType = ARG_AUTH_TYPE;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        }

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
     * @return WARNING This listener MUST be removed either by returned interface or using the method removeAccountRemovedListener
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
     *
     * @param activity
     * @param savedInstanceState
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
     * This interface is used to interact with Account Manager. It informs outsiders if login was made successfully or not and gives manager the user credentials
     */
    public interface ILoginInterface {

        /**
         * Called when logis is made successfully
         */
        void onLoginSuccess();

        /**
         * Called when the login fails
         *
         * @param reason
         */
        void onLoginFail(String reason);

        /**
         * Used to get user name inserted by user
         *
         * @return user name
         */
        String getUser();

        /**
         * Used to get password inserted by user
         *
         * @return password
         */
        String getPassword();
    }

    public enum LoginMode {FACEBOOK, GOOGLE, APTOIDE}

}

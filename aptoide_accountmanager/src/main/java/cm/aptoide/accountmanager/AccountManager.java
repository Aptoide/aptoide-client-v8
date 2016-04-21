package cm.aptoide.accountmanager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by trinkes on 4/18/16.
 */
public class AccountManager implements GoogleApiClient.OnConnectionFailedListener, FacebookCallback<LoginResult>, Application.ActivityLifecycleCallbacks {
    private static String TAG = AccountManager.class.getSimpleName();

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
     * private variables
     */
    private LoginInterface mCallback;
    private final static AccountManager instance = new AccountManager();

    /**
     * This method should be used to open login or account activity
     *
     * @param context
     */
    public static void openAccountManager(Context context) {
        if (isLoggedIn(context)) {
            Toast.makeText(context, "My account activity not implemented yet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "LoginActivity", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }

    public static boolean isLoggedIn(Context context) {
        android.accounts.AccountManager manager = android.accounts.AccountManager.get(context);
        return manager.getAccountsByType(Constants.ACCOUNT_TYPE).length != 0;
    }

    public static AccountManager getInstance() {
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
    protected void setupLogins(LoginInterface callback, FragmentActivity activity, LoginButton facebookLoginButton, Button loginButton, Button registerButton) {
        this.mCallback = callback;
        GoogleLoginUtils.setUpGoogle(activity, this);
        FacebookLoginUtils.setupFacebook(this, facebookLoginButton);
        AccountManager.getInstance().setupAptoideLogin(loginButton, registerButton);
        activity.getApplication().registerActivityLifecycleCallbacks(this);
    }


    /**
     * Handles the answer given by google after login. It receives the data and inform the Aptoide server
     *
     * @param requestCode Given on onActivityResult method
     * @param resultCode
     * @param data        Given on onActivityResult method  @return true if the login was successful, false otherwise
     */
    protected static boolean handleSignInResult(int requestCode, int resultCode, Intent data) {
        GoogleLoginUtils.handleSignInResult(requestCode, data);
        FacebookLoginUtils.handleSignInResult(requestCode, resultCode, data);
        return true;
    }


    private void setupAptoideLogin(Button loginButton, Button registerButton) {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mCallback.getUser();
                String password = mCallback.getPassword();

                if (username==null || password ==null || (username.length() == 0 || password.length() == 0)) {
                    Toast.makeText(v.getContext(), R.string.fields_cannot_empty, Toast.LENGTH_LONG).show();
                    return;
                }

                AccountManager.getInstance().submit(LoginMode.APTOIDE, username, password, null);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Register acitivity not implemented yet", Snackbar.LENGTH_LONG).show();
//                Intent signup = new Intent(v.getContext(), signupClass);
//                ((Activity)v.getContext()).startActivityForResult(signup, REQ_SIGNUP);
            }
        });
    }

    public void submit(LoginMode mode, final String userName, final String passwordOrToken, final String nameForGoogle) {
        // TODO: 4/20/16 trinkes implement login submission
        Log.e(TAG, "login submission to server not implemented yet");
        mCallback.onLoginSuccess();
    }

    /**
     * Google sign in onConnectionFailed
     *
     * @param connectionResult Data about the login attempt
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO: 4/21/16 trinkes send reason
        mCallback.onLoginFail();
    }

    /**
     * Facebook onSuccess
     *
     * @param loginResult User Data given by facebook
     */
    @Override
    public void onSuccess(LoginResult loginResult) {
        AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        submit(LoginMode.FACEBOOK, currentAccessToken.getUserId(), currentAccessToken.getToken(), null);
    }

    /**
     * Facebook onCancel
     */
    @Override
    public void onCancel() {
        // TODO: 4/21/16 trinkes facebook cancel/fail
        mCallback.onLoginFail();
    }

    /**
     * Facebook onError
     *
     * @param error error
     */
    @Override
    public void onError(FacebookException error) {
        // TODO: 4/21/16 trinkes facebook cancel/fail
        mCallback.onLoginFail();
    }

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
     * Informs the outsiders if the login was successful or not
     */
    public interface LoginInterface {
        void onLoginSuccess();
        void onLoginFail();
        String getUser();
        String getPassword();
    }

    public enum LoginMode {FACEBOOK, GOOGLE, APTOIDE,}

}

package cm.aptoide.accountmanager;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

/**
 * Created by trinkes on 4/19/16.
 */
public class FacebookLoginUtils {

    private static final String TAG = FacebookLoginUtils.class.getSimpleName();
    static CallbackManager callbackManager;

    static protected void setupFacebook(FacebookCallback<LoginResult> callback, LoginButton mFacebookLoginButton) {
        FacebookSdk.sdkInitialize(mFacebookLoginButton.getContext().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton.setReadPermissions(Arrays.asList("email", "user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager, callback);
    }

    public static void handleSignInResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}

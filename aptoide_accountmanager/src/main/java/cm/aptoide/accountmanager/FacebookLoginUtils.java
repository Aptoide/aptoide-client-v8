package cm.aptoide.accountmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by trinkes on 4/19/16.
 */
public class FacebookLoginUtils {

    private static final String TAG = FacebookLoginUtils.class.getSimpleName();
    static CallbackManager callbackManager;

    static protected void setupFacebook(LoginButton mFacebookLoginButton) {
        FacebookSdk.sdkInitialize(mFacebookLoginButton.getContext().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton.setReadPermissions(Arrays.asList("email", "user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookLoginCallback());
    }

    public static void handleSignInResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected static Bundle getFacebookData(JSONObject object) {

        Bundle bundle = null;
        try {
            bundle = new Bundle();
            String id = object.getString("id");

            URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
            Log.i("profile_pic", profile_pic + "");
            bundle.putString("profile_pic", profile_pic.toString());


            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));

            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    static class FacebookLoginCallback implements FacebookCallback<LoginResult> {
        /**
         * Facebook onSuccess
         *
         * @param loginResult User Data given by facebook
         */
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();

            // TODO: 4/22/16 trinkes check declined permissions
            Log.e(TAG, "TODO: 4/22/16 trinkes check declined permissions");

            final String token = currentAccessToken.getToken();
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.i("LoginActivity", response.toString());
                    // Get facebook data from login
                    Bundle bFacebookData = FacebookLoginUtils.getFacebookData(object);
                    AptoideAccountManager.submit(AptoideAccountManager.LoginMode.FACEBOOK, bFacebookData.getString("email"), token, null);
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, email");
            request.setParameters(parameters);
            request.executeAsync();
        }

        /**
         * Facebook onCancel
         */
        @Override
        public void onCancel() {
            // TODO: 4/21/16 trinkes facebook cancel/fail
            AptoideAccountManager.getInstance().onLoginFail("Canceled");
        }

        /**
         * Facebook onError
         *
         * @param error error
         */
        @Override
        public void onError(FacebookException error) {
            // TODO: 4/21/16 trinkes facebook cancel/fail
            AptoideAccountManager.getInstance().onLoginFail(error.toString());
        }
    }
}

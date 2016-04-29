package cm.aptoide.accountmanager;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;
import java.util.Collection;

import cm.aptoide.accountmanager.ws.LoginMode;

/**
 * Created by trinkes on 4/19/16.
 */
public class GoogleLoginUtils {

    private static final int REQ_SIGN_IN_GOOGLE = 2;
    private static final String TAG = GoogleLoginUtils.class.getSimpleName();

    /**
     * This method set's up google social login
     * @param activity Where the login button is
     * @param onConnectionFailedListener This callback will be called if login fails
     */
    protected static void setUpGoogle(FragmentActivity activity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        final View googleSignIn = activity.findViewById(R.id.g_sign_in_button);
        final int connectionResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);
        final Collection<Integer> badResults = Arrays.asList(ConnectionResult.SERVICE_MISSING, ConnectionResult.SERVICE_DISABLED);
        final boolean gmsAvailable = BuildConfig.GMS_CONFIGURED && !badResults.contains(connectionResult);
        if (!gmsAvailable) {
            googleSignIn.setVisibility(View.GONE);
            return;
        }

        Log.d(TAG, "setUpGoogle serverId: "+BuildConfig.GMS_SERVER_ID);
        final GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
                .build();
        final GoogleApiClient client = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, onConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        if (googleSignIn != null) {
            googleSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(client);
                    if (v.getContext() instanceof Activity) {
                        ((Activity) v.getContext()).startActivityForResult(signInIntent, REQ_SIGN_IN_GOOGLE);
                    } else {
                        throw new ClassCastException("The context must be an instance of Activity");
                    }
                }
            });
        }
    }

    /**
     * Handles the answer given by google after login. It receives the data and inform the Aptoide server
     * @param requestCode Given on onActivityResult method
     * @param data Given on onActivityResult method
     * @return An account
     */
    protected static void onActivityResult(int requestCode, Intent data) {
        boolean toReturn = false;
        if (requestCode == REQ_SIGN_IN_GOOGLE) {
            final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Logger.d(TAG, "GoogleSignInResult. status: " + result.getStatus());
            GoogleSignInAccount account = result.getSignInAccount();
            if (!result.isSuccess()) {
                handleErrors(account);
            }

            if (result.isSuccess() && account != null) {
                AptoideAccountManager.loginUserCredentials(LoginMode.GOOGLE, account.getEmail(),account.getServerAuthCode(), account.getDisplayName());
            }
        }
        Logger.d(TAG, "onActivityResult() returned: " + toReturn);
    }

    private static void handleErrors(GoogleSignInAccount account) {
        // TODO: 4/20/16 trinkes handle google sign in errors
    }

}

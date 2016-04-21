package cm.aptoide.accountmanager;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

/**
 * Created by brutus on 09-12-2013.
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private String TAG = "UdinicAuthenticator";
    private final Context mContext;

    public AccountAuthenticator(Context context) {
        super(context);


        // I hate you! Google - set mContext as protected!
        this.mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d("udinic", TAG + "> addAccount");

        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(cm.aptoide.accountmanager.AccountManager.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(cm.aptoide.accountmanager.AccountManager.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(cm.aptoide.accountmanager.AccountManager.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(cm.aptoide.accountmanager.AccountManager.ARG_OPTIONS_BUNDLE, options);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);


        final Bundle bundle = new Bundle();

        if(!(requiredFeatures == null || requiredFeatures.length == 0) && contains(requiredFeatures, "timelineLogin")){
            String username = options.getString(AccountManager.KEY_ACCOUNT_NAME);
            String password = options.getString(AccountManager.KEY_PASSWORD);
            String authToken = options.getString(AccountManager.KEY_AUTHTOKEN);
            Account account = new Account(username, accountType);
            AccountManager.get(mContext).addAccountExplicitly(account, password, null);
            AccountManager.get(mContext).setAuthToken(account, authTokenType, authToken);
            Bundle data = new Bundle();
            data.putString(AccountManager.KEY_ACCOUNT_NAME, username);
            data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);

            response.onResult(data);

        }else{
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        }

        return bundle;
    }

    private boolean contains(String[] requiredFeatures, String timelineLogin) {
        boolean toReturn = false;

        for (String requiredFeature : requiredFeatures) {
            if (requiredFeature.contains(timelineLogin)) {
                toReturn = true;
            }
        }
        return toReturn;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        Log.d("udinic", TAG + "> getAuthToken");

        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(cm.aptoide.accountmanager.AccountManager.AUTHTOKEN_TYPE_READ_ONLY) && !authTokenType.equals(cm.aptoide.accountmanager.AccountManager.AUTHTOKEN_TYPE_FULL_ACCESS)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = am.peekAuthToken(account, authTokenType);

        Log.d("udinic", TAG + "> peekAuthToken returned - " + account+ " " + authToken);

        // Lets give another try to authenticate the user


        // If we get an authToken - we return it
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);

            Log.d("udinic", TAG + "> getAuthToken returning - " + account+ " " + authToken);


            return result;


        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.

    }


    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (cm.aptoide.accountmanager.AccountManager.AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
            return cm.aptoide.accountmanager.AccountManager.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
        else if (cm.aptoide.accountmanager.AccountManager.AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
            return cm.aptoide.accountmanager.AccountManager.AUTHTOKEN_TYPE_READ_ONLY_LABEL;
        else
            return authTokenType + " (Label)";
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @NonNull
    @Override
    public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse response, Account account) throws NetworkErrorException {


        final Bundle result = new Bundle();
//        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);
//
//
//        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(mContext);
//        sPref.edit()
//                .remove("queueName")
//                .remove(Configs.LOGIN_USER_LOGIN)
//                .remove("username")
//                .remove("useravatar")
//                .remove("userRepo")
//                .remove(Preferences.REPOS_SYNCED)
//                .remove(Preferences.TIMELINE_ACEPTED_BOOL)
//                .remove(Preferences.SHARE_TIMELINE_DOWNLOAD_BOOL)
//                .remove(Preferences.REPOS_SYNCED)
//         .apply();
//        SecurePreferences.getInstance().edit().remove("access_token").apply();
//        mContext.stopService(new Intent(mContext, RabbitMqService.class));

        return result;

    }
}

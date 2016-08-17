package cm.aptoide.accountmanager;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

/**
 * Created by brutus on 09-12-2013.
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {

	public static final String INVALID_AUTH_TOKEN_TYPE = "invalid authTokenType";
	private static final String TAG = AccountAuthenticator.class.getSimpleName();

	public AccountAuthenticator(Context context) {
		super(context);
	}

	protected Bundle createAuthActivityIntentBundle(AccountAuthenticatorResponse response, String
			accountType, String[] requiredFeatures, String authTokenType, String password, Bundle
			options) {

		final Bundle bundle = new Bundle();
		final Intent intent = createAuthActivityIntent(response, accountType, authTokenType,
				options);
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
//        }

		return bundle;
	}

	protected Intent createAuthActivityIntent(AccountAuthenticatorResponse response, String
			accountType, String authTokenType, Bundle options) {
		Intent intent = new Intent(Application.getContext(), LoginActivity.class);
		intent.putExtra(Constants.ARG_ACCOUNT_TYPE, accountType);
		intent.putExtra(Constants.ARG_AUTH_TYPE, authTokenType);
		intent.putExtra(Constants.ARG_IS_ADDING_NEW_ACCOUNT, true);
		intent.putExtra(Constants.ARG_OPTIONS_BUNDLE, options);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		return intent;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String
			authTokenType, String[] requiredFeatures, Bundle options) throws
			NetworkErrorException {
		Logger.d(TAG, "Adding account: type=" + accountType);

		return createAuthActivityIntentBundle(response, accountType, requiredFeatures,
				authTokenType, null, options);
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
									 Bundle options) throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String
			authTokenType, Bundle options) throws NetworkErrorException {

		// If the caller requested an authToken type we don't support, then
		// return an error
		if (!authTokenType.equals(Constants.AUTHTOKEN_TYPE_FULL_ACCESS)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ERROR_MESSAGE, INVALID_AUTH_TOKEN_TYPE);
			return result;
		}

		// Extract the username and password from the Account Manager, and ask
		// the server for an appropriate AuthToken.
		final AccountManager am = AccountManager.get(Application.getContext());

		String authToken = am.peekAuthToken(account, authTokenType);

		Logger.d("udinic", TAG + "> peekAuthToken returned - " + account + " " + authToken);

		// Lets give another try to authenticate the user

		// If we get an authToken - we return it
		final Bundle result = new Bundle();
		result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
		result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
		result.putString(AccountManager.KEY_AUTHTOKEN, authToken);

		Logger.d("udinic", TAG + "> getAuthToken returning - " + account + " " + authToken);

		return result;

		// If we get here, then we couldn't access the user's password - so we
		// need to re-prompt them for their credentials. We do that by creating
		// an intent to display our AuthenticatorActivity.
// TODO: 4/28/16 trinkes ask to relog if refresh token expires
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		if (Constants.AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
			return Constants.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
		else if (Constants.AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
			return Constants.AUTHTOKEN_TYPE_READ_ONLY_LABEL;
		else return authTokenType + " (Label)";
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String
			authTokenType, Bundle options) throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[]
			features) throws NetworkErrorException {
		final Bundle result = new Bundle();
		result.putBoolean(KEY_BOOLEAN_RESULT, false);
		return result;
	}

	@Override
	public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse response, Account account)
			throws NetworkErrorException {
		final Bundle result = super.getAccountRemovalAllowed(response, account);
		if (result != null && result.containsKey(AccountManager.KEY_BOOLEAN_RESULT) && !result
				.containsKey(AccountManager.KEY_INTENT)) {
			if (result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT)) {
				AptoideAccountManager.logout(null);
				AptoideAccountManager.getInstance().sendRemoveLocalAccountBroadcaster();
			}
		}
		return result;
	}
}

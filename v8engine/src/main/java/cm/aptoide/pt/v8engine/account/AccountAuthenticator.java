/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.view.account.LoginSignUpFragment;
import cm.aptoide.pt.v8engine.view.MainActivity;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

/**
 * Created by brutus on 09-12-2013.
 */
class AccountAuthenticator extends AbstractAccountAuthenticator {

  private static final String INVALID_AUTH_TOKEN_TYPE = "invalid authTokenType";
  private static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
  private static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
  private static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL =
      "Read only access to an Aptoide " + "account";
  /**
   * Auth token types
   */
  private static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL =
      "Full access to an Aptoide " + "account";
  private final static String ARG_OPTIONS_BUNDLE = "BE";
  private final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
  private final static String ARG_AUTH_TYPE = "AUTH_TYPE";
  private final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
  private static final String TAG = AccountAuthenticator.class.getSimpleName();
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;

  AccountAuthenticator(Context context, AptoideAccountManager accountManager,
      CrashReport crashReport) {
    super(context);
    this.accountManager = accountManager;
    this.crashReport = crashReport;
  }

  @Override
  public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
      String authTokenType, String[] requiredFeatures, Bundle options)
      throws NetworkErrorException {
    Logger.v(TAG, "Adding account: type=" + accountType);
    return createAuthActivityIntentBundle(response, accountType, requiredFeatures, authTokenType,
        null, options);
  }

  @Override public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
      Bundle options) throws NetworkErrorException {
    return null;
  }

  @Override public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
      String authTokenType, Bundle options) throws NetworkErrorException {

    // If the caller requested an authToken type we don't support, then
    // return an error
    if (!authTokenType.equals(AUTHTOKEN_TYPE_FULL_ACCESS)) {
      final Bundle result = new Bundle();
      result.putString(AccountManager.KEY_ERROR_MESSAGE, INVALID_AUTH_TOKEN_TYPE);
      return result;
    }

    // Extract the username and password from the Account Manager, and ask
    // the server for an appropriate AuthToken.
    final AccountManager am = AccountManager.get(Application.getContext());

    String authToken = am.peekAuthToken(account, authTokenType);

    Logger.v(TAG, "peekAuthToken returned - " + account + " " + authToken);

    // Lets give another try to authenticate the user

    // If we get an authToken - we return it
    final Bundle result = new Bundle();
    result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
    result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
    result.putString(AccountManager.KEY_AUTHTOKEN, authToken);

    Logger.v(TAG, "getAuthToken returning - " + account + " " + authToken);

    return result;

    // If we get here, then we couldn't access the user's password - so we
    // need to re-prompt them for their credentials. We do that by creating
    // an intent to display our AuthenticatorActivity.
    // TODO: 4/28/16 trinkes ask to relog if refresh token expires
  }

  @Override public String getAuthTokenLabel(String authTokenType) {
    if (AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType)) {
      return AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
    } else if (AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType)) {
      return AUTHTOKEN_TYPE_READ_ONLY_LABEL;
    } else {
      return authTokenType + " (Label)";
    }
  }

  @Override public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
      String authTokenType, Bundle options) throws NetworkErrorException {
    return null;
  }

  @Override public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
      String[] features) throws NetworkErrorException {
    final Bundle result = new Bundle();
    result.putBoolean(KEY_BOOLEAN_RESULT, false);
    return result;
  }

  @Override
  public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse response, Account account)
      throws NetworkErrorException {
    final Bundle result = super.getAccountRemovalAllowed(response, account);
    if (result != null
        && result.containsKey(AccountManager.KEY_BOOLEAN_RESULT)
        && !result.containsKey(AccountManager.KEY_INTENT)) {
      if (result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT)) {
        accountManager.logout()
            .doOnError(throwable -> crashReport.log(throwable))
            .onErrorComplete()
            .subscribe();
      }
    }
    return result;

    //
    // this indicates that the user must explicitly logout inside Aptoide and is not able to
    // logout from the Settings -> Accounts
    //

    //Bundle result = new Bundle();
    //result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
    //return result;
  }

  private Bundle createAuthActivityIntentBundle(AccountAuthenticatorResponse response,
      String accountType, String[] requiredFeatures, String authTokenType, String password,
      Bundle options) {

    final Bundle bundle = new Bundle();
    final Intent intent = createAuthActivityIntent(response, accountType, authTokenType, options);
    bundle.putParcelable(AccountManager.KEY_INTENT, intent);

    return bundle;
  }

  private Intent createAuthActivityIntent(AccountAuthenticatorResponse response, String accountType,
      String authTokenType, Bundle options) {
    Intent intent = new Intent(Application.getContext(), MainActivity.class);
    intent.putExtra(MainActivity.FRAGMENT, LoginSignUpFragment.class.getName());
    // FIXME: 14/2/2017 sithengineer add this funtionality in main Activity
    intent.putExtra(ARG_ACCOUNT_TYPE, accountType);
    intent.putExtra(ARG_AUTH_TYPE, authTokenType);
    intent.putExtra(ARG_IS_ADDING_NEW_ACCOUNT, true);
    intent.putExtra(ARG_OPTIONS_BUNDLE, options);
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
    return intent;
  }
}

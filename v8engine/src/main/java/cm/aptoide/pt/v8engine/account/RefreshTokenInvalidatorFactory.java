package cm.aptoide.pt.v8engine.account;

import cm.aptoide.accountmanager.AccountManagerTokenInvalidatorFactory;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.v8engine.networking.RefreshTokenInvalidator;

public class RefreshTokenInvalidatorFactory implements AccountManagerTokenInvalidatorFactory {

  @Override public TokenInvalidator getTokenInvalidator(AptoideAccountManager accountManager) {
    return new RefreshTokenInvalidator(accountManager);
  }
}

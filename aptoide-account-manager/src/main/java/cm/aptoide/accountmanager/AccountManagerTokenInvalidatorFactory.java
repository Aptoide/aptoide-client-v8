package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;

public interface AccountManagerTokenInvalidatorFactory {

  TokenInvalidator getTokenInvalidator(AptoideAccountManager accountManager);
}

package cm.aptoide.pt.v8engine.networking;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import rx.Completable;

public class AccountManagerTokenInvalidator implements TokenInvalidator {

  private final AptoideAccountManager accountManager;

  public AccountManagerTokenInvalidator(AptoideAccountManager accountManager) {
    this.accountManager = accountManager;
  }

  @Override public Completable invalidateAccessToken() {
    return accountManager.refreshToken();
  }
}

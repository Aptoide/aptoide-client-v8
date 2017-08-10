package cm.aptoide.pt.networking;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import rx.Completable;

public class RefreshTokenInvalidator implements TokenInvalidator {

  private final AptoideAccountManager accountManager;

  public RefreshTokenInvalidator(AptoideAccountManager accountManager) {
    this.accountManager = accountManager;
  }

  @Override public Completable invalidateAccessToken() {
    return accountManager.refreshToken();
  }
}

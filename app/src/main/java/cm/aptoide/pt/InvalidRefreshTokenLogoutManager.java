package cm.aptoide.pt;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.networking.RefreshTokenInvalidator;

/**
 * Created by filipegoncalves on 6/20/18.
 */

public class InvalidRefreshTokenLogoutManager {

  private AptoideAccountManager aptoideAccountManager;
  private RefreshTokenInvalidator refreshTokenInvalidator;

  public InvalidRefreshTokenLogoutManager(AptoideAccountManager aptoideAccountManager,
      RefreshTokenInvalidator refreshTokenInvalidator) {
    this.aptoideAccountManager = aptoideAccountManager;
    this.refreshTokenInvalidator = refreshTokenInvalidator;
  }

  public void start() {
    refreshTokenInvalidator.getLogoutSubject()
        .flatMapCompletable(__ -> aptoideAccountManager.logout())
        .subscribe();
  }
}

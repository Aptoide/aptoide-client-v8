package cm.aptoide.pt.account;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountException;
import cm.aptoide.accountmanager.AptoideAccountManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import rx.Completable;

public class GoogleLoginManager {

  private final AptoideAccountManager accountManager;

  public GoogleLoginManager(AptoideAccountManager accountManager) {
    this.accountManager = accountManager;
  }

  public Completable login(GoogleSignInResult result) {
    final GoogleSignInAccount account = result.getSignInAccount();
    if (result.isSuccess() && account != null) {
      return accountManager.login(Account.Type.GOOGLE, account.getEmail(),
          account.getServerAuthCode(), account.getDisplayName());
    } else {
      return Completable.error(
          new AccountException("Google login invalid result " + result.getStatus()));
    }
  }
}
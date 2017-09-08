package cm.aptoide.pt.account;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountException;
import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.SignUpAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;

public class GoogleSignUpAdapter implements SignUpAdapter<GoogleSignInResult> {

  public static final String TYPE = "GOOGLE";
  private final GoogleApiClient client;
  private final LoginPreferences preferences;

  public GoogleSignUpAdapter(GoogleApiClient client, LoginPreferences preferences) {
    this.preferences = preferences;
    this.client = client;
  }

  @Override public Single<Account> signUp(GoogleSignInResult result, AccountService service) {

    if (!isEnabled()) {
      return Single.error(new IllegalStateException("Google sign up is not enabled"));
    }

    final GoogleSignInAccount account = result.getSignInAccount();
    if (result.isSuccess() && account != null) {
      return service.createAccount(account.getEmail(), account.getServerAuthCode(),
          account.getDisplayName(), "GOOGLE");
    } else {
      return Single.error(
          new IllegalStateException("Google login invalid result " + result.getStatus()));
    }
  }

  @Override public boolean isEnabled() {
    return preferences.isGoogleLoginEnabled();
  }

  @Override public Completable logout() {
    return Completable.fromAction(() -> {
      client.blockingConnect();
      if (client.isConnected()) {
        Auth.GoogleSignInApi.signOut(client);
      }
    })
        .subscribeOn(Schedulers.computation());
  }
}
package cm.aptoide.pt.account;

import cm.aptoide.accountmanager.Account;
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
      return Single.error(new GoogleSignUpException());
    }
  }

  @Override public Completable logout() {
    return Completable.defer(() -> {
      if (client.blockingConnect()
          .isSuccess()) {
        Auth.GoogleSignInApi.signOut(client);
        return Completable.complete();
      } else {
        return Completable.error(
            new IllegalStateException("Could not connect to Google Play Services to sign out."));
      }
    })
        .subscribeOn(Schedulers.io());
  }

  @Override public boolean isEnabled() {
    return preferences.isGoogleLoginEnabled();
  }
}
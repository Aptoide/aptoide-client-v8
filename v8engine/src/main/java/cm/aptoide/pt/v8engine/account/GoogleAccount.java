package cm.aptoide.pt.v8engine.account;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.Store;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.List;
import rx.Completable;
import rx.schedulers.Schedulers;

public class GoogleAccount implements Account {

  private final Account account;
  private final GoogleApiClient client;

  public GoogleAccount(Account account, GoogleApiClient client) {
    this.account = account;
    this.client = client;
  }

  @Override public Completable logout() {
    return Completable.fromAction(() -> {
      client.blockingConnect();
      if (client.isConnected()) {
        Auth.GoogleSignInApi.signOut(client);
      }
    }).subscribeOn(Schedulers.computation());
  }

  @Override public Completable refreshToken() {
    return account.refreshToken();
  }

  @Override public List<Store> getSubscribedStores() {
    return account.getSubscribedStores();
  }

  @Override public String getId() {
    return account.getId();
  }

  @Override public String getNickname() {
    return account.getNickname();
  }

  @Override public String getAvatar() {
    return account.getAvatar();
  }

  @Override public String getStoreName() {
    return account.getStoreName();
  }

  @Override public String getStoreAvatar() {
    return account.getStoreAvatar();
  }

  @Override public boolean isAdultContentEnabled() {
    return account.isAdultContentEnabled();
  }

  @Override public Access getAccess() {
    return account.getAccess();
  }

  @Override public boolean isAccessConfirmed() {
    return account.isAccessConfirmed();
  }

  @Override public boolean isLoggedIn() {
    return account.isLoggedIn();
  }

  @Override public String getEmail() {
    return account.getEmail();
  }

  @Override public String getAccessToken() {
    return account.getAccessToken();
  }

  @Override public String getRefreshToken() {
    return account.getRefreshToken();
  }

  @Override public String getPassword() {
    return account.getPassword();
  }

  @Override public Type getType() {
    return account.getType();
  }
}

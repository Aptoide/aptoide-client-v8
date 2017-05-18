package cm.aptoide.pt.v8engine.account;

import android.content.Context;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.Store;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import java.util.List;
import rx.Completable;

public class FacebookAccount implements Account {

  private final Account account;
  private final Context applicationContext;

  public FacebookAccount(Context applicationContext, Account account) {
    this.applicationContext = applicationContext;
    this.account = account;
  }

  @Override public Completable logout() {
    return Completable.fromAction(() -> {
      FacebookSdk.sdkInitialize(applicationContext);
      LoginManager.getInstance()
          .logOut();
    });
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

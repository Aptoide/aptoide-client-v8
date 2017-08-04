package cm.aptoide.accountmanager;

import android.text.TextUtils;
import java.util.Collections;
import java.util.List;
import rx.Completable;

public class LocalAccount implements Account {

  private final Store store;

  public LocalAccount(Store store) {
    this.store = store;
  }

  @Override public Completable logout() {
    return Completable.complete();
  }

  @Override public Completable refreshToken() {
    return Completable.error(new Exception("Can not refresh token of local account."));
  }

  @Override public List<Store> getSubscribedStores() {
    return Collections.emptyList();
  }

  @Override public String getId() {
    return "";
  }

  @Override public String getNickname() {
    return "";
  }

  @Override public String getAvatar() {
    return "";
  }

  @Override public boolean isAdultContentEnabled() {
    return false;
  }

  @Override public Access getAccess() {
    return Access.UNLISTED;
  }

  @Override public boolean isAccessConfirmed() {
    return false;
  }

  @Override public boolean isLoggedIn() {
    return false;
  }

  @Override public String getEmail() {
    return "";
  }

  @Override public String getAccessToken() {
    return "";
  }

  @Override public String getRefreshToken() {
    return "";
  }

  @Override public String getPassword() {
    return "";
  }

  @Override public Type getType() {
    return Type.LOCAL;
  }

  @Override public Store getStore() {
    return store;
  }

  @Override public boolean hasStore() {
    return store != null && !TextUtils.isEmpty(store.getName());
  }

  @Override public boolean isPublicUser() {
    return getAccess() == Access.PUBLIC;
  }
}

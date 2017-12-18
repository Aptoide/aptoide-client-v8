package cm.aptoide.accountmanager;

import android.text.TextUtils;
import java.util.Collections;
import java.util.List;

public class LocalAccount implements Account {

  private final Store store;
  private final User user;

  public LocalAccount(Store store, User user) {

    this.store = store;
    this.user = user;
  }

  @Override public List<Store> getSubscribedStores() {
    return Collections.emptyList();
  }

  @Override public List<User> getSubscribedUsers() {
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

  @Override public Store getStore() {
    return store;
  }

  @Override public boolean hasStore() {
    return store != null && !TextUtils.isEmpty(store.getName());
  }

  @Override public boolean isPublicUser() {
    return getAccess() == Access.PUBLIC;
  }

  @Override public boolean isPublicStore() {
    return store != null && store.hasPublicAccess();
  }
}

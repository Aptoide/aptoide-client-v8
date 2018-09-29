package cm.aptoide.accountmanager;

import android.text.TextUtils;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LocalAccount implements Account {

  private final Store store;

  public LocalAccount(Store store) {
    this.store = store;
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

  @Override public boolean acceptedTermsAndConditions() {
    return false;
  }

  @Override public boolean acceptedPrivacyPolicy() {
    return false;
  }

  @Override public Date getBirthDate() {
    return new Date(1970, 1, 1);
  }
}

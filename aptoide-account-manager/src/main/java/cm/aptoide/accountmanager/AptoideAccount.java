/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

import android.text.TextUtils;
import java.util.List;

public final class AptoideAccount implements Account {

  private final String id;
  private final String email;
  private final String nickname;
  private final String avatar;
  private final Store store;
  private final boolean adultContentEnabled;
  private final Access access;
  private final boolean accessConfirmed;
  private final List<Store> subscribedStores;
  private final List<User> subscribedUsers;

  public AptoideAccount(String id, String email, String nickname, String avatar, Store store,
      boolean adultContentEnabled, Access access, boolean accessConfirmed,
      List<Store> subscribedStores, List<User> subscribedUsers) {
    this.id = id;
    this.email = email;
    this.nickname = nickname;
    this.avatar = avatar;
    this.store = store;
    this.adultContentEnabled = adultContentEnabled;
    this.access = access;
    this.accessConfirmed = accessConfirmed;
    this.subscribedStores = subscribedStores;
    this.subscribedUsers = subscribedUsers;
  }

  @Override public List<Store> getSubscribedStores() {
    return subscribedStores;
  }

  @Override public List<User> getSubscribedUsers() {
    return subscribedUsers;
  }

  @Override public String getId() {
    return id;
  }

  @Override public String getNickname() {
    return nickname;
  }

  @Override public String getAvatar() {
    return avatar;
  }

  @Override public boolean isAdultContentEnabled() {
    return adultContentEnabled;
  }

  @Override public Access getAccess() {
    return access;
  }

  @Override public boolean isAccessConfirmed() {
    return accessConfirmed;
  }

  @Override public boolean isLoggedIn() {
    return true;
  }

  @Override public String getEmail() {
    return email;
  }

  @Override public Store getStore() {
    return store;
  }

  @Override public boolean hasStore() {
    return store != null && !TextUtils.isEmpty(store.getName());
  }

  @Override public boolean isPublicUser() {
    return access == Access.PUBLIC;
  }

  @Override public boolean isPublicStore() {
    return store != null && store.hasPublicAccess();
  }
}

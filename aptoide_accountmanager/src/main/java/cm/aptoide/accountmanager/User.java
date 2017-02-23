/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

import cm.aptoide.pt.actions.UserData;

public class User implements UserData {

  private final String id;
  private final String email;
  private final String nickname;
  private final String avatar;
  private final String store;
  private final String storeAvatar;
  private final boolean mature;
  private final String access;
  private final boolean accessConfirmed;

  public User(String id, String nickname, String email, String avatar, String store, boolean mature,
      String storeAvatar, String access, boolean accessConfirmed) {
    this.id = id;
    this.nickname = nickname;
    this.email = email;
    this.avatar = avatar;
    this.store = store;
    this.storeAvatar = storeAvatar;
    this.mature = mature;
    this.access = access;
    this.accessConfirmed = accessConfirmed;
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getNickname() {
    return nickname;
  }

  public String getAvatar() {
    return avatar;
  }

  public String getStore() {
    return store;
  }

  public String getStoreAvatar() {
    return storeAvatar;
  }

  public boolean isMature() {
    return mature;
  }

  public String getAccess() {
    return access;
  }

  public boolean isAccessConfirmed() {
    return accessConfirmed;
  }
}

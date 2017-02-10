/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

import cm.aptoide.pt.actions.UserData;

public class User implements UserData {

  private final String id;
  private final String username;
  private final String name;
  private final String webInstallName;
  private final String avatar;
  private final String store;
  private final String storeAvatar;
  private final boolean mature;

  public User(String userId, String userNickName, String username, String webInstallName,
      String avatar, String store, boolean mature, String storeAvatar) {
    id = userId;
    this.name = userNickName;
    this.username = username;
    this.webInstallName = webInstallName;
    this.avatar = avatar;
    this.store = store;
    this.storeAvatar = storeAvatar;
    this.mature = mature;
  }

  public String getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getName() {
    return name;
  }

  public String getWebInstallName() {
    return webInstallName;
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
}

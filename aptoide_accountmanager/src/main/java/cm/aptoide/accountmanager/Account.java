/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

/**
 * Created by marcelobenites on 10/02/17.
 */

public class Account {

  private final String id;
  private final String email;
  private final String nickname;
  private final String avatar;

  private final String refreshToken;
  private final String token;
  private final String password;
  private final Type type;
  private final String store;
  private final String storeAvatar;
  private final boolean mature;
  private final String access;
  private final boolean accessConfirmed;

  public Account(String id, String email, String nickname, String avatar, String refreshToken,
      String token, String password, Type type, String store, String storeAvatar,
      boolean mature, String access, boolean accessConfirmed) {
    this.id = id;
    this.email = email;
    this.nickname = nickname;
    this.avatar = avatar;
    this.refreshToken = refreshToken;
    this.token = token;
    this.password = password;
    this.type = type;
    this.store = store;
    this.storeAvatar = storeAvatar;
    this.mature = mature;
    this.access = access;
    this.accessConfirmed = accessConfirmed;
  }

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getToken() {
    return token;
  }

  public Type getType() {
    return type;
  }

  public String getId() {
    return id;
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

  public static enum Type {
    APTOIDE, GOOGLE, FACEBOOK, ABAN
  }
}

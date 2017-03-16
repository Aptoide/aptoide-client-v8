/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

import java.util.Collections;
import java.util.List;

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
  private final Type type;
  private final String store;
  private final String storeAvatar;
  private final boolean adultContentEnabled;
  private final Access access;
  private final boolean accessConfirmed;
  private final String password;
  private final List<Store> subscribedStores;

  private Account() {
    this.id = "";
    this.email = "";
    this.nickname = "";
    this.avatar = "";
    this.refreshToken = "";
    this.token = "";
    this.password = "";
    this.type = null;
    this.store = "";
    this.storeAvatar = "";
    this.adultContentEnabled = false;
    this.access = null;
    this.accessConfirmed = false;
    this.subscribedStores = Collections.emptyList();
  }

  public Account(String id, String email, String nickname, String avatar, String refreshToken,
      String token, String password, Type type, String store, String storeAvatar,
      boolean adultContentEnabled, Access access, boolean accessConfirmed,
      List<Store> subscribedStores) {
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
    this.adultContentEnabled = adultContentEnabled;
    this.access = access;
    this.accessConfirmed = accessConfirmed;
    this.subscribedStores = subscribedStores;
  }

  public static Account empty() {
    return new Account();
  }

  public List<Store> getSubscribedStores() {
    return subscribedStores;
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

  public boolean isAdultContentEnabled() {
    return adultContentEnabled;
  }

  public Access getAccess() {
    return access;
  }

  public boolean isAccessConfirmed() {
    return accessConfirmed;
  }

  public boolean isLoggedIn() {
    return (!isEmpty(getEmail()) && !isEmpty(getToken()) && !isEmpty(getRefreshToken()) && !isEmpty(
        getPassword()));
  }

  private boolean isEmpty(String string) {
    return string == null || string.trim().length() == 0;
  }

  public String getEmail() {
    return email;
  }

  public String getToken() {
    return token;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getPassword() {
    return password;
  }

  public enum Type {
    APTOIDE, GOOGLE, FACEBOOK, ABAN
  }

  public enum Access {
    PUBLIC, PRIVATE, UNLISTED
  }
}

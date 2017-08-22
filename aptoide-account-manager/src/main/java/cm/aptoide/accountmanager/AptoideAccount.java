/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

import android.text.TextUtils;
import java.util.List;
import rx.Completable;

public final class AptoideAccount implements Account {

  private final String id;
  private final String email;
  private final String nickname;
  private final String avatar;
  private final String refreshToken;
  private final Type type;
  private final Store store;
  private final boolean adultContentEnabled;
  private final Access access;
  private final boolean accessConfirmed;
  private final String password;
  private final List<Store> subscribedStores;
  private final AccountService accountService;

  private String token;

  public AptoideAccount(String id, String email, String nickname, String avatar,
      String refreshToken, String token, String password, Type type, Store store,
      boolean adultContentEnabled, Access access, boolean accessConfirmed,
      List<Store> subscribedStores, AccountService accountService) {
    this.id = id;
    this.email = email;
    this.nickname = nickname;
    this.avatar = avatar;
    this.refreshToken = refreshToken;
    this.token = token;
    this.password = password;
    this.type = type;
    this.store = store;
    this.adultContentEnabled = adultContentEnabled;
    this.access = access;
    this.accessConfirmed = accessConfirmed;
    this.subscribedStores = subscribedStores;
    this.accountService = accountService;
  }

  @Override public Completable logout() {
    return Completable.complete();
  }

  @Override public Completable refreshToken() {
    return accountService.refreshToken(getRefreshToken())
        .doOnSuccess(token -> refreshToken(token))
        .toCompletable();
  }

  @Override public List<Store> getSubscribedStores() {
    return subscribedStores;
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
    return (!isEmpty(getEmail())
        && !isEmpty(getAccessToken())
        && !isEmpty(getRefreshToken())
        && !isEmpty(getPassword()));
  }

  @Override public String getEmail() {
    return email;
  }

  @Override public String getAccessToken() {
    return token;
  }

  @Override public String getRefreshToken() {
    return refreshToken;
  }

  @Override public String getPassword() {
    return password;
  }

  @Override public Type getType() {
    return type;
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

  private void refreshToken(String accessToken) {
    this.token = accessToken;
  }

  private boolean isEmpty(String string) {
    return string == null
        || string.trim()
        .length() == 0;
  }

  @Override public String toString() {
    return "AptoideAccount{"
        + "id='"
        + id
        + '\''
        + ", email='"
        + email
        + '\''
        + ", nickname='"
        + nickname
        + '\''
        + ", avatar='"
        + avatar
        + '\''
        + ", refreshToken='"
        + refreshToken
        + '\''
        + ", type="
        + type
        + ", store='"
        + store.toString()
        + '\''
        + ", adultContentEnabled="
        + adultContentEnabled
        + ", access="
        + access
        + ", accessConfirmed="
        + accessConfirmed
        + ", password='"
        + password
        + '\''
        + ", subscribedStores="
        + subscribedStores
        + ", accountService="
        + accountService
        + ", token='"
        + token
        + '\''
        + '}';
  }
}

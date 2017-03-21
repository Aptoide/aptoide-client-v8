/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

import android.text.TextUtils;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountException;
import cm.aptoide.accountmanager.Store;
import cm.aptoide.pt.dataprovider.ws.v3.OAuth2AuthenticationRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v3.OAuth;
import java.util.Collections;
import java.util.List;
import rx.Completable;
import rx.schedulers.Schedulers;

public final class AptoideAccount implements Account {

  private final String id;
  private final String email;
  private final String nickname;
  private final String avatar;
  private final String refreshToken;
  private final Type type;
  private final String store;
  private final String storeAvatar;
  private final boolean adultContentEnabled;
  private final Access access;
  private final boolean accessConfirmed;
  private final String password;
  private final List<Store> subscribedStores;
  private final AptoideClientUUID aptoideClientUUID;

  private String token;

  public AptoideAccount(String id, String email, String nickname, String avatar,
      String refreshToken, String token, String password, Type type, String store,
      String storeAvatar, boolean adultContentEnabled, Access access, boolean accessConfirmed,
      List<Store> subscribedStores, AptoideClientUUID aptoideClientUUID) {
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
    this.aptoideClientUUID = aptoideClientUUID;
  }

  @Override public Completable logout() {
    return Completable.complete();
  }

  @Override public Completable refreshToken() {
    return OAuth2AuthenticationRequest.of(refreshToken, aptoideClientUUID.getUniqueIdentifier())
        .observe()
        .subscribeOn(Schedulers.io())
        .toSingle()
        .flatMapCompletable(oAuth -> {
          if (!oAuth.hasErrors()) {
            return Completable.fromAction(() -> refreshToken(oAuth));
          } else {
            return Completable.error(new AccountException(oAuth.getError()));
          }
        });
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

  @Override public String getStoreName() {
    return store;
  }

  @Override public String getStoreAvatar() {
    return storeAvatar;
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
    return (!isEmpty(getEmail()) && !isEmpty(getToken()) && !isEmpty(getRefreshToken()) && !isEmpty(
        getPassword()));
  }

  @Override public String getEmail() {
    return email;
  }

  @Override public String getToken() {
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

  private void refreshToken(OAuth oAuth) {
    this.token = oAuth.getAccessToken();
  }

  private boolean isEmpty(String string) {
    return string == null || string.trim().length() == 0;
  }
}

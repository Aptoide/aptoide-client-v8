/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

/**
 * Created by marcelobenites on 10/02/17.
 */

public class Account {

  private final String username;
  private final String refreshToken;
  private final String token;
  private final String encryptedPassword;

  public Account(String username, String refreshToken, String token, String encryptedPassword) {
    this.username = username;
    this.refreshToken = refreshToken;
    this.token = token;
    this.encryptedPassword = encryptedPassword;
  }

  public String getEncryptedPassword() {
    return encryptedPassword;
  }

  public String getUsername() {
    return username;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getToken() {
    return token;
  }
}

/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 23-05-2016.
 */
public abstract class BaseRequestWithStore<U, B extends BaseBodyWithStore> extends V7<U, B> {

  public BaseRequestWithStore(B body, OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static class StoreCredentials {
    private final Long id;
    private final String name;
    private final String username;
    private final String passwordSha1;

    public Long getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getUsername() {
      return username;
    }

    public String getPasswordSha1() {
      return passwordSha1;
    }

    public StoreCredentials() {
      this.name = null;
      this.id = null;
      this.username = null;
      this.passwordSha1 = null;
    }

    public StoreCredentials(long id, String username, String passwordSha1) {
      this.name = null;
      this.id = id;
      this.username = username;
      this.passwordSha1 = passwordSha1;
    }

    public StoreCredentials(String name, String username, String passwordSha1) {
      this.id = null;
      this.name = name;
      this.username = username;
      this.passwordSha1 = passwordSha1;
    }

    public StoreCredentials(long id, String name, String username, String passwordSha1) {
      this.id = id;
      this.name = name;
      this.username = username;
      this.passwordSha1 = passwordSha1;
    }
  }
}

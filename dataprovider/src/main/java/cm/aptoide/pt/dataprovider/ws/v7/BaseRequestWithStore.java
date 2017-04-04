/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 23-05-2016.
 */
public abstract class BaseRequestWithStore<U, B extends BaseBodyWithStore> extends V7<U, B> {

  public BaseRequestWithStore(B body, OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
  }

  public static class StoreCredentials {
    @Getter private final Long id;
    @Getter private final String name;
    @Getter private final String username;
    @Getter private final String passwordSha1;

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

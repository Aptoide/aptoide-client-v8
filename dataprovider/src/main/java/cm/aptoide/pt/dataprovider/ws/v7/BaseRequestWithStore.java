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

  public BaseRequestWithStore(B body, String baseHost) {
    super(body, baseHost);
  }

  public BaseRequestWithStore(B body, Converter.Factory converterFactory,
      String baseHost) {
    super(body, converterFactory, baseHost);
  }

  public BaseRequestWithStore(B body, OkHttpClient httpClient, String baseHost) {
    super(body, httpClient, baseHost);
  }

  public BaseRequestWithStore(B body, OkHttpClient httpClient, Converter.Factory converterFactory,
      String baseHost) {
    super(body, httpClient, converterFactory, baseHost);
  }

  public static class StoreCredentials {
    @Getter private final Long id;
    @Getter private final String name;
    @Getter private final String username;
    @Getter private final String passwordSha1;

    public StoreCredentials(Long id, String username, String passwordSha1) {
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
  }
}

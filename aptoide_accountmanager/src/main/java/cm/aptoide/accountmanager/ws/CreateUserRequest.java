/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.accountmanager.ws;

import android.text.TextUtils;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 4/29/16.
 */
@Data @Accessors(chain = true) @EqualsAndHashCode(callSuper = true) public class CreateUserRequest
    extends v3accountManager<OAuth> {

  private String password;
  private String email;
  private String name;

  CreateUserRequest() {
  }

  CreateUserRequest(OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(httpClient, converterFactory);
  }

  public static CreateUserRequest of(String email, String password) {
    return new CreateUserRequest().setEmail(email).setName("").setPassword(password);
  }

  @Override
  protected Observable<OAuth> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {

    HashMapNotNull<String, String> parameters = new HashMapNotNull<String, String>();

    String passhash;
    passhash = AptoideUtils.AlgorithmU.computeSha1(password);
    parameters.put("mode", "json");
    parameters.put("email", email);
    parameters.put("passhash", passhash);

    if (!TextUtils.isEmpty(Application.getConfiguration().getExtraId())) {
      parameters.put("oem_id", Application.getConfiguration().getExtraId());
    }

    parameters.put("hmac",
        AptoideUtils.AlgorithmU.computeHmacSha1(email + passhash + name, "bazaar_hmac"));

    return interfaces.createUser(parameters);
  }
}

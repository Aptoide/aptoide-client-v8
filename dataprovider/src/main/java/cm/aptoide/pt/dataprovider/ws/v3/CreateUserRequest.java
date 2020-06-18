/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.BaseV3Response;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.utils.AptoideUtils;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import rx.Observable;

public class CreateUserRequest extends V3<BaseV3Response> {

  private final MultipartBody.Part multipartBodyFile;
  private final HashMapNotNull<String, RequestBody> multipartRequestBody;

  public CreateUserRequest(MultipartBody.Part file, BaseBody baseBody, OkHttpClient httpClient,
      BodyInterceptor<BaseBody> bodyInterceptor, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, WebService.getDefaultConverter(), bodyInterceptor, tokenInvalidator,
        sharedPreferences);
    multipartBodyFile = file;
    multipartRequestBody = null;
  }

  public static CreateUserRequest of(String email, String code,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, String extraId) {
    final BaseBody body = new BaseBody();
    final String codeHash = AptoideUtils.AlgorithmU.computeSha1(code);
    addBaseParameters(email, body, codeHash, extraId);

    body.put("hmac", AptoideUtils.AlgorithmU.computeHmacSha1(email + codeHash, "bazaar_hmac"));

    return new CreateUserRequest(null, body, httpClient, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  private static void addBaseParameters(String email, BaseBody parameters, String codeHash,
      String extraId) {
    parameters.put("mode", "json");
    parameters.put("email", email);
    parameters.put("passhash", codeHash);

    if (!TextUtils.isEmpty(extraId)) {
      parameters.put("oem_id", extraId);
    }

    parameters.put("accepts", "tos,privacy");
  }

  @Override
  protected Observable<BaseV3Response> loadDataFromNetwork(Service service, boolean bypassCache) {
    if (multipartBodyFile != null) {
      return service.createUserWithFile(multipartBodyFile, multipartRequestBody, bypassCache);
    }
    return service.createUser(map, bypassCache);
  }
}

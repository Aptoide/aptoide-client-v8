/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v3.BaseV3Response;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import rx.Observable;

/**
 * Created by trinkes on 4/29/16.
 */
public class CreateUserRequest extends V3<BaseV3Response> {

  private final MultipartBody.Part multipartBodyFile;
  private final HashMapNotNull<String, RequestBody> multipartRequestBody;

  public CreateUserRequest(MultipartBody.Part file, BaseBody baseBody, OkHttpClient httpClient,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(baseBody, httpClient, WebService.getDefaultConverter(), bodyInterceptor);
    multipartBodyFile = file;
    multipartRequestBody = null;
  }

  public CreateUserRequest(MultipartBody.Part file, HashMapNotNull<String, RequestBody> body,
      OkHttpClient okHttpClient, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(okHttpClient, WebService.getDefaultConverter(), bodyInterceptor);
    multipartBodyFile = file;
    multipartRequestBody = body;
  }

  public static CreateUserRequest of(String email, String password, String aptoideClientUUID,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    final BaseBody body = new BaseBody();
    final String passhash = AptoideUtils.AlgorithmU.computeSha1(password);
    addBaseParameters(email, aptoideClientUUID, body, passhash);

    body.put("hmac", AptoideUtils.AlgorithmU.computeHmacSha1(email + passhash, "bazaar_hmac"));

    return new CreateUserRequest(null, body,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        bodyInterceptor);
  }

  private static void addBaseParameters(String email, String aptoideClientUUID, BaseBody parameters,
      String passhash) {
    parameters.put("mode", "json");
    parameters.put("email", email);
    parameters.put("passhash", passhash);
    parameters.put("aptoide_uid", aptoideClientUUID);

    if (!TextUtils.isEmpty(Application.getConfiguration().getExtraId())) {
      parameters.put("oem_id", Application.getConfiguration().getExtraId());
    }
  }

  public static CreateUserRequest of(String email, String name, String password,
      String userAvatarPath, String aptoideClientUUID, String accessToken,
      BodyInterceptor<BaseBody> bodyInterceptor) {

    final BaseBody body = new BaseBody();
    final String passhash = AptoideUtils.AlgorithmU.computeSha1(password);
    if (!TextUtils.isEmpty(userAvatarPath)) {

      if (!TextUtils.isEmpty(Application.getConfiguration().getExtraId())) {
        body.put("oem_id", createBodyPartFromString(Application.getConfiguration().getExtraId()));
      }
      HashMapNotNull<String, RequestBody> multipartBody = new HashMapNotNull<>();
      multipartBody.put("mode", createBodyPartFromString("json"));
      multipartBody.put("email", createBodyPartFromString(email));
      multipartBody.put("passhash", createBodyPartFromString(passhash));
      multipartBody.put("hmac", createBodyPartFromString(
          AptoideUtils.AlgorithmU.computeHmacSha1(email + passhash + name + "true",
              "bazaar_hmac")));
      multipartBody.put("name", createBodyPartFromString(name));
      multipartBody.put("update", createBodyPartFromString("true"));
      final File file = new File(userAvatarPath);
      RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
      MultipartBody.Part multipartBodyFile =
          MultipartBody.Part.createFormData("user_avatar", file.getName(), requestFile);
      return new CreateUserRequest(multipartBodyFile, multipartBody, getHttpClient(accessToken),
          bodyInterceptor);
    } else if (userAvatarPath.isEmpty()) {
      body.put("update", "true");
      body.put("name", name);
    }

    addBaseParameters(email, aptoideClientUUID, body, passhash);

    body.put("hmac",
        AptoideUtils.AlgorithmU.computeHmacSha1(email + passhash + name + "true", "bazaar_hmac"));

    return new CreateUserRequest(null, body,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        bodyInterceptor);
  }

  private static RequestBody createBodyPartFromString(String string) {
    return RequestBody.create(MediaType.parse("multipart/form-data"), string);
  }

  private static OkHttpClient getHttpClient(String accessToken) {
    OkHttpClient.Builder clientBuilder =
        OkHttpClientFactory.newClient(() -> accessToken).newBuilder();
    clientBuilder.connectTimeout(2, TimeUnit.MINUTES);
    clientBuilder.readTimeout(2, TimeUnit.MINUTES);
    clientBuilder.writeTimeout(2, TimeUnit.MINUTES);
    return clientBuilder.build();
  }

  @Override protected Observable<BaseV3Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    if (multipartBodyFile != null) {
      return interfaces.createUserWithFile(multipartBodyFile, multipartRequestBody, bypassCache);
    }
    return interfaces.createUser(map, bypassCache);
  }
}

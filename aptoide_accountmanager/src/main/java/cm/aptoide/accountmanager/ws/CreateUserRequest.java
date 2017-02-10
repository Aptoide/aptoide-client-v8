/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.accountmanager.ws;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.Application;
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
public class CreateUserRequest extends v3accountManager<OAuth> {

  private final String email;
  private final String password;
  private final String name;
  private final String update;
  private final String userAvatarPath;
  private String aptoideClientUUID;

  private CreateUserRequest(OkHttpClient client, String email, String password,
      String aptoideClientUUID, AptoideAccountManager accountManager) {
    this(client, email, password, "", "", "", aptoideClientUUID, accountManager);
  }

  private CreateUserRequest(OkHttpClient client, String email, String password, String name,
      String update, String userAvatarPath, String aptoideClientUUID,
      AptoideAccountManager accountManager) {
    super(client, accountManager);
    this.email = email;
    this.password = password;
    this.name = name;
    this.update = update;
    this.userAvatarPath = userAvatarPath;
    this.aptoideClientUUID = aptoideClientUUID;
  }

  public static CreateUserRequest of(String email, String password, String aptoideClientUUID,
      AptoideAccountManager accountManager) {
    return new CreateUserRequest(getHttpClient(accountManager), email, password, aptoideClientUUID,
        accountManager);
  }

  private static OkHttpClient getHttpClient(AptoideAccountManager accountManager) {
    OkHttpClient.Builder clientBuilder =
        OkHttpClientFactory.newClient(() -> accountManager.getAccessToken()).newBuilder();
    clientBuilder.connectTimeout(2, TimeUnit.MINUTES);
    clientBuilder.readTimeout(2, TimeUnit.MINUTES);
    clientBuilder.writeTimeout(2, TimeUnit.MINUTES);
    return clientBuilder.build();
  }

  public static CreateUserRequest of(String update, String email, String name, String password,
      String userAvatarPath, String aptoideClientUUID, AptoideAccountManager accountManager) {
    return new CreateUserRequest(getHttpClient(accountManager), email, password, name, update,
        userAvatarPath, aptoideClientUUID, accountManager);
  }

  public String getPassword() {
    return password;
  }

  @Override
  protected Observable<OAuth> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {

    HashMapNotNull<String, String> parameters = new HashMapNotNull<>();

    if (update.equals("true") && !TextUtils.isEmpty(getUserAvatarPath())) {
      HashMapNotNull<String, RequestBody> body = new HashMapNotNull<>();

      String calculatedPasshash;
      calculatedPasshash = AptoideUtils.AlgorithmU.computeSha1(password);
      RequestBody mode = createBodyPartFromString("json");
      RequestBody email = createBodyPartFromString(getEmail());
      RequestBody passhash = createBodyPartFromString(calculatedPasshash);

      if (!TextUtils.isEmpty(Application.getConfiguration().getExtraId())) {
        RequestBody oem_id = createBodyPartFromString(Application.getConfiguration().getExtraId());
        body.put("oem_id", oem_id);
      }

      RequestBody hmac = createBodyPartFromString(AptoideUtils.AlgorithmU.computeHmacSha1(
          getEmail() + calculatedPasshash + getName() + getUpdate(), "bazaar_hmac"));

      RequestBody name = createBodyPartFromString(getName());
      RequestBody update = createBodyPartFromString(getUpdate());

      body.put("mode", mode);
      body.put("email", email);
      body.put("passhash", passhash);
      body.put("hmac", hmac);
      body.put("name", name);
      body.put("update", update);
      File file = new File(userAvatarPath);
      RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
      MultipartBody.Part multipartBody =
          MultipartBody.Part.createFormData("user_avatar", file.getName(), requestFile);
      return interfaces.createUserWithFile(multipartBody, body);
    } else if (update.equals("true") && userAvatarPath.isEmpty()) {
      parameters.put("update", update);
      parameters.put("name", name);
    }

    String passhash;
    passhash = AptoideUtils.AlgorithmU.computeSha1(password);
    parameters.put("mode", "json");
    parameters.put("email", email);
    parameters.put("passhash", passhash);
    parameters.put("aptoide_uid", aptoideClientUUID);

    if (!TextUtils.isEmpty(Application.getConfiguration().getExtraId())) {
      parameters.put("oem_id", Application.getConfiguration().getExtraId());
    }

    if (update.equals("true")) {
      parameters.put("hmac",
          AptoideUtils.AlgorithmU.computeHmacSha1(email + passhash + name + update, "bazaar_hmac"));
    } else {
      parameters.put("hmac",
          AptoideUtils.AlgorithmU.computeHmacSha1(email + passhash + name, "bazaar_hmac"));
    }
    return interfaces.createUser(parameters);
  }

  public String getUserAvatarPath() {
    return userAvatarPath;
  }

  private RequestBody createBodyPartFromString(String string) {
    return RequestBody.create(MediaType.parse("multipart/form-data"), string);
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public String getUpdate() {
    return update;
  }
}

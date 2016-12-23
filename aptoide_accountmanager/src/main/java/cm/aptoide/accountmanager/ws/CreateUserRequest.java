/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.accountmanager.ws;

import android.content.res.Configuration;
import android.text.TextUtils;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import java.io.File;
import java.util.ArrayList;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
  private String update = "";
  private String userAvatarPath = "";

  CreateUserRequest() {
  }

  CreateUserRequest(OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(httpClient, converterFactory);
  }

  public static CreateUserRequest of(String email, String password) {
    return new CreateUserRequest().setEmail(email).setName("").setPassword(password);
  }

  public static CreateUserRequest of(String update, String email, String name, String password, String userAvatarPath) {
    return new CreateUserRequest().setEmail(email).setName(name).setPassword(password).setUpdate(update).setUserAvatarPath(userAvatarPath);
  }

  @Override
  protected Observable<OAuth> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {

    HashMapNotNull<String, String> parameters = new HashMapNotNull<String, String>();

    if (update.equals("true") && !userAvatarPath.isEmpty()) {
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

      RequestBody hmac = createBodyPartFromString(AptoideUtils.AlgorithmU
          .computeHmacSha1(getEmail() + calculatedPasshash + getName() + getUpdate(), "bazaar_hmac"));

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
      MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("user_avatar",
          file.getName(), requestFile);
      return interfaces.createUserWithFile(multipartBody, body);
    } else if(update.equals("true") && userAvatarPath.isEmpty()) {
      parameters.put("update", update);
      parameters.put("name", name);
    }


    String passhash;
    passhash = AptoideUtils.AlgorithmU.computeSha1(password);
    parameters.put("mode", "json");
    parameters.put("email", email);
    parameters.put("passhash", passhash);
    parameters.put("aptoide_uid",
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID());

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

  private RequestBody createBodyPartFromString(String string) {
    return RequestBody.create(MediaType.parse("multipart/form-data"), string);
  }

}

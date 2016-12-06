/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.accountmanager.ws;

import android.content.res.Configuration;
import android.text.TextUtils;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import java.io.File;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
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
  private String userAvatarPath;

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

    if (update.equals("true")) {
      parameters.put("name", name);
      parameters.put("update", update);
      File file = new File(Application.getConfiguration().getUserAvatarCachePath()+ "aptoide_user_avatar.png");
      RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
      //MultipartBody.Part body = MultipartBody.Part.createFormData("user_avatar", file.getName(), image);
      return interfaces.createUserWithFile(body, parameters);
    }

    return interfaces.createUser(parameters);

  }
}

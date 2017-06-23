package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by pedroribeiro on 25/05/17.
 */

public class SetUserMultipartRequest
    extends V7<BaseV7Response, HashMapNotNull<String, RequestBody>> {

  private final MultipartBody.Part multipartBody;

  private SetUserMultipartRequest(MultipartBody.Part file, HashMapNotNull<String, RequestBody> body,
      OkHttpClient httpClient, Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, getHost(), httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
    multipartBody = file;
  }

  public static String getHost() {
    return BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static SetUserMultipartRequest of(String username, String userAvatar,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, ObjectMapper serializer,
      TokenInvalidator tokenInvalidator) {

    final RequestBodyFactory requestBodyFactory = new RequestBodyFactory();
    final HashMapNotNull<String, RequestBody> body = new HashMapNotNull<>();

    try {
      body.put("user_properties", requestBodyFactory.createBodyPartFromString(
          serializer.writeValueAsString(new SetUserRequest.UserProperties(username))));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    return new SetUserMultipartRequest(
        requestBodyFactory.createBodyPartFromFile("user_avatar", new File(userAvatar)), body,
        httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.editUser(multipartBody, body);
  }
}

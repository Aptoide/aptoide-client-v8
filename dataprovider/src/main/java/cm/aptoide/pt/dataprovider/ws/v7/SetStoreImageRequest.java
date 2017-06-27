package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Converter;
import rx.Observable;

public class SetStoreImageRequest extends V7<BaseV7Response, HashMapNotNull<String, RequestBody>> {

  private final MultipartBody.Part multipartBody;

  private SetStoreImageRequest(HashMapNotNull<String, RequestBody> body,
      MultipartBody.Part multipartBody,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, SharedPreferences sharedPreferences,
      TokenInvalidator tokenInvalidator) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.multipartBody = multipartBody;
  }

  @NonNull public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static SetStoreImageRequest of(String storeName, String storeTheme,
      String storeDescription, String storeAvatarPath,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, RequestBodyFactory requestBodyFactory,
      ObjectMapper serializer, SharedPreferences sharedPreferences,
      TokenInvalidator tokenInvalidator) {

    final HashMapNotNull<String, RequestBody> body = new HashMapNotNull<>();

    body.put("store_name", requestBodyFactory.createBodyPartFromString(storeName));
    addStoreProperties(storeTheme, storeDescription, requestBodyFactory, serializer, body);

    return new SetStoreImageRequest(body,
        requestBodyFactory.createBodyPartFromFile("store_avatar", new File(storeAvatarPath)),
        bodyInterceptor, httpClient, converterFactory, sharedPreferences, tokenInvalidator);
  }

  public static SetStoreImageRequest of(long storeId, String storeTheme, String storeDescription,
      String storeAvatarPath, BodyInterceptor<HashMapNotNull<String, RequestBody>> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      RequestBodyFactory requestBodyFactory, ObjectMapper serializer,
      SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator) {
    final HashMapNotNull<String, RequestBody> body = new HashMapNotNull<>();

    body.put("store_id", requestBodyFactory.createBodyPartFromLong(storeId));
    addStoreProperties(storeTheme, storeDescription, requestBodyFactory, serializer, body);

    return new SetStoreImageRequest(body,
        requestBodyFactory.createBodyPartFromFile("store_avatar", new File(storeAvatarPath)),
        bodyInterceptor, httpClient, converterFactory, sharedPreferences, tokenInvalidator);
  }

  private static void addStoreProperties(String storeTheme, String storeDescription,
      RequestBodyFactory requestBodyFactory, ObjectMapper serializer,
      HashMapNotNull<String, RequestBody> body) {
    try {
      body.put("store_properties", requestBodyFactory.createBodyPartFromString(
          serializer.writeValueAsString(
              new SimpleSetStoreRequest.StoreProperties(storeTheme, storeDescription))));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.editStore(multipartBody, body);
  }
}

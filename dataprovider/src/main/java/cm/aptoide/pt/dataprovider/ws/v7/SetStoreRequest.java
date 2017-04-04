package cm.aptoide.pt.dataprovider.ws.v7;

import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import rx.Observable;

public class SetStoreRequest extends V7<BaseV7Response, HashMapNotNull<String, RequestBody>> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  private final MultipartBody.Part multipartBody;

  private SetStoreRequest(HashMapNotNull<String, RequestBody> body, MultipartBody.Part file,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> bodyInterceptor) {
    super(body, BASE_HOST, getLongerTimeoutClient(), WebService.getDefaultConverter(),
        bodyInterceptor);
    multipartBody = file;
  }

  public static SetStoreRequest of(String accessToken, String storeName, String storeTheme,
      String storeAvatarPath,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> bodyInterceptor) {

    final RequestBodyFactory requestBodyFactory = new RequestBodyFactory();
    final HashMapNotNull<String, RequestBody> body = new HashMapNotNull<>();

    body.put("store_name", requestBodyFactory.createBodyPartFromString(storeName));

    return new SetStoreRequest(body,
        requestBodyFactory.createBodyPartFromFile("store_avatar", new File(storeAvatarPath)),
        bodyInterceptor);
  }

  public static SetStoreRequest of(String accessToken, String storeName, String storeTheme,
      String storeAvatarPath, String storeDescription, Boolean editStore, long storeId,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> bodyInterceptor) {
    final RequestBodyFactory requestBodyFactory = new RequestBodyFactory();
    final HashMapNotNull<String, RequestBody> body = new HashMapNotNull<>();

    body.put("store_id", requestBodyFactory.createBodyPartFromLong(storeId));

    return new SetStoreRequest(body,
        requestBodyFactory.createBodyPartFromFile("store_avatar", new File(storeAvatarPath)),
        bodyInterceptor);
  }

  @NonNull private static OkHttpClient getLongerTimeoutClient() {
    return OkHttpClientFactory.newClient(() -> SecurePreferences.getUserAgent())
        .newBuilder()
        .connectTimeout(2, TimeUnit.MINUTES)
        .readTimeout(2, TimeUnit.MINUTES)
        .writeTimeout(2, TimeUnit.MINUTES)
        .build();
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.editStore(multipartBody, body);
  }
}

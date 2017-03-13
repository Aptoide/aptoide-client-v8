package cm.aptoide.pt.dataprovider.ws.v7;

import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.ws.v7.store.AccessTokenRequestBodyAdapter;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import java.io.File;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import rx.Observable;

/**
 * Created by pedroribeiro on 09/12/16.
 */

@Data @Accessors(chain = true) @EqualsAndHashCode(callSuper = true) public class SetStoreRequest
    extends V7<BaseV7Response, AccessTokenBody> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  private final MultipartBody.Part multipartBody;

  private SetStoreRequest(AccessTokenBody body, MultipartBody.Part file, OkHttpClient customClient,
      BodyInterceptor bodyInterceptor) {
    super(body, BASE_HOST, customClient, WebService.getDefaultConverter(), bodyInterceptor);
    multipartBody = file;
  }

  public static SetStoreRequest of(String accessToken, String storeName, String storeTheme,
      String storeAvatarPath, BodyInterceptor bodyInterceptor) {
    AccessTokenRequestBodyAdapter body =
        new AccessTokenRequestBodyAdapter(new BaseBody(), bodyInterceptor, accessToken, storeName,
            storeTheme);
    File file = new File(storeAvatarPath);
    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

    // use a client with bigger timeouts
    OkHttpClient.Builder clientBuilder = getBuilder();

    return new SetStoreRequest(body,
        MultipartBody.Part.createFormData("store_avatar", file.getName(), requestFile),
        clientBuilder.build(), bodyInterceptor);
  }

  @NonNull private static OkHttpClient.Builder getBuilder() {
    OkHttpClient.Builder clientBuilder =
        OkHttpClientFactory.newClient(() -> SecurePreferences.getUserAgent()).newBuilder();
    clientBuilder.connectTimeout(2, TimeUnit.MINUTES);
    clientBuilder.readTimeout(2, TimeUnit.MINUTES);
    clientBuilder.writeTimeout(2, TimeUnit.MINUTES);
    return clientBuilder;
  }

  public static SetStoreRequest of(String accessToken, String storeName, String storeTheme,
      String storeAvatarPath, String storeDescription, Boolean editStore, long storeId,
      BodyInterceptor bodyInterceptor) {
    AccessTokenRequestBodyAdapter body =
        new AccessTokenRequestBodyAdapter(new BaseBody(), bodyInterceptor, accessToken, storeName,
            storeTheme, storeDescription, editStore, storeId);
    File file = new File(storeAvatarPath);
    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

    // use a client with bigger timeouts
    OkHttpClient.Builder clientBuilder = getBuilder();

    return new SetStoreRequest(body,
        MultipartBody.Part.createFormData("store_avatar", file.getName(), requestFile),
        clientBuilder.build(), bodyInterceptor);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return ((AccessTokenRequestBodyAdapter) body).get()
        .flatMapObservable(body -> interfaces.editStore(multipartBody, body));
  }
}

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import java.io.File;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Converter;
import rx.Observable;

public class SetStoreImageRequest extends V7<BaseV7Response, HashMapNotNull<String, RequestBody>> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  private final MultipartBody.Part multipartBody;

  private SetStoreImageRequest(HashMapNotNull<String, RequestBody> body, MultipartBody.Part file,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
    multipartBody = file;
  }

  public static SetStoreImageRequest of(String storeAvatarPath, String storeName,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    final RequestBodyFactory requestBodyFactory = new RequestBodyFactory();
    final HashMapNotNull<String, RequestBody> body = new HashMapNotNull<>();

    body.put("store_name", requestBodyFactory.createBodyPartFromString(storeName));

    return new SetStoreImageRequest(body,
        requestBodyFactory.createBodyPartFromFile("store_avatar", new File(storeAvatarPath)),
        bodyInterceptor, httpClient, converterFactory);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.editStore(multipartBody, body);
  }
}

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ChangeStoreSubscriptionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 03/03/2017.
 */

public class ChangeStoreSubscriptionRequest
    extends V7<ChangeStoreSubscriptionResponse, ChangeStoreSubscriptionRequest.Body> {

  private static final String BASE_HOST = (ManagerPreferences.isToolboxEnableHttpScheme() ? "http"
      : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  protected ChangeStoreSubscriptionRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
  }

  public static ChangeStoreSubscriptionRequest of(String storeName,
      ChangeStoreSubscriptionResponse.StoreSubscriptionState storeSubscription, String storeUser,
      String sha1PassWord, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    final Body body = new Body(storeName, storeSubscription, storeUser, sha1PassWord);
    return new ChangeStoreSubscriptionRequest(body, bodyInterceptor, httpClient, converterFactory);
  }

  @Override
  protected Observable<ChangeStoreSubscriptionResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.changeStoreSubscription(bypassCache, body);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {
    @Getter private final String storeName;
    @Getter private final ChangeStoreSubscriptionResponse.StoreSubscriptionState status;
    @Getter private String storePassSha1;
    @Getter private String storeUser;

    public Body(String storeName, ChangeStoreSubscriptionResponse.StoreSubscriptionState status) {
      this.storeName = storeName;
      this.status = status;
    }

    public Body(String storeName, ChangeStoreSubscriptionResponse.StoreSubscriptionState status,
        String storeUser, String storePassSha1) {
      this.storeName = storeName;
      this.storePassSha1 = storePassSha1;
      this.status = status;
      this.storeUser = storeUser;
    }
  }
}

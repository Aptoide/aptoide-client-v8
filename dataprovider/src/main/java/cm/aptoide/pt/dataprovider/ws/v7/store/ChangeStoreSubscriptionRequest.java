package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.ChangeStoreSubscriptionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import rx.Observable;

/**
 * Created by trinkes on 03/03/2017.
 */

public class ChangeStoreSubscriptionRequest
    extends V7<ChangeStoreSubscriptionResponse, ChangeStoreSubscriptionRequest.Body> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  protected ChangeStoreSubscriptionRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static ChangeStoreSubscriptionRequest of(String storeName,
      ChangeStoreSubscriptionResponse.StoreSubscriptionState storeSubscription, String storeUser,
      String sha1PassWord, BodyInterceptor<BaseBody> bodyInterceptor) {
    final Body body = new Body(storeName, storeSubscription, storeUser, sha1PassWord);
    return new ChangeStoreSubscriptionRequest(body, bodyInterceptor);
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

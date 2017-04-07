package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.GetMySubscribedStoresResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by neuro on 06-04-2017.
 */

public class GetMySubscribedStoresRequest
    extends V7<GetMySubscribedStoresResponse, GetMySubscribedStoresRequest.Body> {

  public GetMySubscribedStoresRequest(String accessToken,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(new Body(accessToken), BASE_HOST,
        OkHttpClientFactory.getSingletonClient(SecurePreferences::getUserAgent, false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  @Override
  protected Observable<GetMySubscribedStoresResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getMySubscribedStores(bypassCache, body);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody
      implements Endless {

    Sort sort;
    private Integer limit;
    private int offset;
    private Order order;
    private boolean refresh;

    public Body(String accessToken) {
      setAccessToken(accessToken);
    }

    public enum Sort {
      added, latest, alpha, downloads, trending
    }
  }
}

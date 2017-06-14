package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.GetMySubscribedStoresResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 06-04-2017.
 */

public class GetMySubscribedStoresRequest
    extends V7<GetMySubscribedStoresResponse, GetMySubscribedStoresRequest.Body> {

  public GetMySubscribedStoresRequest(String accessToken, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator) {
    super(new Body(accessToken), BASE_HOST, httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
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

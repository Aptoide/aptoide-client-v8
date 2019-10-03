package cm.aptoide.pt.dataprovider.ws.v7.promotions;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class GetPromotionAppsRequest
    extends V7<GetPromotionAppsResponse, GetPromotionAppsRequest.Body> {

  public GetPromotionAppsRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static GetPromotionAppsRequest of(String promotionId,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return new GetPromotionAppsRequest(new Body(promotionId), bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<GetPromotionAppsResponse> loadDataFromNetwork(V7.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getPromotionApps(30, body, bypassCache, true);
  }

  public static class Body extends BaseBody {
    private String promotionId;

    public Body(String promotionId) {
      this.promotionId = promotionId;
    }

    public String getPromotionId() {
      return promotionId;
    }
  }
}

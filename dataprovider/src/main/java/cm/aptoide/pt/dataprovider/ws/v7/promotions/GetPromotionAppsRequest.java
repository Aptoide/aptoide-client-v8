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

  public static GetPromotionAppsRequest of(Body params, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    return new GetPromotionAppsRequest(params, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<GetPromotionAppsResponse> loadDataFromNetwork(V7.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getPromotionApps(30, body, bypassCache);
  }

  public static class Body extends BaseBody {
    private String promotionId;
    private String packageName;

    public Body(String promotionId, String packageName) {
      this.promotionId = promotionId;
      this.packageName = packageName;
    }

    public String getPromotionId() {
      return promotionId;
    }

    public String getPackageName() {
      return packageName;
    }

    public static class Builder {
      private String promotionId = null;
      private String packageName = null;

      public Builder() {
      }

      public Builder promotionId(String id) {
        this.promotionId = id;
        return this;
      }

      public Builder packageName(String name) {
        this.packageName = name;
        return this;
      }

      public Body build() {
        return new Body(promotionId, packageName);
      }
    }
  }
}

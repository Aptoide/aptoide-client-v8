package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDataListResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by filipegoncalves on 4/26/18.
 */

public class GetAppCoinsAdsRequest
    extends V7<BaseV7EndlessDataListResponse<AppCoinsRewardApp>, GetAppCoinsAdsRequest.Body> {

  protected GetAppCoinsAdsRequest(Body body, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_READ_V7_HOST
        + "/api/7/";
  }

  @Override
  protected Observable<BaseV7EndlessDataListResponse<AppCoinsRewardApp>> loadDataFromNetwork(
      Interfaces interfaces, boolean bypassCache) {
    return interfaces.getAppCoinsAds(body, bypassCache, body.getLimit());
  }

  public static class Body extends BaseBody implements Endless {
    private int offset;
    private int limit;

    public Body(int offset, int limit) {
      this.offset = offset;
      this.limit = limit;
    }

    public int getOffset() {
      return offset;
    }

    public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return this.limit;
    }
  }
}

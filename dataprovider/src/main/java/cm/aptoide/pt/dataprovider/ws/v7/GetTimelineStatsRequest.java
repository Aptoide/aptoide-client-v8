package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.model.v7.TimelineStats;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 15/12/2016.
 */

public class GetTimelineStatsRequest extends V7<TimelineStats, GetTimelineStatsRequest.Body> {

  protected GetTimelineStatsRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static GetTimelineStatsRequest of(BodyInterceptor<BaseBody> bodyInterceptor, Long userId,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    return new GetTimelineStatsRequest(new Body(userId), bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<TimelineStats> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getTimelineStats(body, bypassCache);
  }

  public static class Body extends BaseBody {
    private Long userId;

    public Body(Long userId) {
      this.userId = userId;
    }

    public Long getUserId() {
      return userId;
    }

    public void setUserId(Long userId) {
      this.userId = userId;
    }
  }
}

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.TimelineStats;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 15/12/2016.
 */

public class GetTimelineStatsRequest extends V7<TimelineStats, GetTimelineStatsRequest.Body> {

  protected GetTimelineStatsRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
  }

  public static GetTimelineStatsRequest of(BodyInterceptor<BaseBody> bodyInterceptor, Long userId,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    return new GetTimelineStatsRequest(new Body(userId), bodyInterceptor, httpClient,
        converterFactory);
  }

  @Override protected Observable<TimelineStats> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getTimelineStats(body, bypassCache);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {
    private Long userId;

    public Body(Long userId) {
      this.userId = userId;
    }
  }
}

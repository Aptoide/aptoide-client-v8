package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.TimelineStats;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by trinkes on 15/12/2016.
 */

public class GetTimelineStatsRequest extends V7<TimelineStats, GetTimelineStatsRequest.Body> {

  protected GetTimelineStatsRequest(GetTimelineStatsRequest.Body body, String baseHost) {
    super(body, baseHost);
  }

  public static GetTimelineStatsRequest of(String accessToken, String aptoideClientUUID,
      Long userId) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    return new GetTimelineStatsRequest(
        (GetTimelineStatsRequest.Body) decorator.decorate(new Body(userId), accessToken),
        BASE_HOST);
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

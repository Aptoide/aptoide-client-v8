package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.TimelineStats;
import rx.Observable;

/**
 * Created by trinkes on 15/12/2016.
 */

public class GetTimelineStatsRequest extends V7<TimelineStats, BaseBody> {

  protected GetTimelineStatsRequest(BaseBody body, String baseHost) {
    super(body, baseHost);
  }

  public static GetTimelineStatsRequest of(BodyDecorator bodyDecorator) {
    return new GetTimelineStatsRequest(bodyDecorator.decorate(new BaseBody()), BASE_HOST);
  }

  @Override protected Observable<TimelineStats> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getTimelineStats(body, bypassCache);
  }
}

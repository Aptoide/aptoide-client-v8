package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.AnalyticsBaseBody;
import cm.aptoide.pt.model.v7.BaseV7Response;
import java.util.Map;
import rx.Observable;

/**
 * Created by jdandrade on 25/10/2016.
 */

public class AnalyticsEventRequest extends V7<BaseV7Response, AnalyticsEventRequest.Body> {

  private final String action;
  private final String name;
  private final String context;

  private AnalyticsEventRequest(Body body, String baseHost, String action, String name,
      String context) {
    super(body, baseHost);
    this.action = action;
    this.name = name;
    this.context = context;
  }

  public static AnalyticsEventRequest of(String eventName, String context, String action,
      Map<String, Object> data, BodyInterceptor bodyInterceptor) {
    final AnalyticsEventRequest.Body body =
        new AnalyticsEventRequest.Body(DataProvider.getConfiguration().getAppId(), data);

    return new AnalyticsEventRequest((Body) bodyInterceptor.intercept(body), BASE_HOST, action,
        eventName, context);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.addEvent(name, action, context, body);
  }

  static class Body extends AnalyticsBaseBody {

    private final Map<String, Object> data;

    public Body(String aptoidePackage, Map<String, Object> data) {
      super(aptoidePackage);
      this.data = data;
    }

    public Map<String, Object> getData() {
      return data;
    }
  }
}

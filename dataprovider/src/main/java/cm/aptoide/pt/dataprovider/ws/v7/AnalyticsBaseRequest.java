package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.AnalyticsBaseBody;
import cm.aptoide.pt.model.v7.BaseV7Response;

/**
 * Created by trinkes on 30/12/2016.
 */

public abstract class AnalyticsBaseRequest<T extends AnalyticsBaseBody>
    extends V7<BaseV7Response, T> {
  protected AnalyticsBaseRequest(T body, String baseHost) {
    super(body, baseHost);
  }
}

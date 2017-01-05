package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.model.v7.BaseV7Response;
import rx.Observable;

/**
 * Created by trinkes on 30/12/2016.
 */

public class DownloadAnalyticsRequest
    extends AnalyticsBaseRequest<DownloadInstallAnalyticsBaseBody> {

  private String action;
  private String name;
  private String context;

  protected DownloadAnalyticsRequest(DownloadInstallAnalyticsBaseBody body, String baseHost,
      String action, String name, String context) {
    super(body, baseHost);
    this.action = action;
    this.name = name;
    this.context = context;
  }

  public static DownloadAnalyticsRequest of(String aptoideClientUuId, String accessToken,
      DownloadInstallAnalyticsBaseBody body, String action, String name, String context) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUuId);
    return new DownloadAnalyticsRequest(
        (DownloadInstallAnalyticsBaseBody) decorator.decorate(body, accessToken), BASE_HOST, action,
        name, context);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setDownloadAnalyticsEvent(name, action, context, body);
  }
}

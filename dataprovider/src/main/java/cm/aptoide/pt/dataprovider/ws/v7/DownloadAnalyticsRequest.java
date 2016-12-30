package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.model.v7.BaseV7Response;
import lombok.Data;
import rx.Observable;

/**
 * Created by trinkes on 30/12/2016.
 */

public class DownloadAnalyticsRequest extends
    AnalyticsBaseRequest<DownloadInstallAnalyticsBaseBody<DownloadAnalyticsRequest.DownloadEventBody>> {

  protected DownloadAnalyticsRequest(DownloadInstallAnalyticsBaseBody<DownloadEventBody> body,
      String baseHost) {
    super(body, baseHost);
  }

  public static DownloadAnalyticsRequest of(String aptoideClientUuId, String accessToken) {
    DownloadInstallAnalyticsBaseBody<DownloadEventBody> body =
        new DownloadInstallAnalyticsBaseBody<>();
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUuId);
    return new DownloadAnalyticsRequest(
        (DownloadInstallAnalyticsBaseBody<DownloadEventBody>) decorator.decorate(body, accessToken),
        BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setDownloadAnalyticsEvent(body);
  }

  enum DataStatus {
    start, pause, resume, stop,
  }

  enum DataOrigin {
    install, update, downgrade, update_all
  }

  public @Data static class DownloadEventBody extends DownloadInstallAnalyticsBaseBody.Data {
    DataOrigin origin;
    DataStatus status;
  }
}

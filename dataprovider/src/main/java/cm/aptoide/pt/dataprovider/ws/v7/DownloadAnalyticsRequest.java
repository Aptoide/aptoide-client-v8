package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.AnalyticsBaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;
import rx.Observable;

/**
 * Created by trinkes on 30/12/2016.
 */

public class DownloadAnalyticsRequest extends
    AnalyticsBaseRequest<DownloadInstallAnalyticsBaseBody<DownloadAnalyticsRequest.DownloadEventBody>> {

  private String action;
  private String name;
  private String context;

  protected DownloadAnalyticsRequest(DownloadInstallAnalyticsBaseBody<DownloadEventBody> body,
      String baseHost, String action, String name, String context) {
    super(body, baseHost);
    this.action = action;
    this.name = name;
    this.context = context;
  }

  public static DownloadAnalyticsRequest of(String aptoideClientUuId, String accessToken,
      DownloadInstallAnalyticsBaseBody<DownloadEventBody> body, String action, String name,
      String context) {
    AnalyticsBaseBody.User user = new AnalyticsBaseBody.User();
    AnalyticsBaseBody.Aptoide aptoide = new AnalyticsBaseBody.Aptoide();
    aptoide.setAptoidePackageName(DataProvider.getConfiguration().getAppId());
    aptoide.setVercode(AptoideUtils.Core.getVerCode());

    user.setAptoide(aptoide);
    body.setUser(user);
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUuId);
    return new DownloadAnalyticsRequest(
        (DownloadInstallAnalyticsBaseBody<DownloadEventBody>) decorator.decorate(body, accessToken),
        BASE_HOST, action, name, context);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    action = body.getEvent().getAction();
    name = body.getEvent().getName();
    context = body.getEvent().getContext();
    return interfaces.setDownloadAnalyticsEvent(name, action, context, body);
  }

  public enum DataStatus {
    start, pause, resume, stop,
  }

  public enum DataOrigin {
    install, update, downgrade, update_all
  }

  public @Data static class DownloadEventBody extends DownloadInstallAnalyticsBaseBody.Data {
    DataOrigin origin;
    DataStatus status;
  }
}

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.AnalyticsBaseBody;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.utils.AptoideUtils;

/**
 * Created by trinkes on 30/12/2016.
 */

public abstract class AnalyticsBaseRequest<T extends AnalyticsBaseBody>
    extends V7<BaseV7Response, T> {
  protected AnalyticsBaseRequest(T body, String baseHost) {
    super(body, baseHost);
    AnalyticsBaseBody.User user = new AnalyticsBaseBody.User();
    AnalyticsBaseBody.Aptoide aptoide = new AnalyticsBaseBody.Aptoide();
    aptoide.setAptoidePackageName(DataProvider.getConfiguration().getAppId());
    aptoide.setVercode(AptoideUtils.Core.getVerCode());
    user.setAptoide(aptoide);
    body.setUser(user);
  }
}

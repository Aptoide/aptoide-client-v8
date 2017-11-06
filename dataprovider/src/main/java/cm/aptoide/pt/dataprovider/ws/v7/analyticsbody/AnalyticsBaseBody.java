package cm.aptoide.pt.dataprovider.ws.v7.analyticsbody;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;

/**
 * Created by trinkes on 30/12/2016.
 */
public class AnalyticsBaseBody extends BaseBody {

  private final String aptoidePackage;

  public AnalyticsBaseBody(String aptoidePackage) {
    this.aptoidePackage = aptoidePackage;
  }

  public String getAptoidePackage() {
    return aptoidePackage;
  }
}

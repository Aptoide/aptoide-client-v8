package cm.aptoide.pt.dataprovider.ws.v7.analyticsbody;

/**
 * Created by trinkes on 30/12/2016.
 */

public class DownloadInstallAnalyticsBaseBody extends AnalyticsBaseBody {

  private final Data data;

  public DownloadInstallAnalyticsBaseBody(String hostPackageName, Data data) {
    super(hostPackageName);
    this.data = data;
  }

  public Data getData() {
    return data;
  }
}

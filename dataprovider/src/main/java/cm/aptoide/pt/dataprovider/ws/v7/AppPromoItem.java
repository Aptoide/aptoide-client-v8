package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.model.v7.GetApp;

public class AppPromoItem {
  protected final GetApp getApp;

  public AppPromoItem(GetApp getApp) {
    this.getApp = getApp;
  }

  public GetApp getGetApp() {
    return getApp;
  }
}

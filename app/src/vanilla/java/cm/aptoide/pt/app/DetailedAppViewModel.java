package cm.aptoide.pt.app;

import cm.aptoide.pt.view.app.DetailedApp;

/**
 * Created by D01 on 07/05/18.
 */

public class DetailedAppViewModel {

  private DetailedApp detailedApp;

  public DetailedAppViewModel(DetailedApp detailedApp) {
    this.detailedApp = detailedApp;
  }

  public DetailedApp getDetailedApp() {
    return detailedApp;
  }
}

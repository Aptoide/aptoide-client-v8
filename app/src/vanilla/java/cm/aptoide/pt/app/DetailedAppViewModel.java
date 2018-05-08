package cm.aptoide.pt.app;

import cm.aptoide.pt.view.app.DetailedApp;

/**
 * Created by D01 on 07/05/18.
 */

public class DetailedAppViewModel {

  private DetailedApp detailedApp;
  private boolean isSubscribed;

  public DetailedAppViewModel(DetailedApp detailedApp) {
    this.detailedApp = detailedApp;
    isSubscribed = false;
  }

  public DetailedApp getDetailedApp() {
    return detailedApp;
  }

  public boolean isSubscribed() {
    return isSubscribed;
  }

  public void setSubscribed(boolean subscribed) {
    isSubscribed = subscribed;
  }
}

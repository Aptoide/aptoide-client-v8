package cm.aptoide.pt.app;

import cm.aptoide.pt.view.app.DetailedApp;

/**
 * Created by D01 on 07/05/18.
 */

public class DetailedAppViewModel {

  private final boolean isStoreFollowed;
  private DetailedApp detailedApp;

  public DetailedAppViewModel(DetailedApp detailedApp, boolean isStoreFollowed) {
    this.detailedApp = detailedApp;
    this.isStoreFollowed = isStoreFollowed;
  }

  public DetailedApp getDetailedApp() {
    return detailedApp;
  }

  public boolean isStoreFollowed() {
    return isStoreFollowed;
  }
}

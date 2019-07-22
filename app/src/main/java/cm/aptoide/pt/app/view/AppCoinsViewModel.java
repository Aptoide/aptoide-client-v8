package cm.aptoide.pt.app.view;

import cm.aptoide.pt.app.AppCoinsAdvertisingModel;

public class AppCoinsViewModel {
  private final boolean loading;
  private final boolean hasBilling;
  private final AppCoinsAdvertisingModel advertisingModel;

  public AppCoinsViewModel(boolean loading, boolean hasBilling,
      AppCoinsAdvertisingModel advertisingModel) {
    this.loading = loading;
    this.hasBilling = hasBilling;
    this.advertisingModel = advertisingModel;
  }

  public AppCoinsViewModel() {
    this.loading = false;
    this.hasBilling = false;
    this.advertisingModel = new AppCoinsAdvertisingModel();
  }

  public boolean isLoading() {
    return loading;
  }

  public boolean hasBilling() {
    return hasBilling;
  }

  public AppCoinsAdvertisingModel getAdvertisingModel() {
    return advertisingModel;
  }
}

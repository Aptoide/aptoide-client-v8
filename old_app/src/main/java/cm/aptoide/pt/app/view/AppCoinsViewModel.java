package cm.aptoide.pt.app.view;

import cm.aptoide.pt.app.AppCoinsAdvertisingModel;
import cm.aptoide.pt.bonus.BonusAppcModel;

public class AppCoinsViewModel {
  private final boolean loading;
  private final boolean hasBilling;
  private final AppCoinsAdvertisingModel advertisingModel;
  private final BonusAppcModel bonusAppcModel;

  public AppCoinsViewModel(boolean loading, boolean hasBilling,
      AppCoinsAdvertisingModel advertisingModel, BonusAppcModel bonusAppcModel) {
    this.loading = loading;
    this.hasBilling = hasBilling;
    this.advertisingModel = advertisingModel;
    this.bonusAppcModel = bonusAppcModel;
  }

  public AppCoinsViewModel() {
    this.loading = false;
    this.hasBilling = false;
    this.advertisingModel = new AppCoinsAdvertisingModel();
    this.bonusAppcModel = new BonusAppcModel(false, 0);
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

  public BonusAppcModel getBonusAppcModel() {
    return bonusAppcModel;
  }

  public boolean hasAdvertising() {
    if (advertisingModel != null) {
      return advertisingModel.getHasAdvertising();
    }
    return false;
  }
}

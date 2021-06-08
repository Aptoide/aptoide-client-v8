package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.bonus.BonusAppcModel;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;

public class NewAppCoinsAppPromoItem {
  private final GetApp getApp;
  private final BonusAppcModel bonusAppcModel;

  public NewAppCoinsAppPromoItem(GetApp getApp, BonusAppcModel bonusAppcModel) {
    this.getApp = getApp;
    this.bonusAppcModel = bonusAppcModel;
  }

  public GetApp getGetApp() {
    return getApp;
  }

  public BonusAppcModel getBonusAppcModel() {
    return bonusAppcModel;
  }
}

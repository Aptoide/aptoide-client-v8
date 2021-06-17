package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.bonus.BonusAppcModel;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;

public class NewAppCoinsAppPromoItem extends AppPromoItem {
  private final BonusAppcModel bonusAppcModel;

  public NewAppCoinsAppPromoItem(GetApp getApp, BonusAppcModel bonusAppcModel) {
    super(getApp);
    this.bonusAppcModel = bonusAppcModel;
  }

  public BonusAppcModel getBonusAppcModel() {
    return bonusAppcModel;
  }
}

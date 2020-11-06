package cm.aptoide.pt.dataprovider.ws.v7.home;

import cm.aptoide.pt.bonus.BonusAppcModel;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;

public class BonusAppcBundle {
  private final ListApps listApps;
  private final BonusAppcModel bonusAppcModel;

  public BonusAppcBundle(ListApps listApps, BonusAppcModel bonusAppcModel) {
    this.listApps = listApps;
    this.bonusAppcModel = bonusAppcModel;
  }

  public ListApps getListApps() {
    return listApps;
  }

  public BonusAppcModel getBonusAppcModel() {
    return bonusAppcModel;
  }
}

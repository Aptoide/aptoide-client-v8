package cm.aptoide.pt.promotions;

import java.util.List;

public class PromotionsModel {

  private List<PromotionApp> appsList;
  private int totalAppcValue;

  public PromotionsModel(List<PromotionApp> appsList, int totalAppcValue) {
    this.appsList = appsList;
    this.totalAppcValue = totalAppcValue;
  }

  public List<PromotionApp> getAppsList() {
    return appsList;
  }

  public int getTotalAppcValue() {
    return totalAppcValue;
  }
}

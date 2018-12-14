package cm.aptoide.pt.promotions;

import java.util.List;

public class PromotionsModel {

  private List<PromotionApp> appsList;
  private int totalAppcValue;
  private boolean isWalletInstalled;

  public PromotionsModel(List<PromotionApp> appsList, int totalAppcValue,
      boolean isWalletInstalled) {
    this.appsList = appsList;
    this.totalAppcValue = totalAppcValue;
    this.isWalletInstalled = isWalletInstalled;
  }

  public List<PromotionApp> getAppsList() {
    return appsList;
  }

  public int getTotalAppcValue() {
    return totalAppcValue;
  }

  public boolean isWalletInstalled() {
    return isWalletInstalled;
  }
}

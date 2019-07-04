package cm.aptoide.pt.promotions;

import java.util.Collections;
import java.util.List;

public class PromotionsModel {

  private final List<PromotionApp> appsList;
  private final int totalAppcValue;
  private final String title;
  private final String featureGraphic;
  private final boolean isWalletInstalled;
  private final boolean isError;

  public PromotionsModel(List<PromotionApp> appsList, int totalAppcValue, String title,
      String featureGraphic, boolean isWalletInstalled, boolean isError) {
    this.appsList = appsList;
    this.totalAppcValue = totalAppcValue;
    this.title = title;
    this.featureGraphic = featureGraphic;
    this.isWalletInstalled = isWalletInstalled;
    this.isError = isError;
  }

  public static PromotionsModel ofError() {
    return new PromotionsModel(Collections.emptyList(), -1, "", "", false, true);
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

  public String getTitle() {
    return title;
  }

  public String getFeatureGraphic() {
    return featureGraphic;
  }

  public boolean isError() {
    return isError;
  }
}

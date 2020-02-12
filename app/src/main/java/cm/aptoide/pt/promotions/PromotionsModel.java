package cm.aptoide.pt.promotions;

import java.util.Collections;
import java.util.List;

public class PromotionsModel {

  private final String promotionId;
  private final List<PromotionApp> appsList;
  private final String title;
  private final String featureGraphic;
  private final boolean isWalletInstalled;
  private final boolean isError;
  private final String description;
  private final String dialogDescription;

  public PromotionsModel(String promotionId, List<PromotionApp> appsList, String title,
      String featureGraphic, boolean isWalletInstalled, boolean isError, String description,
      String dialogDescription) {
    this.promotionId = promotionId;
    this.appsList = appsList;
    this.title = title;
    this.featureGraphic = featureGraphic;
    this.isWalletInstalled = isWalletInstalled;
    this.isError = isError;
    this.description = description;
    this.dialogDescription = dialogDescription;
  }

  public static PromotionsModel ofError() {
    return new PromotionsModel("n/a", Collections.emptyList(), "", "", false, true, "", "");
  }

  public List<PromotionApp> getAppsList() {
    return appsList;
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

  public String getPromotionId() {
    return promotionId;
  }

  public String getDescription() {
    return description;
  }

  public String getDialogDescription() {
    return dialogDescription;
  }
}

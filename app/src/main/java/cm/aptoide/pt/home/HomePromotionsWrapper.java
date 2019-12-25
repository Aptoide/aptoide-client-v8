package cm.aptoide.pt.home;

public class HomePromotionsWrapper {

  private final String title;
  private final String featureGraphic;
  private final boolean hasPromotions;
  private final int promotions;
  private final float totalUnclaimedAppcValue;
  private final boolean showDialog;
  private final String description;

  public HomePromotionsWrapper(String title, String featureGraphic, boolean hasPromotions,
      int promotions, float totalUnclaimedAppcValue, boolean showDialog, String description) {
    this.title = title;
    this.featureGraphic = featureGraphic;
    this.hasPromotions = hasPromotions;
    this.promotions = promotions;
    this.totalUnclaimedAppcValue = totalUnclaimedAppcValue;
    this.showDialog = showDialog;
    this.description = description;
  }

  public boolean hasPromotions() {
    return hasPromotions;
  }

  public int getPromotions() {
    return promotions;
  }

  public float getTotalUnclaimedAppcValue() {
    return totalUnclaimedAppcValue;
  }

  public boolean shouldShowDialog() {
    return showDialog;
  }

  public String getTitle() {
    return title;
  }

  public String getFeatureGraphic() {
    return featureGraphic;
  }

  public String getDescription() {
    return description;
  }
}

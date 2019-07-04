package cm.aptoide.pt.home;

public class HomePromotionsWrapper {

  private final String title;
  private final String featureGraphic;
  private final boolean hasPromotions;
  private final int promotions;
  private final float totalUnclaimedAppcValue;
  private final boolean showDialog;
  private final float totalAppcValue;

  public HomePromotionsWrapper(String title, String featureGraphic, boolean hasPromotions,
      int promotions, float totalUnclaimedAppcValue, boolean showDialog, float totalAppcValue) {
    this.title = title;
    this.featureGraphic = featureGraphic;
    this.hasPromotions = hasPromotions;
    this.promotions = promotions;
    this.totalUnclaimedAppcValue = totalUnclaimedAppcValue;
    this.showDialog = showDialog;
    this.totalAppcValue = totalAppcValue;
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

  public float getTotalAppcValue() {
    return totalAppcValue;
  }

  public String getTitle() {
    return title;
  }

  public String getFeatureGraphic() {
    return featureGraphic;
  }
}

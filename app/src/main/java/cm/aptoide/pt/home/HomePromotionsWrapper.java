package cm.aptoide.pt.home;

public class HomePromotionsWrapper {

  private boolean hasPromotions;
  private int promotions;
  private float totalUnclaimedAppcValue;
  private boolean showDialog;
  private float totalAppcValue;

  public HomePromotionsWrapper(boolean hasPromotions, int promotions, float totalUnclaimedAppcValue,
      boolean showDialog, float totalAppcValue) {
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
}

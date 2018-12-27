package cm.aptoide.pt.home;

public class HomePromotionsWrapper {

  private boolean hasPromotions;
  private int promotions;
  private float totalUnclaimedAppcValue;
  private boolean showDialog;

  public HomePromotionsWrapper(boolean hasPromotions, int promotions, float totalUnclaimedAppcValue,
      boolean showDialog) {
    this.hasPromotions = hasPromotions;
    this.promotions = promotions;
    this.totalUnclaimedAppcValue = totalUnclaimedAppcValue;
    this.showDialog = showDialog;
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
}

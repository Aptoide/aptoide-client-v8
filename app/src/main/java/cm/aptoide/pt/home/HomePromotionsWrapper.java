package cm.aptoide.pt.home;

public class HomePromotionsWrapper {

  private boolean hasPromotions;
  private int promotions;
  private float totalAppcValue;
  private boolean showDialog;

  public HomePromotionsWrapper(boolean hasPromotions, int promotions, float totalAppcValue,
      boolean showDialog) {
    this.hasPromotions = hasPromotions;
    this.promotions = promotions;
    this.totalAppcValue = totalAppcValue;
    this.showDialog = showDialog;
  }

  public boolean hasPromotions() {
    return hasPromotions;
  }

  public int getPromotions() {
    return promotions;
  }

  public float getTotalAppcValue() {
    return totalAppcValue;
  }

  public boolean shouldShowDialog() {
    return showDialog;
  }
}

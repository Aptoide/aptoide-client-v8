package cm.aptoide.pt.home;

public class HomePromotionsWrapper {

  private boolean hasPromotions;
  private int promotions;
  private int totalAppcValue;

  public HomePromotionsWrapper(boolean hasPromotions, int promotions, int totalAppcValue) {
    this.hasPromotions = hasPromotions;
    this.promotions = promotions;
    this.totalAppcValue = totalAppcValue;
  }

  public boolean hasPromotions() {
    return hasPromotions;
  }

  public int getPromotions() {
    return promotions;
  }

  public int getTotalAppcValue() {
    return totalAppcValue;
  }
}

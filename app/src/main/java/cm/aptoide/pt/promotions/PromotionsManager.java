package cm.aptoide.pt.promotions;

import java.util.List;
import rx.Single;

public class PromotionsManager {

  private final PromotionsService promotionsService;

  public PromotionsManager(PromotionsService promotionsService) {
    this.promotionsService = promotionsService;
  }

  public Single<List<PromotionApp>> getPromotionApps() {
    return promotionsService.getPromotionApps();
  }

  public Single<ClaimStatusWrapper> claimPromotion(String walletAddress, String packageName,
      String captcha) {
    return promotionsService.claimPromotion(walletAddress, packageName, captcha);
  }

  public void saveWalletAddress(String walletAddress) {
    promotionsService.saveWalletAddress(walletAddress);
  }

  public String getWalletAddress() {
    return promotionsService.getWalletAddress();
  }
}
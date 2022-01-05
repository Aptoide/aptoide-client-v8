package cm.aptoide.pt.promotions;

public class ClaimPromotionsClickWrapper extends ClaimPromotionsWrapper {
  private final String walletAddress;

  public ClaimPromotionsClickWrapper(String walletAddress, String packageName) {
    super(packageName);
    this.walletAddress = walletAddress;
  }

  public String getWalletAddress() {
    return walletAddress;
  }
}

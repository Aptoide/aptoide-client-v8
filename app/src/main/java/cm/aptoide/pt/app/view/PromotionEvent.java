package cm.aptoide.pt.app.view;

import cm.aptoide.pt.promotions.Promotion;
import cm.aptoide.pt.promotions.WalletApp;

public class PromotionEvent {

  private WalletApp walletApp;
  private PromotionEvent.ClickType clickType;
  private Promotion promotion;

  public PromotionEvent(Promotion promotion, WalletApp walletApp,
      PromotionEvent.ClickType clickType) {
    this.walletApp = walletApp;
    this.clickType = clickType;
    this.promotion = promotion;
  }

  public WalletApp getApp() {
    return walletApp;
  }

  public Promotion getPromotion() {
    return promotion;
  }

  public PromotionEvent.ClickType getClickType() {
    return clickType;
  }

  enum ClickType {
    PAUSE_DOWNLOAD, CANCEL_DOWNLOAD, RESUME_DOWNLOAD, INSTALL_APP, DOWNLOAD, RETRY_DOWNLOAD, CLAIM, UPDATE, DOWNGRADE
  }
}

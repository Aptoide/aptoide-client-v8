package cm.aptoide.pt.app.view;

import cm.aptoide.pt.promotions.WalletApp;

public class EskillsPromotionEvent {

  private WalletApp walletApp;
  private EskillsPromotionEvent.ClickType clickType;

  public EskillsPromotionEvent(WalletApp walletApp,
      EskillsPromotionEvent.ClickType clickType) {
    this.walletApp = walletApp;
    this.clickType = clickType;
  }

  public WalletApp getWallet() {
    return walletApp;
  }

  public EskillsPromotionEvent.ClickType getClickType() {
    return clickType;
  }

  enum ClickType {
    PAUSE_DOWNLOAD, CANCEL_DOWNLOAD, RESUME_DOWNLOAD,
  }
}

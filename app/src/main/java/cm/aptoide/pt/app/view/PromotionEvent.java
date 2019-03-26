package cm.aptoide.pt.app.view;

import cm.aptoide.pt.app.WalletPromotionViewModel;

public class PromotionEvent {

  private WalletPromotionViewModel app;
  private PromotionEvent.ClickType clickType;

  public PromotionEvent(WalletPromotionViewModel app, PromotionEvent.ClickType clickType) {
    this.app = app;
    this.clickType = clickType;
  }

  public WalletPromotionViewModel getApp() {
    return app;
  }

  public PromotionEvent.ClickType getClickType() {
    return clickType;
  }

  enum ClickType {
    PAUSE_DOWNLOAD, CANCEL_DOWNLOAD, RESUME_DOWNLOAD, INSTALL_APP, DOWNLOAD, RETRY_DOWNLOAD, CLAIM, UPDATE, DOWNGRADE
  }
}

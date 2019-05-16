package cm.aptoide.pt.app.view;

import cm.aptoide.pt.app.PromotionViewModel;

public class PromotionEvent {

  private PromotionViewModel app;
  private PromotionEvent.ClickType clickType;

  public PromotionEvent(PromotionViewModel app, PromotionEvent.ClickType clickType) {
    this.app = app;
    this.clickType = clickType;
  }

  public PromotionViewModel getApp() {
    return app;
  }

  public PromotionEvent.ClickType getClickType() {
    return clickType;
  }

  enum ClickType {
    PAUSE_DOWNLOAD, CANCEL_DOWNLOAD, RESUME_DOWNLOAD, INSTALL_APP, DOWNLOAD, RETRY_DOWNLOAD, CLAIM, UPDATE, DOWNGRADE
  }
}

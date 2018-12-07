package cm.aptoide.pt.promotions;

public class PromotionAppClick {

  private PromotionApp app;
  private ClickType clickType;

  public PromotionAppClick(PromotionApp app, ClickType clickType) {
    this.app = app;
    this.clickType = clickType;
  }

  public PromotionApp getApp() {
    return app;
  }

  public ClickType getClickType() {
    return clickType;
  }

  enum ClickType {
    PAUSE_DOWNLOAD, CANCEL_DOWNLOAD, RESUME_DOWNLOAD, INSTALL_APP, DOWNLOAD, RETRY_DOWNLOAD, CLAIM
  }
}

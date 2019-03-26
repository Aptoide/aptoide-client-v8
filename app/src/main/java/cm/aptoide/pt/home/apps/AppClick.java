package cm.aptoide.pt.home.apps;

/**
 * Created by filipegoncalves on 3/15/18.
 */

public class AppClick {

  private App app;
  private ClickType clickType;

  public AppClick(App app, ClickType clickType) {
    this.app = app;
    this.clickType = clickType;
  }

  public App getApp() {
    return app;
  }

  public ClickType getClickType() {
    return clickType;
  }

  enum ClickType {
    PAUSE_DOWNLOAD, CANCEL_DOWNLOAD, RESUME_DOWNLOAD, INSTALL_APP, RETRY_DOWNLOAD, UPDATE_ALL_APPS, UPDATE_APP, PAUSE_UPDATE, CANCEL_UPDATE, RESUME_UPDATE, UPDATE_CARD_LONG_CLICK, UPDATE_CARD_CLICK, RETRY_UPDATE, APPC_UPGRADE_APP, APPC_UPGRADE_RESUME, APPC_UPGRADE_CANCEL, APPC_UPGRADE_PAUSE, APPC_UPGRADE_RETRY
  }
}

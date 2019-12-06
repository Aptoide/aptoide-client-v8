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

  public enum ClickType {
    // Generic
    CARD_CLICK, CARD_LONG_CLICK,

    // Action clicks
    DOWNLOAD_ACTION_CLICK, APPC_ACTION_CLICK,

    // Downloads
    PAUSE_CLICK, CANCEL_CLICK, RESUME_CLICK, INSTALL_CLICK,

    // Deprecated SeeMoreAppcFragment events
    APPC_UPGRADE_APP, APPC_UPGRADE_RESUME, APPC_UPGRADE_RETRY, APPC_UPGRADE_CANCEL, APPC_UPGRADE_PAUSE

  }
}

package cm.aptoide.pt.home.apps;

public class AppClickEventWrapper {

  private boolean isAppcUpgrade;
  private App app;

  public AppClickEventWrapper(boolean isAppcUpgrade, App app) {
    this.isAppcUpgrade = isAppcUpgrade;
    this.app = app;
  }

  public boolean isAppcUpgrade() {
    return isAppcUpgrade;
  }

  public App getApp() {
    return app;
  }
}

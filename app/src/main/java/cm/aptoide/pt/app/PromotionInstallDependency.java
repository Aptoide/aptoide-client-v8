package cm.aptoide.pt.app;

import cm.aptoide.pt.install.Install;

class PromotionInstallDependency {
  private final Install install;
  private final boolean isAppInstalled;

  public PromotionInstallDependency(Install install, boolean isAppInstalled) {
    this.install = install;
    this.isAppInstalled = isAppInstalled;
  }

  public Install getInstall() {
    return install;
  }

  public boolean isAppInstalled() {
    return isAppInstalled;
  }
}

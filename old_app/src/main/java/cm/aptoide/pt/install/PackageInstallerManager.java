package cm.aptoide.pt.install;

public class PackageInstallerManager {
  private final boolean isDeviceMiui;
  private final boolean isMIUIWithAABFix;

  public PackageInstallerManager(boolean isDeviceMiui, boolean isMIUIWithAABFix) {
    this.isDeviceMiui = isDeviceMiui;
    this.isMIUIWithAABFix = isMIUIWithAABFix;
  }

  boolean shouldSetInstallerPackageName() {
    return !isDeviceMiui || (isDeviceMiui && isMIUIWithAABFix);
  }
}

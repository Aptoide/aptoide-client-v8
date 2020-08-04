package cm.aptoide.pt.install;

import cm.aptoide.pt.database.room.RoomDownload;

public class PackageInstallerManager {
  private boolean isDeviceMiui;
  private boolean isMIUIWithAABFix;

  public PackageInstallerManager(boolean isDeviceMiui, boolean isMIUIWithAABFix) {
    this.isDeviceMiui = isDeviceMiui;
    this.isMIUIWithAABFix = isMIUIWithAABFix;
  }

  boolean shouldSetInstallerPackageName() {
    return !isDeviceMiui || (isDeviceMiui && isMIUIWithAABFix);
  }
}

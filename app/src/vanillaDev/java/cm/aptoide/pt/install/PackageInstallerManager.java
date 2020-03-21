package cm.aptoide.pt.install;

import cm.aptoide.pt.database.room.RoomDownload;

public class PackageInstallerManager {
  boolean shouldSetInstallerPackageName(RoomDownload download) {
    return download.hasAppc();
  }
}

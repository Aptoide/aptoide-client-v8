package cm.aptoide.pt.download;

public class DownloadApkPathsProvider {

  private static final String UPDATE_ACTION = "?action=update";
  private static final String INSTALL_ACTION = "?action=install";
  private static final String DOWNGRADE_ACTION = "?action=downgrade";

  public ApkPaths getDownloadPaths(int downloadAction, String path, String altPath) {
    switch (downloadAction) {
      case Download.ACTION_INSTALL:
        path += INSTALL_ACTION;
        altPath += INSTALL_ACTION;
        break;
      case Download.ACTION_DOWNGRADE:
        path += DOWNGRADE_ACTION;
        altPath += DOWNGRADE_ACTION;
        break;
      case Download.ACTION_UPDATE:
        path += UPDATE_ACTION;
        altPath += UPDATE_ACTION;
        break;
    }
    return new ApkPaths(path, altPath);
  }
}

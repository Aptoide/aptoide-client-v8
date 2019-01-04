package cm.aptoide.pt.download;

import cm.aptoide.pt.database.realm.Download;

public class DownloadApkPathsProvider {

  private static final String UPDATE_ACTION = "?action=update";
  private static final String INSTALL_ACTION = "?action=install";
  private static final String DOWNGRADE_ACTION = "?action=downgrade";
  private static final String OEMID_QUERY = "&oemid=";
  private final OemidProvider oemidProvider;

  public DownloadApkPathsProvider(OemidProvider oemidProvider) {
    this.oemidProvider = oemidProvider;
  }

  public ApkPaths getDownloadPaths(int downloadAction, String path, String altPath) {
    String oemid = getOemidQueryString();
    switch (downloadAction) {
      case Download.ACTION_INSTALL:
        path += INSTALL_ACTION + oemid;
        altPath += INSTALL_ACTION + oemid;
        break;
      case Download.ACTION_DOWNGRADE:
        path += DOWNGRADE_ACTION + oemid;
        altPath += DOWNGRADE_ACTION + oemid;
        break;
      case Download.ACTION_UPDATE:
        path += UPDATE_ACTION + oemid;
        altPath += UPDATE_ACTION + oemid;
        break;
    }
    return new ApkPaths(path, altPath);
  }

  private String getOemidQueryString() {
    String oemid = oemidProvider.getOemid();
    if (oemid.isEmpty()) {
      return "";
    } else {
      return OEMID_QUERY + oemid;
    }
  }
}

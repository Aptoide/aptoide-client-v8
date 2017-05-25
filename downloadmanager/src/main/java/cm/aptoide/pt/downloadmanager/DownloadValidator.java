package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;

/**
 * Created by trinkes on 25/05/2017.
 */

class DownloadValidator {
  public boolean isFinish(Download download) {
    for (FileToDownload fileToDownload : download.getFilesToDownload()) {
      if (fileToDownload.getStatus() != Download.COMPLETED) {
        return false;
      }
    }
    return true;
  }

  public boolean isMd5Valid(Download download) {
    return true;
  }
}

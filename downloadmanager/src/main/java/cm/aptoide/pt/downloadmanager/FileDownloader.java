package cm.aptoide.pt.downloadmanager;

import rx.Completable;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public interface FileDownloader {
  Completable startFileDownload(String mainDownloadPath, int fileType, String packageName,
      int versionCode, String fileName);

  Completable pauseDownload(DownloadAppFile downloadAppFile);

  Completable removeDownloadFile(DownloadAppFile downloadAppFile);
}

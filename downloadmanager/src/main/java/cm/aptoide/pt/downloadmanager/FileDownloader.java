package cm.aptoide.pt.downloadmanager;

import rx.Completable;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public interface FileDownloader {
  Completable startFileDownload(DownloadAppFile downloadAppFile);

  Completable pauseDownload(DownloadAppFile downloadAppFile);

  void pauseAllDownloads();

  Completable removeDownloadFile(DownloadAppFile downloadAppFile);
}

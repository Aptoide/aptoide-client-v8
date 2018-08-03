package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.Constants;
import cm.aptoide.pt.downloadmanager.DownloadAppFile;
import cm.aptoide.pt.downloadmanager.FileDownloader;
import com.liulishuo.filedownloader.BaseDownloadTask;
import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public class FileDownloadManager implements FileDownloader {

  private com.liulishuo.filedownloader.FileDownloader fileDownloader;
  private FileDownloadTask fileDownloadTask;

  public FileDownloadManager(com.liulishuo.filedownloader.FileDownloader fileDownloader,
      FileDownloadTask fileDownloadTask) {
    this.fileDownloader = fileDownloader;
    this.fileDownloadTask = fileDownloadTask;
  }

  @Override public Completable startFileDownload(DownloadAppFile downloadAppFile) {
    return Completable.fromCallable(() -> {
      if (downloadAppFile.getMainDownloadPath() == null || downloadAppFile.getMainDownloadPath()
          .isEmpty()) {
        throw new IllegalArgumentException("The url for the download can not be empty");
      } else {
        return fileDownloader.start(fileDownloadTask, false);
      }
    });
  }

  @Override public Completable pauseDownload(DownloadAppFile downloadAppFile) {
    return null;
  }

  @Override public Completable removeDownloadFile(DownloadAppFile downloadAppFile) {
    return null;
  }


}

package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.FileDownloader;
import cm.aptoide.pt.downloadmanager.FileDownloaderProvider;

/**
 * Created by filipegoncalves on 8/2/18.
 */

public class FileDownloadManagerProvider implements FileDownloaderProvider {

  @Override public FileDownloader getFileDownloader() {
    return new FileDownloadManager(com.liulishuo.filedownloader.FileDownloader.getImpl(),
        new FileDownloadTask());
  }

  private FileDownloadTask createFileDownloadTask() {
    FileDownloadTask fileDownloadTask = new FileDownloadTask();
    return fileDownloadTask;
  }
}

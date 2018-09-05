package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.FileDownloadCallback;
import cm.aptoide.pt.downloadmanager.FileDownloader;
import cm.aptoide.pt.downloadmanager.FileDownloaderProvider;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 8/2/18.
 */

public class FileDownloadManagerProvider implements FileDownloaderProvider {

  private String downloadsPath;

  public FileDownloadManagerProvider(String downloadsPath) {
    this.downloadsPath = downloadsPath;
  }

  @Override public FileDownloader createFileDownloader(String mainDownloadPath, int fileType,
      String packageName, int versionCode, String fileName,
      PublishSubject<FileDownloadCallback> downloadStatusCallback) {
    return new FileDownloadManager(com.liulishuo.filedownloader.FileDownloader.getImpl(),
        new FileDownloadTask(downloadStatusCallback, fileType), downloadsPath, mainDownloadPath, fileType,
        packageName, versionCode, fileName);
  }
}


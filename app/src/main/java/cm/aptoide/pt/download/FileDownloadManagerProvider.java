package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.FileDownloader;
import cm.aptoide.pt.downloadmanager.FileDownloaderProvider;

/**
 * Created by filipegoncalves on 8/2/18.
 */

public class FileDownloadManagerProvider implements FileDownloaderProvider {

  private String downloadsPath;

  public FileDownloadManagerProvider(String downloadsPath) {
    this.downloadsPath = downloadsPath;
  }

  private FileDownloadTask createFileDownloadTask() {
    FileDownloadTask fileDownloadTask = new FileDownloadTask();
    return fileDownloadTask;
  }

  @Override public FileDownloader createFileDownloader(String mainDownloadPath, int fileType,
      String packageName, int versionCode, String fileName) {
    return new FileDownloadManager(com.liulishuo.filedownloader.FileDownloader.getImpl(),
        new FileDownloadTask(), downloadsPath, mainDownloadPath, fileType, packageName, versionCode,
        fileName);
  }
}


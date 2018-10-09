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
  private com.liulishuo.filedownloader.FileDownloader fileDownloader;
  private Md5Comparator md5Comparator;

  public FileDownloadManagerProvider(String downloadsPath,
      com.liulishuo.filedownloader.FileDownloader fileDownloader, Md5Comparator md5Comparator) {
    this.downloadsPath = downloadsPath;
    this.fileDownloader = fileDownloader;
    this.md5Comparator = md5Comparator;
  }

  @Override
  public FileDownloader createFileDownloader(String md5, String mainDownloadPath, int fileType,
      String packageName, int versionCode, String fileName,
      PublishSubject<FileDownloadCallback> downloadStatusCallback) {
    return new FileDownloadManager(fileDownloader,
        new FileDownloadTask(downloadStatusCallback, md5, md5Comparator, fileName), downloadsPath,
        mainDownloadPath, fileType, packageName, versionCode, fileName);
  }
}


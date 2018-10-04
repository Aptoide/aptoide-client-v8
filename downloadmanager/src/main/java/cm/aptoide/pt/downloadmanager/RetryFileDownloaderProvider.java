package cm.aptoide.pt.downloadmanager;

import rx.subjects.PublishSubject;

public class RetryFileDownloaderProvider implements RetryFileDownloader {

  private final String downloadsPath;
  private FileDownloaderProvider fileDownloaderProvider;

  public RetryFileDownloaderProvider(String downloadsPath,
      FileDownloaderProvider fileDownloaderProvider) {
    this.downloadsPath = downloadsPath;
    this.fileDownloaderProvider = fileDownloaderProvider;
  }

  @Override
  public FileDownloader createRetryFileDownloader(String md5, String mainDownloadPath, int fileType,
      String packageName, int versionCode, String fileName,
      PublishSubject<FileDownloadCallback> fileDownloadCallback, String alternativeDownloadPath) {
    return new RetryFileDownloadManager(mainDownloadPath, fileType, packageName, versionCode,
        fileName, md5, downloadsPath, fileDownloaderProvider, alternativeDownloadPath);
  }
}

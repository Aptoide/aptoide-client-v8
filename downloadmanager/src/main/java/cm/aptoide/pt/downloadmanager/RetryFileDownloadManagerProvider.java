package cm.aptoide.pt.downloadmanager;

import rx.subjects.PublishSubject;

public class RetryFileDownloadManagerProvider implements RetryFileDownloaderProvider {

  private FileDownloaderProvider fileDownloaderProvider;

  public RetryFileDownloadManagerProvider(FileDownloaderProvider fileDownloaderProvider) {
    this.fileDownloaderProvider = fileDownloaderProvider;
  }

  @Override
  public RetryFileDownloader createRetryFileDownloader(String md5, String mainDownloadPath,
      int fileType, String packageName, int versionCode, String fileName,
      PublishSubject<FileDownloadCallback> fileDownloadCallback, String alternativeDownloadPath) {
    return new RetryFileDownloadManager(mainDownloadPath, fileType, packageName, versionCode,
        fileName, md5, fileDownloaderProvider, alternativeDownloadPath);
  }
}

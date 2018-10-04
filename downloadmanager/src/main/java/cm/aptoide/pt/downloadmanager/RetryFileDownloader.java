package cm.aptoide.pt.downloadmanager;

import rx.subjects.PublishSubject;

public interface RetryFileDownloader {

  FileDownloader createRetryFileDownloader(String md5, String mainDownloadPath, int fileType,
      String packageName, int versionCode, String fileName,
      PublishSubject<FileDownloadCallback> fileDownloadCallback, String alternativeDownloadPath);
}

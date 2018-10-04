package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.logger.Logger;
import rx.Completable;
import rx.Observable;
import rx.subjects.PublishSubject;

public class RetryFileDownloadManager implements FileDownloader {

  private final String mainDownloadPath;
  private final int fileType;
  private final String packageName;
  private final int versionCode;
  private final String fileName;
  private String md5;
  private String downloadsPath;
  private FileDownloaderProvider fileDownloaderProvider;
  private String alternativeDownloadPath;
  private FileDownloader fileDownloader;
  private PublishSubject<FileDownloadCallback> retryFileDownloadSubject;

  public RetryFileDownloadManager(String mainDownloadPath, int fileType, String packageName,
      int versionCode, String fileName, String md5, String downloadsPath,
      FileDownloaderProvider fileDownloaderProvider, String alternativeDownloadPath) {
    this.mainDownloadPath = mainDownloadPath;
    this.fileType = fileType;
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.fileName = fileName;
    this.md5 = md5;
    this.downloadsPath = downloadsPath;
    this.fileDownloaderProvider = fileDownloaderProvider;
    this.alternativeDownloadPath = alternativeDownloadPath;
    retryFileDownloadSubject = PublishSubject.create();
  }

  @Override public Completable startFileDownload() {
    return Observable.just(setupFileDownloader())
        .doOnNext(__ -> Logger.getInstance()
            .d("RetryFileDownloader", "Starting app file download " + fileName))
        .flatMap(fileDownloader -> fileDownloader.startFileDownload()
            .andThen(handleFileDownloadProgress(fileDownloader)))
        .toCompletable();
  }

  @Override public Completable pauseDownload() {
    return fileDownloader.pauseDownload();
  }

  @Override public Completable removeDownloadFile() {
    return fileDownloader.removeDownloadFile();
  }

  @Override public Observable<FileDownloadCallback> observeFileDownloadProgress() {
    return retryFileDownloadSubject;
  }

  private Observable<FileDownloadCallback> handleFileDownloadProgress(
      FileDownloader fileDownloader) {
    return fileDownloader.observeFileDownloadProgress()
        .takeUntil(fileDownloadCallback -> fileDownloadCallback.getDownloadState()
            == AppDownloadStatus.AppDownloadState.ERROR_FILE_NOT_FOUND)
        .flatMap(fileDownloadCallback -> {
          if (fileDownloadCallback.getDownloadState()
              == AppDownloadStatus.AppDownloadState.ERROR_FILE_NOT_FOUND) {

            //create new filedownloader
            FileDownloader retryFileDownloader =
                fileDownloaderProvider.createFileDownloader(md5, alternativeDownloadPath, fileType,
                    packageName, versionCode, fileName, PublishSubject.create());
            this.fileDownloader = retryFileDownloader;
            return retryFileDownloader.startFileDownload()
                .andThen(handleFileDownloadProgress(retryFileDownloader));
          } else {
            return Observable.just(fileDownloadCallback);
          }
        })
        .doOnNext(fileDownloadCallback -> retryFileDownloadSubject.onNext(fileDownloadCallback));
  }

  private FileDownloader setupFileDownloader() {
    this.fileDownloader =
        fileDownloaderProvider.createFileDownloader(md5, mainDownloadPath, fileType, packageName,
            versionCode, fileName, PublishSubject.create());
    return fileDownloader;
  }
}

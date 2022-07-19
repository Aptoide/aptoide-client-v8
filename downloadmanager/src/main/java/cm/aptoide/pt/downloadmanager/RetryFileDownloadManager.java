package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.logger.Logger;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

public class RetryFileDownloadManager implements RetryFileDownloader {

  private static final String TAG = "RetryFileDownloadManage";
  private final String mainDownloadPath;
  private final int fileType;
  private final String packageName;
  private final int versionCode;
  private final String fileName;
  private final String attributionId;
  private final String md5;
  private final FileDownloaderProvider fileDownloaderProvider;
  private final String alternativeDownloadPath;
  private final PublishSubject<FileDownloadCallback> retryFileDownloadSubject;
  private final CompositeDisposable startDownloadSubscription;
  private FileDownloader fileDownloader;
  private boolean retried;

  public RetryFileDownloadManager(String mainDownloadPath, int fileType, String packageName,
      int versionCode, String fileName, String md5, FileDownloaderProvider fileDownloaderProvider,
      String alternativeDownloadPath, String attributionId) {
    this.mainDownloadPath = mainDownloadPath;
    this.fileType = fileType;
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.fileName = fileName;
    this.md5 = md5;
    this.fileDownloaderProvider = fileDownloaderProvider;
    this.alternativeDownloadPath = alternativeDownloadPath;
    this.attributionId = attributionId;
    this.retryFileDownloadSubject = PublishSubject.create();
    this.startDownloadSubscription = new CompositeDisposable();
  }

  @Override public void startFileDownload() {
    startDownloadSubscription.add(Observable.just(setupFileDownloader())
        .flatMap(fileDownloader -> fileDownloader.startFileDownload()
            .andThen(handleFileDownloadProgress(fileDownloader)))
        .subscribe());
  }

  @Override public Completable removeDownloadFile() {
    return fileDownloader.removeDownloadFile();
  }

  @Override public Observable<FileDownloadCallback> observeFileDownloadProgress() {
    return retryFileDownloadSubject;
  }

  @Override public void stop() {
    startDownloadSubscription.clear();
  }

  @Override public void stopFailedDownload() {
    fileDownloader.stopFailedDownload();
  }

  private Observable<FileDownloadCallback> handleFileDownloadProgress(
      FileDownloader fileDownloader) {
    return fileDownloader.observeFileDownloadProgress()
        .takeUntil(fileDownloadCallback -> fileDownloadCallback.getDownloadState()
            == AppDownloadStatus.AppDownloadState.ERROR_FILE_NOT_FOUND)
        .flatMap(fileDownloadCallback -> {
          if (fileDownloadCallback.getDownloadState()
              == AppDownloadStatus.AppDownloadState.ERROR_FILE_NOT_FOUND && !retried) {
            Logger.getInstance()
                .d(TAG, "File not found error, restarting the download with the alternative link");
            FileDownloader retryFileDownloader =
                fileDownloaderProvider.createFileDownloader(md5, alternativeDownloadPath, fileType,
                    packageName, versionCode, fileName, PublishSubject.create(), attributionId);
            retried = true;
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
            versionCode, fileName, PublishSubject.create(), attributionId);
    return fileDownloader;
  }
}

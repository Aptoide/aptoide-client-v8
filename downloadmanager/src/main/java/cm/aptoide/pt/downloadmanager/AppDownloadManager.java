package cm.aptoide.pt.downloadmanager;

import androidx.annotation.VisibleForTesting;
import cm.aptoide.pt.logger.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import rx.Completable;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class AppDownloadManager implements AppDownloader {

  private static final String TAG = "AppDownloadManager";
  private final DownloadApp app;
  private RetryFileDownloaderProvider fileDownloaderProvider;
  private HashMap<String, RetryFileDownloader> fileDownloaderPersistence;
  private PublishSubject<FileDownloadCallback> fileDownloadSubject;
  private AppDownloadStatus appDownloadStatus;
  private Subscription subscribe;
  private DownloadAnalytics downloadAnalytics;

  public AppDownloadManager(RetryFileDownloaderProvider fileDownloaderProvider, DownloadApp app,
      HashMap<String, RetryFileDownloader> fileDownloaderPersistence,
      DownloadAnalytics downloadAnalytics) {
    this.fileDownloaderProvider = fileDownloaderProvider;
    this.app = app;
    this.fileDownloaderPersistence = fileDownloaderPersistence;
    this.downloadAnalytics = downloadAnalytics;
    fileDownloadSubject = PublishSubject.create();
    appDownloadStatus = new AppDownloadStatus(app.getMd5(), new ArrayList<>(),
        AppDownloadStatus.AppDownloadState.PENDING, app.getSize());
  }

  @Override public void startAppDownload() {
    subscribe = Observable.from(app.getDownloadFiles())
        .flatMap(downloadAppFile -> startFileDownload(downloadAppFile))
        .subscribe(__ -> {
        }, Throwable::printStackTrace);
  }

  @Override public Completable pauseAppDownload() {
    return Observable.from(app.getDownloadFiles())
        .flatMap(downloadAppFile -> getFileDownloader(downloadAppFile.getMainDownloadPath()))
        .filter(retryFileDownloader -> retryFileDownloader != null)
        .flatMapCompletable(fileDownloader -> fileDownloader.pauseDownload()
            .onErrorComplete())
        .toCompletable();
  }

  @Override public Completable removeAppDownload() {
    return Observable.from(app.getDownloadFiles())
        .flatMap(downloadAppFile -> getFileDownloader(downloadAppFile.getMainDownloadPath()))
        .flatMapCompletable(fileDownloader -> fileDownloader.removeDownloadFile()
            .onErrorComplete())
        .toCompletable();
  }

  @Override public Observable<AppDownloadStatus> observeDownloadProgress() {
    return observeFileDownload().flatMap(fileDownloadCallback -> {
      setAppDownloadStatus(fileDownloadCallback);
      return Observable.just(appDownloadStatus);
    })
        .doOnError(throwable -> throwable.printStackTrace())
        .map(__ -> appDownloadStatus);
  }

  public void stop() {
    if (subscribe != null && !subscribe.isUnsubscribed()) {
      subscribe.unsubscribe();
      fileDownloadSubject = null;
      fileDownloaderPersistence.clear();
      fileDownloaderPersistence = null;
    }
  }

  private Observable<FileDownloadCallback> startFileDownload(DownloadAppFile downloadAppFile) {
    return Observable.just(
        fileDownloaderProvider.createRetryFileDownloader(downloadAppFile.getDownloadMd5(),
            downloadAppFile.getMainDownloadPath(), downloadAppFile.getFileType(),
            downloadAppFile.getPackageName(), downloadAppFile.getVersionCode(),
            downloadAppFile.getFileName(), PublishSubject.create(),
            downloadAppFile.getAlternativeDownloadPath()))
        .doOnNext(
            fileDownloader -> fileDownloaderPersistence.put(downloadAppFile.getMainDownloadPath(),
                fileDownloader))
        .doOnNext(__ -> Logger.getInstance()
            .d(TAG, "Starting app file download " + downloadAppFile.getFileName()))
        .doOnNext(fileDownloader -> fileDownloader.startFileDownload())
        .flatMap(fileDownloader -> handleFileDownloadProgress(fileDownloader))
        .doOnError(Throwable::printStackTrace);
  }

  private Observable<FileDownloadCallback> observeFileDownload() {
    return fileDownloadSubject;
  }

  private void setAppDownloadStatus(FileDownloadCallback fileDownloadCallback) {
    appDownloadStatus.setFileDownloadCallback(fileDownloadCallback);
  }

  private Observable<FileDownloadCallback> handleFileDownloadProgress(
      RetryFileDownloader fileDownloader) {
    return fileDownloader.observeFileDownloadProgress()
        .doOnNext(fileDownloadCallback -> fileDownloadSubject.onNext(fileDownloadCallback))
        .doOnNext(fileDownloadCallback -> {
          if (fileDownloadCallback.getDownloadState()
              == AppDownloadStatus.AppDownloadState.COMPLETED) {
            handleCompletedFileDownload(fileDownloader);
          } else if (fileDownloadCallback.getDownloadState()
              == AppDownloadStatus.AppDownloadState.ERROR_FILE_NOT_FOUND
              || fileDownloadCallback.getDownloadState() == AppDownloadStatus.AppDownloadState.ERROR
              || fileDownloadCallback.getDownloadState()
              == AppDownloadStatus.AppDownloadState.ERROR_NOT_ENOUGH_SPACE) {
            handleErrorFileDownload();
            if (fileDownloadCallback.hasError()) {
              downloadAnalytics.onError(app.getPackageName(), app.getVersionCode(), app.getMd5(),
                  fileDownloadCallback.getError());
            }
          }
        });
  }

  private void handleErrorFileDownload() {
    for (RetryFileDownloader retryFileDownloader : fileDownloaderPersistence.values()) {
      retryFileDownloader.stopFailedDownload();
    }
  }

  private void handleCompletedFileDownload(RetryFileDownloader fileDownloader) {
    fileDownloader.stop();
  }

  @VisibleForTesting
  public Observable<RetryFileDownloader> getFileDownloader(String mainDownloadPath) {
    return Observable.just(fileDownloaderPersistence.get(mainDownloadPath));
  }
}

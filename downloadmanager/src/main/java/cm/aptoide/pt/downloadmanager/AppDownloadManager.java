package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.logger.Logger;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class AppDownloadManager implements AppDownloader {

  private static final String TAG = "AppDownloadManager";
  private final DownloadApp app;
  private final RetryFileDownloaderProvider fileDownloaderProvider;
  private final AppDownloadStatus appDownloadStatus;
  private final CompositeDisposable appDownloaderSubscription;
  private HashMap<String, RetryFileDownloader> fileDownloaderPersistence;
  private PublishSubject<FileDownloadCallback> fileDownloadSubject;

  public AppDownloadManager(RetryFileDownloaderProvider fileDownloaderProvider, DownloadApp app,
      HashMap<String, RetryFileDownloader> fileDownloaderPersistence) {
    this.fileDownloaderProvider = fileDownloaderProvider;
    this.app = app;
    this.fileDownloaderPersistence = fileDownloaderPersistence;
    this.fileDownloadSubject = PublishSubject.create();
    this.appDownloadStatus = new AppDownloadStatus(app.getMd5(), new ArrayList<>(),
        AppDownloadStatus.AppDownloadState.PENDING, app.getSize());
    this.appDownloaderSubscription = new CompositeDisposable();
  }

  @Override public void startAppDownload() {
    appDownloaderSubscription.add(Observable.fromIterable(app.getDownloadFiles())
        .flatMap(downloadAppFile -> startFileDownload(downloadAppFile, app.getAttributionId()))
        .subscribe(__ -> {
        }, Throwable::printStackTrace));
  }

  @Override public Completable removeAppDownload() {
    return Observable.fromIterable(app.getDownloadFiles())
        .flatMap(downloadAppFile -> getFileDownloader(downloadAppFile.getMainDownloadPath()))
        .flatMapCompletable(fileDownloader -> fileDownloader.removeDownloadFile()
            .onErrorComplete());
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
    appDownloaderSubscription.clear();
    fileDownloadSubject = null;
    fileDownloaderPersistence.clear();
    fileDownloaderPersistence = null;
  }

  private Observable<FileDownloadCallback> startFileDownload(DownloadAppFile downloadAppFile,
      String attributionId) {
    return Observable.just(
            fileDownloaderProvider.createRetryFileDownloader(downloadAppFile.getDownloadMd5(),
                downloadAppFile.getMainDownloadPath(), downloadAppFile.getFileType(),
                downloadAppFile.getPackageName(), downloadAppFile.getVersionCode(),
                downloadAppFile.getFileName(), PublishSubject.create(),
                downloadAppFile.getAlternativeDownloadPath(), attributionId))
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
          if (fileDownloadCallback.getDownloadState() != null) {
            switch (fileDownloadCallback.getDownloadState()) {
              case COMPLETED:
                handleCompletedFileDownload(fileDownloader);
                break;
              case ERROR_FILE_NOT_FOUND:
              case ERROR:
              case ERROR_NOT_ENOUGH_SPACE:
                handleErrorFileDownload();
                if (fileDownloadCallback.hasError()) {
                  /*downloadAnalytics.onError(app.getPackageName(), app.getVersionCode(),
                      app.getMd5(), fileDownloadCallback.getError());*/
                }
                break;
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

  public Observable<RetryFileDownloader> getFileDownloader(String mainDownloadPath) {
    return Observable.just(fileDownloaderPersistence.get(mainDownloadPath));
  }
}

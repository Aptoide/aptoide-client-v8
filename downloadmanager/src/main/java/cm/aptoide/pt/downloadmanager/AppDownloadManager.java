package cm.aptoide.pt.downloadmanager;

import android.support.annotation.VisibleForTesting;
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

  private final DownloadApp app;
  private FileDownloaderProvider fileDownloaderProvider;
  private HashMap<String, FileDownloader> fileDownloaderPersistence;
  private PublishSubject<FileDownloadCallback> fileDownloadSubject;
  private AppDownloadStatus appDownloadStatus;
  private Subscription subscribe;

  public AppDownloadManager(FileDownloaderProvider fileDownloaderProvider, DownloadApp app,
      HashMap<String, FileDownloader> fileDownloaderPersistence) {
    this.fileDownloaderProvider = fileDownloaderProvider;
    this.app = app;
    this.fileDownloaderPersistence = fileDownloaderPersistence;
    fileDownloadSubject = PublishSubject.create();
    appDownloadStatus = new AppDownloadStatus(app.getMd5(), new ArrayList<>(),
        AppDownloadStatus.AppDownloadState.PENDING);
  }

  @Override public void startAppDownload() {
    subscribe = Observable.from(app.getDownloadFiles())
        .flatMap(downloadAppFile -> startFileDownload(downloadAppFile))
        .doOnError(throwable -> {
          throw new IllegalStateException(throwable);
        })
        .toCompletable()
        .subscribe(() -> {
        }, Throwable::printStackTrace);
  }

  @Override public Completable pauseAppDownload() {
    return Observable.from(app.getDownloadFiles())
        .flatMap(downloadAppFile -> getFileDownloader(downloadAppFile.getMainDownloadPath()))
        .flatMapCompletable(fileDownloader -> fileDownloader.pauseDownload())
        .toCompletable();
  }

  @Override public Completable removeAppDownload() {
    return Observable.from(app.getDownloadFiles())
        .flatMap(downloadAppFile -> getFileDownloader(downloadAppFile.getMainDownloadPath()))
        .flatMapCompletable(fileDownloader -> fileDownloader.removeDownloadFile())
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
    if (!subscribe.isUnsubscribed()) {
      subscribe.unsubscribe();
    }
  }

  private Observable<FileDownloadCallback> startFileDownload(DownloadAppFile downloadAppFile) {
    return Observable.just(
        fileDownloaderProvider.createFileDownloader(downloadAppFile.getDownloadMd5(),
            downloadAppFile.getMainDownloadPath(), downloadAppFile.getFileType(),
            downloadAppFile.getPackageName(), downloadAppFile.getVersionCode(),
            downloadAppFile.getFileName(), PublishSubject.create()))
        .doOnNext(
            fileDownloader -> fileDownloaderPersistence.put(downloadAppFile.getMainDownloadPath(),
                fileDownloader))
        .doOnNext(__ -> Logger.getInstance()
            .d("AppDownloader", "Starting app file download " + downloadAppFile.getFileName()))
        .flatMap(fileDownloader -> fileDownloader.startFileDownload()
            .andThen(handleFileDownloadProgress(fileDownloader)));
  }

  private Observable<FileDownloadCallback> observeFileDownload() {
    return fileDownloadSubject;
  }

  private void setAppDownloadStatus(FileDownloadCallback fileDownloadCallback) {
    appDownloadStatus.setFileDownloadCallback(fileDownloadCallback);
    appDownloadStatus.setAppDownloadState(fileDownloadCallback.getDownloadState());
  }

  private Observable<FileDownloadCallback> handleFileDownloadProgress(
      FileDownloader fileDownloader) {
    return fileDownloader.observeFileDownloadProgress()
        .doOnNext(fileDownloadCallback -> fileDownloadSubject.onNext(fileDownloadCallback));
  }

  @VisibleForTesting public Observable<FileDownloader> getFileDownloader(String mainDownloadPath) {
    return Observable.just(fileDownloaderPersistence.get(mainDownloadPath));
  }
}

package cm.aptoide.pt.downloadmanager;

import android.support.annotation.VisibleForTesting;
import android.util.Log;
import java.util.Collections;
import java.util.HashMap;
import rx.Completable;
import rx.Observable;
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

  public AppDownloadManager(FileDownloaderProvider fileDownloaderProvider, DownloadApp app) {
    this.fileDownloaderProvider = fileDownloaderProvider;
    this.app = app;
    this.fileDownloaderPersistence = new HashMap<>();
    fileDownloadSubject = PublishSubject.create();
    appDownloadStatus = new AppDownloadStatus(app.getMd5(), Collections.emptyList(),
        AppDownloadStatus.AppDownloadState.PENDING);
  }

  @Override public Completable startAppDownload() {
    return Observable.from(app.getDownloadFiles())
        .flatMap(downloadAppFile -> Observable.just(
            fileDownloaderProvider.createFileDownloader(downloadAppFile.getDownloadMd5(),
                downloadAppFile.getMainDownloadPath(), downloadAppFile.getFileType(),
                downloadAppFile.getPackageName(), downloadAppFile.getVersionCode(),
                downloadAppFile.getFileName(), PublishSubject.create()))
            .doOnNext(fileDownloader -> fileDownloaderPersistence.put(
                downloadAppFile.getAlternativeDownloadPath(), fileDownloader)))
        .flatMap(fileDownloader -> fileDownloader.startFileDownload()
            .andThen(handleFileDownloadProgress(fileDownloader)))
        .doOnError(throwable -> {
          throw new IllegalStateException(throwable);
        })
        .toCompletable();
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
        .doOnSubscribe(() -> Log.d("FileDownloader", "observeDownloadProgress: just subscribe"))
        .map(__ -> appDownloadStatus);
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

package cm.aptoide.pt.downloadmanager;

import android.support.annotation.VisibleForTesting;
import java.util.HashMap;
import rx.Completable;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class AppDownloadManager implements AppDownloader {

  private FileDownloaderProvider fileDownloaderProvider;
  private DownloadApp app;
  private HashMap<String, FileDownloader> fileDownloaderPersistence;

  public AppDownloadManager(FileDownloaderProvider fileDownloaderProvider, DownloadApp app,
      HashMap<String, FileDownloader> fileDownloaderPersistence) {
    this.fileDownloaderProvider = fileDownloaderProvider;
    this.app = app;
    this.fileDownloaderPersistence = fileDownloaderPersistence;
  }

  @Override public Completable startAppDownload() {
    return Observable.from(app.getDownloadFiles())
        .flatMap(downloadAppFile -> Observable.just(
            fileDownloaderProvider.createFileDownloader(downloadAppFile.getMainDownloadPath(),
                downloadAppFile.getFileType(), downloadAppFile.getPackageName(),
                downloadAppFile.getVersionCode(), downloadAppFile.getFileName(),
                PublishSubject.create()))
            .doOnNext(fileDownloader -> fileDownloaderPersistence.put(
                downloadAppFile.getAlternativeDownloadPath(), fileDownloader)))
        .flatMapCompletable(fileDownloader -> fileDownloader.startFileDownload())
        .flatMap(fileDownloader -> handleFileDownloadProgress(fileDownloader))
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
    return null;
  }

  private Observable<FileDownloadCallback> handleFileDownloadProgress(
      FileDownloader fileDownloader) {
    return fileDownloader.observeFileDownloadProgress();
  }

  @VisibleForTesting public Observable<FileDownloader> getFileDownloader(String mainDownloadPath) {
    return Observable.just(fileDownloaderPersistence.get(mainDownloadPath));
  }
}

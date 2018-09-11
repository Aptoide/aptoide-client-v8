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

  private final DownloadApp app;
  private FileDownloaderProvider fileDownloaderProvider;
  private HashMap<String, FileDownloader> fileDownloaderPersistence;
  private PublishSubject<FileDownloadCallback> fileDownloadSubject;
  private AppDownloadStatus appDownloadStatus;

  public AppDownloadManager(FileDownloaderProvider fileDownloaderProvider, DownloadApp app,
      HashMap<String, FileDownloader> fileDownloaderPersistence) {
    this.fileDownloaderProvider = fileDownloaderProvider;
    this.app = app;
    this.fileDownloaderPersistence = fileDownloaderPersistence;
    fileDownloadSubject = PublishSubject.create();
    appDownloadStatus = new AppDownloadStatus(app.getMd5(), null, null, null,
        AppDownloadStatus.AppDownloadState.PENDING);
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
    return fileDownloadSubject.flatMapCompletable(fileDownloadCallback -> {
      setAppDownloadStatus(fileDownloadCallback);
      return Completable.complete();
    })
        .map(__ -> appDownloadStatus);
  }

  private void setAppDownloadStatus(FileDownloadCallback fileDownloadCallback) {
    if (fileDownloadCallback.getFileType() == DownloadAppFile.FileType.APK.getType()) {
      appDownloadStatus.setApk(fileDownloadCallback);
    } else if (fileDownloadCallback.getFileType() == DownloadAppFile.FileType.OBB_MAIN.getType()) {
      appDownloadStatus.setObbMain(fileDownloadCallback);
    } else if (fileDownloadCallback.getFileType() == DownloadAppFile.FileType.OBB_PATCH.getType()) {
      appDownloadStatus.setObbPatch(fileDownloadCallback);
    }
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

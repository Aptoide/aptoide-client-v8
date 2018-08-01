package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;
import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class AppDownloadManager implements AppDownloader {

  private FileDownloader fileDownloader;
  private DownloadApp app;

  public AppDownloadManager(FileDownloader fileDownloader, DownloadApp app) {
    this.fileDownloader = fileDownloader;
    this.app = app;
  }

  @Override public Completable startAppDownload() {
    return Observable.from(app.getDownloadFiles())
        .flatMapCompletable(downloadAppFile -> fileDownloader.startFileDownload(downloadAppFile))
        .toCompletable();
  }

  @Override public Completable pauseAppDownload() {
    return Observable.from(app.getDownloadFiles())
        .flatMapCompletable(downloadAppFile -> fileDownloader.pauseDownload(downloadAppFile))
        .toCompletable();
  }

  @Override public Observable<Download> getAppDownload(String md5) {
    return null;
  }

  @Override public Observable<Download> getCurrentActiveDownload() {
    return null;
  }

  @Override public Completable removeAppDownload() {
    return Observable.from(app.getDownloadFiles())
        .flatMapCompletable(downloadAppFile -> fileDownloader.removeDownloadFile(downloadAppFile))
        .toCompletable();
  }
}

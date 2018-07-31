package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;
import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class AppDownloadManager implements AppDownloader {

  private FileDownloader fileDownloader;

  public AppDownloadManager(FileDownloader fileDownloader) {
    this.fileDownloader = fileDownloader;
  }

  @Override public Completable startAppDownload(DownloadApp app) {
    return Observable.from(app.getDownloadFiles())
        .flatMapCompletable(downloadAppFile -> fileDownloader.startFileDownload(downloadAppFile))
        .toCompletable();
  }

  @Override public void pauseAppDownload(String md5) {

  }

  @Override public Observable<Download> getAppDownload(String md5) {
    return null;
  }

  @Override public Observable<Download> getCurrentActiveDownload() {
    return null;
  }

  @Override public void removeDownload(String md5) {

  }
}

package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;
import java.util.List;
import rx.Observable;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class NewAptoideDownloadManager implements DownloadManager {

  private AppDownloader appDownloader;
  //private DownloadsPersistence downloadsPersistence;

  /**
   * public NewAptoideDownloadManager(AppDownloader appDownloader,
   * DownloadsPersistence downloadsPersistence) {
   * this.appDownloader = appDownloader;
   * this.downloadsPersistence = downloadsPersistence;
   * }
   **/
  public NewAptoideDownloadManager(AppDownloader appDownloader) {
    this.appDownloader = appDownloader;
  }

  public void start() {
    dispatchDownloads();
  }

  private void dispatchDownloads() {

  }

  @Override public Observable<Download> startDownload(Download download) {
    return null;
  }

  @Override public Observable<Download> getDownload(String md5) {
    return null;
  }

  @Override public Observable<Download> getDownloadsByMd5(String md5) {
    return null;
  }

  @Override public Observable<List<Download>> getDownloadsList() {
    return null;
  }

  @Override public Observable<Download> getCurrentActiveDownload() {
    return null;
  }

  @Override public Observable<List<Download>> getCurrentActiveDownloads() {
    return null;
  }

  @Override public void pauseAllDownloads() {

  }

  @Override public void pauseDownload(String md5) {

  }

  @Override public Observable<Integer> getDownloadStatus(String md5) {
    return null;
  }

  @Override public void removeDownload(String md5) {

  }
}

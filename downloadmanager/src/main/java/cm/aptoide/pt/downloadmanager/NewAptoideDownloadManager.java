package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;
import java.util.HashMap;
import java.util.List;
import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class NewAptoideDownloadManager implements DownloadManager {

  private DownloadsRepository downloadsRepository;
  private HashMap<String, AppDownloader> appDownloaderMap;

  public NewAptoideDownloadManager(DownloadsRepository downloadsRepository) {
    this.downloadsRepository = downloadsRepository;
    appDownloaderMap = new HashMap<>();
  }

  public void start() {
    dispatchDownloads();
  }

  @Override public void stop() {
  }

  @Override public Completable startDownload(Download download) {
    return downloadsRepository.save(download);
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

  private void dispatchDownloads() {
  }
}

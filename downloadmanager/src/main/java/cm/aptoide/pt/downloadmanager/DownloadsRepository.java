package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import rx.Observable;

/**
 * Created by filipegoncalves on 8/21/18.
 */

public class DownloadsRepository {

  private DownloadAccessor downloadAccessor;

  public DownloadsRepository(DownloadAccessor downloadAccessor) {
    this.downloadAccessor = downloadAccessor;
  }

  public void save(Download download) {
    downloadAccessor.save(download);
  }

  public Observable<Download> getDownload(String md5) {
    return downloadAccessor.get(md5);
  }
}

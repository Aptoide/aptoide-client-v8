package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import java.util.List;
import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 8/21/18.
 */

public class DownloadsRepository {

  private DownloadAccessor downloadAccessor;

  public DownloadsRepository(DownloadsAccessor downloadAccessor) {
    this.downloadAccessor = downloadAccessor;
  }

  public Observable<List<Download>> getDownloads() {
    downloadAccessor.
  }

  public Completable save(Download download) {

  }
}

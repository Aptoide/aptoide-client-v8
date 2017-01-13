package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import rx.Observable;

public class DownloadRepository implements Repository<Download, String> {

  private final DownloadAccessor accessor;

  DownloadRepository(DownloadAccessor downloadAccessor) {
    this.accessor = downloadAccessor;
  }

  @Override public void save(Download entity) {
    accessor.save(entity);
  }

  @Override public Observable<Download> get(String md5) {
    return accessor.get(md5);
  }
}

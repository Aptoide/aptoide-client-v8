package cm.aptoide.pt.v8engine.download;

import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import java.util.List;
import rx.Observable;

public class DownloadRepository {

  private final DownloadAccessor accessor;

  public DownloadRepository(DownloadAccessor downloadAccessor) {
    this.accessor = downloadAccessor;
  }

  public void save(Download entity) {
    accessor.save(entity);
  }

  public Observable<Download> get(String md5) {
    return accessor.get(md5);
  }

  public Observable<List<Download>> getAll() {
    return accessor.getAll();
  }
}

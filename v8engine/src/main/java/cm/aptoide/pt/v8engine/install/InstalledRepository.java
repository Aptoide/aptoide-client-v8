package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 7/27/16.
 */
public class InstalledRepository {

  private final InstalledAccessor accessor;

  public InstalledRepository(InstalledAccessor accessor) {
    this.accessor = accessor;
  }

  public Observable<List<Installed>> getAsList(String packageName) {
    return accessor.getAsList(packageName);
  }

  public void save(Installed installed) {
    accessor.insert(installed);
  }

  public Observable<Installed> get(String packageName) {
    return accessor.get(packageName);
  }

  public void remove(String packageName) {
    accessor.remove(packageName);
  }

  public boolean contains(String packageName) {
    return accessor.isInstalled(packageName)
        .toBlocking()
        .first();
  }

  /**
   * @return continuous {@link Observable} of all the installed apps
   */
  public Observable<List<Installed>> getAllSorted() {
    return accessor.getAllSorted();
  }
}

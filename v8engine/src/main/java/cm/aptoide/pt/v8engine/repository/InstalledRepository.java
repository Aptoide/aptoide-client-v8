/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 7/27/16.
 */
public class InstalledRepository implements Repository {
  private InstalledAccessor accessor;

  public InstalledRepository(InstalledAccessor accessor) {
    this.accessor = accessor;
  }

  /**
   * Get all installed apps
   * <dl>
   * <dt><b>Scheduler:</b></dt>
   * <dd>{@code getAll} operate by default on {@link RealmSchedulers}.</dd>
   * </dl>
   *
   * @return an observable with a list of installed apps
   */
  public Observable<List<Installed>> getAll() {
    return accessor.getAll();
  }

  public Observable<List<Installed>> getAsList(String packageName) {
    return accessor.getAsList(packageName);
  }

  public Observable<Installed> get(String packageName) {
    return accessor.get(packageName);
  }

  public void insert(Installed installed) {
    accessor.insert(installed);
  }

  public void remove(String packageName) {
    accessor.remove(packageName);
  }

  public boolean contains(String packageName) {
    return accessor.isInstalled(packageName).toBlocking().first();
  }

  public Observable<List<Installed>> getAllSorted() {
    return accessor.getAllSorted();
  }
}

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.UpdatesAccessor;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import java.util.List;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by trinkes on 9/23/16.
 */

@AllArgsConstructor public class UpdateRepository implements Repository {
  private UpdatesAccessor accessor;

  /**
   * Get all updates that should be shown to user, the excluded updates are not in the list
   * <dl>
   * <dt><b>Scheduler:</b></dt>
   * <dd>{@code getUpdates} operate by default on {@link RealmSchedulers}.</dd>
   * </dl>
   *
   * @return an observable with a list of updates
   */
  public Observable<List<Update>> getUpdates() {
    return accessor.getUpdates();
  }

  public Observable<Update> get(String packageName) {
    return accessor.get(packageName);
  }

  public Observable<List<Update>> getAll() {
    return accessor.getAll();
  }
}


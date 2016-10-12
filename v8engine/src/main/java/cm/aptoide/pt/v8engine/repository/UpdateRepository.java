package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.CrashReports;
import java.util.List;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by trinkes on 9/23/16.
 */

@AllArgsConstructor public class UpdateRepository implements Repository {
  private static final String TAG = UpdateRepository.class.getName();
  private UpdateAccessor accessor;

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
    return accessor.getAll(false);
  }

  public Observable<Update> get(String packageName) {
    return accessor.get(packageName);
  }

  public Observable<List<Update>> getAllWithExluded() {
    return accessor.getAll();
  }

  public void remove(Update update) {
    accessor.remove(update.getPackageName());
  }

  public void remove(String packageName) {
    accessor.remove(packageName);
  }

  public void setExcluded(String packageName, boolean excluded) {
    accessor.get(packageName).subscribe(update -> {
      update.setExcluded(excluded);
      accessor.insert(update);
    }, err -> {
      Logger.e(TAG, err);
      CrashReports.logException(err);
    });
  }
}


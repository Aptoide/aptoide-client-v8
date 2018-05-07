package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.updates.UpdateRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class UpdatesManager {
  private UpdateRepository updateRepository;

  public UpdatesManager(UpdateRepository updateRepository) {
    this.updateRepository = updateRepository;
  }

  /**
   * Filters updates returning the installed app or empty item.
   *
   * @param item App to filter.
   *
   * @return {@link Observable} to a {@link Installed} or empty.
   */
  // TODO: 31/1/2017 instead of Observable<Installed> use Single<Installed>
  public Observable<Installed> filterUpdates(Installed item) {
    return updateRepository.contains(item.getPackageName(), false)
        .flatMap(isUpdate -> {
          if (isUpdate) {
            return Observable.empty();
          }
          return Observable.just(item);
        });
  }

  public Observable<List<Update>> getUpdatesList(boolean isExcluded) {
    return updateRepository.getAll(isExcluded)
        .sample(750, TimeUnit.MILLISECONDS);
  }

  public Observable<Update> getUpdate(String packageName) {
    return updateRepository.get(packageName);
  }

  public Observable<List<Update>> getAllUpdates() {
    return updateRepository.getAll(false);
  }

  public Observable<Void> excludeUpdate(String packageName) {
    return updateRepository.setExcluded(packageName, true);
  }

  public Completable refreshUpdates() {
    return updateRepository.sync(true, false);
  }

  public Observable<Integer> getUpdatesNumber() {
    return getUpdatesList(false).map(list -> list.size());
  }
}

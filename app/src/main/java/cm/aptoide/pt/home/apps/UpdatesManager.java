package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.database.room.RoomInstalled;
import cm.aptoide.pt.database.room.RoomUpdate;
import cm.aptoide.pt.updates.UpdateRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Single;

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
   * @return {@link Observable} to a {@link RoomInstalled} or empty.
   */
  public Single<RoomInstalled> filterUpdates(RoomInstalled item) {
    return updateRepository.contains(item.getPackageName(), false)
        .flatMap(isUpdate -> {
          if (isUpdate) {
            return Single.just(null);
          }
          return Single.just(item);
        });
  }

  public Observable<List<RoomUpdate>> getUpdatesList() {
    return updateRepository.getAll(false)
        .flatMap(updates -> Observable.just(updates))
        .sample(750, TimeUnit.MILLISECONDS);
  }

  public Single<RoomUpdate> getUpdate(String packageName) {
    return updateRepository.get(packageName);
  }

  public Completable excludeUpdate(String packageName) {
    return updateRepository.setExcluded(packageName);
  }

  public Completable refreshUpdates() {
    return updateRepository.sync(true, false, true);
  }

  public Observable<Integer> getUpdatesNumber() {
    return getUpdatesList().map(list -> list.size());
  }
}

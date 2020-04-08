package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.database.room.RoomInstalled;
import cm.aptoide.pt.database.room.RoomUpdate;
import cm.aptoide.pt.install.Install;
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
  // TODO: 31/1/2017 instead of Observable<Installed> use Single<Installed>
  public Single<RoomInstalled> filterUpdates(RoomInstalled item) {
    return updateRepository.contains(item.getPackageName(), false)
        .flatMap(isUpdate -> {
          if (isUpdate) {
            return Single.just(null);
          }
          return Single.just(item);
        });
  }

  public Single<Install> filterAppcUpgrade(Install item) {
    return updateRepository.contains(item.getPackageName(), false, true)
        .flatMap(isUpgrade -> {
          if (isUpgrade) {
            return Single.just(null);
          }
          return Single.just(item);
        });
  }

  public Observable<List<RoomUpdate>> getUpdatesList(boolean excludeAppcUpgrades) {
    return updateRepository.getAll(false)
        .flatMap(updates -> Observable.just(updates)
            .flatMapIterable(list -> list)
            .filter(update -> !excludeAppcUpgrades || !update.isAppcUpgrade())
            .toList())
        .sample(750, TimeUnit.MILLISECONDS);
  }

  public Observable<List<RoomUpdate>> getAppcUpgradesList(boolean isExcluded) {
    return updateRepository.getAll(isExcluded)
        .flatMap(updates -> Observable.just(updates)
            .flatMapIterable(list -> list)
            .filter(update -> update.isAppcUpgrade())
            .toList())
        .sample(750, TimeUnit.MILLISECONDS);
  }

  public Observable<RoomUpdate> getUpdate(String packageName) {
    return updateRepository.get(packageName);
  }

  public Observable<List<RoomUpdate>> getAllUpdates() {
    return updateRepository.getAll(false)
        .flatMap(updates -> Observable.just(updates)
            .flatMapIterable(list -> list)
            .filter(update -> !update.isAppcUpgrade())
            .toList());
  }

  public Completable excludeUpdate(String packageName) {
    return updateRepository.setExcluded(packageName, true);
  }

  public Completable refreshUpdates() {
    return updateRepository.sync(true, false);
  }

  public Observable<Integer> getUpdatesNumber() {
    return getUpdatesList(false).map(list -> list.size());
  }
}

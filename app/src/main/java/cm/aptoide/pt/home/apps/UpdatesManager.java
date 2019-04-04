package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.install.Install;
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

  public Observable<Install> filterAppcUpgrade(Install item) {
    return allowAppcUpgrades(item, false);
  }

  public Observable<Install> filterNonAppcUpgrade(Install item) {
    return allowAppcUpgrades(item, true);
  }

  private Observable<Install> allowAppcUpgrades(Install item, boolean allowUpgrades) {
    return updateRepository.contains(item.getPackageName(), false, allowUpgrades)
        .flatMap(isUpdate -> {
          if (isUpdate) {
            return Observable.just(item);
          }
          return Observable.empty();
        });
  }

  public Observable<List<Update>> getUpdatesList(boolean isExcluded, boolean excludeAppcUpgrades) {
    return updateRepository.getAll(isExcluded)
        .flatMap(updates -> Observable.just(updates)
            .flatMapIterable(list -> list)
            .filter(update -> !excludeAppcUpgrades || !update.isAppcUpgrade())
            .toList())
        .sample(750, TimeUnit.MILLISECONDS);
  }

  public Observable<List<Update>> getAppcUpgradesList(boolean isExcluded) {
    return updateRepository.getAll(isExcluded)
        .flatMap(updates -> Observable.just(updates)
            .flatMapIterable(list -> list)
            .filter(update -> update.isAppcUpgrade())
            .toList())
        .sample(750, TimeUnit.MILLISECONDS);
  }

  public Observable<Update> getUpdate(String packageName) {
    return updateRepository.get(packageName);
  }

  public Observable<List<Update>> getAllUpdates() {
    return updateRepository.getAll(false)
        .flatMap(updates -> Observable.just(updates)
            .flatMapIterable(list -> list)
            .filter(update -> !update.isAppcUpgrade())
            .toList());
  }

  public Observable<Void> excludeUpdate(String packageName) {
    return updateRepository.setExcluded(packageName, true);
  }

  public Completable refreshUpdates() {
    return updateRepository.sync(true, false);
  }

  public Observable<Integer> getUpdatesNumber() {
    return getUpdatesList(false, false).map(list -> list.size());
  }
}

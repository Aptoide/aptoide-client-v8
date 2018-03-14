package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.updates.UpdateRepository;
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
}

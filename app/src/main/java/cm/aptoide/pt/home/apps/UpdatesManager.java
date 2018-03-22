package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.updates.UpdateRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

  public Observable<List<Update>> getUpdatesList() {
    return updateRepository.getAll(false)
        .sample(750, TimeUnit.MILLISECONDS);
  }
}

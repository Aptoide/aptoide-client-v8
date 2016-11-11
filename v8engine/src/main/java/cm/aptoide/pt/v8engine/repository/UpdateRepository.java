package cm.aptoide.pt.v8engine.repository;

import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 9/23/16.
 */

@AllArgsConstructor public class UpdateRepository implements Repository {
  private static final String TAG = UpdateRepository.class.getName();
  private UpdateAccessor updateAccessor;
  private StoreAccessor storeAccessor;

  public Observable<List<Update>> getUpdates() {
    return getUpdates(false);
  }

  public @NonNull Observable<List<Update>> getUpdates(boolean bypassCache) {
    return storeAccessor.getAll()
        .observeOn(Schedulers.io())
        .first()
        .flatMapIterable(stores -> stores)
        .map(store -> store.getStoreId())
        .toList()
        .flatMap(storeIds -> getNetworkUpdates(storeIds, bypassCache))
        .flatMapIterable(listAppsUpdates -> listAppsUpdates.getList())
        .flatMap(app -> saveUpdate(app))
        .flatMap(success -> getStoredUpdates());
  }

  private Observable<ListAppsUpdates> getNetworkUpdates(List<Long> storeIds, boolean bypassCache) {
    return ListAppsUpdatesRequest.of(storeIds, AptoideAccountManager.getAccessToken(),
        AptoideAccountManager.getUserEmail()).observe(bypassCache).onErrorReturn(throwable -> {
      ListAppsUpdates listAppsUpdates = new ListAppsUpdates();
      listAppsUpdates.setList(Collections.emptyList());
      return listAppsUpdates;
    });
  }

  @NonNull private Observable<Void> saveUpdate(App app) {
    return updateAccessor.get(app.getPackageName())
        .first()
        .filter(update -> update == null || !update.isExcluded())
        .doOnNext(update -> updateAccessor.save(new Update(app)))
        .map(update -> null);
  }

  /**
   * Get all updates that should be shown to user, the excluded updates are not in the list
   * <dl>
   * <dt><b>Scheduler:</b></dt>
   * <dd>{@code getUpdates} operate by default on {@link RealmSchedulers}.</dd>
   * </dl>
   *
   * @return an observable with a list of updates
   */
  //@Deprecated public Observable<List<Update>> getUpdates() {
  //  return updateAccessor.getAll(false);
  //}
  private Observable<List<Update>> getStoredUpdates() {
    return updateAccessor.getAll(false);
  }

  public Observable<Update> get(String packageName) {
    return updateAccessor.get(packageName);
  }

  public Observable<List<Update>> getAllWithExluded() {
    return updateAccessor.getAll();
  }

  public void remove(Update update) {
    updateAccessor.remove(update.getPackageName());
  }

  public void remove(String packageName) {
    updateAccessor.remove(packageName);
  }

  public Observable<List<Update>> getNonExcludedUpdates() {
    return updateAccessor.getAll()
        .flatMap(
            updates -> Observable.from(updates).filter(update -> !update.isExcluded()).toList());
  }

  public Observable<Void> setExcluded(String packageName, boolean excluded) {
    return updateAccessor.get(packageName).first().map(update -> {
      update.setExcluded(excluded);
      updateAccessor.insert(update);
      return null;
    });
  }
}
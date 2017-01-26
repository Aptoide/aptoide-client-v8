package cm.aptoide.pt.v8engine.repository;

import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 9/23/16.
 */

public class UpdateRepository implements Repository {

  private UpdateAccessor updateAccessor;
  private StoreAccessor storeAccessor;

  UpdateRepository(UpdateAccessor updateAccessor, StoreAccessor storeAccessor) {
    this.updateAccessor = updateAccessor;
    this.storeAccessor = storeAccessor;
  }

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
        .flatMap(storeIds -> getNetworkUpdates(storeIds, bypassCache).distinctUntilChanged())
        .flatMap(updates -> {
          if (!updates.isEmpty()) {
            // network fetch succeeded. remove local non-excluded updates
            // and save the new updates
            return removeNonExcluded().flatMapIterable(aVoid -> updates)
                .flatMap(app -> saveUpdate(app))
                .toList();
          }
          // there was a network error
          return Observable.just(null);
        })
        // return the local (non-excluded) updates
        .flatMap(aVoid -> getStoredUpdates());
  }

  private Observable<List<App>> getNetworkUpdates(List<Long> storeIds, boolean bypassCache) {
    return ListAppsUpdatesRequest.of(storeIds, AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID()).observe(bypassCache).map(result -> {
      if (result.isOk()) {
        return result.getList();
      }
      return Collections.<App>emptyList();
    }).onErrorReturn(throwable -> Collections.emptyList());
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

  private Observable<Void> removeNonExcluded() {
    return getStoredUpdates().first()
        .flatMapIterable(list -> list)
        .doOnNext(update -> remove(update))
        .toList()
        .map(list -> null);
  }

  public Observable<Void> removeAll() {
    return Observable.fromCallable(() -> {
      updateAccessor.removeAll();
      return null;
    });
  }

  public Observable<Void> remove(Update update) {
    return Observable.fromCallable(() -> {
      updateAccessor.remove(update.getPackageName());
      return null;
    });
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

  public Observable<List<Update>> getAllSorted(boolean isExcluded) {
    return updateAccessor.getAllSorted(isExcluded);
  }

  public Observable<Boolean> contains(String packageName, boolean isExcluded) {
    return updateAccessor.contains(packageName, isExcluded);
  }
}

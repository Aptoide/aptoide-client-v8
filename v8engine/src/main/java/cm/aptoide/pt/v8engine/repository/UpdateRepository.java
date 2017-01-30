package cm.aptoide.pt.v8engine.repository;

import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import java.util.Collections;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 9/23/16.
 */

public class UpdateRepository implements Repository<Update, String> {

  private static final String TAG = UpdateRepository.class.getName();

  private final AptoideClientUUID aptoideClientUUID;

  private UpdateAccessor updateAccessor;
  private StoreAccessor storeAccessor;

  UpdateRepository(UpdateAccessor updateAccessor, StoreAccessor storeAccessor) {
    this.updateAccessor = updateAccessor;
    this.storeAccessor = storeAccessor;

    aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext());
  }

  public @NonNull Observable<List<Update>> getUpdates(boolean bypassCache) {
    return storeAccessor.getAll()
        .observeOn(Schedulers.io())
        .first()
        .flatMapIterable(stores -> stores)
        .map(store -> store.getStoreId())
        .toList()
        // distinctUntilChanged is used to avoid getting duplicate entries when fetching network updates
        .flatMap(storeIds -> getNetworkUpdates(storeIds, bypassCache))
        .distinctUntilChanged()
        .flatMap(updates -> {
          // remove local non-excluded updates
          // save the new updates
          return removeAllNonExcluded().andThen(saveNewUpdates(updates)).toObservable();
        })
        // return all the local (non-excluded) updates
        // this is a non-closing Observable, so new db modifications will trigger this observable
        .flatMap(aVoid -> getAllNonExcluded());
  }

  private Observable<List<App>> getNetworkUpdates(List<Long> storeIds, boolean bypassCache) {
    Logger.d(TAG, String.format("getNetworkUpdates() -> using %d stores", storeIds.size()));
    return ListAppsUpdatesRequest.of(storeIds, AptoideAccountManager.getAccessToken(),
        aptoideClientUUID.getAptoideClientUUID()).observe(bypassCache).map(result -> {
      if (result.isOk()) {
        return result.getList();
      }
      return Collections.<App>emptyList();
    });
  }

  private Completable saveNewUpdates(List<App> updates) {
    return Completable.fromSingle(Observable.from(updates)
        .map(app -> new Update(app))
        .toList()
        .toSingle()
        .flatMap(updateList -> {
          Logger.d(TAG, String.format("filter %d updates for non excluded and save the remainder",
              updateList.size()));
          return saveNonExcludedUpdates(updateList);
        }));
  }

  @Override public void save(Update entity) {
    updateAccessor.insert(entity);
  }

  public Observable<List<Update>> getAllNonExcluded() {
    return updateAccessor.getAll(false);
  }

  public Completable removeAllNonExcluded() {
    return getAllNonExcluded().first().toSingle().flatMapCompletable(updates -> removeAll(updates));
  }

  public Completable removeAll(List<Update> updates) {
    return Observable.from(updates)
        .map(update -> update.getPackageName())
        .toList()
        .flatMap(updatesAsPackageNames -> {
          if (updatesAsPackageNames != null && !updatesAsPackageNames.isEmpty()) {
            updateAccessor.removeAll(updatesAsPackageNames);
          }
          return null;
        })
        .toCompletable();
  }

  @NonNull private Single<List<Update>> saveNonExcludedUpdates(List<Update> updateList) {
    // remove excluded from list
    // save the remainder
    return Observable.from(updateList)
        .flatMap(update -> updateAccessor.isExcluded(update.getPackageName()).flatMap(excluded -> {
          if (excluded) {
            return Observable.empty();
          }
          return Observable.just(update);
        }))
        .toList()
        .toSingle()
        .doOnSuccess(updateListFiltered -> {
          if (updateListFiltered != null && !updateList.isEmpty()) {
            updateAccessor.saveAll(updateListFiltered);
          }
        });
  }

  public Completable remove(List<Update> updates) {
    return Observable.from(updates)
        .map(update -> update.getPackageName())
        .toList()
        .doOnNext(updatesAsPackages -> updateAccessor.removeAll(updatesAsPackages))
        .toCompletable();
  }

  public Completable remove(Update update) {
    return Completable.fromCallable(() -> {
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

  public Observable<Boolean> contains(String packageName, boolean isExcluded) {
    return updateAccessor.contains(packageName, isExcluded);
  }

  public Observable<List<Update>> getAllSorted(boolean isExcluded) {
    return updateAccessor.getAllSorted(isExcluded);
  }

  public Observable<Update> get(String packageName) {
    return updateAccessor.get(packageName);
  }
}

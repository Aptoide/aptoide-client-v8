package cm.aptoide.pt.updates;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.room.RoomUpdate;
import cm.aptoide.pt.dataprovider.aab.AppBundlesVisibilityManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppcAppsUpgradesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networking.IdsRepository;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 9/23/16.
 */

public class UpdateRepository {

  private static final String TAG = UpdateRepository.class.getName();

  private final IdsRepository idsRepository;
  private final UpdatePersistence updatePersistence;
  private final StoreAccessor storeAccessor;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final PackageManager packageManager;
  private final AppBundlesVisibilityManager appBundlesVisibilityManager;
  private final UpdateMapper updateMapper;

  public UpdateRepository(UpdatePersistence updatePersistence, StoreAccessor storeAccessor,
      IdsRepository idsRepository, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      PackageManager packageManager, AppBundlesVisibilityManager appBundlesVisibilityManager,
      UpdateMapper updateMapper) {
    this.updatePersistence = updatePersistence;
    this.storeAccessor = storeAccessor;
    this.idsRepository = idsRepository;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.packageManager = packageManager;
    this.appBundlesVisibilityManager = appBundlesVisibilityManager;
    this.updateMapper = updateMapper;
  }

  public @NonNull Completable sync(boolean bypassCache, boolean bypassServerCache) {
    return storeAccessor.getAll()
        .first()
        .observeOn(Schedulers.io())
        .flatMap(stores -> Observable.from(stores)
            .map(store -> store.getStoreId())
            .toList())
        .flatMap(storeIds -> getNetworkUpdates(storeIds, bypassCache, bypassServerCache))
        .toSingle()
        .flatMapCompletable(updates -> {
          // remove local non-excluded updates
          // save the new updates
          // return all the local (non-excluded) updates
          // this is a non-closing Observable, so new db modifications will trigger this observable
          return removeAllNonExcluded().andThen(saveNewUpdates(updates));
        })
        .andThen(saveAppcUpgrades(bypassCache, bypassServerCache));
  }

  private Completable saveAppcUpgrades(boolean bypassCache, boolean bypassServerCache) {
    return getNetworkAppcUpgrades(bypassCache, bypassServerCache).toSingle()
        .flatMapCompletable(upgrades -> saveNewUpgrades(upgrades));
  }

  private Observable<List<App>> getNetworkAppcUpgrades(boolean bypassCache,
      boolean bypassServerCache) {
    return idsRepository.getUniqueIdentifier()
        .flatMapObservable(
            id -> ListAppcAppsUpgradesRequest.of(id, bodyInterceptor, httpClient, converterFactory,
                tokenInvalidator, sharedPreferences, packageManager)
                .observe(bypassCache, bypassServerCache))
        .map(result -> {
          if (result != null && result.isOk()) {
            return result.getList();
          }
          return Collections.emptyList();
        });
  }

  private Observable<List<App>> getNetworkUpdates(List<Long> storeIds, boolean bypassCache,
      boolean bypassServerCache) {
    Logger.getInstance()
        .d(TAG, String.format("getNetworkUpdates() -> using %d stores", storeIds.size()));
    return idsRepository.getUniqueIdentifier()
        .flatMapObservable(
            id -> ListAppsUpdatesRequest.of(storeIds, id, bodyInterceptor, httpClient,
                converterFactory, tokenInvalidator, sharedPreferences, packageManager,
                appBundlesVisibilityManager)
                .observe(bypassCache, bypassServerCache))
        .map(result -> {
          if (result != null && result.isOk()) {
            return result.getList();
          }
          return Collections.<App>emptyList();
        });
  }

  public Completable removeAllNonExcluded() {
    return updatePersistence.getAll(false)
        .flatMapCompletable(updates -> removeAll(updates));
  }

  private Completable saveNewUpdates(List<App> updates) {
    return saveNonExcludedUpdates(updateMapper.mapAppUpdateList(updates, false));
/*
    return Completable.fromSingle(Observable.from(updates)
        .map(app -> mapAppUpdate(app, false))
        .toList()
        .toSingle()
        .flatMap(updateList -> {
          Logger.getInstance()
              .d(TAG, String.format("filter %d updates for non excluded and save the remainder",
                  updateList.size()));
          return saveNonExcludedUpdates(updateList);
        }));*/
  }

  private Completable saveNewUpgrades(List<App> upgrades) {
    return saveNonExcludedUpdates(updateMapper.mapAppUpdateList(upgrades, true));

  /*  return Completable.fromSingle(Observable.from(upgrades)
        .map(app -> mapAppUpdate(app, true))
        .toList()
        .toSingle()
        .flatMap(updateList -> {
          Logger.getInstance()
              .d(TAG, String.format("filter %d updates for non excluded and save the remainder",
                  updateList.size()));
          return saveNonExcludedUpdates(updateList);
        }));*/
  }

  public Completable removeAll(List<RoomUpdate> updates) {
    return updatePersistence.removeAll(updates);
  }

  @NonNull private Completable saveNonExcludedUpdates(List<RoomUpdate> updateList) {
    // remove excluded from list
    // save the remainder
    return Observable.from(updateList)
        .flatMapSingle(update -> updatePersistence.isExcluded(update.getPackageName())
            .flatMap(excluded -> {
              if (excluded) {
                return Single.just(null);
              }
              return Single.just(update);
            }))
        .toList()
        .toSingle()
        .flatMapCompletable(filteredUpdates -> {
          if (filteredUpdates != null && !filteredUpdates.isEmpty()) {
            return updatePersistence.saveAll(filteredUpdates);
          }
          return Completable.complete();
        });
  }

  public @NonNull Observable<List<RoomUpdate>> getAll(boolean isExcluded) {
    return updatePersistence.getAllSorted(isExcluded);
  }

  public Observable<RoomUpdate> get(String packageName) {
    return updatePersistence.get(packageName);
  }

  public Completable remove(List<RoomUpdate> updates) {
    return updatePersistence.removeAll(updates);
  }

  public Completable remove(RoomUpdate update) {
    return updatePersistence.remove(update.getPackageName());
  }

  public Completable remove(String packageName) {
    return updatePersistence.remove(packageName);
  }

  public Completable setExcluded(String packageName, boolean excluded) {
    return updatePersistence.get(packageName)
        .toSingle()
        .flatMapCompletable(update -> {
          update.setExcluded(excluded);
          return updatePersistence.save(update);
        });
  }

  public Single<Boolean> contains(String packageName, boolean isExcluded) {
    return updatePersistence.contains(packageName, isExcluded);
  }

  public Single<Boolean> contains(String packageName, boolean isExcluded, boolean isAppcUpgrade) {
    return updatePersistence.contains(packageName, isExcluded, isAppcUpgrade);
  }
}

package cm.aptoide.pt.updates;

import android.content.SharedPreferences;
import android.util.Pair;
import androidx.annotation.NonNull;
import cm.aptoide.pt.database.room.RoomUpdate;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.install.AptoideInstalledAppsRepository;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.store.RoomStoreRepository;
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
  private static final long SYNC_MIN_INTERVAL_MS = 23 * 60 * 60 * 1000;

  private final IdsRepository idsRepository;
  private final UpdatePersistence updatePersistence;
  private final RoomStoreRepository storeRepository;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final UpdateMapper updateMapper;
  private final AptoideInstalledAppsRepository aptoideInstalledAppsRepository;

  private long lastSyncTimestamp = 0;

  public UpdateRepository(UpdatePersistence updatePersistence, RoomStoreRepository storeRepository,
      IdsRepository idsRepository, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      UpdateMapper updateMapper,
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository) {
    this.updatePersistence = updatePersistence;
    this.storeRepository = storeRepository;
    this.idsRepository = idsRepository;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.updateMapper = updateMapper;
    this.aptoideInstalledAppsRepository = aptoideInstalledAppsRepository;
  }

  public @NonNull Completable sync(boolean bypassCache, boolean bypassServerCache,
      boolean bypassInterval) {
    long startTime = System.currentTimeMillis();
    long dif = startTime - lastSyncTimestamp;
    if (!bypassInterval && dif < SYNC_MIN_INTERVAL_MS) {
      return Completable.complete();
    }
    return storeRepository.getAll()
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
        .doOnCompleted(() -> lastSyncTimestamp = startTime);
  }

  private Observable<List<App>> getNetworkUpdates(List<Long> storeIds, boolean bypassCache,
      boolean bypassServerCache) {
    Logger.getInstance()
        .d(TAG, String.format("getNetworkUpdates() -> using %d stores", storeIds.size()));
    return Single.zip(getInstalledApks(), idsRepository.getUniqueIdentifier(), Pair::new)
        .flatMapObservable(
            pair -> ListAppsUpdatesRequest.of(pair.first, storeIds, pair.second, bodyInterceptor,
                    httpClient, converterFactory, tokenInvalidator, sharedPreferences)
                .observe(bypassCache, bypassServerCache))
        .subscribeOn(Schedulers.io())
        .map(result -> {
          if (result != null && result.isOk()) {
            return result.getList();
          }
          return Collections.emptyList();
        });
  }

  private Single<List<ListAppsUpdatesRequest.ApksData>> getInstalledApks() {
    return aptoideInstalledAppsRepository.getAllSyncedInstalled()
        .toObservable()
        .flatMapIterable(list -> list)
        .map(roomInstalled -> new ListAppsUpdatesRequest.ApksData(roomInstalled.getPackageName(),
            roomInstalled.getVersionCode(), roomInstalled.getSignature(),
            roomInstalled.isEnabled()))
        .toList()
        .toSingle();
  }

  public Completable removeAllNonExcluded() {
    return updatePersistence.getAll(false)
        .flatMapCompletable(updates -> removeAll(updates));
  }

  private Completable saveNewUpdates(List<App> updates) {
    return saveNonExcludedUpdates(updateMapper.mapAppUpdateList(updates));
  }

  public Completable removeAll(List<RoomUpdate> updates) {
    return updatePersistence.removeAll(updates);
  }

  @NonNull private Completable saveNonExcludedUpdates(List<RoomUpdate> updateList) {
    // remove excluded from list
    // save the remainder
    return Observable.from(updateList)
        .flatMap(update -> updatePersistence.isExcluded(update.getPackageName())
            .toObservable()
            .filter(isExcluded -> !isExcluded)
            .map(__ -> update))
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

  public Single<RoomUpdate> get(String packageName) {
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

  public Completable setExcluded(String packageName) {
    return updatePersistence.get(packageName)
        .flatMap(update -> {
          update.setExcluded(true);
          return Single.just(update);
        })
        .flatMapCompletable(update -> updatePersistence.save(update));
  }

  public Single<Boolean> contains(String packageName, boolean isExcluded) {
    return updatePersistence.contains(packageName, isExcluded);
  }
}

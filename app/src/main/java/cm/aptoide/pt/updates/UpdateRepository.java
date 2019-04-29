package cm.aptoide.pt.updates;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
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
  private final UpdateAccessor updateAccessor;
  private final StoreAccessor storeAccessor;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final PackageManager packageManager;

  public UpdateRepository(UpdateAccessor updateAccessor, StoreAccessor storeAccessor,
      IdsRepository idsRepository, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      PackageManager packageManager) {
    this.updateAccessor = updateAccessor;
    this.storeAccessor = storeAccessor;
    this.idsRepository = idsRepository;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.packageManager = packageManager;
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
    return ListAppcAppsUpgradesRequest.of(idsRepository.getUniqueIdentifier(), bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences, packageManager)
        .observe(bypassCache, bypassServerCache)
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
    return ListAppsUpdatesRequest.of(storeIds, idsRepository.getUniqueIdentifier(), bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences, packageManager)
        .observe(bypassCache, bypassServerCache)
        .map(result -> {
          if (result != null && result.isOk()) {
            return result.getList();
          }
          return Collections.<App>emptyList();
        });
  }

  public Completable removeAllNonExcluded() {
    return updateAccessor.getAll(false)
        .first()
        .toSingle()
        .flatMapCompletable(updates -> removeAll(updates));
  }

  private Completable saveNewUpdates(List<App> updates) {
    return Completable.fromSingle(Observable.from(updates)
        .map(app -> mapAppUpdate(app, false))
        .toList()
        .toSingle()
        .flatMap(updateList -> {
          Logger.getInstance()
              .d(TAG, String.format("filter %d updates for non excluded and save the remainder",
                  updateList.size()));
          return saveNonExcludedUpdates(updateList);
        }));
  }

  private Completable saveNewUpgrades(List<App> upgrades) {
    return Completable.fromSingle(Observable.from(upgrades)
        .map(app -> mapAppUpdate(app, true))
        .toList()
        .toSingle()
        .flatMap(updateList -> {
          Logger.getInstance()
              .d(TAG, String.format("filter %d updates for non excluded and save the remainder",
                  updateList.size()));
          return saveNonExcludedUpdates(updateList);
        }));
  }

  private Update mapAppUpdate(App app, boolean isAppcUpgrade) {

    final Obb obb = app.getObb();

    String mainObbFileName = null;
    String mainObbPath = null;
    String mainObbMd5 = null;
    String patchObbFileName = null;
    String patchObbPath = null;
    String patchObbMd5 = null;

    if (obb != null) {
      final Obb.ObbItem mainObb = obb.getMain();
      final Obb.ObbItem patchObb = obb.getPatch();
      if (mainObb != null) {
        mainObbFileName = mainObb.getFilename();
        mainObbPath = mainObb.getPath();
        mainObbMd5 = mainObb.getMd5sum();
      }

      if (patchObb != null) {
        patchObbFileName = patchObb.getFilename();
        patchObbPath = patchObb.getPath();
        patchObbMd5 = patchObb.getMd5sum();
      }
    }

    return new Update(app.getId(), app.getName(), app.getIcon(), app.getPackageName(), app.getFile()
        .getMd5sum(), app.getFile()
        .getPath(), app.getFile()
        .getFilesize(), app.getFile()
        .getVername(), app.getFile()
        .getPathAlt(), app.getFile()
        .getVercode(), app.getFile()
        .getMalware()
        .getRank()
        .name(), mainObbFileName, mainObbPath, mainObbMd5, patchObbFileName, patchObbPath,
        patchObbMd5, isAppcUpgrade);
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
        .flatMap(update -> updateAccessor.isExcluded(update.getPackageName())
            .flatMap(excluded -> {
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

  public @NonNull Observable<List<Update>> getAll(boolean isExcluded) {
    return updateAccessor.getAllSorted(isExcluded);
  }

  public Observable<Update> get(String packageName) {
    return updateAccessor.get(packageName);
  }

  public Completable remove(List<Update> updates) {
    return Observable.from(updates)
        .map(update -> update.getPackageName())
        .toList()
        .doOnNext(updatesAsPackages -> updateAccessor.removeAll(updatesAsPackages))
        .toCompletable();
  }

  public Completable remove(Update update) {
    return Completable.fromAction(() -> updateAccessor.remove(update.getPackageName()));
  }

  public void remove(String packageName) {
    updateAccessor.remove(packageName);
  }

  public Observable<List<Update>> getNonExcludedUpdates() {
    return updateAccessor.getAll()
        .flatMap(updates -> Observable.from(updates)
            .filter(update -> !update.isExcluded())
            .toList());
  }

  public Observable<Void> setExcluded(String packageName, boolean excluded) {
    return updateAccessor.get(packageName)
        .first()
        .map(update -> {
          update.setExcluded(excluded);
          updateAccessor.insert(update);
          return null;
        });
  }

  public Observable<Boolean> contains(String packageName, boolean isExcluded) {
    return updateAccessor.contains(packageName, isExcluded);
  }

  public Observable<Boolean> contains(String packageName, boolean isExcluded,
      boolean isAppcUpgrade) {
    return updateAccessor.contains(packageName, isExcluded, isAppcUpgrade);
  }
}

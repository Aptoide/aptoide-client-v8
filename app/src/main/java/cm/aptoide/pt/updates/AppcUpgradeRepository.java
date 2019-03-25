package cm.aptoide.pt.updates;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import cm.aptoide.pt.database.accessors.AppcUpgradeAccessor;
import cm.aptoide.pt.database.realm.AppcUpgrade;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppcAppsUpgradesRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networking.IdsRepository;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class AppcUpgradeRepository {

  private static final String TAG = AppcUpgradeRepository.class.getName();

  private final AppcUpgradeAccessor upgradeAccessor;
  private final IdsRepository idsRepository;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final PackageManager packageManager;
  private final BodyInterceptor<BaseBody> bodyInterceptor;

  public AppcUpgradeRepository(AppcUpgradeAccessor upgradeAccessor, IdsRepository idsRepository,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      PackageManager packageManager, BodyInterceptor<BaseBody> bodyInterceptor) {
    this.upgradeAccessor = upgradeAccessor;
    this.idsRepository = idsRepository;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.packageManager = packageManager;
    this.bodyInterceptor = bodyInterceptor;
  }

  public @NonNull Completable syncAppcUpgrades(boolean bypassCache, boolean bypassServerCache) {
    return getNetworkAppcUpgrades(bypassCache, bypassServerCache).toSingle()
        .flatMapCompletable(updates -> removeAllNonExcluded().andThen(saveNewUpdates(updates)));
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

  public @NonNull Observable<List<AppcUpgrade>> getAll(boolean isExcluded) {
    return upgradeAccessor.getAllSorted(isExcluded);
  }

  public Completable removeAllNonExcluded() {
    return upgradeAccessor.getAll(false)
        .first()
        .toSingle()
        .flatMapCompletable(upgrades -> removeAll(upgrades));
  }

  private Completable saveNewUpdates(List<App> updates) {
    return Completable.fromSingle(Observable.from(updates)
        .map(app -> mapAppUpgrade(app))
        .toList()
        .toSingle()
        .flatMap(updateList -> {
          Logger.getInstance()
              .d(TAG, String.format("filter %d updates for non excluded and save the remainder",
                  updateList.size()));
          return saveNonExcludedUpgrades(updateList);
        }));
  }

  public Completable removeAll(List<AppcUpgrade> updates) {
    return Observable.from(updates)
        .map(update -> update.getPackageName())
        .toList()
        .flatMap(updatesAsPackageNames -> {
          if (updatesAsPackageNames != null && !updatesAsPackageNames.isEmpty()) {
            upgradeAccessor.removeAll(updatesAsPackageNames);
          }
          return null;
        })
        .toCompletable();
  }

  @NonNull private Single<List<AppcUpgrade>> saveNonExcludedUpgrades(List<AppcUpgrade> updateList) {
    // remove excluded from list
    // save the remainder
    return Observable.from(updateList)
        .flatMap(update -> upgradeAccessor.isExcluded(update.getPackageName())
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
            upgradeAccessor.saveAll(updateListFiltered);
          }
        });
  }

  private AppcUpgrade mapAppUpgrade(App app) {

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

    return new AppcUpgrade(app.getId(), app.getName(), app.getIcon(), app.getPackageName(),
        app.getFile()
            .getMd5sum(), app.getFile()
        .getPath(), app.getFile()
        .getFilesize(), app.getFile()
        .getVername(), app.getFile()
        .getPathAlt(), app.getFile()
        .getVercode(), app.getFile()
        .getMalware()
        .getRank()
        .name(), mainObbFileName, mainObbPath, mainObbMd5, patchObbFileName, patchObbPath,
        patchObbMd5);
  }

  public Observable<AppcUpgrade> get(String packageName) {
    return upgradeAccessor.get(packageName);
  }
}

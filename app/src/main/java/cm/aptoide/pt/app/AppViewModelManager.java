package cm.aptoide.pt.app;

import cm.aptoide.pt.AppCoinsManager;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.app.migration.AppcMigrationManager;
import cm.aptoide.pt.app.view.AppCoinsViewModel;
import cm.aptoide.pt.bonus.BonusAppcModel;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.view.AppViewConfiguration;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.AppStats;
import cm.aptoide.pt.view.app.DetailedApp;
import cm.aptoide.pt.view.app.DetailedAppRequestResult;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import rx.Observable;
import rx.Single;

import static rx.Observable.combineLatest;

public class AppViewModelManager {
  private final AppViewConfiguration appViewConfiguration;
  private final StoreManager storeManager;
  private final String marketName;
  private final AppCenter appCenter;
  private final DownloadStateParser downloadStateParser;
  private final InstallManager installManager;
  private final AppcMigrationManager appcMigrationManager;
  private final AppCoinsAdvertisingManager appCoinsAdvertisingManager;
  private final AppCoinsManager appCoinsManager;

  private AppModel cachedApp;
  private AppCoinsViewModel cachedAppCoinsViewModel;

  public AppViewModelManager(AppViewConfiguration appViewConfiguration, StoreManager storeManager,
      String marketName, AppCenter appCenter, DownloadStateParser downloadStateParser,
      InstallManager installManager, AppcMigrationManager appcMigrationManager,
      AppCoinsAdvertisingManager appCoinsAdvertisingManager, AppCoinsManager appCoinsManager) {
    this.appViewConfiguration = appViewConfiguration;
    this.storeManager = storeManager;
    this.marketName = marketName;
    this.appCenter = appCenter;
    this.downloadStateParser = downloadStateParser;
    this.installManager = installManager;
    this.appcMigrationManager = appcMigrationManager;
    this.appCoinsAdvertisingManager = appCoinsAdvertisingManager;
    this.appCoinsManager = appCoinsManager;
  }

  public Observable<AppViewModel> observeAppViewModel() {
    return getAppModel().toObservable()
        .flatMap(appModel -> {
          Observable<DownloadModel> downloadModelObservable = getDownloadModel(appModel);
          Observable<AppCoinsViewModel> appCoinsViewModelObservable =
              getAppCoinsViewModel(appModel);
          Observable<MigrationModel> migrationModelObservable = getMigrationModel(appModel);
          return Observable.combineLatest(downloadModelObservable, appCoinsViewModelObservable,
              migrationModelObservable,
              (downloadModel, appCoinsModel, migrationModel) -> mergeToAppViewModel(appModel,
                  downloadModel, appCoinsModel, migrationModel));
        });
  }

  /**
   * Returns a snapshot of the AppViewModel. Useful for one-off operations.
   */
  public Single<AppViewModel> getAppViewModel() {
    return observeAppViewModel().first()
        .toSingle();
  }

  /**
   * This method exists as a performance optimization. Most times only AppModel is needed, and it
   * doesn't make sense to wait for DownloadModel (the heaviest model to load as it can't be
   * cached).
   *
   * @return A single of this AppViewModel's AppModel
   */
  public Single<AppModel> getAppModel() {
    if (appViewConfiguration.getAppId() >= 0) {
      return loadAppModel(appViewConfiguration.getAppId(), appViewConfiguration.getStoreName(),
          appViewConfiguration.getPackageName());
    } else if (appViewConfiguration.hasMd5()) {
      return loadAppModelFromMd5(appViewConfiguration.getMd5());
    } else if (appViewConfiguration.hasUniqueName()) {
      return loadAppViewViewModelFromUniqueName(appViewConfiguration.getUniqueName());
    } else {
      return loadAppModel(appViewConfiguration.getPackageName(),
          appViewConfiguration.getStoreName());
    }
  }

  private Observable<AppCoinsViewModel> getAppCoinsViewModel(AppModel app) {
    if (cachedAppCoinsViewModel == null) {
      Single<BonusAppcModel> bonusAppcModelSingle = Single.just(new BonusAppcModel(false, 0));
      Single<AppCoinsAdvertisingModel> appCoinsAdvertisingModelSingle =
          Single.just(new AppCoinsAdvertisingModel());
      if (app.hasBilling()) {
        bonusAppcModelSingle = RxJavaInterop.toV1Single(appCoinsManager.getBonusAppc());
      }
      if (app.hasAdvertising()) {
        appCoinsAdvertisingModelSingle =
            appCoinsAdvertisingManager.getAdvertising(app.getPackageName(), app.getVersionCode());
      }
      return Single.zip(appCoinsAdvertisingModelSingle, bonusAppcModelSingle,
              (advertisingModel, bonusAppcModel) -> {
                cachedAppCoinsViewModel =
                    new AppCoinsViewModel(false, app.hasBilling(), advertisingModel, bonusAppcModel);
                return cachedAppCoinsViewModel;
              })
          .toObservable();
    }
    return Observable.just(cachedAppCoinsViewModel);
  }

  private Observable<MigrationModel> getMigrationModel(AppModel app) {
    return appcMigrationManager.isAppMigrated(app.getPackageName())
        .map(MigrationModel::new);
  }

  private Observable<DownloadModel> getDownloadModel(AppModel app) {
    return loadDownloadModel(app.getMd5(), app.getPackageName(), app.getVersionCode(),
        app.getSignature(), app.getStore()
            .getId(), app.hasAdvertising() || app.hasBilling());
  }

  private AppViewModel mergeToAppViewModel(AppModel appModel, DownloadModel downloadModel,
      AppCoinsViewModel appCoinsModel, MigrationModel migrationModel) {
    return new AppViewModel(appModel, downloadModel, appCoinsModel, migrationModel);
  }

  private Observable<DownloadModel> loadDownloadModel(String md5, String packageName,
      int versionCode, String signature, long storeId, boolean hasAppc) {
    return combineLatest(installManager.getInstall(md5, packageName, versionCode),
        appcMigrationManager.isMigrationApp(packageName, signature, versionCode, storeId, hasAppc),
        (install, isMigration) -> new DownloadModel(
            downloadStateParser.parseDownloadType(install.getType(), isMigration),
            install.getProgress(),
            downloadStateParser.parseDownloadState(install.getState(), install.isIndeterminate()),
            install.getAppSize()));
  }

  private Single<AppModel> loadAppModel(long appId, String storeName, String packageName) {
    if (cachedApp != null) {
      return Single.just(cachedApp);
    }
    return appCenter.loadDetailedApp(appId, storeName, packageName)
        .flatMap(result -> map(result));
  }

  private Single<AppModel> loadAppModel(String packageName, String storeName) {
    if (cachedApp != null && cachedApp.getPackageName()
        .equals(packageName)) {
      return Single.just(cachedApp);
    }
    return appCenter.loadDetailedApp(packageName, storeName)
        .flatMap(result -> map(result));
  }

  private Single<AppModel> loadAppModelFromMd5(String md5) {
    if (cachedApp != null && cachedApp.getMd5()
        .equals(md5)) {
      return Single.just(cachedApp);
    }
    return appCenter.loadDetailedAppFromMd5(md5)
        .flatMap(this::map);
  }

  private Single<AppModel> loadAppViewViewModelFromUniqueName(String uniqueName) {
    if (cachedApp != null && cachedApp.getUniqueName()
        .equals(uniqueName)) {
      return Single.just(cachedApp);
    }
    return appCenter.loadDetailedAppFromUniqueName(uniqueName)
        .flatMap(this::map);
  }

  private Single<AppModel> map(DetailedAppRequestResult result) {
    if (result.getDetailedApp() != null) {
      return createAppViewViewModel(result.getDetailedApp());
    } else if (result.isLoading()) {
      return Single.just(new AppModel(result.isLoading()));
    } else if (result.hasError()) {
      return Single.just(new AppModel(result.getError()));
    } else {
      return Single.just(new AppModel(DetailedAppRequestResult.Error.GENERIC));
    }
  }

  private Single<AppModel> createAppViewViewModel(DetailedApp app) {
    AppStats stats = app.getStats();
    return isStoreFollowed(app.getStore()
        .getId()).map(isStoreFollowed -> cachedApp =
        new AppModel(app.getId(), app.getName(), app.getStore(),
            appViewConfiguration.getStoreTheme(), app.isGoodApp(), app.getMalware(),
            app.getAppFlags(), app.getTags(), app.getUsedFeatures(), app.getUsedPermissions(),
            app.getFileSize(), app.getMd5(), app.getPath(), app.getPathAlt(), app.getVersionCode(),
            app.getVersionName(), app.getPackageName(), app.getSize(), stats.getDownloads(),
            stats.getGlobalRating(), stats.getPackageDownloads(), stats.getRating(),
            app.getDeveloper(), app.getGraphic(), app.getIcon(), app.getMedia(), app.getModified(),
            app.getAdded(), app.getObb(), app.getWebUrls(), app.isLatestTrustedVersion(),
            app.getUniqueName(), appViewConfiguration.shouldInstall(),
            appViewConfiguration.getAppc(), appViewConfiguration.getMinimalAd(),
            appViewConfiguration.getEditorsChoice(), appViewConfiguration.getOriginTag(),
            isStoreFollowed, marketName, app.hasBilling(), app.hasAdvertising(), app.getBdsFlags(),
            appViewConfiguration.getCampaignUrl(), app.getSignature(), app.isMature(),
            app.getSplits(), app.getRequiredSplits(), appViewConfiguration.getOemId(), app.isBeta(),
            appViewConfiguration.isEskillsAppView(), app.getAppCategory()));
  }

  private Single<Boolean> isStoreFollowed(long storeId) {
    return storeManager.isSubscribed(storeId)
        .first()
        .toSingle();
  }
}

package cm.aptoide.pt.autoupdate;

import rx.Single;

public class AutoUpdateRepository {

  private final AptoideImgsService aptoideImgsService;
  private AutoUpdateModel autoUpdateModel;

  public AutoUpdateRepository(AptoideImgsService aptoideImgsService) {
    this.aptoideImgsService = aptoideImgsService;
  }

  public Single<AutoUpdateModel> loadFreshAutoUpdateModel() {
    return loadAndSaveAutoUpdateModel();
  }

  public Single<AutoUpdateModel> loadAutoUpdateModel() {
    if (autoUpdateModel != null) {
      return Single.just(autoUpdateModel);
    }
    return loadAndSaveAutoUpdateModel();
  }

  private Single<AutoUpdateModel> loadAndSaveAutoUpdateModel() {
    return aptoideImgsService.loadAutoUpdateModel()
        .doOnSuccess(autoUpdateModel -> this.autoUpdateModel = autoUpdateModel);
  }
}

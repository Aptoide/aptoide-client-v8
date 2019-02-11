package cm.aptoide.pt.autoupdate;

import rx.Single;

public class AutoUpdateRepository {

  private final AutoUpdateService autoUpdateService;
  private AutoUpdateModel autoUpdateModel;

  public AutoUpdateRepository(AutoUpdateService autoUpdateService) {
    this.autoUpdateService = autoUpdateService;
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
    return autoUpdateService.loadAutoUpdateModel()
        .doOnSuccess(autoUpdateModel -> this.autoUpdateModel = autoUpdateModel);
  }
}

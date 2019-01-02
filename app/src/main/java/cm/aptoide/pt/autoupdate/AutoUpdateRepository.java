package cm.aptoide.pt.autoupdate;

import rx.Single;

public class AutoUpdateRepository {

  private final AutoUpdateService autoUpdateService;
  private AutoUpdateModel autoUpdateViewModel;

  public AutoUpdateRepository(AutoUpdateService autoUpdateService) {
    this.autoUpdateService = autoUpdateService;
  }

  public Single<AutoUpdateModel> loadFreshAutoUpdateViewModel() {
    return loadAndSaveAutoUpdateViewModel();
  }

  public Single<AutoUpdateModel> loadAutoUpdateViewModel() {
    if (autoUpdateViewModel != null) {
      return Single.just(autoUpdateViewModel);
    }
    return loadAndSaveAutoUpdateViewModel();
  }

  private Single<AutoUpdateModel> loadAndSaveAutoUpdateViewModel() {
    return autoUpdateService.loadAutoUpdateViewModel()
        .doOnSuccess(autoUpdateViewModel -> this.autoUpdateViewModel = autoUpdateViewModel);
  }
}

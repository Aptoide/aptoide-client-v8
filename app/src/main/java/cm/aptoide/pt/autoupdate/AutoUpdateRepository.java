package cm.aptoide.pt.autoupdate;

import rx.Single;

public class AutoUpdateRepository {

  private final AutoUpdateService autoUpdateService;
  private AutoUpdateViewModel autoUpdateViewModel;

  public AutoUpdateRepository(AutoUpdateService autoUpdateService) {
    this.autoUpdateService = autoUpdateService;
  }

  public Single<AutoUpdateViewModel> loadFreshAutoUpdateViewModel() {
    return loadAndSaveAutoUpdateViewModel();
  }

  public Single<AutoUpdateViewModel> loadAutoUpdateViewModel() {
    if (autoUpdateViewModel != null) {
      return Single.just(autoUpdateViewModel);
    }
    return autoUpdateService.loadAutoUpdateViewModel();
  }

  private Single<AutoUpdateViewModel> loadAndSaveAutoUpdateViewModel() {
    return autoUpdateService.loadAutoUpdateViewModel()
        .doOnSuccess(autoUpdateViewModel -> this.autoUpdateViewModel = autoUpdateViewModel);
  }
}

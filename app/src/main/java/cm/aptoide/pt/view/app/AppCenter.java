package cm.aptoide.pt.view.app;

import rx.Single;

/**
 * Created by trinkes on 17/10/2017.
 */

public class AppCenter {
  private final AppCenterRepository appService;

  public AppCenter(AppCenterRepository appRepository) {

    this.appService = appRepository;
  }

  public Single<AppsList> loadNextApps(long storeId, int limit) {
    return appService.loadNextApps(storeId, limit);
  }

  public Single<AppsList> loadFreshApps(long storeId, int limit) {
    return appService.loadFreshApps(storeId, limit);
  }

  public Single<AppsList> getApps(long storeId, int limit) {
    return appService.getApplications(storeId, limit);
  }
}

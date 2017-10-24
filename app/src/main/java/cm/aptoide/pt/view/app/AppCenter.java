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

  public Single<AppsList> loadNextApps(long storeId) {
    return appService.loadNextApps(storeId);
  }

  public Single<AppsList> loadFreshApps(long storeId) {
    return appService.loadFreshApps(storeId);
  }

  public void setLimit(int limit) {
    appService.setLimit(limit);
  }

  public Single<AppsList> getApps(long storeId) {
    return appService.getApplications(storeId);
  }
}

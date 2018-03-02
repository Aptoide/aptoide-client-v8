package cm.aptoide.pt.view.app;

import rx.Single;

/**
 * Created by trinkes on 17/10/2017.
 */

public class AppCenter {
  private final AppCenterRepository appCenterRepository;

  public AppCenter(AppCenterRepository appRepository) {
    this.appCenterRepository = appRepository;
  }

  public Single<AppsList> loadNextApps(long storeId, int limit) {
    return appCenterRepository.loadNextApps(storeId, limit);
  }

  public Single<AppsList> loadFreshApps(long storeId, int limit) {
    return appCenterRepository.loadFreshApps(storeId, limit);
  }

  public Single<AppsList> getApps(long storeId, int limit) {
    return appCenterRepository.getApplications(storeId, limit);
  }
}

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

  public Single<DetailedAppRequestResult> loadDetailedApp(long appId, String storeName,
      String packageName) {
    return appCenterRepository.loadDetailedApp(appId, storeName, packageName);
  }

  public Single<DetailedAppRequestResult> loadDetailedApp(String packageName, String storeName) {
    return appCenterRepository.loadDetailedApp(packageName, storeName);
  }

  public Single<DetailedAppRequestResult> loadDetailedAppFromMd5(String md5) {
    return appCenterRepository.loadDetailedAppFromMd5(md5);
  }

  public Single<DetailedAppRequestResult> loadDetailedAppAppFromUniqueName(String uniqueName) {
    return appCenterRepository.loadDetailedAppAppFromUniqueName(uniqueName);
  }

  public Single<AppsList> loadRecommendedApps(int limit, String packageName) {
    return appCenterRepository.loadRecommendedApps(limit, packageName);
  }
}

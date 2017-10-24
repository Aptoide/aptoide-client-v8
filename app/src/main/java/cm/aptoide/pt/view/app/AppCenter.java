package cm.aptoide.pt.view.app;

import java.util.List;
import rx.Single;

/**
 * Created by trinkes on 17/10/2017.
 */

public class AppCenter {
  private final AppCenterRepository appService;

  public AppCenter(AppCenterRepository appRepository) {

    this.appService = appRepository;
  }

  public Single<List<Application>> loadNextApps(long storeId) {
    return appService.loadNextApps(storeId);
  }

  public Single<List<Application>> loadFreshApps(long storeId) {
    return appService.loadFreshApps(storeId);
  }

  public void setLimit(int limit) {
    appService.setLimit(limit);
  }

  public Single<List<Application>> getApps(long storeId) {
    return appService.getApplications(storeId);
  }
}

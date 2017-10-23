package cm.aptoide.pt.view.app;

import java.util.List;
import rx.Single;

/**
 * Created by trinkes on 17/10/2017.
 */

public class AppCenter {
  private final AppService appService;

  public AppCenter(AppService appService) {

    this.appService = appService;
  }

  public Single<List<Application>> loadNextApps(long storeId) {
    return appService.loadNextApps(storeId);
  }

  public Single<List<Application>> loadFreshApps(long storeId) {
    return appService.loadFreshApps(storeId);
  }
}

package cm.aptoide.pt.view.app;

import java.util.List;
import rx.Observable;

/**
 * Created by trinkes on 17/10/2017.
 */

public class AppCenter {
  private final AppService appService;

  public AppCenter(AppService appService) {

    this.appService = appService;
  }

  public Observable<List<Application>> getStoreApps(long storeId, boolean bypassCache) {
    return appService.getStoreApps(storeId, bypassCache);
  }
}

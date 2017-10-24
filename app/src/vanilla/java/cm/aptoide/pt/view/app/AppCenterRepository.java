package cm.aptoide.pt.view.app;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import rx.Single;

/**
 * Created by trinkes on 23/10/2017.
 */

public class AppCenterRepository {
  private final AppService appService;
  private final Map<Long, List<Application>> storeApplications;

  public AppCenterRepository(AppService appService,
      Map<Long, List<Application>> storeApplications) {
    this.appService = appService;
    this.storeApplications = storeApplications;
  }

  public Single<List<Application>> loadNextApps(long storeId) {
    return appService.loadNextApps(storeId)
        .doOnSuccess(applications -> updateCache(storeId, applications, false))
        .map(applications -> cloneList(applications));
  }

  public Single<List<Application>> loadFreshApps(long storeId) {
    return appService.loadFreshApps(storeId)
        .doOnSuccess(applications -> updateCache(storeId, applications, true))
        .map(applications -> cloneList(applications));
  }

  private void updateCache(long storeId, List<Application> applications, boolean isFresh) {
    List<Application> cache = storeApplications.get(storeId);
    if (cache == null || isFresh) {
      storeApplications.put(storeId, applications);
    } else {
      cache.addAll(applications);
    }
  }

  public void setLimit(int limit) {
    appService.setLimit(limit);
  }

  public Single<List<Application>> getApplications(long storeId) {
    List<Application> applications = storeApplications.get(storeId);
    if (applications == null) {
      return loadNextApps(storeId);
    }
    return Single.just(cloneList(applications));
  }

  @NonNull private ArrayList<Application> cloneList(List<Application> applications) {
    return new ArrayList<>(applications);
  }
}

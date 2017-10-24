package cm.aptoide.pt.view.app;

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

  public Single<AppsList> loadNextApps(long storeId) {
    return appService.loadNextApps(storeId)
        .doOnSuccess(applications -> updateCache(storeId, applications, false))
        .map(appsList -> cloneList(appsList));
  }

  public Single<AppsList> loadFreshApps(long storeId) {
    return appService.loadFreshApps(storeId)
        .doOnSuccess(applications -> updateCache(storeId, applications, true))
        .map(appsList -> cloneList(appsList));
  }

  private AppsList cloneList(AppsList appsList) {
    if (appsList.hasErrors() || appsList.isLoading()) {
      return appsList;
    }
    return new AppsList(new ArrayList<>(appsList.getList()), appsList.isLoading());
  }

  private void updateCache(long storeId, AppsList applications, boolean isFresh) {
    if (!applications.hasErrors()) {
      List<Application> cache = storeApplications.get(storeId);
      if (cache == null || isFresh) {
        storeApplications.put(storeId, applications.getList());
      } else {
        cache.addAll(applications.getList());
      }
    }
  }

  public void setLimit(int limit) {
    appService.setLimit(limit);
  }

  public Single<AppsList> getApplications(long storeId) {
    List<Application> applications = storeApplications.get(storeId);
    if (applications == null || applications.isEmpty()) {
      return loadNextApps(storeId);
    }
    return Single.just(new AppsList(new ArrayList<>(applications), false));
  }
}

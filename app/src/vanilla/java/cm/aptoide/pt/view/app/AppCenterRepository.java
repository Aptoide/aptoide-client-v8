package cm.aptoide.pt.view.app;

import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import rx.Single;

/**
 * Created by trinkes on 23/10/2017.
 */

public class AppCenterRepository {
  private final AppService appService;
  private final Map<Long, Pair<Integer, List<Application>>> storeApplications;

  public AppCenterRepository(AppService appService,
      Map<Long, Pair<Integer, List<Application>>> storeApplications) {
    this.appService = appService;
    this.storeApplications = storeApplications;
  }

  public Single<AppsList> loadNextApps(long storeId) {
    Pair<Integer, List<Application>> longListPair = storeApplications.get(storeId);
    int offset = 0;
    if (longListPair != null) {
      offset = longListPair.first;
    }
    return appService.loadApps(storeId, offset)
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
    return new AppsList(new ArrayList<>(appsList.getList()), appsList.isLoading(),
        appsList.getOffset());
  }

  private void updateCache(long storeId, AppsList applications, boolean isFresh) {
    if (!applications.hasErrors() && !applications.isLoading()) {
      Pair<Integer, List<Application>> cache = storeApplications.get(storeId);
      if (cache == null || isFresh) {
        storeApplications.put(storeId,
            new Pair<>(applications.getOffset(), applications.getList()));
      } else {
        List<Application> list = cache.second;
        list.addAll(applications.getList());
        storeApplications.put(storeId, new Pair<>(applications.getOffset(), list));
      }
    }
  }

  public void setLimit(int limit) {
    appService.setLimit(limit);
  }

  public Single<AppsList> getApplications(long storeId) {
    Pair<Integer, List<Application>> pair = storeApplications.get(storeId);
    if (pair == null || pair.second.isEmpty()) {
      return loadNextApps(storeId);
    }
    return Single.just(new AppsList(new ArrayList<>(pair.second), false, pair.first));
  }
}

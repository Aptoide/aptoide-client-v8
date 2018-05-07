package cm.aptoide.pt.view.app;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import rx.Single;

/**
 * Created by trinkes on 23/10/2017.
 */

public class AppCenterRepository {
  private final AppService appService;
  private final Map<Long, AbstractMap.SimpleEntry<Integer, List<Application>>>
      cachedStoreApplications;

  public AppCenterRepository(AppService appService,
      Map<Long, AbstractMap.SimpleEntry<Integer, List<Application>>> cachedStoreApplications) {
    this.appService = appService;
    this.cachedStoreApplications = cachedStoreApplications;
  }

  public Single<AppsList> loadNextApps(long storeId, int limit) {
    AbstractMap.SimpleEntry<Integer, List<Application>> cache =
        cachedStoreApplications.get(storeId);
    int offset = 0;
    if (cache != null) {
      offset = cache.getKey();
    }
    return appService.loadApps(storeId, offset, limit)
        .doOnSuccess(applications -> updateCache(storeId, applications, false))
        .map(appsList -> cloneList(appsList));
  }

  public Single<AppsList> loadFreshApps(long storeId, int limit) {
    return appService.loadFreshApps(storeId, limit)
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
      AbstractMap.SimpleEntry<Integer, List<Application>> cache =
          cachedStoreApplications.get(storeId);
      if (cache == null || isFresh) {
        cachedStoreApplications.put(storeId,
            new AbstractMap.SimpleEntry<>(applications.getOffset(), applications.getList()));
      } else {
        List<Application> list = cache.getValue();
        list.addAll(applications.getList());
        cachedStoreApplications.put(storeId,
            new AbstractMap.SimpleEntry<>(applications.getOffset(), list));
      }
    }
  }

  /**
   * @param limit parameter represents the number of apps returned;
   * if there cached apps, a multiple of limit apps will be returned
   * else limit apps will be requested to server
   * <p>example:&#09;limit=2</p>
   * <p>No cached apps:</p>
   * <p>&#09;return list's size = 2</p>
   * <p>3 Cached apps</p>
   * <p>&#09;return list's size = 4</p>
   */
  public Single<AppsList> getApplications(long storeId, int limit) {
    AbstractMap.SimpleEntry<Integer, List<Application>> cache =
        cachedStoreApplications.get(storeId);
    if (cache == null || cache.getValue()
        .isEmpty()) {
      return loadNextApps(storeId, limit);
    }
    int appsLeft = limit
        - cache.getValue()
        .size() % limit;
    if (appsLeft == 0) {
      return Single.just(new AppsList(new ArrayList<>(cache.getValue()), false, cache.getKey()));
    } else {
      return loadNextApps(storeId, appsLeft).map(
          appsList -> new AppsList(new ArrayList<>(cache.getValue()), false, cache.getKey()));
    }
  }

  public Single<DetailedApp> getDetailedApp(long appId, String packageName) {
    return appService.loadDetailedApp(appId, packageName, true, false);
  }
}

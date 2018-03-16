package cm.aptoide.pt.home;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class BundlesRepository {
  private final BundleDataSource remoteBundleDataSource;
  private AbstractMap.SimpleEntry<Integer, List<HomeBundle>> cachedBundles;

  public BundlesRepository(BundleDataSource remoteBundleDataSource,
      AbstractMap.SimpleEntry<Integer, List<HomeBundle>> cachedBundles) {
    this.remoteBundleDataSource = remoteBundleDataSource;
    this.cachedBundles = cachedBundles;
  }

  public Single<HomeBundlesModel> loadHomeBundles() {
    int limit = 5;
    if (cachedBundles.getKey() == null || cachedBundles.getValue()
        .isEmpty()) {
      return loadNextHomeBundles(limit);
    }
    int bundlesLeft = limit
        - cachedBundles.getValue()
        .size() % limit;
    if (bundlesLeft == 0) {
      return Single.just(new HomeBundlesModel(new ArrayList<>(cachedBundles.getValue()), false,
          cachedBundles.getKey()));
    } else {
      return loadNextHomeBundles(bundlesLeft).map(
          appsList -> new HomeBundlesModel(new ArrayList<>(cachedBundles.getValue()), false,
              cachedBundles.getKey()));
    }
  }

  public Single<HomeBundlesModel> loadFreshHomeBundles() {
    return remoteBundleDataSource.loadFreshHomeBundles()
        .doOnSuccess(applications -> updateCache(applications, true))
        .map(appsList -> cloneList(appsList));
  }

  private HomeBundlesModel cloneList(HomeBundlesModel appsList) {
    if (appsList.hasErrors() || appsList.isLoading()) {
      return appsList;
    }
    return new HomeBundlesModel(new ArrayList<>(appsList.getList()), appsList.isLoading(),
        appsList.getOffset());
  }

  public Single<HomeBundlesModel> loadNextHomeBundles(int limit) {
    int offset = 0;
    if (cachedBundles != null) {
      offset = cachedBundles.getKey();
    }
    return remoteBundleDataSource.loadNextHomeBundles(offset, limit)
        .doOnSuccess(homeBundlesModel -> updateCache(homeBundlesModel, false));
  }

  private void updateCache(HomeBundlesModel homeBundles, boolean cacheIsDirty) {
    if (!homeBundles.hasErrors() && !homeBundles.isLoading()) {
      if (cachedBundles.getKey() == null || cacheIsDirty) {
        cachedBundles =
            new AbstractMap.SimpleEntry<>(homeBundles.getOffset(), homeBundles.getList());
      } else {
        List<HomeBundle> list = cachedBundles.getValue();
        list.addAll(homeBundles.getList());
        cachedBundles = new AbstractMap.SimpleEntry<>(homeBundles.getOffset(), list);
      }
    }
  }

  public boolean hasMore() {
    return remoteBundleDataSource.hasMore(cachedBundles.getKey());
  }
}

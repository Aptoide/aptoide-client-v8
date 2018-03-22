package cm.aptoide.pt.home;

import java.util.ArrayList;
import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class BundlesRepository {
  private final BundleDataSource remoteBundleDataSource;
  private List<HomeBundle> cachedBundles;
  private int offset;
  private int limit;

  public BundlesRepository(BundleDataSource remoteBundleDataSource, List<HomeBundle> cachedBundles,
      int offset, int limit) {
    this.remoteBundleDataSource = remoteBundleDataSource;
    this.cachedBundles = cachedBundles;
    this.offset = offset;
    this.limit = limit;
  }

  public Single<HomeBundlesModel> loadHomeBundles() {
    if (cachedBundles.isEmpty()) {
      return loadNextHomeBundles();
    } else {
      return Single.just(new HomeBundlesModel(new ArrayList<>(cachedBundles), false, offset));
    }
  }

  public Single<HomeBundlesModel> loadFreshHomeBundles() {
    return remoteBundleDataSource.loadFreshHomeBundles()
        .doOnSuccess(homeBundlesModel -> updateCache(homeBundlesModel, true))
        .map(homeBundlesModel -> cloneList(homeBundlesModel));
  }

  private HomeBundlesModel cloneList(HomeBundlesModel homeBundlesModel) {
    if (homeBundlesModel.hasErrors() || homeBundlesModel.isLoading()) {
      return homeBundlesModel;
    }
    return new HomeBundlesModel(new ArrayList<>(homeBundlesModel.getList()),
        homeBundlesModel.isLoading(), homeBundlesModel.getOffset());
  }

  public Single<HomeBundlesModel> loadNextHomeBundles() {
    return remoteBundleDataSource.loadNextHomeBundles(offset, limit)
        .doOnSuccess(homeBundlesModel -> updateCache(homeBundlesModel, false))
        .map(homeBundlesModel -> cloneList(homeBundlesModel));
  }

  private void updateCache(HomeBundlesModel homeBundles, boolean cacheIsDirty) {
    if (!homeBundles.hasErrors() && !homeBundles.isLoading()) {
      offset = homeBundles.getOffset();
      if (cacheIsDirty) {
        cachedBundles = new ArrayList<>(homeBundles.getList());
      } else {
        cachedBundles.addAll(homeBundles.getList());
      }
    }
  }

  public boolean hasMore() {
    return remoteBundleDataSource.hasMore(offset);
  }
}

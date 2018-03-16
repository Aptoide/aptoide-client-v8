package cm.aptoide.pt.home;

import rx.Single;

/**
 * Created by jdandrade on 08/03/2018.
 */

public interface BundleDataSource {

  Single<HomeBundlesModel> loadFreshHomeBundles();

  Single<HomeBundlesModel> loadNextHomeBundles(int offset, int limit);

  boolean hasMore(Integer offset);
}

package cm.aptoide.pt.home.bundles;

import rx.Observable;
import rx.Single;

/**
 * Created by jdandrade on 08/03/2018.
 */

public interface BundleDataSource {

  Observable<HomeBundlesModel> loadFreshHomeBundles(String key);

  Observable<HomeBundlesModel> loadNextHomeBundles(int offset, int limit, String key,
      boolean skeletonLoad);

  boolean hasMore(Integer offset, String title);

  Single<HomeBundlesModel> loadFreshBundleForEvent(String url, String key);

  Single<HomeBundlesModel> loadNextBundleForEvent(String url, int offset, String key, int limit);
}

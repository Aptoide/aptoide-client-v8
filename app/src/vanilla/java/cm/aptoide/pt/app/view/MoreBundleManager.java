package cm.aptoide.pt.app.view;

import cm.aptoide.pt.home.BundlesRepository;
import cm.aptoide.pt.home.HomeBundlesModel;
import rx.Single;

/**
 * Created by D01 on 05/06/2018.
 */

public class MoreBundleManager {

  private final BundlesRepository bundlesRepository;

  public MoreBundleManager(BundlesRepository bundlesRepository) {
    this.bundlesRepository = bundlesRepository;
  }

  public Single<HomeBundlesModel> loadBundle(String title, String url) {
    return bundlesRepository.loadBundles(title, url);
  }

  public Single<HomeBundlesModel> loadFreshBundles(String title, String url) {
    return bundlesRepository.loadFreshBundles(title, url);
  }

  public Single<HomeBundlesModel> loadNextBundles(String title, String url) {
    return bundlesRepository.loadNextBundles(title, url);
  }

  public boolean hasMore(String title) {
    return bundlesRepository.hasMore(title);
  }
}

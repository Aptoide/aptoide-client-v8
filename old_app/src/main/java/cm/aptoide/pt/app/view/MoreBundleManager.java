package cm.aptoide.pt.app.view;

import cm.aptoide.pt.home.bundles.BundlesRepository;
import cm.aptoide.pt.home.bundles.HomeBundlesModel;
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
    return bundlesRepository.loadBundles(title, url)
        .flatMap(homeBundlesModel -> handleEmptyBundles(title, url, homeBundlesModel));
  }

  private Single<HomeBundlesModel> handleEmptyBundles(String title, String url,
      HomeBundlesModel homeBundlesModel) {
    if (isOnlyEmptyBundles(homeBundlesModel)) {
      return loadNextBundles(title, url);
    }
    return Single.just(homeBundlesModel);
  }

  public Single<HomeBundlesModel> loadFreshBundles(String title, String url) {
    return bundlesRepository.loadFreshBundles(title, url);
  }

  public Single<HomeBundlesModel> loadNextBundles(String title, String url) {
    return bundlesRepository.loadNextBundles(title, url)
        .flatMap(homeBundlesModel -> handleEmptyBundles(title, url, homeBundlesModel));
  }

  public boolean hasMore(String title) {
    return bundlesRepository.hasMore(title);
  }

  private boolean isOnlyEmptyBundles(HomeBundlesModel homeBundlesModel) {
    return !homeBundlesModel.hasErrors()
        && !homeBundlesModel.isLoading()
        && homeBundlesModel.getList()
        .isEmpty();
  }
}

package cm.aptoide.pt.home;

import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class Home {

  private final BundlesRepository bundlesRepository;

  public Home(BundlesRepository bundlesRepository) {
    this.bundlesRepository = bundlesRepository;
  }

  public Single<List<HomeBundle>> getHomeBundles() {
    return bundlesRepository.getHomeBundles();
  }

  public Single<List<HomeBundle>> getFreshHomeBundles() {
    return bundlesRepository.getFreshHomeBundles();
  }

  public Single<List<HomeBundle>> getNextHomeBundles() {
    return bundlesRepository.getNextHomeBundles();
  }

  public boolean hasMore() {
    return bundlesRepository.hasMore();
  }
}

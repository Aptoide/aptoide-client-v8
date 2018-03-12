package cm.aptoide.pt.home;

import cm.aptoide.accountmanager.AptoideAccountManager;
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

  public Single<List<AppBundle>> getHomeBundles() {
    return bundlesRepository.getHomeBundles();
  }
}

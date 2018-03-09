package cm.aptoide.pt.home;

import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class BundlesRepository {
  private final BundleDataSource remoteBundleDataSource;
  private final BundleDataSource localBundleDataSource;

  public BundlesRepository(BundleDataSource remoteBundleDataSource,
      BundleDataSource localBundleDataSource) {
    this.remoteBundleDataSource = remoteBundleDataSource;
    this.localBundleDataSource = localBundleDataSource;
  }

  public Single<List<AppBundle>> getHomeBundles() {
    return remoteBundleDataSource.getBundles();
  }
}

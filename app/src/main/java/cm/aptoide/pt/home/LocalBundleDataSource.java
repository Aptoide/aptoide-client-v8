package cm.aptoide.pt.home;

import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class LocalBundleDataSource implements BundleDataSource {
  private List<HomeBundle> appBundles;

  @Override public Single<List<HomeBundle>> getBundles() {
    return Single.just(appBundles);
  }
}

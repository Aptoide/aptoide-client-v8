package cm.aptoide.pt.home.bundles.misc;

import cm.aptoide.pt.home.bundles.base.DummyBundle;

/**
 * Created by jdandrade on 14/03/2018.
 */

public class ProgressBundle extends DummyBundle {
  @Override public BundleType getType() {
    return BundleType.LOADING;
  }
}
